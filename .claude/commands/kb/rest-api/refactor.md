# REST API Layer TDD Refactor - Improve Structure

You are in the REFACTOR phase of Kent Beck's TDD cycle for **REST API Layer**.

## Instructions

1. **Test is already PASSING** (GREEN phase complete)
2. **Improve code structure** without changing behavior
3. **Apply design patterns** and best practices
4. **Ensure Zero-Tolerance compliance** (RESTful 설계, DTO 패턴, Validation, Error Handling)
5. **Run all tests** after each refactoring step
6. **Commit incremental changes** for safety

## Refactoring Goals

- **Clarity**: Make code easier to understand
- **Maintainability**: Make code easier to change
- **RESTful Design**: Strengthen HTTP semantics and resource modeling
- **Error Handling**: Consolidate exception handling patterns
- **Response Mapping**: Streamline DTO conversion logic

## REST API Layer Refactoring Patterns

### 1. Extract Common Validation Logic

**Before**:
```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) {
        if (request.quantity() > 100) {
            throw new InvalidQuantityException("Quantity exceeds maximum: 100");
        }
        // ...
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(
        @PathVariable String orderId,
        @Valid @RequestBody UpdateOrderRequest request
    ) {
        if (request.quantity() > 100) {
            throw new InvalidQuantityException("Quantity exceeds maximum: 100");
        }
        // ...
    }
}
```

**After**:
```java
// Extract to Validator
@Component
public class OrderRequestValidator {

    private static final int MAX_QUANTITY = 100;

    public void validateQuantity(Integer quantity) {
        if (quantity > MAX_QUANTITY) {
            throw new InvalidQuantityException(
                "Quantity exceeds maximum: " + MAX_QUANTITY
            );
        }
    }

    public void validateOrderRequest(PlaceOrderRequest request) {
        validateQuantity(request.quantity());
        // Other validations
    }
}

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRequestValidator validator;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) {
        validator.validateOrderRequest(request);
        // ...
    }
}
```

### 2. Extract Response Mapping Utilities

**Before**:
```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) {
        // ...
        OrderResponse response = placeOrderUseCase.execute(command);

        // Repeated URI creation logic
        URI location = URI.create("/api/orders/" + response.orderId());
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<OrderResponse>> createOrders(@Valid @RequestBody List<PlaceOrderRequest> requests) {
        // ...
        List<OrderResponse> responses = batchPlaceOrderUseCase.execute(commands);

        // Similar URI creation logic
        URI location = URI.create("/api/orders/batch");
        return ResponseEntity.created(location).body(responses);
    }
}
```

**After**:
```java
// Extract ResponseBuilder
public class OrderResponseBuilder {

    private static final String ORDERS_BASE_PATH = "/api/orders";

    public static ResponseEntity<OrderResponse> created(OrderResponse response) {
        URI location = URI.create(ORDERS_BASE_PATH + "/" + response.orderId());
        return ResponseEntity.created(location).body(response);
    }

    public static ResponseEntity<List<OrderResponse>> createdBatch(List<OrderResponse> responses) {
        URI location = URI.create(ORDERS_BASE_PATH + "/batch");
        return ResponseEntity.created(location).body(responses);
    }

    public static ResponseEntity<OrderResponse> ok(OrderResponse response) {
        return ResponseEntity.ok(response);
    }

    private OrderResponseBuilder() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) {
        // ...
        OrderResponse response = placeOrderUseCase.execute(command);
        return OrderResponseBuilder.created(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<OrderResponse>> createOrders(@Valid @RequestBody List<PlaceOrderRequest> requests) {
        // ...
        List<OrderResponse> responses = batchPlaceOrderUseCase.execute(commands);
        return OrderResponseBuilder.createdBatch(responses);
    }
}
```

### 3. Consolidate Error Response Handling

**Before**:
```java
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        ErrorResponse errorResponse = ErrorResponse.of(
            ex.getErrorCode(),
            ex.getMessage()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.of(
            "ORDER_NOT_FOUND",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.of(
            "CUSTOMER_NOT_FOUND",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
```

**After**:
```java
// Extract ErrorResponseBuilder
public class ErrorResponseBuilder {

    public static ResponseEntity<ErrorResponse> badRequest(String errorCode, String message) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    public static ResponseEntity<ErrorResponse> notFound(String errorCode, String message) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    public static ResponseEntity<ErrorResponse> internalServerError(String message) {
        ErrorResponse errorResponse = ErrorResponse.of("INTERNAL_SERVER_ERROR", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private ErrorResponseBuilder() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        return ErrorResponseBuilder.badRequest(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler({OrderNotFoundException.class, CustomerNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(RuntimeException ex) {
        String errorCode = ex instanceof OrderNotFoundException
            ? "ORDER_NOT_FOUND"
            : "CUSTOMER_NOT_FOUND";
        return ErrorResponseBuilder.notFound(errorCode, ex.getMessage());
    }
}
```

