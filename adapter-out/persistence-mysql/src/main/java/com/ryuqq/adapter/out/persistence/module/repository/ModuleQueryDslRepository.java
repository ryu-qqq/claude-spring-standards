package com.ryuqq.adapter.out.persistence.module.repository;

import static com.ryuqq.adapter.out.persistence.module.entity.QModuleJpaEntity.moduleJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.module.condition.ModuleConditionBuilder;
import com.ryuqq.adapter.out.persistence.module.entity.ModuleJpaEntity;
import com.ryuqq.domain.module.query.ModuleSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ModuleQueryDslRepository - Module QueryDSL Repository
 *
 * <p>Module 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class ModuleQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ModuleConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ModuleQueryDslRepository(
            JPAQueryFactory queryFactory, ModuleConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 모듈 조회
     *
     * @param id 모듈 ID
     * @return 모듈 엔티티 (Optional)
     */
    public Optional<ModuleJpaEntity> findById(Long id) {
        ModuleJpaEntity result =
                queryFactory
                        .selectFrom(moduleJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 슬라이스 조건으로 Module 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return Module 엔티티 목록
     */
    public List<ModuleJpaEntity> findBySliceCriteria(ModuleSliceCriteria criteria) {
        return queryFactory
                .selectFrom(moduleJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.layerIdsIn(criteria),
                        conditionBuilder.cursorLt(criteria))
                .orderBy(moduleJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * 레이어 ID로 전체 모듈 목록 조회 (트리 구조용)
     *
     * @param layerId 레이어 ID
     * @return 모듈 엔티티 목록
     */
    public List<ModuleJpaEntity> findAllByLayerId(Long layerId) {
        return queryFactory
                .selectFrom(moduleJpaEntity)
                .where(conditionBuilder.layerIdEq(layerId), conditionBuilder.deletedAtIsNull())
                .orderBy(
                        moduleJpaEntity.parentModuleId.asc().nullsFirst(),
                        moduleJpaEntity.name.asc())
                .fetch();
    }

    /**
     * 레이어 내 모듈 이름 존재 여부 확인
     *
     * @param layerId 레이어 ID
     * @param name 모듈 이름
     * @return 존재 여부
     */
    public boolean existsByLayerIdAndName(Long layerId, String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(moduleJpaEntity)
                        .where(
                                conditionBuilder.layerIdEq(layerId),
                                conditionBuilder.nameEq(name),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 특정 ID를 제외한 레이어 내 모듈 이름 존재 여부 확인
     *
     * @param layerId 레이어 ID
     * @param name 모듈 이름
     * @param excludeId 제외할 모듈 ID
     * @return 존재 여부
     */
    public boolean existsByLayerIdAndNameExcluding(Long layerId, String name, Long excludeId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(moduleJpaEntity)
                        .where(
                                conditionBuilder.layerIdEq(layerId),
                                conditionBuilder.nameEq(name),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 부모 모듈 ID로 자식 모듈 존재 여부 확인
     *
     * @param parentModuleId 부모 모듈 ID
     * @return 자식 모듈 존재 여부
     */
    public boolean existsByParentModuleId(Long parentModuleId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(moduleJpaEntity)
                        .where(
                                conditionBuilder.parentModuleIdEq(parentModuleId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 키워드 검색
     *
     * <p>name, description 필드에서 키워드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param layerId 레이어 ID (nullable)
     * @return 검색된 모듈 목록
     */
    public List<ModuleJpaEntity> searchByKeyword(String keyword, Long layerId) {
        return queryFactory
                .selectFrom(moduleJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.layerIdEq(layerId),
                        conditionBuilder.keywordContains(keyword))
                .orderBy(moduleJpaEntity.name.asc())
                .fetch();
    }
}
