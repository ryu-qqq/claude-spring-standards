package com.ryuqq.domain.layerdependency.fixture;

import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * LayerDependencyRule Aggregate Test Fixture
 *
 * <p>모든 레이어에서 재사용 가능한 LayerDependencyRule 객체 생성 유틸리티
 *
 * @author ryu-qqq
 */
public final class LayerDependencyRuleFixture {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

    private LayerDependencyRuleFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * 신규 LayerDependencyRule 생성 (ID 미할당)
     *
     * @return 신규 LayerDependencyRule
     */
    public static LayerDependencyRule forNew() {
        return LayerDependencyRule.forNew(
                LayerDependencyRuleVoFixtures.fixedArchitectureId(),
                LayerDependencyRuleVoFixtures.defaultFromLayer(),
                LayerDependencyRuleVoFixtures.defaultToLayer(),
                LayerDependencyRuleVoFixtures.defaultDependencyType(),
                LayerDependencyRuleVoFixtures.emptyConditionDescription(),
                FIXED_CLOCK.instant());
    }

    /**
     * 기존 LayerDependencyRule 복원 (기본 설정)
     *
     * @return 복원된 LayerDependencyRule
     */
    public static LayerDependencyRule reconstitute() {
        return reconstitute(LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId());
    }

    /**
     * 지정된 ID로 LayerDependencyRule 복원
     *
     * @param id LayerDependencyRuleId
     * @return 복원된 LayerDependencyRule
     */
    public static LayerDependencyRule reconstitute(LayerDependencyRuleId id) {
        Instant now = FIXED_CLOCK.instant();
        return LayerDependencyRule.reconstitute(
                id,
                LayerDependencyRuleVoFixtures.fixedArchitectureId(),
                LayerDependencyRuleVoFixtures.defaultFromLayer(),
                LayerDependencyRuleVoFixtures.defaultToLayer(),
                LayerDependencyRuleVoFixtures.defaultDependencyType(),
                LayerDependencyRuleVoFixtures.emptyConditionDescription(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 기본 기존 LayerDependencyRule (저장된 상태)
     *
     * @return 기존 LayerDependencyRule
     */
    public static LayerDependencyRule defaultExistingLayerDependencyRule() {
        Instant now = FIXED_CLOCK.instant();
        return LayerDependencyRule.of(
                LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId(),
                LayerDependencyRuleVoFixtures.fixedArchitectureId(),
                LayerDependencyRuleVoFixtures.defaultFromLayer(),
                LayerDependencyRuleVoFixtures.defaultToLayer(),
                LayerDependencyRuleVoFixtures.defaultDependencyType(),
                LayerDependencyRuleVoFixtures.emptyConditionDescription(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 허용된 의존성 규칙
     *
     * @return 허용된 LayerDependencyRule
     */
    public static LayerDependencyRule allowedRule() {
        Instant now = FIXED_CLOCK.instant();
        return LayerDependencyRule.reconstitute(
                LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId(),
                LayerDependencyRuleVoFixtures.fixedArchitectureId(),
                LayerType.DOMAIN,
                LayerType.APPLICATION,
                DependencyType.ALLOWED,
                LayerDependencyRuleVoFixtures.emptyConditionDescription(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 금지된 의존성 규칙
     *
     * @return 금지된 LayerDependencyRule
     */
    public static LayerDependencyRule forbiddenRule() {
        Instant now = FIXED_CLOCK.instant();
        return LayerDependencyRule.reconstitute(
                LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId(),
                LayerDependencyRuleVoFixtures.fixedArchitectureId(),
                LayerType.APPLICATION,
                LayerType.DOMAIN,
                DependencyType.FORBIDDEN,
                LayerDependencyRuleVoFixtures.emptyConditionDescription(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 조건부 의존성 규칙
     *
     * @return 조건부 LayerDependencyRule
     */
    public static LayerDependencyRule conditionalRule() {
        Instant now = FIXED_CLOCK.instant();
        return LayerDependencyRule.reconstitute(
                LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId(),
                LayerDependencyRuleVoFixtures.fixedArchitectureId(),
                LayerType.ADAPTER_IN,
                LayerType.DOMAIN,
                DependencyType.CONDITIONAL,
                LayerDependencyRuleVoFixtures.defaultConditionDescription(),
                DeletionStatus.active(),
                now,
                now);
    }

    /**
     * 삭제된 LayerDependencyRule
     *
     * @return 삭제된 LayerDependencyRule
     */
    public static LayerDependencyRule deletedRule() {
        Instant now = FIXED_CLOCK.instant();
        return LayerDependencyRule.reconstitute(
                LayerDependencyRuleVoFixtures.nextLayerDependencyRuleId(),
                LayerDependencyRuleVoFixtures.fixedArchitectureId(),
                LayerDependencyRuleVoFixtures.defaultFromLayer(),
                LayerDependencyRuleVoFixtures.defaultToLayer(),
                LayerDependencyRuleVoFixtures.defaultDependencyType(),
                LayerDependencyRuleVoFixtures.emptyConditionDescription(),
                DeletionStatus.deletedAt(now),
                now,
                now);
    }
}
