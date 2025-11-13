# Domain Layer TDD Red - Write Failing Test

You are in the RED phase of Kent Beck's TDD cycle for **Domain Layer**.

## Instructions

1. **Read plan file** from `docs/prd/plans/{ISSUE-KEY}-domain-plan.md`
2. **Understand the requirement** for the current test
3. **Create TestFixture classes FIRST** (if not exists)
4. **Write the simplest failing test** using TestFixture
5. **Run the test** and verify it FAILS for the right reason
6. **Report the failure** clearly

## Domain Layer TestFixture Pattern (MANDATORY)

### Why TestFixture in Domain Layer?
- **Reusability**: Share Aggregate and Value Object creation across tests
- **Law of Demeter**: Encapsulate object creation logic
- **Maintainability**: Change test data in one place
- **Zero-Tolerance compliance**: Enforce Pure Java (no Lombok)

### TestFixture Structure
```
domain/src/
├── main/java/
│   └── {basePackage}/domain/
│       ├── OrderDomain.java
│       ├── OrderId.java
│       └── OrderStatus.java
└── testFixtures/java/
    └── {basePackage}/domain/fixture/
        ├── OrderDomainFixture.java
        ├── OrderIdFixture.java
        └── OrderStatusFixture.java
```

### TestFixture Template (Domain Layer)
```java
package com.company.template.domain.fixture;

import com.company.template.domain.OrderDomain;
import com.company.template.domain.OrderId;
import com.company.template.domain.OrderStatus;

/**
 * TestFixture for OrderDomain.
 *
 * <p>Object Mother 패턴으로 Domain Aggregate를 생성합니다.</p>
 *
 * @author Claude Code
 * @since 2025-01-13
 */
public class OrderDomainFixture {

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long DEFAULT_PRODUCT_ID = 100L;
    private static final Integer DEFAULT_QUANTITY = 10;
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
            status
        );
    }

    /**
     * PENDING 상태 OrderDomain 생성 (결제 대기).
     */
    public static OrderDomain createPending() {
        return createWithStatus(OrderStatus.PENDING);
    }

    /**
     * CONFIRMED 상태 OrderDomain 생성 (주문 승인).
     */
    public static OrderDomain createConfirmed() {
        return createWithStatus(OrderStatus.CONFIRMED);
    }

    /**
     * CANCELLED 상태 OrderDomain 생성 (주문 취소).
     */
    public static OrderDomain createCancelled() {
        return createWithStatus(OrderStatus.CANCELLED);
    }

    /**
     * Fixture 클래스는 인스턴스화할 수 없습니다.
     */
    private OrderDomainFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Value Object Fixture Example
```java
package com.company.template.domain.fixture;

import com.company.template.domain.OrderId;

import java.util.UUID;

/**
 * TestFixture for OrderId.
 *
 * @author Claude Code
 * @since 2025-01-13
 */
public class OrderIdFixture {

    private static final String DEFAULT_ORDER_ID = "550e8400-e29b-41d4-a716-446655440000";

    /**
     * 기본 OrderId 생성.
     */
    public static OrderId create() {
        return new OrderId(DEFAULT_ORDER_ID);
    }

    /**
     * 랜덤 OrderId 생성.
     */
    public static OrderId createRandom() {
        return new OrderId(UUID.randomUUID().toString());
    }

    /**
     * 특정 값으로 OrderId 생성.
     */
    public static OrderId createWith(String value) {
        return new OrderId(value);
    }

