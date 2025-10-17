package com.company.template.order.domain.model;

/**
 * Order 상태 Enum
 *
 * <p>주문의 생명주기를 나타내는 상태 값입니다.</p>
 *
 * <p><strong>상태 전이 규칙:</strong></p>
 * <ul>
 *   <li>PENDING → CONFIRMED (주문 확정)</li>
 *   <li>PENDING → CANCELLED (주문 취소)</li>
 *   <li>CONFIRMED → CANCELLED (주문 취소)</li>
 *   <li>CANCELLED → (전이 불가)</li>
 * </ul>
 *
 * @author Claude
 * @since 2025-10-17
 */
public enum OrderStatus {

    /**
     * 주문 생성 상태.
     *
     * <p>주문이 생성되었지만 아직 확정되지 않은 상태입니다.</p>
     */
    PENDING,

    /**
     * 주문 확정 상태.
     *
     * <p>주문이 확정되어 처리 중인 상태입니다.</p>
     */
    CONFIRMED,

    /**
     * 주문 취소 상태.
     *
     * <p>주문이 취소되어 더 이상 처리되지 않는 상태입니다.</p>
     * <p>이 상태에서는 다른 상태로 전이할 수 없습니다 (최종 상태).</p>
     */
    CANCELLED
}
