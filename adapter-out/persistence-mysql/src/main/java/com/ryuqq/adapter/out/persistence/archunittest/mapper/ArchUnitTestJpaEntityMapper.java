package com.ryuqq.adapter.out.persistence.archunittest.mapper;

import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.domain.archunittest.aggregate.ArchUnitTest;
import com.ryuqq.domain.archunittest.id.ArchUnitTestId;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestDescription;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestName;
import com.ryuqq.domain.archunittest.vo.ArchUnitTestSeverity;
import com.ryuqq.domain.archunittest.vo.TestCode;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestJpaEntityMapper - ArchUnitTest Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * <p>EMAP-002: Pure Java만 사용 (Lombok/MapStruct 금지)
 *
 * <p>EMAP-003: 시간 필드 생성 금지 (Instant.now() 금지)
 *
 * @author ryu-qqq
 */
@Component
public class ArchUnitTestJpaEntityMapper {

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public ArchUnitTestJpaEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return ArchUnitTest 도메인 객체
     */
    public ArchUnitTest toDomain(ArchUnitTestJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ArchUnitTest.reconstitute(
                ArchUnitTestId.of(entity.getId()),
                PackageStructureId.of(entity.getStructureId()),
                entity.getCode(),
                ArchUnitTestName.of(entity.getName()),
                mapDescription(entity.getDescription()),
                entity.getTestClassName(),
                entity.getTestMethodName(),
                TestCode.of(entity.getTestCode()),
                mapSeverity(entity.getSeverity()),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain -> JPA Entity 변환
     *
     * <p>EMAP-004: toEntity(Domain) 메서드 필수
     *
     * <p>EMAP-006: Entity.of() 호출
     *
     * @param domain ArchUnitTest 도메인 객체
     * @return JPA 엔티티
     */
    /**
     * Domain -> JPA Entity 변환
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain ArchUnitTest 도메인 객체
     * @return JPA 엔티티
     */
    public ArchUnitTestJpaEntity toEntity(ArchUnitTest domain) {
        if (domain == null) {
            return null;
        }
        return ArchUnitTestJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.structureIdValue(),
                domain.code(),
                domain.nameValue(),
                domain.descriptionValue(),
                domain.testClassName(),
                domain.testMethodName(),
                domain.testCodeValue(),
                domain.severity() != null ? domain.severity().name() : null,
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private ArchUnitTestSeverity mapSeverity(String severity) {
        if (severity == null || severity.isBlank()) {
            return null;
        }
        return ArchUnitTestSeverity.valueOf(severity);
    }

    /**
     * Description 매핑
     *
     * @param description 설명 문자열 (nullable)
     * @return ArchUnitTestDescription 또는 null
     */
    private ArchUnitTestDescription mapDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return ArchUnitTestDescription.of(description);
    }

    /**
     * Entity의 삭제 상태 -> DeletionStatus 변환
     *
     * <p>EMAP-008: Null 안전 처리
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(ArchUnitTestJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
