# Domain Layer TDD Go - Execute Next Test from Plan

You are executing the Kent Beck TDD workflow for **Domain Layer**.

## Instructions

1. **Read plan file** from `docs/prd/plans/{ISSUE-KEY}-domain-plan.md`
2. **Find the next unmarked test** in the Domain Layer section
3. **Mark the test as in-progress** by adding a checkbox or marker
4. **Execute the TDD Cycle**:
   - **RED**: Write the simplest failing test first
   - **GREEN**: Implement minimum code to make the test pass
   - **REFACTOR**: Improve structure only after tests pass
   - **TIDY**: Clean up tests using TestFixture pattern
5. **Run all tests** (excluding long-running tests)
6. **Verify** all tests pass before proceeding
7. **Mark test complete** in plan file

## Domain Layer Specific Rules

### Zero-Tolerance Rules (MUST follow)
- ✅ **Lombok 금지**: Pure Java 또는 Record 패턴 사용
- ✅ **Law of Demeter**: Getter 체이닝 금지 (`order.getCustomer().getAddress()` ❌)
- ✅ **Tell, Don't Ask**: 비즈니스 로직은 Domain 내부에 캡슐화
- ✅ **Long FK 전략**: JPA 관계 어노테이션 금지 (`private Long customerId;` 사용)

### TestFixture Pattern (MANDATORY)
**Domain Layer에서는 TestFixture가 필수입니다**:

```java
// ✅ CORRECT (Use Fixture)
@Test
@DisplayName("주문 취소 - PLACED 상태만 가능")
void shouldCancelOrderWhenPlaced() {
    // Given
    OrderDomain order = OrderDomainFixture.create();

    // When
    order.cancel(CancelReason.CUSTOMER_REQUEST);

    // Then
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
}

// ❌ WRONG (Inline object creation)
@Test
void shouldCancelOrder() {
    OrderDomain order = OrderDomain.create(1L, 2L, 3L, 10, OrderStatus.PLACED, ...);
    // ...
}
```

**Fixture 위치**: `domain/src/testFixtures/java/{basePackage}/domain/fixture/`

### Domain Test Focus
- **Aggregate 비즈니스 로직 테스트**:
  - State transition (상태 전환)
  - Invariant validation (불변식 검증)
  - Business rule enforcement (비즈니스 규칙 강제)
- **Value Object 테스트**:
  - Immutability (불변성)
  - Validation (검증 로직)
  - Equality (동등성)

## Core Principles

- Write ONE test at a time
- Make it run with minimum code
- Improve structure ONLY after green
- Run ALL tests after each change
- Never skip the Red phase
- Never mix structural and behavioral changes
- **ALWAYS use TestFixture** (Domain Layer 필수!)

## Success Criteria

- ✅ Plan file updated (test marked as in-progress)
- ✅ Test written and initially failing (RED)
- ✅ Minimum code makes test pass (GREEN)
- ✅ Code structure improved if needed (REFACTOR)
- ✅ TestFixture used (NOT inline object creation)
- ✅ All tests passing
- ✅ Zero-Tolerance rules followed (Lombok 금지, Law of Demeter, Long FK 전략)
- ✅ Test marked complete in plan file

## What NOT to Do

- ❌ Don't work on Application, Persistence, or REST API code
- ❌ Don't create tests without TestFixture
- ❌ Don't use Lombok (`@Data`, `@Getter`, `@Setter`, etc.)
- ❌ Don't use Getter chaining (`order.getCustomer().getAddress()`)
- ❌ Don't use JPA relationship annotations (`@ManyToOne`, `@OneToMany`, etc.)

## Example Workflow

```bash
# 1. User: /kb-domain /go
# 2. Claude: Reads docs/prd/plans/PROJ-123-domain-plan.md
# 3. Claude: Finds next test: "주문 취소 - PLACED 상태만 가능"
# 4. Claude: Marks test as in-progress
# 5. Claude: RED - Writes failing test (uses OrderDomainFixture)
# 6. Claude: GREEN - Implements Order.cancel() method
# 7. Claude: REFACTOR - Extracts CancelReason validation logic
# 8. Claude: TIDY - Ensures TestFixture is used properly
# 9. Claude: Runs all tests (./gradlew test)
# 10. Claude: Marks test as complete
```

Follow the workflow from CLAUDE.md precisely. Stop and report if any step fails.
