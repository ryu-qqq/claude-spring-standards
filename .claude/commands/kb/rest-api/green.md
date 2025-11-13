# REST API Layer TDD Green - Implement Minimum Code

You are in the GREEN phase of Kent Beck's TDD cycle for **REST API Layer**.

## Instructions

1. **Test is already FAILING** (RED phase complete)
2. **Write the SIMPLEST code** to make the test pass
3. **No premature optimization** - just make it work
4. **Run the test** and verify it PASSES
5. **Report success** clearly

## REST API Layer Implementation Guidelines

### Core Principles
- **Minimum Code**: Write only what's needed to pass the test
- **RESTful 설계**: HTTP 메서드 및 상태 코드 올바르게 사용
- **DTO 패턴**: Request/Response DTO 분리 (Domain 직접 노출 금지)
- **Validation**: `@Valid` + Bean Validation 어노테이션 사용
- **Javadoc 필수**: `@author`, `@since` 포함

### Implementation Pattern

**Step 1: Request DTO (Record 패턴)**
```java
package com.company.template.restapi.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 주문 생성 요청 DTO.
 *
 * @param customerId 고객 ID
 * @param productId 상품 ID
 * @param quantity 수량
 * @author Claude Code
 * @since 2025-01-13
 */
public record PlaceOrderRequest(
    @NotNull(message = "고객 ID는 필수입니다")
    @Positive(message = "고객 ID는 양수여야 합니다")
    Long customerId,

    @NotNull(message = "상품 ID는 필수입니다")
    @Positive(message = "상품 ID는 양수여야 합니다")
    Long productId,

    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 양수여야 합니다")
    Integer quantity
) {}
```

**Step 2: Response DTO (Record 패턴)**
```java
package com.company.template.restapi.dto.response;

import com.company.template.domain.OrderStatus;

/**
 * 주문 응답 DTO.
 *
 * @param orderId 주문 ID
 * @param customerId 고객 ID
 * @param productId 상품 ID
 * @param quantity 수량
 * @param status 주문 상태
 * @author Claude Code
 * @since 2025-01-13
 */
public record OrderResponse(
    String orderId,
    Long customerId,
    Long productId,
    Integer quantity,
    OrderStatus status
) {}
```

**Step 3: Request Mapper**
```java
package com.company.template.restapi.mapper;

import com.company.template.restapi.dto.request.PlaceOrderRequest;
import com.company.template.application.dto.command.PlaceOrderCommand;

/**
 * Request DTO to Command Mapper.
 *
 * <p>REST API Layer의 Request DTO를 Application Layer의 Command로 변환합니다.</p>
 *
 * @author Claude Code
 * @since 2025-01-13
 */
public class OrderRequestMapper {

    /**
     * PlaceOrderRequest → PlaceOrderCommand 변환.
     */
    public static PlaceOrderCommand toCommand(PlaceOrderRequest request) {
        return new PlaceOrderCommand(
            request.customerId(),
            request.productId(),
            request.quantity()
        );
    }

    /**
     * CancelOrderRequest → CancelOrderCommand 변환.
     */
    public static CancelOrderCommand toCancelCommand(String orderId, CancelOrderRequest request) {
        return new CancelOrderCommand(
            orderId,
            request.cancelReason()
        );
    }

    private OrderRequestMapper() {
        throw new AssertionError("Mapper 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

**Step 4: Response Mapper**
```java
package com.company.template.restapi.mapper;

import com.company.template.restapi.dto.response.OrderResponse;
import com.company.template.application.dto.response.OrderApplicationResponse;

/**
 * Application Response to REST Response Mapper.
 *
 * <p>Application Layer의 Response를 REST API Response DTO로 변환합니다.</p>
 *
 * @author Claude Code
 * @since 2025-01-13
 */
public class OrderResponseMapper {

