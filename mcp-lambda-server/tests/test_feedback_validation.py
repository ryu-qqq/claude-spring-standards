"""
feedback 사전 검증 테스트

CODING_RULE ADD 시 conventionId/appliesTo 사전 검증 + 힌트
"""

from unittest.mock import MagicMock, patch

import pytest

from src.models import ClassTypeApiResponse
from src.tools.feedback import (
    _suggest_convention_by_code_prefix,
    _validate_coding_rule_add_payload,
)


class TestValidateCodingRuleAddPayload:
    """_validate_coding_rule_add_payload 테스트"""

    @patch("src.tools.feedback.get_api_client")
    def test_valid_payload_returns_none(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
        ]
        mock_client.get_class_types.return_value = [
            ClassTypeApiResponse(id=2, category_id=1, code="AGGREGATE_ROOT", name="Aggregate Root", order_index=1),
        ]

        result = _validate_coding_rule_add_payload({
            "conventionId": 16,
            "appliesTo": ["AGGREGATE_ROOT"],
        })
        assert result is None

    @patch("src.tools.feedback.get_api_client")
    def test_invalid_convention_id_returns_error_with_hints(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
            {"id": 19, "module_name": "rest-api", "layer_code": "ADAPTER_IN", "active": True},
        ]
        mock_client.get_class_types.return_value = []

        result = _validate_coding_rule_add_payload({
            "conventionId": 999,
            "code": "DOM-TEST-001",
        })

        assert result is not None
        assert result["success"] is False
        assert "999" in result["error"]
        assert "available_conventions" in result["hints"]
        assert len(result["hints"]["available_conventions"]) == 2
        # code prefix 기반 추천도 포함
        assert "suggested_convention" in result["hints"]
        assert result["hints"]["suggested_convention"]["layer_code"] == "DOMAIN"

    @patch("src.tools.feedback.get_api_client")
    def test_invalid_applies_to_returns_error_with_hints(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
        ]
        mock_client.get_class_types.return_value = [
            ClassTypeApiResponse(id=2, category_id=1, code="AGGREGATE_ROOT", name="Aggregate Root", order_index=1),
        ]

        result = _validate_coding_rule_add_payload({
            "conventionId": 16,
            "appliesTo": ["AGGREGATE_ROOT", "INVALID_TYPE"],
        })

        assert result is not None
        assert result["success"] is False
        assert "INVALID_TYPE" in result["error"]
        assert "available_class_types" in result["hints"]

    @patch("src.tools.feedback.get_api_client")
    def test_both_invalid_returns_combined_errors(self, mock_get_client):
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
        ]
        mock_client.get_class_types.return_value = [
            ClassTypeApiResponse(id=2, category_id=1, code="AGGREGATE_ROOT", name="Aggregate Root", order_index=1),
        ]

        result = _validate_coding_rule_add_payload({
            "conventionId": 999,
            "appliesTo": ["INVALID_TYPE"],
        })

        assert result is not None
        assert "999" in result["error"]
        assert "INVALID_TYPE" in result["error"]

    @patch("src.tools.feedback.get_api_client")
    def test_api_client_failure_returns_none(self, mock_get_client):
        """API 클라이언트 초기화 실패 → graceful pass-through"""
        mock_get_client.side_effect = Exception("Connection failed")

        result = _validate_coding_rule_add_payload({
            "conventionId": 999,
        })
        assert result is None

    @patch("src.tools.feedback.get_api_client")
    def test_convention_api_failure_skips_validation(self, mock_get_client):
        """convention API 실패 → convention 검증만 skip"""
        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.side_effect = Exception("API error")
        mock_client.get_class_types.return_value = [
            ClassTypeApiResponse(id=2, category_id=1, code="AGGREGATE_ROOT", name="Aggregate Root", order_index=1),
        ]

        result = _validate_coding_rule_add_payload({
            "conventionId": 999,
            "appliesTo": ["INVALID_TYPE"],
        })

        # convention 검증은 skip, appliesTo 검증만 실행
        assert result is not None
        assert "INVALID_TYPE" in result["error"]

    def test_has_tip_in_error_response(self):
        """에러 응답에 tip 메시지 포함"""
        with patch("src.tools.feedback.get_api_client") as mock_get_client:
            mock_client = MagicMock()
            mock_get_client.return_value = mock_client
            mock_client.get_all_conventions_with_modules.return_value = []
            mock_client.get_class_types.return_value = []

            result = _validate_coding_rule_add_payload({
                "conventionId": 999,
            })

            assert result is not None
            assert "tip" in result
            assert "get_feedback_schema" in result["tip"]


