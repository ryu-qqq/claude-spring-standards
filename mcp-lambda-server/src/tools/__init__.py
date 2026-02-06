"""
Spring Standards MCP Tools (v2.2)

순수 정보 브릿지 도구들 - MCP는 정보만 전달, 판단은 LLM이 직접 수행

도구 목록 (14개):
- 워크플로우 API: planning_context, module_context, validation_context
- Convention Hub: get_config_files, get_onboarding_contexts
- 컨텍스트 조회: get_context, get_rule, list_rules
- 계층 정보: list_tech_stacks, get_architecture, get_layer_detail
- 피드백: feedback, approve, suggest_convention
"""

from .approve import approve
from .feedback import feedback
from .get_architecture import get_architecture
from .get_config_files import get_config_files
from .get_context import get_context
from .get_layer_detail import get_layer_detail
from .get_onboarding_contexts import get_onboarding_contexts
from .get_rule import get_rule
from .list_rules import list_rules
from .list_tech_stacks import list_tech_stacks
from .module_context import module_context
from .planning_context import planning_context
from .suggest import suggest_convention
from .validation_context import validation_context

__all__ = [
    # 워크플로우 API (v2.0 - Module-Centric)
    "planning_context",
    "module_context",
    "validation_context",
    # Convention Hub (Phase 2)
    "get_config_files",
    "get_onboarding_contexts",
    # 컨텍스트 조회
    "get_context",
    "get_rule",
    "list_rules",
    # 계층 정보 조회
    "list_tech_stacks",
    "get_architecture",
    "get_layer_detail",
    # 피드백
    "feedback",
    "approve",
    "suggest_convention",
]
