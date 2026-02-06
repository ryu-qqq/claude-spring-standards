"""
Minimal Context Collector

의도와 레이어에 따라 필요한 최소한의 컨텍스트만 수집하는 모듈
토큰 사용량을 최적화하면서 필요한 규칙과 템플릿을 제공
"""

import logging
from typing import Optional

import httpx
from pydantic import BaseModel, Field

from ..api_client import get_api_client
from ..models import IntentType, Layer, ClassType

logger = logging.getLogger(__name__)


class MinimalContext(BaseModel):
    """최소 컨텍스트 결과 모델"""

    # 기본 정보
    intent_type: str = Field(description="의도 타입")
    target_layer: Optional[str] = Field(None, description="대상 레이어")
    class_type: Optional[str] = Field(None, description="클래스 타입")

    # 수집된 컨텍스트
    zero_tolerance_rules: list[dict] = Field(
        default_factory=list, description="Zero-Tolerance 규칙 목록"
    )
    layer_rules: list[dict] = Field(
        default_factory=list, description="레이어별 코딩 규칙 목록"
    )
    class_template: Optional[dict] = Field(
        None, description="클래스 템플릿 (있는 경우)"
    )
    layer_dependencies: list[dict] = Field(
        default_factory=list, description="레이어 의존성 규칙"
    )

    # 메타 정보
    token_estimate: int = Field(0, description="예상 토큰 사용량")
    context_summary: str = Field(description="컨텍스트 요약 설명")


