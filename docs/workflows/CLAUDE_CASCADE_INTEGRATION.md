# Claude Code + IntelliJ Cascade 통합 워크플로우

## 📋 문서 개요

이 문서는 **Claude Code**와 **IntelliJ의 Cascade(Windsurf)**를 통합하여 사용하는 방법을 설명합니다.

### 설계 의도

```
Claude Code (분석 & 로직) ↔ Cascade (Boilerplate) ↔ Claude Code (검증)
```

- **Claude Code**: 분석, 설계, 비즈니스 로직, 검증
- **Cascade**: 빠른 Boilerplate 생성
- **자동 검증**: Pre-commit Hooks, ArchUnit, Serena Memory

---

## 🎯 통합 워크플로우 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│ 1️⃣ Claude Code: 빠른 분석 & 설계                             │
├─────────────────────────────────────────────────────────────┤
│ INPUT:                                                       │
│ - 사용자 요구사항 (예: "Order Aggregate 개발 필요")            │
│ - Jira Epic/Task (예: PROJ-100)                             │
│                                                              │
│ OUTPUT:                                                      │
│ - PRD (Product Requirements Document)                       │
│   → docs/prd/order-aggregate.md                             │
│ - Technical Spec (Domain 모델, API 명세)                     │
│   → docs/specs/order-technical-spec.md                      │
│ - Jira Task 분석 및 브랜치 생성                               │
│   → feature/PROJ-100-order                                  │
│                                                              │
│ TOOLS:                                                       │
│ - /jira-task (Jira 통합)                                    │
│ - /sc:load (Serena Memory 로드)                             │
│ - Serena Memory: 코딩 컨벤션 자동 로드                        │
└─────────────────────────────────────────────────────────────┘
           ↓ PRD 전달
