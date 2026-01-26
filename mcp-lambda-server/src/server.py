"""
Spring Standards MCP Server (v2.1)

순수 정보 브릿지 서버 - MCP는 정보만 전달, 판단은 LLM이 직접 수행

도구 목록 (13개):
- 워크플로우 API (v2.0): planning_context, module_context, validation_context ⭐ Module-Centric
- Convention Hub: get_config_files, get_onboarding_contexts ⭐ Phase 2
- 컨텍스트 조회: get_context, get_rule, list_rules ⭐ Index + Lookup
- 계층 정보: list_tech_stacks, get_architecture, get_layer_detail
- 피드백 시스템: feedback, approve
"""

from typing import Any

from fastmcp import FastMCP

from .config import get_server_config
from .tools import (
    approve,
    feedback,
    get_architecture,
    get_config_files,
    get_context,
    get_layer_detail,
    get_onboarding_contexts,
    get_rule,
    list_rules,
    list_tech_stacks,
    module_context,
    planning_context,
    validation_context,
)

# ============================================
# Server Initialization
# ============================================

config = get_server_config()
mcp = FastMCP(
    name=config.name,
    version="2.1.0",
    instructions="Convention Hub - 팀 코딩 컨벤션 조회 서버 (13 Tools, Module-Centric Workflow)",
)


# ============================================
# Tool Definitions (13개 - 순수 정보 브릿지)
# ============================================


# --- 워크플로우 API (v2.0 - Module-Centric) ---


@mcp.tool()
def tool_planning_context(
    layers: list[str], tech_stack_id: int = None
) -> dict[str, Any]:
    """개발 계획 수립을 위한 컨텍스트 조회.
    사용 시점: /epic, /plan 단계에서 "어떤 컴포넌트를 어디에 만들지" 결정
    layers: 조회할 레이어 목록 (DOMAIN|APPLICATION|PERSISTENCE|REST_API)
    tech_stack_id: 기술 스택 ID (선택, 기본값: 활성 스택)
    Returns: 기술스택, 아키텍처, 레이어별 모듈 및 패키지 구조 요약"""
    return planning_context(layers=layers, tech_stack_id=tech_stack_id)


@mcp.tool()
def tool_module_context(
    module_id: int, class_type_id: int = None
) -> dict[str, Any]:
    """코드 생성을 위한 Module 전체 컨텍스트 조회.
    사용 시점: /implement 단계에서 실제 코드 작성
    module_id: 모듈 ID (필수)
    class_type_id: 클래스 타입 ID로 필터링 (list_tech_stacks()로 class_types 조회)
    Returns: execution_context (PackageStructure, Template, ArchUnitTest), rule_context (Convention, CodingRule, RuleExample)"""
    return module_context(module_id=module_id, class_type_id=class_type_id)


@mcp.tool()
def tool_validation_context(
    layers: list[str],
    tech_stack_id: int,
    architecture_id: int,
    class_types: list[str] = None,
) -> dict[str, Any]:
    """코드 검증을 위한 컨텍스트 조회.
    사용 시점: /review, /check 단계에서 생성된 코드 검증
    layers: 검증 대상 레이어 (DOMAIN|APPLICATION|PERSISTENCE|REST_API)
    tech_stack_id: 기술 스택 ID (필수)
    architecture_id: 아키텍처 ID (필수)
    class_types: 검증 대상 클래스 타입 (선택, AGGREGATE|USE_CASE|ENTITY 등)
    Returns: zero_tolerance_rules (자동 거부 규칙, REGEX 패턴 포함), checklist (체크리스트 항목)"""
    return validation_context(
        layers=layers,
        tech_stack_id=tech_stack_id,
        architecture_id=architecture_id,
        class_types=class_types,
    )


# --- Convention Hub (Phase 2) ---


@mcp.tool()
def tool_get_config_files(
    tech_stack_id: int,
    architecture_id: int = None,
    tool_types: list[str] = None,
) -> dict[str, Any]:
    """프로젝트 초기화를 위한 설정 파일 템플릿 조회 (init_project Tool).
    사용 시점: 프로젝트 초기 설정 시 필요한 설정 파일 생성
    tech_stack_id: 기술 스택 ID (필수)
    architecture_id: 아키텍처 ID (선택)
    tool_types: 도구 타입 목록 (선택, CLAUDE|CURSOR|COPILOT|WINDSURF|CODEIUM)
    Returns: config_files (설정 파일 템플릿 목록), total_count"""
    return get_config_files(
        tech_stack_id=tech_stack_id,
        architecture_id=architecture_id,
        tool_types=tool_types,
    )