    /**
     * OrderApplicationResponse → OrderResponse 변환.
     */
    public static OrderResponse toResponse(OrderApplicationResponse appResponse) {
        return new OrderResponse(
            appResponse.orderId(),
            appResponse.customerId(),
            appResponse.productId(),
            appResponse.quantity(),
            appResponse.status()
        );
    }

    private OrderResponseMapper() {
        throw new AssertionError("Mapper 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

**Step 5: Controller (RESTful 설계)**
```java
package com.company.template.restapi.controller;

import com.company.template.restapi.dto.request.PlaceOrderRequest;
import com.company.template.restapi.dto.request.CancelOrderRequest;
import com.company.template.restapi.dto.response.OrderResponse;
import com.company.template.restapi.mapper.OrderRequestMapper;
import com.company.template.restapi.mapper.OrderResponseMapper;
import com.company.template.application.port.in.PlaceOrderPort;
import com.company.template.application.port.in.LoadOrderPort;
import com.company.template.application.port.in.CancelOrderPort;
import com.company.template.application.dto.command.PlaceOrderCommand;
import com.company.template.application.dto.command.CancelOrderCommand;
import com.company.template.application.dto.response.OrderApplicationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * 주문 REST API Controller.
 *
 * <p>주문 생성, 조회, 취소 API를 제공합니다.</p>
 *
 * @author Claude Code
 * @since 2025-01-13
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PlaceOrderPort placeOrderUseCase;
    private final LoadOrderPort loadOrderUseCase;
    private final CancelOrderPort cancelOrderUseCase;

    /**
     * 주문 생성.
     *
     * @param request 주문 생성 요청 DTO
     * @return 생성된 주문 응답 DTO (HTTP 201 Created)
     * @author Claude Code
     * @since 2025-01-13
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) {
        // 1. Request DTO → Command 변환
        PlaceOrderCommand command = OrderRequestMapper.toCommand(request);

        // 2. UseCase 호출
        OrderApplicationResponse appResponse = placeOrderUseCase.execute(command);

        // 3. Application Response → REST Response 변환
        OrderResponse response = OrderResponseMapper.toResponse(appResponse);

        // 4. HTTP 201 Created 반환 (Location 헤더 포함)
        URI location = URI.create("/api/orders/" + response.orderId());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * 주문 조회.
     *
     * @param orderId 주문 ID
     * @return 주문 응답 DTO (HTTP 200 OK)
     * @author Claude Code
     * @since 2025-01-13
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        // 1. UseCase 호출
        OrderApplicationResponse appResponse = loadOrderUseCase.loadById(orderId);

        // 2. Application Response → REST Response 변환
        OrderResponse response = OrderResponseMapper.toResponse(appResponse);

        // 3. HTTP 200 OK 반환
        return ResponseEntity.ok(response);
    }

    /**
     * 주문 취소.
     *
     * @param orderId 주문 ID
     * @param request 취소 요청 DTO
     * @return No Content (HTTP 204)
     * @author Claude Code
     * @since 2025-01-13
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
        @PathVariable String orderId,
        @Valid @RequestBody CancelOrderRequest request
    ) {
        // 1. Request DTO → Command 변환
        CancelOrderCommand command = OrderRequestMapper.toCancelCommand(orderId, request);

        // 2. UseCase 호출
        cancelOrderUseCase.execute(command);

        // 3. HTTP 204 No Content 반환
        return ResponseEntity.noContent().build();
    }

    /**
     * 주문 목록 조회 (페이징).
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 주문 목록 응답 DTO (HTTP 200 OK)
     * @author Claude Code
     * @since 2025-01-13
     */
    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        // 1. UseCase 호출
        PageApplicationResponse<OrderApplicationResponse> appPage =
            loadOrderUseCase.findAll(page, size);

        // 2. Application Response → REST Response 변환
        PageResponse<OrderResponse> response = PageResponse.of(
            appPage.content().stream()
                .map(OrderResponseMapper::toResponse)
                .toList(),
            appPage.totalElements(),
            appPage.totalPages(),
            appPage.currentPage()
        );

        // 3. HTTP 200 OK 반환
        return ResponseEntity.ok(response);
    }
}
```

**Step 6: Error Response DTO**
```java
package com.company.template.restapi.dto.response;

import java.time.LocalDateTime;

/**
 * 에러 응답 DTO.
 *
 * @param errorCode 에러 코드
 * @param message 에러 메시지
 * @param timestamp 에러 발생 시간
 * @author Claude Code
 * @since 2025-01-13
 */
public record ErrorResponse(
    String errorCode,
    String message,
    LocalDateTime timestamp
) {
    /**
     * ErrorResponse 생성.
     */
    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, LocalDateTime.now());
    }

    /**
     * Validation 실패 ErrorResponse 생성.
     */
    public static ErrorResponse ofValidationFailed(String message) {
        return ErrorResponse.of("VALIDATION_FAILED", message);
    }
}
```

**Step 7: Global Exception Handler**
```java
package com.company.template.restapi.exception;

import com.company.template.restapi.dto.response.ErrorResponse;
import com.company.template.domain.exception.DomainException;
import com.company.template.application.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리 Handler.
 *
 * <p>모든 예외를 처리하고 일관된 ErrorResponse를 반환합니다.</p>
 *
 * @author Claude Code
 * @since 2025-01-13
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * 도메인 예외 처리 (비즈니스 로직 예외).
     *
     * @param ex 도메인 예외
     * @return 400 Bad Request
     * @author Claude Code
     * @since 2025-01-13
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        log.warn("Domain Exception: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
            ex.getErrorCode(),
            ex.getMessage()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 리소스 없음 예외 처리.
     *
     * @param ex 리소스 없음 예외
     * @return 404 Not Found
     * @author Claude Code
     * @since 2025-01-13
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource Not Found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
            "RESOURCE_NOT_FOUND",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Validation 예외 처리.
     *
     * @param ex Validation 예외
     * @return 400 Bad Request
     * @author Claude Code
     * @since 2025-01-13
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));

        log.warn("Validation Failed: {}", message);

        ErrorResponse errorResponse = ErrorResponse.ofValidationFailed(message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 기타 예외 처리.
     *
     * @param ex 기타 예외
     * @return 500 Internal Server Error
     * @author Claude Code
     * @since 2025-01-13
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected Exception: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.of(
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
```

**Step 8: Page Response DTO (페이징 결과)**
```java
package com.company.template.restapi.dto.response;

import java.util.List;

/**
 * 페이징 응답 DTO.
 *
 * @param content 컨텐츠 목록
 * @param totalElements 전체 요소 수
 * @param totalPages 전체 페이지 수
 * @param currentPage 현재 페이지 번호
 * @param <T> 컨텐츠 타입
 * @author Claude Code
 * @since 2025-01-13
 */
public record PageResponse<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int currentPage
) {
    /**
     * PageResponse 생성.
     */
    public static <T> PageResponse<T> of(
        List<T> content,
        long totalElements,
        int totalPages,
        int currentPage
    ) {
        return new PageResponse<>(content, totalElements, totalPages, currentPage);
    }
}
```

## GREEN Phase Workflow

**Step 1: Focus on the Failing Test**
```java
// Test from RED phase
@Test
@DisplayName("POST /api/orders - 주문 생성 성공")
void shouldCreateOrder() throws Exception {
    // Given
    PlaceOrderRequest request = PlaceOrderRequestFixture.create();
    OrderResponse response = OrderResponseFixture.create();

    given(placeOrderUseCase.execute(any()))
        .willReturn(response);

    // When & Then
    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
}
```

**Step 2: Write Minimum Code (Controller + DTOs + Mappers)**
```java
// OrderController.java (최소 구현)
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PlaceOrderPort placeOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) {
        PlaceOrderCommand command = OrderRequestMapper.toCommand(request);
        OrderApplicationResponse appResponse = placeOrderUseCase.execute(command);
        OrderResponse response = OrderResponseMapper.toResponse(appResponse);

        URI location = URI.create("/api/orders/" + response.orderId());
        return ResponseEntity.created(location).body(response);
    }
}
```

**Step 3: Run the Test**
```bash
./gradlew test --tests "*OrderControllerTest.shouldCreateOrder"
```

**Step 4: Verify GREEN**
```
✅ Test PASSED
```

## REST API Layer Implementation Patterns

### 1. RESTful HTTP Method Mapping
```java
// ✅ CORRECT (RESTful 설계)
@PostMapping                    // Create (201 Created)
@GetMapping("/{id}")           // Read (200 OK)
@PutMapping("/{id}")           // Update (200 OK)
@DeleteMapping("/{id}")        // Delete (204 No Content)
@GetMapping                    // List (200 OK)

// ❌ WRONG (비RESTful)
@PostMapping("/createOrder")   // ❌ 동사 사용
@GetMapping("/getOrder")       // ❌ 동사 사용
```

### 2. HTTP Status Code Usage
```java
// ✅ CORRECT (적절한 상태 코드)
return ResponseEntity.created(location).body(response);        // 201 Created
return ResponseEntity.ok(response);                            // 200 OK
return ResponseEntity.noContent().build();                     // 204 No Content
return ResponseEntity.badRequest().body(errorResponse);        // 400 Bad Request
return ResponseEntity.status(HttpStatus.NOT_FOUND).body(...);  // 404 Not Found
```

### 3. Validation Annotation Usage
```java
// ✅ CORRECT (Bean Validation)
public record PlaceOrderRequest(
    @NotNull(message = "고객 ID는 필수입니다")
    @Positive(message = "고객 ID는 양수여야 합니다")
    Long customerId,

    @NotBlank(message = "주문자명은 필수입니다")
    @Size(min = 2, max = 50, message = "주문자명은 2-50자여야 합니다")
    String customerName,

    @Email(message = "유효한 이메일 형식이어야 합니다")
    String email
) {}
```

### 4. DTO Layering (Domain 노출 금지)
```java
// ✅ CORRECT (DTO Layer 분리)
Controller → REST Response DTO → Application Response → Domain

// ❌ WRONG (Domain 직접 노출)
@GetMapping("/{id}")
public ResponseEntity<OrderDomain> getOrder(@PathVariable String id) {  // ❌
    return ResponseEntity.ok(orderService.findById(id));
}
```

## Common Mistakes to Avoid

### ❌ WRONG: Domain 직접 반환
```java
// ❌ Domain 직접 노출
@GetMapping("/{id}")
public OrderDomain getOrder(@PathVariable String id) {
    return orderService.findById(id);
}
```

### ❌ WRONG: Validation 생략
```java
// ❌ @Valid 생략
@PostMapping
public ResponseEntity<OrderResponse> createOrder(@RequestBody PlaceOrderRequest request) {
    // ...
}
```

### ❌ WRONG: 잘못된 HTTP 상태 코드
```java
// ❌ 생성 성공인데 200 OK
@PostMapping
public ResponseEntity<OrderResponse> createOrder(...) {
    return ResponseEntity.ok(response);  // ❌ 201 Created 사용해야 함
}
```

## Success Criteria

- ✅ Test runs and PASSES
- ✅ Minimum code written (no extra features)
- ✅ RESTful 설계 준수 (HTTP 메서드, 상태 코드)
- ✅ DTO 패턴 준수 (Domain 직접 노출 금지)
- ✅ Validation 적용 (`@Valid` + Bean Validation)
- ✅ Javadoc 작성 (`@author`, `@since`)
- ✅ Global Exception Handler 구현

## What NOT to Do

- ❌ Don't write more code than needed to pass the test
- ❌ Don't add "nice to have" features
- ❌ Don't refactor yet (that's the next phase!)
- ❌ Don't expose Domain/Entity directly
- ❌ Don't skip Validation
- ❌ Don't use wrong HTTP status codes

This is Kent Beck's TDD: Write the SIMPLEST code to pass the test, then REFACTOR.
