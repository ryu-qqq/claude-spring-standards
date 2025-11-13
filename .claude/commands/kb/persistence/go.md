# Persistence Layer TDD Go - Execute Next Test from Plan

You are executing the Kent Beck TDD workflow for **Persistence Layer**.

## Instructions

1. **Read plan file** from `docs/prd/plans/{ISSUE-KEY}-persistence-plan.md`
2. **Find the next unmarked test** in the Persistence Layer section
3. **Mark the test as in-progress** by adding a checkbox or marker
4. **Execute the TDD Cycle**:
   - **RED**: Write the simplest failing test first
   - **GREEN**: Implement minimum code to make the test pass
   - **REFACTOR**: Improve structure only after tests pass
   - **TIDY**: Clean up tests using TestFixture pattern
5. **Run all tests** (excluding long-running tests)
6. **Verify** all tests pass before proceeding
7. **Mark test complete** in plan file

## Persistence Layer Specific Rules

### Zero-Tolerance Rules (MUST follow)
- ✅ **Long FK 전략**: JPA 관계 어노테이션 절대 금지 (`@ManyToOne`, `@OneToMany` 등)
- ✅ **Lombok 금지**: JPA Entity에 Lombok 사용 금지
- ✅ **Constructor Pattern**: Private 생성자 + Static factory method 또는 Protected 생성자
- ✅ **Audit Entity**: BaseAuditEntity 상속으로 생성/수정 시간 관리
- ✅ **QueryDSL DTO Projection**: Entity 조회 금지, DTO Projection 필수

### TestFixture Pattern (MANDATORY)
**Persistence Layer에서는 TestFixture가 필수입니다**:

```java
// ✅ CORRECT (Use Fixture)
@Test
@DisplayName("주문 저장 - 정상 케이스")
void shouldSaveOrder() {
    // Given
    OrderJpaEntity entity = OrderJpaEntityFixture.create();

    // When
    OrderJpaEntity saved = orderJpaRepository.save(entity);

    // Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCustomerId()).isEqualTo(entity.getCustomerId());
}

// ❌ WRONG (Inline object creation)
@Test
void shouldSaveOrder() {
    OrderJpaEntity entity = new OrderJpaEntity(1L, 100L, 10, OrderStatus.PLACED);
    // ...
}
```

**Fixture 위치**: `persistence/src/testFixtures/java/{basePackage}/persistence/fixture/`

### Persistence Test Focus
- **Command Adapter 테스트** (저장/삭제):
  - JPA Repository save/delete 검증
  - Entity ↔ Domain 변환 (Mapper)
  - Transaction 동작 확인
- **Query Adapter 테스트** (조회):
  - QueryDSL DTO Projection
  - Dynamic Query 조건
  - Paging/Sorting
- **Entity 테스트**:
  - Constructor 패턴
  - Long FK 전략
  - Audit Entity 상속

## Core Principles

- Write ONE test at a time
- Make it run with minimum code
- Improve structure ONLY after green
- Run ALL tests after each change
- Never skip the Red phase
- Never mix structural and behavioral changes
- **ALWAYS use TestFixture** (Persistence Layer 필수!)

## Success Criteria

- ✅ Plan file updated (test marked as in-progress)
- ✅ Test written and initially failing (RED)
- ✅ Minimum code makes test pass (GREEN)
- ✅ Code structure improved if needed (REFACTOR)
- ✅ TestFixture used (NOT inline object creation)
- ✅ All tests passing
- ✅ Zero-Tolerance rules followed (Long FK, Lombok 금지, QueryDSL DTO Projection)
- ✅ Test marked complete in plan file

## What NOT to Do

- ❌ Don't work on Domain, Application, or REST API code
- ❌ Don't create tests without TestFixture
- ❌ Don't use JPA relationship annotations (`@ManyToOne`, `@OneToMany`, etc.)
- ❌ Don't use Lombok in JPA Entity
- ❌ Don't query Entity directly (use DTO Projection)

## Example Workflow

```bash
# 1. User: /kb-persistence /go
# 2. Claude: Reads docs/prd/plans/PROJ-123-persistence-plan.md
# 3. Claude: Finds next test: "주문 저장 Adapter - 정상 케이스"
# 4. Claude: Marks test as in-progress
# 5. Claude: RED - Writes failing test (uses OrderJpaEntityFixture)
# 6. Claude: GREEN - Implements SaveOrderAdapter
# 7. Claude: REFACTOR - Extracts Mapper logic
# 8. Claude: TIDY - Ensures TestFixture is used properly
# 9. Claude: Runs all tests (./gradlew test)
# 10. Claude: Marks test as complete
```

## Long FK 전략 Example

```java
// ✅ CORRECT (Long FK 전략)
@Entity
@Table(name = "orders")
public class OrderJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;  // ✅ Long FK (관계 어노테이션 없음)

    @Column(name = "product_id", nullable = false)
    private Long productId;   // ✅ Long FK

    // ...
}

// ❌ WRONG (JPA 관계 어노테이션)
@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // ❌ 절대 금지!
    @JoinColumn(name = "customer_id")
    private CustomerJpaEntity customer;

    // ...
}
```

## QueryDSL DTO Projection Example

```java
// ✅ CORRECT (DTO Projection)
@Repository
@RequiredArgsConstructor
public class OrderQueryAdapter implements LoadOrderPort {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<OrderDomain> loadById(String orderId) {
        OrderDto dto = queryFactory
            .select(Projections.constructor(
                OrderDto.class,
                order.id,
                order.customerId,
                order.productId,
                order.quantity,
                order.status
            ))
            .from(order)
            .where(order.orderId.eq(orderId))
            .fetchOne();

        return Optional.ofNullable(dto)
            .map(OrderEntityMapper::toDomain);
    }
}

// ❌ WRONG (Entity 직접 조회)
@Override
public Optional<OrderDomain> loadById(String orderId) {
    OrderJpaEntity entity = queryFactory
        .selectFrom(order)  // ❌ Entity 직접 조회
        .where(order.orderId.eq(orderId))
        .fetchOne();

    return Optional.ofNullable(entity)
        .map(OrderEntityMapper::toDomain);
}
```

Follow the workflow from CLAUDE.md precisely. Stop and report if any step fails.
