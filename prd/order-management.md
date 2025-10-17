# Order Management Feature - Product Requirements Document (PRD)

## 📋 Overview

주문 생성, 조회, 상태 관리를 위한 핵심 Domain Aggregate 구현

**목표**: 헥사고날 아키텍처와 DDD 원칙을 준수하는 Order Domain 구현

---

## 🎯 Business Requirements

### Core Functionality

1. **주문 생성** - 고객이 새로운 주문을 생성할 수 있어야 함
2. **주문 조회** - 주문 ID로 주문 정보를 조회할 수 있어야 함
3. **주문 확정** - PENDING 상태의 주문을 CONFIRMED로 변경
4. **주문 취소** - 주문을 CANCELLED 상태로 변경

### Business Rules

- 주문 ID는 시스템이 자동 생성 (예: ORD-20251017-001)
- 주문 금액은 0보다 커야 함
- PENDING 상태에서만 확정 가능
- CONFIRMED 상태에서는 취소 불가능

---

## 🏗️ Technical Requirements

### Domain Layer

#### 1. Order Aggregate Root

**파일**: `domain/src/main/java/com/company/template/order/domain/model/Order.java`

**필수 속성**:
```java
- OrderId orderId          // Value Object (record)
- CustomerId customerId    // Value Object (record)
- Money amount            // Value Object (record)
- OrderStatus status      // Enum
- LocalDateTime createdAt
```

**필수 메서드**:
```java
+ static Order create(OrderId, CustomerId, Money): Order
+ void confirm(): void
+ void cancel(): void
+ boolean isConfirmable(): boolean
+ boolean isCancellable(): boolean
```

**Invariants (불변 조건)**:
- 주문 금액은 항상 0보다 커야 함
- 상태 전이는 비즈니스 규칙을 따라야 함

#### 2. Value Objects

**OrderId**:
```java
public record OrderId(String value) {
    public OrderId {
        Objects.requireNonNull(value, "OrderId cannot be null");
        if (!value.matches("^ORD-\\d{8}-\\d{3}$")) {
            throw new IllegalArgumentException("Invalid OrderId format");
        }
    }
}
```

**CustomerId**:
```java
public record CustomerId(Long value) {
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("CustomerId must be positive");
        }
    }
}
```

**Money**:
```java
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public Money add(Money other) { /* ... */ }
    public Money subtract(Money other) { /* ... */ }
}
```

#### 3. Enums

**OrderStatus**:
```java
public enum OrderStatus {
    PENDING,     // 주문 생성 상태
    CONFIRMED,   // 주문 확정 상태
    CANCELLED    // 주문 취소 상태
}
```

### Application Layer

#### PlaceOrderUseCase

**파일**: `application/src/main/java/com/company/template/order/application/usecase/PlaceOrderUseCase.java`

**Input DTO** (record):
```java
public record PlaceOrderCommand(
    Long customerId,
    BigDecimal amount,
    String currency
) {
    public PlaceOrderCommand {
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");
    }
}
```

**Output DTO** (record):
```java
public record PlaceOrderResult(
    String orderId,
    String status,
    BigDecimal amount,
    String currency
) {}
```

**Transaction Boundary**:
```java
@Transactional
public PlaceOrderResult execute(PlaceOrderCommand command) {
    // 1. Domain 객체 생성
    // 2. Repository 저장
    // 3. Result 반환
}
```

### Adapter Layer (REST API)

#### OrderController

**파일**: `adapter/adapter-in-admin-web/src/main/java/.../OrderController.java`

**Endpoints**:

**POST /api/v1/orders**
```json
Request:
{
  "customerId": 12345,
  "amount": 50000,
  "currency": "KRW"
}

Response: 201 Created
{
  "orderId": "ORD-20251017-001",
  "status": "PENDING",
  "amount": 50000,
  "currency": "KRW"
}
```

**GET /api/v1/orders/{orderId}**
```json
Response: 200 OK
{
  "orderId": "ORD-20251017-001",
  "customerId": 12345,
  "status": "PENDING",
  "amount": 50000,
  "currency": "KRW",
  "createdAt": "2025-10-17T10:30:00"
}
```

---

## ✅ Acceptance Criteria

