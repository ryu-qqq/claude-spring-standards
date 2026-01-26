package com.ryuqq.domain.techstack.vo;

import java.util.List;

/**
 * LanguageFeatures - 언어 기능 목록 Value Object
 *
 * <p>예: ["VIRTUAL_THREAD", "RECORD", "PATTERN_MATCHING"]
 *
 * @author ryu-qqq
 */
public record LanguageFeatures(List<String> values) {

    public LanguageFeatures {
        values = values != null ? List.copyOf(values) : List.of();
    }

    public static LanguageFeatures of(List<String> values) {
        return new LanguageFeatures(values);
    }

    public static LanguageFeatures empty() {
        return new LanguageFeatures(List.of());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean contains(String feature) {
        return values.contains(feature);
    }
}
