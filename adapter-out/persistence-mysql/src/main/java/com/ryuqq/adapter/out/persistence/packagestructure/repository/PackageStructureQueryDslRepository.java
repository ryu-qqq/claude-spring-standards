package com.ryuqq.adapter.out.persistence.packagestructure.repository;

import static com.ryuqq.adapter.out.persistence.packagestructure.entity.QPackageStructureJpaEntity.packageStructureJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.packagestructure.condition.PackageStructureConditionBuilder;
import com.ryuqq.adapter.out.persistence.packagestructure.entity.PackageStructureJpaEntity;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * PackageStructureQueryDslRepository - 패키지 구조 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class PackageStructureQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final PackageStructureConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public PackageStructureQueryDslRepository(
            JPAQueryFactory queryFactory, PackageStructureConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 패키지 구조 조회
     *
     * @param id 패키지 구조 ID
     * @return 패키지 구조 Optional
     */
    public Optional<PackageStructureJpaEntity> findById(Long id) {
        PackageStructureJpaEntity entity =
                queryFactory
                        .selectFrom(packageStructureJpaEntity)
                        .where(
                                packageStructureJpaEntity.id.eq(id),
                                conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * Module ID로 패키지 구조 목록 조회
     *
     * @param moduleId 모듈 ID
     * @return 패키지 구조 목록
     */
    public List<PackageStructureJpaEntity> findByModuleId(Long moduleId) {
        return queryFactory
                .selectFrom(packageStructureJpaEntity)
                .where(
                        packageStructureJpaEntity.moduleId.eq(moduleId),
                        conditionBuilder.deletedAtIsNull())
                .orderBy(packageStructureJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 모듈 내 경로 패턴 존재 여부 확인
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @return 존재하면 true
     */
    public boolean existsByModuleIdAndPathPattern(Long moduleId, String pathPattern) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(packageStructureJpaEntity)
                        .where(
                                packageStructureJpaEntity.moduleId.eq(moduleId),
                                packageStructureJpaEntity.pathPattern.eq(pathPattern),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 모듈 내 경로 패턴 존재 여부 확인 (특정 ID 제외)
     *
     * @param moduleId 모듈 ID
     * @param pathPattern 경로 패턴
     * @param excludeId 제외할 패키지 구조 ID
     * @return 존재하면 true
     */
    public boolean existsByModuleIdAndPathPatternExcluding(
            Long moduleId, String pathPattern, Long excludeId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(packageStructureJpaEntity)
                        .where(
                                packageStructureJpaEntity.moduleId.eq(moduleId),
                                packageStructureJpaEntity.pathPattern.eq(pathPattern),
                                packageStructureJpaEntity.id.ne(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 커서 기반 슬라이스 조회 (레거시 - 호환성 유지)
     *
     * @param moduleId 모듈 ID (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 패키지 구조 목록
     */
    public List<PackageStructureJpaEntity> findBySlice(Long moduleId, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(packageStructureJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.moduleIdEq(moduleId),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(packageStructureJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 슬라이스 조건으로 PackageStructure 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return PackageStructure 엔티티 목록
     */
    public List<PackageStructureJpaEntity> findBySliceCriteria(
            PackageStructureSliceCriteria criteria) {
        return queryFactory
                .selectFrom(packageStructureJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.moduleIdsIn(criteria),
                        conditionBuilder.cursorLt(criteria))
                .orderBy(packageStructureJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * 전체 패키지 구조 목록 조회
     *
     * @return 패키지 구조 목록
     */
    public List<PackageStructureJpaEntity> searchAll() {
        return queryFactory
                .selectFrom(packageStructureJpaEntity)
                .where(conditionBuilder.deletedAtIsNull())
                .orderBy(packageStructureJpaEntity.id.asc())
                .fetch();
    }
}
