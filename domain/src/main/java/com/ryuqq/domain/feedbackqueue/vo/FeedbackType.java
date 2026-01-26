package com.ryuqq.domain.feedbackqueue.vo;

/**
 * FeedbackType - 피드백 유형 Value Object
 *
 * <p>피드백의 작업 유형을 나타냅니다.
 *
 * <ul>
 *   <li>ADD: 새로운 항목 추가
 *   <li>MODIFY: 기존 항목 수정
 *   <li>DELETE: 기존 항목 삭제
 * </ul>
 *
 * @author ryu-qqq
 */
public enum FeedbackType {
    ADD("추가"),
    MODIFY("수정"),
    DELETE("삭제");

    private final String description;

    FeedbackType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    /**
     * 추가 작업인지 확인
     *
     * @return ADD인 경우 true
     */
    public boolean isAdd() {
        return this == ADD;
    }

    /**
     * 수정 작업인지 확인
     *
     * @return MODIFY인 경우 true
     */
    public boolean isModify() {
        return this == MODIFY;
    }

    /**
     * 삭제 작업인지 확인
     *
     * @return DELETE인 경우 true
     */
    public boolean isDelete() {
        return this == DELETE;
    }

    /**
     * 피드백 유형에 따른 RiskLevel 결정
     *
     * <p>삭제 작업은 HIGH, 나머지는 SAFE로 결정됩니다.
     *
     * @return DELETE → HIGH, ADD/MODIFY → SAFE
     */
    public RiskLevel riskLevel() {
        if (this == DELETE) {
            return RiskLevel.HIGH;
        }
        return RiskLevel.SAFE;
    }
}
