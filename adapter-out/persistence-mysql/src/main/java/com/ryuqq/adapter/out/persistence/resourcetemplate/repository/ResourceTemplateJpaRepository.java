package com.ryuqq.adapter.out.persistence.resourcetemplate.repository;

import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ResourceTemplateJpaRepository - 리소스 템플릿 JPA 레포지토리
 *
 * <p>Spring Data JPA를 통한 기본 CRUD를 제공합니다.
 *
 * <p>CQRS Command 전용 Repository입니다. 저장/삭제만 수행합니다.
 *
 * <p><strong>CQRS 분리 원칙:</strong>
 *
 * <ul>
 *   <li>Command 전용: save(), delete() 만 사용
 *   <li>Query Method 금지: findByXxx() 정의 금지
 *   <li>@Query 금지: JPQL/Native Query 금지
 *   <li>조회는 QueryDslRepository에서 수행
 * </ul>
 *
 * @author ryu-qqq
 */
public interface ResourceTemplateJpaRepository
        extends JpaRepository<ResourceTemplateJpaEntity, Long> {
    // Command 전용: 상속받은 save(), delete() 만 사용
    // 조회 메서드 정의 금지 - QueryDslRepository 사용
}
