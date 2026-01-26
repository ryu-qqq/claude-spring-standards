package com.ryuqq.adapter.out.persistence.packagestructure.condition;

import static com.ryuqq.adapter.out.persistence.packagestructure.entity.QPackageStructureJpaEntity.packageStructureJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.packagestructure.query.PackageStructureSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PackageStructureConditionBuilder - 패키지 구조 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 */
@Component
public class PackageStructureConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return packageStructureJpaEntity.deletedAt.isNull();
    }

    /**
     * 모듈 ID 일치 조건
     *
     * @param moduleId 모듈 ID
     * @return moduleId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression moduleIdEq(Long moduleId) {
        return moduleId != null ? packageStructureJpaEntity.moduleId.eq(moduleId) : null;
    }

    /**
     * 모듈 ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 moduleIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return moduleIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression moduleIdsIn(PackageStructureSliceCriteria criteria) {
        if (!criteria.hasModuleIds()) {
            return null;
        }
        List<Long> moduleIdValues = criteria.moduleIds().stream().map(id -> id.value()).toList();
        return packageStructureJpaEntity.moduleId.in(moduleIdValues);
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(PackageStructureSliceCriteria criteria) {
        return criteria.hasCursor()
                ? packageStructureJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * 커서 기반 페이징 조건 (레거시 - 호환성 유지)
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? packageStructureJpaEntity.id.lt(cursor) : null;
    }
}
