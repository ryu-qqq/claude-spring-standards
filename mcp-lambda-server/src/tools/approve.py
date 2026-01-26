"""
approve Tool

피드백 승인/거절 처리
- LLM 1차 승인/거절 (PENDING → LLM_APPROVED/LLM_REJECTED)
- Human 2차 승인/거절 (LLM_APPROVED → HUMAN_APPROVED/HUMAN_REJECTED)
- Safe 레벨 승인 시 자동 머지
"""

from typing import Any, Optional

from ..api_client import get_api_client
from ..models import FeedbackRiskLevel


def approve(
    feedback_id: int,
    action: str,
    reviewer: str = "llm",
    review_notes: Optional[str] = None,
    auto_merge: bool = True,
) -> dict[str, Any]:
    """
    피드백 승인/거절 처리

    Args:
        feedback_id: FeedbackQueue ID
        action: 액션 유형 ("approve" | "reject")
        reviewer: 검토자 유형 ("llm" | "human")
        review_notes: 거절 사유 또는 리뷰 코멘트 (Optional)
        auto_merge: Safe 레벨 승인 시 자동 머지 여부 (기본값: True)

    Returns:
        dict: 처리 결과

    Example:
        # LLM 1차 승인
        approve(feedback_id=1, action="approve", reviewer="llm")

        # LLM 1차 거절
        approve(feedback_id=2, action="reject", reviewer="llm", review_notes="중복된 예시")

        # Human 2차 승인
        approve(feedback_id=3, action="approve", reviewer="human")

        # Human 2차 거절
        approve(feedback_id=4, action="reject", reviewer="human", review_notes="품질 부족")
    """
    # 유효성 검증
    if action not in ["approve", "reject"]:
        return {
            "success": False,
            "error": f"Invalid action: {action}. Must be 'approve' or 'reject'",
        }

    if reviewer not in ["llm", "human"]:
        return {
            "success": False,
            "error": f"Invalid reviewer: {reviewer}. Must be 'llm' or 'human'",
        }

    try:
        client = get_api_client()

        # 승인/거절 처리
        if reviewer == "llm":
            result = _process_llm_action(client, feedback_id, action, review_notes)
        else:
            result = _process_human_action(client, feedback_id, action, review_notes)

        if not result["success"]:
            return result

        feedback_result = result["data"]

        # Safe 레벨 승인 시 자동 머지
        merged = False
        if (
            action == "approve"
            and auto_merge
            and feedback_result.risk_level == FeedbackRiskLevel.SAFE.value
        ):
            try:
                merge_result = client.merge_feedback(feedback_id)
                merged = True
                feedback_result = merge_result
            except Exception as e:
                return {
                    "success": True,
                    "feedback_queue_id": feedback_id,
                    "status": feedback_result.status,
                    "action": action,
                    "reviewer": reviewer,
                    "merged": False,
                    "warning": f"승인 완료, 자동 머지 실패: {e!s}",
                }

        return {
            "success": True,
            "feedback_queue_id": feedback_id,
            "status": feedback_result.status,
            "action": action,
            "reviewer": reviewer,
            "merged": merged,
            "message": _get_result_message(action, reviewer, merged, feedback_result.risk_level),
        }

    except Exception as e:
        return {
            "success": False,
            "error": f"Failed to process feedback: {e!s}",
        }


def _process_llm_action(
    client,
    feedback_id: int,
    action: str,
    review_notes: Optional[str],
) -> dict[str, Any]:
    """LLM 1차 승인/거절 처리"""
    try:
        if action == "approve":
            result = client.llm_approve_feedback(feedback_id)
        else:
            result = client.llm_reject_feedback(feedback_id, review_notes)

        return {"success": True, "data": result}

    except Exception as e:
        return {"success": False, "error": f"LLM {action} failed: {e!s}"}


def _process_human_action(
    client,
    feedback_id: int,
    action: str,
    review_notes: Optional[str],
) -> dict[str, Any]:
    """Human 2차 승인/거절 처리"""
    try:
        if action == "approve":
            result = client.human_approve_feedback(feedback_id)
            # Human 승인 후 자동 머지 시도
            try:
                merge_result = client.merge_feedback(feedback_id)
                return {"success": True, "data": merge_result}
            except Exception:
                return {"success": True, "data": result}
        else:
            result = client.human_reject_feedback(feedback_id, review_notes)

        return {"success": True, "data": result}

    except Exception as e:
        return {"success": False, "error": f"Human {action} failed: {e!s}"}


def _get_result_message(action: str, reviewer: str, merged: bool, risk_level: str) -> str:
    """결과 메시지 생성"""
    if action == "reject":
        return f"피드백이 {reviewer.upper()}에 의해 거절되었습니다."

    if merged:
        return "피드백이 승인되어 실제 데이터에 반영되었습니다."

    if risk_level == FeedbackRiskLevel.MEDIUM.value:
        if reviewer == "llm":
            return "LLM 1차 승인 완료. Human 2차 승인이 필요합니다."
        else:
            return "Human 2차 승인 완료. 머지되었습니다."

    return f"피드백이 {reviewer.upper()}에 의해 승인되었습니다."


# 편의 함수들

def llm_approve(feedback_id: int) -> dict[str, Any]:
    """LLM 1차 승인 (편의 함수)"""
    return approve(feedback_id=feedback_id, action="approve", reviewer="llm")


def llm_reject(feedback_id: int, reason: Optional[str] = None) -> dict[str, Any]:
    """LLM 1차 거절 (편의 함수)"""
    return approve(
        feedback_id=feedback_id,
        action="reject",
        reviewer="llm",
        review_notes=reason,
    )


def human_approve(feedback_id: int) -> dict[str, Any]:
    """Human 2차 승인 (편의 함수)"""
    return approve(feedback_id=feedback_id, action="approve", reviewer="human")


def human_reject(feedback_id: int, reason: Optional[str] = None) -> dict[str, Any]:
    """Human 2차 거절 (편의 함수)"""
    return approve(
        feedback_id=feedback_id,
        action="reject",
        reviewer="human",
        review_notes=reason,
    )


def batch_approve(
    feedback_ids: list[int],
    reviewer: str = "llm",
) -> dict[str, Any]:
    """여러 피드백 일괄 승인"""
    results = {
        "success": True,
        "total": len(feedback_ids),
        "approved": 0,
        "failed": 0,
        "details": [],
    }

    for fid in feedback_ids:
        result = approve(feedback_id=fid, action="approve", reviewer=reviewer)
        if result.get("success"):
            results["approved"] += 1
        else:
            results["failed"] += 1

        results["details"].append({
            "feedback_id": fid,
            "success": result.get("success", False),
            "status": result.get("status"),
            "error": result.get("error"),
        })

    return results
