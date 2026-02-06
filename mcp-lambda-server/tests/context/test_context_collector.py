"""
Minimal Context Collector Tests

AESA-119: get_minimal_context() 구현 검증 테스트
API 호출이 필요한 테스트는 mock 처리하여 단위 테스트로 실행
"""

import pytest
import httpx
from unittest.mock import patch, MagicMock

from src.context.context_collector import (
    INTENT_CONTEXT_REQUIREMENTS,
    MinimalContext,
    get_minimal_context,
    _estimate_tokens,
    _generate_context_summary,
    _simplify_rule,
    _simplify_template,
    _simplify_dependency,
)
from src.models import IntentType


class TestMinimalContextModel:
    """MinimalContext Pydantic 모델 테스트"""

    def test_model_with_required_fields(self):
        """필수 필드만으로 모델 생성"""
        ctx = MinimalContext(
            intent_type="CREATE_AGGREGATE",
            context_summary="테스트 요약",
        )
        assert ctx.intent_type == "CREATE_AGGREGATE"
        assert ctx.target_layer is None
        assert ctx.class_type is None
        assert ctx.zero_tolerance_rules == []
        assert ctx.layer_rules == []
        assert ctx.class_template is None
        assert ctx.layer_dependencies == []
        assert ctx.token_estimate == 0
        assert ctx.context_summary == "테스트 요약"

    def test_model_with_all_fields(self):
        """모든 필드로 모델 생성"""
        ctx = MinimalContext(
            intent_type="CREATE_AGGREGATE",
            target_layer="DOMAIN",
            class_type="AGGREGATE",
            zero_tolerance_rules=[{"code": "AGG-001", "name": "Lombok 금지"}],
            layer_rules=[{"code": "DOM-001", "name": "Tell, Don't Ask"}],
            class_template={"class_type": "AGGREGATE", "template_code": "..."},
            layer_dependencies=[
                {"source_layer": "DOMAIN", "target_layer": "APPLICATION"}
            ],
            token_estimate=2500,
            context_summary="전체 컨텍스트",
        )
        assert ctx.intent_type == "CREATE_AGGREGATE"
        assert ctx.target_layer == "DOMAIN"
        assert ctx.class_type == "AGGREGATE"
        assert len(ctx.zero_tolerance_rules) == 1
        assert len(ctx.layer_rules) == 1
        assert ctx.class_template is not None
        assert len(ctx.layer_dependencies) == 1
        assert ctx.token_estimate == 2500

    def test_model_dump(self):
        """모델 직렬화 테스트"""
        ctx = MinimalContext(
            intent_type="CREATE_AGGREGATE",
            target_layer="DOMAIN",
            context_summary="테스트",
        )
        data = ctx.model_dump()
        assert isinstance(data, dict)
        assert data["intent_type"] == "CREATE_AGGREGATE"
        assert data["target_layer"] == "DOMAIN"


class TestIntentContextRequirements:
    """의도별 컨텍스트 요구사항 매핑 테스트"""

    def test_all_intent_types_mapped(self):
        """모든 의도 타입이 매핑되어 있는지 확인"""
        for intent_type in IntentType:
            assert intent_type.value in INTENT_CONTEXT_REQUIREMENTS, (
                f"IntentType.{intent_type.name} ({intent_type.value}) is not mapped"
            )

    def test_create_aggregate_requirements(self):
        """CREATE_AGGREGATE 요구사항 확인"""
        req = INTENT_CONTEXT_REQUIREMENTS["CREATE_AGGREGATE"]
        assert req["need_zero_tolerance"] is True
        assert req["need_layer_rules"] is True
        assert req["need_template"] is True
        assert req["target_layer"] == "DOMAIN"
        assert req["class_type"] == "AGGREGATE"
        assert "STRUCTURE" in req["rule_categories"]

    def test_create_use_case_requirements(self):
        """CREATE_USE_CASE 요구사항 확인"""
        req = INTENT_CONTEXT_REQUIREMENTS["CREATE_USE_CASE"]
        assert req["need_zero_tolerance"] is True
        assert req["need_layer_rules"] is True
        assert req["need_template"] is True
        assert req["target_layer"] == "APPLICATION"
        assert req["class_type"] == "USE_CASE"
        assert "ANNOTATION" in req["rule_categories"]

    def test_add_method_requirements(self):
        """ADD_METHOD 요구사항 확인 (동적 레이어)"""
        req = INTENT_CONTEXT_REQUIREMENTS["ADD_METHOD"]
        assert req["need_zero_tolerance"] is True
        assert req["need_layer_rules"] is True
        assert req["need_template"] is False
        assert req["target_layer"] is None  # 동적으로 결정
        assert req["class_type"] is None

    def test_explain_code_requirements(self):
        """EXPLAIN_CODE 요구사항 확인 (최소 컨텍스트)"""
        req = INTENT_CONTEXT_REQUIREMENTS["EXPLAIN_CODE"]
        assert req["need_zero_tolerance"] is False
        assert req["need_layer_rules"] is False
        assert req["need_template"] is False


