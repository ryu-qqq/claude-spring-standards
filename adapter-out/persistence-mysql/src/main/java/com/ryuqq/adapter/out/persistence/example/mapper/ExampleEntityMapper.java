package com.ryuqq.adapter.out.persistence.example.mapper;

import com.ryuqq.adapter.out.persistence.example.entity.ExampleJpaEntity;
import com.ryuqq.domain.example.ExampleDomain;
import com.ryuqq.domain.example.ExampleStatus;

import org.springframework.stereotype.Component;

/**
 * ExampleEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 Domain 객체 간 변환을 담당합니다.</p>
 *
 * <p><strong>변환 책임:</strong></p>
 * <ul>
 *   <li>ExampleDomain → ExampleJpaEntity (저장용)</li>
 *   <li>ExampleJpaEntity → ExampleDomain (조회용)</li>
 *   <li>Value Object 추출 및 재구성</li>
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong></p>
 * <ul>
 *   <li>Adapter Layer의 책임</li>
 *   <li>Domain과 Infrastructure 기술 분리</li>
 *   <li>Domain은 JPA 의존성 없음</li>
 * </ul>
 *
 * <p><strong>불변성 원칙:</strong></p>
 * <ul>
 *   <li>Domain 객체는 항상 불변 (record)</li>
 *   <li>Entity는 JPA 제약으로 가변</li>
 *   <li>변환 시 새로운 인스턴스 생성</li>
 * </ul>
 *
 * <p><strong>ExampleStatus Enum 공유:</strong></p>
 * <ul>
 *   <li>Domain과 Entity가 동일한 ExampleStatus enum 사용</li>
 *   <li>별도 변환 로직 불필요 (직접 전달)</li>
 *   <li>코드 단순화 및 일관성 유지</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Component
public class ExampleEntityMapper {

    /**
     * Domain → Entity 변환
     *
     * <p><strong>사용 시나리오:</strong></p>
     * <ul>
     *   <li>신규 Example 저장 (ID가 null)</li>
     *   <li>기존 Example 수정 (ID가 있음)</li>
     * </ul>
     *
     * <p><strong>변환 규칙:</strong></p>
     * <ul>
     *   <li>ID: Domain.getId() → Entity.id</li>
     *   <li>Message: Domain.getMessage() → Entity.message</li>
     *   <li>Status: Domain.status (ExampleStatus) → Entity.status (ExampleStatus, 직접 전달)</li>
     *   <li>CreatedAt: Domain.getCreatedAt() → Entity.createdAt</li>
     *   <li>UpdatedAt: Domain.getUpdatedAt() → Entity.updatedAt</li>
     * </ul>
     *
     * @param domain Example 도메인
     * @return ExampleJpaEntity
     */
    public ExampleJpaEntity toEntity(ExampleDomain domain) {
        return new ExampleJpaEntity(
            domain.getId(),
            domain.getMessage(),
            domain.status(),  // ExampleStatus enum 직접 전달
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }

    /**
     * Entity → Domain 변환
     *
     * <p><strong>사용 시나리오:</strong></p>
     * <ul>
     *   <li>데이터베이스에서 조회한 Entity를 Domain으로 변환</li>
     *   <li>Application Layer로 전달</li>
     * </ul>
     *
     * <p><strong>변환 규칙:</strong></p>
     * <ul>
     *   <li>ID: Entity.id → Domain.ExampleId</li>
     *   <li>Message: Entity.message → Domain.ExampleContent</li>
     *   <li>Status: Entity.status (ExampleStatus) → Domain.status (ExampleStatus, 직접 전달)</li>
     *   <li>CreatedAt/UpdatedAt: Entity → Domain.ExampleAudit</li>
     * </ul>
     *
     * @param entity ExampleJpaEntity
     * @return Example 도메인
     */
    public ExampleDomain toDomain(ExampleJpaEntity entity) {
        return ExampleDomain.of(
            entity.getId(),
            entity.getMessage(),
            entity.getStatus().asString(),  // ExampleStatus enum → String
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
