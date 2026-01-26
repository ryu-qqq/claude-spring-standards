package com.ryuqq.adapter.out.persistence.techstack.adapter;

import com.ryuqq.adapter.out.persistence.techstack.entity.TechStackJpaEntity;
import com.ryuqq.adapter.out.persistence.techstack.mapper.TechStackEntityMapper;
import com.ryuqq.adapter.out.persistence.techstack.repository.TechStackJpaRepository;
import com.ryuqq.application.techstack.port.out.TechStackCommandPort;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import org.springframework.stereotype.Component;

/**
 * TechStackCommandAdapter - TechStack 명령 어댑터
 *
 * <p>TechStackCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 */
@Component
public class TechStackCommandAdapter implements TechStackCommandPort {

    private final TechStackJpaRepository repository;
    private final TechStackEntityMapper mapper;

    public TechStackCommandAdapter(
            TechStackJpaRepository repository, TechStackEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * TechStack 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param techStack 영속화할 TechStack
     * @return 영속화된 TechStack ID
     */
    @Override
    public Long persist(TechStack techStack) {
        TechStackJpaEntity entity = mapper.toEntity(techStack);
        TechStackJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
