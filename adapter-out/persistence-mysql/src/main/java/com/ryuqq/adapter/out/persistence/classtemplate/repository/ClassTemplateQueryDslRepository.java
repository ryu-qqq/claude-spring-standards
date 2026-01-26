package com.ryuqq.adapter.out.persistence.classtemplate.repository;

import static com.ryuqq.adapter.out.persistence.classtemplate.entity.QClassTemplateJpaEntity.classTemplateJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.adapter.out.persistence.classtemplate.condition.ClassTemplateConditionBuilder;
import com.ryuqq.adapter.out.persistence.classtemplate.entity.ClassTemplateJpaEntity;
import com.ryuqq.domain.classtemplate.query.ClassTemplateSliceCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * ClassTemplateQueryDslRepository - 클래스 템플릿 QueryDSL 레포지토리
 *
 * <p>복잡한 쿼리를 위한 QueryDSL 기반 조회를 제공합니다.
 *
 * @author ryu-qqq
 */
@Repository
public class ClassTemplateQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final ClassTemplateConditionBuilder conditionBuilder;

    /**
     * 생성자 주입
     *
     * @param queryFactory JPAQueryFactory
     * @param conditionBuilder 조건 빌더
     */
    public ClassTemplateQueryDslRepository(
            JPAQueryFactory queryFactory, ClassTemplateConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * ID로 클래스 템플릿 조회
     *
     * @param id 클래스 템플릿 ID
     * @return 클래스 템플릿 Optional
     */
    public Optional<ClassTemplateJpaEntity> findById(Long id) {
        ClassTemplateJpaEntity entity =
                queryFactory
                        .selectFrom(classTemplateJpaEntity)
                        .where(conditionBuilder.idEq(id), conditionBuilder.deletedAtIsNull())
                        .fetchOne();
        return Optional.ofNullable(entity);
    }

    /**
     * 커서 기반 슬라이스 조회 (레거시 - 호환성 유지)
     *
     * @param structureId 패키지 구조 ID (nullable)
     * @param cursor 커서 (마지막 ID, nullable)
     * @param fetchSize 조회 크기
     * @return 클래스 템플릿 목록
     */
    public List<ClassTemplateJpaEntity> findBySlice(Long structureId, Long cursor, int fetchSize) {
        return queryFactory
                .selectFrom(classTemplateJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.structureIdEq(structureId),
                        conditionBuilder.cursorLt(cursor))
                .orderBy(classTemplateJpaEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    /**
     * 슬라이스 조건으로 ClassTemplate 목록 조회 (커서 기반)
     *
     * @param criteria 슬라이스 조건
     * @return ClassTemplate 엔티티 목록
     */
    public List<ClassTemplateJpaEntity> findBySliceCriteria(ClassTemplateSliceCriteria criteria) {
        return queryFactory
                .selectFrom(classTemplateJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.structureIdsIn(criteria),
                        conditionBuilder.classTypeIdsIn(criteria),
                        conditionBuilder.cursorLt(criteria))
                .orderBy(classTemplateJpaEntity.id.desc())
                .limit(criteria.fetchSize())
                .fetch();
    }

    /**
     * 패키지 구조 내 템플릿 코드 존재 여부 확인
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @return 존재하면 true
     */
    public boolean existsByStructureIdAndTemplateCode(Long structureId, String templateCode) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(classTemplateJpaEntity)
                        .where(
                                classTemplateJpaEntity.structureId.eq(structureId),
                                conditionBuilder.templateCodeEq(templateCode),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 패키지 구조 내 템플릿 코드 존재 여부 확인 (특정 ID 제외)
     *
     * @param structureId 패키지 구조 ID
     * @param templateCode 템플릿 코드
     * @param excludeId 제외할 클래스 템플릿 ID
     * @return 존재하면 true
     */
    public boolean existsByStructureIdAndTemplateCodeExcluding(
            Long structureId, String templateCode, Long excludeId) {
        Integer fetchOne =
                queryFactory
                        .selectOne()
                        .from(classTemplateJpaEntity)
                        .where(
                                classTemplateJpaEntity.structureId.eq(structureId),
                                conditionBuilder.templateCodeEq(templateCode),
                                conditionBuilder.idNe(excludeId),
                                conditionBuilder.deletedAtIsNull())
                        .fetchFirst();
        return fetchOne != null;
    }

    /**
     * 패키지 구조 ID로 클래스 템플릿 목록 조회
     *
     * @param structureId 패키지 구조 ID
     * @return 클래스 템플릿 목록
     */
    public List<ClassTemplateJpaEntity> findByStructureId(Long structureId) {
        return queryFactory
                .selectFrom(classTemplateJpaEntity)
                .where(
                        classTemplateJpaEntity.structureId.eq(structureId),
                        conditionBuilder.deletedAtIsNull())
                .orderBy(classTemplateJpaEntity.id.asc())
                .fetch();
    }

    /**
     * 키워드 검색
     *
     * <p>templateCode, description 필드에서 키워드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @param structureId 패키지 구조 ID (nullable)
     * @return 검색된 클래스 템플릿 목록
     */
    public List<ClassTemplateJpaEntity> searchByKeyword(String keyword, Long structureId) {
        return queryFactory
                .selectFrom(classTemplateJpaEntity)
                .where(
                        conditionBuilder.deletedAtIsNull(),
                        conditionBuilder.structureIdEq(structureId),
                        conditionBuilder.keywordContains(keyword))
                .orderBy(classTemplateJpaEntity.id.asc())
                .fetch();
    }
}