# 의도별 필요 컨텍스트 매핑
INTENT_CONTEXT_REQUIREMENTS: dict[str, dict] = {
    # Domain Layer Intents
    IntentType.CREATE_AGGREGATE.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.DOMAIN.value,
        "class_type": ClassType.AGGREGATE.value,
        "rule_categories": ["STRUCTURE", "BEHAVIOR", "NAMING"],
    },
    IntentType.CREATE_VALUE_OBJECT.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.DOMAIN.value,
        "class_type": ClassType.VALUE_OBJECT.value,
        "rule_categories": ["STRUCTURE", "NAMING"],
    },
    IntentType.CREATE_DOMAIN_EVENT.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.DOMAIN.value,
        "class_type": ClassType.DOMAIN_EVENT.value,
        "rule_categories": ["STRUCTURE", "NAMING"],
    },
    IntentType.CREATE_DOMAIN_EXCEPTION.value: {
        "need_zero_tolerance": False,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.DOMAIN.value,
        "class_type": ClassType.DOMAIN_EXCEPTION.value,
        "rule_categories": ["STRUCTURE", "NAMING"],
    },
    # Application Layer Intents
    IntentType.CREATE_USE_CASE.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.APPLICATION.value,
        "class_type": ClassType.USE_CASE.value,
        "rule_categories": ["STRUCTURE", "DEPENDENCY", "ANNOTATION"],
    },
    IntentType.CREATE_COMMAND_SERVICE.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.APPLICATION.value,
        "class_type": ClassType.COMMAND_SERVICE.value,
        "rule_categories": ["STRUCTURE", "DEPENDENCY", "ANNOTATION"],
    },
    IntentType.CREATE_QUERY_SERVICE.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.APPLICATION.value,
        "class_type": ClassType.QUERY_SERVICE.value,
        "rule_categories": ["STRUCTURE", "DEPENDENCY", "ANNOTATION"],
    },
    IntentType.CREATE_PORT.value: {
        "need_zero_tolerance": False,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.APPLICATION.value,
        "class_type": ClassType.PORT_IN.value,
        "rule_categories": ["STRUCTURE", "NAMING"],
    },
    # Persistence Layer Intents
    IntentType.CREATE_ENTITY.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.ADAPTER_OUT.value,
        "class_type": ClassType.ENTITY.value,
        "rule_categories": ["STRUCTURE", "ANNOTATION", "NAMING"],
    },
    IntentType.CREATE_REPOSITORY.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.ADAPTER_OUT.value,
        "class_type": ClassType.JPA_REPOSITORY.value,
        "rule_categories": ["STRUCTURE", "DEPENDENCY"],
    },
    IntentType.CREATE_PERSISTENCE_ADAPTER.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.ADAPTER_OUT.value,
        "class_type": ClassType.ADAPTER.value,
        "rule_categories": ["STRUCTURE", "DEPENDENCY", "ANNOTATION"],
    },
    # REST API Layer Intents
    IntentType.CREATE_CONTROLLER.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.ADAPTER_IN.value,
        "class_type": ClassType.CONTROLLER.value,
        "rule_categories": ["STRUCTURE", "ANNOTATION", "SECURITY"],
    },
    IntentType.CREATE_REQUEST_DTO.value: {
        "need_zero_tolerance": False,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.ADAPTER_IN.value,
        "class_type": ClassType.REQUEST_DTO.value,
        "rule_categories": ["STRUCTURE", "NAMING"],
    },
    IntentType.CREATE_RESPONSE_DTO.value: {
        "need_zero_tolerance": False,
        "need_layer_rules": True,
        "need_template": True,
        "target_layer": Layer.ADAPTER_IN.value,
        "class_type": ClassType.RESPONSE_DTO.value,
        "rule_categories": ["STRUCTURE", "NAMING"],
    },
    # Modification Intents
    IntentType.ADD_METHOD.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": False,
        "target_layer": None,  # 동적으로 결정
        "class_type": None,
        "rule_categories": ["BEHAVIOR", "NAMING"],
    },
    IntentType.MODIFY_LOGIC.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": False,
        "target_layer": None,
        "class_type": None,
        "rule_categories": ["BEHAVIOR"],
    },
    IntentType.REFACTOR_CODE.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": False,
        "target_layer": None,
        "class_type": None,
        "rule_categories": ["STRUCTURE", "BEHAVIOR", "DEPENDENCY"],
    },
    # Analysis Intents
    IntentType.ANALYZE_CODE.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": False,
        "target_layer": None,
        "class_type": None,
        "rule_categories": None,  # 모든 카테고리
    },
    IntentType.EXPLAIN_CODE.value: {
        "need_zero_tolerance": False,
        "need_layer_rules": False,
        "need_template": False,
        "target_layer": None,
        "class_type": None,
        "rule_categories": None,
    },
    IntentType.UNKNOWN.value: {
        "need_zero_tolerance": True,
        "need_layer_rules": True,
        "need_template": False,
        "target_layer": None,
        "class_type": None,
        "rule_categories": None,
    },
}


def _estimate_tokens(context: MinimalContext) -> int:
    """컨텍스트의 토큰 사용량 추정"""
    # 대략적인 토큰 추정 (1 토큰 ≈ 4 characters)
    token_count = 0

    # Zero-Tolerance 규칙
    for rule in context.zero_tolerance_rules:
        token_count += len(str(rule)) // 4

    # 레이어 규칙
    for rule in context.layer_rules:
        token_count += len(str(rule)) // 4

    # 클래스 템플릿
    if context.class_template:
        token_count += len(str(context.class_template)) // 4

    # 의존성 규칙
    for dep in context.layer_dependencies:
        token_count += len(str(dep)) // 4

    return token_count


def _generate_context_summary(context: MinimalContext) -> str:
    """컨텍스트 요약 생성"""
    parts = []

    if context.target_layer:
        parts.append(f"레이어: {context.target_layer}")

    if context.class_type:
        parts.append(f"클래스 타입: {context.class_type}")

    parts.append(f"Zero-Tolerance 규칙: {len(context.zero_tolerance_rules)}개")
    parts.append(f"레이어 규칙: {len(context.layer_rules)}개")

    if context.class_template:
        parts.append("템플릿: 포함됨")

    parts.append(f"예상 토큰: ~{context.token_estimate}")

    return " | ".join(parts)


