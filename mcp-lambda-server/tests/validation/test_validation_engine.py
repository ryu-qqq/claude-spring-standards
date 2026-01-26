"""
Validation Engine Tests
AESA-125~128: Tree-sitter 기반 Java 코드 검증 시스템 테스트
"""

from src.validation import (
    get_java_parser,
    get_validation_engine,
    ValidationResult,
    Violation,
    ViolationSeverity,
    FixSuggestion,
    ValidationContext,
    get_zero_tolerance_rules,
)


# ============================================
# JavaParser Tests (AESA-125)
# ============================================


class TestJavaParser:
    """Tree-sitter Java 파서 테스트"""

    def test_get_java_parser_singleton(self):
        """싱글톤 패턴 테스트"""
        parser1 = get_java_parser()
        parser2 = get_java_parser()
        assert parser1 is parser2

    def test_parse_simple_class(self):
        """간단한 클래스 파싱"""
        parser = get_java_parser()
        code = """
        public class Order {
            private Long id;
            private String name;
        }
        """
        classes = parser.find_classes(code)
        assert len(classes) == 1
        assert classes[0].name == "Order"
        assert "public" in classes[0].modifiers

    def test_find_annotations(self):
        """어노테이션 찾기"""
        parser = get_java_parser()
        code = """
        @Data
        @Entity
        public class Order {
            @Id
            private Long id;
        }
        """
        annotations = parser.find_annotations(code)
        annotation_names = [a.name for a in annotations]
        assert "Data" in annotation_names
        assert "Entity" in annotation_names
        assert "Id" in annotation_names

    def test_has_annotation(self):
        """특정 어노테이션 존재 확인"""
        parser = get_java_parser()
        code = """
        @Data
        public class Order {}
        """
        assert parser.has_annotation(code, "Data")
        assert parser.has_annotation(code, "@Data")
        assert not parser.has_annotation(code, "Entity")

    def test_find_methods(self):
        """메서드 찾기"""
        parser = get_java_parser()
        code = """
        public class Order {
            public void setName(String name) {
                this.name = name;
            }

            public String getName() {
                return this.name;
            }
        }
        """
        methods = parser.find_methods(code)
        assert len(methods) == 2
        method_names = [m.name for m in methods]
        assert "setName" in method_names
        assert "getName" in method_names

    def test_find_fields(self):
        """필드 찾기"""
        parser = get_java_parser()
        code = """
        public class Order {
            private Long id;
            private String name;
            private final Money amount;
        }
        """
        fields = parser.find_fields(code)
        assert len(fields) == 3
        field_names = [f.name for f in fields]
        assert "id" in field_names
        assert "name" in field_names
        assert "amount" in field_names

    def test_find_imports(self):
        """import 문 찾기"""
        parser = get_java_parser()
        code = """
        import java.util.List;
        import com.example.domain.Money;

        public class Order {}
        """
        imports = parser.find_imports(code)
        assert len(imports) == 2
        assert "java.util.List" in imports
        assert "com.example.domain.Money" in imports

    def test_find_package(self):
        """패키지 선언 찾기"""
        parser = get_java_parser()
        code = """
        package com.example.domain.order;

        public class Order {}
        """
        package = parser.find_package(code)
        assert package == "com.example.domain.order"

    def test_check_method_chaining(self):
        """메서드 체이닝 검사"""
        parser = get_java_parser()
        code = """
        public class OrderService {
            public void process() {
                order.getCustomer().getAddress().getCity();
            }
        }
        """
        violations = parser.check_method_chaining(code)
        assert len(violations) > 0

    def test_parse_record(self):
        """Record 클래스 파싱"""
        parser = get_java_parser()
        code = """
        public record OrderResponse(Long id, String name) {}
        """
        classes = parser.find_classes(code)
        assert len(classes) == 1
        assert classes[0].name == "OrderResponse"


# ============================================
# ZeroToleranceRules Tests (AESA-126)
# ============================================


