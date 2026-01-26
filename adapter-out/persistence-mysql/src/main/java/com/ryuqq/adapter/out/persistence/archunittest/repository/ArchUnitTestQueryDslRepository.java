package com.ryuqq.adapter.out.persistence.archunittest.repository;

import static com.ryuqq.adapter.out.persistence.archunittest.entity.QArchUnitTestJpaEntity.archUnitTestJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.archunittest.condition.ArchUnitTestConditionBuilder;
import com.ryuqq.adapter.out.persistence.archunittest.entity.ArchUnitTestJpaEntity;
import com.ryuqq.domain.archunittest.query.ArchUnitTestSliceCriteria;
import com.ryuqq.domain.packagestructure.id.PackageStructureId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ArchUnitTestQueryDslRepository - ArchUnit 테스트 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class ArchUnitTestQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ArchUnitTestConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ArchUnitTestQueryDslRepository(
            JPAQueryFactory queryFactory, ArchUnitTestConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 ArchUnit 테스트 조회
     *
     * @param id ArchUnit 테스트 ID
     * @return ArchUnit 테스트 Optional
     */
    public Optional<ArchUnitTestJpaEntity> findById(Long id) {
        ArchUnitTestJpaEntity entity =
                queryFactory
                        .selectFrom(archUnitTestJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 코드로 ArchUnit 테스트 조회
     *
     * @param code 테스트 코드
     * @return ArchUnit 테스트 Optional
     */
    public Optional<ArchUnitTestJpaEntity> findByCode(String code) {
        ArchUnitTestJpaEntity entity =
                queryFactory
                        .selectFrom(archUnitTestJpaEntity)
                        .where(conditionBuilder.codeEq(code), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 패키지 구조 ID로 ArchUnit 테스트 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return ArchUnit 테스트 목록
     */
    public List<ArchUnitTestJpaEntity> findByStructureId(Long structureId) {
        return queryFactory
                .selectFrom(archUnitTestJpaEntity)
                .where(
                        archUnitTestJpaEntity.structureId.eq(structureId),
                        conditionBuilder.deletedAtIsNull())
                .orderBy(archUnitTestJpaEntity.id.desc())
                .fetch();
    }

    /**
     * 심각도로 ArchUnit 테스트 목록 조회
     *
     * @param severity 심각도
     * @return ArchUnit 테스트 목록
     */
    public List<ArchUnitTestJpaEntity> findBySeverity(String severity) {
        return queryFactory
                .selectFrom(archUnitTestJpaEntity)
                .where(conditionBuilder.severityEq(severity), conditionBuilder.deletedAtIsNull())
                .orderBy(archUnitTestJpaEntity.id.desc())
                .fetch();
    }

    /**
     * 커서 기반 슬라이스 조회
     *
     * @param structureId 패키지 구조 ID (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return ArchUnit 테스트 목록
     */
    public List<ArchUnitTestJpaEntity> findBySlice(Long structureId, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(archUnitTestJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.structureIdEq(structureId),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(archUnitTestJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    public List<ArchUnitTestJpaEntity> findBySliceCriteria(ArchUnitTestSliceCriteria criteria) {
        List<Long> structureIds =
                criteria.hasStructureFilter()
                        ? criteria.structureIds().stream().map(PackageStructureId::value).toList()
                        : null;

        List<String> severities =
                criteria.hasSeverities()
                        ? criteria.severities().stream().map(Enum::name).toList()
                        : null;

        return queryFactory
                .selectFrom(archUnitTestJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.structureIdsIn(structureIds),
                        conditionBuilder.searchContains(
                                criteria.searchField(), criteria.searchWord()),
                        conditionBuilder.severitiesIn(severities),
                        conditionBuilder.cursorLt(criteria.cursorPageRequest().cursor()))
                .orderBy(archUnitTestJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * 패키지 구조 내 코드 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @return 존재하면 true
     */
    public boolean existsByStructureIdAndCode(Long structureId, String code) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(archUnitTestJpaEntity)
                        .where(
                                archUnitTestJpaEntity.structureId.eq(structureId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 패키지 구조 내 코드 존재 여부 확인 (특정 ID 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 테스트 코드
     * @param excludeId 제외할 ArchUnit 테스트 ID
     * @return 존재하면 true
     */
    public boolean existsByStructureIdAndCodeExcluding(
            Long structureId, String code, Long excludeId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(archUnitTestJpaEntity)
                        .where(
                                archUnitTestJpaEntity.structureId.eq(structureId),
                                conditionBuilder.codeEq(code),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }
}
