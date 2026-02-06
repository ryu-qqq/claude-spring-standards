"""
Zero-Tolerance Rules
AESA-126: Zero-Tolerance 검증 규칙 정의

헥사고날 아키텍처의 레이어별 Zero-Tolerance 규칙 정의 및 검사 로직
"""

from dataclasses import dataclass
from typing import Callable, Optional
from .models import Violation, ViolationSeverity
from .parser import JavaParser, get_java_parser


@dataclass
class ZeroToleranceRule:
    """Zero-Tolerance 규칙 정의"""

    code: str  # 규칙 코드 (예: AGG-001)
    name: str  # 규칙 명칭
    description: str  # 규칙 설명
    layers: list[str]  # 적용 레이어
    check: Callable[[str, JavaParser], list[Violation]]  # 검사 함수


# Singleton instance
_rules: Optional["ZeroToleranceRules"] = None


class ZeroToleranceRules:
    """Zero-Tolerance 규칙 집합"""

    # Lombok 금지 어노테이션
    LOMBOK_ANNOTATIONS = [
        "Data",
        "Getter",
        "Setter",
        "Builder",
        "NoArgsConstructor",
        "AllArgsConstructor",
        "RequiredArgsConstructor",
        "Value",
        "ToString",
        "EqualsAndHashCode",
        "Slf4j",
        "Log",
        "Log4j",
        "Log4j2",
    ]

    # JPA 관계 어노테이션
    JPA_RELATIONSHIP_ANNOTATIONS = [
        "OneToMany",
        "ManyToOne",
        "OneToOne",
        "ManyToMany",
    ]

    def __init__(self):
        """규칙 초기화"""
        self._rules: list[ZeroToleranceRule] = []
        self._register_domain_rules()
        self._register_application_rules()
        self._register_persistence_rules()
        self._register_rest_api_rules()

    def _register_domain_rules(self):
        """Domain Layer 규칙 등록"""

        # AGG-001: Lombok 금지
        self._rules.append(
            ZeroToleranceRule(
                code="AGG-001",
                name="Lombok 금지",
                description="Domain 레이어에서 Lombok 어노테이션 사용 금지. 도메인 모델의 명시적 구현 필요.",
                layers=["DOMAIN"],
                check=self._make_lombok_checker("AGG-001", "Lombok 금지"),
            )
        )

        # AGG-014: Law of Demeter (getter 체이닝 금지)
        self._rules.append(
            ZeroToleranceRule(
                code="AGG-014",
                name="Law of Demeter 위반",
                description="getter 체이닝 금지. Tell, Don't Ask 원칙 준수.",
                layers=["DOMAIN"],
                check=self._check_law_of_demeter,
            )
        )

        # AGG-003: 불변 필드 강제
        self._rules.append(
            ZeroToleranceRule(
                code="AGG-003",
                name="Setter 메서드 금지",
                description="Domain 객체에서 Setter 메서드 사용 금지. 불변성 유지.",
                layers=["DOMAIN"],
                check=self._check_setter_methods,
            )
        )

    def _register_application_rules(self):
        """Application Layer 규칙 등록"""

        # APP-002: UseCase 단일 책임
        self._rules.append(
            ZeroToleranceRule(
                code="APP-002",
                name="UseCase 단일 책임",
                description="UseCase는 하나의 public 메서드만 가져야 함.",
                layers=["APPLICATION"],
                check=self._check_use_case_single_responsibility,
            )
        )

        # APP-004: @Transactional 내 외부 API 금지
        self._rules.append(
            ZeroToleranceRule(
                code="APP-004",
                name="@Transactional 내 외부 API 금지",
                description="@Transactional 메서드 내에서 외부 API 호출 금지.",
                layers=["APPLICATION"],
                check=self._check_transactional_external_call,
            )
        )

    def _register_persistence_rules(self):
        """Persistence Layer 규칙 등록"""

        # ENT-002: Long FK 전략
        self._rules.append(
            ZeroToleranceRule(
                code="ENT-002",
                name="JPA 관계 어노테이션 금지",
                description="JPA Entity에서 @OneToMany, @ManyToOne 등 관계 어노테이션 금지. Long FK 전략 사용.",
                layers=["ADAPTER_OUT"],
                check=self._check_jpa_relationship,
            )
        )

        # ENT-001: Entity Lombok 금지
        self._rules.append(
            ZeroToleranceRule(
                code="ENT-001",
                name="Entity Lombok 금지",
                description="JPA Entity에서 Lombok 어노테이션 사용 금지.",
                layers=["ADAPTER_OUT"],
                check=self._make_lombok_checker("ENT-001", "Entity Lombok 금지"),
            )
        )

    def _register_rest_api_rules(self):
        """REST API Layer 규칙 등록"""

        # CTR-005: Controller @Transactional 금지
        self._rules.append(
            ZeroToleranceRule(
                code="CTR-005",
                name="Controller @Transactional 금지",
                description="Controller에서 @Transactional 사용 금지. UseCase에서 처리.",
                layers=["ADAPTER_IN"],
                check=self._check_controller_transactional,
            )
        )

        # CTR-001: MockMvc 금지
        self._rules.append(
            ZeroToleranceRule(
                code="CTR-001",
                name="MockMvc 테스트 금지",
                description="Controller 테스트에서 MockMvc 사용 금지. TestRestTemplate 사용.",
                layers=["ADAPTER_IN"],
                check=self._check_mock_mvc_usage,
            )
        )

    def get_rules_for_layer(self, layer: str) -> list[ZeroToleranceRule]:
        """특정 레이어의 규칙 반환"""
        layer_upper = layer.upper()
        return [r for r in self._rules if layer_upper in r.layers]

    def get_all_rules(self) -> list[ZeroToleranceRule]:
        """모든 규칙 반환"""
        return self._rules.copy()

    def validate(self, code: str, layer: str) -> list[Violation]:
        """코드 검증"""
        parser = get_java_parser()
        violations = []

        rules = self.get_rules_for_layer(layer)
        for rule in rules:
            rule_violations = rule.check(code, parser)
            for v in rule_violations:
                v.layer = layer
            violations.extend(rule_violations)

        return violations

    # ============================================
    # 검사 함수 구현
    # ============================================

    def _make_lombok_checker(
        self, rule_code: str, rule_name: str
    ) -> Callable[[str, JavaParser], list[Violation]]:
        """Lombok 어노테이션 검사 함수 생성 팩토리"""

        def check_lombok(code: str, parser: JavaParser) -> list[Violation]:
            violations = []

            for annotation in self.LOMBOK_ANNOTATIONS:
                if parser.has_annotation(code, annotation):
                    annotations = parser.find_annotations(code)
                    for ann in annotations:
                        if ann.name.lstrip("@") == annotation:
                            violations.append(
                                Violation(
                                    rule_code=rule_code,
                                    rule_name=rule_name,
                                    severity=ViolationSeverity.CRITICAL,
                                    message=f"Lombok 어노테이션 @{annotation} 사용 금지. 명시적으로 구현하세요.",
                                    line_number=ann.line_number,
                                    node_type="annotation",
                                    code_snippet=f"@{annotation}",
                                )
                            )
                            break  # 같은 어노테이션은 한 번만 보고

            return violations

        return check_lombok

    def _check_law_of_demeter(self, code: str, parser: JavaParser) -> list[Violation]:
        """Law of Demeter 위반 (getter 체이닝) 검사"""
        violations = []

        chain_violations = parser.check_method_chaining(code)
        for line_number, snippet in chain_violations:
            # getter 패턴인 경우만 위반으로 간주
            if ".get" in snippet.lower() and snippet.count(".") >= 2:
                violations.append(
                    Violation(
                        rule_code="AGG-014",
                        rule_name="Law of Demeter 위반",
                        severity=ViolationSeverity.CRITICAL,
                        message="getter 체이닝 금지. Tell, Don't Ask 원칙을 따르세요.",
                        line_number=line_number,
                        node_type="method_invocation",
                        code_snippet=snippet[:100],  # 너무 긴 경우 잘라냄
                    )
                )

        return violations

    def _check_setter_methods(self, code: str, parser: JavaParser) -> list[Violation]:
        """Setter 메서드 검사"""
        violations = []

        methods = parser.find_methods(code)
        for method in methods:
            # setXxx 패턴이고 void 반환이면 setter로 간주
            if (
                method.name.startswith("set")
                and len(method.name) > 3
                and method.name[3].isupper()
                and method.return_type == "void"
                and len(method.parameters) == 1
            ):
                violations.append(
                    Violation(
                        rule_code="AGG-003",
                        rule_name="Setter 메서드 금지",
                        severity=ViolationSeverity.CRITICAL,
                        message=f"Setter 메서드 {method.name}() 금지. 불변 객체를 유지하세요.",
                        line_number=method.line_number,
                        node_type="method_declaration",
                        code_snippet=f"void {method.name}({method.parameters[0][0]} {method.parameters[0][1]})",
                    )
                )

        return violations

    def _check_use_case_single_responsibility(
        self, code: str, parser: JavaParser
    ) -> list[Violation]:
        """UseCase 단일 책임 검사"""
        violations = []

        classes = parser.find_classes(code)
        for cls in classes:
            # UseCase로 끝나는 클래스만 검사
            if not cls.name.endswith("UseCase"):
                continue

            # public 메서드 카운트 (생성자 제외)
            public_methods = [
                m
                for m in cls.methods
                if "public" in m.modifiers
                and m.name != cls.name  # 생성자 제외
                and not m.name.startswith("get")  # getter 제외
            ]

            if len(public_methods) > 1:
                method_names = [m.name for m in public_methods]
                violations.append(
                    Violation(
                        rule_code="APP-002",
                        rule_name="UseCase 단일 책임",
                        severity=ViolationSeverity.CRITICAL,
                        message=f"UseCase는 하나의 public 메서드만 가져야 합니다. 현재: {method_names}",
                        line_number=cls.node.start_point[0] + 1,
                        node_type="class_declaration",
                        code_snippet=cls.name,
                    )
                )

        return violations

    def _check_transactional_external_call(
        self, code: str, parser: JavaParser
    ) -> list[Violation]:
        """@Transactional 내 외부 API 호출 검사"""
        violations = []

        # 외부 API 호출 패턴
        external_patterns = [
            "restTemplate",
            "webClient",
            "feignClient",
            "httpClient",
            "RestClient",
            "WebClient",
        ]

        methods = parser.find_methods(code)
        for method in methods:
            has_transactional = (
                "@Transactional" in method.annotations
                or "Transactional" in method.annotations
            )
            if not has_transactional:
                continue

            # 메서드 본문에서 외부 API 호출 패턴 검사
            if method.body:
                for pattern in external_patterns:
                    if pattern.lower() in method.body.lower():
                        violations.append(
                            Violation(
                                rule_code="APP-004",
                                rule_name="@Transactional 내 외부 API 금지",
                                severity=ViolationSeverity.CRITICAL,
                                message=f"@Transactional 메서드에서 외부 API 호출({pattern}) 금지. Saga 패턴을 사용하세요.",
                                line_number=method.line_number,
                                node_type="method_declaration",
                                code_snippet=method.name,
                            )
                        )
                        break

        return violations

    def _check_jpa_relationship(self, code: str, parser: JavaParser) -> list[Violation]:
        """JPA 관계 어노테이션 검사"""
        violations = []

        for annotation in self.JPA_RELATIONSHIP_ANNOTATIONS:
            if parser.has_annotation(code, annotation):
                annotations = parser.find_annotations(code)
                for ann in annotations:
                    if ann.name.lstrip("@") == annotation:
                        violations.append(
                            Violation(
                                rule_code="ENT-002",
                                rule_name="JPA 관계 어노테이션 금지",
                                severity=ViolationSeverity.CRITICAL,
                                message=f"JPA 관계 어노테이션 @{annotation} 금지. Long FK 전략을 사용하세요.",
                                line_number=ann.line_number,
                                node_type="annotation",
                                code_snippet=f"@{annotation}",
                            )
                        )
                        break

        return violations

    def _check_controller_transactional(
        self, code: str, parser: JavaParser
    ) -> list[Violation]:
        """Controller @Transactional 검사"""
        violations = []

        classes = parser.find_classes(code)
        for cls in classes:
            # Controller 클래스인지 확인
            is_controller = (
                cls.name.endswith("Controller")
                or "@RestController" in cls.annotations
                or "@Controller" in cls.annotations
                or "RestController" in cls.annotations
                or "Controller" in cls.annotations
            )

            if not is_controller:
                continue

            # 클래스 레벨 @Transactional 검사
            if (
                "@Transactional" in cls.annotations
                or "Transactional" in cls.annotations
            ):
                violations.append(
                    Violation(
                        rule_code="CTR-005",
                        rule_name="Controller @Transactional 금지",
                        severity=ViolationSeverity.CRITICAL,
                        message="Controller 클래스에 @Transactional 금지. UseCase에서 처리하세요.",
                        line_number=cls.node.start_point[0] + 1,
                        node_type="class_declaration",
                        code_snippet=cls.name,
                    )
                )

            # 메서드 레벨 @Transactional 검사
            for method in cls.methods:
                if (
                    "@Transactional" in method.annotations
                    or "Transactional" in method.annotations
                ):
                    violations.append(
                        Violation(
                            rule_code="CTR-005",
                            rule_name="Controller @Transactional 금지",
                            severity=ViolationSeverity.CRITICAL,
                            message=f"Controller 메서드 {method.name}()에 @Transactional 금지. UseCase에서 처리하세요.",
                            line_number=method.line_number,
                            node_type="method_declaration",
                            code_snippet=method.name,
                        )
                    )

        return violations

    def _check_mock_mvc_usage(self, code: str, parser: JavaParser) -> list[Violation]:
        """MockMvc 사용 검사"""
        violations = []

        imports = parser.find_imports(code)
        for imp in imports:
            if "MockMvc" in imp:
                violations.append(
                    Violation(
                        rule_code="CTR-001",
                        rule_name="MockMvc 테스트 금지",
                        severity=ViolationSeverity.CRITICAL,
                        message="MockMvc 사용 금지. TestRestTemplate을 사용하세요.",
                        node_type="import_declaration",
                        code_snippet=f"import {imp}",
                    )
                )
                # import에서 발견되면 중복 보고 방지를 위해 바로 반환
                return violations

        # import가 없는 경우에만 필드/파라미터 검사 (import가 있으면 이미 반환됨)
        if "MockMvc" in code and "mockMvc" in code.lower():
            violations.append(
                Violation(
                    rule_code="CTR-001",
                    rule_name="MockMvc 테스트 금지",
                    severity=ViolationSeverity.CRITICAL,
                    message="MockMvc 사용 금지. TestRestTemplate을 사용하세요.",
                    node_type="field_declaration",
                    code_snippet="MockMvc mockMvc",
                )
            )

        return violations


def get_zero_tolerance_rules() -> ZeroToleranceRules:
    """싱글톤 ZeroToleranceRules 인스턴스 반환"""
    global _rules
    if _rules is None:
        _rules = ZeroToleranceRules()
    return _rules
