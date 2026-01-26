"""
planning_context Tool

Planning Phase 컨텍스트 조회 - 개발 계획 수립용 (순수 정보 브릿지)
"""

from typing import Any, Optional

from ..api_client import get_api_client


def planning_context(
    layers: list[str],
    tech_stack_id: Optional[int] = None,
) -> dict[str, Any]:
    """개발 계획 수립을 위한 컨텍스트 조회.

    사용 시점: /epic, /plan 단계에서 "어떤 컴포넌트를 어디에 만들지" 결정

    Args:
        layers: 조회할 레이어 목록 (DOMAIN|APPLICATION|PERSISTENCE|REST_API)
        tech_stack_id: 기술 스택 ID (선택, 기본값: 활성 스택)

    Returns:
        기술스택, 아키텍처, 레이어별 모듈 및 패키지 구조 요약
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

    try:
        result = client.get_planning_context(
            layers=normalized_layers,
            tech_stack_id=tech_stack_id,
        )
        return result
    except Exception as e:
        return {"error": f"Failed to get planning context: {str(e)}"}
