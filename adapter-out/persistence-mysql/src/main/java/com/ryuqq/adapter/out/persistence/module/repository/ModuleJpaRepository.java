package com.ryuqq.adapter.out.persistence.module.repository;

import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ModuleJpaRepository - Module JPA Repository
 *
 * <p>Module 엔티티의 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ModuleJpaRepository extends JpaRepository<ModuleJpaEntity, Long> {}