class TestZeroToleranceRules:
    """Zero-Tolerance 규칙 테스트"""

    def test_get_rules_singleton(self):
        """싱글톤 패턴 테스트"""
        rules1 = get_zero_tolerance_rules()
        rules2 = get_zero_tolerance_rules()
        assert rules1 is rules2

    def test_get_rules_for_domain_layer(self):
        """Domain 레이어 규칙 조회"""
        rules = get_zero_tolerance_rules()
        domain_rules = rules.get_rules_for_layer("DOMAIN")
        rule_codes = [r.code for r in domain_rules]
        assert "AGG-001" in rule_codes  # Lombok 금지
        assert "AGG-014" in rule_codes  # Law of Demeter
        assert "AGG-003" in rule_codes  # Setter 금지

    def test_get_rules_for_application_layer(self):
        """Application 레이어 규칙 조회"""
        rules = get_zero_tolerance_rules()
        app_rules = rules.get_rules_for_layer("APPLICATION")
        rule_codes = [r.code for r in app_rules]
        assert "APP-002" in rule_codes  # UseCase 단일 책임
        assert "APP-004" in rule_codes  # Transactional 내 외부 API 금지

    def test_get_rules_for_persistence_layer(self):
        """Persistence 레이어 규칙 조회"""
        rules = get_zero_tolerance_rules()
        persistence_rules = rules.get_rules_for_layer("PERSISTENCE")
        rule_codes = [r.code for r in persistence_rules]
        assert "ENT-002" in rule_codes  # JPA 관계 어노테이션 금지
        assert "ENT-001" in rule_codes  # Entity Lombok 금지

    def test_get_rules_for_rest_api_layer(self):
        """REST API 레이어 규칙 조회"""
        rules = get_zero_tolerance_rules()
        rest_rules = rules.get_rules_for_layer("REST_API")
        rule_codes = [r.code for r in rest_rules]
        assert "CTR-005" in rule_codes  # Controller @Transactional 금지
        assert "CTR-001" in rule_codes  # MockMvc 금지

    def test_validate_lombok_violation(self):
        """Lombok 사용 위반 검사"""
        rules = get_zero_tolerance_rules()
        code = """
        @Data
        public class Order {
            private Long id;
        }
        """
        violations = rules.validate(code, "DOMAIN")
        assert len(violations) > 0
        assert any(v.rule_code == "AGG-001" for v in violations)

    def test_validate_setter_violation(self):
        """Setter 메서드 위반 검사"""
        rules = get_zero_tolerance_rules()
        code = """
        public class Order {
            private String name;

            public void setName(String name) {
                this.name = name;
            }
        }
        """
        violations = rules.validate(code, "DOMAIN")
        assert len(violations) > 0
        assert any(v.rule_code == "AGG-003" for v in violations)

    def test_validate_jpa_relationship_violation(self):
        """JPA 관계 어노테이션 위반 검사"""
        rules = get_zero_tolerance_rules()
        code = """
        @Entity
        public class OrderEntity {
            @OneToMany
            private List<OrderItemEntity> items;
        }
        """
        violations = rules.validate(code, "PERSISTENCE")
        assert len(violations) > 0
        assert any(v.rule_code == "ENT-002" for v in violations)

    def test_validate_controller_transactional_violation(self):
        """Controller @Transactional 위반 검사"""
        rules = get_zero_tolerance_rules()
        code = """
        @RestController
        public class OrderController {
            @Transactional
            @PostMapping
            public void createOrder() {}
        }
        """
        violations = rules.validate(code, "REST_API")
        assert len(violations) > 0
        assert any(v.rule_code == "CTR-005" for v in violations)

    def test_validate_clean_code_no_violation(self):
        """위반 없는 깨끗한 코드"""
        rules = get_zero_tolerance_rules()
        code = """
        public class Order {
            private final Long id;
            private final String name;

            public Order(Long id, String name) {
                this.id = id;
                this.name = name;
            }

            public Long getId() {
                return this.id;
            }

            public String getName() {
                return this.name;
            }
        }
        """
        violations = rules.validate(code, "DOMAIN")
        # Lombok, Setter 위반 없음
        assert not any(v.rule_code == "AGG-001" for v in violations)
        assert not any(v.rule_code == "AGG-003" for v in violations)


