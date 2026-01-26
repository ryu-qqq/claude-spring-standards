package com.ryuqq.adapter.out.persistence.classtype.repository;

import com.ryuqq.adapter.out.persistence.classtype.entity.ClassTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ClassTypeJpaRepository - ClassType JPA Repository
 *
 * <p>ClassType 엔티티의 기본 CRUD를 담당합니다.
 *
 * <p>CommandAdapter에서만 사용됩니다 (QueryAdapter 사용 금지).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ClassTypeJpaRepository extends JpaRepository<ClassTypeJpaEntity, Long> {}
