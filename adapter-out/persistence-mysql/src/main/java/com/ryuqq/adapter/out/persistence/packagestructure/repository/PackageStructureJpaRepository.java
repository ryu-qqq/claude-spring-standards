package com.ryuqq.adapter.out.persistence.packagestructure.repository;

import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageStructureJpaRepository - 패키지 구조 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface PackageStructureJpaRepository
        extends JpaRepository<PackageStructureJpaEntity, Long> {}
