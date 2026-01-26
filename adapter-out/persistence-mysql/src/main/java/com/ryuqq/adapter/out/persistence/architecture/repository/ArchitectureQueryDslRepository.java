package com.ryuqq.adapter.out.persistence.architecture.repository;

import static com.ryuqq.adapter.out.persistence.architecture.entity.QArchitectureJpaEntity.architectureJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.architecture.condition.ArchitectureConditionBuilder;
import com.ryuqq.adapter.out.persistence.architecture.entity.ArchitectureJpaEntity;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ArchitectureQueryDslRepository - Architecture QueryDSL Repository
 *
 * <p>Architecture 엔티티의 복잡한 조회를 담당합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class ArchitectureQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ArchitectureConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ArchitectureQueryDslRepository(
            JPAQueryFactory queryFactory, ArchitectureConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 Architecture 조회
     *
     * @param id Architecture ID
     * @return Architecture 엔티티 (Optional)
     */
    public Optional<ArchitectureJpaEntity> findById(Long id) {
        ArchitectureJpaEntity result =
                queryFactory
                        .selectFrom(architectureJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * ID로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param id Architecture ID
     * @return 존재 여부
     */
    public boolean existsById(Long id) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(architectureJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * 커서 기반 슬라이스 조건으로 Architecture 목록 조회
     *
     * <p>커서 기반 조회: ID 내림차순으로 정렬하고, 커서 ID보다 작은 ID를 조회합니다.
     *
     * <p>TechStack ID 필터를 지원합니다.
     *
     * @param criteria 슬라이스 조건 (커서 기반, techStackIds 필터 포함)
     * @return Architecture 엔티티 목록
     */
    public List<ArchitectureJpaEntity> findBySliceCriteria(ArchitectureSliceCriteria criteria) {
        return queryFactory
                .selectFrom(architectureJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.cursorLt(criteria),
                        conditionBuilder.techStackIdsIn(criteria))
                .orderBy(architectureJpaEntity.id.desc())
                .limit(criteria.cursorPageRequest().fetchSize())
                .fetch();
    }

    /**
     * 이름으로 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param name Architecture 이름
     * @return 존재 여부
     */
    public boolean existsByName(String name) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(architectureJpaEntity)
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
                        .from(architectureJpaEntity)
                        .where(
                                conditionBuilder.nameEq(name),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }

    /**
     * TechStack ID로 하위 Architecture 존재 여부 확인 (삭제되지 않은 것만)
     *
     * @param techStackId TechStack ID
     * @return 하위 리소스 존재 여부
     */
    public boolean existsByTechStackId(Long techStackId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(architectureJpaEntity)
                        .where(
                                conditionBuilder.techStackIdEq(techStackId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return result != null;
    }
}
