package com.ryuqq.domain.architecture.vo;

import java.util.List;

/**
 * PatternPrinciples - 패턴 원칙 목록 Value Object
 *
 * <p>예: ["DIP", "SRP", "OCP", "ISP"]
 *
 * @author ryu-qqq
 */
public record PatternPrinciples(List<String> values) {

    public PatternPrinciples {
        values = values != null ? List.copyOf(values) : List.of();
    }

    public static PatternPrinciples of(List<String> values) {
        return new PatternPrinciples(values);
    }

    public static PatternPrinciples empty() {
        return new PatternPrinciples(List.of());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean contains(String principle) {
        return values.contains(principle);
    }
}