@mcp.tool()
def tool_get_onboarding_contexts(
    tech_stack_id: int,
    architecture_id: int = None,
    context_types: list[str] = None,
) -> dict[str, Any]:
    """프로젝트 온보딩을 위한 컨텍스트 조회 (get_onboarding_context Tool).
    사용 시점: 프로젝트 이해 및 온보딩 시 필요한 컨텍스트 정보 조회
    tech_stack_id: 기술 스택 ID (필수)
    architecture_id: 아키텍처 ID (선택)
    context_types: 컨텍스트 타입 목록 (선택, SUMMARY|ZERO_TOLERANCE|RULES_INDEX|MCP_USAGE)
    Returns: onboarding_contexts (컨텍스트 목록), total_count"""
    return get_onboarding_contexts(
        tech_stack_id=tech_stack_id,
        architecture_id=architecture_id,
        context_types=context_types,
    )


# --- 컨텍스트 조회 ---


@mcp.tool()
def tool_get_context(
    layer: str = None,
    class_type: str = None,
    tech_stack_id: int = None,
    architecture_id: int = None,
) -> dict[str, Any]:
    """컨벤션 컨텍스트 조회. layer: DOMAIN|APPLICATION|PERSISTENCE|REST_API, class_type: AGGREGATE|USE_CASE|ENTITY|CONTROLLER 등, tech_stack_id: 기술 스택 ID, architecture_id: 아키텍처 ID"""
    return get_context(
        layer=layer,
        class_type=class_type,
        tech_stack_id=tech_stack_id,
        architecture_id=architecture_id,
    )


@mcp.tool()
def tool_get_rule(code: str) -> dict[str, Any]:
    """규칙 상세 + 예시 조회. code: DOM-001, APP-002 등"""
    return get_rule(code=code)


@mcp.tool()
def tool_list_rules(
    convention_id: int = None,
    severities: list[str] = None,
    categories: list[str] = None,
) -> dict[str, Any]:
    """규칙 인덱스 조회 (캐싱 최적화). code, name, severity, category만 반환.
    상세 정보는 get_rule(code)로 개별 조회.
    convention_id: 컨벤션 ID (선택, None이면 전체)
    severities: 심각도 필터 (BLOCKER|CRITICAL|MAJOR|MINOR)
    categories: 카테고리 필터 (STRUCTURE|NAMING|DEPENDENCY 등)"""
    return list_rules(
        convention_id=convention_id,
        severities=severities,
        categories=categories,
    )


# --- 계층 정보 조회 ---


@mcp.tool()
def tool_list_tech_stacks() -> dict[str, Any]:
    """기술 스택 목록 조회. Spring Boot, Java 21 등 프로젝트 기술 스택 정보 반환"""
    return list_tech_stacks()


@mcp.tool()
def tool_get_architecture(architecture_id: int = None) -> dict[str, Any]:
    """아키텍처 상세 조회. Hexagonal Architecture 등 아키텍처 패턴 정보 반환. ID 없으면 전체 목록"""
    return get_architecture(architecture_id=architecture_id)


@mcp.tool()
def tool_get_layer_detail(
    layer_id: int = None, layer_code: str = None
) -> dict[str, Any]:
    """레이어 상세 조회. DOMAIN|APPLICATION|PERSISTENCE|REST_API 등 레이어 정보 반환. 파라미터 없으면 전체 목록"""
    return get_layer_detail(layer_id=layer_id, layer_code=layer_code)


# --- 피드백 시스템 (FeedbackQueue 기반) ---


