package com.ryuqq.domain.codingrule.fixture;

import com.ryuqq.domain.codingrule.aggregate.CodingRule;
import com.ryuqq.domain.codingrule.id.CodingRuleId;
import com.ryuqq.domain.codingrule.vo.AppliesTo;
import com.ryuqq.domain.codingrule.vo.RuleCategory;
import com.ryuqq.domain.codingrule.vo.RuleCode;
import com.ryuqq.domain.codingrule.vo.RuleName;
import com.ryuqq.domain.codingrule.vo.RuleSeverity;
import com.ryuqq.domain.codingrule.vo.SdkConstraint;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.convention.id.ConventionId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * CodingRule Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 CodingRule 객체 생성 유틸리티
 *
 * @author ryu-qqq
 */
public final class CodingRuleFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private CodingRuleFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 신규 CodingRule 생성 (ID 미할당)
     *
     * @return 신규 CodingRule
     */
    public static CodingRule forNew() {
        return CodingRule.forNew(
                CodingRuleVoFixtures.fixedConventionId(),
                null,
                CodingRuleVoFixtures.defaultRuleCode(),
                CodingRuleVoFixtures.defaultRuleName(),
                CodingRuleVoFixtures.defaultRuleSeverity(),
                CodingRuleVoFixtures.defaultRuleCategory(),
                CodingRuleVoFixtures.defaultRuleDescription(),
                CodingRuleVoFixtures.defaultRuleRationale(),
                false,
                CodingRuleVoFixtures.defaultAppliesTo(),
                SdkConstraint.empty(),
                FIXED_CLOCK.instant());
    }

    /**
     * 기존 CodingRule 복원 (기본 설정)
     *
     * @return 복원된 CodingRule
     */
    public static CodingRule reconstitute() {
        return reconstitute(CodingRuleVoFixtures.nextCodingRuleId());
    }

    /**
     * 지정된 ID로 CodingRule 복원
     *
     * @param id CodingRuleId
     * @return 복원된 CodingRule
     */
    public static CodingRule reconstitute(CodingRuleId id) {
        Instant now = FIXED_CLOCK.instant();
        return CodingRule.reconstitute(
                id,
                CodingRuleVoFixtures.fixedConventionId(),
                null,
                CodingRuleVoFixtures.defaultRuleCode(),
                CodingRuleVoFixtures.defaultRuleName(),
                CodingRuleVoFixtures.defaultRuleSeverity(),
                CodingRuleVoFixtures.defaultRuleCategory(),
                CodingRuleVoFixtures.defaultRuleDescription(),
                CodingRuleVoFixtures.defaultRuleRationale(),
                false,
                CodingRuleVoFixtures.defaultAppliesTo(),
                SdkConstraint.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 상세 파라미터로 CodingRule 복원
     *
     * @param id CodingRuleId
     * @param conventionId ConventionId
     * @param code RuleCode
     * @param name RuleName
     * @param severity RuleSeverity
     * @param category RuleCategory
     * @param description 설명
     * @param rationale 근거
     * @param autoFixable 자동 수정 가능 여부
     * @param appliesTo 적용 대상
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @param deletionStatus 삭제 상태
     * @return 복원된 CodingRule
     */
    public static CodingRule reconstitute(
            CodingRuleId id,
            ConventionId conventionId,
            RuleCode code,
            RuleName name,
            RuleSeverity severity,
            RuleCategory category,
            String description,
            String rationale,
            boolean autoFixable,
            AppliesTo appliesTo,
            Instant createdAt,
            Instant updatedAt,
            DeletionStatus deletionStatus) {
        return CodingRule.reconstitute(
                id,
                conventionId,
                null,
                code,
                name,
                severity,
                category,
                description,
                rationale,
                autoFixable,
                appliesTo,
                SdkConstraint.empty(),
                deletionStatus,
                createdAt,
                updatedAt);
    }

    /**
     * 삭제된 CodingRule
     *
     * @return 삭제된 CodingRule
     */
    public static CodingRule deletedRule() {
        Instant now = FIXED_CLOCK.instant();
        return CodingRule.reconstitute(
                CodingRuleVoFixtures.nextCodingRuleId(),
                CodingRuleVoFixtures.fixedConventionId(),
                null,
                CodingRuleVoFixtures.defaultRuleCode(),
                CodingRuleVoFixtures.defaultRuleName(),
                CodingRuleVoFixtures.defaultRuleSeverity(),
                CodingRuleVoFixtures.defaultRuleCategory(),
                CodingRuleVoFixtures.defaultRuleDescription(),
                CodingRuleVoFixtures.defaultRuleRationale(),
                false,
                CodingRuleVoFixtures.defaultAppliesTo(),
                SdkConstraint.empty(),
                DeletionStatus.deletedAt(now),
                now,
                now);
    }

    /**
     * Zero-Tolerance 성격의 CodingRule (BLOCKER 심각도)
     *
     * <p>Note: Zero-Tolerance 여부는 ZeroToleranceRule 엔티티 존재 여부로 결정됩니다. 이 메서드는 Zero-Tolerance 규칙에 적합한
     * 특성을 가진 CodingRule을 생성합니다.
     *
     * @return Zero-Tolerance 성격의 CodingRule
     */
    public static CodingRule zeroToleranceRule() {
        Instant now = FIXED_CLOCK.instant();
        return CodingRule.reconstitute(
                CodingRuleVoFixtures.nextCodingRuleId(),
                CodingRuleVoFixtures.fixedConventionId(),
                null,
                RuleCode.of("ZT-001"),
                RuleName.of("Zero Tolerance Rule"),
                RuleSeverity.BLOCKER,
                RuleCategory.ANNOTATION,
                "Zero tolerance rule description",
                "Zero tolerance rule rationale",
                false,
                CodingRuleVoFixtures.defaultAppliesTo(),
                SdkConstraint.empty(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * AutoFixable CodingRule
     *
     * @return AutoFixable CodingRule
     */
    public static CodingRule autoFixableRule() {
        Instant now = FIXED_CLOCK.instant();
        return CodingRule.reconstitute(
                CodingRuleVoFixtures.nextCodingRuleId(),
                CodingRuleVoFixtures.fixedConventionId(),
                null,
                RuleCode.of("AF-001"),
                RuleName.of("Auto Fixable Rule"),
                RuleSeverity.MINOR,
                RuleCategory.NAMING,
                "Auto fixable rule description",
                "Auto fixable rule rationale",
                true,
                CodingRuleVoFixtures.defaultAppliesTo(),
                SdkConstraint.empty(),
                DeletionStatus.active(),
                now,
                now);
    }
}
