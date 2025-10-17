package com.company.template.order.domain.model;

import java.util.Objects;

/**
 * Customer 식별자
 *
 * <p>고객 ID는 양수 Long 값을 가집니다.</p>
 *
 * @param value ID 값
 * @author Claude
 * @since 2025-10-17
 */
public record CustomerId(Long value) {
    /**
     * CustomerId를 생성합니다.
     *
     * @param value ID 값
     * @throws IllegalArgumentException ID가 null이거나 0 이하인 경우
     */
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException(
                "CustomerId must be positive, but got: " + value
            );
        }
    }
}
