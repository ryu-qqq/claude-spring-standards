package com.ryuqq.adapter.out.persistence.layer.repository;

import static com.ryuqq.adapter.out.persistence.layer.entity.QLayerJpaEntity.layerJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.layer.condition.LayerConditionBuilder;
import com.ryuqq.adapter.out.persistence.layer.entity.LayerJpaEntity;
import com.ryuqq.domain.layer.query.LayerSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * LayerQueryDslRepository - Layer QueryDSL Repository
 *
 * <p>Layer 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class LayerQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final LayerConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public LayerQueryDslRepository(
            JPAQueryFactory queryFactory, LayerConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 Layer 조회
     *
     * @param id Layer ID
     * @return Layer 엔티티 (Optional)
     */
    public Optional<LayerJpaEntity> findById(Long id) {
        LayerJpaEntity result =
                queryFactory
                        .selectFrom(layerJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param id Layer ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(layerJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 커서 기반 슬라이스 조건으로 Layer 목록 조회
     *
     * <p>커서 기반 조회: orderIndex 오름차순, ID 오름차순으로 정렬하고 커서 ID보다 큰 ID를 조회합니다.
     *
     * <p>Architecture ID 필터링 및 필드별 검색을 지원합니다.
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return Layer 엔티티 목록
     */
    public List<LayerJpaEntity> findBySliceCriteria(LayerSliceCriteria criteria) {
        return queryFactory
                .selectFrom(layerJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.architectureIdsIn(criteria),
                        conditionBuilder.searchByField(
                                criteria.searchField(), criteria.searchWord()),
                        conditionBuilder.cursorGt(criteria))
                .orderBy(layerJpaEntity.orderIndex.asc(), layerJpaEntity.id.asc())
                .limit(criteria.cursorPageRequest().fetchSize())
                .fetch();
    }

    /**
     * 아키텍처 내 코드 중복 체크 (삭제되지 않은 것만)
     *
     * @param architectureId 아키텍처 ID
     * @param code Layer 코드
     * @return 존재 여부
     */
    public boolean existsByArchitectureIdAndCode(Long architectureId, String code) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(layerJpaEntity)
                        .where(
                                conditionBuilder.architectureIdEq(architectureId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * ID를 제외한 코드 중복 체크 (수정 시 사용, 삭제되지 않은 것만)
     *
     * @param architectureId 아키텍처 ID
     * @param code 체크할 코드
     * @param excludeId 제외할 ID
     * @return 중복 여부
     */
    public boolean existsByArchitectureIdAndCodeAndIdNot(
            Long architectureId, String code, Long excludeId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(layerJpaEntity)
                        .where(
                                conditionBuilder.architectureIdEq(architectureId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * Architecture ID로 하위 Layer 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param architectureId Architecture ID
     * @return 하위 리소스 존재 여부
     */
    public boolean existsByArchitectureId(Long architectureId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(layerJpaEntity)
                        .where(
                                conditionBuilder.architectureIdEq(architectureId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }
}
