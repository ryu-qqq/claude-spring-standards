package com.company.template.order.domain.model;

import java.util.Objects;

/**
 * Order 식별자
 *
 * <p>주문 ID는 "ORD-YYYYMMDD-NNN" 형식을 따릅니다.</p>
 *
 * <p>예시: ORD-20251017-001</p>
 *
 * @param value ID 값
 * @author Claude
 * @since 2025-10-17
 */
public record OrderId(String value) {
    /**
     * OrderId를 생성합니다.
     *
     * @param value ID 값
     * @throws IllegalArgumentException ID가 null이거나 잘못된 형식인 경우
     */
    public OrderId {
        Objects.requireNonNull(value, "OrderId cannot be null");
        if (!value.matches("^ORD-\\d{8}-\\d{3}$")) {
            throw new IllegalArgumentException(
                "Invalid OrderId format. Expected format: ORD-YYYYMMDD-NNN, but got: " + value
            );
        }
    }
}
