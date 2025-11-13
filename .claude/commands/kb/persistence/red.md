# Persistence Layer TDD Red - Write Failing Test

You are in the RED phase of Kent Beck's TDD cycle for **Persistence Layer**.

## Instructions

1. **Read plan file** from `docs/prd/plans/{ISSUE-KEY}-persistence-plan.md`
2. **Understand the requirement** for the current test
3. **Create TestFixture classes FIRST** (if not exists)
4. **Write the simplest failing test** using TestFixture
5. **Run the test** and verify it FAILS for the right reason
6. **Report the failure** clearly

## Persistence Layer TestFixture Pattern (MANDATORY)

### Why TestFixture in Persistence Layer?
- **Reusability**: Share Entity creation across tests
- **Long FK Strategy**: Consistent FK value management
- **Maintainability**: Change test data in one place
- **Integration Testing**: Support Testcontainers scenarios

### TestFixture Structure
```
persistence/src/
├── main/java/
│   └── {basePackage}/persistence/
│       ├── entity/
│       │   ├── OrderJpaEntity.java
│       │   └── BaseAuditEntity.java
│       ├── repository/
│       │   └── OrderJpaRepository.java
│       ├── adapter/
│       │   ├── SaveOrderAdapter.java
│       │   └── LoadOrderQueryAdapter.java
│       └── mapper/
│           └── OrderEntityMapper.java
└── testFixtures/java/
    └── {basePackage}/persistence/fixture/
        ├── OrderJpaEntityFixture.java
        └── OrderDtoFixture.java
```

### TestFixture Template (JPA Entity)
```java
package com.company.template.persistence.fixture;

import com.company.template.persistence.entity.OrderJpaEntity;
import com.company.template.domain.OrderStatus;

/**
 * TestFixture for OrderJpaEntity.
 *
 * <p>Object Mother 패턴으로 JPA Entity를 생성합니다.</p>
 *
 * @author Claude Code
 * @since 2025-01-13
 */
public class OrderJpaEntityFixture {

    private static final String DEFAULT_ORDER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long DEFAULT_PRODUCT_ID = 100L;
    private static final Integer DEFAULT_QUANTITY = 10;
    private static final OrderStatus DEFAULT_STATUS = OrderStatus.PLACED;

    /**
     * 기본 OrderJpaEntity 생성 (ID 없음, 영속화 전).
     */
    public static OrderJpaEntity create() {
        return OrderJpaEntity.of(
            DEFAULT_ORDER_ID,
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            DEFAULT_STATUS
        );
    }

    /**
     * 특정 고객 ID로 OrderJpaEntity 생성.
     */
    public static OrderJpaEntity createWithCustomerId(Long customerId) {
        return OrderJpaEntity.of(
            DEFAULT_ORDER_ID,
            customerId,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            DEFAULT_STATUS
        );
    }

    /**
     * 특정 상태로 OrderJpaEntity 생성.
     */
    public static OrderJpaEntity createWithStatus(OrderStatus status) {
        return OrderJpaEntity.of(
            DEFAULT_ORDER_ID,
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            status
        );
    }

    /**
     * ID가 있는 OrderJpaEntity 생성 (영속화 후 시뮬레이션).
     */
    public static OrderJpaEntity createWithId(Long id) {
        OrderJpaEntity entity = OrderJpaEntity.of(
            DEFAULT_ORDER_ID,
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            DEFAULT_STATUS
        );
        entity.setId(id);  // Reflection or test-only setter
        return entity;
    }

    private OrderJpaEntityFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### QueryDSL DTO Fixture
```java
package com.company.template.persistence.fixture;

import com.company.template.persistence.dto.OrderDto;
import com.company.template.domain.OrderStatus;

/**
 * TestFixture for OrderDto (QueryDSL Projection).
 *
 * @author Claude Code
 * @since 2025-01-13
 */
public class OrderDtoFixture {

