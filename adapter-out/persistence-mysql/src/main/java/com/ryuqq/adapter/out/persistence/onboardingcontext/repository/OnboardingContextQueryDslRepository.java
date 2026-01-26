package com.ryuqq.adapter.out.persistence.onboardingcontext.repository;

import static com.ryuqq.adapter.out.persistence.onboardingcontext.entity.QOnboardingContextJpaEntity.onboardingContextJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.onboardingcontext.condition.OnboardingContextConditionBuilder;
import com.ryuqq.adapter.out.persistence.onboardingcontext.entity.OnboardingContextJpaEntity;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * OnboardingContextQueryDslRepository - OnboardingContext QueryDSL Repository
 *
 * <p>OnboardingContext 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
@SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Spring Bean injection - immutable after construction")
public class OnboardingContextQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final OnboardingContextConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public OnboardingContextQueryDslRepository(
            JPAQueryFactory queryFactory, OnboardingContextConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 OnboardingContext 조회
     *
     * @param id OnboardingContext ID
     * @return OnboardingContext 엔티티 (Optional)
     */
    public Optional<OnboardingContextJpaEntity> findById(Long id) {
        OnboardingContextJpaEntity result =
                queryFactory
                        .selectFrom(onboardingContextJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param id OnboardingContext ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(onboardingContextJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 커서 기반 슬라이스 조건으로 OnboardingContext 목록 조회
     *
     * <p>커서 기반 조회: ID 내림차순으로 정렬하고, 커서 ID보다 작은 ID를 조회합니다.
     *
     * <p>TechStack ID, Context Type 필터를 지원합니다.
     *
     * @param criteria 슬라이스 조건 (커서 기반, 필터 포함)
     * @return OnboardingContext 엔티티 목록
     */
    public List<OnboardingContextJpaEntity> findBySliceCriteria(
            OnboardingContextSliceCriteria criteria) {
        return queryFactory
                .selectFrom(onboardingContextJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.cursorLt(criteria),
                        conditionBuilder.techStackIdsIn(criteria),
                        conditionBuilder.contextTypesIn(criteria))
                .orderBy(onboardingContextJpaEntity.id.desc())
                .limit(criteria.cursorPageRequest().fetchSize())
                .fetch();
    }

    /**
     * TechStack ID로 하위 OnboardingContext 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param techStackId TechStack ID
     * @return 하위 리소스 존재 여부
     */
    public boolean existsByTechStackId(Long techStackId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(onboardingContextJpaEntity)
                        .where(
                                conditionBuilder.techStackIdEq(techStackId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    // ========== Legacy Methods (MCP API용) ==========

    /**
     * 기술스택 + 아키텍처로 온보딩 컨텍스트 전체 조회
     *
     * <p>priority 순으로 정렬됩니다.
     *
     * @param techStackId 기술스택 ID
     * @param architectureId 아키텍처 ID (nullable)
     * @return OnboardingContext 엔티티 목록
     */
    public List<OnboardingContextJpaEntity> findByTechStackAndArchitecture(
            Long techStackId, Long architectureId) {
        return queryFactory
                .selectFrom(onboardingContextJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.techStackIdEq(techStackId),
                        architectureIdEq(architectureId))
                .orderBy(
                        onboardingContextJpaEntity.priority.asc(),
                        onboardingContextJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 기술스택 + 컨텍스트 타입으로 조회
     *
     * @param techStackId 기술스택 ID
     * @param contextType 컨텍스트 타입 (SUMMARY, ZERO_TOLERANCE 등)
     * @return OnboardingContext 엔티티 목록
     */
    public List<OnboardingContextJpaEntity> findByTechStackAndContextType(
            Long techStackId, String contextType) {
        return queryFactory
                .selectFrom(onboardingContextJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.techStackIdEq(techStackId),
                        contextTypeEq(contextType))
                .orderBy(
                        onboardingContextJpaEntity.priority.asc(),
                        onboardingContextJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 특정 기술스택의 모든 온보딩 컨텍스트 조회
     *
     * @param techStackId 기술스택 ID
     * @return OnboardingContext 엔티티 목록
     */
    public List<OnboardingContextJpaEntity> findAllByTechStack(Long techStackId) {
        return queryFactory
                .selectFrom(onboardingContextJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.techStackIdEq(techStackId))
                .orderBy(
                        onboardingContextJpaEntity.priority.asc(),
                        onboardingContextJpaEntity.id.asc())
                .fetch();
    }

    // ========== Legacy Condition Methods (MCP API용) ==========

    private BooleanExpression architectureIdEq(Long architectureId) {
        return architectureId != null
                ? onboardingContextJpaEntity.architectureId.eq(architectureId)
                : null;
    }

    private BooleanExpression contextTypeEq(String contextType) {
        return contextType != null ? onboardingContextJpaEntity.contextType.eq(contextType) : null;
    }

    private BooleanExpression contextTypesIn(List<String> contextTypes) {
        return contextTypes != null && !contextTypes.isEmpty()
                ? onboardingContextJpaEntity.contextType.in(contextTypes)
                : null;
    }

    /**
     * 기술스택 + 아키텍처 + 컨텍스트 타입으로 온보딩 컨텍스트 조회 (MCP API용)
     *
     * <p>priority 순으로 정렬됩니다.
     *
     * @param techStackId 기술스택 ID
     * @param architectureId 아키텍처 ID (nullable)
     * @param contextTypes 컨텍스트 타입 목록 (nullable)
     * @return OnboardingContext 엔티티 목록
     */
    public List<OnboardingContextJpaEntity> findForMcp(
            Long techStackId, Long architectureId, List<String> contextTypes) {
        return queryFactory
                .selectFrom(onboardingContextJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.techStackIdEq(techStackId),
                        architectureIdEq(architectureId),
                        contextTypesIn(contextTypes))
                .orderBy(
                        onboardingContextJpaEntity.priority.asc(),
                        onboardingContextJpaEntity.id.asc())
                .fetch();
    }
}
