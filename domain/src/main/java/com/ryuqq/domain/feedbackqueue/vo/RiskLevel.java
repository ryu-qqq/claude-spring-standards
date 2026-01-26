package com.ryuqq.domain.feedbackqueue.vo;

/**
 * RiskLevel - 피드백 위험도 레벨 Value Object
 *
 * <p>피드백 대상에 따른 위험도를 나타냅니다. 위험도에 따라 자동 병합 가능 여부가 결정됩니다.
 *
 * <ul>
 *   <li>SAFE: LLM 승인만으로 자동 병합 가능
 *   <li>MEDIUM: 사람의 추가 승인 필요
 *   <li>HIGH: 삭제 등 고위험 작업 - 반드시 사람 승인 필요
 * </ul>
 *
 * @author ryu-qqq
 */
public enum RiskLevel {
    SAFE("안전 - LLM 승인만으로 자동 병합 가능"),
    MEDIUM("중간 - 사람의 추가 승인 필요"),
    HIGH("고위험 - 삭제 등 반드시 사람 승인 필요");

    private final String description;

    RiskLevel(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    /**
     * 자동 병합 가능 여부 확인
     *
     * @return SAFE인 경우 true
     */
    public boolean isAutoMergeable() {
        return this == SAFE;
    }

    /**
     * 사람의 승인이 필요한지 확인
     *
     * @return MEDIUM 또는 HIGH인 경우 true
     */
    public boolean requiresHumanApproval() {
        return this == MEDIUM || this == HIGH;
    }
}
