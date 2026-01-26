package com.ryuqq.adapter.out.persistence.architecture.repository;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ArchitectureJpaRepository - Architecture JPA Repository
 *
 * <p>Architecture 엔티티의 기본 CRUD를 제공합니다.
 *
 * <p><strong>주의사항:</strong>
 *
 * <ul>
 *   <li>Command 작업 전용입니다 (save, delete 등)
 *   <li>Query 작업은 ArchitectureQueryDslRepository를 사용합니다
 *   <li>QueryAdapter에서 이 Repository를 의존하면 안 됩니다
 * </ul>
 *
 * @author ryu-qqq
 */
public interface ArchitectureJpaRepository extends JpaRepository<ArchitectureJpaEntity, Long> {
    // JpaRepository의 기본 메서드만 사용 (save, delete, findById 등)
    // Query 작업은 ArchitectureQueryDslRepository에서 처리
}