### Domain Layer
- [ ] Order Aggregate는 Lombok을 사용하지 않음
- [ ] Law of Demeter 준수 (Getter 체이닝 금지)
- [ ] 모든 public 클래스/메서드에 Javadoc 존재 (@author, @since 포함)
- [ ] Spring/JPA 의존성 없음 (순수 Java)

### Application Layer
- [ ] UseCase는 `@Transactional` 사용
- [ ] 트랜잭션 내부에 외부 API 호출 없음
- [ ] DTO는 record 패턴 사용
- [ ] Assembler 패턴으로 Domain ↔ DTO 변환

### Adapter Layer
- [ ] Controller는 `@RestController` 사용
- [ ] Request DTO는 `@Valid` 검증 적용
- [ ] HTTP 상태 코드 표준 (201 Created, 200 OK)
- [ ] 응답에 Domain 객체 직접 반환 금지

### Testing
- [ ] Domain Layer 테스트 커버리지 90% 이상
- [ ] Application Layer 테스트 커버리지 80% 이상
- [ ] Adapter Layer 테스트 커버리지 70% 이상

---

## 📐 Architecture Diagram

```
┌─────────────────────────────────────────┐
│         REST API Layer                  │
│  OrderController                        │
│  - POST /api/v1/orders                  │
│  - GET /api/v1/orders/{id}              │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Application Layer                  │
│  PlaceOrderUseCase (@Transactional)     │
│  - PlaceOrderCommand (Input DTO)        │
│  - PlaceOrderResult (Output DTO)        │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         Domain Layer                    │
│  Order (Aggregate Root)                 │
│  - OrderId (Value Object)               │
│  - CustomerId (Value Object)            │
│  - Money (Value Object)                 │
│  - OrderStatus (Enum)                   │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Persistence Layer                  │
│  OrderJpaRepository                     │
│  OrderJpaEntity                         │
└─────────────────────────────────────────┘
```

---

## 🔍 Code Generation Guidelines

### Zero-Tolerance Rules

이 PRD를 기반으로 코드를 생성할 때 **반드시** 준수해야 하는 규칙:

1. **Lombok 절대 금지** - Pure Java만 사용
2. **Law of Demeter** - `order.getCustomer().getName()` 같은 체이닝 금지
3. **Javadoc 필수** - 모든 public 클래스/메서드에 `@author`, `@since` 포함
4. **Transaction 경계** - `@Transactional` 내에서 외부 API 호출 금지
5. **Domain Purity** - Domain Layer에 Spring/JPA 의존성 금지

### Expected File Structure

```
domain/src/main/java/com/company/template/order/domain/model/
├── Order.java                 # Aggregate Root
├── OrderId.java              # Value Object (record)
├── CustomerId.java           # Value Object (record)
├── Money.java                # Value Object (record)
└── OrderStatus.java          # Enum

application/src/main/java/com/company/template/order/application/
├── usecase/
│   └── PlaceOrderUseCase.java
├── dto/
│   ├── PlaceOrderCommand.java (record)
│   └── PlaceOrderResult.java (record)
└── assembler/
    └── OrderAssembler.java

adapter/adapter-in-admin-web/src/main/java/.../order/adapter/in/web/
├── OrderController.java
├── dto/
│   ├── OrderCreateRequest.java
│   └── OrderResponse.java
└── mapper/
    └── OrderApiMapper.java
```

---

## 🎓 Usage Example

### With Claude Code

```bash
# Domain Layer 생성
/code-gen-domain Order prd/order-management.md

# Application Layer 생성
/code-gen-usecase PlaceOrder prd/order-management.md

# Adapter Layer 생성
/code-gen-controller Order prd/order-management.md
```

### 수동 검증

```bash
# Domain Layer 검증
python3 .claude/hooks/scripts/validation-helper.py \
  domain/src/main/java/.../Order.java domain

# 전체 아키텍처 검증
./gradlew :application:test --tests "*ArchitectureTest"
```

---

## 📝 Notes

- 이 PRD는 **실제 코드 생성 테스트**에 사용됩니다
- **일관성 검증**: 동일한 PRD로 3회 생성하여 결과물 비교
- **토큰 효율성**: Cache 시스템 vs Non-Cache 비교 측정

**마지막 업데이트**: 2025-10-17
