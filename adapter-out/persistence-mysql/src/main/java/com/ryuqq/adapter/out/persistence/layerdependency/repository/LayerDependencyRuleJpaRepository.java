package com.ryuqq.adapter.out.persistence.layerdependency.repository;

import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LayerDependencyRuleJpaRepository - 레이어 의존성 규칙 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface LayerDependencyRuleJpaRepository
        extends JpaRepository<LayerDependencyRuleJpaEntity, Long> {}
