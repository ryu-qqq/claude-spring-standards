# Application Layer TDD Tidy - Clean Up Tests

You are in the TIDY phase of Kent Beck's TDD cycle for **Application Layer**.

## Instructions

1. **Code and tests are PASSING** (REFACTOR phase complete)
2. **Clean up test code** for maintainability
3. **Ensure all tests use TestFixture pattern** (mandatory!)
4. **Remove duplication** in test setup
5. **Improve test readability** and documentation
6. **Run all tests** to verify cleanup didn't break anything

## Tidy Goals

- **TestFixture Usage**: Every test uses Fixture.create() methods
- **Mock Consistency**: Standard mock patterns for Outbound Ports
- **Test Clarity**: Test names and structure clearly communicate intent
- **DRY Tests**: Eliminate duplicated mock setup
- **Fast Tests**: Remove unnecessary slow operations

## TestFixture Pattern Enforcement

### ❌ BEFORE: Inline Object Creation
```java
@Test
@DisplayName("주문 생성 - 정상 케이스")
void shouldPlaceOrder() {
    // ❌ BAD: Inline Command creation
    PlaceOrderCommand command = new PlaceOrderCommand(1L, 100L, 10);

    // ❌ BAD: Inline Domain creation for mocking
    CustomerDomain customer = new CustomerDomain(1L, "John", "john@example.com");
    OrderDomain order = new OrderDomain(
        new OrderId("order-123"),
        1L, 100L, 10,
        OrderStatus.PLACED
    );

    given(loadCustomerPort.loadById(1L))
        .willReturn(Optional.of(customer));
    given(saveOrderPort.save(any(OrderDomain.class)))
        .willReturn(order);

    OrderResponse response = placeOrderUseCase.execute(command);

    assertThat(response.orderId()).isNotNull();
}
```

### ✅ AFTER: TestFixture Pattern
```java
@Test
@DisplayName("주문 생성 - 정상 케이스")
void shouldPlaceOrder() {
    // ✅ GOOD: Use Fixtures
    PlaceOrderCommand command = PlaceOrderCommandFixture.create();
    CustomerDomain customer = CustomerFixture.create();
    OrderDomain order = OrderDomainFixture.create();

    given(loadCustomerPort.loadById(command.customerId()))
        .willReturn(Optional.of(customer));
    given(saveOrderPort.save(any(OrderDomain.class)))
        .willReturn(order);

    // When
    OrderResponse response = placeOrderUseCase.execute(command);

    // Then
    assertThat(response.orderId()).isNotNull();
    assertThat(response.status()).isEqualTo(OrderStatus.PLACED);
}
```

## TestFixture Consolidation Patterns

### Pattern 1: Command Fixture with Variants
```java
// PlaceOrderCommandFixture.java
public class PlaceOrderCommandFixture {

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long DEFAULT_PRODUCT_ID = 100L;
    private static final Integer DEFAULT_QUANTITY = 10;

    /**
     * 기본 PlaceOrderCommand 생성.
     */
    public static PlaceOrderCommand create() {
        return new PlaceOrderCommand(
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY
        );
    }

    /**
     * 특정 고객 ID로 생성.
     */
    public static PlaceOrderCommand createWithCustomerId(Long customerId) {
        return new PlaceOrderCommand(
            customerId,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY
        );
    }

    /**
     * 특정 수량으로 생성.
     */
    public static PlaceOrderCommand createWithQuantity(int quantity) {
        return new PlaceOrderCommand(
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            quantity
        );
    }

    /**
     * 잘못된 Command 생성 (Validation 테스트용).
     */
    public static PlaceOrderCommand createInvalid() {
        return new PlaceOrderCommand(null, DEFAULT_PRODUCT_ID, DEFAULT_QUANTITY);
    }

    private PlaceOrderCommandFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Pattern 2: Extract Mock Setup Utilities
```java
// UseCaseTestUtils.java
public class UseCaseTestUtils {

    /**
     * Mock setup for successful customer load.
     */
    public static void mockLoadCustomer(
        LoadCustomerPort port,
        Long customerId,
        CustomerDomain customer
    ) {
        given(port.loadById(customerId))
            .willReturn(Optional.of(customer));
    }

    /**
     * Mock setup for customer not found.
     */
    public static void mockCustomerNotFound(
        LoadCustomerPort port,
        Long customerId
    ) {
        given(port.loadById(customerId))
            .willReturn(Optional.empty());
    }