    private static final String DEFAULT_ORDER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final Long DEFAULT_PRODUCT_ID = 100L;
    private static final Integer DEFAULT_QUANTITY = 10;
    private static final OrderStatus DEFAULT_STATUS = OrderStatus.PLACED;

    /**
     * 기본 OrderDto 생성.
     */
    public static OrderDto create() {
        return new OrderDto(
            DEFAULT_ORDER_ID,
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            DEFAULT_STATUS
        );
    }

    /**
     * 특정 상태로 OrderDto 생성.
     */
    public static OrderDto createWithStatus(OrderStatus status) {
        return new OrderDto(
            DEFAULT_ORDER_ID,
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            status
        );
    }

    private OrderDtoFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

## RED Phase Workflow with TestFixture

**Step 1: Create Fixtures FIRST**
```bash
# Create testFixtures directory structure
mkdir -p persistence/src/testFixtures/java/{basePackage}/persistence/fixture/

# Create Fixture classes
touch persistence/src/testFixtures/java/.../OrderJpaEntityFixture.java
touch persistence/src/testFixtures/java/.../OrderDtoFixture.java
```

**Step 2: Write Tests Using Fixtures**
```java
package com.company.template.persistence.adapter;

import com.company.template.persistence.fixture.OrderJpaEntityFixture;
import com.company.template.persistence.entity.OrderJpaEntity;
import com.company.template.persistence.repository.OrderJpaRepository;
import com.company.template.domain.OrderDomain;
import com.company.template.domain.fixture.OrderDomainFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SaveOrderAdapterTest {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    private SaveOrderAdapter saveOrderAdapter;

    @BeforeEach
    void setUp() {
        saveOrderAdapter = new SaveOrderAdapter(orderJpaRepository);
    }

    @Test
    @DisplayName("주문 저장 - 정상 케이스")
    void shouldSaveOrder() {
        // Given - Use Fixtures
        OrderDomain domain = OrderDomainFixture.create();

        // When
        OrderDomain saved = saveOrderAdapter.save(domain);

        // Then
        assertThat(saved.getOrderId()).isNotNull();
        assertThat(saved.getCustomerId()).isEqualTo(domain.getCustomerId());
    }

    @Test
    @DisplayName("주문 저장 - Entity로 변환 검증")
    void shouldConvertDomainToEntity() {
        // Given - Use Fixtures
        OrderDomain domain = OrderDomainFixture.create();

        // When
        OrderDomain saved = saveOrderAdapter.save(domain);

        // Then - Verify entity was saved
        OrderJpaEntity entity = orderJpaRepository.findByOrderId(
            saved.getOrderId().getValue()
        ).orElseThrow();

        assertThat(entity.getCustomerId()).isEqualTo(domain.getCustomerId());
        assertThat(entity.getProductId()).isEqualTo(domain.getProductId());
        assertThat(entity.getQuantity()).isEqualTo(domain.getQuantity());
    }
}
```

## Persistence Layer Specific Test Patterns

### 1. Command Adapter Test (저장)
```java
@Test
@DisplayName("주문 저장 Adapter - Domain → Entity 변환")
void shouldSaveDomainAsEntity() {
    // Given
    OrderDomain domain = OrderDomainFixture.create();

    // When
    OrderDomain saved = saveOrderAdapter.save(domain);

    // Then
    assertThat(saved.getOrderId()).isNotNull();

    // Verify Entity was saved with Long FK
    OrderJpaEntity entity = orderJpaRepository.findByOrderId(
        saved.getOrderId().getValue()
    ).orElseThrow();

    assertThat(entity.getCustomerId()).isEqualTo(domain.getCustomerId());
    assertThat(entity.getProductId()).isEqualTo(domain.getProductId());
}
```

### 2. Query Adapter Test (조회 with DTO Projection)
```java
@Test
@DisplayName("주문 조회 Adapter - DTO Projection")
void shouldLoadOrderWithDtoProjection() {
    // Given
    OrderJpaEntity entity = OrderJpaEntityFixture.create();
    orderJpaRepository.save(entity);

    // When
    Optional<OrderDomain> result = loadOrderQueryAdapter.loadById(
        entity.getOrderId()
    );

    // Then
    assertThat(result).isPresent();
    OrderDomain domain = result.get();
    assertThat(domain.getOrderId().getValue()).isEqualTo(entity.getOrderId());
    assertThat(domain.getCustomerId()).isEqualTo(entity.getCustomerId());
}
```

### 3. Mapper Test (Entity ↔ Domain)
```java
@Test
@DisplayName("Mapper - Domain → Entity 변환")
void shouldConvertDomainToEntity() {
    // Given
    OrderDomain domain = OrderDomainFixture.create();

    // When
    OrderJpaEntity entity = OrderEntityMapper.toEntity(domain);

    // Then
    assertThat(entity.getOrderId()).isEqualTo(domain.getOrderId().getValue());
    assertThat(entity.getCustomerId()).isEqualTo(domain.getCustomerId());
    assertThat(entity.getProductId()).isEqualTo(domain.getProductId());
}

@Test
@DisplayName("Mapper - Entity → Domain 변환")
void shouldConvertEntityToDomain() {
    // Given
    OrderJpaEntity entity = OrderJpaEntityFixture.createWithId(1L);

    // When
    OrderDomain domain = OrderEntityMapper.toDomain(entity);

    // Then
    assertThat(domain.getOrderId().getValue()).isEqualTo(entity.getOrderId());
    assertThat(domain.getCustomerId()).isEqualTo(entity.getCustomerId());
}
```

### 4. JPA Repository Test (기본 CRUD)
```java
@Test
@DisplayName("JPA Repository - 저장 및 조회")
void shouldSaveAndFindEntity() {
    // Given
    OrderJpaEntity entity = OrderJpaEntityFixture.create();

    // When
    OrderJpaEntity saved = orderJpaRepository.save(entity);

    // Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCreatedAt()).isNotNull();  // BaseAuditEntity
    assertThat(saved.getUpdatedAt()).isNotNull();

    // Verify Long FK
    assertThat(saved.getCustomerId()).isEqualTo(entity.getCustomerId());
    assertThat(saved.getProductId()).isEqualTo(entity.getProductId());
}
```

### 5. QueryDSL Dynamic Query Test
```java
@Test
@DisplayName("QueryDSL - 동적 쿼리 (customerId 조건)")
void shouldFindOrdersByCustomerId() {
    // Given
    Long customerId = 1L;
    OrderJpaEntity entity1 = OrderJpaEntityFixture.createWithCustomerId(customerId);
    OrderJpaEntity entity2 = OrderJpaEntityFixture.createWithCustomerId(customerId);
    orderJpaRepository.saveAll(List.of(entity1, entity2));

    // When
    List<OrderDomain> result = loadOrderQueryAdapter.findByCustomerId(customerId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).allMatch(order -> order.getCustomerId().equals(customerId));
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
- Use `@DataJpaTest` for JPA Repository tests

## Success Criteria

- ✅ TestFixture classes created in `testFixtures/` directory
- ✅ Test written with clear, descriptive name
- ✅ Test uses Fixture.create() methods (NOT inline object creation)
- ✅ Test runs and FAILS
- ✅ Failure message is clear and informative
- ✅ Test defines a small, specific increment of functionality
- ✅ Zero-Tolerance rules followed (Long FK, Lombok 금지, QueryDSL DTO Projection)

## What NOT to Do

- ❌ Don't write implementation code yet
- ❌ Don't write multiple tests at once
- ❌ Don't skip running the test to verify failure
- ❌ Don't write tests that pass immediately
- ❌ Don't create objects inline in tests (use Fixture instead)
- ❌ Don't use JPA relationship annotations in Entity
- ❌ Don't use Lombok in JPA Entity
- ❌ Don't query Entity directly (use DTO Projection)

This is Kent Beck's TDD: Start with RED, make the failure explicit, and use TestFixture for maintainability.
