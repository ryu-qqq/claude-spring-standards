package com.ryuqq.adapter.out.persistence.techstack.mapper;

import com.ryuqq.adapter.out.persistence.config.PersistenceObjectMapper;
import com.ryuqq.adapter.out.persistence.techstack.entity.TechStackJpaEntity;
import com.ryuqq.domain.common.vo.DeletionStatus;
import com.ryuqq.domain.common.vo.ReferenceLinks;
import com.ryuqq.domain.techstack.aggregate.TechStack;
import com.ryuqq.domain.techstack.id.TechStackId;
import com.ryuqq.domain.techstack.vo.BuildConfigFile;
import com.ryuqq.domain.techstack.vo.BuildToolType;
import com.ryuqq.domain.techstack.vo.FrameworkModules;
import com.ryuqq.domain.techstack.vo.FrameworkType;
import com.ryuqq.domain.techstack.vo.FrameworkVersion;
import com.ryuqq.domain.techstack.vo.LanguageFeatures;
import com.ryuqq.domain.techstack.vo.LanguageType;
import com.ryuqq.domain.techstack.vo.LanguageVersion;
import com.ryuqq.domain.techstack.vo.PlatformType;
import com.ryuqq.domain.techstack.vo.RuntimeEnvironment;
import com.ryuqq.domain.techstack.vo.TechStackName;
import com.ryuqq.domain.techstack.vo.TechStackStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TechStackEntityMapper - TechStack Entity ↔ Domain 변환
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
public class TechStackEntityMapper {

    private final PersistenceObjectMapper objectMapper;

    public TechStackEntityMapper(PersistenceObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * JPA Entity -> Domain 변환
     *
     * @param entity JPA 엔티티 (null 허용)
     * @return TechStack 도메인 객체, 입력이 null이면 null 반환
     */
    public TechStack toDomain(TechStackJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return TechStack.reconstitute(
                TechStackId.of(entity.getId()),
                TechStackName.of(entity.getName()),
                TechStackStatus.valueOf(entity.getStatus()),
                LanguageType.valueOf(entity.getLanguageType()),
                LanguageVersion.of(entity.getLanguageVersion()),
                parseLanguageFeatures(entity.getLanguageFeatures()),
                FrameworkType.valueOf(entity.getFrameworkType()),
                FrameworkVersion.of(entity.getFrameworkVersion()),
                parseFrameworkModules(entity.getFrameworkModules()),
                PlatformType.valueOf(entity.getPlatformType()),
                RuntimeEnvironment.valueOf(entity.getRuntimeEnvironment()),
                BuildToolType.valueOf(entity.getBuildToolType()),
                BuildConfigFile.of(entity.getBuildConfigFile()),
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
     * @param domain TechStack 도메인 객체 (null 허용)
     * @return JPA 엔티티, 입력이 null이면 null 반환
     */
    public TechStackJpaEntity toEntity(TechStack domain) {
        if (domain == null) {
            return null;
        }
        return TechStackJpaEntity.of(
                domain.isNew() ? null : domain.idValue(),
                domain.nameValue(),
                domain.statusName(),
                domain.languageTypeName(),
                domain.languageVersionValue(),
                toJsonArray(domain.languageFeatures().values()),
                domain.frameworkTypeName(),
                domain.frameworkVersionValue(),
                toJsonArray(domain.frameworkModules().values()),
                domain.platformTypeName(),
                domain.runtimeEnvironmentName(),
                domain.buildToolTypeName(),
                domain.buildConfigFileValue(),
                toJsonArray(domain.referenceLinkValues()),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    private DeletionStatus mapDeletionStatus(TechStackJpaEntity entity) {
        if (entity.getDeletedAt() != null) {
            return DeletionStatus.deletedAt(entity.getDeletedAt());
        }
        return DeletionStatus.active();
    }

    private LanguageFeatures parseLanguageFeatures(String json) {
        List<String> values = objectMapper.readValueAsStringList(json);
        return LanguageFeatures.of(values);
    }

    private FrameworkModules parseFrameworkModules(String json) {
        List<String> values = objectMapper.readValueAsStringList(json);
        return FrameworkModules.of(values);
    }

    private ReferenceLinks parseReferenceLinks(String json) {
        List<String> values = objectMapper.readValueAsStringList(json);
        return ReferenceLinks.of(values);
    }

    private String toJsonArray(List<String> list) {
        return objectMapper.writeValueAsString(list);
    }
}
