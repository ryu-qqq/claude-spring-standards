package com.ryuqq.domain.codingrule.fixture;

import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.AppliesTo;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.codingrule.vo.RuleName;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.id.ConventionId;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CodingRule VO 테스트 Fixture
 *
 * <p>CodingRule의 Value Object들을 위한 테스트 데이터 생성 유틸리티입니다.
 *
 * @author ryu-qqq
 */
public final class CodingRuleVoFixtures {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1L);

    private CodingRuleVoFixtures() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ==================== ID Fixtures ====================

    /**
     * 다음 CodingRuleId 생성 (시퀀스 증가)
     *
     * @return 새로운 CodingRuleId
     */
    public static CodingRuleId nextCodingRuleId() {
        return CodingRuleId.of(ID_SEQUENCE.getAndIncrement());
    }

    /**
     * 고정 Convention ID
     *
     * @return ConventionId
     */
    public static ConventionId fixedConventionId() {
        return ConventionId.of(12345L);
    }

    // ==================== RuleCode Fixtures ====================

    /**
     * 기본 규칙 코드
     *
     * @return RuleCode
     */
    public static RuleCode defaultRuleCode() {
        return RuleCode.of("DOMAIN-001");
    }

    /**
     * 지정된 값의 규칙 코드
     *
     * @param code 코드 문자열
     * @return RuleCode
     */
    public static RuleCode ruleCodeOf(String code) {
        return RuleCode.of(code);
    }

    // ==================== RuleName Fixtures ====================

    /**
     * 기본 규칙 이름
     *
     * @return RuleName
     */
    public static RuleName defaultRuleName() {
        return RuleName.of("Test Rule Name");
    }

    /**
     * 지정된 값의 규칙 이름
     *
     * @param name 이름 문자열
     * @return RuleName
     */
    public static RuleName ruleNameOf(String name) {
        return RuleName.of(name);
    }

    // ==================== RuleSeverity Fixtures ====================

    /**
     * 기본 심각도 (MAJOR)
     *
     * @return RuleSeverity
     */
    public static RuleSeverity defaultRuleSeverity() {
        return RuleSeverity.MAJOR;
    }

    /**
     * Zero-Tolerance 심각도 (BLOCKER)
     *
     * @return RuleSeverity
     */
    public static RuleSeverity zeroToleranceSeverity() {
        return RuleSeverity.BLOCKER;
    }

    // ==================== RuleCategory Fixtures ====================

    /**
     * 기본 카테고리 (ANNOTATION)
     *
     * @return RuleCategory
     */
    public static RuleCategory defaultRuleCategory() {
        return RuleCategory.ANNOTATION;
    }

    // ==================== Description/Rationale Fixtures ====================

    /**
     * 기본 규칙 설명
     *
     * @return 설명 문자열
     */
    public static String defaultRuleDescription() {
        return "Test rule description for testing purposes";
    }

    /**
     * 기본 규칙 근거
     *
     * @return 근거 문자열
     */
    public static String defaultRuleRationale() {
        return "Test rule rationale explaining why this rule exists";
    }

    // ==================== AppliesTo Fixtures ====================

    /**
     * 기본 적용 대상 (domain, aggregate, vo)
     *
     * @return AppliesTo
     */
    public static AppliesTo defaultAppliesTo() {
        return AppliesTo.of(Arrays.asList("domain", "aggregate", "vo"));
    }

    /**
     * 빈 적용 대상
     *
     * @return AppliesTo
     */
    public static AppliesTo emptyAppliesTo() {
        return AppliesTo.empty();
    }

    // ==================== DeletionStatus Fixtures ====================

    /**
     * 활성 삭제 상태
     *
     * @return DeletionStatus
     */
    public static DeletionStatus activeDeletionStatus() {
        return DeletionStatus.active();
    }
}
