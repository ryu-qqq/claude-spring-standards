package com.ryuqq.adapter.out.persistence.architecture.adapter;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.adapter.out.persistence.architecture.mapper.ArchitectureEntityMapper;
import com.ryuqq.adapter.out.persistence.architecture.repository.ArchitectureJpaRepository;
import com.ryuqq.application.architecture.port.out.ArchitectureCommandPort;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import org.springframework.stereotype.Component;

/**
 * ArchitectureCommandAdapter - Architecture 명령 어댑터
 *
 * <p>ArchitectureCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ArchitectureCommandAdapter implements ArchitectureCommandPort {

    private final ArchitectureJpaRepository repository;
    private final ArchitectureEntityMapper mapper;

    public ArchitectureCommandAdapter(
            ArchitectureJpaRepository repository, ArchitectureEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Architecture 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param architecture 영속화할 Architecture
     * @return 영속화된 Architecture ID
     */
    @Override
    public Long persist(Architecture architecture) {
        ArchitectureJpaEntity entity = mapper.toEntity(architecture);
        ArchitectureJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
