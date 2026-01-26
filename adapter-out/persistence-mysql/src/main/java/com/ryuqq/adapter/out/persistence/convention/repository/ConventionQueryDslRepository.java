package com.ryuqq.adapter.out.persistence.convention.repository;

import static com.ryuqq.adapter.out.persistence.convention.entity.QConventionJpaEntity.conventionJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.convention.condition.ConventionConditionBuilder;
import com.ryuqq.adapter.out.persistence.convention.entity.ConventionJpaEntity;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ConventionQueryDslRepository - Convention QueryDSL Repository
 *
 * <p>Convention 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class ConventionQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ConventionConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ConventionQueryDslRepository(
            JPAQueryFactory queryFactory, ConventionConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 전체 활성화된 컨벤션 조회
     *
     * @return 활성화된 컨벤션 엔티티 목록
     */
    public List<ConventionJpaEntity> findAllActive() {
        return queryFactory
                .selectFrom(conventionJpaEntity)
                .where(conditionBuilder.isActiveTrue(), conditionBuilder.deletedAtIsNull())
                .orderBy(conventionJpaEntity.moduleId.asc())
                .fetch();
    }

    /**
     * 모듈 ID별 활성화된 컨벤션 조회
     *
     * @param moduleId 모듈 ID
     * @return 컨벤션 엔티티 (Optional)
     */
    public Optional<ConventionJpaEntity> findActiveByModuleId(Long moduleId) {
        ConventionJpaEntity result =
                queryFactory
                        .selectFrom(conventionJpaEntity)
                        .where(
                                conditionBuilder.moduleIdEq(moduleId),
                                conditionBuilder.isActiveTrue(),
                                conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 컨벤션 조회
     *
     * @param id 컨벤션 ID
     * @return 컨벤션 엔티티 (Optional)
     */
    public Optional<ConventionJpaEntity> findById(Long id) {
        ConventionJpaEntity result =
                queryFactory
                        .selectFrom(conventionJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 존재 여부 확인
     *
     * @param id 컨벤션 ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(conventionJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 모듈 ID + 버전 조합으로 존재 여부 확인
     *
     * @param moduleId 모듈 ID
     * @param version 버전 문자열
     * @return 존재 여부
     */
    public boolean existsByModuleIdAndVersion(Long moduleId, String version) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(conventionJpaEntity)
                        .where(
                                conditionBuilder.moduleIdEq(moduleId),
                                conventionJpaEntity.version.eq(version),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 특정 ID를 제외한 모듈 ID + 버전 조합으로 존재 여부 확인
     *
     * @param moduleId 모듈 ID
     * @param version 버전 문자열
     * @param excludeId 제외할 ID
     * @return 존재 여부
     */
    public boolean existsByModuleIdAndVersionAndIdNot(
            Long moduleId, String version, Long excludeId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(conventionJpaEntity)
                        .where(
                                conditionBuilder.moduleIdEq(moduleId),
                                conventionJpaEntity.version.eq(version),
                                conventionJpaEntity.id.ne(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 슬라이스 조건으로 Convention 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return Convention 엔티티 목록
     */
    public List<ConventionJpaEntity> findBySliceCriteria(ConventionSliceCriteria criteria) {
        return queryFactory
                .selectFrom(conventionJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.moduleIdsIn(criteria),
                        conditionBuilder.cursorLt(criteria))
                .orderBy(conventionJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }
}
