# Persistence Layer TDD Tidy - Clean Up Tests

You are in the TIDY phase of Kent Beck's TDD cycle for **Persistence Layer**.

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
- **Fast Tests**: Remove unnecessary slow operations

## TestFixture Pattern Enforcement

### ❌ BEFORE: Inline Object Creation
```java
@Test
@DisplayName("주문 저장 - 정상 케이스")
void shouldSaveOrder() {
    // ❌ BAD: Inline Entity creation
    OrderJpaEntity entity = new OrderJpaEntity(
        "order-123",
        1L,
        100L,
        10,
        OrderStatus.PLACED
    );

    OrderJpaEntity saved = orderJpaRepository.save(entity);

    assertThat(saved.getId()).isNotNull();
}

@Test
@DisplayName("주문 조회 - orderId로 조회")
void shouldFindByOrderId() {
    // ❌ BAD: Duplication of creation logic
    OrderJpaEntity entity = new OrderJpaEntity(
        "order-123",
        1L,
        100L,
        10,
        OrderStatus.PLACED
    );
    orderJpaRepository.save(entity);

    Optional<OrderJpaEntity> found = orderJpaRepository.findByOrderId("order-123");

    assertThat(found).isPresent();
}
```

### ✅ AFTER: TestFixture Pattern
```java
@Test
@DisplayName("주문 저장 - 정상 케이스")
void shouldSaveOrder() {
    // ✅ GOOD: Use Fixture
    OrderJpaEntity entity = OrderJpaEntityFixture.create();

    OrderJpaEntity saved = orderJpaRepository.save(entity);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCustomerId()).isEqualTo(entity.getCustomerId());
}

@Test
@DisplayName("주문 조회 - orderId로 조회")
void shouldFindByOrderId() {
    // ✅ GOOD: Use Fixture
    OrderJpaEntity entity = OrderJpaEntityFixture.create();
    OrderJpaEntity saved = orderJpaRepository.save(entity);

    Optional<OrderJpaEntity> found = orderJpaRepository.findByOrderId(saved.getOrderId());

    assertThat(found).isPresent();
    assertThat(found.get().getOrderId()).isEqualTo(saved.getOrderId());
}
```

## TestFixture Consolidation Patterns

### Pattern 1: Entity Fixture with Variants
```java
// OrderJpaEntityFixture.java
public class OrderJpaEntityFixture {

    private static final String DEFAULT_ORDER_ID = "order-123";
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
     * 특정 고객 ID로 생성.
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
     * 특정 상태로 생성.
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
     * 랜덤 orderId로 생성 (중복 방지).
     */
    public static OrderJpaEntity createWithRandomOrderId() {
        return OrderJpaEntity.of(
            UUID.randomUUID().toString(),
            DEFAULT_CUSTOMER_ID,
            DEFAULT_PRODUCT_ID,
            DEFAULT_QUANTITY,
            DEFAULT_STATUS
        );
    }

    private OrderJpaEntityFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Pattern 2: Extract Test Data Setup Utilities
```java
// PersistenceTestUtils.java
public class PersistenceTestUtils {

    /**
     * Entity 저장 및 영속성 컨텍스트 플러시.
     */
    public static <T> T saveAndFlush(JpaRepository<T, ?> repository, T entity) {
        T saved = repository.save(entity);
        repository.flush();
        return saved;
    }

    /**
     * Entity 저장 후 ID 반환.
     */
    public static Long saveAndGetId(OrderJpaRepository repository, OrderJpaEntity entity) {
        OrderJpaEntity saved = repository.save(entity);
        repository.flush();
        return saved.getId();
    }

    /**
     * 여러 Entity 저장.
     */
    public static <T> List<T> saveAll(JpaRepository<T, ?> repository, List<T> entities) {
        List<T> saved = repository.saveAll(entities);
        repository.flush();
        return saved;
    }

