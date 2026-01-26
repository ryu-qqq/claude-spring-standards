"""
Validation Engine
AESA-126~128: 검증 엔진 및 자동 수정 시스템

코드 검증, 위반 탐지, 자동 수정 제안, 재생성 파이프라인
"""

import re
from typing import Optional
from .models import (
    ValidationResult,
    ValidationContext,
    Violation,
    ViolationSeverity,
    FixSuggestion,
)
from .parser import JavaParser, get_java_parser
from .rules import ZeroToleranceRules, get_zero_tolerance_rules


# Singleton instance
_engine: Optional["ValidationEngine"] = None


class ValidationEngine:
    """통합 검증 엔진"""

    # code 파라미터가 필요한 규칙 코드
    _RULES_REQUIRING_CODE: frozenset[str] = frozenset({"AGG-001", "ENT-001", "ENT-002"})

    def __init__(
        self,
        parser: Optional[JavaParser] = None,
        rules: Optional[ZeroToleranceRules] = None,
    ):
        """검증 엔진 초기화"""
        self._parser = parser or get_java_parser()
        self._rules = rules or get_zero_tolerance_rules()

        # 규칙 코드 → 수정 제안 핸들러 매핑
        self._fix_suggestion_handlers: dict[str, callable] = {
            "AGG-001": self._suggest_lombok_fix,
            "ENT-001": self._suggest_lombok_fix,
            "AGG-014": self._suggest_law_of_demeter_fix,
            "AGG-003": self._suggest_setter_fix,
            "APP-002": self._suggest_single_responsibility_fix,
            "APP-004": self._suggest_transactional_external_fix,
            "ENT-002": self._suggest_jpa_relationship_fix,
            "CTR-005": self._suggest_controller_transactional_fix,
            "CTR-001": self._suggest_mock_mvc_fix,
        }

    @property
    def parser(self) -> JavaParser:
        """Java 파서 반환"""
        return self._parser

    @property
    def rules(self) -> ZeroToleranceRules:
        """Zero-Tolerance 규칙 반환"""
        return self._rules

    def validate(self, context: ValidationContext) -> ValidationResult:
        """코드 검증 수행

        Args:
            context: 검증 컨텍스트 (코드, 레이어, 클래스 타입 등)

        Returns:
            ValidationResult: 검증 결과
        """
        result = ValidationResult(
            is_valid=True,
            layer=context.layer,
            class_type=context.class_type,
            validated_code=context.code,
        )

        # 레이어가 지정된 경우 해당 레이어 규칙 검사
        if context.layer:
            violations = self._rules.validate(context.code, context.layer)
            for v in violations:
                result.add_violation(v)

        # 수정 제안 생성
        if context.include_suggestions and result.violations:
            suggestions = self.suggest_fixes(result.violations, context.code)
            for s in suggestions[: context.max_suggestions]:
                result.add_suggestion(s)

        return result

    def validate_zero_tolerance(self, code: str, layer: str) -> ValidationResult:
        """Zero-Tolerance 규칙만 검증

        Args:
            code: 검증할 Java 코드
            layer: 대상 레이어 (DOMAIN, APPLICATION, PERSISTENCE, REST_API)

        Returns:
            ValidationResult: 검증 결과 (Zero-Tolerance 위반만 포함)
        """
        context = ValidationContext(
            code=code,
            layer=layer,
            strict_mode=True,
            include_suggestions=True,
        )

        result = self.validate(context)

        # Critical 위반만 필터링
        critical_violations = [
            v for v in result.violations if v.severity == ViolationSeverity.CRITICAL
        ]

        filtered_result = ValidationResult(
            is_valid=len(critical_violations) == 0,
            violations=critical_violations,
            critical_count=len(critical_violations),
            layer=layer,
            validated_code=code,
        )

        # 수정 제안 필터링
        for s in result.suggestions:
            if s.violation.severity == ViolationSeverity.CRITICAL:
                filtered_result.add_suggestion(s)

        return filtered_result

    def suggest_fixes(
        self, violations: list[Violation], code: str
    ) -> list[FixSuggestion]:
        """위반 사항에 대한 수정 제안 생성

        Args:
            violations: 위반 사항 목록
            code: 원본 코드

        Returns:
            list[FixSuggestion]: 수정 제안 목록
        """
        suggestions = []

        for violation in violations:
            suggestion = self._generate_fix_suggestion(violation, code)
            if suggestion:
                suggestions.append(suggestion)

        return suggestions

    def suggest_fix(self, code: str, layer: str) -> list[FixSuggestion]:
        """코드 검증 후 수정 제안 생성 (단축 메서드)

        Args:
            code: 검증할 Java 코드
            layer: 대상 레이어

        Returns:
            list[FixSuggestion]: 수정 제안 목록
        """
        result = self.validate_zero_tolerance(code, layer)
        return result.suggestions

    def validate_and_regenerate(
        self,
        code: str,
        layer: str,
        regenerate_callback: Optional[callable] = None,
        max_attempts: int = 3,
    ) -> tuple[ValidationResult, str, int]:
        """검증 후 통과할 때까지 재생성

        Args:
            code: 초기 코드
            layer: 대상 레이어
            regenerate_callback: 재생성 콜백 함수 (suggestions -> new_code)
            max_attempts: 최대 재시도 횟수

        Returns:
            tuple[ValidationResult, str, int]:
                - 최종 검증 결과
                - 최종 코드
                - 시도 횟수
        """
        current_code = code
        attempt = 0

        while attempt < max_attempts:
            attempt += 1

            result = self.validate_zero_tolerance(current_code, layer)

            # 검증 통과
            if result.is_valid:
                return result, current_code, attempt

            # 재생성 콜백이 없으면 자동 수정 시도
            if regenerate_callback is None:
                fixed_code = self._auto_fix(current_code, result.suggestions)
                if fixed_code == current_code:
                    # 더 이상 자동 수정 불가
                    break
                current_code = fixed_code
            else:
                # 콜백을 통한 재생성
                new_code = regenerate_callback(result.suggestions)
                if new_code is None or new_code == current_code:
                    break
                current_code = new_code

        # 최종 검증
        final_result = self.validate_zero_tolerance(current_code, layer)
        return final_result, current_code, attempt

    def _generate_fix_suggestion(
        self, violation: Violation, code: str
    ) -> Optional[FixSuggestion]:
        """개별 위반에 대한 수정 제안 생성

        규칙 코드에 매핑된 핸들러를 사용하여 수정 제안을 생성합니다.
        핸들러 매핑은 __init__에서 초기화됩니다.
        """
        handler = self._fix_suggestion_handlers.get(violation.rule_code)
        if handler is None:
            return None

        # 일부 핸들러는 code 파라미터 필요
        if violation.rule_code in self._RULES_REQUIRING_CODE:
            return handler(violation, code)
        return handler(violation)

    def _suggest_lombok_fix(self, violation: Violation, code: str) -> FixSuggestion:
        """Lombok 위반 수정 제안"""
        annotation = violation.code_snippet or "@Data"

        suggestion_text = f"Lombok {annotation} 제거 후 명시적으로 구현하세요."

        if "@Data" in annotation:
            suggestion_text = """@Data 제거 후 다음을 직접 구현:
1. private final 필드 선언
2. 생성자 (또는 정적 팩토리 메서드)
3. getter 메서드 (setter 없음)
4. equals(), hashCode(), toString()"""
        elif "@Getter" in annotation:
            suggestion_text = "getter 메서드를 직접 작성하세요. (예: public String getName() { return this.name; })"
        elif "@Builder" in annotation:
            suggestion_text = "정적 팩토리 메서드 또는 빌더 클래스를 직접 구현하세요."

        return FixSuggestion(
            violation=violation,
            suggestion=suggestion_text,
            before_code=annotation,
            after_code="// Lombok 제거 - 명시적 구현 필요",
            auto_fixable=False,  # Lombok 제거는 수동 작업 필요
            confidence=0.95,
        )

    def _suggest_law_of_demeter_fix(self, violation: Violation) -> FixSuggestion:
        """Law of Demeter 위반 수정 제안"""
        return FixSuggestion(
            violation=violation,
            suggestion="""getter 체이닝 대신 Tell, Don't Ask 원칙 적용:
1. 객체에게 행동을 위임하세요.
2. order.getCustomer().getAddress() → order.getDeliveryAddress()
3. 중간 객체를 노출하지 말고, 필요한 행동을 캡슐화하세요.""",
            before_code=violation.code_snippet,
            after_code="// 행동 위임 메서드로 리팩토링 필요",
            auto_fixable=False,
            confidence=0.85,
        )

    def _suggest_setter_fix(self, violation: Violation) -> FixSuggestion:
        """Setter 메서드 수정 제안"""
        method_name = violation.code_snippet or "setXxx"

        return FixSuggestion(
            violation=violation,
            suggestion=f"""Setter 제거 후 불변 패턴 적용:
1. 필드를 private final로 변경
2. 생성자에서 초기화
3. 상태 변경이 필요하면 새 객체 반환: {method_name.replace("set", "with")}() 메서드""",
            before_code=f"public void {method_name}(...) {{ ... }}",
            after_code="// Setter 제거, 불변 객체로 변경",
            auto_fixable=False,
            confidence=0.9,
        )

    def _suggest_single_responsibility_fix(self, violation: Violation) -> FixSuggestion:
        """UseCase 단일 책임 수정 제안"""
        return FixSuggestion(
            violation=violation,
            suggestion="""UseCase를 분리하세요:
1. 각 public 메서드를 별도의 UseCase 클래스로 분리
2. CreateOrderUseCase, UpdateOrderUseCase, DeleteOrderUseCase 등
3. 하나의 UseCase = 하나의 비즈니스 행동""",
            before_code=violation.code_snippet,
            after_code="// UseCase 분리 필요",
            auto_fixable=False,
            confidence=0.85,
        )

    def _suggest_transactional_external_fix(
        self, violation: Violation
    ) -> FixSuggestion:
        """@Transactional 내 외부 API 수정 제안"""
        return FixSuggestion(
            violation=violation,
            suggestion="""외부 API 호출을 트랜잭션 외부로 분리:
1. Saga 패턴 적용
2. 이벤트 기반 처리 (도메인 이벤트 발행)
3. 또는 트랜잭션 완료 후 @TransactionalEventListener 사용""",
            before_code="@Transactional + 외부 API 호출",
            after_code="// Saga 패턴 또는 이벤트 기반으로 분리",
            auto_fixable=False,
            confidence=0.8,
        )

    def _suggest_jpa_relationship_fix(
        self, violation: Violation, code: str
    ) -> FixSuggestion:
        """JPA 관계 어노테이션 수정 제안"""
        annotation = violation.code_snippet or "@OneToMany"

        return FixSuggestion(
            violation=violation,
            suggestion=f"""{annotation} 제거 후 Long FK로 변경:
1. 관계 어노테이션 대신 Long 타입 FK 필드 사용
2. @Column(name = "parent_id") private Long parentId;
3. 연관 엔티티는 Repository를 통해 조회""",
            before_code=annotation,
            after_code='@Column(name = "xxx_id")\nprivate Long xxxId;',
            auto_fixable=True,
            confidence=0.9,
        )

    def _suggest_controller_transactional_fix(
        self, violation: Violation
    ) -> FixSuggestion:
        """Controller @Transactional 수정 제안"""
        return FixSuggestion(
            violation=violation,
            suggestion="""Controller에서 @Transactional 제거:
1. @Transactional은 UseCase 클래스에서 관리
2. Controller는 요청/응답 변환만 담당
3. 비즈니스 로직과 트랜잭션은 Application Layer에서 처리""",
            before_code="@Transactional",
            after_code="// @Transactional 제거 - UseCase에서 처리",
            auto_fixable=True,
            confidence=0.95,
        )

    def _suggest_mock_mvc_fix(self, violation: Violation) -> FixSuggestion:
        """MockMvc 수정 제안"""
        return FixSuggestion(
            violation=violation,
            suggestion="""MockMvc 대신 TestRestTemplate 사용:
1. @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
2. @Autowired TestRestTemplate restTemplate;
3. restTemplate.getForEntity("/api/...", ResponseType.class)""",
            before_code="MockMvc mockMvc",
            after_code="TestRestTemplate restTemplate",
            auto_fixable=True,
            confidence=0.95,
        )

    def _auto_fix(self, code: str, suggestions: list[FixSuggestion]) -> str:
        """자동 수정 가능한 항목 적용"""
        fixed_code = code

        for suggestion in suggestions:
            if not suggestion.auto_fixable:
                continue

            # 간단한 패턴 기반 자동 수정
            if suggestion.violation.rule_code == "CTR-005":
                # Controller @Transactional 제거
                fixed_code = self._remove_annotation(fixed_code, "@Transactional")
            elif suggestion.violation.rule_code == "ENT-002":
                # JPA 관계 어노테이션 제거 (복잡하므로 생략)
                pass
            elif suggestion.violation.rule_code == "CTR-001":
                # MockMvc → TestRestTemplate (복잡하므로 생략)
                pass

        return fixed_code

    def _remove_annotation(self, code: str, annotation: str) -> str:
        """어노테이션 제거

        정확한 어노테이션 매칭을 사용하여 부분 매칭 문제 방지
        예: @Transactional 제거 시 @TransactionalEventListener는 유지

        Args:
            code: 소스 코드
            annotation: 제거할 어노테이션 (예: "@Transactional")

        Returns:
            어노테이션이 제거된 코드
        """
        lines = code.split("\n")
        new_lines = []

        # 정규식 특수문자 escape 및 word boundary 패턴 생성
        # @Transactional, @Transactional(readOnly = true) 등 매칭
        # @TransactionalEventListener는 매칭 안 됨
        escaped_annotation = re.escape(annotation)
        pattern = re.compile(rf"^\s*{escaped_annotation}(?:\s*\(.*\))?\s*$")

        for line in lines:
            if pattern.match(line):
                # 정확히 매칭되는 어노테이션 라인만 스킵
                continue
            new_lines.append(line)

        return "\n".join(new_lines)


def get_validation_engine() -> ValidationEngine:
    """싱글톤 ValidationEngine 인스턴스 반환"""
    global _engine
    if _engine is None:
        _engine = ValidationEngine()
    return _engine
