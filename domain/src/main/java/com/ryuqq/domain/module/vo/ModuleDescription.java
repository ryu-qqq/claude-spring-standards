package com.ryuqq.domain.module.vo;

/**
 * ModuleDescription - 모듈 설명 Value Object
 *
 * @author ryu-qqq
 */
public record ModuleDescription(String value) {

    public ModuleDescription {
        if (value != null && value.isBlank()) {
            value = null;
        }
    }

    public static ModuleDescription of(String value) {
        return new ModuleDescription(value);
    }

    public static ModuleDescription empty() {
        return new ModuleDescription(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
