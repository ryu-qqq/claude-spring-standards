"""
ClassTypeApiResponse 모델 테스트
"""

import pytest

from src.models import ClassTypeApiResponse


class TestClassTypeApiResponse:
    """ClassTypeApiResponse 파싱 테스트"""

    def test_parse_with_alias(self):
        """camelCase alias로 파싱"""
        data = {
            "id": 2,
            "categoryId": 1,
            "code": "AGGREGATE_ROOT",
            "name": "Aggregate Root",
            "description": "DDD Aggregate Root",
            "orderIndex": 1,
        }
        result = ClassTypeApiResponse(**data)
        assert result.id == 2
        assert result.category_id == 1
        assert result.code == "AGGREGATE_ROOT"
        assert result.name == "Aggregate Root"
        assert result.description == "DDD Aggregate Root"
        assert result.order_index == 1

    def test_parse_with_snake_case(self):
        """snake_case로 파싱 (populate_by_name=True)"""
        data = {
            "id": 3,
            "category_id": 2,
            "code": "REQUEST_DTO",
            "name": "Request DTO",
            "order_index": 5,
        }
        result = ClassTypeApiResponse(**data)
        assert result.id == 3
        assert result.category_id == 2
        assert result.code == "REQUEST_DTO"

    def test_parse_optional_description_none(self):
        """description이 None인 경우"""
        data = {
            "id": 4,
            "categoryId": 1,
            "code": "VALUE_OBJECT",
            "name": "Value Object",
            "orderIndex": 2,
        }
        result = ClassTypeApiResponse(**data)
        assert result.description is None

    def test_parse_multiple_class_types(self):
        """여러 ClassType 파싱"""
        items = [
            {"id": 2, "categoryId": 1, "code": "AGGREGATE_ROOT", "name": "Aggregate Root", "orderIndex": 1},
            {"id": 3, "categoryId": 1, "code": "VALUE_OBJECT", "name": "Value Object", "orderIndex": 2},
            {"id": 5, "categoryId": 2, "code": "REQUEST_DTO", "name": "Request DTO", "orderIndex": 5},
        ]
        results = [ClassTypeApiResponse(**item) for item in items]
        assert len(results) == 3
        assert results[0].code == "AGGREGATE_ROOT"
        assert results[1].code == "VALUE_OBJECT"
        assert results[2].code == "REQUEST_DTO"
