# Application Layer TDD Go - Execute Next Test from Plan

You are executing the Kent Beck TDD workflow for **Application Layer**.

## MUST DO BEFORE STARTING

### 1. Git Branch Check ⚠️
**CRITICAL**: Never work on main/master branch!

**Steps**:
1. Check current branch: `git branch`
2. If on main/master:
   - Extract Issue Key from plan file (e.g., MEMBER-001)
   - Create branch: `git checkout -b feature/{ISSUE-KEY}-application`
   - Example: `feature/MEMBER-001-application`
3. If branch doesn't exist: create it
4. If branch exists: checkout to it

### 2. Plan File Checklist Update 📝
**After completing each TDD phase**, update the plan file checklist items to `[x]`.

**When to Update**:
- **RED Phase**: After writing tests → Mark Red checklist as `[x]` → Commit
- **GREEN Phase**: After implementation → Mark Green checklist as `[x]` → Commit
- **REFACTOR Phase**: After refactoring → Mark Refactor checklist as `[x]` → Commit

## Instructions

1. **Check Git Branch** (see MUST DO section above):
   - Ensure NOT on main/master branch
   - Extract Issue Key from plan file (e.g., MEMBER-001)
   - If feature/{ISSUE-KEY}-application branch doesn't exist, create it
   - Checkout to feature branch before starting TDD

2. **Read plan file** from `docs/prd/plans/{ISSUE-KEY}-application-plan.md`

3. **Find the next unmarked test** in the Application Layer section

4. **Mark the test as in-progress** by adding "🔄 IN PROGRESS" marker

5. **Execute the TDD Cycle**:
   - **RED**: Write the simplest failing test first → **Update Red checklist items to [x]** → Commit
   - **GREEN**: Implement minimum code to make the test pass → **Update Green checklist items to [x]** → Commit
   - **REFACTOR**: Improve structure only after tests pass → **Update Refactor checklist items to [x]** → Commit
   - **TIDY**: Clean up tests using TestFixture pattern

6. **Run all tests** (excluding long-running tests)

7. **Verify** all tests pass before proceeding

8. **Mark test complete** in plan file (change "🔄 IN PROGRESS" to "✅ COMPLETED")

## Application Layer Specific Rules

### Zero-Tolerance Rules (MUST follow)
- ✅ **Transaction 경계**: `@Transactional` 내 외부 API 호출 절대 금지
- ✅ **Spring Proxy 제약**: Private/Final 메서드에 `@Transactional` 금지
- ✅ **CQRS 분리**: Command UseCase와 Query UseCase 명확히 분리
- ✅ **Assembler 사용**: UseCase 내부 DTO 변환은 Assembler에 위임
- ✅ **Port 명명 규칙**:
  - Command: `Save*Port`, `Delete*Port`
  - Query: `Load*Port`, `Find*Port`

### TestFixture Pattern (MANDATORY)
**Application Layer에서는 TestFixture가 필수입니다**:

```java
// ✅ CORRECT (Use Fixture)
@Test
@DisplayName("주문 생성 - 정상 케이스")
void shouldPlaceOrder() {
    // Given
    PlaceOrderCommand command = PlaceOrderCommandFixture.create();
    OrderDomain order = OrderDomainFixture.create();

    given(loadCustomerPort.loadById(command.customerId()))
        .willReturn(Optional.of(CustomerFixture.create()));
    given(saveOrderPort.save(any(OrderDomain.class)))
        .willReturn(order);

    // When
    OrderResponse response = placeOrderUseCase.execute(command);

    // Then
    assertThat(response.orderId()).isNotNull();
    assertThat(response.status()).isEqualTo(OrderStatus.PLACED);
}

// ❌ WRONG (Inline object creation)
@Test
void shouldPlaceOrder() {
    PlaceOrderCommand command = new PlaceOrderCommand(1L, 100L, 10);
    // ...
}
```

**Fixture 위치**: `application/src/testFixtures/java/{basePackage}/application/fixture/`

### Application Test Focus
- **UseCase 비즈니스 로직 테스트**:
  - Command UseCase: 트랜잭션 내부 로직 검증
  - Query UseCase: 조회 로직 및 DTO 변환 검증
  - Validation: 입력 검증 로직
- **Assembler 테스트**:
  - Command → Domain 변환
  - Domain → Response 변환
  - DTO 불변성 검증
- **Port Mocking**:
  - Outbound Port Mock 사용
  - Interaction 검증 (verify)

## Core Principles

- Write ONE test at a time
- Make it run with minimum code
- Improve structure ONLY after green
- Run ALL tests after each change
- Never skip the Red phase
- Never mix structural and behavioral changes
- **ALWAYS use TestFixture** (Application Layer 필수!)

## Success Criteria

- ✅ Plan file updated (test marked as in-progress)
- ✅ Test written and initially failing (RED)
- ✅ Minimum code makes test pass (GREEN)
- ✅ Code structure improved if needed (REFACTOR)
- ✅ TestFixture used (NOT inline object creation)
- ✅ All tests passing
- ✅ Zero-Tolerance rules followed (Transaction 경계, Spring Proxy, CQRS)
- ✅ Test marked complete in plan file

## What NOT to Do

- ❌ Don't work on Domain, Persistence, or REST API code
- ❌ Don't create tests without TestFixture
- ❌ Don't call external APIs inside `@Transactional` methods
- ❌ Don't use `@Transactional` on private/final methods
- ❌ Don't mix Command and Query UseCase logic

## Example Workflow

```bash
# 1. User: /kb-application /go
# 2. Claude: Reads docs/prd/plans/PROJ-123-application-plan.md
# 3. Claude: Finds next test: "주문 생성 UseCase - 정상 케이스"
# 4. Claude: Marks test as in-progress
# 5. Claude: RED - Writes failing test (uses PlaceOrderCommandFixture)
# 6. Claude: GREEN - Implements PlaceOrderUseCase.execute()
# 7. Claude: REFACTOR - Extracts validation logic to separate method
# 8. Claude: TIDY - Ensures TestFixture is used properly
# 9. Claude: Runs all tests (./gradlew test)
# 10. Claude: Marks test as complete
```

## Transaction Boundary Example

```java
// ✅ CORRECT (Transaction 경계 관리)
@UseCase
@RequiredArgsConstructor
public class PlaceOrderUseCase implements PlaceOrderPort {

    private final LoadCustomerPort loadCustomerPort;
    private final SaveOrderPort saveOrderPort;
    private final PaymentClient paymentClient;  // External API

    @Override
    @Transactional
    public OrderResponse execute(PlaceOrderCommand command) {
        // 1. 트랜잭션 내부 로직
        CustomerDomain customer = loadCustomerPort.loadById(command.customerId())
            .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));

        OrderDomain order = OrderDomain.create(
            OrderId.generate(),
            command.customerId(),
            command.productId(),
            command.quantity()
        );

        OrderDomain savedOrder = saveOrderPort.save(order);

        // 2. 트랜잭션 외부로 위임 (외부 API 호출)
        return executeExternalOperations(savedOrder);
    }

    // 트랜잭션 밖에서 외부 API 호출
    private OrderResponse executeExternalOperations(OrderDomain order) {
        // 외부 결제 API 호출
        PaymentResult paymentResult = paymentClient.requestPayment(
            order.getOrderId(),
            order.getTotalPrice()
        );

        if (!paymentResult.isSuccess()) {
            throw new PaymentFailedException(paymentResult.getReason());
        }

        return OrderAssembler.toResponse(order);
    }
}
```

Follow the workflow from CLAUDE.md precisely. Stop and report if any step fails.
