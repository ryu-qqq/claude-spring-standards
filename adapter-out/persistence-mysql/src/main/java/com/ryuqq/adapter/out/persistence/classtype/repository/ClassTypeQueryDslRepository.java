package com.ryuqq.adapter.out.persistence.classtype.repository;

import static com.ryuqq.adapter.out.persistence.classtype.entity.QClassTypeJpaEntity.classTypeJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.classtype.condition.ClassTypeConditionBuilder;
import com.ryuqq.adapter.out.persistence.classtype.entity.ClassTypeJpaEntity;
import com.ryuqq.domain.classtype.query.ClassTypeSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ClassTypeQueryDslRepository - ClassType QueryDSL Repository
 *
 * <p>ClassType 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class ClassTypeQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ClassTypeConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ClassTypeQueryDslRepository(
            JPAQueryFactory queryFactory, ClassTypeConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 ClassType 조회
     *
     * @param id ClassType ID
     * @return ClassType 엔티티 (Optional)
     */
    public Optional<ClassTypeJpaEntity> findById(Long id) {
        ClassTypeJpaEntity result =
                queryFactory
                        .selectFrom(classTypeJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param id ClassType ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(classTypeJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 커서 기반 슬라이스 조건으로 ClassType 목록 조회
     *
     * @param criteria 슬라이스 조건 (커서 기반)
     * @return ClassType 엔티티 목록
     */
    public List<ClassTypeJpaEntity> findBySliceCriteria(ClassTypeSliceCriteria criteria) {
        return queryFactory
                .selectFrom(classTypeJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.categoryIdsIn(criteria),
                        conditionBuilder.searchByField(
                                criteria.searchField(), criteria.searchWord()),
                        conditionBuilder.cursorGt(criteria))
                .orderBy(classTypeJpaEntity.orderIndex.asc(), classTypeJpaEntity.id.asc())
                .limit(criteria.cursorPageRequest().fetchSize())
                .fetch();
    }

    /**
     * 카테고리 내 코드 중복 체크 (삭제되지 않은 것만)
     *
     * @param categoryId 카테고리 ID
     * @param code ClassType 코드
     * @return 존재 여부
     */
    public boolean existsByCategoryIdAndCode(Long categoryId, String code) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(classTypeJpaEntity)
                        .where(
                                conditionBuilder.categoryIdEq(categoryId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * ID를 제외한 코드 중복 체크 (수정 시 사용, 삭제되지 않은 것만)
     *
     * @param categoryId 카테고리 ID
     * @param code 체크할 코드
     * @param excludeId 제외할 ID
     * @return 중복 여부
     */
    public boolean existsByCategoryIdAndCodeAndIdNot(Long categoryId, String code, Long excludeId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(classTypeJpaEntity)
                        .where(
                                conditionBuilder.categoryIdEq(categoryId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * Category ID로 하위 ClassType 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param categoryId ClassTypeCategory ID
     * @return 하위 리소스 존재 여부
     */
    public boolean existsByCategoryId(Long categoryId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(classTypeJpaEntity)
                        .where(
                                conditionBuilder.categoryIdEq(categoryId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }
}
