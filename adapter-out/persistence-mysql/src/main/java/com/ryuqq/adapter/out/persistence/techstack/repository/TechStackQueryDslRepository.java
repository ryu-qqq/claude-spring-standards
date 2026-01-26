package com.ryuqq.adapter.out.persistence.techstack.repository;

import static com.ryuqq.adapter.out.persistence.techstack.entity.QTechStackJpaEntity.techStackJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.techstack.condition.TechStackConditionBuilder;
import com.ryuqq.adapter.out.persistence.techstack.entity.TechStackJpaEntity;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * TechStackQueryDslRepository - TechStack QueryDSL Repository
 *
 * <p>TechStack 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class TechStackQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final TechStackConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public TechStackQueryDslRepository(
            JPAQueryFactory queryFactory, TechStackConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 TechStack 조회
     *
     * @param id TechStack ID
     * @return TechStack 엔티티 (Optional)
     */
    public Optional<TechStackJpaEntity> findById(Long id) {
        TechStackJpaEntity result =
                queryFactory
                        .selectFrom(techStackJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param id TechStack ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(techStackJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 이름으로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param name TechStack 이름
     * @return 존재 여부
     */
    public boolean existsByName(String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(techStackJpaEntity)
                        .where(conditionBuilder.nameEq(name), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * ID를 제외한 이름 중복 체크 (수정 시 사용, 삭제되지 않은 것만)
     *
     * @param name 체크할 이름
     * @param excludeId 제외할 ID
     * @return 중복 여부
     */
    public boolean existsByNameAndIdNot(String name, Long excludeId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(techStackJpaEntity)
                        .where(
                                conditionBuilder.nameEq(name),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 슬라이스 조건으로 TechStack 목록 조회 (커서 기반)
     *
     * <p>상태 및 플랫폼 타입 필터를 지원합니다.
     *
     * @param criteria 슬라이스 조건
     * @return TechStack 엔티티 목록
     */
    public List<TechStackJpaEntity> findBySliceCriteria(TechStackSliceCriteria criteria) {
        return queryFactory
                .selectFrom(techStackJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.cursorLt(criteria),
                        conditionBuilder.statusEqFromCriteria(criteria),
                        conditionBuilder.platformTypeIn(criteria))
                .orderBy(techStackJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }
}
