package com.ryuqq.adapter.out.persistence.resourcetemplate.repository;

import static com.ryuqq.adapter.out.persistence.resourcetemplate.entity.QResourceTemplateJpaEntity.resourceTemplateJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.resourcetemplate.condition.ResourceTemplateConditionBuilder;
import com.ryuqq.adapter.out.persistence.resourcetemplate.entity.ResourceTemplateJpaEntity;
import com.ryuqq.domain.resourcetemplate.query.ResourceTemplateSliceCriteria;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ResourceTemplateQueryDslRepository - 리소스 템플릿 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class ResourceTemplateQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ResourceTemplateConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    @SuppressFBWarnings(value = "EI2", justification = "Spring-managed bean injection")
    public ResourceTemplateQueryDslRepository(
            JPAQueryFactory queryFactory, ResourceTemplateConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 리소스 템플릿 조회
     *
     * @param id 리소스 템플릿 ID
     * @return 리소스 템플릿 Optional
     */
    public Optional<ResourceTemplateJpaEntity> findById(Long id) {
        ResourceTemplateJpaEntity entity =
                queryFactory
                        .selectFrom(resourceTemplateJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 모듈 ID로 리소스 템플릿 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 리소스 템플릿 목록
     */
    public List<ResourceTemplateJpaEntity> findByModuleId(Long moduleId) {
        return queryFactory
                .selectFrom(resourceTemplateJpaEntity)
                .where(conditionBuilder.moduleIdEq(moduleId), conditionBuilder.deletedAtIsNull())
                .orderBy(resourceTemplateJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 모듈 ID와 카테고리로 리소스 템플릿 목록 조회
     *
     * @param moduleId 모듈 ID
     * @param category 카테고리 문자열
     * @return 리소스 템플릿 목록
     */
    public List<ResourceTemplateJpaEntity> findByModuleIdAndCategory(
            Long moduleId, String category) {
        return queryFactory
                .selectFrom(resourceTemplateJpaEntity)
                .where(
                        conditionBuilder.moduleIdEq(moduleId),
                        conditionBuilder.categoryEq(category),
                        conditionBuilder.deletedAtIsNull())
                .orderBy(resourceTemplateJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 커서 기반 슬라이스 조회 (조건 없음)
     *
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 리소스 템플릿 목록
     */
    public List<ResourceTemplateJpaEntity> findBySlice(Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(resourceTemplateJpaEntity)
                .where(conditionBuilder.deletedAtIsNull(), conditionBuilder.cursorLt(cursor))
                .orderBy(resourceTemplateJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 모듈 ID로 커서 기반 슬라이스 조회
     *
     * @param moduleId 모듈 ID
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 리소스 템플릿 목록
     */
    public List<ResourceTemplateJpaEntity> findByModuleIdSlice(
            Long moduleId, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(resourceTemplateJpaEntity)
                .where(
                        conditionBuilder.moduleIdEq(moduleId),
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(resourceTemplateJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 모듈 ID와 카테고리로 커서 기반 슬라이스 조회
     *
     * @param moduleId 모듈 ID
     * @param category 카테고리 문자열
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 리소스 템플릿 목록
     */
    public List<ResourceTemplateJpaEntity> findByModuleIdAndCategorySlice(
            Long moduleId, String category, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(resourceTemplateJpaEntity)
                .where(
                        conditionBuilder.moduleIdEq(moduleId),
                        conditionBuilder.categoryEq(category),
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(resourceTemplateJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * ResourceTemplateSliceCriteria 기반 슬라이스 조회
     *
     * <p>복합 조건(모듈 ID, 카테고리, 파일 타입) 필터링과 커서 기반 페이징을 지원합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return 리소스 템플릿 목록
     */
    public List<ResourceTemplateJpaEntity> findBySliceCriteria(
            ResourceTemplateSliceCriteria criteria) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(conditionBuilder.deletedAtIsNull());
        where.and(conditionBuilder.moduleIdsIn(criteria));
        where.and(conditionBuilder.categoriesIn(criteria));
        where.and(conditionBuilder.fileTypesIn(criteria));
        where.and(conditionBuilder.cursorLt(criteria));

        return queryFactory
                .selectFrom(resourceTemplateJpaEntity)
                .where(where)
                .orderBy(resourceTemplateJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }
}
