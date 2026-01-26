"""
get_rule Tool

규칙 상세 + 예시 조회 (3개 기존 Tool 통합)
"""

from typing import Any

from ..services.context_service import get_context_service


def get_rule(code: str) -> dict[str, Any]:
    """규칙 상세 + 예시 조회. code: DOM-001, APP-002 등"""
    service = get_context_service()
    result = service.get_rule_detail(code)

    if result is None:
        return {
            "error": f"Rule not found: {code}",
            "suggestion": "Use search() to find available rules",
        }

    return result
