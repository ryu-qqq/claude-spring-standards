"""
feedback Tool

피드백 제출 → FeedbackQueue에 PENDING 상태로 저장
LLM이 직접 데이터를 수정하지 않고, 검토 큐를 통해 안전하게 반영

변경 전: RuleExample 직접 INSERT (위험)
변경 후: FeedbackQueue API 호출 (안전)
"""

import json
from typing import Any, Optional

from ..api_client import get_api_client
from ..models import FeedbackTargetType, FeedbackType


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
