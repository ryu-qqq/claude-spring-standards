package com.ryuqq.domain.feedbackqueue.vo;

/**
 * FeedbackStatus - 피드백 상태 Value Object
 *
 * <p>피드백의 처리 상태를 나타냅니다.
 *
 * <p><strong>상태 전이 규칙:</strong>
 *
 * <pre>
 * PENDING → LLM_APPROVED (llmApprove)
 * PENDING → LLM_REJECTED (llmReject) [Terminal]
 * LLM_APPROVED + SAFE → MERGED (merge)
 * LLM_APPROVED + MEDIUM → HUMAN_APPROVED (humanApprove)
 * LLM_APPROVED + MEDIUM → HUMAN_REJECTED (humanReject) [Terminal]
 * HUMAN_APPROVED → MERGED (merge) [Terminal]
 * </pre>
 *
 * @author ryu-qqq
 */
public enum FeedbackStatus {
    PENDING("대기 중", false),
    LLM_APPROVED("LLM 승인", false),
    LLM_REJECTED("LLM 거절", true),
    HUMAN_APPROVED("사람 승인", false),
    HUMAN_REJECTED("사람 거절", true),
    MERGED("병합 완료", true);

    private final String description;
    private final boolean terminal;

    FeedbackStatus(String description, boolean terminal) {
        this.description = description;
        this.terminal = terminal;
    }

    public String description() {
        return description;
    }

    /**
     * 종료 상태인지 확인
     *
     * @return 더 이상 상태 전이가 불가능하면 true
     */
    public boolean isTerminal() {
        return terminal;
    }

    /**
     * LLM 승인이 가능한 상태인지 확인
     *
     * @return PENDING인 경우 true
     */
    public boolean canLlmApprove() {
        return this == PENDING;
    }

    /**
     * LLM 거절이 가능한 상태인지 확인
     *
     * @return PENDING인 경우 true
     */
    public boolean canLlmReject() {
        return this == PENDING;
    }

    /**
     * 사람 승인이 가능한 상태인지 확인
     *
     * @return LLM_APPROVED인 경우 true
     */
    public boolean canHumanApprove() {
        return this == LLM_APPROVED;
    }

    /**
     * 사람 거절이 가능한 상태인지 확인
     *
     * @return LLM_APPROVED인 경우 true
     */
    public boolean canHumanReject() {
        return this == LLM_APPROVED;
    }

    /**
     * 병합이 가능한 상태인지 확인
     *
     * @return LLM_APPROVED 또는 HUMAN_APPROVED인 경우 true
     */
    public boolean canMerge() {
        return this == LLM_APPROVED || this == HUMAN_APPROVED;
    }

    /**
     * 승인된 상태인지 확인
     *
     * @return LLM_APPROVED 또는 HUMAN_APPROVED인 경우 true
     */
    public boolean isApproved() {
        return this == LLM_APPROVED || this == HUMAN_APPROVED;
    }

    /**
     * 거절된 상태인지 확인
     *
     * @return LLM_REJECTED 또는 HUMAN_REJECTED인 경우 true
     */
    public boolean isRejected() {
        return this == LLM_REJECTED || this == HUMAN_REJECTED;
    }

    /**
     * 병합 완료 상태인지 확인
     *
     * @return MERGED인 경우 true
     */
    public boolean isMerged() {
        return this == MERGED;
    }
}
