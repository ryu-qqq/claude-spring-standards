package com.ryuqq.adapter.out.persistence.classtypecategory.repository;

import com.ryuqq.adapter.out.persistence.classtypecategory.entity.ClassTypeCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ClassTypeCategoryJpaRepository - ClassTypeCategory JPA Repository
 *
 * <p>ClassTypeCategory 엔티티의 기본 CRUD를 담당합니다.
 *
 * <p>CommandAdapter에서만 사용됩니다 (QueryAdapter 사용 금지).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ClassTypeCategoryJpaRepository
        extends JpaRepository<ClassTypeCategoryJpaEntity, Long> {}
