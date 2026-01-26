"""
validation_context Tool

Validation Phase 컨텍스트 조회 - 코드 검증용 (순수 정보 브릿지)
"""

from typing import Any, Optional

from ..api_client import get_api_client


def validation_context(
    layers: list[str],
    tech_stack_id: int,
    architecture_id: int,
    class_types: Optional[list[str]] = None,
) -> dict[str, Any]:
    """코드 검증을 위한 컨텍스트 조회.

    사용 시점: /review, /check 단계에서 생성된 코드 검증

    Args:
        layers: 검증 대상 레이어 (DOMAIN|APPLICATION|PERSISTENCE|REST_API)
        tech_stack_id: 기술 스택 ID (필수)
        architecture_id: 아키텍처 ID (필수)
        class_types: 검증 대상 클래스 타입 (선택, AGGREGATE|USE_CASE|ENTITY 등)

    Returns:
        zero_tolerance_rules: 자동 거부 규칙 (REGEX 패턴 포함)
        checklist: 체크리스트 항목
    """
    client = get_api_client()

    # 레이어 유효성 검사
    valid_layers = {"DOMAIN", "APPLICATION", "PERSISTENCE", "REST_API"}
    normalized_layers = [layer.upper() for layer in layers]

    invalid_layers = [layer for layer in normalized_layers if layer not in valid_layers]
    if invalid_layers:
        return {
            "error": f"Invalid layers: {invalid_layers}. Valid: {valid_layers}"
        }

    if not normalized_layers:
        return {"error": "At least one layer must be specified"}

    # classTypes 유효성 검사 (선택 파라미터)
    valid_class_types = {
        "AGGREGATE", "VALUE_OBJECT", "DOMAIN_EVENT", "DOMAIN_EXCEPTION",
        "USE_CASE", "COMMAND_SERVICE", "QUERY_SERVICE", "PORT_IN", "PORT_OUT",
        "ENTITY", "JPA_REPOSITORY", "ADAPTER", "MAPPER",
        "CONTROLLER", "REQUEST_DTO", "RESPONSE_DTO",
        "ASSEMBLER", "FACTORY", "QUERY_DSL_REPOSITORY",
    }

    normalized_class_types = None
    if class_types:
        normalized_class_types = [ct.upper() for ct in class_types]
        invalid_types = [ct for ct in normalized_class_types if ct not in valid_class_types]
        if invalid_types:
            return {
                "error": f"Invalid class_types: {invalid_types}. Valid: {sorted(valid_class_types)}"
            }

    try:
        result = client.get_validation_context(
            layers=normalized_layers,
            tech_stack_id=tech_stack_id,
            architecture_id=architecture_id,
            class_types=normalized_class_types,
        )
        return result
    except Exception as e:
        return {"error": f"Failed to get validation context: {str(e)}"}