    /**
     * Mock setup for successful order save.
     */
    public static void mockSaveOrder(
        SaveOrderPort port,
        OrderDomain order
    ) {
        given(port.save(any(OrderDomain.class)))
            .willReturn(order);
    }

    private UseCaseTestUtils() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Pattern 3: Parameterized Tests with Fixture
```java
// Before: Multiple similar tests
@Test
void shouldPlaceOrderWithQuantity5() { /* ... */ }

@Test
void shouldPlaceOrderWithQuantity10() { /* ... */ }

@Test
void shouldPlaceOrderWithQuantity50() { /* ... */ }

// After: Parameterized test with Fixture
@ParameterizedTest
@ValueSource(ints = {1, 5, 10, 50, 100})
@DisplayName("주문 생성 - 다양한 수량으로 성공")
void shouldPlaceOrderWithVariousQuantities(int quantity) {
    // Given
    PlaceOrderCommand command = PlaceOrderCommandFixture.createWithQuantity(quantity);
    CustomerDomain customer = CustomerFixture.create();
    OrderDomain order = OrderDomainFixture.createWithQuantity(quantity);

    UseCaseTestUtils.mockLoadCustomer(loadCustomerPort, command.customerId(), customer);
    UseCaseTestUtils.mockSaveOrder(saveOrderPort, order);

    // When
    OrderResponse response = placeOrderUseCase.execute(command);

    // Then
    assertThat(response.orderId()).isNotNull();
    assertThat(response.status()).isEqualTo(OrderStatus.PLACED);
}
```

## Test Cleanup Patterns

### 1. Remove @BeforeEach Setup (Use Fixture Instead)
```java
// ❌ BEFORE: Setup in @BeforeEach
@ExtendWith(MockitoExtension.class)
class PlaceOrderUseCaseTest {

    @Mock
    private LoadCustomerPort loadCustomerPort;

    @Mock
    private SaveOrderPort saveOrderPort;

    @InjectMocks
    private PlaceOrderUseCase placeOrderUseCase;

    private PlaceOrderCommand command;
    private CustomerDomain customer;
    private OrderDomain order;

    @BeforeEach
    void setUp() {
        command = new PlaceOrderCommand(1L, 100L, 10);
        customer = new CustomerDomain(1L, "John", "john@example.com");
        order = new OrderDomain(...);
    }

    @Test
    void shouldPlaceOrder() {
        given(loadCustomerPort.loadById(command.customerId()))
            .willReturn(Optional.of(customer));
        // ...
    }
}

// ✅ AFTER: Use Fixture directly in each test
@ExtendWith(MockitoExtension.class)
class PlaceOrderUseCaseTest {

    @Mock
    private LoadCustomerPort loadCustomerPort;

    @Mock
    private SaveOrderPort saveOrderPort;

    @InjectMocks
    private PlaceOrderUseCase placeOrderUseCase;

    @Test
    @DisplayName("주문 생성 - 정상 케이스")
    void shouldPlaceOrder() {
        // Given - explicit and clear
        PlaceOrderCommand command = PlaceOrderCommandFixture.create();
        CustomerDomain customer = CustomerFixture.create();
        OrderDomain order = OrderDomainFixture.create();

        UseCaseTestUtils.mockLoadCustomer(loadCustomerPort, command.customerId(), customer);
        UseCaseTestUtils.mockSaveOrder(saveOrderPort, order);

        // When
        OrderResponse response = placeOrderUseCase.execute(command);

        // Then
        assertThat(response.orderId()).isNotNull();
    }
}
```

### 2. Improve Test Names
```java
// ❌ BEFORE: Vague names
@Test
void testExecute() { /* ... */ }

@Test
void testExecute2() { /* ... */ }

// ✅ AFTER: Descriptive names
@Test
@DisplayName("주문 생성 - 정상 케이스")
void shouldPlaceOrderWhenValidCommand() { /* ... */ }

@Test
@DisplayName("주문 생성 - 고객 없음 예외")
void shouldThrowExceptionWhenCustomerNotFound() { /* ... */ }
```

### 3. Extract Complex Verification
```java
// ❌ BEFORE: Complex inline verification
@Test
void shouldPlaceOrder() {
    // ...
    verify(loadCustomerPort).loadById(command.customerId());
    verify(saveOrderPort).save(argThat(order ->
        order.getCustomerId().equals(command.customerId()) &&
        order.getProductId().equals(command.productId()) &&
        order.getQuantity().equals(command.quantity()) &&
        order.getStatus().equals(OrderStatus.PLACED)
    ));
}

// ✅ AFTER: Extract to test utility
@Test
@DisplayName("주문 생성 - Port 호출 검증")
void shouldCallPortsCorrectly() {
    // ...
    UseCaseTestUtils.verifyLoadCustomer(loadCustomerPort, command.customerId());
    UseCaseTestUtils.verifySaveOrder(saveOrderPort, command);
}
```

### 4. Group Related Tests with @Nested
```java
@DisplayName("PlaceOrderUseCase 테스트")
class PlaceOrderUseCaseTest {

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("정상적인 주문 생성")
        void shouldPlaceOrderSuccessfully() {
            PlaceOrderCommand command = PlaceOrderCommandFixture.create();
            CustomerDomain customer = CustomerFixture.create();
            OrderDomain order = OrderDomainFixture.create();

            UseCaseTestUtils.mockLoadCustomer(loadCustomerPort, command.customerId(), customer);
            UseCaseTestUtils.mockSaveOrder(saveOrderPort, order);

            OrderResponse response = placeOrderUseCase.execute(command);

            assertThat(response.orderId()).isNotNull();
            assertThat(response.status()).isEqualTo(OrderStatus.PLACED);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("고객 없음 - CustomerNotFoundException 발생")
        void shouldThrowExceptionWhenCustomerNotFound() {
            PlaceOrderCommand command = PlaceOrderCommandFixture.create();

            UseCaseTestUtils.mockCustomerNotFound(loadCustomerPort, command.customerId());

            assertThatThrownBy(() -> placeOrderUseCase.execute(command))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found");
        }

        @Test
        @DisplayName("잘못된 수량 - InvalidQuantityException 발생")
        void shouldThrowExceptionWhenInvalidQuantity() {
            PlaceOrderCommand command = PlaceOrderCommandFixture.createWithQuantity(0);
            CustomerDomain customer = CustomerFixture.create();

            UseCaseTestUtils.mockLoadCustomer(loadCustomerPort, command.customerId(), customer);

            assertThatThrownBy(() -> placeOrderUseCase.execute(command))
                .isInstanceOf(InvalidQuantityException.class)
                .hasMessageContaining("Quantity must be between 1 and 100");
        }
    }
}
```

## Tidy Workflow

### Step 1: Identify Tests Without Fixtures
```bash
# Find tests with inline object creation
grep -r "new PlaceOrderCommand\|new OrderDomain" application/src/test/
```

### Step 2: Convert to Fixture Pattern
```bash
# For each test:
# 1. Replace inline creation with Fixture.create()
# 2. Run test to verify it still passes
./gradlew test --tests "*PlaceOrderUseCaseTest"
```

### Step 3: Extract Common Mock Setups
```bash
# Identify repeated mock patterns
# Extract to UseCaseTestUtils
```

### Step 4: Final Verification
```bash
# Run all tests
./gradlew test

# Verify no inline object creation remains
grep -r "new PlaceOrderCommand(" application/src/test/
# Should return only Fixture implementations
```

## Success Criteria

- ✅ All tests use TestFixture.create() methods (NO inline object creation)
- ✅ Test names clearly describe behavior (@DisplayName)
- ✅ No duplication in mock setup (use UseCaseTestUtils)
- ✅ Complex verifications extracted to test utilities
- ✅ Related tests grouped with @Nested
- ✅ All tests still PASS
- ✅ Tests are easy to read and maintain

## What NOT to Do

- ❌ Don't change test behavior (tests should still pass)
- ❌ Don't create Fixtures with inline object creation
- ❌ Don't use @BeforeEach for Command/Domain object setup (use Fixture instead)
- ❌ Don't leave inline object creation in tests
- ❌ Don't over-complicate test structure

## Final Checklist

- [ ] All tests use PlaceOrderCommandFixture.create() methods
- [ ] No inline "new PlaceOrderCommand(...)" in tests
- [ ] TestFixture has methods for common scenarios (createWithCustomerId, createWithQuantity, etc.)
- [ ] Mock setups extracted to UseCaseTestUtils
- [ ] Test names use @DisplayName with clear descriptions
- [ ] Complex verifications extracted to test utilities
- [ ] All tests PASS after cleanup
- [ ] Code committed with message: "test: tidy Application Layer tests with TestFixture pattern"

This is Kent Beck's TDD: After REFACTOR, TIDY tests to maintain long-term quality and readability.