class TestSuggestConventionByCodePrefix:
    """_suggest_convention_by_code_prefix 테스트"""

    def test_domain_prefix(self):
        conventions = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
        ]
        result = _suggest_convention_by_code_prefix("DOM-AGG-001", conventions)
        assert result is not None
        assert result["id"] == 16
        assert result["layer_code"] == "DOMAIN"

    def test_api_prefix(self):
        conventions = [
            {"id": 19, "module_name": "rest-api", "layer_code": "ADAPTER_IN", "active": True},
        ]
        result = _suggest_convention_by_code_prefix("API-DTO-SEARCH-002", conventions)
        assert result is not None
        assert result["id"] == 19

    def test_no_matching_convention(self):
        conventions = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
        ]
        result = _suggest_convention_by_code_prefix("API-DTO-001", conventions)
        assert result is None  # ADAPTER_IN convention 없음

    def test_unknown_prefix_returns_none(self):
        conventions = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
        ]
        result = _suggest_convention_by_code_prefix("XYZ-001", conventions)
        assert result is None

    def test_skips_inactive_conventions(self):
        conventions = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": False},
        ]
        result = _suggest_convention_by_code_prefix("DOM-001", conventions)
        assert result is None


class TestFeedbackWithValidation:
    """feedback() 함수에서 사전 검증 통합 테스트"""

    @patch("src.tools.feedback.get_api_client")
    def test_coding_rule_add_with_invalid_convention_id(self, mock_get_client):
        from src.tools.feedback import feedback

        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.get_all_conventions_with_modules.return_value = [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN", "active": True},
        ]
        mock_client.get_class_types.return_value = []

        result = feedback(
            target_type="CODING_RULE",
            feedback_type="ADD",
            payload={"conventionId": 999, "code": "TEST-001"},
        )

        assert result["success"] is False
        assert "999" in result["error"]

    @patch("src.tools.feedback.get_api_client")
    def test_coding_rule_modify_skips_validation(self, mock_get_client):
        """MODIFY는 사전 검증 skip"""
        from src.tools.feedback import feedback

        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.create_feedback.return_value = MagicMock(feedback_queue_id=1)

        result = feedback(
            target_type="CODING_RULE",
            feedback_type="MODIFY",
            target_id=123,
            payload={"description": "updated"},
        )

        assert result["success"] is True
        # get_all_conventions_with_modules는 호출되지 않아야 함
        mock_client.get_all_conventions_with_modules.assert_not_called()

    @patch("src.tools.feedback.get_api_client")
    def test_rule_example_add_skips_validation(self, mock_get_client):
        """RULE_EXAMPLE는 CODING_RULE 검증 skip"""
        from src.tools.feedback import feedback

        mock_client = MagicMock()
        mock_get_client.return_value = mock_client
        mock_client.create_feedback.return_value = MagicMock(feedback_queue_id=2)

        result = feedback(
            target_type="RULE_EXAMPLE",
            feedback_type="ADD",
            payload={"ruleId": 123, "exampleType": "GOOD", "code": "test", "language": "JAVA"},
        )

        assert result["success"] is True
        mock_client.get_all_conventions_with_modules.assert_not_called()
