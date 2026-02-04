"""
module_context Tool

Execution Phase 컨텍스트 조회 - 코드 생성용 (순수 정보 브릿지)
"""

from typing import Any

from ..api_client import get_api_client


def module_context(
    module_id: int,
    class_type_id: int,
) -> dict[str, Any]:
    """코드 생성을 위한 Module 전체 컨텍스트 조회.

    사용 시점: /implement 단계에서 실제 코드 작성

    Args:
        module_id: 모듈 ID (필수)
        class_type_id: 클래스 타입 ID (필수, list_tech_stacks()로 조회)

    Returns:
        execution_context: PackageStructure, Template, ArchUnitTest
        rule_context: Convention, CodingRule, RuleExample
    """
    client = get_api_client()

    if not module_id or module_id <= 0:
        return {"error": "Valid module_id is required"}

    if class_type_id <= 0:
        return {"error": "class_type_id must be a positive integer"}

    try:
        result = client.get_module_context(
            module_id=module_id,
            class_type_id=class_type_id,
        )
        return result
    except Exception as e:
        return {"error": f"Failed to get module context: {str(e)}"}
