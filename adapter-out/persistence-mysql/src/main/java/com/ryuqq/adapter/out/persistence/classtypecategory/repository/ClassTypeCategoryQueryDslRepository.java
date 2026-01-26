package com.ryuqq.adapter.out.persistence.classtypecategory.repository;

import static com.ryuqq.adapter.out.persistence.classtypecategory.entity.QClassTypeCategoryJpaEntity.classTypeCategoryJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.classtypecategory.condition.ClassTypeCategoryConditionBuilder;
import com.ryuqq.adapter.out.persistence.classtypecategory.entity.ClassTypeCategoryJpaEntity;
import com.ryuqq.domain.classtypecategory.query.ClassTypeCategorySliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ClassTypeCategoryQueryDslRepository - ClassTypeCategory QueryDSL Repository
 *
 * <p>ClassTypeCategory 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class ClassTypeCategoryQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ClassTypeCategoryConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ClassTypeCategoryQueryDslRepository(
            JPAQueryFactory queryFactory, ClassTypeCategoryConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 ClassTypeCategory 조회
     *
     * @param id ClassTypeCategory ID
     * @return ClassTypeCategory 엔티티 (Optional)
     */
    public Optional<ClassTypeCategoryJpaEntity> findById(Long id) {
        ClassTypeCategoryJpaEntity result =
                queryFactory
                        .selectFrom(classTypeCategoryJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param id ClassTypeCategory ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(classTypeCategoryJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 커서 기반 슬라이스 조건으로 ClassTypeCategory 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ClassTypeCategory 엔티티 목록
     */
    public List<ClassTypeCategoryJpaEntity> findBySliceCriteria(
            ClassTypeCategorySliceCriteria criteria) {
        return queryFactory
                .selectFrom(classTypeCategoryJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.architectureIdsIn(criteria),
                        conditionBuilder.searchByField(
                                criteria.searchField(), criteria.searchWord()),
                        conditionBuilder.cursorGt(criteria))
                .orderBy(
                        classTypeCategoryJpaEntity.orderIndex.asc(),
                        classTypeCategoryJpaEntity.id.asc())
                .limit(criteria.cursorPageRequest().fetchSize())
                .fetch();
    }

    /**
     * 아키텍처 내 코드 중복 체크 (삭제되지 않은 것만)
     *
     * @param architectureId 아키텍처 ID
     * @param code ClassTypeCategory 코드
     * @return 존재 여부
     */
    public boolean existsByArchitectureIdAndCode(Long architectureId, String code) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(classTypeCategoryJpaEntity)
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
                        .from(classTypeCategoryJpaEntity)
                        .where(
                                conditionBuilder.architectureIdEq(architectureId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * Architecture ID로 하위 ClassTypeCategory 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param architectureId Architecture ID
     * @return 하위 리소스 존재 여부
     */
    public boolean existsByArchitectureId(Long architectureId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(classTypeCategoryJpaEntity)
                        .where(
                                conditionBuilder.architectureIdEq(architectureId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }
}
