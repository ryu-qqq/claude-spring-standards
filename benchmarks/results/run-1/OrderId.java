package com.company.template.order.domain.model;

import java.util.Objects;

/**
 * OrderId Value Object
 *
 * <p>주문 ID를 표현하는 Value Object입니다. 주문 ID는 "ORD-YYYYMMDD-XXX" 형식입니다.</p>
 *
 * <p><strong>포맷 규칙:</strong></p>
 * <ul>
 *   <li>형식: ORD-YYYYMMDD-XXX</li>
 *   <li>예시: ORD-20251017-001</li>
 *   <li>YYYYMMDD: 주문 생성 날짜</li>
 *   <li>XXX: 일련번호 (001-999)</li>
 * </ul>
 *
 * @param value 주문 ID 값
 * @author Claude
 * @since 2025-10-17
 */
public record OrderId(String value) {

    /**
     * Compact Constructor - 유효성 검증.
     *
     * <p><strong>검증 규칙:</strong></p>
     * <ul>
     *   <li>null 불가</li>
     *   <li>형식: ^ORD-\\d{8}-\\d{3}$ (정규식)</li>
     * </ul>
     *
     * @throws IllegalArgumentException value가 null이거나 형식이 맞지 않을 경우
     * @author Claude
     * @since 2025-10-17
     */
    public OrderId {
        Objects.requireNonNull(value, "OrderId는 null일 수 없습니다");
        if (!value.matches("^ORD-\\d{8}-\\d{3}$")) {
            throw new IllegalArgumentException(
                    "OrderId 형식이 올바르지 않습니다. 예상 형식: ORD-YYYYMMDD-XXX, 입력값: " + value
            );
        }
    }
}
