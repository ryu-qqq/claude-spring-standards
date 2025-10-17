package com.company.template.order.domain.model;

import java.util.Objects;

/**
 * Order 식별자 Value Object
 *
 * <p>주문 ID 형식: ORD-{yyyyMMdd}-{seq}</p>
 * <p>예시: ORD-20251017-001</p>
 *
 * <p><strong>규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Java Record 사용 (불변성 보장)</li>
 *   <li>✅ 생성자에서 유효성 검증</li>
 *   <li>✅ null 방어</li>
 * </ul>
 *
 * @param value OrderId 값 (형식: ORD-yyyyMMdd-seq)
 * @author Claude
 * @since 2025-10-17
 */
public record OrderId(String value) {

    /**
     * OrderId의 compact 생성자 (유효성 검증).
     *
     * <p>비즈니스 규칙:</p>
     * <ul>
     *   <li>null 불가</li>
     *   <li>형식: ORD-{8자리 숫자}-{3자리 숫자}</li>
     * </ul>
     *
     * @throws NullPointerException value가 null인 경우
     * @throws IllegalArgumentException OrderId 형식이 유효하지 않은 경우
     * @author Claude
     * @since 2025-10-17
     */
    public OrderId {
        Objects.requireNonNull(value, "OrderId cannot be null");
        if (!value.matches("^ORD-\\d{8}-\\d{3}$")) {
            throw new IllegalArgumentException(
                "Invalid OrderId format. Expected: ORD-yyyyMMdd-seq (e.g., ORD-20251017-001), but was: " + value
            );
        }
    }

    /**
     * OrderId를 문자열로 반환합니다.
     *
     * @return OrderId 문자열 값
     * @author Claude
     * @since 2025-10-17
     */
    @Override
    public String toString() {
        return value;
    }
}
