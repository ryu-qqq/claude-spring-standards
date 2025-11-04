package com.ryuqq.application.order.port.out;

import java.math.BigDecimal;

/**
 * Email Out Port
 *
 * 이메일 발송 Port (Hexagonal Architecture의 Output Port)
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public interface EmailOutPort {

    /**
     * 주문 확인 이메일 발송
     *
     * ⚠️ 외부 API 호출이므로 @Transactional 밖에서 호출되어야 합니다.
     *
     * @param email 고객 이메일
     * @param orderId 주문 ID
     * @param totalAmount 주문 금액
     */
    void sendOrderConfirmation(String email, String orderId, BigDecimal totalAmount);
}
