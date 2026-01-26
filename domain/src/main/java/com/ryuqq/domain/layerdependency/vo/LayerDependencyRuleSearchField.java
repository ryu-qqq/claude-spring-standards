package com.ryuqq.domain.layerdependency.vo;

/**
 * LayerDependencyRuleSearchField - LayerDependencyRule 검색 필드 Value Object
 *
 * @author ryu-qqq
 */
public enum LayerDependencyRuleSearchField {
    CONDITION_DESCRIPTION("conditionDescription");

    private final String fieldName;

    LayerDependencyRuleSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
