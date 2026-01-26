"""
get_context Tool

컨벤션 컨텍스트 조회 (6개 기존 Tool 통합)
"""

from typing import Any, Optional

from ..services.context_service import get_context_service


def get_context(
    layer: Optional[str] = None,
    class_type: Optional[str] = None,
    tech_stack_id: Optional[int] = None,
    architecture_id: Optional[int] = None,
) -> dict[str, Any]:
    """컨벤션 컨텍스트 조회. layer: DOMAIN|APPLICATION|PERSISTENCE|REST_API, class_type: AGGREGATE|USE_CASE|ENTITY|CONTROLLER 등, tech_stack_id: 기술 스택 ID, architecture_id: 아키텍처 ID"""
    service = get_context_service()
    return service.get_context(
        layer=layer,
        class_type=class_type,
        tech_stack_id=tech_stack_id,
        architecture_id=architecture_id,
    )