class TestHelperFunctions:
    """헬퍼 함수 테스트"""

    def test_estimate_tokens_empty_context(self):
        """빈 컨텍스트 토큰 추정"""
        ctx = MinimalContext(
            intent_type="TEST",
            context_summary="test",
        )
        tokens = _estimate_tokens(ctx)
        assert tokens == 0

    def test_estimate_tokens_with_data(self):
        """데이터가 있는 컨텍스트 토큰 추정"""
        ctx = MinimalContext(
            intent_type="TEST",
            zero_tolerance_rules=[
                {"code": "ABC", "description": "Test rule with some content"}
            ],
            layer_rules=[{"code": "DEF", "description": "Another rule"}],
            class_template={
                "class_type": "AGGREGATE",
                "template_code": "public class Test {}",
            },
            context_summary="test",
        )
        tokens = _estimate_tokens(ctx)
        assert tokens > 0

    def test_generate_context_summary(self):
        """컨텍스트 요약 생성"""
        ctx = MinimalContext(
            intent_type="TEST",
            target_layer="DOMAIN",
            class_type="AGGREGATE",
            zero_tolerance_rules=[{"code": "A"}],
            layer_rules=[{"code": "B"}, {"code": "C"}],
            class_template={"type": "test"},
            token_estimate=100,
            context_summary="",
        )
        summary = _generate_context_summary(ctx)
        assert "DOMAIN" in summary
        assert "AGGREGATE" in summary
        assert "1개" in summary  # zero_tolerance
        assert "2개" in summary  # layer_rules
        assert "템플릿" in summary
        assert "100" in summary

    def test_simplify_rule(self):
        """규칙 간소화"""
        mock_rule = MagicMock()
        mock_rule.code = "AGG-001"
        mock_rule.name = "Lombok 금지"
        mock_rule.severity = "ERROR"
        mock_rule.category = "STRUCTURE"
        mock_rule.description = "Domain에서 Lombok 사용 금지"
        mock_rule.rationale = "명시적 코드 작성"
        mock_rule.zero_tolerance = True

        result = _simplify_rule(mock_rule)
        assert result["code"] == "AGG-001"
        assert result["name"] == "Lombok 금지"
        assert result["zero_tolerance"] is True

    def test_simplify_template(self):
        """템플릿 간소화"""
        mock_template = MagicMock()
        mock_template.class_type = "AGGREGATE"
        mock_template.template_code = "public class {ClassName} {}"
        mock_template.naming_pattern = "*Aggregate"
        mock_template.required_annotations = []
        mock_template.forbidden_annotations = ["@Data"]
        mock_template.description = "Aggregate 템플릿"

        result = _simplify_template(mock_template)
        assert result["class_type"] == "AGGREGATE"
        assert result["naming_pattern"] == "*Aggregate"
        assert "@Data" in result["forbidden_annotations"]

    def test_simplify_dependency(self):
        """의존성 간소화"""
        mock_dep = MagicMock()
        mock_dep.source_layer = "DOMAIN"
        mock_dep.target_layer = "APPLICATION"
        mock_dep.allowed = False
        mock_dep.direction = "OUTBOUND"
        mock_dep.rationale = "Domain은 Application에 의존하면 안됨"

        result = _simplify_dependency(mock_dep)
        assert result["source_layer"] == "DOMAIN"
        assert result["target_layer"] == "APPLICATION"
        assert result["allowed"] is False


