"""
list_tech_stacks Tool

기술 스택 목록 조회 (순수 정보 브릿지)
"""

from typing import Any

from ..api_client import get_api_client


def list_tech_stacks() -> dict[str, Any]:
    """기술 스택 목록 조회. Spring Boot, Java 21 등 프로젝트 기술 스택 정보 반환"""
    client = get_api_client()
    tech_stacks = client.get_tech_stacks()

    return {
        "tech_stacks": [
            {
                "id": ts.id,
                "name": ts.name,
                "status": ts.status,
                "language_type": ts.language_type,
                "language_version": ts.language_version,
                "language_features": ts.language_features,
                "framework_type": ts.framework_type,
                "framework_version": ts.framework_version,
                "framework_modules": ts.framework_modules,
                "platform_type": ts.platform_type,
                "runtime_environment": ts.runtime_environment,
                "build_tool_type": ts.build_tool_type,
                "build_config_file": ts.build_config_file,
            }
            for ts in tech_stacks
        ],
        "count": len(tech_stacks),
    }
