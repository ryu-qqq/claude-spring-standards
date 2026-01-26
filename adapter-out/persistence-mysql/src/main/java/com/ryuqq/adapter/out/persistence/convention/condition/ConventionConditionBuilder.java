package com.ryuqq.adapter.out.persistence.convention.condition;

import static com.ryuqq.adapter.out.persistence.convention.entity.QConventionJpaEntity.conventionJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.convention.query.ConventionSliceCriteria;
import com.ryuqq.domain.module.id.ModuleId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ConventionConditionBuilder - Convention QueryDSL 조건 빌더
 *
 * <p>Convention 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConventionConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return conventionJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(ConventionSliceCriteria criteria) {
        return criteria.hasCursor()
                ? conventionJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * ID 일치 조건
     *
     * @param id Convention ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? conventionJpaEntity.id.eq(id) : null;
    }

    /**
     * 모듈 ID 일치 조건
     *
     * @param moduleId 모듈 ID
     * @return moduleId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression moduleIdEq(Long moduleId) {
        return moduleId != null ? conventionJpaEntity.moduleId.eq(moduleId) : null;
    }

    /**
     * 활성화 상태 조건
     *
     * @return isActive가 true인 조건
     */
    public BooleanExpression isActiveTrue() {
        return conventionJpaEntity.isActive.isTrue();
    }

    /**
     * Module ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * <p>SliceCriteria에 moduleIds가 있는 경우 IN 조건을 생성합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return moduleIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression moduleIdsIn(ConventionSliceCriteria criteria) {
        if (!criteria.hasModuleIds()) {
            return null;
        }
        List<Long> moduleIds = criteria.moduleIds().stream().map(ModuleId::value).toList();
        return conventionJpaEntity.moduleId.in(moduleIds);
    }
}
