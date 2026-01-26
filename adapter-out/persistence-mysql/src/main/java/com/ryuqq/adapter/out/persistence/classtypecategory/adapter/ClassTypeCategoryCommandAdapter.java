package com.ryuqq.adapter.out.persistence.classtypecategory.adapter;

import com.ryuqq.adapter.out.persistence.classtypecategory.entity.ClassTypeCategoryJpaEntity;
import com.ryuqq.adapter.out.persistence.classtypecategory.mapper.ClassTypeCategoryEntityMapper;
import com.ryuqq.adapter.out.persistence.classtypecategory.repository.ClassTypeCategoryJpaRepository;
import com.ryuqq.application.classtypecategory.port.out.ClassTypeCategoryCommandPort;
import com.ryuqq.domain.classtypecategory.aggregate.ClassTypeCategory;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryCommandAdapter - ClassTypeCategory 명령 어댑터
 *
 * <p>ClassTypeCategoryCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드만 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeCategoryCommandAdapter implements ClassTypeCategoryCommandPort {

    private final ClassTypeCategoryJpaRepository repository;
    private final ClassTypeCategoryEntityMapper mapper;

    public ClassTypeCategoryCommandAdapter(
            ClassTypeCategoryJpaRepository repository, ClassTypeCategoryEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ClassTypeCategory 영속화 (생성/수정/삭제)
     *
     * <p>Domain의 상태(isNew, isDeleted 등)에 따라 적절한 영속화를 수행합니다.
     *
     * @param classTypeCategory 영속화할 ClassTypeCategory
     * @return 영속화된 ClassTypeCategory ID
     */
    @Override
    public Long persist(ClassTypeCategory classTypeCategory) {
        ClassTypeCategoryJpaEntity entity = mapper.toEntity(classTypeCategory);
        ClassTypeCategoryJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
