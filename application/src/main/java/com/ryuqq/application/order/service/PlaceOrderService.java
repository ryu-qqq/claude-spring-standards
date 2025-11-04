package com.ryuqq.application.order.service;

import com.ryuqq.application.order.dto.command.PlaceOrderCommand;
import com.ryuqq.application.order.dto.response.OrderResponse;
import com.ryuqq.application.order.port.in.PlaceOrderUseCase;
import com.ryuqq.application.order.port.out.EmailOutPort;
import com.ryuqq.application.order.port.out.OrderCommandOutPort;
import com.ryuqq.application.order.port.out.PaymentOutPort;
import com.ryuqq.domain.order.Order;
import com.ryuqq.domain.order.OrderId;
import com.ryuqq.domain.order.OrderLineItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Place Order Service
 *
 * 주문 생성 Use Case 구현
 *
 * Transaction 경계 관리:
 * - 외부 API 호출 (결제, 이메일)은 트랜잭션 밖에서 처리
 * - 주문 저장만 트랜잭션 내부에서 처리
 *
 * @author sangwon-ryu
 * @since 1.0
 */
@Service
public class PlaceOrderService implements PlaceOrderUseCase {

    private final OrderCommandOutPort orderCommandOutPort;
    private final PaymentOutPort paymentOutPort;
    private final EmailOutPort emailOutPort;

    public PlaceOrderService(
        OrderCommandOutPort orderCommandOutPort,
        PaymentOutPort paymentOutPort,
        EmailOutPort emailOutPort
    ) {
        this.orderCommandOutPort = orderCommandOutPort;
        this.paymentOutPort = paymentOutPort;
        this.emailOutPort = emailOutPort;
    }

    /**
     * 주문 생성 (Public Entry Point)
     *
     * ⚠️ 중요: @Transactional 없음
     * 외부 API 호출이 포함되므로 트랜잭션을 메서드 내부에서 명시적으로 분리
     *
     * @param command 주문 생성 명령
     * @return 생성된 주문 정보
     */
    @Override
    public OrderResponse placeOrder(PlaceOrderCommand command) {
        // 1. 외부 결제 API 호출 (트랜잭션 밖)
        String paymentTransactionId = callPaymentApi(command);

        // 2. 주문 저장 (트랜잭션 내부)
        Order savedOrder = executeInTransaction(command, paymentTransactionId);

        // 3. 이메일 발송 (트랜잭션 밖)
        sendConfirmationEmail(savedOrder);

        return OrderResponse.from(savedOrder);
    }

    /**
     * 외부 결제 API 호출
     *
     * ✅ 트랜잭션 밖에서 실행
     *
     * @param command 주문 명령
     * @return 결제 트랜잭션 ID
     */
    private String callPaymentApi(PlaceOrderCommand command) {
        return paymentOutPort.processPayment(
            command.getCustomerId(),
            command.getTotalAmount(),
            command.getPaymentMethod()
        );
    }

    /**
     * 주문 저장 (트랜잭션 내부)
     *
     * ✅ @Transactional 적용
     * ✅ 외부 API 호출 없음 (DB 저장만)
     *
     * @param command 주문 명령
     * @param paymentTransactionId 결제 트랜잭션 ID
     * @return 저장된 Order
     */
    @Transactional
    public Order executeInTransaction(PlaceOrderCommand command, String paymentTransactionId) {
        // Domain 객체 생성
        List<OrderLineItem> lineItems = command.getLineItems().stream()
            .map(item -> OrderLineItem.of(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice()
            ))
            .collect(Collectors.toList());

        Order order = Order.placeOrder(
            OrderId.newOrderId(),
            command.getCustomerId(),
            command.getCustomerName(),
            command.getCustomerEmail(),
            command.getShippingAddress(),
            command.getShippingZipCode(),
            command.getShippingCity(),
            lineItems
        );

        // DB 저장 (Port 호출)
        return orderCommandOutPort.save(order);
    }

    /**
     * 주문 확인 이메일 발송
     *
     * ✅ 트랜잭션 밖에서 실행
     *
     * @param order 저장된 주문
     */
    private void sendConfirmationEmail(Order order) {
        emailOutPort.sendOrderConfirmation(
            order.getCustomerEmail(),
            order.getOrderId().getValue(),
            order.getTotalAmount()
        );
    }
}
