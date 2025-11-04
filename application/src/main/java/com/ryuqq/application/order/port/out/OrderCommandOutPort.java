package com.ryuqq.application.order.port.out;

import com.ryuqq.domain.order.Order;

/**
 * Order Command Out Port
 *
 * 주문 저장 Port (Hexagonal Architecture의 Output Port)
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public interface OrderCommandOutPort {

    /**
     * 주문 저장
     *
     * @param order 저장할 주문
     * @return 저장된 주문
     */
    Order save(Order order);
}