### 4. Extract Pagination Response Builder

**Before**:
```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @GetMapping
    public ResponseEntity<PageApiResponse<OrderResponse>> getOrders(
        @RequestParam(required = false) Long customerId,
        Pageable pageable
    ) {
        Page<OrderResponse> page = findOrdersUseCase.execute(customerId, pageable);

        // Repeated Page → PageApiResponse conversion
        PageApiResponse<OrderResponse> response = new PageApiResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<PageApiResponse<OrderResponse>> getOrderHistory(
        @RequestParam Long customerId,
        Pageable pageable
    ) {
        Page<OrderResponse> page = findOrderHistoryUseCase.execute(customerId, pageable);

        // Same conversion logic
        PageApiResponse<OrderResponse> response = new PageApiResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}
```

**After**:
```java
// Extract PageResponseBuilder
public class PageResponseBuilder {

    public static <T> PageApiResponse<T> of(Page<T> page) {
        return new PageApiResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    public static <T> ResponseEntity<PageApiResponse<T>> ok(Page<T> page) {
        return ResponseEntity.ok(of(page));
    }

    private PageResponseBuilder() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @GetMapping
    public ResponseEntity<PageApiResponse<OrderResponse>> getOrders(
        @RequestParam(required = false) Long customerId,
        Pageable pageable
    ) {
        Page<OrderResponse> page = findOrdersUseCase.execute(customerId, pageable);
        return PageResponseBuilder.ok(page);
    }

    @GetMapping("/history")
    public ResponseEntity<PageApiResponse<OrderResponse>> getOrderHistory(
        @RequestParam Long customerId,
        Pageable pageable
    ) {
        Page<OrderResponse> page = findOrderHistoryUseCase.execute(customerId, pageable);
        return PageResponseBuilder.ok(page);
    }
}
```

### 5. Group Related Endpoints with @Nested (Test)

**Before**:
```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Test
    @DisplayName("POST /api/orders - 주문 생성 성공")
    void shouldCreateOrder() { /* ... */ }

    @Test
    @DisplayName("POST /api/orders - customerId가 null이면 400")
    void shouldReturn400WhenCustomerIdIsNull() { /* ... */ }

    @Test
    @DisplayName("GET /api/orders/{orderId} - 주문 조회 성공")
    void shouldGetOrder() { /* ... */ }

    @Test
    @DisplayName("GET /api/orders/{orderId} - 존재하지 않으면 404")
    void shouldReturn404WhenOrderNotFound() { /* ... */ }

    @Test
    @DisplayName("DELETE /api/orders/{orderId} - 주문 취소 성공")
    void shouldCancelOrder() { /* ... */ }
}
```

**After**:
```java
@DisplayName("OrderController 테스트")
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Nested
    @DisplayName("주문 생성 (POST /api/orders)")
    class CreateOrderTests {

        @Test
        @DisplayName("정상 케이스 - 주문 생성 성공")
        void shouldCreateOrder() {
            // Given
            PlaceOrderRequest request = PlaceOrderRequestFixture.create();
            OrderResponse response = OrderResponseFixture.create();

            given(placeOrderUseCase.execute(any())).willReturn(response);

            // When & Then
            mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.orderId").value(response.orderId()));
        }

        @Test
        @DisplayName("검증 실패 - customerId가 null이면 400 Bad Request")
        void shouldReturn400WhenCustomerIdIsNull() { /* ... */ }

        @Test
        @DisplayName("검증 실패 - quantity가 음수면 400 Bad Request")
        void shouldReturn400WhenQuantityIsNegative() { /* ... */ }
    }

    @Nested
    @DisplayName("주문 조회 (GET /api/orders/{orderId})")
    class GetOrderTests {

        @Test
        @DisplayName("정상 케이스 - 주문 조회 성공")
        void shouldGetOrder() { /* ... */ }

        @Test
        @DisplayName("실패 케이스 - 존재하지 않으면 404 Not Found")
        void shouldReturn404WhenOrderNotFound() { /* ... */ }
    }

    @Nested
    @DisplayName("주문 취소 (DELETE /api/orders/{orderId})")
    class CancelOrderTests {

        @Test
        @DisplayName("정상 케이스 - 주문 취소 성공")
        void shouldCancelOrder() { /* ... */ }

        @Test
        @DisplayName("실패 케이스 - SHIPPED 상태면 취소 불가")
        void shouldReturn400WhenOrderIsShipped() { /* ... */ }
    }
}
```

### 6. Extract Common HTTP Headers

