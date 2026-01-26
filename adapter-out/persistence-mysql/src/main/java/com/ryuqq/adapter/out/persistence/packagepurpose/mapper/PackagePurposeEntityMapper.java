package com.ryuqq.adapter.out.persistence.packagepurpose.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagepurpose.aggregate.PackagePurpose;
import com.ryuqq.domain.packagepurpose.id.PackagePurposeId;
import com.ryuqq.domain.packagepurpose.vo.AllowedClassTypes;
import com.ryuqq.domain.packagepurpose.vo.NamingPattern;
import com.ryuqq.domain.packagepurpose.vo.NamingSuffix;
import com.ryuqq.domain.packagepurpose.vo.PurposeCode;
import com.ryuqq.domain.packagepurpose.vo.PurposeName;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeEntityMapper - PackagePurpose Entity <-> Domain 변환
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
public class PackagePurposeEntityMapper {

    private final ObjectMapper objectMapper;

    public PackagePurposeEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return PackagePurpose 도메인 객체
     */
    public PackagePurpose toDomain(PackagePurposeJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PackagePurpose.reconstitute(
                PackagePurposeId.of(entity.getId()),
                PackageStructureId.of(entity.getStructureId()),
                PurposeCode.of(entity.getCode()),
                PurposeName.of(entity.getName()),
                entity.getDescription(),
                parseAllowedClassTypes(entity.getDefaultAllowedClassTypes()),
                parseNamingPattern(entity.getDefaultNamingPattern()),
                parseNamingSuffix(entity.getDefaultNamingSuffix()),
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
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain PackagePurpose 도메인 객체
     * @return JPA 엔티티
     */
    public PackagePurposeJpaEntity toEntity(PackagePurpose domain) {
        if (domain == null) {
            return null;
        }
        return PackagePurposeJpaEntity.of(
                domain.idValue(),
                domain.structureIdValue(),
                domain.codeValue(),
                domain.nameValue(),
                domain.description(),
                serializeAllowedClassTypes(domain.defaultAllowedClassTypes()),
                domain.defaultNamingPatternValue(),
                domain.defaultNamingSuffixValue(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * JSON 문자열 -> AllowedClassTypes 변환
     *
     * @param json JSON 문자열
     * @return AllowedClassTypes 객체
     */
    private AllowedClassTypes parseAllowedClassTypes(String json) {
        if (json == null || json.isBlank()) {
            return AllowedClassTypes.empty();
        }
        try {
            List<String> values =
                    objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return AllowedClassTypes.of(values);
        } catch (JsonProcessingException e) {
            return AllowedClassTypes.empty();
        }
    }

    /**
     * AllowedClassTypes -> JSON 문자열 변환
     *
     * @param allowedClassTypes AllowedClassTypes 객체
     * @return JSON 문자열
     */
    private String serializeAllowedClassTypes(AllowedClassTypes allowedClassTypes) {
        if (allowedClassTypes == null || allowedClassTypes.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(allowedClassTypes.values());
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 문자열 -> NamingPattern 변환
     *
     * @param pattern 패턴 문자열
     * @return NamingPattern 객체
     */
    private NamingPattern parseNamingPattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return NamingPattern.empty();
        }
        return NamingPattern.of(pattern);
    }

    /**
     * 문자열 -> NamingSuffix 변환
     *
     * @param suffix 접미사 문자열
     * @return NamingSuffix 객체
     */
    private NamingSuffix parseNamingSuffix(String suffix) {
        if (suffix == null || suffix.isBlank()) {
            return NamingSuffix.empty();
        }
        return NamingSuffix.of(suffix);
    }

    /**
     * Entity의 삭제 상태 -> DeletionStatus 변환
     *
     * <p>EMAP-008: Null 안전 처리
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(PackagePurposeJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
