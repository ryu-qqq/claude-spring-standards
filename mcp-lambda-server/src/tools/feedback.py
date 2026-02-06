"""
feedback Tool

피드백 제출 → FeedbackQueue에 PENDING 상태로 저장
LLM이 직접 데이터를 수정하지 않고, 검토 큐를 통해 안전하게 반영

변경 전: RuleExample 직접 INSERT (위험)
변경 후: FeedbackQueue API 호출 (안전)
"""

import json
import logging
from typing import Any, Optional

from ..api_client import get_api_client
from ..models import FeedbackTargetType, FeedbackType

logger = logging.getLogger(__name__)


def _validate_coding_rule_add_payload(
    payload: dict[str, Any],
) -> Optional[dict[str, Any]]:
    """CODING_RULE ADD 시 payload 사전 검증

    conventionId, appliesTo 유효성을 검증하고, 실패 시 힌트를 포함한 에러를 반환합니다.
    API 호출 실패 시 graceful pass-through (None 반환 → Spring에서 최종 검증).

    Returns:
        검증 실패 시 에러 dict, 성공 또는 skip 시 None
    """
    try:
        client = get_api_client()
    except Exception:
        return None  # API 클라이언트 실패 → pass-through

    errors = []
    hints: dict[str, Any] = {}

    # 1. conventionId 검증
    convention_id = payload.get("conventionId")
    if convention_id is not None:
        try:
            conventions = client.get_all_conventions_with_modules()
            valid_ids = {c["id"] for c in conventions}
            if convention_id not in valid_ids:
                errors.append(f"conventionId={convention_id}는 존재하지 않습니다.")
                hints["available_conventions"] = [
                    {"id": c["id"], "module_name": c["module_name"], "layer_code": c["layer_code"]}
                    for c in conventions
                    if c.get("active", True)
                ]

                # code prefix 기반 convention 추천
                code = payload.get("code", "")
                if code and conventions:
                    suggested = _suggest_convention_by_code_prefix(code, conventions)
                    if suggested:
                        hints["suggested_convention"] = suggested
        except Exception as e:
            logger.warning(f"Convention 검증 실패 (graceful skip): {e}")

    # 2. appliesTo 검증
    applies_to = payload.get("appliesTo")
    if applies_to and isinstance(applies_to, list):
        try:
            class_types = client.get_class_types()
            valid_codes = {ct.code for ct in class_types}
            invalid_codes = [code for code in applies_to if code not in valid_codes]
            if invalid_codes:
                errors.append(f"appliesTo 값 {invalid_codes}는 유효한 class_type 코드가 아닙니다.")
                hints["available_class_types"] = [
                    {"code": ct.code, "name": ct.name}
                    for ct in class_types
                ]
        except Exception as e:
            logger.warning(f"ClassType 검증 실패 (graceful skip): {e}")

    if errors:
        return {
            "success": False,
            "error": " ".join(errors),
            "hints": hints,
            "tip": "get_feedback_schema('CODING_RULE')로 유효한 값을 먼저 확인하세요.",
        }

    return None


def _suggest_convention_by_code_prefix(
    code: str, conventions: list[dict[str, Any]]
) -> Optional[dict[str, Any]]:
    """코드 prefix로 convention 추천 (간단 버전)"""
    prefix_layer_map = {
        "DOM": "DOMAIN", "AGG": "DOMAIN", "VO": "DOMAIN", "EVT": "DOMAIN",
        "APP": "APPLICATION", "SVC": "APPLICATION", "UC": "APPLICATION",
        "PER": "ADAPTER_OUT", "ENT": "ADAPTER_OUT", "JPA": "ADAPTER_OUT",
        "API": "ADAPTER_IN", "CTR": "ADAPTER_IN",
        "BOOT": "BOOTSTRAP", "CFG": "BOOTSTRAP",
    }

    code_upper = code.upper()
    for prefix, layer_code in prefix_layer_map.items():
        if code_upper.startswith(prefix):
            for conv in conventions:
                if conv.get("layer_code") == layer_code and conv.get("active", True):
                    return {
                        "id": conv["id"],
                        "module_name": conv["module_name"],
                        "layer_code": conv["layer_code"],
                        "reason": f"코드 prefix '{prefix}' 기반 추천",
                    }
            break

    return None


