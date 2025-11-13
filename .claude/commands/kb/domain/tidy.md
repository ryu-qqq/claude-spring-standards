# Domain Layer TDD Tidy - Clean Up Tests

You are in the TIDY phase of Kent Beck's TDD cycle for **Domain Layer**.

## Instructions

1. **Code and tests are PASSING** (REFACTOR phase complete)
2. **Clean up test code** for maintainability
3. **Ensure all tests use TestFixture pattern** (mandatory!)
4. **Remove duplication** in test setup
5. **Improve test readability** and documentation
6. **Run all tests** to verify cleanup didn't break anything

## Tidy Goals

- **TestFixture Usage**: Every test uses Fixture.create() methods
- **Test Clarity**: Test names and structure clearly communicate intent
- **DRY Tests**: Eliminate duplicated setup code
- **Maintainability**: Easy to add new tests or modify existing ones
- **Fast Tests**: Remove unnecessary slow operations

## TestFixture Pattern Enforcement

### ❌ BEFORE: Inline Object Creation
```java
@Test
@DisplayName("주문 취소 - PLACED 상태만 가능")
void shouldCancelOrderWhenPlaced() {
    // ❌ BAD: Inline object creation
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    OrderDomain order = OrderDomain.create(
        orderId,
        1L,              // customerId
        100L,            // productId
        10,              // quantity
        OrderStatus.PLACED
    );

    order.cancel(CancelReason.CUSTOMER_REQUEST);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
}

@Test
@DisplayName("주문 취소 - CONFIRMED 상태 가능 (수수료 발생)")
void shouldCancelOrderWhenConfirmedWithFee() {
    // ❌ BAD: Duplication of creation logic
    OrderId orderId = new OrderId(UUID.randomUUID().toString());
    OrderDomain order = OrderDomain.create(
        orderId,
        1L,
        100L,
        10,
        OrderStatus.CONFIRMED  // Only difference!
    );

    order.cancel(CancelReason.CUSTOMER_REQUEST);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    assertThat(order.getCancellationFee()).isEqualTo(BigDecimal.valueOf(1000));
}
```

### ✅ AFTER: TestFixture Pattern
```java
@Test
@DisplayName("주문 취소 - PLACED 상태만 가능")
void shouldCancelOrderWhenPlaced() {
    // ✅ GOOD: Use Fixture
    OrderDomain order = OrderDomainFixture.create();

    order.cancel(CancelReason.CUSTOMER_REQUEST);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
}

@Test
@DisplayName("주문 취소 - CONFIRMED 상태 가능 (수수료 발생)")
void shouldCancelOrderWhenConfirmedWithFee() {
    // ✅ GOOD: Use specific state Fixture
    OrderDomain order = OrderDomainFixture.createConfirmed();

    order.cancel(CancelReason.CUSTOMER_REQUEST);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    assertThat(order.getCancellationFee()).isEqualTo(BigDecimal.valueOf(1000));
}
```

## TestFixture Consolidation Patterns

### Pattern 1: Extract Common Test Data
```java
// TestFixture class
public class OrderDomainFixture {

    // Default test data (centralized)
    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long DEFAULT_PRODUCT_ID = 100L;
    private static final Integer DEFAULT_QUANTITY = 10;
    private static final BigDecimal DEFAULT_UNIT_PRICE = BigDecimal.valueOf(1000);
    private static final OrderStatus DEFAULT_STATUS = OrderStatus.PLACED;

    /**
     * 기본 OrderDomain 생성 (PLACED 상태).
     */
    public static OrderDomain create() {
        return OrderDomain.create(
            OrderId.generate(),
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            DEFAULT_UNIT_PRICE,
            DEFAULT_STATUS
        );
    }

    /**
     * 특정 상태로 OrderDomain 생성.
     */
    public static OrderDomain createWithStatus(OrderStatus status) {
        return OrderDomain.create(
            OrderId.generate(),
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            DEFAULT_UNIT_PRICE,
            status
        );
    }

    /**
     * CONFIRMED 상태 OrderDomain 생성.
     */
    public static OrderDomain createConfirmed() {
        return createWithStatus(OrderStatus.CONFIRMED);
    }

    /**
     * SHIPPED 상태 OrderDomain 생성.
     */
    public static OrderDomain createShipped() {
        return createWithStatus(OrderStatus.SHIPPED);
    }

    /**
     * 특정 수량으로 OrderDomain 생성.
     */
    public static OrderDomain createWithQuantity(int quantity) {
        return OrderDomain.create(
            OrderId.generate(),
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            quantity,
            DEFAULT_UNIT_PRICE,
            DEFAULT_STATUS
        );
    }

    private OrderDomainFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Pattern 2: Extract Test Utilities
```java
// Test utility methods
public class OrderDomainTestUtils {