    private OrderIdFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

## RED Phase Workflow with TestFixture

**Step 1: Create Fixtures FIRST**
```bash
# Create testFixtures directory structure
mkdir -p domain/src/testFixtures/java/{basePackage}/domain/fixture/

# Create Fixture classes
touch domain/src/testFixtures/java/.../OrderDomainFixture.java
touch domain/src/testFixtures/java/.../OrderIdFixture.java
```

**Step 2: Write Tests Using Fixtures**
```java
package com.company.template.domain;

import com.company.template.domain.fixture.OrderDomainFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderDomainTest {

    @Test
    @DisplayName("주문 취소 - PLACED 상태만 가능")
    void shouldCancelOrderWhenPlaced() {
        // Given - Use Fixture
        OrderDomain order = OrderDomainFixture.create(); // PLACED 상태

        // When
        order.cancel(CancelReason.CUSTOMER_REQUEST);

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getCancelReason()).isEqualTo(CancelReason.CUSTOMER_REQUEST);
    }

    @Test
    @DisplayName("주문 취소 - CONFIRMED 상태 가능 (수수료 발생)")
    void shouldCancelOrderWhenConfirmedWithFee() {
        // Given - Use Fixture
        OrderDomain order = OrderDomainFixture.createConfirmed();

        // When
        order.cancel(CancelReason.CUSTOMER_REQUEST);

        // Then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getCancellationFee()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("주문 취소 - SHIPPED 상태는 불가")
    void shouldNotCancelOrderWhenShipped() {
        // Given - Use Fixture
        OrderDomain order = OrderDomainFixture.createWithStatus(OrderStatus.SHIPPED);

        // When & Then
        assertThatThrownBy(() -> order.cancel(CancelReason.CUSTOMER_REQUEST))
            .isInstanceOf(OrderCannotBeCancelledException.class)
            .hasMessageContaining("SHIPPED 상태에서는 취소할 수 없습니다");
    }
}
```

## Domain Layer Specific Test Patterns

### 1. State Transition Test
```java
@Test
@DisplayName("주문 상태 전환 - PENDING → PLACED")
void shouldTransitionFromPendingToPlaced() {
    // Given
    OrderDomain order = OrderDomainFixture.createPending();

    // When
    order.confirmPayment();

    // Then
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);
}
```

### 2. Invariant Validation Test
```java
@Test
@DisplayName("주문 수량 - 최소 1개, 최대 100개")
void shouldValidateQuantityRange() {
    // When & Then (Min)
    assertThatThrownBy(() -> OrderDomain.create(
        OrderIdFixture.create(),
        1L,
        100L,
        0, // Invalid: < 1
        OrderStatus.PLACED
    )).isInstanceOf(InvalidQuantityException.class);

    // When & Then (Max)
    assertThatThrownBy(() -> OrderDomain.create(
        OrderIdFixture.create(),
        1L,
        100L,
        101, // Invalid: > 100
        OrderStatus.PLACED
    )).isInstanceOf(InvalidQuantityException.class);
}
```

### 3. Law of Demeter Compliance Test
```java
// ✅ CORRECT (Tell, Don't Ask)
@Test
@DisplayName("주문 금액 계산 - 내부 캡슐화")
void shouldCalculateTotalPriceInternally() {
    // Given
    OrderDomain order = OrderDomainFixture.create();

    // When
    BigDecimal totalPrice = order.calculateTotalPrice();

    // Then
    assertThat(totalPrice).isPositive();
}

// ❌ WRONG (Ask, Then Tell - Getter Chaining)
@Test
void shouldCalculateTotalPriceExternally() {
    OrderDomain order = OrderDomainFixture.create();

    // ❌ Law of Demeter 위반
    BigDecimal totalPrice = order.getCustomer().getAddress().getShippingFee();
}
```

## Core Principles

- **Fixture First**: Always create Fixture classes before writing tests
- Write the SIMPLEST test that could possibly fail
- Test should fail for the RIGHT reason (not compilation error)
- One assertion per test when possible
- Test name describes the expected behavior
- No implementation code yet - just the test
- **Use Fixture.create()** instead of inline object creation

## Success Criteria

- ✅ TestFixture classes created in `testFixtures/` directory
- ✅ Test written with clear, descriptive name
- ✅ Test uses Fixture.create() methods (NOT inline object creation)
- ✅ Test runs and FAILS
- ✅ Failure message is clear and informative
- ✅ Test defines a small, specific increment of functionality
- ✅ Zero-Tolerance rules followed (no Lombok, Law of Demeter, Long FK)

## What NOT to Do

- ❌ Don't write implementation code yet
- ❌ Don't write multiple tests at once
- ❌ Don't skip running the test to verify failure
- ❌ Don't write tests that pass immediately
- ❌ Don't create objects inline in tests (use Fixture instead)
- ❌ Don't use Lombok in Domain code
- ❌ Don't use Getter chaining (Law of Demeter)
- ❌ Don't use JPA relationship annotations (Long FK 전략)

This is Kent Beck's TDD: Start with RED, make the failure explicit, and use TestFixture for maintainability.
