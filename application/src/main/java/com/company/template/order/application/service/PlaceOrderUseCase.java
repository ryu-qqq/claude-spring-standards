package com.company.template.order.application.service;

import com.company.template.order.application.port.in.PlaceOrderCommand;
import com.company.template.order.application.port.in.PlaceOrderResponse;
import com.company.template.order.application.port.out.LoadCustomerPort;
import com.company.template.order.application.port.out.SaveOrderPort;
import com.company.template.order.domain.model.Order;
import com.company.template.order.domain.model.OrderId;
import com.company.template.order.domain.model.CustomerId;
import com.company.template.order.domain.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * PlaceOrder UseCase Implementation
 *
 * <p>주문 생성 UseCase를 구현합니다.
 * Command를 받아 Domain 로직을 실행하고 Response를 반환합니다.
 *
 * <h3>트랜잭션 경계</h3>
 * <ul>
 *   <li>외부 API 호출 없음 (고객 조회는 내부 DB)</li>
 *   <li>Domain 로직 + DB 저장만 포함</li>
 * </ul>
 *
 * @author Claude
 * @since 2025-10-17
 */
@Service
public class PlaceOrderUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final SaveOrderPort saveOrderPort;

    /**
     * Constructor Injection (Field Injection 금지)
     *
     * @param loadCustomerPort 고객 조회 Port
     * @param saveOrderPort 주문 저장 Port
     */
    public PlaceOrderUseCase(
            LoadCustomerPort loadCustomerPort,
            SaveOrderPort saveOrderPort) {
        this.loadCustomerPort = loadCustomerPort;
        this.saveOrderPort = saveOrderPort;
    }

    /**
     * 주문 생성 실행
     *
     * <p>트랜잭션 내에서 다음을 수행합니다:
     * <ol>
     *   <li>고객 존재 확인 (내부 DB 조회)</li>
     *   <li>Order Aggregate 생성</li>
     *   <li>Order 저장</li>
     * </ol>
     *
     * <p><strong>외부 API 호출 없음</strong> - 트랜잭션 경계 규칙 준수
     *
     * @param command 주문 생성 Command
     * @return PlaceOrderResponse 주문 생성 결과
     * @throws IllegalArgumentException 고객이 존재하지 않는 경우
     */
    @Transactional
    public PlaceOrderResponse execute(PlaceOrderCommand command) {
        // 1. 고객 존재 확인 (내부 DB 조회 - 트랜잭션 내 허용)
        CustomerId customerId = new CustomerId(command.getCustomerId());
        validateCustomerExists(customerId);

        // 2. Order Aggregate 생성 (Domain 로직)
        OrderId orderId = OrderId.generate();
        List<OrderItem> orderItems = command.toOrderItems();
        Order order = Order.placeOrder(orderId, customerId, orderItems);

        // 3. Order 저장 (DB 트랜잭션)
        Order savedOrder = saveOrderPort.save(order);

        // 4. Response 생성 (Assembler 패턴)
        return PlaceOrderResponse.from(savedOrder);
    }

    /**
     * 고객 존재 확인
     *
     * <p>내부 DB 조회이므로 트랜잭션 내 실행 가능
     *
     * @param customerId 고객 ID
     * @throws IllegalArgumentException 고객이 존재하지 않는 경우
     */
    private void validateCustomerExists(CustomerId customerId) {
        if (!loadCustomerPort.exists(customerId)) {
            throw new IllegalArgumentException("Customer not found: " + customerId.getValue());
        }
    }
}
