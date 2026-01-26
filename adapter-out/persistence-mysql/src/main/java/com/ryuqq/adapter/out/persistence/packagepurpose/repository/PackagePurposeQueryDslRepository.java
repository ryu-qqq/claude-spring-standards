package com.ryuqq.adapter.out.persistence.packagepurpose.repository;

import static com.ryuqq.adapter.out.persistence.packagepurpose.entity.QPackagePurposeJpaEntity.packagePurposeJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.packagepurpose.condition.PackagePurposeConditionBuilder;
import com.ryuqq.adapter.out.persistence.packagepurpose.entity.PackagePurposeJpaEntity;
import com.ryuqq.domain.packagepurpose.query.PackagePurposeSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * PackagePurposeQueryDslRepository - 패키지 목적 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class PackagePurposeQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final PackagePurposeConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public PackagePurposeQueryDslRepository(
            JPAQueryFactory queryFactory, PackagePurposeConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 패키지 목적 조회
     *
     * @param id 패키지 목적 ID
     * @return 패키지 목적 Optional
     */
    public Optional<PackagePurposeJpaEntity> findById(Long id) {
        PackagePurposeJpaEntity entity =
                queryFactory
                        .selectFrom(packagePurposeJpaEntity)
                        .where(
                                packagePurposeJpaEntity.id.eq(id),
                                conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 패키지 구조 ID로 패키지 목적 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return 패키지 목적 목록
     */
    public List<PackagePurposeJpaEntity> findByStructureId(Long structureId) {
        return queryFactory
                .selectFrom(packagePurposeJpaEntity)
                .where(
                        conditionBuilder.structureIdEq(structureId),
                        conditionBuilder.deletedAtIsNull())
                .orderBy(packagePurposeJpaEntity.code.asc())
                .fetch();
    }

    /**
     * 슬라이스 조건으로 PackagePurpose 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return PackagePurpose 엔티티 목록
     */
    public List<PackagePurposeJpaEntity> findBySliceCriteria(PackagePurposeSliceCriteria criteria) {
        return queryFactory
                .selectFrom(packagePurposeJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.structureIdsIn(criteria),
                        conditionBuilder.searchByField(criteria),
                        conditionBuilder.cursorLt(criteria))
                .orderBy(packagePurposeJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * 패키지 구조 ID와 코드로 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @return 존재하면 true
     */
    public boolean existsByStructureIdAndCode(Long structureId, String code) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(packagePurposeJpaEntity)
                        .where(
                                conditionBuilder.structureIdEq(structureId),
                                packagePurposeJpaEntity.code.eq(code),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 패키지 구조 ID와 코드로 존재 여부 확인 (특정 ID 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param code 목적 코드
     * @param excludeId 제외할 ID
     * @return 존재하면 true
     */
    public boolean existsByStructureIdAndCodeExcluding(
            Long structureId, String code, Long excludeId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(packagePurposeJpaEntity)
                        .where(
                                conditionBuilder.structureIdEq(structureId),
                                packagePurposeJpaEntity.code.eq(code),
                                packagePurposeJpaEntity.id.ne(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }
}
