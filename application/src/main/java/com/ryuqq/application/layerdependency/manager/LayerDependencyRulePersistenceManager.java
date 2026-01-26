package com.ryuqq.application.layerdependency.manager;

import com.ryuqq.application.layerdependency.port.out.LayerDependencyRuleCommandPort;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * LayerDependencyRulePersistenceManager - 레이어 의존성 규칙 영속화 관리자
 *
 * <p>레이어 의존성 규칙 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class LayerDependencyRulePersistenceManager {

    private final LayerDependencyRuleCommandPort layerDependencyRuleCommandPort;

    public LayerDependencyRulePersistenceManager(
            LayerDependencyRuleCommandPort layerDependencyRuleCommandPort) {
        this.layerDependencyRuleCommandPort = layerDependencyRuleCommandPort;
    }

    /**
     * 레이어 의존성 규칙 영속화 (생성 또는 수정)
     *
     * @param layerDependencyRule 영속화할 레이어 의존성 규칙
     * @return 영속화된 레이어 의존성 규칙 ID
     */
    @Transactional
    public LayerDependencyRuleId persist(LayerDependencyRule layerDependencyRule) {
        return layerDependencyRuleCommandPort.persist(layerDependencyRule);
    }

    /**
     * 레이어 의존성 규칙 삭제 (실제 삭제)
     *
     * @param layerDependencyRuleId 삭제할 레이어 의존성 규칙 ID
     */
    @Transactional
    public void delete(LayerDependencyRuleId layerDependencyRuleId) {
        layerDependencyRuleCommandPort.delete(layerDependencyRuleId);
    }
}
