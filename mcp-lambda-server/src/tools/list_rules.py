"""
list_rules Tool

규칙 인덱스 조회 (code, name, severity, category만)
캐싱 효율성을 위한 경량 API. 상세는 get_rule(code)로 개별 조회.
"""

from typing import Any, Optional

from ..services.context_service import get_context_service


def list_rules(
    convention_id: Optional[int] = None,
    severities: Optional[list[str]] = None,
    categories: Optional[list[str]] = None,
) -> dict[str, Any]:
    """규칙 인덱스 조회 (캐싱 최적화)

    경량 인덱스만 조회하여 Serena Memory 캐싱에 적합.
    상세 정보가 필요하면 get_rule(code)로 개별 조회.

    Args:
        convention_id: 컨벤션 ID (None이면 전체)
        severities: 심각도 필터 (BLOCKER|CRITICAL|MAJOR|MINOR)
        categories: 카테고리 필터 (STRUCTURE|NAMING|DEPENDENCY 등)

    Returns:
        {
            "total_count": int,
            "rules": [{"code": "DOM-001", "name": "...", "severity": "BLOCKER", "category": "STRUCTURE"}, ...],
            "usage_hint": "상세 정보는 get_rule(code)로 개별 조회"
        }

    Example:
        # 전체 규칙 인덱스 조회
        list_rules()

        # BLOCKER 규칙만 조회
        list_rules(severities=["BLOCKER"])

        # DOMAIN 레이어의 STRUCTURE 규칙
        list_rules(categories=["STRUCTURE"])
    """
    service = get_context_service()
    return service.list_rules(
        convention_id=convention_id,
        severities=severities,
        categories=categories,
    )
