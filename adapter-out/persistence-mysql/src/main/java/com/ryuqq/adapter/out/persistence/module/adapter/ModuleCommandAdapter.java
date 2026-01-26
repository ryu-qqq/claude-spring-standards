package com.ryuqq.adapter.out.persistence.module.adapter;

import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
import com.ryuqq.adapter.out.persistence.module.mapper.ModuleJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.module.repository.ModuleJpaRepository;
import com.ryuqq.application.module.port.out.ModuleCommandPort;
import com.ryuqq.domain.module.aggregate.Module;
import org.springframework.stereotype.Component;

/**
 * ModuleCommandAdapter - Module 명령 어댑터
 *
 * <p>ModuleCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ModuleCommandAdapter implements ModuleCommandPort {

    private final ModuleJpaRepository repository;
    private final ModuleJpaEntityMapper mapper;

    public ModuleCommandAdapter(ModuleJpaRepository repository, ModuleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Module 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param module 영속화할 Module
     * @return 영속화된 Module ID
     */
    @Override
    public Long persist(Module module) {
        ModuleJpaEntity entity = mapper.toEntity(module);
        ModuleJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
