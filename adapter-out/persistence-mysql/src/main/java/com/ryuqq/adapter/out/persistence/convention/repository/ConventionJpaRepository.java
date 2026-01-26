package com.ryuqq.adapter.out.persistence.convention.repository;

import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ConventionJpaRepository - Convention JPA Repository
 *
 * <p>Convention 엔티티의 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ConventionJpaRepository extends JpaRepository<ConventionJpaEntity, Long> {}
