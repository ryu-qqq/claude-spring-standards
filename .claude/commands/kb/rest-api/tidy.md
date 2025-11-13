# REST API Layer TDD Tidy - Clean Up Tests

You are in the TIDY phase of Kent Beck's TDD cycle for **REST API Layer**.

## Instructions

1. **Code and tests are PASSING** (REFACTOR phase complete)
2. **Clean up test code** for maintainability
3. **Ensure all tests use TestFixture pattern** (mandatory!)
4. **Remove duplication** in test setup
5. **Improve test readability** and documentation
6. **Run all tests** to verify cleanup didn't break anything

## Tidy Goals

- **TestFixture Usage**: Every test uses Fixture.create() methods
- **Test Isolation**: Each test is independent and repeatable
- **Test Clarity**: Test names and structure clearly communicate intent
- **DRY Tests**: Eliminate duplicated setup code
- **Fast Tests**: Remove unnecessary MockMvc setup overhead

## TestFixture Pattern Enforcement

### ❌ BEFORE: Inline Object Creation
```java
@Test
@DisplayName("POST /api/orders - 주문 생성 성공")
void shouldCreateOrder() throws Exception {
    // ❌ BAD: Inline Request DTO creation
    PlaceOrderRequest request = new PlaceOrderRequest(1L, 100L, 10);

    // ❌ BAD: Inline Response DTO creation
    OrderResponse response = new OrderResponse(
        "order-123",
        1L,
        100L,
        10,
        OrderStatus.PLACED
    );

    given(placeOrderUseCase.execute(any())).willReturn(response);

    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
}
```

### ✅ AFTER: TestFixture Pattern
```java
@Test
@DisplayName("POST /api/orders - 주문 생성 성공")
void shouldCreateOrder() throws Exception {
    // ✅ GOOD: Use Fixtures
    PlaceOrderRequest request = PlaceOrderRequestFixture.create();
    OrderResponse response = OrderResponseFixture.create();

    given(placeOrderUseCase.execute(any())).willReturn(response);

    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderId").value(response.orderId()));
}
```

## TestFixture Consolidation Patterns

### Pattern 1: Request DTO Fixture with Variants
```java
// PlaceOrderRequestFixture.java
public class PlaceOrderRequestFixture {

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long DEFAULT_PRODUCT_ID = 100L;
    private static final Integer DEFAULT_QUANTITY = 10;

    /**
     * 기본 PlaceOrderRequest 생성.
     */
    public static PlaceOrderRequest create() {
        return new PlaceOrderRequest(
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY
        );
    }

    /**
     * 특정 고객 ID로 생성.
     */
    public static PlaceOrderRequest createWithCustomerId(Long customerId) {
        return new PlaceOrderRequest(
            customerId,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY
        );
    }

    /**
     * 특정 수량으로 생성.
     */
    public static PlaceOrderRequest createWithQuantity(int quantity) {
        return new PlaceOrderRequest(
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            quantity
        );
    }

    /**
     * Validation 실패 케이스 - null customerId.
     */
    public static PlaceOrderRequest createWithNullCustomerId() {
        return new PlaceOrderRequest(null, DEFAULT_PRODUCT_ID, DEFAULT_QUANTITY);
    }

    /**
     * Validation 실패 케이스 - 음수 quantity.
     */
    public static PlaceOrderRequest createWithNegativeQuantity() {
        return new PlaceOrderRequest(DEFAULT_CUSTOMER_ID, DEFAULT_PRODUCT_ID, -1);
    }

    /**
     * Validation 실패 케이스 - 초과 quantity.
     */
    public static PlaceOrderRequest createWithExcessiveQuantity() {
        return new PlaceOrderRequest(DEFAULT_CUSTOMER_ID, DEFAULT_PRODUCT_ID, 101);
    }

    private PlaceOrderRequestFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Pattern 2: Extract MockMvc Test Utilities
```java
// MockMvcTestUtils.java
public class MockMvcTestUtils {