    /**
     * Assert that order is in cancelled state with reason.
     */
    public static void assertCancelled(OrderDomain order, CancelReason expectedReason) {
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getCancelReason()).isEqualTo(expectedReason);
        assertThat(order.getCancelledAt()).isNotNull();
    }

    /**
     * Assert that order has cancellation fee.
     */
    public static void assertHasCancellationFee(OrderDomain order, BigDecimal expectedFee) {
        assertThat(order.getCancellationFee()).isEqualByComparingTo(expectedFee);
    }

    private OrderDomainTestUtils() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Pattern 3: Parameterized Tests with Fixture
```java
// Before: Multiple similar tests
@Test
void shouldCancelOrderFromPlaced() { /* ... */ }

@Test
void shouldCancelOrderFromConfirmed() { /* ... */ }

// After: Parameterized test with Fixture
@ParameterizedTest
@MethodSource("provideCancellableStates")
@DisplayName("주문 취소 - 취소 가능한 상태에서 취소 성공")
void shouldCancelOrderFromCancellableStates(OrderStatus initialStatus, BigDecimal expectedFee) {
    // Given
    OrderDomain order = OrderDomainFixture.createWithStatus(initialStatus);

    // When
    order.cancel(CancelReason.CUSTOMER_REQUEST);

    // Then
    OrderDomainTestUtils.assertCancelled(order, CancelReason.CUSTOMER_REQUEST);
    if (expectedFee != null) {
        OrderDomainTestUtils.assertHasCancellationFee(order, expectedFee);
    }
}

private static Stream<Arguments> provideCancellableStates() {
    return Stream.of(
        Arguments.of(OrderStatus.PLACED, null),
        Arguments.of(OrderStatus.CONFIRMED, BigDecimal.valueOf(1000))
    );
}
```

## Test Cleanup Patterns

### 1. Remove @BeforeEach Setup (Use Fixture Instead)
```java
// ❌ BEFORE: Setup in @BeforeEach
class OrderDomainTest {

    private OrderDomain order;

    @BeforeEach
    void setUp() {
        order = OrderDomain.create(
            OrderId.generate(),
            1L, 100L, 10,
            OrderStatus.PLACED
        );
    }

    @Test
    void shouldCancelOrder() {
        order.cancel(CancelReason.CUSTOMER_REQUEST);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}

// ✅ AFTER: Use Fixture directly in each test
class OrderDomainTest {

    @Test
    @DisplayName("주문 취소 - PLACED 상태만 가능")
    void shouldCancelOrder() {
        // Given - explicit and clear
        OrderDomain order = OrderDomainFixture.create();

        // When
        order.cancel(CancelReason.CUSTOMER_REQUEST);

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}
```

### 2. Improve Test Names
```java
// ❌ BEFORE: Vague names
@Test
void testCancel() { /* ... */ }

@Test
void testCancel2() { /* ... */ }

// ✅ AFTER: Descriptive names
@Test
@DisplayName("주문 취소 - PLACED 상태에서 취소 성공")
void shouldCancelOrderWhenPlaced() { /* ... */ }

@Test
@DisplayName("주문 취소 - SHIPPED 상태에서 취소 실패")
void shouldNotCancelOrderWhenShipped() { /* ... */ }
```