class TestGetMinimalContextWithMock:
    """get_minimal_context() 함수 테스트 (API 호출 Mock)"""

    @patch("src.context.context_collector.get_api_client")
    def test_create_aggregate_context(self, mock_get_client):
        """CREATE_AGGREGATE 컨텍스트 수집 (Mock)"""
        # Mock 설정
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_zero_tolerance_rules.return_value = []
        mock_client.get_coding_rules_by_layer.return_value = []
        mock_client.get_class_template_by_type.return_value = None
        mock_client.get_layer_dependencies.return_value = []

        ctx = get_minimal_context("CREATE_AGGREGATE")

        assert ctx.intent_type == "CREATE_AGGREGATE"
        assert ctx.target_layer == "DOMAIN"
        assert ctx.class_type == "AGGREGATE"
        assert ctx.context_summary != ""

    @patch("src.context.context_collector.get_api_client")
    def test_create_use_case_context(self, mock_get_client):
        """CREATE_USE_CASE 컨텍스트 수집 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_zero_tolerance_rules.return_value = []
        mock_client.get_coding_rules_by_layer.return_value = []
        mock_client.get_class_template_by_type.return_value = None
        mock_client.get_layer_dependencies.return_value = []

        ctx = get_minimal_context("CREATE_USE_CASE")

        assert ctx.intent_type == "CREATE_USE_CASE"
        assert ctx.target_layer == "APPLICATION"
        assert ctx.class_type == "USE_CASE"

    @patch("src.context.context_collector.get_api_client")
    def test_create_controller_context(self, mock_get_client):
        """CREATE_CONTROLLER 컨텍스트 수집 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_zero_tolerance_rules.return_value = []
        mock_client.get_coding_rules_by_layer.return_value = []
        mock_client.get_class_template_by_type.return_value = None
        mock_client.get_layer_dependencies.return_value = []

        ctx = get_minimal_context("CREATE_CONTROLLER")

        assert ctx.intent_type == "CREATE_CONTROLLER"
        assert ctx.target_layer == "ADAPTER_IN"
        assert ctx.class_type == "CONTROLLER"

    @patch("src.context.context_collector.get_api_client")
    def test_create_entity_context(self, mock_get_client):
        """CREATE_ENTITY 컨텍스트 수집 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_zero_tolerance_rules.return_value = []
        mock_client.get_coding_rules_by_layer.return_value = []
        mock_client.get_class_template_by_type.return_value = None
        mock_client.get_layer_dependencies.return_value = []

        ctx = get_minimal_context("CREATE_ENTITY")

        assert ctx.intent_type == "CREATE_ENTITY"
        assert ctx.target_layer == "ADAPTER_OUT"
        assert ctx.class_type == "ENTITY"

    @patch("src.context.context_collector.get_api_client")
    def test_add_method_with_explicit_layer(self, mock_get_client):
        """ADD_METHOD에 명시적 레이어 지정 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_zero_tolerance_rules.return_value = []
        mock_client.get_coding_rules_by_layer.return_value = []
        mock_client.get_layer_dependencies.return_value = []

        ctx = get_minimal_context("ADD_METHOD", target_layer="APPLICATION")

        assert ctx.intent_type == "ADD_METHOD"
        assert ctx.target_layer == "APPLICATION"
        assert ctx.class_template is None  # ADD_METHOD는 템플릿 불필요

    @patch("src.context.context_collector.get_api_client")
    def test_explain_code_minimal_context(self, mock_get_client):
        """EXPLAIN_CODE 최소 컨텍스트 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client

        ctx = get_minimal_context("EXPLAIN_CODE")

        assert ctx.intent_type == "EXPLAIN_CODE"
        # EXPLAIN_CODE는 규칙이나 템플릿이 필요 없음
        assert len(ctx.zero_tolerance_rules) == 0
        assert len(ctx.layer_rules) == 0
        assert ctx.class_template is None
        # API 호출이 없어야 함
        mock_client.get_zero_tolerance_rules.assert_not_called()
        mock_client.get_coding_rules_by_layer.assert_not_called()

    @patch("src.context.context_collector.get_api_client")
    def test_unknown_intent_fallback(self, mock_get_client):
        """알 수 없는 의도 → UNKNOWN 폴백 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_zero_tolerance_rules.return_value = []
        mock_client.get_coding_rules_by_layer.return_value = []
        mock_client.get_layer_dependencies.return_value = []

        ctx = get_minimal_context("NONEXISTENT_INTENT")

        assert ctx.intent_type == "NONEXISTENT_INTENT"
        # UNKNOWN 요구사항 적용됨

    @patch("src.context.context_collector.get_api_client")
    def test_without_dependencies(self, mock_get_client):
        """의존성 규칙 제외 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_zero_tolerance_rules.return_value = []
        mock_client.get_coding_rules_by_layer.return_value = []
        mock_client.get_class_template_by_type.return_value = None

        ctx = get_minimal_context("CREATE_AGGREGATE", include_dependencies=False)

        assert ctx.intent_type == "CREATE_AGGREGATE"
        assert ctx.layer_dependencies == []
        # get_layer_dependencies가 호출되지 않아야 함
        mock_client.get_layer_dependencies.assert_not_called()

    @patch("src.context.context_collector.get_api_client")
    def test_explicit_class_type_override(self, mock_get_client):
        """명시적 class_type 오버라이드 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_zero_tolerance_rules.return_value = []
        mock_client.get_coding_rules_by_layer.return_value = []
        mock_client.get_layer_dependencies.return_value = []

        ctx = get_minimal_context(
            "ADD_METHOD",
            target_layer="DOMAIN",
            class_type="AGGREGATE",
        )

        assert ctx.intent_type == "ADD_METHOD"
        assert ctx.target_layer == "DOMAIN"
        assert ctx.class_type == "AGGREGATE"

    @patch("src.context.context_collector.get_api_client")
    def test_with_real_rules(self, mock_get_client):
        """실제 규칙 데이터가 있는 경우 (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client

        # Mock 규칙 생성
        mock_zt_rule = MagicMock()
        mock_zt_rule.code = "AGG-001"
        mock_zt_rule.name = "Lombok 금지"
        mock_zt_rule.severity = "ERROR"
        mock_zt_rule.category = "STRUCTURE"
        mock_zt_rule.description = "Domain에서 Lombok 금지"
        mock_zt_rule.rationale = "명시적 코드"
        mock_zt_rule.zero_tolerance = True

        mock_layer_rule = MagicMock()
        mock_layer_rule.code = "DOM-002"
        mock_layer_rule.name = "Tell Don't Ask"
        mock_layer_rule.severity = "WARNING"
        mock_layer_rule.category = "BEHAVIOR"
        mock_layer_rule.description = "상태를 묻지 말고 행동을 요청"
        mock_layer_rule.rationale = "캡슐화"
        mock_layer_rule.zero_tolerance = False

        mock_template = MagicMock()
        mock_template.class_type = "AGGREGATE"
        mock_template.template_code = "public class {Name} {}"
        mock_template.naming_pattern = "*Aggregate"
        mock_template.required_annotations = []
        mock_template.forbidden_annotations = ["@Data"]
        mock_template.description = "Aggregate 템플릿"

        mock_client.get_zero_tolerance_rules.return_value = [mock_zt_rule]
        mock_client.get_coding_rules_by_layer.return_value = [mock_layer_rule]
        mock_client.get_class_template_by_type.return_value = mock_template
        mock_client.get_layer_dependencies.return_value = []

        ctx = get_minimal_context("CREATE_AGGREGATE")

        assert len(ctx.zero_tolerance_rules) == 1
        assert ctx.zero_tolerance_rules[0]["code"] == "AGG-001"
        assert len(ctx.layer_rules) == 1
        assert ctx.layer_rules[0]["code"] == "DOM-002"
        assert ctx.class_template is not None
        assert ctx.class_template["class_type"] == "AGGREGATE"
        assert ctx.token_estimate > 0

    @patch("src.context.context_collector.get_api_client")
    def test_zero_tolerance_error_propagation(self, mock_get_client):
        """Zero-Tolerance 규칙 수집 실패 시 RuntimeError 전파 (가드레일 핵심)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client

        # httpx.RequestError로 네트워크 오류 시뮬레이션
        mock_client.get_zero_tolerance_rules.side_effect = httpx.RequestError(
            "Connection failed"
        )

        # Zero-Tolerance 실패는 RuntimeError로 전파되어야 함
        with pytest.raises(RuntimeError) as exc_info:
            get_minimal_context("CREATE_AGGREGATE")

        assert "Zero-Tolerance" in str(exc_info.value)
        assert "가드레일" in str(exc_info.value)

    @patch("src.context.context_collector.get_api_client")
    def test_non_critical_api_error_graceful_degradation(self, mock_get_client):
        """비필수 API 에러 시 graceful degradation (Mock)"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client

        # Zero-Tolerance는 정상 동작
        mock_client.get_zero_tolerance_rules.return_value = []

        # 비필수 항목들은 네트워크 오류 발생
        mock_client.get_coding_rules_by_layer.side_effect = httpx.RequestError(
            "Connection failed"
        )
        mock_client.get_class_template_by_type.side_effect = httpx.RequestError(
            "Connection failed"
        )
        mock_client.get_layer_dependencies.side_effect = httpx.RequestError(
            "Connection failed"
        )

        ctx = get_minimal_context("CREATE_AGGREGATE")

        # 에러 시에도 빈 리스트로 정상 동작 (graceful degradation)
        assert ctx.intent_type == "CREATE_AGGREGATE"
        assert ctx.target_layer == "DOMAIN"
        assert ctx.zero_tolerance_rules == []
        assert ctx.layer_rules == []
        assert ctx.class_template is None
        assert ctx.layer_dependencies == []


class TestContextCollectionIntegration:
    """컨텍스트 수집 통합 테스트 (레이어/의도 매핑 확인)"""

    def test_all_domain_intents(self):
        """Domain 레이어 의도들의 요구사항 확인"""
        domain_intents = [
            "CREATE_AGGREGATE",
            "CREATE_VALUE_OBJECT",
            "CREATE_DOMAIN_EVENT",
            "CREATE_DOMAIN_EXCEPTION",
        ]
        for intent in domain_intents:
            req = INTENT_CONTEXT_REQUIREMENTS[intent]
            assert req["target_layer"] == "DOMAIN", f"Failed for {intent}"

    def test_all_application_intents(self):
        """Application 레이어 의도들의 요구사항 확인"""
        app_intents = [
            "CREATE_USE_CASE",
            "CREATE_COMMAND_SERVICE",
            "CREATE_QUERY_SERVICE",
            "CREATE_PORT",
        ]
        for intent in app_intents:
            req = INTENT_CONTEXT_REQUIREMENTS[intent]
            assert req["target_layer"] == "APPLICATION", f"Failed for {intent}"

    def test_all_persistence_intents(self):
        """Persistence 레이어 의도들의 요구사항 확인"""
        per_intents = [
            "CREATE_ENTITY",
            "CREATE_REPOSITORY",
            "CREATE_PERSISTENCE_ADAPTER",
        ]
        for intent in per_intents:
            req = INTENT_CONTEXT_REQUIREMENTS[intent]
            assert req["target_layer"] == "ADAPTER_OUT", f"Failed for {intent}"

    def test_all_rest_api_intents(self):
        """REST API 레이어 의도들의 요구사항 확인"""
        api_intents = [
            "CREATE_CONTROLLER",
            "CREATE_REQUEST_DTO",
            "CREATE_RESPONSE_DTO",
        ]
        for intent in api_intents:
            req = INTENT_CONTEXT_REQUIREMENTS[intent]
            assert req["target_layer"] == "ADAPTER_IN", f"Failed for {intent}"

    def test_modification_intents_have_no_fixed_layer(self):
        """수정 의도들은 레이어가 동적으로 결정됨"""
        mod_intents = [
            "ADD_METHOD",
            "MODIFY_LOGIC",
            "REFACTOR_CODE",
        ]
        for intent in mod_intents:
            req = INTENT_CONTEXT_REQUIREMENTS[intent]
            assert req["target_layer"] is None, f"Failed for {intent}"

    def test_analysis_intents_requirements(self):
        """분석 의도들의 요구사항 확인"""
        # ANALYZE_CODE는 규칙 필요, EXPLAIN_CODE는 필요 없음
        analyze_req = INTENT_CONTEXT_REQUIREMENTS["ANALYZE_CODE"]
        explain_req = INTENT_CONTEXT_REQUIREMENTS["EXPLAIN_CODE"]

        assert analyze_req["need_zero_tolerance"] is True
        assert analyze_req["need_layer_rules"] is True

        assert explain_req["need_zero_tolerance"] is False
        assert explain_req["need_layer_rules"] is False
