package com.ryuqq.adapter.out.persistence.layer.adapter;

import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import com.ryuqq.adapter.out.persistence.layer.mapper.LayerEntityMapper;
import com.ryuqq.adapter.out.persistence.layer.repository.LayerJpaRepository;
import com.ryuqq.application.layer.port.out.LayerCommandPort;
import com.ryuqq.domain.layer.aggregate.Layer;
import org.springframework.stereotype.Component;

/**
 * LayerCommandAdapter - Layer 명령 어댑터
 *
 * <p>LayerCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
@Component
public class LayerCommandAdapter implements LayerCommandPort {

    private final LayerJpaRepository repository;
    private final LayerEntityMapper mapper;

    public LayerCommandAdapter(LayerJpaRepository repository, LayerEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Layer 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param layer 영속화할 Layer
     * @return 영속화된 Layer ID
     */
    @Override
    public Long persist(Layer layer) {
        LayerJpaEntity entity = mapper.toEntity(layer);
        LayerJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
