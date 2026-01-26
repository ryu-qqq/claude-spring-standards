package com.ryuqq.domain.techstack.vo;

import java.util.List;

/**
 * FrameworkModules - 프레임워크 모듈 목록 Value Object
 *
 * <p>예: ["WEB", "JPA", "SECURITY", "ACTUATOR"]
 *
 * @author ryu-qqq
 */
public record FrameworkModules(List<String> values) {

    public FrameworkModules {
        values = values != null ? List.copyOf(values) : List.of();
    }

    public static FrameworkModules of(List<String> values) {
        return new FrameworkModules(values);
    }

    public static FrameworkModules empty() {
        return new FrameworkModules(List.of());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean contains(String module) {
        return values.contains(module);
    }
}
