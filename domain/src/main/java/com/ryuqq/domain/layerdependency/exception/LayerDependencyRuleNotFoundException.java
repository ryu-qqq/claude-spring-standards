package com.ryuqq.domain.layerdependency.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * LayerDependencyRuleNotFoundException - 레이어 의존성 규칙 미존재 예외
 *
 * @author ryu-qqq
 */
public class LayerDependencyRuleNotFoundException extends DomainException {

    public LayerDependencyRuleNotFoundException(Long layerDependencyRuleId) {
        super(
                LayerDependencyRuleErrorCode.LAYER_DEPENDENCY_RULE_NOT_FOUND,
                String.format("LayerDependencyRule not found: %d", layerDependencyRuleId),
                Map.of("layerDependencyRuleId", layerDependencyRuleId));
    }
}