    private PersistenceTestUtils() {
        throw new AssertionError("Utility 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### Pattern 3: Parameterized Tests with Fixture
```java
// Before: Multiple similar tests
@Test
void shouldSaveOrderWithStatusPlaced() { /* ... */ }

@Test
void shouldSaveOrderWithStatusConfirmed() { /* ... */ }

@Test
void shouldSaveOrderWithStatusShipped() { /* ... */ }

// After: Parameterized test with Fixture
@ParameterizedTest
@EnumSource(OrderStatus.class)
@DisplayName("주문 저장 - 다양한 상태로 저장 성공")
void shouldSaveOrderWithVariousStatuses(OrderStatus status) {
    // Given
    OrderJpaEntity entity = OrderJpaEntityFixture.createWithStatus(status);

    // When
    OrderJpaEntity saved = orderJpaRepository.save(entity);

    // Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getStatus()).isEqualTo(status);
    assertThat(saved.getCreatedAt()).isNotNull();  // BaseAuditEntity
}
```

## Test Cleanup Patterns

### 1. Remove @BeforeEach Setup (Use Fixture Instead)
```java
// ❌ BEFORE: Setup in @BeforeEach
@DataJpaTest
class OrderJpaRepositoryTest {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    private OrderJpaEntity entity;

    @BeforeEach
    void setUp() {
        entity = new OrderJpaEntity("order-123", 1L, 100L, 10, OrderStatus.PLACED);
    }

    @Test
    void shouldSaveOrder() {
        OrderJpaEntity saved = orderJpaRepository.save(entity);
        assertThat(saved.getId()).isNotNull();
    }
}

// ✅ AFTER: Use Fixture directly in each test
@DataJpaTest
@ActiveProfiles("test")
class OrderJpaRepositoryTest {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    @DisplayName("주문 저장 - 정상 케이스")
    void shouldSaveOrder() {
        // Given - explicit and clear
        OrderJpaEntity entity = OrderJpaEntityFixture.create();

        // When
        OrderJpaEntity saved = orderJpaRepository.save(entity);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
```

### 2. Improve Test Names
```java
// ❌ BEFORE: Vague names
@Test
void testSave() { /* ... */ }

@Test
void testFind() { /* ... */ }

// ✅ AFTER: Descriptive names
@Test
@DisplayName("주문 저장 - Entity ID 자동 생성 검증")
void shouldGenerateIdWhenSavingOrder() { /* ... */ }

@Test
@DisplayName("주문 조회 - orderId로 조회 성공")
void shouldFindOrderByOrderId() { /* ... */ }
```

### 3. Extract Complex Assertions
```java
// ❌ BEFORE: Complex inline assertions
@Test
void shouldSaveOrderWithAllFields() {
    OrderJpaEntity entity = OrderJpaEntityFixture.create();
    OrderJpaEntity saved = orderJpaRepository.save(entity);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getOrderId()).isEqualTo(entity.getOrderId());
    assertThat(saved.getCustomerId()).isEqualTo(entity.getCustomerId());
    assertThat(saved.getProductId()).isEqualTo(entity.getProductId());
    assertThat(saved.getQuantity()).isEqualTo(entity.getQuantity());
    assertThat(saved.getStatus()).isEqualTo(entity.getStatus());
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
}

// ✅ AFTER: Extract to test utility
@Test
@DisplayName("주문 저장 - 모든 필드 검증")
void shouldSaveOrderWithAllFields() {
    // Given
    OrderJpaEntity entity = OrderJpaEntityFixture.create();

    // When
    OrderJpaEntity saved = orderJpaRepository.save(entity);

    // Then
    PersistenceTestUtils.assertOrderEntityEquals(entity, saved);
    PersistenceTestUtils.assertAuditFieldsNotNull(saved);
}
```

### 4. Group Related Tests with @Nested
```java
@DisplayName("OrderJpaRepository 테스트")
@DataJpaTest
@ActiveProfiles("test")
class OrderJpaRepositoryTest {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Nested
    @DisplayName("저장 테스트")
    class SaveTests {

        @Test
        @DisplayName("주문 저장 - ID 자동 생성")
        void shouldGenerateIdWhenSaving() {
            OrderJpaEntity entity = OrderJpaEntityFixture.create();

            OrderJpaEntity saved = orderJpaRepository.save(entity);

            assertThat(saved.getId()).isNotNull();
        }

        @Test
        @DisplayName("주문 저장 - Audit 필드 자동 생성")
        void shouldGenerateAuditFieldsWhenSaving() {
            OrderJpaEntity entity = OrderJpaEntityFixture.create();

            OrderJpaEntity saved = orderJpaRepository.save(entity);

            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("조회 테스트")
    class FindTests {

        @Test
        @DisplayName("주문 조회 - orderId로 조회 성공")
        void shouldFindByOrderId() {
            OrderJpaEntity entity = OrderJpaEntityFixture.create();
            OrderJpaEntity saved = orderJpaRepository.save(entity);

            Optional<OrderJpaEntity> found = orderJpaRepository.findByOrderId(saved.getOrderId());

            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("주문 조회 - 존재하지 않는 orderId 조회 실패")
        void shouldReturnEmptyWhenOrderIdNotFound() {
            Optional<OrderJpaEntity> found = orderJpaRepository.findByOrderId("non-existent");

            assertThat(found).isEmpty();
        }
    }
}
```

### 5. Use @Sql for Database Setup
```java
// ❌ BEFORE: Manual setup in each test
@Test
void shouldFindOrdersByCustomerId() {
    OrderJpaEntity entity1 = OrderJpaEntityFixture.createWithCustomerId(1L);
    OrderJpaEntity entity2 = OrderJpaEntityFixture.createWithCustomerId(1L);
    orderJpaRepository.saveAll(List.of(entity1, entity2));

    List<OrderJpaEntity> result = orderJpaRepository.findByCustomerId(1L);

    assertThat(result).hasSize(2);
}

// ✅ AFTER: Use @Sql for complex setup
@Test
@Sql("/sql/orders-setup.sql")
@DisplayName("주문 조회 - customerId로 여러 주문 조회")
void shouldFindOrdersByCustomerId() {
    // Given - already set up by SQL script

    // When
    List<OrderJpaEntity> result = orderJpaRepository.findByCustomerId(1L);

    // Then
    assertThat(result).hasSize(2);
}
```

## Tidy Workflow

### Step 1: Identify Tests Without Fixtures
```bash
# Find tests with inline object creation
grep -r "new OrderJpaEntity(" persistence/src/test/
```

### Step 2: Convert to Fixture Pattern
```bash
# For each test:
# 1. Replace inline creation with Fixture.create()
# 2. Run test to verify it still passes
./gradlew test --tests "*OrderJpaRepositoryTest"
```

### Step 3: Extract Common Assertions
```bash
# Identify repeated assertion patterns
# Extract to PersistenceTestUtils
```

### Step 4: Final Verification
```bash
# Run all tests
./gradlew test

# Verify no inline object creation remains
grep -r "new OrderJpaEntity(" persistence/src/test/
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
- ❌ Don't use @BeforeEach for Entity setup (use Fixture instead)
- ❌ Don't leave inline object creation in tests
- ❌ Don't over-complicate test structure

## Final Checklist

- [ ] All tests use OrderJpaEntityFixture.create() methods
- [ ] No inline "new OrderJpaEntity(...)" in tests
- [ ] TestFixture has methods for common scenarios (createWithCustomerId, createWithStatus, etc.)
- [ ] Test names use @DisplayName with clear descriptions
- [ ] Complex assertions extracted to PersistenceTestUtils
- [ ] Related tests grouped with @Nested
- [ ] All tests PASS after cleanup
- [ ] Code committed with message: "test: tidy Persistence Layer tests with TestFixture pattern"

This is Kent Beck's TDD: After REFACTOR, TIDY tests to maintain long-term quality and readability.
