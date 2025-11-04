package com.ryuqq.application.order.port.out;

import java.math.BigDecimal;

/**
 * Payment Out Port
 *
 * 외부 결제 API 호출 Port (Hexagonal Architecture의 Output Port)
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public interface PaymentOutPort {

    /**
     * 결제 처리
     *
     * ⚠️ 외부 API 호출이므로 @Transactional 밖에서 호출되어야 합니다.
     *
     * @param customerId 고객 ID
     * @param amount 결제 금액
     * @param paymentMethod 결제 수단
     * @return 결제 트랜잭션 ID
     */
    String processPayment(Long customerId, BigDecimal amount, String paymentMethod);
}