    /**
     * POST 요청 수행 (JSON).
     */
    public static ResultActions performPost(
        MockMvc mockMvc,
        String url,
        Object requestBody,
        ObjectMapper objectMapper
    ) throws Exception {
        return mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)));
    }

    /**
     * GET 요청 수행.
     */
    public static ResultActions performGet(MockMvc mockMvc, String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    /**
     * DELETE 요청 수행 (JSON Body).
     */
    public static ResultActions performDelete(
        MockMvc mockMvc,
        String url,
        Object requestBody,
        ObjectMapper objectMapper
    ) throws Exception {
        return mockMvc.perform(delete(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)));
    }

    /**
     * 201 Created 검증 + Location 헤더.
     */
    public static ResultActions assertCreated(ResultActions result, String expectedLocation) throws Exception {
        return result
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", expectedLocation));
    }

    /**
     * 400 Bad Request 검증 + errorCode.
     */
    public static ResultActions assertBadRequest(ResultActions result, String expectedErrorCode) throws Exception {
        return result
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value(expectedErrorCode));
    }

    /**
     * 404 Not Found 검증 + errorCode.
     */
    public static ResultActions assertNotFound(ResultActions result, String expectedErrorCode) throws Exception {
        return result
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value(expectedErrorCode));
    }

    private MockMvcTestUtils() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Pattern 3: Parameterized Tests with Fixture
```java
// Before: Multiple similar tests
@Test
void shouldReturn400WhenCustomerIdIsNull() { /* ... */ }

@Test
void shouldReturn400WhenProductIdIsNull() { /* ... */ }

@Test
void shouldReturn400WhenQuantityIsNull() { /* ... */ }

// After: Parameterized test with Fixture
@ParameterizedTest
@MethodSource("provideInvalidRequests")
@DisplayName("POST /api/orders - Validation 실패 케이스")
void shouldReturn400WhenRequestIsInvalid(
    PlaceOrderRequest invalidRequest,
    String expectedMessage
) throws Exception {
    // Given
    // (Request already provided by @MethodSource)

    // When & Then
    MockMvcTestUtils.performPost(mockMvc, "/api/orders", invalidRequest, objectMapper)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMessage));
}

private static Stream<Arguments> provideInvalidRequests() {
    return Stream.of(
        Arguments.of(
            PlaceOrderRequestFixture.createWithNullCustomerId(),
            "고객 ID는 필수입니다"
        ),
        Arguments.of(
            PlaceOrderRequestFixture.createWithNegativeQuantity(),
            "수량은 양수여야 합니다"
        ),
        Arguments.of(
            PlaceOrderRequestFixture.createWithExcessiveQuantity(),
            "수량은 100 이하여야 합니다"
        )
    );
}
```

## Test Cleanup Patterns

### 1. Remove @BeforeEach Setup (Use Fixture Instead)
```java
// ❌ BEFORE: Setup in @BeforeEach
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaceOrderPort placeOrderUseCase;

    private PlaceOrderRequest defaultRequest;
    private OrderResponse defaultResponse;

    @BeforeEach
    void setUp() {
        defaultRequest = new PlaceOrderRequest(1L, 100L, 10);
        defaultResponse = new OrderResponse("order-123", 1L, 100L, 10, OrderStatus.PLACED);
    }

    @Test
    void shouldCreateOrder() throws Exception {
        given(placeOrderUseCase.execute(any())).willReturn(defaultResponse);
        // ...
    }
}

// ✅ AFTER: Use Fixture directly in each test
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaceOrderPort placeOrderUseCase;

    @Test
    @DisplayName("POST /api/orders - 주문 생성 성공")
    void shouldCreateOrder() throws Exception {
        // Given - explicit and clear
        PlaceOrderRequest request = PlaceOrderRequestFixture.create();
        OrderResponse response = OrderResponseFixture.create();

        given(placeOrderUseCase.execute(any())).willReturn(response);

        // When & Then
        MockMvcTestUtils.performPost(mockMvc, "/api/orders", request, objectMapper)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").value(response.orderId()));
    }
}
```

### 2. Improve Test Names
```java
// ❌ BEFORE: Vague names
@Test
void testPost() { /* ... */ }

@Test
void testGet() { /* ... */ }

// ✅ AFTER: Descriptive names
@Test
@DisplayName("POST /api/orders - 주문 생성 성공, 201 Created 반환")
void shouldCreateOrderAndReturn201Created() { /* ... */ }

@Test
@DisplayName("GET /api/orders/{orderId} - 주문 조회 성공, 200 OK 반환")
void shouldGetOrderAndReturn200Ok() { /* ... */ }
```