**Before**:
```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) {
        // ...
        return ResponseEntity.created(location)
            .header("X-Request-Id", UUID.randomUUID().toString())
            .body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        // ...
        return ResponseEntity.ok()
            .header("X-Request-Id", UUID.randomUUID().toString())
            .body(response);
    }
}
```

**After**:
```java
// Extract to Interceptor
@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString();
        response.setHeader(REQUEST_ID_HEADER, requestId);
        return true;
    }
}

// Register Interceptor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestIdInterceptor requestIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor)
            .addPathPatterns("/api/**");
    }
}

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) {
        // ...
        return ResponseEntity.created(location).body(response);
        // X-Request-Id header automatically added by interceptor
    }
}
```

## Refactoring Workflow

### Step 1: Identify Code Smells
- Duplicated validation logic across controllers
- Repeated response mapping patterns
- Inconsistent error handling
- Long controller methods (>30 lines)
- Missing common HTTP semantics (headers, status codes)

### Step 2: Apply Refactoring
```bash
# 1. Run tests to ensure GREEN
./gradlew test

# 2. Apply ONE refactoring
# (e.g., Extract validation logic)

# 3. Run tests again
./gradlew test

# 4. If GREEN, commit
git add .
git commit -m "refactor: extract validation logic in OrderController"

# 5. Repeat for next refactoring
```

### Step 3: Verify Zero-Tolerance Compliance

**Check 1: RESTful Design**
```bash
# Find non-RESTful endpoint patterns
grep -r "@PostMapping\|@GetMapping\|@PutMapping\|@DeleteMapping" adapter-in/rest-api/src/main/java/ | grep -E "create|get|update|delete"
# Should use proper HTTP methods and resource names
```

**Check 2: DTO Pattern**
```bash
# Find Domain/Entity direct exposure
grep -r "Domain\|Entity" adapter-in/rest-api/src/main/java/.../controller/
# Should use Response DTO only
```

**Check 3: Validation**
```bash
# Find @RequestBody without @Valid
grep -r "@RequestBody" adapter-in/rest-api/src/main/java/.../controller/ | grep -v "@Valid"
# Should have @Valid on all @RequestBody
```

## Common REST API Refactorings

### 1. Extract API Versioning Strategy
```java
// Before: Hardcoded version in paths
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController { }

// After: Centralized versioning
@RestController
@RequestMapping("${api.version.prefix}/orders")
public class OrderController { }

// application.yml
api:
  version:
    prefix: /api/v1
```

### 2. Add API Documentation (Javadoc)
```java
// Before: Missing documentation
@PostMapping
public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) { }

// After: Complete Javadoc
/**
 * 주문 생성 API.
 *
 * @param request 주문 생성 요청 DTO
 * @return 201 Created - 생성된 주문 응답 DTO
 * @throws InvalidQuantityException 수량이 유효하지 않은 경우
 * @throws CustomerNotFoundException 고객을 찾을 수 없는 경우
 * @author Claude Code
 * @since 2025-01-13
 */
@PostMapping
public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody PlaceOrderRequest request) { }
```

### 3. Extract HATEOAS Link Builder
```java
// Before: Manual link creation
@GetMapping("/{orderId}")
public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
    OrderResponse response = loadOrderUseCase.loadById(orderId);
    // No links to related resources
    return ResponseEntity.ok(response);
}

// After: HATEOAS with links
@GetMapping("/{orderId}")
public ResponseEntity<OrderResponseWithLinks> getOrder(@PathVariable String orderId) {
    OrderResponse response = loadOrderUseCase.loadById(orderId);

    // Add HATEOAS links
    OrderResponseWithLinks responseWithLinks = new OrderResponseWithLinks(response);
    responseWithLinks.add(linkTo(methodOn(OrderController.class).getOrder(orderId)).withSelfRel());
    responseWithLinks.add(linkTo(methodOn(OrderController.class).cancelOrder(orderId, null)).withRel("cancel"));

    return ResponseEntity.ok(responseWithLinks);
}
```

## Success Criteria

- ✅ All tests still PASS after refactoring
- ✅ Code is more readable and maintainable
- ✅ Duplicated logic extracted and reused
- ✅ RESTful design principles maintained
- ✅ Error handling consolidated
- ✅ Response mapping streamlined
- ✅ Zero-Tolerance rules followed (DTO 패턴, Validation, Javadoc)

## What NOT to Do

- ❌ Don't change behavior (tests should still pass)
- ❌ Don't refactor without tests passing first
- ❌ Don't expose Domain/Entity directly
- ❌ Don't skip @Valid on @RequestBody
- ❌ Don't mix HTTP methods incorrectly
- ❌ Don't over-engineer (YAGNI)

This is Kent Beck's TDD: After tests pass, REFACTOR to improve structure while keeping tests GREEN.
