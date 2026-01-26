package com.ryuqq.adapter.out.persistence.layerdependency.repository;

import static com.ryuqq.adapter.out.persistence.layerdependency.entity.QLayerDependencyRuleJpaEntity.layerDependencyRuleJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.layerdependency.condition.LayerDependencyRuleConditionBuilder;
import com.ryuqq.adapter.out.persistence.layerdependency.entity.LayerDependencyRuleJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * LayerDependencyRuleQueryDslRepository - 레이어 의존성 규칙 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class LayerDependencyRuleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final LayerDependencyRuleConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public LayerDependencyRuleQueryDslRepository(
            JPAQueryFactory queryFactory, LayerDependencyRuleConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 레이어 의존성 규칙 조회
     *
     * @param id 레이어 의존성 규칙 ID
     * @return LayerDependencyRuleJpaEntity (Optional)
     */
    public Optional<LayerDependencyRuleJpaEntity> findById(Long id) {
        LayerDependencyRuleJpaEntity entity =
                queryFactory
                        .selectFrom(layerDependencyRuleJpaEntity)
                        .where(conditionBuilder.idEq(id))
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 아키텍처 ID로 레이어 의존성 규칙 목록 조회
     *
     * @param architectureId 아키텍처 ID
     * @return 레이어 의존성 규칙 목록
     */
    public List<LayerDependencyRuleJpaEntity> findByArchitectureId(Long architectureId) {
        return queryFactory
                .selectFrom(layerDependencyRuleJpaEntity)
                .where(conditionBuilder.architectureIdEq(architectureId))
                .orderBy(
                        layerDependencyRuleJpaEntity.fromLayer.asc(),
                        layerDependencyRuleJpaEntity.toLayer.asc())
                .fetch();
    }

    /**
     * 슬라이스 조건으로 레이어 의존성 규칙 목록 조회
     *
     * @param criteria 슬라이스 조회 조건
     * @return LayerDependencyRuleJpaEntity 목록
     */
    public List<LayerDependencyRuleJpaEntity> findBySliceCriteria(
            com.ryuqq.domain.layerdependency.query.LayerDependencyRuleSliceCriteria criteria) {
        List<Long> architectureIds =
                criteria.hasArchitectureFilter()
                        ? criteria.architectureIds().stream()
                                .map(com.ryuqq.domain.architecture.id.ArchitectureId::value)
                                .toList()
                        : null;

        List<String> dependencyTypes =
                criteria.hasDependencyTypeFilter()
                        ? criteria.dependencyTypes().stream().map(Enum::name).toList()
                        : null;

        return queryFactory
                .selectFrom(layerDependencyRuleJpaEntity)
                .where(
                        conditionBuilder.architectureIdsIn(architectureIds),
                        conditionBuilder.dependencyTypesIn(dependencyTypes),
                        conditionBuilder.searchContains(
                                criteria.searchField(), criteria.searchWord()),
                        conditionBuilder.cursorLt(criteria.cursorPageRequest().cursor()))
                .orderBy(layerDependencyRuleJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id 레이어 의존성 규칙 ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(layerDependencyRuleJpaEntity)
                        .where(conditionBuilder.idEq(id))
                        .fetchFirst();
        return fetchOne != null;
    }
}
