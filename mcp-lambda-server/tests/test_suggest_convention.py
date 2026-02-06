"""
suggest_convention 도구 테스트

3가지 전략 + 투표 + 유사규칙 조회
"""

from unittest.mock import MagicMock, patch

import pytest

from src.tools.suggest import (
    _strategy_applies_to_layer,
    _strategy_code_prefix,
    _strategy_description_keywords,
    suggest_convention,
)


# 테스트용 convention 데이터
MOCK_CONVENTIONS = [
    {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True, "description": "Domain Convention"},
    {"id": 17, "module_name": "application", "layer_code": "APPLICATION", "active": True, "description": "App Convention"},
    {"id": 18, "module_name": "persistence-mysql", "layer_code": "ADAPTER_OUT", "active": True, "description": "Persistence"},
    {"id": 19, "module_name": "rest-api", "layer_code": "ADAPTER_IN", "active": True, "description": "REST API"},
    {"id": 20, "module_name": "bootstrap", "layer_code": "BOOTSTRAP", "active": True, "description": "Bootstrap"},
]


class TestStrategyCodePrefix:
    """전략 1: 코드 prefix 기반 추천"""

    def test_domain_prefix(self):
        result = _strategy_code_prefix("DOM-AGG-001", MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["id"] == 16
        assert result["confidence"] == 0.9
        assert result["strategy"] == "code_prefix"

    def test_api_prefix(self):
        result = _strategy_code_prefix("API-DTO-SEARCH-002", MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["id"] == 19

    def test_app_prefix(self):
        result = _strategy_code_prefix("APP-SVC-001", MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["id"] == 17

    def test_persistence_prefix(self):
        result = _strategy_code_prefix("PER-REPO-001", MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["id"] == 18

    def test_bootstrap_prefix(self):
        result = _strategy_code_prefix("BOOT-CFG-001", MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["id"] == 20

    def test_unknown_prefix_returns_none(self):
        result = _strategy_code_prefix("XYZ-001", MOCK_CONVENTIONS)
        assert result is None

    def test_empty_code_returns_none(self):
        result = _strategy_code_prefix("", MOCK_CONVENTIONS)
        assert result is None

    def test_case_insensitive(self):
        result = _strategy_code_prefix("dom-agg-001", MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["id"] == 16


class TestStrategyAppliesToLayer:
    """전략 2: appliesTo class_type → 레이어 역추적"""

    def test_aggregate_maps_to_domain(self):
        result = _strategy_applies_to_layer(["AGGREGATE"], MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["layer_code"] == "DOMAIN"
        assert result["confidence"] == 0.85

    def test_controller_maps_to_adapter_in(self):
        result = _strategy_applies_to_layer(["CONTROLLER"], MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["layer_code"] == "ADAPTER_IN"

    def test_entity_maps_to_adapter_out(self):
        result = _strategy_applies_to_layer(["ENTITY"], MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["layer_code"] == "ADAPTER_OUT"

    def test_use_case_maps_to_application(self):
        result = _strategy_applies_to_layer(["USE_CASE"], MOCK_CONVENTIONS)
        assert result is not None
        assert result["convention"]["layer_code"] == "APPLICATION"

    def test_multiple_types_majority_vote(self):
        """여러 타입 → 다수결로 레이어 결정"""
        result = _strategy_applies_to_layer(
            ["AGGREGATE", "VALUE_OBJECT", "CONTROLLER"], MOCK_CONVENTIONS
        )
        assert result is not None
        # AGGREGATE, VALUE_OBJECT → DOMAIN (2표) vs CONTROLLER → ADAPTER_IN (1표)
        assert result["convention"]["layer_code"] == "DOMAIN"

    def test_unknown_class_type_returns_none(self):
        result = _strategy_applies_to_layer(["UNKNOWN_TYPE"], MOCK_CONVENTIONS)
        assert result is None

    def test_empty_list_returns_none(self):
        result = _strategy_applies_to_layer([], MOCK_CONVENTIONS)
        assert result is None


class TestStrategyDescriptionKeywords:
    """전략 3: description 키워드 매칭"""

    def test_domain_keywords(self):
        result = _strategy_description_keywords(
            "도메인 aggregate 규칙", MOCK_CONVENTIONS
        )
        assert result is not None
        assert result["convention"]["layer_code"] == "DOMAIN"
        assert result["strategy"] == "description_keywords"

    def test_api_keywords(self):
        result = _strategy_description_keywords(
            "REST API controller 요청 규칙", MOCK_CONVENTIONS
        )
        assert result is not None
        assert result["convention"]["layer_code"] == "ADAPTER_IN"

    def test_application_keywords(self):
        result = _strategy_description_keywords(
            "application service use case 규칙", MOCK_CONVENTIONS
        )
        assert result is not None
        assert result["convention"]["layer_code"] == "APPLICATION"

    def test_persistence_keywords(self):
        result = _strategy_description_keywords(
            "persistence repository JPA entity 규칙", MOCK_CONVENTIONS
        )
        assert result is not None
        assert result["convention"]["layer_code"] == "ADAPTER_OUT"

    def test_no_keyword_match_returns_none(self):
        result = _strategy_description_keywords(
            "아무 관련 없는 텍스트", MOCK_CONVENTIONS
        )
        assert result is None

    def test_confidence_increases_with_more_keywords(self):
        # 1 keyword
        result1 = _strategy_description_keywords("domain", MOCK_CONVENTIONS)
        # 3 keywords
        result3 = _strategy_description_keywords("domain aggregate value object", MOCK_CONVENTIONS)

        assert result1 is not None
        assert result3 is not None
        assert result3["confidence"] > result1["confidence"]


class TestSuggestConvention:
    """suggest_convention 통합 테스트"""

    @patch("src.tools.suggest.get_api_client")
    def test_no_input_returns_error(self, mock_get_client):
        result = suggest_convention()
        assert result["success"] is False
        assert "하나 이상" in result["error"]

    @patch("src.tools.suggest.get_api_client")
    def test_code_prefix_suggestion(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = MOCK_CONVENTIONS
        mock_client.get_coding_rules.return_value = []

        result = suggest_convention(code="API-DTO-SEARCH-002")

        assert result["success"] is True
        assert result["suggested_convention"]["id"] == 19
        assert result["suggested_convention"]["layer_code"] == "ADAPTER_IN"
        assert "all_conventions" in result

    @patch("src.tools.suggest.get_api_client")
    def test_applies_to_suggestion(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = MOCK_CONVENTIONS

        result = suggest_convention(applies_to=["AGGREGATE", "VALUE_OBJECT"])

        assert result["success"] is True
        assert result["suggested_convention"]["layer_code"] == "DOMAIN"

    @patch("src.tools.suggest.get_api_client")
    def test_description_suggestion(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = MOCK_CONVENTIONS

        result = suggest_convention(description="REST API controller 요청 DTO 규칙")

        assert result["success"] is True
        assert result["suggested_convention"]["layer_code"] == "ADAPTER_IN"

    @patch("src.tools.suggest.get_api_client")
    def test_multiple_strategies_confidence_bonus(self, mock_get_client):
        """여러 전략이 동일 convention 추천 → confidence 보너스"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = MOCK_CONVENTIONS
        mock_client.get_coding_rules.return_value = []

        result = suggest_convention(
            code="API-DTO-001",
            applies_to=["CONTROLLER", "REQUEST_DTO"],
            description="REST API controller 규칙",
        )

        assert result["success"] is True
        assert result["suggested_convention"]["layer_code"] == "ADAPTER_IN"
        # 3가지 전략 모두 ADAPTER_IN → confidence > 0.9
        assert result["suggested_convention"]["confidence"] > 0.9

    @patch("src.tools.suggest.get_api_client")
    def test_similar_rules_included(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = MOCK_CONVENTIONS
        mock_client.get_coding_rules.return_value = [
            MagicMock(code="API-DTO-001", name="API DTO 기본 규칙"),
            MagicMock(code="API-DTO-002", name="Request DTO 설계 규칙"),
        ]

        result = suggest_convention(code="API-DTO-SEARCH-002")

        assert result["success"] is True
        assert len(result["existing_similar_rules"]) == 2

    @patch("src.tools.suggest.get_api_client")
    def test_no_match_returns_all_conventions(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = MOCK_CONVENTIONS

        result = suggest_convention(description="전혀 관련 없는 내용")

        assert result["success"] is True
        assert result["suggested_convention"] is None
        assert len(result["all_conventions"]) == 5

    @patch("src.tools.suggest.get_api_client")
    def test_api_failure_returns_error(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.side_effect = Exception("API error")

        result = suggest_convention(code="API-DTO-001")

        assert result["success"] is False
        assert "조회 실패" in result["error"]

    @patch("src.tools.suggest.get_api_client")
    def test_all_conventions_in_response(self, mock_get_client):
        """응답에 항상 all_conventions 포함"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = MOCK_CONVENTIONS
        mock_client.get_coding_rules.return_value = []

        result = suggest_convention(code="DOM-AGG-001")

        assert "all_conventions" in result
        assert len(result["all_conventions"]) == 5
