"""
get_onboarding_contexts Tool

온보딩 컨텍스트 조회 - 프로젝트 이해를 위한 컨텍스트 정보 (순수 정보 브릿지)
"""

from typing import Any, Optional

from ..api_client import get_api_client


def get_onboarding_contexts(
    tech_stack_id: int,
    architecture_id: Optional[int] = None,
    context_types: Optional[list[str]] = None,
) -> dict[str, Any]:
    """프로젝트 온보딩을 위한 컨텍스트 조회.

    사용 시점: 프로젝트 이해 및 온보딩 시 필요한 컨텍스트 정보 조회

    Args:
        tech_stack_id: 기술 스택 ID (필수)
        architecture_id: 아키텍처 ID (선택)
        context_types: 컨텍스트 타입 목록 (선택)
            - SUMMARY: 프로젝트 개요
            - ZERO_TOLERANCE: 절대 규칙 (반드시 지켜야 할 것)
            - RULES_INDEX: 규칙 인덱스 (레이어별 규칙 요약)
            - MCP_USAGE: MCP 사용법

    Returns:
        onboarding_contexts: 온보딩 컨텍스트 목록
        total_count: 총 개수

    Example:
        >>> get_onboarding_contexts(tech_stack_id=1, context_types=["SUMMARY", "ZERO_TOLERANCE"])
        {
            "onboarding_contexts": [
                {
                    "id": 1,
                    "context_type": "SUMMARY",
                    "title": "프로젝트 개요",
                    "content": "# Spring Boot 3.5.x + Java 21 프로젝트...",
                    "priority": 0
                },
                {
                    "id": 2,
                    "context_type": "ZERO_TOLERANCE",
                    "title": "절대 규칙",
                    "content": "# Zero-Tolerance Rules...",
                    "priority": 1
                }
            ],
            "total_count": 2
        }
    """
    client = get_api_client()

    # context_types 유효성 검사
    valid_context_types = {"SUMMARY", "ZERO_TOLERANCE", "RULES_INDEX", "MCP_USAGE"}
    if context_types:
        normalized_types = [t.upper() for t in context_types]
        invalid_types = [t for t in normalized_types if t not in valid_context_types]
        if invalid_types:
            return {
                "error": f"Invalid context types: {invalid_types}. Valid: {valid_context_types}"
            }
        context_types = normalized_types

    try:
        result = client.get_onboarding_for_mcp(
            tech_stack_id=tech_stack_id,
            architecture_id=architecture_id,
            context_types=context_types,
        )
        return result
    except Exception as e:
        return {"error": f"Failed to get onboarding contexts: {str(e)}"}
