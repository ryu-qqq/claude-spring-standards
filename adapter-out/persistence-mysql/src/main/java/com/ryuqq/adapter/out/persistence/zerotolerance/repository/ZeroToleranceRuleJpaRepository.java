package com.ryuqq.adapter.out.persistence.zerotolerance.repository;

import com.ryuqq.adapter.out.persistence.zerotolerance.entity.ZeroToleranceRuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ZeroToleranceRuleJpaRepository - Zero-Tolerance 규칙 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface ZeroToleranceRuleJpaRepository
        extends JpaRepository<ZeroToleranceRuleJpaEntity, Long> {

    /**
     * ruleId로 존재 여부 확인
     *
     * @param ruleId 코딩 규칙 ID
     * @return 존재하면 true
     */
    boolean existsByRuleId(Long ruleId);
}
