package com.ryuqq.domain.packagepurpose.vo;

import java.util.List;

/**
 * AllowedClassTypes - 허용 클래스 타입 목록 Value Object
 *
 * <p>예: ["AGGREGATE_ROOT", "ENTITY", "VALUE_OBJECT"]
 *
 * @author ryu-qqq
 */
public record AllowedClassTypes(List<String> values) {

    public AllowedClassTypes {
        values = values != null ? List.copyOf(values) : List.of();
    }

    public static AllowedClassTypes of(List<String> values) {
        return new AllowedClassTypes(values);
    }

    public static AllowedClassTypes empty() {
        return new AllowedClassTypes(List.of());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean contains(String classType) {
        return values.contains(classType);
    }
}
