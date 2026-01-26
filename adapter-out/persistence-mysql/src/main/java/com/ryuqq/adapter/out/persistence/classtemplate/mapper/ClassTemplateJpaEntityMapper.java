package com.ryuqq.adapter.out.persistence.classtemplate.mapper;

import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.domain.classtemplate.aggregate.ClassTemplate;
import com.ryuqq.domain.classtemplate.id.ClassTemplateId;
import com.ryuqq.domain.classtemplate.vo.NamingPattern;
import com.ryuqq.domain.classtemplate.vo.TemplateCode;
import com.ryuqq.domain.classtemplate.vo.TemplateDescription;
import com.ryuqq.domain.classtype.id.ClassTypeId;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateJpaEntityMapper - ClassTemplate Entity <-> Domain 변환
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
public class ClassTemplateJpaEntityMapper {

    private static final String LIST_DELIMITER = ",";

    /**
     * 기본 생성자
     *
     * <p>ObjectMapper 의존성 없이 순수 Java로 구현합니다.
     */
    public ClassTemplateJpaEntityMapper() {}

    /**
     * JPA Entity -> Domain 변환
     *
     * <p>EMAP-005: toDomain(Entity) 메서드 필수
     *
     * <p>EMAP-007: Domain.reconstitute() 호출
     *
     * @param entity JPA 엔티티
     * @return ClassTemplate 도메인 객체
     */
    public ClassTemplate toDomain(ClassTemplateJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ClassTemplate.reconstitute(
                ClassTemplateId.of(entity.getId()),
                PackageStructureId.of(entity.getStructureId()),
                ClassTypeId.of(entity.getClassTypeId()),
                TemplateCode.of(entity.getTemplateCode()),
                mapNamingPattern(entity.getNamingPattern()),
                mapDescription(entity.getDescription()),
                parseJsonToList(entity.getRequiredAnnotations()),
                parseJsonToList(entity.getForbiddenAnnotations()),
                parseJsonToList(entity.getRequiredInterfaces()),
                parseJsonToList(entity.getForbiddenInheritance()),
                parseJsonToList(entity.getRequiredMethods()),
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
     * @param domain ClassTemplate 도메인 객체
     * @return JPA 엔티티
     */
    public ClassTemplateJpaEntity toEntity(ClassTemplate domain) {
        if (domain == null) {
            return null;
        }
        return ClassTemplateJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.structureIdValue(),
                domain.classTypeIdValue(),
                domain.templateCodeValue(),
                domain.namingPatternValue(),
                listToJson(domain.requiredAnnotations()),
                listToJson(domain.forbiddenAnnotations()),
                listToJson(domain.requiredInterfaces()),
                listToJson(domain.forbiddenInheritance()),
                listToJson(domain.requiredMethods()),
                domain.descriptionValue(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    /**
     * NamingPattern 매핑
     *
     * @param namingPattern 네이밍 패턴 문자열 (nullable)
     * @return NamingPattern 또는 null
     */
    private NamingPattern mapNamingPattern(String namingPattern) {
        return NamingPattern.of(namingPattern);
    }

    /**
     * Description 매핑
     *
     * @param description 설명 문자열 (nullable)
     * @return TemplateDescription 또는 null
     */
    private TemplateDescription mapDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return TemplateDescription.of(description);
    }

    /**
     * JSON 문자열 -> List<String> 변환
     *
     * <p>JSON 배열 형식 또는 쉼표 구분 문자열을 List로 변환합니다.
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
    private DeletionStatus mapDeletionStatus(ClassTemplateJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }
}
