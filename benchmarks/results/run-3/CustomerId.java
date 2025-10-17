package com.company.template.order.domain.model;

import java.util.Objects;

/**
 * Customer 식별자 Value Object
 *
 * <p>고객 ID는 양수 Long 값을 가집니다.</p>
 *
 * <p><strong>규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Java Record 사용 (불변성 보장)</li>
 *   <li>✅ 생성자에서 유효성 검증</li>
 *   <li>✅ null 방어 및 양수 검증</li>
 *   <li>✅ Long FK 전략 - 다른 Aggregate 참조는 ID만 저장</li>
 * </ul>
 *
 * @param value CustomerId 값 (양수)
 * @author Claude
 * @since 2025-10-17
 */
public record CustomerId(Long value) {

    /**
     * CustomerId의 compact 생성자 (유효성 검증).
     *
     * <p>비즈니스 규칙:</p>
     * <ul>
     *   <li>null 불가</li>
     *   <li>양수(0보다 큰 값)만 허용</li>
     * </ul>
     *
     * @throws NullPointerException value가 null인 경우
     * @throws IllegalArgumentException value가 0 이하인 경우
     * @author Claude
     * @since 2025-10-17
     */
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException(
                "CustomerId must be positive, but was: " + value
            );
        }
    }

    /**
     * CustomerId를 문자열로 반환합니다.
     *
     * @return CustomerId 문자열 표현
     * @author Claude
     * @since 2025-10-17
     */
    @Override
    public String toString() {
        return "CustomerId{" + value + "}";
    }
}
