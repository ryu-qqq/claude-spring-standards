package com.ryuqq.adapter.out.persistence.packagepurpose.repository;

import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackagePurposeJpaRepository - 패키지 목적 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * @author ryu-qqq
 */
public interface PackagePurposeJpaRepository extends JpaRepository<PackagePurposeJpaEntity, Long> {}