### 3. Extract Complex Assertions
```java
// ❌ BEFORE: Complex inline assertions
@Test
void shouldCreateOrderWithAllFields() throws Exception {
    PlaceOrderRequest request = PlaceOrderRequestFixture.create();
    OrderResponse response = OrderResponseFixture.create();

    given(placeOrderUseCase.execute(any())).willReturn(response);

    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.orderId").value(response.orderId()))
        .andExpect(jsonPath("$.customerId").value(response.customerId()))
        .andExpect(jsonPath("$.productId").value(response.productId()))
        .andExpect(jsonPath("$.quantity").value(response.quantity()))
        .andExpect(jsonPath("$.status").value(response.status().name()));
}

// ✅ AFTER: Extract to test utility
@Test
@DisplayName("POST /api/orders - 모든 필드 검증")
void shouldCreateOrderWithAllFields() throws Exception {
    // Given
    PlaceOrderRequest request = PlaceOrderRequestFixture.create();
    OrderResponse response = OrderResponseFixture.create();

    given(placeOrderUseCase.execute(any())).willReturn(response);

    // When
    ResultActions result = MockMvcTestUtils.performPost(mockMvc, "/api/orders", request, objectMapper);

    // Then
    MockMvcTestUtils.assertCreated(result, "/api/orders/" + response.orderId());
    RestApiTestUtils.assertOrderResponseEquals(result, response);
}

// RestApiTestUtils.java
public class RestApiTestUtils {

    public static void assertOrderResponseEquals(ResultActions result, OrderResponse expected) throws Exception {
        result
            .andExpect(jsonPath("$.orderId").value(expected.orderId()))
            .andExpect(jsonPath("$.customerId").value(expected.customerId()))
            .andExpect(jsonPath("$.productId").value(expected.productId()))
            .andExpect(jsonPath("$.quantity").value(expected.quantity()))
            .andExpect(jsonPath("$.status").value(expected.status().name()));
    }
}
```

### 4. Group Related Tests with @Nested
```java
@DisplayName("OrderController 테스트")
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaceOrderPort placeOrderUseCase;

    @MockBean
    private LoadOrderPort loadOrderUseCase;

    @MockBean
    private CancelOrderPort cancelOrderUseCase;

    @Nested
    @DisplayName("주문 생성 (POST /api/orders)")
    class CreateOrderTests {

        @Test
        @DisplayName("정상 케이스 - 주문 생성 성공")
        void shouldCreateOrder() throws Exception {
            PlaceOrderRequest request = PlaceOrderRequestFixture.create();
            OrderResponse response = OrderResponseFixture.create();

            given(placeOrderUseCase.execute(any())).willReturn(response);

            MockMvcTestUtils.performPost(mockMvc, "/api/orders", request, objectMapper)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(response.orderId()));
        }

        @Test
        @DisplayName("검증 실패 - customerId가 null이면 400 Bad Request")
        void shouldReturn400WhenCustomerIdIsNull() throws Exception {
            PlaceOrderRequest request = PlaceOrderRequestFixture.createWithNullCustomerId();

            MockMvcTestUtils.performPost(mockMvc, "/api/orders", request, objectMapper)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("고객 ID는 필수입니다"));
        }
    }

    @Nested
    @DisplayName("주문 조회 (GET /api/orders/{orderId})")
    class GetOrderTests {

        @Test
        @DisplayName("정상 케이스 - 주문 조회 성공")
        void shouldGetOrder() throws Exception {
            String orderId = "order-123";
            OrderResponse response = OrderResponseFixture.createWithOrderId(orderId);

            given(loadOrderUseCase.loadById(orderId)).willReturn(response);

            MockMvcTestUtils.performGet(mockMvc, "/api/orders/" + orderId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId));
        }

        @Test
        @DisplayName("실패 케이스 - 존재하지 않는 주문이면 404 Not Found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            String orderId = "non-existent";

            given(loadOrderUseCase.loadById(orderId))
                .willThrow(new OrderNotFoundException(orderId));

            MockMvcTestUtils.performGet(mockMvc, "/api/orders/" + orderId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ORDER_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("주문 취소 (DELETE /api/orders/{orderId})")
    class CancelOrderTests {

        @Test
        @DisplayName("정상 케이스 - 주문 취소 성공")
        void shouldCancelOrder() throws Exception {
            String orderId = "order-123";
            CancelOrderRequest request = CancelOrderRequestFixture.create();

            MockMvcTestUtils.performDelete(mockMvc, "/api/orders/" + orderId, request, objectMapper)
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("실패 케이스 - SHIPPED 상태면 취소 불가")
        void shouldReturn400WhenOrderIsShipped() throws Exception {
            String orderId = "order-123";
            CancelOrderRequest request = CancelOrderRequestFixture.create();

            given(cancelOrderUseCase.execute(any()))
                .willThrow(new OrderCannotBeCancelledException("Cannot cancel shipped order"));

            MockMvcTestUtils.performDelete(mockMvc, "/api/orders/" + orderId, request, objectMapper)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot cancel shipped order"));
        }
    }
}
```