def _simplify_rule(rule) -> dict:
    """규칙을 간소화된 딕셔너리로 변환"""
    return {
        "code": rule.code,
        "name": rule.name,
        "severity": rule.severity,
        "category": rule.category,
        "description": rule.description,
        "rationale": rule.rationale,
        "zero_tolerance": rule.zero_tolerance,
    }


def _simplify_template(template) -> dict:
    """템플릿을 간소화된 딕셔너리로 변환"""
    return {
        "class_type": template.class_type,
        "template_code": template.template_code,
        "naming_pattern": template.naming_pattern,
        "required_annotations": template.required_annotations,
        "forbidden_annotations": template.forbidden_annotations,
        "description": template.description,
    }


def _simplify_dependency(dep) -> dict:
    """의존성을 간소화된 딕셔너리로 변환"""
    return {
        "source_layer": dep.source_layer,
        "target_layer": dep.target_layer,
        "allowed": dep.allowed,
        "direction": dep.direction,
        "rationale": dep.rationale,
    }


def get_minimal_context(
    intent_type: str,
    target_layer: Optional[str] = None,
    class_type: Optional[str] = None,
    include_dependencies: bool = True,
) -> MinimalContext:
    """
    의도와 레이어에 따라 필요한 최소한의 컨텍스트를 수집

    Args:
        intent_type: 의도 타입 (IntentType 값)
        target_layer: 대상 레이어 (옵션, 없으면 의도에서 추론)
        class_type: 클래스 타입 (옵션, 없으면 의도에서 추론)
        include_dependencies: 레이어 의존성 규칙 포함 여부

    Returns:
        MinimalContext: 수집된 최소 컨텍스트

    Example:
        >>> ctx = get_minimal_context("CREATE_AGGREGATE")
        >>> ctx.target_layer
        'DOMAIN'
        >>> len(ctx.zero_tolerance_rules) > 0
        True
    """
    # 의도별 요구사항 조회
    requirements = INTENT_CONTEXT_REQUIREMENTS.get(
        intent_type, INTENT_CONTEXT_REQUIREMENTS[IntentType.UNKNOWN.value]
    )

    # 레이어와 클래스 타입 결정 (명시적 값 > 요구사항 기본값)
    final_layer = target_layer or requirements.get("target_layer")
    final_class_type = class_type or requirements.get("class_type")

    # 결과 초기화
    context = MinimalContext(
        intent_type=intent_type,
        target_layer=final_layer,
        class_type=final_class_type,
        context_summary="",
    )

    # API 클라이언트
    client = get_api_client()

    # 1. Zero-Tolerance 규칙 수집 (실패 시 에러 전파 - 가드레일 핵심 기능)
    if requirements.get("need_zero_tolerance"):
        try:
            zt_rules = client.get_zero_tolerance_rules(layer=final_layer)
            context.zero_tolerance_rules = [_simplify_rule(r) for r in zt_rules]
        except httpx.HTTPStatusError as e:
            logger.error(
                "Zero-Tolerance 규칙 API 응답 오류 (HTTP %s): %s",
                e.response.status_code,
                e,
            )
            raise RuntimeError(
                f"Zero-Tolerance 규칙 API 응답 오류 (HTTP {e.response.status_code}). "
                "가드레일이 작동하지 않을 수 있어 코드 생성을 중단합니다."
            ) from e
        except httpx.RequestError as e:
            logger.error("Zero-Tolerance 규칙 수집 네트워크 오류: %s", e)
            raise RuntimeError(
                f"Zero-Tolerance 규칙 수집 네트워크 오류: {e}. "
                "가드레일이 작동하지 않을 수 있어 코드 생성을 중단합니다."
            ) from e
        except (KeyError, AttributeError, TypeError) as e:
            logger.error("Zero-Tolerance 규칙 응답 데이터 처리 오류: %s", e)
            raise RuntimeError(
                f"Zero-Tolerance 규칙 응답 데이터 처리 오류: {e}. "
                "가드레일이 작동하지 않을 수 있어 코드 생성을 중단합니다."
            ) from e

    # 2. 레이어별 코딩 규칙 수집
    if requirements.get("need_layer_rules") and final_layer:
        try:
            layer_rules = client.get_coding_rules_by_layer(final_layer)

            # 카테고리 필터링 (필요한 경우)
            rule_categories = requirements.get("rule_categories")
            if rule_categories:
                layer_rules = [r for r in layer_rules if r.category in rule_categories]

            # Zero-Tolerance와 중복 제거
            zt_codes = {r["code"] for r in context.zero_tolerance_rules}
            layer_rules = [r for r in layer_rules if r.code not in zt_codes]

            context.layer_rules = [_simplify_rule(r) for r in layer_rules]
        except httpx.HTTPStatusError as e:
            logger.warning(
                "레이어 코딩 규칙 API 응답 오류 (layer=%s, HTTP %s): %s",
                final_layer,
                e.response.status_code,
                e,
            )
            context.layer_rules = []
        except httpx.RequestError as e:
            logger.warning(
                "레이어 코딩 규칙 수집 네트워크 오류 (layer=%s): %s",
                final_layer,
                e,
            )
            context.layer_rules = []
        except (KeyError, AttributeError, TypeError) as e:
            logger.warning(
                "레이어 코딩 규칙 응답 데이터 처리 오류 (layer=%s): %s",
                final_layer,
                e,
            )
            context.layer_rules = []

    # 3. 클래스 템플릿 수집 (실패해도 진행 - 필수 아님)
    if requirements.get("need_template") and final_class_type:
        try:
            template = client.get_class_template_by_type(final_class_type)
            if template:
                context.class_template = _simplify_template(template)
        except httpx.HTTPStatusError as e:
            logger.warning(
                "클래스 템플릿 API 응답 오류 (class_type=%s, HTTP %s): %s",
                final_class_type,
                e.response.status_code,
                e,
            )
            context.class_template = None
        except httpx.RequestError as e:
            logger.warning(
                "클래스 템플릿 수집 네트워크 오류 (class_type=%s): %s",
                final_class_type,
                e,
            )
            context.class_template = None
        except (KeyError, AttributeError, TypeError) as e:
            logger.warning(
                "클래스 템플릿 응답 데이터 처리 오류 (class_type=%s): %s",
                final_class_type,
                e,
            )
            context.class_template = None

    # 4. 레이어 의존성 규칙 수집 (실패해도 진행 - 필수 아님)
    if include_dependencies and final_layer:
        try:
            all_deps = client.get_layer_dependencies()
            # 현재 레이어와 관련된 의존성만 필터링
            relevant_deps = [
                d
                for d in all_deps
                if d.source_layer == final_layer or d.target_layer == final_layer
            ]
            context.layer_dependencies = [
                _simplify_dependency(d) for d in relevant_deps
            ]
        except httpx.HTTPStatusError as e:
            logger.warning(
                "레이어 의존성 API 응답 오류 (layer=%s, HTTP %s): %s",
                final_layer,
                e.response.status_code,
                e,
            )
            context.layer_dependencies = []
        except httpx.RequestError as e:
            logger.warning(
                "레이어 의존성 수집 네트워크 오류 (layer=%s): %s",
                final_layer,
                e,
            )
            context.layer_dependencies = []
        except (KeyError, AttributeError, TypeError) as e:
            logger.warning(
                "레이어 의존성 응답 데이터 처리 오류 (layer=%s): %s",
                final_layer,
                e,
            )
            context.layer_dependencies = []

    # 5. 토큰 추정 및 요약 생성
    context.token_estimate = _estimate_tokens(context)
    context.context_summary = _generate_context_summary(context)

    return context