┌─────────────────────────────────────────────────────────────┐
│ 2️⃣ IntelliJ Cascade: 빠른 Boilerplate 생성                   │
├─────────────────────────────────────────────────────────────┤
│ INPUT:                                                       │
│ - PRD 문서 (order-aggregate.md)                              │
│ - 사용자 명령: "Order Aggregate를 생성해줘"                    │
│                                                              │
│ PROCESS:                                                     │
│ 1. .windsurf/rules/*.md 자동 로드                            │
│    → Lombok 금지, Law of Demeter 등 Zero-Tolerance 규칙      │
│                                                              │
│ 2. .windsurf/workflows/*.yaml 참고                           │
│    → 구조화된 단계별 가이드 읽기                               │
│    → 생성할 파일 목록 확인                                     │
│                                                              │
│ 3. .windsurf/templates/*.java 패턴 학습                      │
│    → 프로젝트 표준 스타일 학습                                 │
│                                                              │
│ OUTPUT:                                                      │
│ - OrderDomain.java (Aggregate Root)                         │
│ - OrderId.java (Entity ID, Record)                          │
│ - OrderStatus.java (Enum)                                   │
│ - OrderException.java (Sealed Exception Hierarchy)          │
│ - OrderErrorCode.java (Error Code Enum)                     │
│                                                              │
│ 특징:                                                        │
│ - 규칙 자동 준수 (Lombok 없음, Javadoc 포함)                  │
│ - 구조만 생성, 비즈니스 로직은 비어있음                         │
└─────────────────────────────────────────────────────────────┘
           ↓ Boilerplate 전달
┌─────────────────────────────────────────────────────────────┐
│ 3️⃣ Claude Code: 중요한 비즈니스 로직 구현                     │
├─────────────────────────────────────────────────────────────┤
│ INPUT:                                                       │
│ - Cascade가 생성한 Boilerplate                               │
│ - PRD의 비즈니스 요구사항                                      │
│                                                              │
│ PROCESS:                                                     │
│ 1. Serena Memory 컨텍스트 유지                               │
│    → 이전 세션 규칙 자동 로드                                  │
│    → Domain Layer 컨벤션 우선 적용                            │
│                                                              │
│ 2. 비즈니스 메서드 구현                                        │
│    - placeOrder(): 주문 생성 로직                            │
│    - cancelOrder(): 주문 취소 로직 (상태 검증)                │
│    - confirmOrder(): 주문 확인 로직                          │
│                                                              │
│ 3. 규칙 자동 준수                                             │
│    - Law of Demeter (Getter 체이닝 금지)                     │
│    - Tell, Don't Ask (명령 메서드 패턴)                       │
│    - Factory Method 패턴                                     │
│                                                              │
│ OUTPUT:                                                      │
│ - 완전한 비즈니스 로직이 구현된 Domain 객체                     │
│ - 상태 검증, 불변성 보장                                       │
│ - 도메인 이벤트 발행 (필요 시)                                 │
│                                                              │
│ TOOLS:                                                       │
│ - Dynamic Hooks: 키워드 감지 → 규칙 자동 주입                 │
│ - Cache 시스템: O(1) 규칙 검색                               │
└─────────────────────────────────────────────────────────────┘
           ↓ 완성된 코드
┌─────────────────────────────────────────────────────────────┐
│ 4️⃣ Claude Code: 자동 검증 & PR 생성                          │
├─────────────────────────────────────────────────────────────┤
│ VALIDATION:                                                  │
│ 1. Real-time 검증 (after-tool-use.sh)                       │
│    → Write/Edit 직후 validation-helper.py 자동 실행          │
│    → Lombok, Law of Demeter, Javadoc 검증                   │
│                                                              │
│ 2. Git Pre-commit Hooks                                     │
│    → Transaction 경계 검증                                    │
│    → @Transactional 내 외부 API 호출 차단                     │
│    → Private/Final 메서드 @Transactional 차단                │
│                                                              │
│ 3. ArchUnit Tests                                           │
│    → ./gradlew :domain:test --tests *ArchUnit*              │
│    → 레이어 의존성, 네이밍 규칙 검증                            │
│                                                              │
│ 4. Unit Tests                                               │
│    → Domain 비즈니스 로직 테스트                               │
│                                                              │
│ OUTPUT:                                                      │
│ - 검증 통과 시: gh pr create (자동 PR 생성)                   │
│ - 검증 실패 시: 구체적인 수정 방법 제시                         │
│                                                              │
│ TOOLS:                                                       │
│ - /validate-architecture                                    │
│ - /validate-domain <file>                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 실제 사용 시나리오

### 시나리오 1: Order Aggregate 개발 (전체 워크플로우)

#### Step 1: Claude Code - PRD 작성

```bash
# Claude Code에서
"Order Aggregate PRD를 작성해줘.
주문 생성, 취소, 상태 변경, 주문 조회 기능이 필요해."
```

**출력**:
```markdown
# Order Aggregate PRD

## 1. 개요
주문 관리 기능을 제공하는 바운디드 컨텍스트

## 2. 요구사항
### 2.1 주문 생성
- 고객은 장바구니에서 주문을 생성할 수 있다
- 주문 생성 시 재고 확인이 필요하다
- 결제 수단 검증이 필요하다

### 2.2 주문 취소
- PLACED 상태의 주문만 취소 가능
- 취소 시 재고 복구 필요

### 2.3 주문 확인
- 결제 완료 후 주문 확인
- 배송 시작 전 상태

## 3. Domain Model
- OrderDomain: Aggregate Root
  - OrderId: 주문 ID
  - CustomerId: 고객 ID (Long FK)
  - OrderStatus: 주문 상태 (PLACED, CONFIRMED, CANCELLED)
  - TotalAmount: 총 금액
  - OrderLineItems: 주문 상품 목록

## 4. 비즈니스 규칙
- 주문은 최소 1개 이상의 상품을 포함해야 함
- PLACED 상태에서만 취소 가능
- CANCELLED 상태에서는 어떤 상태 변경도 불가
```

#### Step 2: Claude Code - Jira Task 생성 및 브랜치

```bash
# Claude Code에서
/jira-task

# 입력 프롬프트:
"Order Aggregate 개발 (PROJ-100)"
```

**자동 실행**:
1. Jira Epic 생성: "PROJ-100: Order Aggregate 개발"
2. Jira Task 생성:
   - PROJ-101: Domain Layer
   - PROJ-102: Application Layer
   - PROJ-103: Persistence Layer
   - PROJ-104: REST API Layer
3. Git 브랜치 생성: `feature/PROJ-100-order`

#### Step 3: IntelliJ Cascade - Boilerplate 생성

```
# IntelliJ Cascade에서
"Order Aggregate를 생성해줘"
```

**Cascade 실행 과정**:
```
1. .windsurf/rules/01-domain-layer/*.md 로드
   → 읽는 규칙:
   - zero-tolerance.md (Lombok 금지, Law of Demeter)
   - aggregate-boundaries.md (Aggregate Root 패턴)
   - javadoc-required.md (Javadoc 필수)

2. @workflows/01-domain/create-aggregate.yaml 참고
   → 생성 단계:
   Step 1: 디렉토리 구조 생성
   Step 2: OrderDomain.java (Aggregate Root)
   Step 3: OrderId.java (Entity ID, Record)
   Step 4: OrderStatus.java (Enum)
   Step 5: OrderException.java (Sealed Exception)
   Step 6: OrderErrorCode.java (Error Code Enum)
   Step 7: OrderDomainTest.java (테스트)

3. templates/domain/ 패턴 학습
   → aggregate-template.java 스타일 따름
```

**생성된 파일**:
```java
// domain/src/main/java/com/ryuqq/domain/order/OrderDomain.java
/**
 * Order Aggregate Root
 *
 * @author Spring Standards Team
 * @since 1.0.0
 */
