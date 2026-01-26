package com.ryuqq.adapter.out.persistence.architecture.mapper;

import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.adapter.out.persistence.config.PersistenceObjectMapper;
import com.ryuqq.domain.architecture.aggregate.Architecture;
import com.ryuqq.domain.architecture.id.ArchitectureId;
import com.ryuqq.domain.architecture.vo.ArchitectureName;
import com.ryuqq.domain.architecture.vo.PatternDescription;
import com.ryuqq.domain.architecture.vo.PatternPrinciples;
import com.ryuqq.domain.architecture.vo.PatternType;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ArchitectureEntityMapper - Architecture Entity <-> Domain 변환
 *
 * <p>JPA 엔티티와 도메인 객체 간 변환을 담당합니다.
 *
 * <p><strong>JSON 처리:</strong>
 *
 * <ul>
 *   <li>PersistenceObjectMapper 래퍼를 통해 JSON 파싱/직렬화 수행
 *   <li>에러 처리는 PersistenceObjectMapper에서 중앙 관리
 * </ul>
 *
 * @author ryu-qqq
 */
@Component
public class ArchitectureEntityMapper {

    private final PersistenceObjectMapper objectMapper;

    public ArchitectureEntityMapper(PersistenceObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * JPA Entity -> Domain 변환
     *
     * @param entity JPA 엔티티 (null 허용)
     * @return Architecture 도메인 객체, 입력이 null이면 null 반환
     */
    public Architecture toDomain(ArchitectureJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Architecture.reconstitute(
                ArchitectureId.of(entity.getId()),
                TechStackId.of(entity.getTechStackId()),
                ArchitectureName.of(entity.getName()),
                PatternType.valueOf(entity.getPatternType()),
                parsePatternDescription(entity.getPatternDescription()),
                parsePatternPrinciples(entity.getPatternPrinciples()),
                parseReferenceLinks(entity.getReferenceLinks()),
                mapDeletionStatus(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Domain -> JPA Entity 변환
     *
     * <p>AGG-014: Law of Demeter 준수 - 위임 메서드 사용 (체이닝 금지)
     *
     * @param domain Architecture 도메인 객체
     * @return JPA 엔티티
     */
    public ArchitectureJpaEntity toEntity(Architecture domain) {
        if (domain == null) {
            return null;
        }
        return ArchitectureJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.techStackIdValue(),
                domain.nameValue(),
                domain.patternTypeName(),
                domain.patternDescriptionValue(),
                toJsonArray(domain.patternPrinciples().values()),
                toJsonArray(domain.referenceLinkValues()),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private DeletionStatus mapDeletionStatus(ArchitectureJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }

    private PatternDescription parsePatternDescription(String value) {
        if (value == null || value.isBlank()) {
            return PatternDescription.empty();
        }
        return PatternDescription.of(value);
    }

    private PatternPrinciples parsePatternPrinciples(String json) {
        List<String> values = objectMapper.readValueAsStringList(json);
        return PatternPrinciples.of(values);
    }

    private ReferenceLinks parseReferenceLinks(String json) {
        List<String> values = objectMapper.readValueAsStringList(json);
        return ReferenceLinks.of(values);
    }

    private String toJsonArray(List<String> list) {
        return objectMapper.writeValueAsString(list);
    }
}
