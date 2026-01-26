package com.ryuqq.adapter.out.persistence.convention.adapter;

import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import com.ryuqq.adapter.out.persistence.convention.mapper.ConventionJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.convention.repository.ConventionJpaRepository;
import com.ryuqq.application.convention.port.out.ConventionCommandPort;
import com.ryuqq.domain.convention.aggregate.Convention;
import org.springframework.stereotype.Component;

/**
 * ConventionCommandAdapter - Convention 명령 어댑터
 *
 * <p>ConventionCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ConventionCommandAdapter implements ConventionCommandPort {

    private final ConventionJpaRepository repository;
    private final ConventionJpaEntityMapper mapper;

    public ConventionCommandAdapter(
            ConventionJpaRepository repository, ConventionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Convention 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param convention 영속화할 Convention
     * @return 영속화된 Convention ID
     */
    @Override
    public Long persist(Convention convention) {
        ConventionJpaEntity entity = mapper.toEntity(convention);
        ConventionJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