# ============================================
# ValidationEngine Tests (AESA-126~128)
# ============================================


class TestValidationEngine:
    """ValidationEngine 통합 테스트"""

    def test_get_engine_singleton(self):
        """싱글톤 패턴 테스트"""
        engine1 = get_validation_engine()
        engine2 = get_validation_engine()
        assert engine1 is engine2

    def test_validate_with_context(self):
        """ValidationContext로 검증"""
        engine = get_validation_engine()
        code = """
        @Data
        public class Order {
            private Long id;
        }
        """
        context = ValidationContext(
            code=code,
            layer="DOMAIN",
            include_suggestions=True,
        )
        result = engine.validate(context)
        assert not result.is_valid
        assert result.critical_count > 0
        assert len(result.violations) > 0

    def test_validate_zero_tolerance(self):
        """Zero-Tolerance 규칙만 검증"""
        engine = get_validation_engine()
        code = """
        @Data
        public class Order {
            private Long id;
        }
        """
        result = engine.validate_zero_tolerance(code, "DOMAIN")
        assert not result.is_valid
        assert all(v.severity == ViolationSeverity.CRITICAL for v in result.violations)

    def test_validate_zero_tolerance_pass(self):
        """Zero-Tolerance 통과"""
        engine = get_validation_engine()
        code = """
        public class Order {
            private final Long id;

            public Order(Long id) {
                this.id = id;
            }

            public Long getId() {
                return this.id;
            }
        }
        """
        result = engine.validate_zero_tolerance(code, "DOMAIN")
        assert result.is_valid
        assert result.critical_count == 0

    def test_suggest_fixes(self):
        """수정 제안 생성"""
        engine = get_validation_engine()
        code = """
        @Data
        public class Order {
            private Long id;
        }
        """
        suggestions = engine.suggest_fix(code, "DOMAIN")
        assert len(suggestions) > 0
        assert all(isinstance(s, FixSuggestion) for s in suggestions)

    def test_suggest_fix_lombok(self):
        """Lombok 수정 제안"""
        engine = get_validation_engine()
        code = """
        @Data
        public class Order {}
        """
        suggestions = engine.suggest_fix(code, "DOMAIN")
        lombok_suggestion = next(
            (s for s in suggestions if s.violation.rule_code == "AGG-001"), None
        )
        assert lombok_suggestion is not None
        assert (
            "명시적" in lombok_suggestion.suggestion
            or "직접 구현" in lombok_suggestion.suggestion
        )

    def test_suggest_fix_setter(self):
        """Setter 수정 제안"""
        engine = get_validation_engine()
        code = """
        public class Order {
            private String name;
            public void setName(String name) {
                this.name = name;
            }
        }
        """
        suggestions = engine.suggest_fix(code, "DOMAIN")
        setter_suggestion = next(
            (s for s in suggestions if s.violation.rule_code == "AGG-003"), None
        )
        assert setter_suggestion is not None
        assert (
            "불변" in setter_suggestion.suggestion
            or "final" in setter_suggestion.suggestion
        )

    def test_validate_and_regenerate_pass_first_attempt(self):
        """첫 시도에 통과하는 경우"""
        engine = get_validation_engine()
        code = """
        public class Order {
            private final Long id;
            public Order(Long id) { this.id = id; }
        }
        """
        result, _final_code, attempts = engine.validate_and_regenerate(code, "DOMAIN")
        assert result.is_valid
        assert attempts == 1

    def test_validate_and_regenerate_with_auto_fix(self):
        """자동 수정 가능한 경우"""
        engine = get_validation_engine()
        # Controller @Transactional은 자동 수정 가능
        code = """
        @RestController
        public class OrderController {
            @Transactional
            @GetMapping
            public void getOrder() {}
        }
        """
        result, final_code, attempts = engine.validate_and_regenerate(code, "REST_API")
        # 자동 수정 후 @Transactional 제거 확인
        if result.is_valid:
            assert "@Transactional" not in final_code or attempts > 1

    def test_validate_and_regenerate_max_attempts(self):
        """최대 재시도 횟수 제한"""
        engine = get_validation_engine()
        # 자동 수정 불가능한 위반
        code = """
        @Data
        public class Order {}
        """
        result, _final_code, attempts = engine.validate_and_regenerate(
            code, "DOMAIN", max_attempts=3
        )
        # Lombok은 자동 수정 불가, 최대 시도 횟수 내에서 종료
        assert attempts <= 3


