package com.ryuqq.domain.layerdependency.aggregate;

import com.ryuqq.domain.layerdependency.vo.ConditionDescription;
import com.ryuqq.domain.layerdependency.vo.DependencyType;
import com.ryuqq.domain.layerdependency.vo.LayerType;

/**
 * LayerDependencyRuleUpdateData - 레이어 의존성 규칙 수정 데이터
 *
 * @author ryu-qqq
 */
public record LayerDependencyRuleUpdateData(
        LayerType fromLayer,
        LayerType toLayer,
        DependencyType dependencyType,
        ConditionDescription conditionDescription) {

    public LayerDependencyRuleUpdateData {
        if (fromLayer == null) {
            throw new IllegalArgumentException("fromLayer must not be null");
        }
        if (toLayer == null) {
            throw new IllegalArgumentException("toLayer must not be null");
        }
        if (dependencyType == null) {
            throw new IllegalArgumentException("dependencyType must not be null");
        }
        if (conditionDescription == null) {
            conditionDescription = ConditionDescription.empty();
        }
    }
}
