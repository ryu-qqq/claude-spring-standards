package com.company.template.order.domain.model;

import java.util.Objects;

/**
 * CustomerId Value Object
 *
 * <p>고객 ID를 표현하는 Value Object입니다. 고객 ID는 양수 Long 값입니다.</p>
 *
 * <p><strong>Long FK 전략:</strong></p>
 * <ul>
 *   <li>다른 Aggregate(Customer)를 참조할 때 ID만 보유</li>
 *   <li>JPA 관계 어노테이션 사용 금지 (@ManyToOne, @OneToMany 등)</li>
 *   <li>Aggregate 경계를 명확히 유지</li>
 * </ul>
 *
 * @param value 고객 ID 값
 * @author Claude
 * @since 2025-10-17
 */
public record CustomerId(Long value) {

    /**
     * Compact Constructor - 유효성 검증.
     *
     * <p><strong>검증 규칙:</strong></p>
     * <ul>
     *   <li>null 불가</li>
     *   <li>양수만 허용 (value &gt; 0)</li>
     * </ul>
     *
     * @throws IllegalArgumentException value가 null이거나 양수가 아닐 경우
     * @author Claude
     * @since 2025-10-17
     */
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId는 null일 수 없습니다");
        if (value <= 0) {
            throw new IllegalArgumentException("CustomerId는 양수여야 합니다. 입력값: " + value);
        }
    }
}
