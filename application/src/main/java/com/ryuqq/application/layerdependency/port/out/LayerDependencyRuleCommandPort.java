package com.ryuqq.application.layerdependency.port.out;

import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;

/**
 * LayerDependencyRuleCommandPort - 레이어 의존성 규칙 명령 Port
 *
 * <p>영속성 계층으로의 LayerDependencyRule CUD 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
public interface LayerDependencyRuleCommandPort {

    /**
     * LayerDependencyRule 영속화 (생성/수정)
     *
     * @param layerDependencyRule 영속화할 LayerDependencyRule
     * @return 영속화된 LayerDependencyRule ID
     */
    LayerDependencyRuleId persist(LayerDependencyRule layerDependencyRule);

    /**
     * LayerDependencyRule 삭제 (실제 삭제)
     *
     * <p>Sub-resource이므로 Hard Delete를 수행합니다.
     *
     * @param layerDependencyRuleId 삭제할 LayerDependencyRule ID
     */
    void delete(LayerDependencyRuleId layerDependencyRuleId);
}
