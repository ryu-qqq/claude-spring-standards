package com.ryuqq.adapter.out.persistence.layerdependency.condition;

import static com.ryuqq.adapter.out.persistence.layerdependency.entity.QLayerDependencyRuleJpaEntity.layerDependencyRuleJpaEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.domain.layerdependency.vo.LayerDependencyRuleSearchField;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleConditionBuilder - 레이어 의존성 규칙 QueryDSL 조건 빌더
 *
 * <p>BooleanExpression 조건을 재사용 가능한 형태로 제공합니다.
 *
 * <p>Note: LayerDependencyRule 도메인은 soft delete를 사용하지 않습니다.
 *
 * @author ryu-qqq
 */
@Component
public class LayerDependencyRuleConditionBuilder {

    /**
     * ID 일치 조건
     *
     * @param id 레이어 의존성 규칙 ID
     * @return ID 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression idEq(Long id) {
        return id != null ? layerDependencyRuleJpaEntity.id.eq(id) : null;
    }

    /**
     * 아키텍처 ID 일치 조건
     *
     * @param architectureId 아키텍처 ID
     * @return architectureId 일치 조건 (nullable이면 null 반환)
     */
    public BooleanExpression architectureIdEq(Long architectureId) {
        return architectureId != null
                ? layerDependencyRuleJpaEntity.architectureId.eq(architectureId)
                : null;
    }

    /**
     * 아키텍처 ID 목록 IN 조건
     *
     * @param architectureIds 아키텍처 ID 목록
     * @return architectureId IN 조건 (nullable이면 null 반환)
     */
    public BooleanExpression architectureIdsIn(List<Long> architectureIds) {
        return architectureIds != null && !architectureIds.isEmpty()
                ? layerDependencyRuleJpaEntity.architectureId.in(architectureIds)
                : null;
    }

    /**
     * 의존성 타입 목록 IN 조건
     *
     * @param dependencyTypes 의존성 타입 목록
     * @return dependencyType IN 조건 (nullable이면 null 반환)
     */
    public BooleanExpression dependencyTypesIn(List<String> dependencyTypes) {
        return dependencyTypes != null && !dependencyTypes.isEmpty()
                ? layerDependencyRuleJpaEntity.dependencyType.in(dependencyTypes)
                : null;
    }

    /**
     * 검색 조건
     *
     * @param searchField 검색 필드
     * @param searchWord 검색어
     * @return 검색 조건 (nullable이면 null 반환)
     */
    public BooleanExpression searchContains(
            LayerDependencyRuleSearchField searchField, String searchWord) {
        if (searchField == null || searchWord == null || searchWord.isBlank()) {
            return null;
        }
        return switch (searchField) {
            case CONDITION_DESCRIPTION ->
                    layerDependencyRuleJpaEntity.conditionDescription.containsIgnoreCase(
                            searchWord);
        };
    }

    /**
     * 커서 기반 페이징 조건
     *
     * @param cursor 커서 (마지막 ID)
     * @return ID < cursor 조건 (nullable이면 null 반환)
     */
    public BooleanExpression cursorLt(Long cursor) {
        return cursor != null ? layerDependencyRuleJpaEntity.id.lt(cursor) : null;
    }
}
