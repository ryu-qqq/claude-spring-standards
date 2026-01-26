"""
get_architecture Tool

아키텍처 상세 조회 (순수 정보 브릿지)
"""

from typing import Any, Optional

from ..api_client import get_api_client


def get_architecture(architecture_id: Optional[int] = None) -> dict[str, Any]:
    """아키텍처 상세 조회. Hexagonal Architecture 등 아키텍처 패턴 정보 반환"""
    client = get_api_client()

    if architecture_id:
        # 단건 조회
        arch = client.get_architecture_by_id(architecture_id)
        if not arch:
            return {"error": f"Architecture not found: {architecture_id}"}

        return {
            "architecture": {
                "id": arch.id,
                "tech_stack_id": arch.tech_stack_id,
                "name": arch.name,
                "pattern_type": arch.pattern_type,
                "pattern_description": arch.pattern_description,
                "pattern_principles": arch.pattern_principles,
            }
        }

    # 목록 조회
    architectures = client.get_architectures()
    return {
        "architectures": [
            {
                "id": a.id,
                "tech_stack_id": a.tech_stack_id,
                "name": a.name,
                "pattern_type": a.pattern_type,
                "pattern_description": a.pattern_description,
                "pattern_principles": a.pattern_principles,
            }
            for a in architectures
        ],
        "count": len(architectures),
    }
