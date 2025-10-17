package com.company.template.order.adapter.in.web;

import com.company.template.order.application.port.in.PlaceOrderCommand;
import com.company.template.order.application.port.in.PlaceOrderResponse;
import com.company.template.order.application.service.PlaceOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Order REST API Controller
 *
 * <p>주문 관련 REST API를 제공합니다.
 * Controller는 비즈니스 로직을 포함하지 않고, UseCase로 위임합니다.
 *
 * <h3>책임</h3>
 * <ul>
 *   <li>HTTP 요청 수신 및 검증</li>
 *   <li>API DTO → Command 변환</li>
 *   <li>UseCase 실행</li>
 *   <li>Response → API DTO 변환</li>
 * </ul>
 *
 * @author Claude
 * @since 2025-10-17
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final OrderMapper orderMapper;

    /**
     * Constructor Injection
     *
     * @param placeOrderUseCase 주문 생성 UseCase
     * @param orderMapper API DTO ↔ Command/Response 변환 Mapper
     */
    public OrderController(
            PlaceOrderUseCase placeOrderUseCase,
            OrderMapper orderMapper) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.orderMapper = orderMapper;
    }

    /**
     * 주문 생성 API
     *
     * <p>POST /api/v1/orders
     *
     * <h3>요청 검증</h3>
     * <ul>
     *   <li>@Valid: Bean Validation 자동 실행</li>
     *   <li>customerId: 필수 (@NotNull)</li>
     *   <li>orderItems: 최소 1개 이상 (@NotEmpty)</li>
     * </ul>
     *
     * @param request 주문 생성 요청 DTO
     * @return ResponseEntity<PlaceOrderApiResponse> 201 Created + 주문 생성 결과
     */
    @PostMapping
    public ResponseEntity<PlaceOrderApiResponse> placeOrder(
            @Valid @RequestBody PlaceOrderApiRequest request) {

        // 1. API Request → Command 변환 (Mapper 사용)
        PlaceOrderCommand command = orderMapper.toCommand(request);

        // 2. UseCase 실행 (비즈니스 로직은 UseCase에 위임)
        PlaceOrderResponse response = placeOrderUseCase.execute(command);

        // 3. Response → API Response 변환
        PlaceOrderApiResponse apiResponse = orderMapper.toApiResponse(response);

        // 4. HTTP 201 Created 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }
}
