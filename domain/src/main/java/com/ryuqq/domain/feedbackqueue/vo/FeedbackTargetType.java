package com.ryuqq.domain.feedbackqueue.vo;

/**
 * FeedbackTargetType - 피드백 대상 타입 Value Object
 *
 * <p>피드백이 적용될 대상 엔티티 타입을 나타냅니다. 각 타입별로 위험도(RiskLevel)가 결정됩니다.
 *
 * <ul>
 *   <li>RULE_EXAMPLE: 규칙 예시 (SAFE)
 *   <li>CLASS_TEMPLATE: 클래스 템플릿 (MEDIUM)
 *   <li>CODING_RULE: 코딩 규칙 (MEDIUM)
 *   <li>CHECKLIST_ITEM: 체크리스트 항목 (SAFE)
 *   <li>ARCH_UNIT_TEST: ArchUnit 테스트 (MEDIUM)
 * </ul>
 *
 * @author ryu-qqq
 */
public enum FeedbackTargetType {
    RULE_EXAMPLE("규칙 예시", RiskLevel.SAFE),
    CLASS_TEMPLATE("클래스 템플릿", RiskLevel.MEDIUM),
    CODING_RULE("코딩 규칙", RiskLevel.MEDIUM),
    CHECKLIST_ITEM("체크리스트 항목", RiskLevel.SAFE),
    ARCH_UNIT_TEST("ArchUnit 테스트", RiskLevel.MEDIUM);

    private final String description;
    private final RiskLevel riskLevel;

    FeedbackTargetType(String description, RiskLevel riskLevel) {
        this.description = description;
        this.riskLevel = riskLevel;
    }

    public String description() {
        return description;
    }

    /**
     * 해당 타입의 위험도 반환
     *
     * @return RiskLevel
     */
    public RiskLevel riskLevel() {
        return riskLevel;
    }

    /**
     * 자동 병합 가능 여부 확인 (위험도 기반)
     *
     * @return SAFE 타입인 경우 true
     */
    public boolean isAutoMergeable() {
        return riskLevel.isAutoMergeable();
    }

    /**
     * 사람의 승인이 필요한지 확인 (위험도 기반)
     *
     * @return MEDIUM 타입인 경우 true
     */
    public boolean requiresHumanApproval() {
        return riskLevel.requiresHumanApproval();
    }
}