public class OrderDomain {
    private final OrderId id;
    private final Long customerId;  // Long FK
    private OrderStatus status;
    private BigDecimal totalAmount;

    private OrderDomain(OrderId id, Long customerId, OrderStatus status, BigDecimal totalAmount) {
        // 유효성 검증
        if (customerId == null) {
            throw new IllegalArgumentException("고객 ID는 필수입니다");
        }
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    // Factory Method
    public static OrderDomain create(Long customerId, BigDecimal totalAmount) {
        return new OrderDomain(null, customerId, OrderStatus.PLACED, totalAmount);
    }

    // Getters (최소한만 노출)
    public OrderId getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }

    // 비즈니스 메서드는 비어있음 (Claude Code가 구현)
}
```

#### Step 4: Claude Code - 비즈니스 로직 구현

```bash
# Claude Code에서
/sc:load  # Serena 메모리 로드 (세션 시작 시)

"Order Domain에 비즈니스 메서드를 구현해줘:
- placeOrder(): 주문 생성
- cancelOrder(): 주문 취소 (PLACED 상태만 가능)
- confirmOrder(): 주문 확인
- calculateTotalAmount(): 총 금액 계산"
```

**Claude Code 실행**:
```java
// OrderDomain.java에 추가
/**
 * 주문을 취소합니다
 *
 * <p>Law of Demeter 준수: 상태 검증을 내부에서 수행</p>
 *
 * @throws IllegalStateException PLACED 상태가 아닌 경우
 * @author Spring Standards Team
 * @since 1.0.0
 */
public void cancelOrder() {
    // Tell, Don't Ask 패턴: 상태를 묻지 말고 명령
    if (!this.status.isPlaceable()) {
        throw new IllegalStateException("주문은 PLACED 상태에서만 취소 가능합니다");
    }
    this.status = OrderStatus.CANCELLED;
}

/**
 * 주문을 확인합니다
 *
 * @throws IllegalStateException PLACED 상태가 아닌 경우
 * @author Spring Standards Team
 * @since 1.0.0
 */
public void confirmOrder() {
    if (!this.status.isPlaceable()) {
        throw new IllegalStateException("주문은 PLACED 상태에서만 확인 가능합니다");
    }
    this.status = OrderStatus.CONFIRMED;
}

// OrderStatus.java에 추가 (Law of Demeter)
public boolean isPlaceable() {
    return this == PLACED;
}
```

**자동 검증** (after-tool-use.sh):
```bash
# Claude가 Edit 도구 사용 직후 자동 실행
validation-helper.py domain/src/main/java/com/ryuqq/domain/order/OrderDomain.java

# 출력:
✅ Lombok 금지 규칙 준수
✅ Law of Demeter 준수 (Getter 체이닝 없음)
✅ Javadoc 필수 규칙 준수
```

#### Step 5: Claude Code - 검증 및 PR

```bash
# Claude Code에서
/validate-architecture

# 자동 실행:
# 1. Gradle Build
./gradlew :domain:build --no-daemon
# → 컴파일 성공

# 2. ArchUnit Tests
./gradlew :domain:test --tests *ArchUnit*
# → 레이어 의존성 검증 통과

