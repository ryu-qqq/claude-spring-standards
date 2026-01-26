package com.ryuqq.adapter.out.persistence.architecture.condition;

import static com.ryuqq.adapter.out.persistence.architecture.entity.QArchitectureJpaEntity.architectureJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.architecture.query.ArchitectureSliceCriteria;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ArchitectureConditionBuilder - Architecture QueryDSL 조건 빌더
 *
 * <p>Architecture 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ArchitectureConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return architectureJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(ArchitectureSliceCriteria criteria) {
        return criteria.cursorPageRequest().hasCursor()
                ? architectureJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * TechStack ID 일치 조건
     *
     * @param techStackId TechStack ID
     * @return techStackId 일치 조건
     */
    public BooleanExpression techStackIdEq(Long techStackId) {
        return architectureJpaEntity.techStackId.eq(techStackId);
    }

    /**
     * TechStack ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 techStackIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return techStackIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression techStackIdsIn(ArchitectureSliceCriteria criteria) {
        if (!criteria.hasTechStackIds()) {
            return null;
        }
        List<Long> techStackIds = criteria.techStackIds().stream().map(TechStackId::value).toList();
        return architectureJpaEntity.techStackId.in(techStackIds);
    }

    /**
     * 키워드 검색 조건 (이름 필드)
     *
     * <p>대소문자 구분 없이 이름에 키워드가 포함된 레코드를 검색합니다.
     *
     * @param keyword 검색 키워드
     * @return 이름에 키워드 포함 조건 (nullable이면 null 반환)
     */
    public BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return architectureJpaEntity.name.containsIgnoreCase(keyword);
    }

    /**
     * 패턴 타입 일치 조건
     *
     * @param patternType 패턴 타입 문자열
     * @return patternType 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression patternTypeEq(String patternType) {
        if (patternType == null || patternType.isBlank()) {
            return null;
        }
        return architectureJpaEntity.patternType.eq(patternType);
    }

    /**
     * ID 일치 조건
     *
     * @param id Architecture ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? architectureJpaEntity.id.eq(id) : null;
    }

    /**
     * 이름 일치 조건
     *
     * @param name Architecture 이름
     * @return name 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression nameEq(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return architectureJpaEntity.name.eq(name);
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? architectureJpaEntity.id.ne(excludeId) : null;
    }
}
