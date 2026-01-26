package com.ryuqq.adapter.out.persistence.onboardingcontext.condition;

import static com.ryuqq.adapter.out.persistence.onboardingcontext.entity.QOnboardingContextJpaEntity.onboardingContextJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.onboardingcontext.query.OnboardingContextSliceCriteria;
import com.ryuqq.domain.onboardingcontext.vo.ContextType;
import com.ryuqq.domain.techstack.id.TechStackId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * OnboardingContextConditionBuilder - OnboardingContext QueryDSL 조건 빌더
 *
 * <p>OnboardingContext 엔티티 조회에 사용되는 BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class OnboardingContextConditionBuilder {

    /**
     * 삭제되지 않은 레코드 조건
     *
     * @return deletedAt IS NULL 조건
     */
    public BooleanExpression deletedAtIsNull() {
        return onboardingContextJpaEntity.deletedAt.isNull();
    }

    /**
     * 커서 기반 페이징 조건 (ID 내림차순)
     *
     * <p>커서가 있는 경우 해당 ID보다 작은 레코드를 조회합니다.
     *
     * @param criteria 슬라이스 조회 조건
     * @return ID < cursor 조건 (커서가 없으면 null 반환)
     */
    public BooleanExpression cursorLt(OnboardingContextSliceCriteria criteria) {
        return criteria.cursorPageRequest().hasCursor()
                ? onboardingContextJpaEntity.id.lt(criteria.cursorPageRequest().cursor())
                : null;
    }

    /**
     * TechStack ID 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return techStackIds IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression techStackIdsIn(OnboardingContextSliceCriteria criteria) {
        if (!criteria.hasTechStackIds()) {
            return null;
        }
        List<Long> techStackIds = criteria.techStackIds().stream().map(TechStackId::value).toList();
        return onboardingContextJpaEntity.techStackId.in(techStackIds);
    }

    /**
     * Context Type 목록 IN 조건 (SliceCriteria 기반)
     *
     * @param criteria 슬라이스 조회 조건
     * @return contextTypes IN 조건 (필터가 없으면 null 반환)
     */
    public BooleanExpression contextTypesIn(OnboardingContextSliceCriteria criteria) {
        if (!criteria.hasContextTypes()) {
            return null;
        }
        List<String> contextTypes =
                criteria.contextTypes().stream().map(ContextType::name).toList();
        return onboardingContextJpaEntity.contextType.in(contextTypes);
    }

    /**
     * TechStack ID 일치 조건
     *
     * @param techStackId TechStack ID
     * @return techStackId 일치 조건
     */
    public BooleanExpression techStackIdEq(Long techStackId) {
        return onboardingContextJpaEntity.techStackId.eq(techStackId);
    }

    /**
     * ID 일치 조건
     *
     * @param id OnboardingContext ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? onboardingContextJpaEntity.id.eq(id) : null;
    }
}
