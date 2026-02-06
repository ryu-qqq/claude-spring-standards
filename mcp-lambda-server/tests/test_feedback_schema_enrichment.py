"""
get_feedback_schema 동적 enrichment 테스트
"""

from unittest.mock import MagicMock, patch

import pytest

from src.models import ClassTypeApiResponse


class TestGetStaticSchemas:
    """기존 정적 스키마 반환 테스트"""

    def test_returns_all_target_types(self):
        from src.server import _get_static_schemas

        schemas = _get_static_schemas()
        assert "RULE_EXAMPLE" in schemas
        assert "CODING_RULE" in schemas
        assert "CHECKLIST_ITEM" in schemas
        assert "CLASS_TEMPLATE" in schemas

    def test_coding_rule_has_convention_id_field(self):
        from src.server import _get_static_schemas

        schemas = _get_static_schemas()
        assert "conventionId" in schemas["CODING_RULE"]["add_schema"]

    def test_coding_rule_has_applies_to_field(self):
        from src.server import _get_static_schemas

        schemas = _get_static_schemas()
        assert "appliesTo" in schemas["CODING_RULE"]["add_schema"]


class TestEnrichCodingRuleSchema:
    """CODING_RULE 스키마 enrichment 테스트"""

    @patch("src.server.get_api_client")
    def test_enriches_convention_id_valid_values(self, mock_get_client):
        from src.server import _enrich_coding_rule_schema, _get_static_schemas

        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
            {"id": 19, "module_name": "rest-api", "layer_code": "ADAPTER_IN", "active": True},
        ]
        mock_client.get_class_types.return_value = [
            ClassTypeApiResponse(id=2, category_id=1, code="AGGREGATE_ROOT", name="Aggregate Root", order_index=1),
        ]

        schema = _get_static_schemas()["CODING_RULE"]
        enriched = _enrich_coding_rule_schema(schema)

        valid_values = enriched["add_schema"]["conventionId"]["valid_values"]
        assert len(valid_values) == 2
        assert valid_values[0]["id"] == 16
        assert valid_values[1]["layer_code"] == "ADAPTER_IN"

    @patch("src.server.get_api_client")
    def test_enriches_applies_to_valid_values(self, mock_get_client):
        from src.server import _enrich_coding_rule_schema, _get_static_schemas

        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = []
        mock_client.get_class_types.return_value = [
            ClassTypeApiResponse(id=2, category_id=1, code="AGGREGATE_ROOT", name="Aggregate Root", order_index=1),
            ClassTypeApiResponse(id=5, category_id=2, code="REQUEST_DTO", name="Request DTO", order_index=5),
        ]

        schema = _get_static_schemas()["CODING_RULE"]
        enriched = _enrich_coding_rule_schema(schema)

        valid_values = enriched["add_schema"]["appliesTo"]["valid_values"]
        assert len(valid_values) == 2
        assert valid_values[0]["code"] == "AGGREGATE_ROOT"
        assert valid_values[1]["code"] == "REQUEST_DTO"

    @patch("src.server.get_api_client")
    def test_filters_inactive_conventions(self, mock_get_client):
        from src.server import _enrich_coding_rule_schema, _get_static_schemas

        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
            {"id": 17, "module_name": "old-module", "layer_code": "DOMAIN", "active": False},
        ]
        mock_client.get_class_types.return_value = []

        schema = _get_static_schemas()["CODING_RULE"]
        enriched = _enrich_coding_rule_schema(schema)

        valid_values = enriched["add_schema"]["conventionId"]["valid_values"]
        assert len(valid_values) == 1
        assert valid_values[0]["id"] == 16

    @patch("src.server.get_api_client")
    def test_graceful_degradation_on_convention_api_failure(self, mock_get_client):
        from src.server import _enrich_coding_rule_schema, _get_static_schemas

        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.side_effect = Exception("API error")
        mock_client.get_class_types.return_value = [
            ClassTypeApiResponse(id=2, category_id=1, code="AGGREGATE_ROOT", name="Aggregate Root", order_index=1),
        ]

        schema = _get_static_schemas()["CODING_RULE"]
        enriched = _enrich_coding_rule_schema(schema)

        # conventionId에는 valid_values가 없어야 함
        assert "valid_values" not in enriched["add_schema"]["conventionId"]
        # appliesTo에는 정상 주입
        assert "valid_values" in enriched["add_schema"]["appliesTo"]

    @patch("src.server.get_api_client")
    def test_graceful_degradation_on_classtype_api_failure(self, mock_get_client):
        from src.server import _enrich_coding_rule_schema, _get_static_schemas

        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
        ]
        mock_client.get_class_types.side_effect = Exception("API error")

        schema = _get_static_schemas()["CODING_RULE"]
        enriched = _enrich_coding_rule_schema(schema)

        assert "valid_values" in enriched["add_schema"]["conventionId"]
        assert "valid_values" not in enriched["add_schema"]["appliesTo"]

    @patch("src.server.get_api_client")
    def test_graceful_degradation_on_client_init_failure(self, mock_get_client):
        from src.server import _enrich_coding_rule_schema, _get_static_schemas

        mock_get_client.side_effect = Exception("Client init error")

        schema = _get_static_schemas()["CODING_RULE"]
        enriched = _enrich_coding_rule_schema(schema)

        # 기존 스키마 그대로 반환
        assert "valid_values" not in enriched["add_schema"]["conventionId"]
        assert "valid_values" not in enriched["add_schema"]["appliesTo"]