def feedback(
    target_type: str,
    feedback_type: str,
    payload: dict[str, Any],
    target_id: Optional[int] = None,
) -> dict[str, Any]:
    """
    피드백 제출 → FeedbackQueue에 PENDING 상태로 저장

    Args:
        target_type: 대상 타입 (RULE_EXAMPLE | CLASS_TEMPLATE | CODING_RULE | CHECKLIST_ITEM | ARCH_UNIT_TEST)
        feedback_type: 피드백 유형 (ADD | MODIFY | DELETE) - CREATE/UPDATE도 허용 (자동 변환)
        payload: 대상별 JSON 데이터
        target_id: 수정/삭제 시 기존 대상 ID (Optional, ADD 시 자동으로 0)

    Returns:
        dict: 결과 정보 (feedback_queue_id, status 등)

    Example:
        # RuleExample 추가
        feedback(
            target_type="RULE_EXAMPLE",
            feedback_type="ADD",  # 또는 "CREATE"
            payload={
                "codingRuleId": 123,
                "exampleType": "GOOD",
                "code": "// Good example code",
                "language": "java",
                "explanation": "설명"
            }
        )

        # CodingRule 수정
        feedback(
            target_type="CODING_RULE",
            feedback_type="MODIFY",  # 또는 "UPDATE"
            target_id=456,
            payload={
                "description": "수정된 설명"
            }
        )
    """
    # target_type 유효성 검증
    valid_target_types = [t.value for t in FeedbackTargetType]
    if target_type not in valid_target_types:
        return {
            "success": False,
            "error": f"Invalid target_type: {target_type}. Must be one of {valid_target_types}",
        }

    # feedback_type 별칭 변환 (CREATE→ADD, UPDATE→MODIFY)
    try:
        resolved_feedback_type = FeedbackType.from_alias(feedback_type)
        feedback_type = resolved_feedback_type.value
    except ValueError:
        valid_feedback_types = [t.value for t in FeedbackType]
        return {
            "success": False,
            "error": f"Invalid feedback_type: {feedback_type}. Must be one of {valid_feedback_types} (or CREATE/UPDATE aliases)",
        }

    # MODIFY/DELETE 시 target_id 필수
    if feedback_type in [FeedbackType.MODIFY.value, FeedbackType.DELETE.value] and target_id is None:
        return {
            "success": False,
            "error": f"target_id is required for {feedback_type} operation",
        }

    # CODING_RULE ADD 사전 검증
    if target_type == "CODING_RULE" and feedback_type == FeedbackType.ADD.value:
        validation_error = _validate_coding_rule_add_payload(payload)
        if validation_error:
            return validation_error

    # payload를 JSON 문자열로 변환
    try:
        payload_str = json.dumps(payload, ensure_ascii=False)
    except (TypeError, ValueError) as e:
        return {
            "success": False,
            "error": f"Invalid payload format: {e!s}",
        }

    # FeedbackQueue API 호출
    try:
        client = get_api_client()

        result = client.create_feedback(
            target_type=target_type,
            feedback_type=feedback_type,
            payload=payload_str,
            target_id=target_id,
        )

        return {
            "success": True,
            "feedback_queue_id": result.feedback_queue_id,
            "status": "PENDING",
            "message": f"피드백이 큐에 등록되었습니다. (target: {target_type}, type: {feedback_type})",
            "next_step": "sync 도구를 실행하면 LLM이 검토 후 승인/거절합니다.",
        }

    except Exception as e:
        return {
            "success": False,
            "error": f"Failed to submit feedback: {e!s}",
        }


# 편의 함수들 (기존 인터페이스 호환)

def submit_rule_example(
    rule_code: str,
    example_type: str,
    code: str,
    explanation: Optional[str] = None,
) -> dict[str, Any]:
    """
    규칙 예시 제출 (RULE_EXAMPLE CREATE)

    Args:
        rule_code: 규칙 코드 (예: DOM-001)
        example_type: 예시 유형 (GOOD | BAD)
        code: 코드 스니펫
        explanation: 설명 (Optional)

    Returns:
        dict: 결과 정보
    """
    # rule_code → rule_id 변환 필요
    try:
        client = get_api_client()
        coding_rule = client.get_coding_rule_by_code(rule_code)

        if not coding_rule:
            return {
                "success": False,
                "error": f"Cannot find rule for code: {rule_code}",
            }

        payload = {
            "codingRuleId": coding_rule.coding_rule_id,
            "exampleType": example_type.upper(),
            "code": code,
            "language": "java",
        }

        if explanation:
            payload["explanation"] = explanation

        return feedback(
            target_type="RULE_EXAMPLE",
            feedback_type="ADD",
            payload=payload,
        )

    except Exception as e:
        return {
            "success": False,
            "error": f"Failed to submit rule example: {e!s}",
        }


def submit_coding_rule_update(
    rule_id: int,
    updates: dict[str, Any],
) -> dict[str, Any]:
    """
    코딩 규칙 수정 제출 (CODING_RULE UPDATE)

    Args:
        rule_id: 규칙 ID
        updates: 수정할 필드들

    Returns:
        dict: 결과 정보
    """
    return feedback(
        target_type="CODING_RULE",
        feedback_type="MODIFY",
        target_id=rule_id,
        payload=updates,
    )


def submit_class_template_update(
    template_id: int,
    updates: dict[str, Any],
) -> dict[str, Any]:
    """
    클래스 템플릿 수정 제출 (CLASS_TEMPLATE UPDATE)

    Args:
        template_id: 템플릿 ID
        updates: 수정할 필드들

    Returns:
        dict: 결과 정보
    """
    return feedback(
        target_type="CLASS_TEMPLATE",
        feedback_type="MODIFY",
        target_id=template_id,
        payload=updates,
    )
