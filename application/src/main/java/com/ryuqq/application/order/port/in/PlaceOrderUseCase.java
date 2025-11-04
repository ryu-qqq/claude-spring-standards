package com.ryuqq.application.order.port.in;

import com.ryuqq.application.order.dto.command.PlaceOrderCommand;
import com.ryuqq.application.order.dto.response.OrderResponse;

/**
 * Place Order UseCase Interface
 *
 * 주문 생성 Use Case의 진입점 (Hexagonal Architecture의 Input Port)
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public interface PlaceOrderUseCase {

    /**
     * 주문 생성
     *
     * 외부 결제 API 호출과 이메일 발송은 트랜잭션 밖에서 처리됩니다.
     *
     * @param command 주문 생성 명령
     * @return 생성된 주문 정보
     */
    OrderResponse placeOrder(PlaceOrderCommand command);
}
