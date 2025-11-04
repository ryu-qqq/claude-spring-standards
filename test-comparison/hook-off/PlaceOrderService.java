package com.ryuqq.application.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Place Order Service (Hook OFF 스타일)
 *
 * ❌ @Transactional 내 외부 API 호출
 * ❌ Private 메서드에 @Transactional
 * ❌ Lombok 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor  // ❌ Lombok
public class PlaceOrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final EmailClient emailClient;

    /**
     * 주문 생성
     *
     * ❌ @Transactional 내 외부 API 호출 (paymentClient, emailClient)
     * ❌ Transaction 경계가 너무 김 (전체 프로세스)
     */
    @Transactional  // ❌ Public method에 @Transactional + 외부 API 호출
    public OrderResponse placeOrder(PlaceOrderCommand command) {
        log.info("Placing order for customer: {}", command.getCustomerId());

        // ❌ Transaction 내에서 외부 결제 API 호출 (네트워크 I/O)
        String paymentId = paymentClient.processPayment(
            command.getCustomerId(),
            command.getTotalAmount()
        );

        // Transaction 내에서 DB 저장
        Order order = new Order();
        order.setCustomerId(command.getCustomerId());
        order.setPaymentId(paymentId);
        order.setStatus("PLACED");
        order.setTotalAmount(command.getTotalAmount());

        orderRepository.save(order);

        // ❌ Transaction 내에서 외부 이메일 API 호출 (네트워크 I/O)
        emailClient.sendOrderConfirmation(
            order.getCustomerId(),
            order.getOrderId()
        );

        // ❌ Private 메서드 호출 (Spring proxy 작동 안 함)
        updateInventory(command);

        return OrderResponse.from(order);
    }

    /**
     * ❌ Private 메서드에 @Transactional
     * ❌ Spring Proxy는 public method만 작동
     * ❌ 이 @Transactional은 무시됨
     */
    @Transactional  // ❌ Private + @Transactional = 작동 안 함
    private void updateInventory(PlaceOrderCommand command) {
        // Transaction이 작동하지 않음
        // Spring proxy는 private method를 가로챌 수 없음
        log.info("Updating inventory...");
    }

    /**
     * 주문 취소
     *
     * ❌ Transaction 내에서 외부 API 호출
     */
    @Transactional
    public void cancelOrder(String orderId, String reason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        // ❌ Transaction 내에서 외부 결제 취소 API 호출
        paymentClient.refundPayment(order.getPaymentId());

        order.setStatus("CANCELLED");
        order.setCancellationReason(reason);

        orderRepository.save(order);

        // ❌ Transaction 내에서 외부 이메일 API 호출
        emailClient.sendCancellationNotification(
            order.getCustomerId(),
            orderId
        );
    }
}

// ❌ Lombok 사용
@lombok.Data
class PlaceOrderCommand {
    private Long customerId;
    private java.math.BigDecimal totalAmount;
    private java.util.List<OrderLineItem> lineItems;
}

@lombok.Data
class OrderResponse {
    private String orderId;
    private String status;
    private String paymentId;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setStatus(order.getStatus());
        response.setPaymentId(order.getPaymentId());
        return response;
    }
}

@lombok.Data
class Order {
    private String orderId;
    private Long customerId;
    private String paymentId;
    private String status;
    private java.math.BigDecimal totalAmount;
    private String cancellationReason;
}

interface OrderRepository {
    Order save(Order order);
    java.util.Optional<Order> findById(String orderId);
}

interface PaymentClient {
    String processPayment(Long customerId, java.math.BigDecimal amount);
    void refundPayment(String paymentId);
}

interface EmailClient {
    void sendOrderConfirmation(Long customerId, String orderId);
    void sendCancellationNotification(Long customerId, String orderId);
}

class OrderLineItem {
    private Long productId;
    private int quantity;
}
