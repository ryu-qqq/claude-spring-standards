package com.ryuqq.domain.feedbackqueue.vo;

/**
 * FeedbackAction - 피드백 처리 액션 Value Object
 *
 * <p>피드백에 대해 수행할 수 있는 액션을 정의합니다.
 *
 * <p>VO-001: Value Object는 enum 또는 record로 정의.
 *
 * @author ryu-qqq
 */
public enum FeedbackAction {
    LLM_APPROVE("LLM 1차 승인"),
    LLM_REJECT("LLM 1차 거절"),
    HUMAN_APPROVE("Human 2차 승인"),
    HUMAN_REJECT("Human 2차 거절");

    private final String description;

    FeedbackAction(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    /**
     * 거절 액션인지 확인
     *
     * @return LLM_REJECT 또는 HUMAN_REJECT인 경우 true
     */
    public boolean isReject() {
        return this == LLM_REJECT || this == HUMAN_REJECT;
    }

    /**
     * LLM 액션인지 확인
     *
     * @return LLM_APPROVE 또는 LLM_REJECT인 경우 true
     */
    public boolean isLlmAction() {
        return this == LLM_APPROVE || this == LLM_REJECT;
    }

    /**
     * Human 액션인지 확인
     *
     * @return HUMAN_APPROVE 또는 HUMAN_REJECT인 경우 true
     */
    public boolean isHumanAction() {
        return this == HUMAN_APPROVE || this == HUMAN_REJECT;
    }
}
