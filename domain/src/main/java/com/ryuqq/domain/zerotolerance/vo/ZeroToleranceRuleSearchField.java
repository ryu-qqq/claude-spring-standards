package com.ryuqq.domain.zerotolerance.vo;

/**
 * ZeroToleranceRuleSearchField - ZeroToleranceRule 검색 필드 Value Object
 *
 * @author ryu-qqq
 */
public enum ZeroToleranceRuleSearchField {
    TYPE("type");

    private final String fieldName;

    ZeroToleranceRuleSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
