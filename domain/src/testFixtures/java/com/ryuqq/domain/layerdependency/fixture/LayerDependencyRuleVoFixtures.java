package com.ryuqq.domain.layerdependency.fixture;

import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import com.ryuqq.domain.layerdependency.vo.ConditionDescription;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LayerDependencyRule VO 테스트 Fixture
 *
 * <p>LayerDependencyRule의 Value Object들을 위한 테스트 데이터 생성 유틸리티입니다.
 *
 * @author ryu-qqq
 */
public final class LayerDependencyRuleVoFixtures {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1L);

    private LayerDependencyRuleVoFixtures() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ==================== ID Fixtures ====================

    /**
     * 다음 LayerDependencyRuleId 생성 (시퀀스 증가)
     *
     * @return 새로운 LayerDependencyRuleId
     */
    public static LayerDependencyRuleId nextLayerDependencyRuleId() {
        return LayerDependencyRuleId.of(ID_SEQUENCE.getAndIncrement());
    }

    /**
     * 고정 Architecture ID
     *
     * @return ArchitectureId
     */
    public static ArchitectureId fixedArchitectureId() {
        return ArchitectureId.of(200L);
    }

    // ==================== LayerType Fixtures ====================

    /**
     * 기본 소스 레이어 (DOMAIN)
     *
     * @return LayerType
     */
    public static LayerType defaultFromLayer() {
        return LayerType.DOMAIN;
    }

    /**
     * 기본 타겟 레이어 (APPLICATION)
     *
     * @return LayerType
     */
    public static LayerType defaultToLayer() {
        return LayerType.APPLICATION;
    }

    // ==================== DependencyType Fixtures ====================

    /**
     * 기본 의존성 타입 (ALLOWED)
     *
     * @return DependencyType
     */
    public static DependencyType defaultDependencyType() {
        return DependencyType.ALLOWED;
    }

    // ==================== ConditionDescription Fixtures ====================

    /**
     * 기본 조건 설명
     *
     * @return ConditionDescription
     */
    public static ConditionDescription defaultConditionDescription() {
        return ConditionDescription.of("기본 조건 설명");
    }

    /**
     * 빈 조건 설명
     *
     * @return ConditionDescription
     */
    public static ConditionDescription emptyConditionDescription() {
        return ConditionDescription.empty();
    }
}
