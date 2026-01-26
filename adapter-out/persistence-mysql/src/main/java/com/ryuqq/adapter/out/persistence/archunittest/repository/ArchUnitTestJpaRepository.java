package com.ryuqq.adapter.out.persistence.archunittest.repository;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ArchUnitTestJpaRepository - ArchUnit 테스트 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface ArchUnitTestJpaRepository extends JpaRepository<ArchUnitTestJpaEntity, Long> {}
