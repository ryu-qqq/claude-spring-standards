package com.ryuqq.adapter.out.persistence.techstack.condition;

import static com.ryuqq.adapter.out.persistence.techstack.entity.QTechStackJpaEntity.techStackJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.techstack.query.TechStackSliceCriteria;
import com.ryuqq.domain.techstack.vo.PlatformType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TechStackConditionBuilder - TechStack QueryDSL 조건 빌더
 *
 * <p>TechStack 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class TechStackConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return techStackJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(TechStackSliceCriteria criteria) {
        return criteria.hasCursor()
                ? techStackJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * ID 일치 조건
     *
     * @param id TechStack ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? techStackJpaEntity.id.eq(id) : null;
    }

    /**
     * 이름 일치 조건
     *
     * @param name TechStack 이름
     * @return name 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression nameEq(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return techStackJpaEntity.name.eq(name);
    }

    /**
     * ID 불일치 조건 (특정 ID 제외)
     *
     * @param excludeId 제외할 ID
     * @return ID 불일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idNe(Long excludeId) {
        return excludeId != null ? techStackJpaEntity.id.ne(excludeId) : null;
    }

    // ==================== Slice Criteria Filter Methods ====================

    /**
     * 슬라이스 조회 상태 필터 조건
     *
     * @param criteria 슬라이스 조회 조건
     * @return status 일치 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression statusEqFromCriteria(TechStackSliceCriteria criteria) {
        return criteria.hasStatus() ? techStackJpaEntity.status.eq(criteria.status().name()) : null;
    }

    /**
     * 슬라이스 조회 플랫폼 타입 필터 조건 (IN 조건)
     *
     * @param criteria 슬라이스 조회 조건
     * @return platformType IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression platformTypeIn(TechStackSliceCriteria criteria) {
        if (!criteria.hasPlatformTypes()) {
            return null;
        }
        List<String> platformTypeNames =
                criteria.platformTypes().stream().map(PlatformType::name).toList();
        return techStackJpaEntity.platformType.in(platformTypeNames);
    }
}