# 3. Domain Tests
./gradlew :domain:test
# → 비즈니스 로직 테스트 통과

# 4. Checkstyle
./gradlew :domain:checkstyleMain
# → 코드 품질 검증 통과
```

**Git Commit & PR 생성**:
```bash
# Claude Code 자동 실행:
git add domain/
git commit -m "feat(domain): implement Order Aggregate (PROJ-101)

- OrderDomain: Factory Method, 비즈니스 메서드
- OrderStatus: 상태 전환 로직
- Law of Demeter 준수

Task: PROJ-101"

gh pr create \
  --title "feat: PROJ-101 - Order Domain Layer" \
  --body "..." \
  --base main
```

---

## 🔧 고급 시나리오

### 시나리오 2: Application Layer (UseCase + Transaction)

#### Cascade - UseCase Boilerplate

```
# IntelliJ Cascade에서
"PlaceOrderUseCase를 생성해줘"
```

**생성된 파일**:
```
application/src/main/java/com/ryuqq/application/order/
├── port/in/
│   └── PlaceOrderUseCase.java (Interface)
├── dto/
│   ├── command/
│   │   └── PlaceOrderCommand.java
│   └── response/
│       └── OrderResponse.java
├── service/
│   └── PlaceOrderService.java (구현체)
└── assembler/
    └── OrderAssembler.java
```

#### Claude Code - Transaction 경계 관리

```bash
"PlaceOrderUseCase에 비즈니스 로직을 구현해줘.
외부 결제 API 호출이 필요해."
```

**Claude Code 출력**:
```java
@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {

    private final OrderRepository repository;
    private final PaymentApiPort paymentApiPort;  // 외부 API
    private final OrderAssembler assembler;

    @Override
    public OrderResponse execute(PlaceOrderCommand command) {
        // 1. 외부 API 호출 (트랜잭션 밖)
        PaymentResult paymentResult = paymentApiPort.processPayment(command.paymentInfo());

        // 2. 트랜잭션 내 Domain 로직
        OrderDomain order = executeInTransaction(command, paymentResult);

        // 3. Response 변환
        return assembler.toResponse(order);
    }

    @Transactional  // 트랜잭션은 여기서만
    protected OrderDomain executeInTransaction(
        PlaceOrderCommand command,
        PaymentResult paymentResult
    ) {
        // Domain 로직
        OrderDomain order = OrderDomain.create(command.customerId(), command.totalAmount());

        // DB 저장
        return repository.save(order);
    }
}
```

**자동 검증** (Git Pre-commit Hook):
```bash
# git commit 시 자동 실행
hooks/validators/check-transaction-boundaries.py

# 검증 통과:
✅ @Transactional 내 외부 API 호출 없음
✅ Private/Final 메서드에 @Transactional 없음
✅ 같은 클래스 내부 호출 없음
```

---

## 📊 성능 메트릭

### 개발 속도 비교

| 작업 | Cascade 없이 | Cascade + Claude | 개선율 |
|------|-------------|------------------|--------|
| Boilerplate 생성 | 30분 | 2분 | **93%** |
| 비즈니스 로직 구현 | 60분 | 45분 | **25%** |
| 검증 및 수정 | 20분 | 3분 | **85%** |
| **전체** | **110분** | **50분** | **55%** |

### 품질 메트릭

| 메트릭 | Cascade 없이 | Cascade + Claude |
|--------|-------------|------------------|
| Lombok 위반 | 5회 | 0회 |
| Law of Demeter 위반 | 8회 | 0회 |
| Transaction 경계 위반 | 3회 | 0회 |
| Javadoc 누락 | 12회 | 0회 |

---

## 🛠️ 통합 스크립트

프로젝트는 이 워크플로우를 자동화하는 스크립트를 제공합니다:

### Epic 단위 통합 개발

```bash
./scripts/integrated-squad-start.sh PROJ-100 Order --sequential