### 5. Extract Test Base Class (Optional)
```java
// ❌ BEFORE: Repeated setup across multiple test classes
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private PlaceOrderPort placeOrderUseCase;
    // ...
}

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CreateCustomerPort createCustomerUseCase;
    // ...
}

// ✅ AFTER: Extract to base class
public abstract class RestApiTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * POST 요청 수행 helper.
     */
    protected ResultActions performPost(String url, Object requestBody) throws Exception {
        return MockMvcTestUtils.performPost(mockMvc, url, requestBody, objectMapper);
    }

    /**
     * GET 요청 수행 helper.
     */
    protected ResultActions performGet(String url) throws Exception {
        return MockMvcTestUtils.performGet(mockMvc, url);
    }
}

@WebMvcTest(OrderController.class)
class OrderControllerTest extends RestApiTestBase {

    @MockBean
    private PlaceOrderPort placeOrderUseCase;

    @Test
    void shouldCreateOrder() throws Exception {
        // performPost() inherited from base class
        performPost("/api/orders", PlaceOrderRequestFixture.create())
            .andExpect(status().isCreated());
    }
}
```

## Tidy Workflow

### Step 1: Identify Tests Without Fixtures
```bash
# Find tests with inline object creation
grep -r "new PlaceOrderRequest\|new OrderResponse" adapter-in/rest-api/src/test/
```

### Step 2: Convert to Fixture Pattern
```bash
# For each test:
# 1. Replace inline creation with Fixture.create()
# 2. Run test to verify it still passes
./gradlew test --tests "*OrderControllerTest"
```

### Step 3: Extract Common Assertions
```bash
# Identify repeated assertion patterns
# Extract to MockMvcTestUtils or RestApiTestUtils
```

### Step 4: Final Verification
```bash
# Run all tests
./gradlew test

# Verify no inline object creation remains
grep -r "new PlaceOrderRequest" adapter-in/rest-api/src/test/
# Should return only Fixture implementations
```

## Success Criteria

- ✅ All tests use TestFixture.create() methods (NO inline object creation)
- ✅ Test names clearly describe behavior (@DisplayName)
- ✅ No duplication in test setup
- ✅ Complex assertions extracted to test utilities
- ✅ Related tests grouped with @Nested
- ✅ All tests still PASS
- ✅ Tests are easy to read and maintain
- ✅ Tests are fast and isolated

## What NOT to Do

- ❌ Don't change test behavior (tests should still pass)
- ❌ Don't create Fixtures with inline object creation
- ❌ Don't use @BeforeEach for Request/Response setup (use Fixture instead)
- ❌ Don't leave inline object creation in tests
- ❌ Don't over-complicate test structure

## Final Checklist

- [ ] All tests use PlaceOrderRequestFixture.create() methods
- [ ] All tests use OrderResponseFixture.create() methods
- [ ] No inline "new PlaceOrderRequest(...)" in tests
- [ ] No inline "new OrderResponse(...)" in tests
- [ ] TestFixture has methods for common scenarios (createWithCustomerId, createWithStatus, etc.)
- [ ] Test names use @DisplayName with clear descriptions
- [ ] Complex assertions extracted to MockMvcTestUtils or RestApiTestUtils
- [ ] Related tests grouped with @Nested
- [ ] All tests PASS after cleanup
- [ ] Code committed with message: "test: tidy REST API Layer tests with TestFixture pattern"

This is Kent Beck's TDD: After REFACTOR, TIDY tests to maintain long-term quality and readability.
