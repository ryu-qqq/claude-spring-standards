package com.ryuqq.adapter.out.persistence.configfiletemplate.repository;

import static com.ryuqq.adapter.out.persistence.configfiletemplate.entity.QConfigFileTemplateJpaEntity.configFileTemplateJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.configfiletemplate.condition.ConfigFileTemplateConditionBuilder;
import com.ryuqq.adapter.out.persistence.configfiletemplate.entity.ConfigFileTemplateJpaEntity;
import com.ryuqq.domain.configfiletemplate.query.ConfigFileTemplateSliceCriteria;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ConfigFileTemplateQueryDslRepository - ConfigFileTemplate QueryDSL Repository
 *
 * <p>ConfigFileTemplate 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
@SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Spring Bean injection - immutable after construction")
public class ConfigFileTemplateQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ConfigFileTemplateConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ConfigFileTemplateQueryDslRepository(
            JPAQueryFactory queryFactory, ConfigFileTemplateConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 ConfigFileTemplate 조회
     *
     * @param id ConfigFileTemplate ID
     * @return ConfigFileTemplate 엔티티 (Optional)
     */
    public Optional<ConfigFileTemplateJpaEntity> findById(Long id) {
        ConfigFileTemplateJpaEntity result =
                queryFactory
                        .selectFrom(configFileTemplateJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param id ConfigFileTemplate ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(configFileTemplateJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 커서 기반 슬라이스 조건으로 ConfigFileTemplate 목록 조회
     *
     * <p>커서 기반 조회: ID 내림차순으로 정렬하고, 커서 ID보다 작은 ID를 조회합니다.
     *
     * <p>TechStack ID, Tool Type, Category 필터를 지원합니다.
     *
     * @param criteria 슬라이스 조건 (커서 기반, 필터 포함)
     * @return ConfigFileTemplate 엔티티 목록
     */
    public List<ConfigFileTemplateJpaEntity> findBySliceCriteria(
            ConfigFileTemplateSliceCriteria criteria) {
        return queryFactory
                .selectFrom(configFileTemplateJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.cursorLt(criteria),
                        conditionBuilder.techStackIdsIn(criteria),
                        conditionBuilder.toolTypesIn(criteria),
                        conditionBuilder.categoriesIn(criteria))
                .orderBy(configFileTemplateJpaEntity.id.desc())
                .limit(criteria.cursorPageRequest().fetchSize())
                .fetch();
    }

    /**
     * TechStack ID로 하위 ConfigFileTemplate 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param techStackId TechStack ID
     * @return 하위 리소스 존재 여부
     */
    public boolean existsByTechStackId(Long techStackId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(configFileTemplateJpaEntity)
                        .where(
                                conditionBuilder.techStackIdEq(techStackId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 기술스택 + 아키텍처 + 도구타입으로 템플릿 목록 조회 (MCP API용)
     *
     * @param techStackId 기술스택 ID
     * @param architectureId 아키텍처 ID (nullable)
     * @param toolTypes 도구 타입 목록
     * @return ConfigFileTemplate 엔티티 목록
     */
    public List<ConfigFileTemplateJpaEntity> findByTechStackAndTools(
            Long techStackId, Long architectureId, List<String> toolTypes) {
        return queryFactory
                .selectFrom(configFileTemplateJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.techStackIdEq(techStackId),
                        architectureIdEq(architectureId),
                        toolTypeIn(toolTypes))
                .orderBy(
                        configFileTemplateJpaEntity.displayOrder.asc(),
                        configFileTemplateJpaEntity.id.asc())
                .fetch();
    }

    // ========== Legacy Condition Methods (MCP API용) ==========

    private com.querydsl.core.types.dsl.BooleanExpression architectureIdEq(Long architectureId) {
        return architectureId != null
                ? configFileTemplateJpaEntity.architectureId.eq(architectureId)
                : null;
    }

    private com.querydsl.core.types.dsl.BooleanExpression toolTypeIn(List<String> toolTypes) {
        return toolTypes != null && !toolTypes.isEmpty()
                ? configFileTemplateJpaEntity.toolType.in(toolTypes)
                : null;
    }
}