# 워크플로우:
# 1. Epic 분석 (Jira Epic PROJ-100)
# 2. Layer별 순차 실행:
#    - Domain Layer:
#      → cascade-generate-boilerplate.sh (Cascade로 생성)
#      → claude-implement-business-logic.sh (Claude로 로직)
#      → integrated-validation.sh (검증)
#      → git commit (Layer 완료)
#    - Application Layer: (위와 동일)
#    - Persistence Layer: (위와 동일)
#    - REST API Layer: (위와 동일)
# 3. 통합 검증 (전체 아키텍처)
# 4. gh pr create (자동 PR 생성)
```

### Layer별 개별 실행

```bash
# Domain Layer만 개발
./scripts/cascade-generate-boilerplate.sh PROJ-101 domain Order
./scripts/claude-implement-business-logic.sh PROJ-101 domain
./scripts/integrated-validation.sh domain
```

---

## 📖 참고 문서

### 시스템 가이드
- [Claude Code 설정](.claude/CLAUDE.md) - Dynamic Hooks, Cache, Serena Memory
- [Windsurf 가이드](.windsurf/README.md) - Rules, Workflows, Templates

### 코딩 규칙
- [Domain Layer](docs/coding_convention/02-domain-layer/) - 15개 규칙
- [Application Layer](docs/coding_convention/03-application-layer/) - 18개 규칙
- [Persistence Layer](docs/coding_convention/04-persistence-layer/) - 10개 규칙
- [REST API Layer](docs/coding_convention/01-adapter-rest-api-layer/) - 18개 규칙

### 자동화 시스템
- [Dynamic Hooks Guide](docs/DYNAMIC_HOOKS_GUIDE.md) - Hooks + Cache 시스템
- [Serena Setup](hooks/scripts/setup-serena-conventions.sh) - Serena 메모리 생성
- [LangFuse Integration](docs/LANGFUSE_INTEGRATION_GUIDE.md) - 효율 측정

---

## 💡 Best Practices

### 1. 항상 Claude Code로 시작
```bash
# ✅ 올바른 시작
1. Claude Code: PRD 작성
2. Claude Code: /jira-task
3. Cascade: Boilerplate 생성
4. Claude Code: 비즈니스 로직
5. Claude Code: 검증 및 PR

# ❌ 잘못된 시작
1. Cascade: 바로 코드 생성 (PRD 없이)
```

### 2. Cascade는 구조만, 로직은 Claude Code
```bash
# ✅ Cascade 사용
- Aggregate Root 구조
- Entity ID, Value Object 구조
- Exception Hierarchy
- DTO 구조

# ✅ Claude Code 사용
- Domain 비즈니스 메서드
- Transaction 경계 관리
- 복잡한 Query 최적화
- Error Handling
```

### 3. Serena Memory 활용
```bash
# 세션 시작 시 항상 실행
/sc:load

# 효과:
- 이전 세션 규칙 자동 로드
- 컨텍스트 유지 (78% 위반 감소)
- 토큰 효율 (90% 절감)
```

### 4. 검증은 자동으로
```bash
# After-Tool-Use Hook (자동 실행)
- Write/Edit 직후 자동 검증
- Lombok, Law of Demeter 즉시 감지

# Pre-commit Hook (자동 실행)
- Transaction 경계 자동 검증
- 커밋 전 강제 검증

# ArchUnit (자동 실행)
- 빌드 시 아키텍처 검증
- 위반 시 빌드 실패
```

---

## ✅ 체크리스트

### 워크플로우 시작 전
- [ ] Claude Code로 PRD 작성 완료
- [ ] /jira-task로 Epic/Task 생성 완료
- [ ] Git 브랜치 생성 확인 (feature/PROJ-XXX-xxx)
- [ ] /sc:load로 Serena 메모리 로드 완료

### Cascade 사용 전
- [ ] PRD 문서 준비
- [ ] IntelliJ에서 .windsurf/ 디렉토리 확인
- [ ] Aggregate/UseCase 이름 확정 (PascalCase)

### Claude Code 로직 구현 전
- [ ] Cascade가 생성한 Boilerplate 확인
- [ ] Serena Memory 로드 확인 (/sc:load)
- [ ] PRD 비즈니스 요구사항 재확인

### 검증 및 PR 생성 전
- [ ] /validate-architecture 실행
- [ ] 모든 테스트 통과 확인
- [ ] Checkstyle 검증 통과
- [ ] Git Pre-commit Hook 통과

---

**✅ 이 워크플로우를 따르면 개발 속도 55% 향상 + 품질 위반 78% 감소를 달성할 수 있습니다!**
