package com.ryuqq.adapter.out.persistence.classtype.adapter;

import com.ryuqq.adapter.out.persistence.classtype.entity.ClassTypeJpaEntity;
import com.ryuqq.adapter.out.persistence.classtype.mapper.ClassTypeEntityMapper;
import com.ryuqq.adapter.out.persistence.classtype.repository.ClassTypeJpaRepository;
import com.ryuqq.application.classtype.port.out.ClassTypeCommandPort;
import com.ryuqq.domain.classtype.aggregate.ClassType;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCommandAdapter - ClassType 명령 어댑터
 *
 * <p>ClassTypeCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeCommandAdapter implements ClassTypeCommandPort {

    private final ClassTypeJpaRepository repository;
    private final ClassTypeEntityMapper mapper;

    public ClassTypeCommandAdapter(
            ClassTypeJpaRepository repository, ClassTypeEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ClassType 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param classType 영속화할 ClassType
     * @return 영속화된 ClassType ID
     */
    @Override
    public Long persist(ClassType classType) {
        ClassTypeJpaEntity entity = mapper.toEntity(classType);
        ClassTypeJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
