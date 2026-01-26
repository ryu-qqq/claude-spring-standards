"""
get_config_files Tool (init_project)

설정 파일 템플릿 조회 - 프로젝트 초기화 시 필요한 설정 파일 목록 (순수 정보 브릿지)
"""

from typing import Any, Optional

from ..api_client import get_api_client


def get_config_files(
    tech_stack_id: int,
    architecture_id: Optional[int] = None,
    tool_types: Optional[list[str]] = None,
) -> dict[str, Any]:
    """프로젝트 초기화를 위한 설정 파일 템플릿 조회.

    사용 시점: 프로젝트 초기 설정 시 필요한 설정 파일 생성 (init_project Tool)

    Args:
        tech_stack_id: 기술 스택 ID (필수)
        architecture_id: 아키텍처 ID (선택)
        tool_types: 도구 타입 목록 (선택, 예: CLAUDE, CURSOR, COPILOT)

    Returns:
        config_files: 설정 파일 템플릿 목록
        total_count: 총 개수

    Example:
        >>> get_config_files(tech_stack_id=1, tool_types=["CLAUDE"])
        {
            "config_files": [
                {
                    "id": 1,
                    "tool_type": "CLAUDE",
                    "file_path": ".claude/CLAUDE.md",
                    "file_name": "CLAUDE.md",
                    "content": "# Project Configuration...",
                    "category": "MAIN_CONFIG",
                    "description": "Claude Code 메인 설정 파일",
                    "is_required": true
                }
            ],
            "total_count": 1
        }
    """
    client = get_api_client()

    # tool_types 유효성 검사
    valid_tool_types = {"CLAUDE", "CURSOR", "COPILOT", "WINDSURF", "CODEIUM"}
    if tool_types:
        normalized_types = [t.upper() for t in tool_types]
        invalid_types = [t for t in normalized_types if t not in valid_tool_types]
        if invalid_types:
            return {
                "error": f"Invalid tool types: {invalid_types}. Valid: {valid_tool_types}"
            }
        tool_types = normalized_types

    try:
        result = client.get_config_files_for_mcp(
            tech_stack_id=tech_stack_id,
            architecture_id=architecture_id,
            tool_types=tool_types,
        )
        return result
    except Exception as e:
        return {"error": f"Failed to get config files: {str(e)}"}
