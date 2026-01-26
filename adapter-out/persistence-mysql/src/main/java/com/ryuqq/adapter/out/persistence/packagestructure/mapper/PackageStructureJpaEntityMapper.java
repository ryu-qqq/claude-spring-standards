package com.ryuqq.adapter.out.persistence.packagestructure.mapper;

import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.module.id.ModuleId;
import com.ryuqq.domain.packagepurpose.vo.AllowedClassTypes;
import com.ryuqq.domain.packagepurpose.vo.NamingPattern;
import com.ryuqq.domain.packagepurpose.vo.NamingSuffix;
import com.ryuqq.domain.packagestructure.aggregate.PackageStructure;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import com.ryuqq.domain.packagestructure.vo.PathPattern;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PackageStructureJpaEntityMapper - PackageStructure Entity <-> Domain 변환
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
public class PackageStructureJpaEntityMapper {

    private static final String LIST_DELIMITER = ",";

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public PackageStructureJpaEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return PackageStructure 도메인 객체
     */
    public PackageStructure toDomain(PackageStructureJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PackageStructure.reconstitute(
                PackageStructureId.of(entity.getId()),
                ModuleId.of(entity.getModuleId()),
                PathPattern.of(entity.getPathPattern()),
                parseJsonToAllowedClassTypes(entity.getAllowedClassTypes()),
                mapNamingPattern(entity.getNamingPattern()),
                mapNamingSuffix(entity.getNamingSuffix()),
                entity.getDescription(),
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
     * @param domain PackageStructure 도메인 객체
     * @return JPA 엔티티
     */
    public PackageStructureJpaEntity toEntity(PackageStructure domain) {
        if (domain == null) {
            return null;
        }
        return PackageStructureJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.moduleIdValue(),
                domain.pathPatternValue(),
                allowedClassTypesToJson(domain.allowedClassTypes()),
                domain.namingPatternValue(),
                domain.namingSuffixValue(),
                domain.description(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * NamingPattern 매핑
     *
     * @param namingPattern 네이밍 패턴 문자열 (nullable)
     * @return NamingPattern 또는 empty
     */
    private NamingPattern mapNamingPattern(String namingPattern) {
        if (namingPattern == null || namingPattern.isBlank()) {
            return NamingPattern.empty();
        }
        return NamingPattern.of(namingPattern);
    }

    /**
     * NamingSuffix 매핑
     *
     * @param namingSuffix 네이밍 접미사 문자열 (nullable)
     * @return NamingSuffix 또는 empty
     */
    private NamingSuffix mapNamingSuffix(String namingSuffix) {
        if (namingSuffix == null || namingSuffix.isBlank()) {
            return NamingSuffix.empty();
        }
        return NamingSuffix.of(namingSuffix);
    }

    /**
     * JSON 문자열 -> AllowedClassTypes 변환
     *
     * <p>JSON 배열 형식 또는 쉼표 구분 문자열을 AllowedClassTypes로 변환합니다.
     *
     * @param json JSON 문자열 (nullable)
     * @return AllowedClassTypes
     */
    private AllowedClassTypes parseJsonToAllowedClassTypes(String json) {
        if (json == null || json.isBlank()) {
            return AllowedClassTypes.empty();
        }
        List<String> values = parseJsonToList(json);
        return AllowedClassTypes.of(values);
    }

    /**
     * JSON 문자열 -> List<String> 변환
     *
     * @param json JSON 문자열 (nullable)
     * @return List<String>
     */
    private List<String> parseJsonToList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        // JSON 배열 형식 처리: ["item1", "item2"]
        String cleaned = json.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        if (cleaned.isBlank()) {
            return List.of();
        }
        // 쉼표로 분리하고 따옴표 제거
        return Arrays.stream(cleaned.split(LIST_DELIMITER))
                .map(String::trim)
                .map(s -> s.replace("\"", ""))
                .filter(s -> !s.isBlank())
                .toList();
    }

    /**
     * AllowedClassTypes -> JSON 문자열 변환
     *
     * @param allowedClassTypes AllowedClassTypes
     * @return JSON 배열 문자열 또는 null
     */
    private String allowedClassTypesToJson(AllowedClassTypes allowedClassTypes) {
        if (allowedClassTypes == null || allowedClassTypes.isEmpty()) {
            return null;
        }
        return listToJson(allowedClassTypes.values());
    }

    /**
     * List<String> -> JSON 문자열 변환
     *
     * @param list 문자열 리스트
     * @return JSON 배열 문자열 또는 null
     */
    private String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"").append(list.get(i)).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Entity의 삭제 상태 -> DeletionStatus 변환
     *
     * <p>EMAP-008: Null 안전 처리
     *
     * @param entity JPA 엔티티
     * @return DeletionStatus 객체
     */
    private DeletionStatus mapDeletionStatus(PackageStructureJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