@mcp.tool()
def tool_get_feedback_schema(target_type: str) -> dict[str, Any]:
    """피드백 payload 스키마 조회. feedback() 호출 전 스키마 확인용.
    target_type: RULE_EXAMPLE|CODING_RULE|CHECKLIST_ITEM|CLASS_TEMPLATE"""
    schemas = {
        "RULE_EXAMPLE": {
            "description": "규칙 예시 추가/수정",
            "add_schema": {
                "ruleId": {"type": "Long", "required": True, "desc": "코딩 규칙 ID"},
                "exampleType": {"type": "String", "required": True, "desc": "GOOD|BAD"},
                "code": {"type": "String", "required": True, "desc": "예시 코드"},
                "language": {"type": "String", "required": True, "desc": "JAVA|KOTLIN|SQL 등"},
                "explanation": {"type": "String", "required": False, "desc": "설명"},
                "highlightLines": {"type": "List[int]", "required": False, "desc": "강조 라인 번호"},
            },
            "example": {
                "ruleId": 123,
                "exampleType": "GOOD",
                "code": "public class Example {}",
                "language": "JAVA",
                "explanation": "좋은 예시"
            }
        },
        "CODING_RULE": {
            "description": "코딩 규칙 추가/수정",
            "add_schema": {
                "conventionId": {"type": "Long", "required": True, "desc": "컨벤션 ID"},
                "code": {"type": "String", "required": True, "desc": "규칙 코드 (예: DOM-001)"},
                "name": {"type": "String", "required": True, "desc": "규칙 이름"},
                "severity": {"type": "String", "required": True, "desc": "BLOCKER|CRITICAL|MAJOR|MINOR|INFO"},
                "category": {"type": "String", "required": True, "desc": "ANNOTATION|BEHAVIOR|STRUCTURE|NAMING|DEPENDENCY"},
                "description": {"type": "String", "required": True, "desc": "상세 설명"},
                "rationale": {"type": "String", "required": True, "desc": "근거"},
                "autoFixable": {"type": "boolean", "required": True, "desc": "자동 수정 가능 여부"},
                "appliesTo": {"type": "List[String]", "required": True, "desc": "CLASS|METHOD|FIELD 등"},
                "structureId": {"type": "Long", "required": False, "desc": "특정 패키지 구조 ID"},
            }
        },
        "CHECKLIST_ITEM": {
            "description": "체크리스트 항목 추가/수정",
            "add_schema": {
                "ruleId": {"type": "Long", "required": True, "desc": "코딩 규칙 ID"},
                "sequenceOrder": {"type": "int", "required": True, "desc": "순서"},
                "checkDescription": {"type": "String", "required": True, "desc": "체크 설명"},
                "checkType": {"type": "String", "required": True, "desc": "AUTOMATED|MANUAL|SEMI_AUTO"},
                "critical": {"type": "boolean", "required": True, "desc": "필수 항목 여부"},
                "automationTool": {"type": "String", "required": False, "desc": "자동화 도구"},
                "automationRuleId": {"type": "String", "required": False, "desc": "자동화 규칙 ID"},
            }
        },
        "CLASS_TEMPLATE": {
            "description": "클래스 템플릿 추가/수정",
            "add_schema": {
                "structureId": {"type": "Long", "required": True, "desc": "패키지 구조 ID"},
                "classType": {"type": "String", "required": True, "desc": "AGGREGATE|VALUE_OBJECT|ENTITY 등"},
                "templateCode": {"type": "String", "required": True, "desc": "템플릿 코드"},
                "description": {"type": "String", "required": True, "desc": "설명"},
                "namingPattern": {"type": "String", "required": False, "desc": "네이밍 패턴"},
                "requiredAnnotations": {"type": "List[String]", "required": False},
                "forbiddenAnnotations": {"type": "List[String]", "required": False},
                "requiredInterfaces": {"type": "List[String]", "required": False},
                "forbiddenInheritance": {"type": "List[String]", "required": False},
                "requiredMethods": {"type": "List[String]", "required": False},
            }
        }
    }

    if target_type not in schemas:
        return {"error": f"Unknown target_type: {target_type}. Valid: {list(schemas.keys())}"}

    return schemas[target_type]


@mcp.tool()
def tool_feedback(
    target_type: str,
    feedback_type: str,
    payload: dict,
    target_id: int = None,
) -> dict[str, Any]:
    """피드백 제출 → FeedbackQueue에 PENDING 상태로 저장.
    target_type: RULE_EXAMPLE|CODING_RULE|CHECKLIST_ITEM|CLASS_TEMPLATE
    feedback_type: ADD|MODIFY|DELETE
    payload: 대상별 JSON (스키마는 get_feedback_schema로 조회)
    target_id: MODIFY/DELETE 시 대상 ID (ADD 시 생략)"""
    return feedback(
        target_type=target_type,
        feedback_type=feedback_type,
        payload=payload,
        target_id=target_id,
    )


@mcp.tool()
def tool_approve(
    feedback_id: int,
    action: str,
    reviewer: str = "llm",
    review_notes: str = None,
    auto_merge: bool = True,
) -> dict[str, Any]:
    """피드백 승인/거절.
    action: approve|reject
    reviewer: llm|human
    Safe 레벨 승인 시 자동 머지. Medium 레벨은 LLM→Human 2단계 승인 필요."""
    return approve(
        feedback_id=feedback_id,
        action=action,
        reviewer=reviewer,
        review_notes=review_notes,
        auto_merge=auto_merge,
    )


# ============================================
# Entry Point
# ============================================


def main() -> None:
    """MCP 서버 실행"""
    mcp.run()


if __name__ == "__main__":
    main()