### 3. Extract Complex Assertions
```java
// ❌ BEFORE: Complex inline assertions
@Test
void shouldCalculateTotalPrice() {
    OrderDomain order = OrderDomainFixture.create();
    BigDecimal total = order.getTotalPrice();

    BigDecimal expected = order.getUnitPrice()
        .multiply(BigDecimal.valueOf(order.getQuantity()))
        .add(order.getShippingFee())
        .multiply(BigDecimal.ONE.add(order.getTaxRate()));

    assertThat(total).isEqualByComparingTo(expected);
}

// ✅ AFTER: Extract to test utility
@Test
@DisplayName("총 가격 계산 - 상품 가격 + 배송비 + 세금")
void shouldCalculateTotalPrice() {
    // Given
    OrderDomain order = OrderDomainFixture.create();

    // When
    BigDecimal total = order.getTotalPrice();

    // Then
    OrderDomainTestUtils.assertTotalPrice(order, total);
}
```

### 4. Group Related Tests with @Nested
```java
@DisplayName("OrderDomain 주문 취소 테스트")
class OrderDomainCancellationTest {

    @Nested
    @DisplayName("취소 가능한 경우")
    class WhenCancellable {

        @Test
        @DisplayName("PLACED 상태 - 수수료 없이 취소 성공")
        void shouldCancelWithoutFeeFromPlaced() {
            OrderDomain order = OrderDomainFixture.create();
            order.cancel(CancelReason.CUSTOMER_REQUEST);
            OrderDomainTestUtils.assertCancelled(order, CancelReason.CUSTOMER_REQUEST);
        }

        @Test
        @DisplayName("CONFIRMED 상태 - 수수료 포함 취소 성공")
        void shouldCancelWithFeeFromConfirmed() {
            OrderDomain order = OrderDomainFixture.createConfirmed();
            order.cancel(CancelReason.CUSTOMER_REQUEST);
            OrderDomainTestUtils.assertCancelled(order, CancelReason.CUSTOMER_REQUEST);
            OrderDomainTestUtils.assertHasCancellationFee(order, BigDecimal.valueOf(1000));
        }
    }

    @Nested
    @DisplayName("취소 불가능한 경우")
    class WhenNotCancellable {

        @Test
        @DisplayName("SHIPPED 상태 - 취소 불가 예외 발생")
        void shouldNotCancelFromShipped() {
            OrderDomain order = OrderDomainFixture.createShipped();

            assertThatThrownBy(() -> order.cancel(CancelReason.CUSTOMER_REQUEST))
                .isInstanceOf(OrderCannotBeCancelledException.class)
                .hasMessageContaining("SHIPPED");
        }
    }
}
```

## Tidy Workflow

### Step 1: Identify Tests Without Fixtures
```bash
# Find tests with inline object creation
grep -r "new OrderDomain\|OrderDomain.create" domain/src/test/
```

### Step 2: Convert to Fixture Pattern
```bash
# For each test:
# 1. Replace inline creation with Fixture.create()
# 2. Run test to verify it still passes
./gradlew test --tests "*OrderDomainTest"
```

### Step 3: Extract Common Assertions
```bash
# Identify repeated assertion patterns
# Extract to OrderDomainTestUtils
```

### Step 4: Final Verification
```bash
# Run all tests
./gradlew test

# Verify no inline object creation remains
grep -r "new OrderDomain(" domain/src/test/
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

## What NOT to Do

- ❌ Don't change test behavior (tests should still pass)
- ❌ Don't create Fixtures with inline object creation
- ❌ Don't use @BeforeEach for Domain object setup (use Fixture instead)
- ❌ Don't leave inline object creation in tests
- ❌ Don't over-complicate test structure

## Final Checklist

- [ ] All tests use OrderDomainFixture.create() methods
- [ ] No inline "new OrderDomain(...)" in tests
- [ ] TestFixture has methods for common scenarios (createConfirmed, createShipped, etc.)
- [ ] Test names use @DisplayName with clear descriptions
- [ ] Complex assertions extracted to OrderDomainTestUtils
- [ ] All tests PASS after cleanup
- [ ] Code committed with message: "test: tidy Domain Layer tests with TestFixture pattern"

This is Kent Beck's TDD: After REFACTOR, TIDY tests to maintain long-term quality and readability.