# ============================================
# ValidationResult Model Tests
# ============================================


class TestValidationResult:
    """ValidationResult 모델 테스트"""

    def test_add_critical_violation(self):
        """CRITICAL 위반 추가 시 is_valid = False"""
        result = ValidationResult(is_valid=True, layer="DOMAIN")
        violation = Violation(
            rule_code="AGG-001",
            rule_name="Lombok 금지",
            severity=ViolationSeverity.CRITICAL,
            message="Lombok 사용 금지",
        )
        result.add_violation(violation)
        assert not result.is_valid
        assert result.critical_count == 1

    def test_add_warning_violation(self):
        """WARNING 위반 추가 시 is_valid 유지"""
        result = ValidationResult(is_valid=True, layer="DOMAIN")
        violation = Violation(
            rule_code="TEST-001",
            rule_name="Test Warning",
            severity=ViolationSeverity.WARNING,
            message="경고 메시지",
        )
        result.add_violation(violation)
        assert result.is_valid
        assert result.warning_count == 1

    def test_get_critical_violations(self):
        """CRITICAL 위반만 필터링"""
        result = ValidationResult(is_valid=True, layer="DOMAIN")
        result.add_violation(
            Violation(
                rule_code="AGG-001",
                rule_name="Lombok 금지",
                severity=ViolationSeverity.CRITICAL,
                message="Critical",
            )
        )
        result.add_violation(
            Violation(
                rule_code="TEST-001",
                rule_name="Test Warning",
                severity=ViolationSeverity.WARNING,
                message="Warning",
            )
        )
        critical = result.get_critical_violations()
        assert len(critical) == 1
        assert critical[0].rule_code == "AGG-001"

    def test_to_dict(self):
        """딕셔너리 변환"""
        result = ValidationResult(is_valid=True, layer="DOMAIN")
        d = result.to_dict()
        assert "is_valid" in d
        assert "violations" in d
        assert "critical_count" in d
        assert d["layer"] == "DOMAIN"

    def test_summary(self):
        """요약 문자열 생성"""
        result = ValidationResult(is_valid=True, layer="DOMAIN")
        summary = result.summary()
        assert "PASS" in summary
        assert "DOMAIN" in summary


# ============================================
# FixSuggestion Model Tests
# ============================================


class TestFixSuggestion:
    """FixSuggestion 모델 테스트"""

    def test_fix_suggestion_creation(self):
        """FixSuggestion 생성"""
        violation = Violation(
            rule_code="AGG-001",
            rule_name="Lombok 금지",
            severity=ViolationSeverity.CRITICAL,
            message="Lombok 사용 금지",
        )
        suggestion = FixSuggestion(
            violation=violation,
            suggestion="명시적으로 구현하세요",
            before_code="@Data",
            after_code="// Lombok 제거",
            auto_fixable=False,
            confidence=0.9,
        )
        assert suggestion.violation.rule_code == "AGG-001"
        assert suggestion.confidence == 0.9
        assert not suggestion.auto_fixable

    def test_to_dict(self):
        """딕셔너리 변환"""
        violation = Violation(
            rule_code="AGG-001",
            rule_name="Lombok 금지",
            severity=ViolationSeverity.CRITICAL,
            message="Lombok 사용 금지",
        )
        suggestion = FixSuggestion(
            violation=violation,
            suggestion="명시적으로 구현하세요",
        )
        d = suggestion.to_dict()
        assert "violation" in d
        assert "suggestion" in d
        assert d["violation"]["rule_code"] == "AGG-001"
