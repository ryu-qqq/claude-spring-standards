package com.ryuqq.adapter.out.persistence.example.repository;

import com.ryuqq.adapter.out.persistence.example.entity.ExampleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * ExampleRepository - Example JPA Repository
 *
 * <p>Spring Data JPA Repository로서 Example Entity의 데이터베이스 접근을 담당합니다.</p>
 *
 * <p><strong>QueryDSL 지원:</strong></p>
 * <ul>
 *   <li>QuerydslPredicateExecutor 상속으로 타입 안전 쿼리 지원</li>
 *   <li>복잡한 조회 조건을 타입 안전하게 구성</li>
 *   <li>동적 쿼리 작성 용이</li>
 * </ul>
 *
 * <p><strong>Spring Data JPA 기능:</strong></p>
 * <ul>
 *   <li>기본 CRUD 메서드 자동 제공</li>
 *   <li>Query Method 지원</li>
 *   <li>@Query 어노테이션 지원</li>
 * </ul>
 *
 * <p><strong>타입 파라미터 순서:</strong></p>
 * <ul>
 *   <li>첫 번째: Entity 타입 (ExampleJpaEntity)</li>
 *   <li>두 번째: ID 타입 (Long)</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
public interface ExampleRepository extends
    JpaRepository<ExampleJpaEntity, Long>,
    QuerydslPredicateExecutor<ExampleJpaEntity> {
}
