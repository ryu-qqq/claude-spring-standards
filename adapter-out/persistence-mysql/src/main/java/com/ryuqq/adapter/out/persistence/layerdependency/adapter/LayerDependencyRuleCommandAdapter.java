package com.ryuqq.adapter.out.persistence.layerdependency.adapter;

import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import com.ryuqq.adapter.out.persistence.layerdependency.mapper.LayerDependencyRuleEntityMapper;
import com.ryuqq.adapter.out.persistence.layerdependency.repository.LayerDependencyRuleJpaRepository;
import com.ryuqq.application.layerdependency.port.out.LayerDependencyRuleCommandPort;
import com.ryuqq.domain.layerdependency.aggregate.LayerDependencyRule;
import com.ryuqq.domain.layerdependency.id.LayerDependencyRuleId;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleCommandAdapter - 레이어 의존성 규칙 명령 어댑터
 *
 * <p>LayerDependencyRuleCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class LayerDependencyRuleCommandAdapter implements LayerDependencyRuleCommandPort {

    private final LayerDependencyRuleJpaRepository repository;
    private final LayerDependencyRuleEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public LayerDependencyRuleCommandAdapter(
            LayerDependencyRuleJpaRepository repository, LayerDependencyRuleEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * LayerDependencyRule 영속화 (생성/수정)
     *
     * @param layerDependencyRule 영속화할 LayerDependencyRule
     * @return 영속화된 LayerDependencyRule ID
     */
    @Override
    public LayerDependencyRuleId persist(LayerDependencyRule layerDependencyRule) {
        LayerDependencyRuleJpaEntity entity = mapper.toEntity(layerDependencyRule);
        LayerDependencyRuleJpaEntity saved = repository.save(entity);
        return LayerDependencyRuleId.of(saved.getId());
    }

    /**
     * LayerDependencyRule 삭제 (실제 삭제)
     *
     * <p>Sub-resource이므로 Hard Delete를 수행합니다.
     *
     * @param layerDependencyRuleId 삭제할 LayerDependencyRule ID
     */
    @Override
    public void delete(LayerDependencyRuleId layerDependencyRuleId) {
        repository.deleteById(layerDependencyRuleId.value());
    }
}
