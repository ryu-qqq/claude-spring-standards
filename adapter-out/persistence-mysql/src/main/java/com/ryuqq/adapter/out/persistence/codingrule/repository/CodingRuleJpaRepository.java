package com.ryuqq.adapter.out.persistence.codingrule.repository;

import com.ryuqq.adapter.out.persistence.codingrule.entity.CodingRuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CodingRuleJpaRepository - 코딩 규칙 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface CodingRuleJpaRepository extends JpaRepository<CodingRuleJpaEntity, Long> {}
