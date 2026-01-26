package com.ryuqq.adapter.out.persistence.configfiletemplate.repository;

import com.ryuqq.adapter.out.persistence.configfiletemplate.entity.ConfigFileTemplateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ConfigFileTemplateJpaRepository - ConfigFileTemplate JPA Repository
 *
 * <p>ConfigFileTemplate 엔티티의 기본 CRUD를 제공합니다.
 *
 * <p><strong>주의사항:</strong>
 *
 * <ul>
 *   <li>Command 작업 전용입니다 (save, delete 등)
 *   <li>Query 작업은 ConfigFileTemplateQueryDslRepository를 사용합니다
 * </ul>
 *
 * @author ryu-qqq
 */
public interface ConfigFileTemplateJpaRepository
        extends JpaRepository<ConfigFileTemplateJpaEntity, Long> {
    // JpaRepository의 기본 메서드만 사용 (save, delete, findById 등)
    // Query 작업은 ConfigFileTemplateQueryDslRepository에서 처리
}
