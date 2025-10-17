# Claude Spring Standards

> **AI 기반 엔터프라이즈 Spring Boot 템플릿**
> Spring Boot 3.5.x + Java 21 기반 헥사고날 아키텍처 템플릿 with Dynamic Hooks + Cache System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue.svg)](https://alistair.cockburn.us/hexagonal-architecture/)

---

## 🎯 무엇이 다른가?

이 프로젝트는 단순한 Spring Boot 템플릿이 아닙니다. **Dynamic Hooks + Cache System**을 통해 AI가 자동으로 코딩 규칙을 주입하고 검증하는 혁신적인 개발 환경을 제공합니다.

### 핵심 차별점

| 기능 | 기존 방식 | 이 프로젝트 |
|------|----------|------------|
| 코딩 표준 준수 | 수동 코드 리뷰 | **자동 규칙 주입 + 실시간 검증** |
| 문서 로딩 | 전체 문서 읽기 (50K+ 토큰) | **JSON Cache (500-1K 토큰, 90% 절감)** |
| 검증 속도 | 561ms (순차) | **148ms (병렬, 73.6% 향상)** |
| 컨텍스트 인식 | 없음 | **키워드 기반 Layer 자동 감지** |
| 코드 생성 | 일반 템플릿 | **Layer별 규칙 자동 적용** |

---

## 📖 목차

- [빠른 시작 (5분)](#-빠른-시작-5분)
- [아키텍처 개요](#-아키텍처-개요)
- [Dynamic Hooks 시스템](#-dynamic-hooks-시스템-우리의-혁신)
- [코딩 표준](#-코딩-표준)
- [개발 워크플로우](#-개발-워크플로우)
- [사용 가능한 명령어](#-사용-가능한-명령어)
- [테스팅](#-테스팅)
- [문제 해결](#-문제-해결)
- [기여하기](#-기여하기)

---

## 🚀 빠른 시작 (5분)

### 사전 요구사항

- **Java 21+**
- **Gradle 8.5+**
- **Python 3.8+** (Cache 시스템용)
- **Claude Code** (선택사항, AI 지원 개발용)

### 설치

```bash
# 1. 리포지토리 클론
git clone https://github.com/your-org/claude-spring-standards.git
cd claude-spring-standards

# 2. Rule Cache 빌드 (90개 문서 → JSON, 약 5초)
python3 .claude/hooks/scripts/build-rule-cache.py

# 3. Git Hooks 설정
ln -s ../../hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# 4. 빌드 & 테스트
./gradlew build

# 5. 애플리케이션 실행
./gradlew :bootstrap:bootstrap-web-api:bootRun
```

**✅ 완료!** 애플리케이션이 `http://localhost:8080`에서 실행됩니다.

### 첫 번째 코드 생성

```bash
# Claude Code 사용 (권장)
claude code

# Claude Code에서:
> "Create an Order aggregate in the domain layer"
```

**자동으로 수행되는 작업:**
1. **user-prompt-submit.sh**가 "aggregate" 키워드 감지 → 도메인 규칙 주입
2. Claude가 Zero-Tolerance 규칙이 적용된 `Order.java` 생성
3. **after-tool-use.sh**가 실시간 코드 검증
4. **검증 통과** → 커밋 준비 완료

---

## 🏗 아키텍처 개요

### 헥사고날 아키텍처 (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────┐
│                    Bootstrap Layer                       │
│              (Web API, Batch, CLI, etc.)                │
└──────────────────────┬──────────────────────────────────┘
                       │
       ┌───────────────┴───────────────┐
       │                               │
┌──────▼──────┐                 ┌──────▼──────┐
│   Adapter   │                 │   Adapter   │
│   (REST)    │◄────────────────┤(Persistence)│
│   In-Port   │                 │  Out-Port   │
└──────┬──────┘                 └──────┬──────┘
       │                               │
       │        Application Layer      │
       │    (Use Cases, Ports)         │
       │                               │
       └───────────────┬───────────────┘
                       │
            ┌──────────▼──────────┐
            │   Domain Layer      │
            │  (Pure Business     │
            │   Logic)            │
            └─────────────────────┘
```

### 프로젝트 구조

```
claude-spring-standards/
├── .claude/                          # 🚀 Dynamic Hooks System (핵심 혁신)
│   ├── cache/
│   │   └── rules/                    # 90개 JSON 규칙 (90% 토큰 절감)
│   │       ├── index.json            # O(1) 키워드 조회
│   │       └── *.json                # Layer별 규칙
│   ├── hooks/
│   │   ├── user-prompt-submit.sh     # 생성 전 자동 규칙 주입
│   │   ├── after-tool-use.sh         # 생성 후 실시간 검증
│   │   └── scripts/
│   │       ├── build-rule-cache.py   # Cache 빌더
│   │       ├── inject-rules.py       # 규칙 주입 엔진
│   │       └── validation-helper.py  # 검증 엔진
│   └── commands/                     # Slash 명령어
│       ├── code-gen-domain.md        # /code-gen-domain
│       ├── code-gen-usecase.md       # /code-gen-usecase
│       └── code-gen-controller.md    # /code-gen-controller
│
├── docs/
│   ├── coding_convention/            # 90개 코딩 규칙 (8개 레이어)
│   │   ├── 01-adapter-rest-api-layer/
│   │   ├── 02-domain-layer/
│   │   ├── 03-application-layer/
│   │   ├── 04-persistence-layer/
│   │   ├── 05-testing/
│   │   ├── 06-java21-patterns/
│   │   ├── 07-enterprise-patterns/
│   │   └── 08-error-handling/
│   ├── DYNAMIC_HOOKS_GUIDE.md        # 포괄적인 시스템 가이드
│   └── tutorials/                    # 단계별 가이드
│
├── domain/                           # 순수 비즈니스 로직 (프레임워크 무관)
├── application/                      # Use Cases, Ports, Services
├── adapter/
│   ├── adapter-in-admin-web/         # REST Controllers
│   ├── adapter-out-persistence-jpa/  # JPA Repositories
│   └── adapter-out-aws-*/            # AWS 통합
├── bootstrap/
│   └── bootstrap-web-api/            # 실행 가능한 애플리케이션
│
├── config/                           # 품질 게이트
│   ├── checkstyle/
│   ├── pmd/                          # Law of Demeter, GodClass 규칙
│   └── spotbugs/
│
└── hooks/                            # Git pre-commit hooks
    └── validators/                   # Layer별 검증기
```

---

## 🔥 Dynamic Hooks 시스템 (우리의 혁신)

### 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│  Tier 1: 컨텍스트 분석 (<100ms)                         │
│  ────────────────────────────────────                   │
│  입력: "Create an Order aggregate"                      │
│  - 키워드 감지: "aggregate" → domain (+30 점)          │
│  - Layer 매핑: domain                                   │
│  - 우선순위 점수: Critical/High/Medium/Low              │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  Tier 2: 규칙 주입 (<50ms)                              │
│  ────────────────────────────────────                   │
│  - Cache 조회: index.json → O(1)                        │
│  - 로드: domain-layer-*.json (13개 규칙)                │
│  - 필터: Critical + High 우선순위                        │
│  - 주입: Claude 컨텍스트에 규칙 주입                     │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  Tier 3: 생성 + 검증 (<500ms)                           │
│  ──────────────────────────────────────────             │
│  - Claude: 규칙이 적용된 Order.java 생성                │
│  - after-tool-use.sh: 실시간 검증                       │
│  - 결과: ✅ 통과 또는 ⚠️ 실패 (수정 가이드 포함)        │
└─────────────────────────────────────────────────────────┘
```

### 작동 원리

#### 1. Cache 시스템 (90% 토큰 절감)

**Before** (기존 방식):
```
로드: docs/coding_convention/02-domain-layer/law-of-demeter/01_getter-chaining-prohibition.md
→ 2,150 토큰 (예제, 설명 포함 전체 마크다운)

로드: docs/coding_convention/02-domain-layer/aggregate-design/01_aggregate-boundaries.md
→ 3,250 토큰

총계: 90개 문서에 50,000+ 토큰
```

**After** (Cache 시스템):
```
로드: .claude/cache/rules/domain-layer-law-of-demeter-01_getter-chaining-prohibition.json
→ 215 토큰 (구조화된 규칙만)

로드: .claude/cache/rules/domain-layer-aggregate-design-01_aggregate-boundaries.json
→ 325 토큰

총계: 선택된 규칙에 500-1,000 토큰 (90% 절감)
```

**JSON Cache 구조**:
```json
{
  "id": "domain-layer-law-of-demeter-01_getter-chaining-prohibition",
  "metadata": {
    "layer": "domain",
    "priority": "critical",
    "keywords": {
      "primary": ["getter", "chaining", "prohibition"],
      "anti": ["order.getCustomer().getAddress()"]
    }
  },
  "rules": {
    "prohibited": [
      "❌ `order.getCustomer().getAddress().getZip()`",
      "❌ Getter 체이닝 (Law of Demeter 위반)"
    ],
    "required": [
      "✅ `order.isReadyForShipment()` (Tell, Don't Ask)",
      "✅ 메서드에 행위 캡슐화"
    ]
  }
}
```

#### 2. 키워드 기반 Layer 감지

**키워드 매핑 테이블**:

| 키워드 | Layer | 점수 | 예제 |
|---------|-------|-------|---------|
| aggregate, 애그리게이트 | domain | 30 | "Create Order aggregate" |
| controller, 컨트롤러 | adapter-rest | 30 | "Add OrderController" |
| usecase, service | application | 30 | "Implement PlaceOrderUseCase" |
| repository, jpa | adapter-persistence | 30 | "Create OrderRepository" |
| test, 테스트 | testing | 25 | "Write unit tests" |
| record, sealed | java21 | 20 | "Use Java 21 records" |

**예제 플로우**:
```
사용자 입력: "Create an Order aggregate with status management"

감지:
- "aggregate" → domain (+30)
- "order" → domain 컨텍스트 (+15)
- 컨텍스트 점수: 45 (임계값: 25)

액션:
- 전략: CACHE_BASED
- 로드: domain layer 규칙 (13개 JSON 파일)
- 주입: Critical + High 우선순위 규칙
- 생성: 규칙이 적용된 Order.java
```

#### 3. 실시간 검증

**코드 생성 후**:
```bash
# after-tool-use.sh 자동 트리거
→ 파일 감지: domain/src/main/java/.../Order.java
→ Layer 감지: domain (경로 패턴으로)
→ 실행: validation-helper.py Order.java domain
→ 확인: Critical 검증기
   ├─ ✅ Lombok 없음
   ├─ ✅ Javadoc @author 존재
   ├─ ✅ 순수 Java (Spring/JPA 없음)
   └─ ✅ Law of Demeter 준수
→ 결과: ✅ 통과
```

**검증 출력 예제**:

✅ **성공**:
```
---
✅ **검증 통과**

파일: `domain/src/main/java/.../Order.java`

모든 규칙을 준수합니다!
---
```

⚠️ **실패**:
```
---
⚠️ **검증 실패**

**파일**: `domain/src/main/java/.../Order.java`

**규칙 위반**: Lombok 사용 금지

**문제**: 금지된 어노테이션: @Data

**금지 사항**:
- ❌ @Data
- ❌ @Builder
- ❌ @Getter

**해결 방법**:
명시적 생성자와 getter가 있는 순수 Java를 사용하세요.

**참고**: docs/coding_convention/02-domain-layer/...

💡 코드를 수정한 후 다시 시도하세요.
---
```

### 성능 지표

| 지표 | Before | After | 개선율 |
|--------|--------|-------|-------------|
| 토큰 사용량 | 50,000 | 500-1,000 | **90% 절감** |
| 검증 속도 | 561ms | 148ms | **73.6% 빠름** |
| 문서 로딩 | 2-3초 | <100ms | **95% 빠름** |
| Cache 빌드 | N/A | 5초 | 1회성 비용 |
| 컨텍스트 인식 | 수동 | 자동 | **100% 커버리지** |

---

## 📚 코딩 표준

### Zero-Tolerance 규칙

이 규칙들은 **자동으로 강제되며** **위반할 수 없습니다**:

#### 1. **Lombok 금지** (Domain + Application + Adapter)

❌ **금지**:
```java
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class Order {
    private String id;
}
```

✅ **필수**:
```java
/**
 * Order Aggregate Root
 *
 * @author YourName
 * @since 2025-10-17
 */
public class Order {
    private final OrderId id;
    private final Money amount;

    private Order(OrderId id, Money amount) {
        this.id = id;
        this.amount = amount;
    }

    public static Order create(OrderId id, Money amount) {
        return new Order(id, amount);
    }

    public OrderId getId() {
        return this.id;
    }

    public Money getAmount() {
        return this.amount;
    }
}
```

#### 2. **Law of Demeter** (Domain Layer)

❌ **Getter 체이닝 금지**:
```java
// ❌ 나쁨: Law of Demeter 위반
String zipCode = order.getCustomer().getAddress().getZipCode();
```

✅ **Tell, Don't Ask**:
```java
// ✅ 좋음: Order가 행위 캡슐화
String zipCode = order.getCustomerZipCode();

// Order 클래스 내부:
public String getCustomerZipCode() {
    return this.customer.getZipCode();
}
```

#### 3. **트랜잭션 경계** (Application Layer)

❌ **@Transactional 내부의 외부 API 호출**:
```java
@Transactional
public Order createOrder(CreateOrderCommand command) {
    // ❌ 나쁨: 트랜잭션 내 외부 API 호출
    ExternalData data = externalApiPort.fetchData(command.externalId());

    Order order = Order.create(command.toOrderId(), data.toMoney());
    orderRepository.save(order);
    return order;
}
```

✅ **트랜잭션 외부의 외부 호출**:
```java
public Order createOrder(CreateOrderCommand command) {
    // 1. 트랜잭션 밖에서 외부 API 호출
    ExternalData data = externalApiPort.fetchData(command.externalId());

    // 2. 도메인 로직 + 영속성을 위한 트랜잭션만
    return executeInTransaction(command, data);
}

@Transactional
protected Order executeInTransaction(CreateOrderCommand command, ExternalData data) {
    Order order = Order.create(command.toOrderId(), data.toMoney());
    orderRepository.save(order);
    return order;
}
```

#### 4. **Javadoc 필수** (모든 레이어)

❌ **문서화 없음**:
```java
public class Order {
    public void confirm() {
        // ...
    }
}
```

✅ **완전한 문서화**:
```java
/**
 * 주문 Aggregate Root
 *
 * <p>비즈니스 규칙:</p>
 * <ul>
 *   <li>주문은 PENDING 상태에서만 확정 가능</li>
 *   <li>확정 시 재고 차감 이벤트 발행</li>
 * </ul>
 *
 * @author YourName (your.email@company.com)
 * @since 2025-10-17
 */
public class Order {
    /**
     * 주문을 확정합니다.
     *
     * @throws IllegalStateException PENDING 상태가 아닌 경우
     */
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
    }
}
```

### Layer별 규칙

#### Domain Layer

**허용**:
- ✅ `java.*`, `java.util.*`
- ✅ 내부 도메인 코드만
- ✅ Value Object용 `record`

**금지**:
- ❌ `org.springframework.*`
- ❌ `jakarta.persistence.*`
- ❌ `com.fasterxml.jackson.*`
- ❌ `lombok.*`
- ❌ 모든 I/O 작업

**예제**:
```java
// ✅ 좋음: 순수 도메인 모델
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        Objects.requireNonNull(currency, "Currency is required");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

#### Application Layer

**허용**:
- ✅ 도메인 모델
- ✅ `@Service`, `@Transactional`
- ✅ Port 인터페이스

**금지**:
- ❌ 직접적인 adapter 참조
- ❌ private/final 메서드에 `@Transactional`
- ❌ 트랜잭션 내 외부 API 호출

#### Adapter Layer (REST)

**필수**:
- ✅ `@RestController`
- ✅ 요청 검증용 `@Valid`
- ✅ HTTP 상태 코드 표준 (생성 201, 삭제 204)

**금지**:
- ❌ 응답에 도메인 객체
- ❌ Controller의 비즈니스 로직

**예제**:
```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
        @Valid @RequestBody OrderCreateRequest request
    ) {
        // 1. Request → Command
        PlaceOrderCommand command = mapper.toCommand(request);

        // 2. UseCase 실행
        PlaceOrderResult result = useCase.execute(command);

        // 3. Result → Response
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toResponse(result));
    }
}
```

---

## 💻 개발 워크플로우

### 표준 개발 플로우

```bash
# 1. feature 브랜치 생성
git checkout -b feature/order-management

# 2. Domain Layer 생성 (Domain-Driven)
# 옵션 A: Claude Code 사용
claude code
> "/code-gen-domain Order @prd/order-feature.md"

# 옵션 B: 수동 작성 후 자동 검증
# ... Order.java 작성 ...
python3 .claude/hooks/scripts/validation-helper.py Order.java domain

# 3. Application Layer 생성
> "/code-gen-usecase PlaceOrder @prd/order-feature.md"

# 4. Adapter Layer 생성
> "/code-gen-controller Order @prd/order-api-spec.md"

# 5. 테스트 작성 (TDD)
./gradlew :domain:test
./gradlew :application:test
./gradlew :adapter:adapter-in-admin-web:test

# 6. 품질 게이트 실행
./gradlew checkstyleMain pmdMain spotbugsMain

# 7. 커밋 (pre-commit hook을 통한 자동 검증)
git add .
git commit -m "feat: implement order management"
# → Pre-commit hook 자동 실행
# → 모든 검증기 통과
# → 커밋 성공

# 8. Push 및 PR 생성
git push origin feature/order-management
gh pr create --title "feat: implement order management"
```

### 품질 게이트 체크리스트

커밋 전 확인사항:

- [ ] ArchUnit 테스트 통과 (`./gradlew :application:test --tests "*ArchitectureTest"`)
- [ ] Checkstyle 위반 없음 (`./gradlew checkstyleMain`)
- [ ] SpotBugs 이슈 없음 (`./gradlew spotbugsMain`)
- [ ] PMD 위반 없음 (`./gradlew pmdMain`)
- [ ] 테스트 커버리지 임계값 충족 (Domain 90%, Application 80%, Adapter 70%)
- [ ] `@author`, `@since`가 포함된 완전한 Javadoc
- [ ] Lombok 사용 없음
- [ ] Dead code 없음

---

## 🔧 사용 가능한 명령어

### Slash 명령어 (Claude Code)

#### `/code-gen-domain <Aggregate> [PRD]`

전체 도메인 규칙이 적용된 DDD Aggregate 생성.

```bash
/code-gen-domain Order
/code-gen-domain Payment @prd/payment-feature.md
```

**생성되는 파일**:
- `Order.java` - Aggregate Root
- `OrderId.java` - 타입 ID (record)
- `OrderStatus.java` - 상태 Enum

**자동 적용 규칙**:
- ❌ Lombok 없음
- ✅ Law of Demeter
- ✅ Tell, Don't Ask 패턴
- ✅ 순수 Java (Spring/JPA 없음)

#### `/code-gen-usecase <UseCase> [PRD]`

트랜잭션 경계가 있는 Application UseCase 생성.

```bash
/code-gen-usecase PlaceOrder
/code-gen-usecase CancelOrder @prd/order-management.md
```

**생성되는 파일**:
- `PlaceOrderUseCase.java` - UseCase 서비스
- `PlaceOrderCommand.java` - 입력 DTO (record)
- `PlaceOrderResult.java` - 출력 DTO (record)
- `OrderAssembler.java` - Domain ↔ DTO 매퍼

**자동 적용 규칙**:
- ❌ `@Transactional` 내 외부 API 호출 금지
- ❌ private/final 메서드에 `@Transactional` 금지
- ✅ DTO 변환 패턴
- ✅ 짧은 트랜잭션 유지

#### `/code-gen-controller <Resource> [PRD]`

OpenAPI 표준의 REST API Controller 생성.

```bash
/code-gen-controller Order
/code-gen-controller Payment @prd/payment-api.md
```

**생성되는 파일**:
- `OrderController.java` - REST Controller
- `OrderCreateRequest.java` - Request DTO
- `OrderResponse.java` - Response DTO
- `OrderApiMapper.java` - API ↔ UseCase 매퍼

**자동 적용 규칙**:
- ✅ `@RestController` 어노테이션
- ✅ `@Valid` 검증
- ✅ HTTP 상태 코드 (201/204/200)
- ❌ 응답에 도메인 객체 금지

### 검증 명령어

```bash
# 단일 파일 검증
/validate-domain domain/src/main/java/.../Order.java

# 전체 레이어 검증
/validate-architecture domain

# 모든 ArchUnit 테스트 실행
./gradlew :application:test --tests "*ArchitectureTest"
```

### Git Pre-commit Hooks

`git commit` 시 자동 실행:

```bash
# Layer별 검증기
hooks/validators/domain-validator.sh          # 도메인 순수성
hooks/validators/application-validator.sh     # 트랜잭션 경계
hooks/validators/adapter-in-validator.sh      # REST API 표준
hooks/validators/adapter-out-validator.sh     # Repository 패턴
hooks/validators/common-validator.sh          # Javadoc, @author 태그
hooks/validators/dead-code-detector.sh        # 미사용 코드 감지
```

---

## 🧪 테스팅

### 테스트 커버리지 요구사항

| Layer | 커버리지 | 검증 도구 |
|-------|----------|--------------|
| Domain | 90% | JaCoCo + ArchUnit |
| Application | 80% | JaCoCo + ArchUnit |
| Adapter | 70% | JaCoCo + Testcontainers |

### 테스트 실행

```bash
# 단위 테스트 (빠름)
./gradlew test

# 통합 테스트 (Testcontainers 사용)
./gradlew integrationTest

# 아키텍처 테스트
./gradlew :application:test --tests "*HexagonalArchitectureTest"
./gradlew :application:test --tests "*SingleResponsibilityTest"
./gradlew :application:test --tests "*LawOfDemeterTest"

# 커버리지 리포트
./gradlew jacocoTestReport
# → Open: build/reports/jacoco/test/html/index.html

# 커버리지 검증 (임계값 미달 시 빌드 실패)
./gradlew jacocoTestCoverageVerification
```

### 테스트 예제

**Domain 테스트** (순수 단위 테스트, 90% 커버리지):
```java
@Test
void 주문_생성_성공() {
    // given
    OrderId orderId = OrderId.of("ORD-001");
    Money amount = Money.of(BigDecimal.valueOf(10000), Currency.KRW);

    // when
    Order order = Order.create(orderId, amount);

    // then
    assertThat(order.getId()).isEqualTo(orderId);
    assertThat(order.getAmount()).isEqualTo(amount);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
}
```

**Application 테스트** (Mock을 사용한 UseCase 테스트, 80% 커버리지):
```java
@Test
void 주문_생성_유즈케이스_성공() {
    // given
    PlaceOrderCommand command = new PlaceOrderCommand(
        "CUST-001",
        BigDecimal.valueOf(10000)
    );
    given(orderRepository.save(any(Order.class)))
        .willReturn(savedOrder);

    // when
    PlaceOrderResult result = useCase.execute(command);

    // then
    assertThat(result.orderId()).isEqualTo("ORD-001");
    verify(orderRepository).save(any(Order.class));
}
```

**Adapter 테스트** (Testcontainers를 사용한 통합 테스트, 70% 커버리지):
```java
@SpringBootTest
@Testcontainers
class OrderJpaRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Test
    void 주문_저장_성공() {
        // given
        OrderEntity entity = new OrderEntity("ORD-001", 10000);

        // when
        OrderEntity saved = repository.save(entity);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOrderId()).isEqualTo("ORD-001");
    }
}
```

---

## 🐛 문제 해결

### 문제 1: Cache 파일을 찾을 수 없음

**증상**:
```
FileNotFoundError: .claude/cache/rules/index.json
```

**해결**:
```bash
python3 .claude/hooks/scripts/build-rule-cache.py
```

### 문제 2: Hooks가 실행되지 않음

**증상**:
- 규칙이 주입되지 않음
- 검증이 실행되지 않음

**해결**:
```bash
# 권한 확인
ls -la .claude/hooks/*.sh

# 실행 권한 부여
chmod +x .claude/hooks/user-prompt-submit.sh
chmod +x .claude/hooks/after-tool-use.sh

# 로그 확인
tail -50 .claude/hooks/logs/hook-execution.log
```

### 문제 3: Lombok으로 검증 실패

**증상**:
```
⚠️ 검증 실패: Lombok 사용 감지
```

**해결**:
```java
// ❌ Lombok 제거
@Data
public class Order {
    private String id;
}

// ✅ 순수 Java 사용
public class Order {
    private final String id;

    public Order(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
```

### 문제 4: Layer가 감지되지 않음

**증상**:
```
Detected Layer: unknown
Fallback to basic validation
```

**해결**:
파일 경로가 layer 패턴과 일치하는지 확인:
- Domain: `domain/*/model/`
- Application: `application/`
- Adapter-REST: `adapter/in/web/`
- Adapter-Persistence: `adapter/out/persistence/`

### 문제 5: 커버리지로 빌드 실패

**증상**:
```
JaCoCo coverage verification failed: Domain coverage 85% < 90%
```

**해결**:
```bash
# 커버리지 리포트 생성
./gradlew jacocoTestReport

# 리포트 보기
open build/reports/jacoco/test/html/index.html

# 90% 임계값에 도달하도록 누락된 테스트 추가
```

---

## 📖 학습 경로

### 신규 팀원을 위한 가이드

#### 1일차: 온보딩 (2시간)
1. 이 README 읽기 (30분)
2. 빠른 시작 실행 (15분)
3. [Getting Started 튜토리얼](docs/tutorials/01-getting-started.md) 읽기 (30분)
4. `/code-gen-domain`으로 첫 코드 생성 (30분)
5. 생성된 코드 및 검증 결과 리뷰 (15분)

#### 1주차: 핵심 개념
- [ ] **아키텍처**: [헥사고날 아키텍처 가이드](docs/architecture/) 읽기 (TODO)
- [ ] **DDD**: [DDD Aggregate Migration 가이드](docs/DDD_AGGREGATE_MIGRATION_GUIDE.md) 읽기
- [ ] **DTO 패턴**: [DTO 패턴 가이드](docs/DTO_PATTERNS_GUIDE.md) 읽기
- [ ] **테스팅**: 1개의 domain, 1개의 application, 1개의 adapter 컴포넌트 테스트 작성

#### 2주차: 고급 주제
- [ ] **Dynamic Hooks**: [Dynamic Hooks 가이드](docs/DYNAMIC_HOOKS_GUIDE.md) 읽기
- [ ] **커스텀 규칙**: `.claude/cache/rules/`에 커스텀 규칙 추가
- [ ] **에러 처리**: [Exception Handling 가이드](docs/EXCEPTION_HANDLING_GUIDE.md) 읽기
- [ ] **Java 21**: [Java Record 가이드](docs/JAVA_RECORD_GUIDE.md) 읽기

#### 1개월차: 숙련
- [ ] 전체 기능 완성 (Domain → Application → Adapter)
- [ ] 첫 시도에 모든 품질 게이트 통과
- [ ] 코딩 표준 문서에 기여
- [ ] 신규 팀원 멘토링

---

## 🤝 기여하기

### 새로운 규칙 추가

1. `docs/coding_convention/`에 마크다운 파일 생성
2. Cache 재빌드:
   ```bash
   python3 .claude/hooks/scripts/build-rule-cache.py
   ```
3. 검증 테스트:
   ```bash
   python3 .claude/hooks/scripts/validation-helper.py YourFile.java layer
   ```

### 이슈 제보

GitHub Issues에 다음 내용을 포함하여 제보:
- 명확한 제목 (예: "유효한 코드에 대해 검증 실패")
- 재현 단계
- 예상 동작 vs 실제 동작
- 코드 샘플

---

## 📚 문서

### 빠른 참조
- [코딩 표준 요약본](docs/CODING_STANDARDS_SUMMARY.md) - 10개 핵심 규칙
- [엔터프라이즈 표준 요약본](docs/ENTERPRISE_SPRING_STANDARDS_SUMMARY.md) - 아키텍처 패턴

### 완전한 가이드
- [코딩 표준 (전체)](docs/CODING_STANDARDS.md) - 87개 규칙, 2,676줄
- [엔터프라이즈 표준 (전체)](docs/ENTERPRISE_SPRING_STANDARDS_PROMPT.md) - 96개 패턴, 3,361줄

### 전문 주제
- [DDD Aggregate Migration](docs/DDD_AGGREGATE_MIGRATION_GUIDE.md)
- [DTO 패턴](docs/DTO_PATTERNS_GUIDE.md)
- [Exception Handling](docs/EXCEPTION_HANDLING_GUIDE.md)
- [Java Record 사용법](docs/JAVA_RECORD_GUIDE.md)
- [버전 관리](docs/VERSION_MANAGEMENT_GUIDE.md)

### 시스템 가이드
- [Dynamic Hooks 시스템](docs/DYNAMIC_HOOKS_GUIDE.md) - 완전한 시스템 문서
- [Cache 시스템](/.claude/cache/rules/README.md) - JSON cache 세부사항
- [Slash 명령어](.claude/commands/README.md) - 명령어 참조

### 품질 도구
- [Checkstyle 설정](config/checkstyle/README.md)
- [PMD 규칙](config/pmd/README.md)
- [SpotBugs 설정](config/spotbugs/README.md)

---

## 🎯 프로젝트 목표

1. **Zero Configuration**: Clone → Build → Run (5분)
2. **자동 품질 관리**: AI가 표준을 자동으로 강제
3. **고성능**: 90% 토큰 절감, 73% 빠른 검증
4. **개발자 경험**: Claude Code와의 원활한 통합
5. **프로덕션 준비**: 엔터프라이즈 패턴, 포괄적인 테스팅

---

## 📊 기술 스택

| 카테고리 | 기술 |
|----------|-----------|
| **언어** | Java 21 |
| **프레임워크** | Spring Boot 3.5.x |
| **빌드 도구** | Gradle 8.5+ (Kotlin DSL) |
| **아키텍처** | Hexagonal (Ports & Adapters) |
| **설계 패턴** | DDD, CQRS |
| **ORM** | JPA + QueryDSL |
| **데이터베이스** | PostgreSQL 16 |
| **테스팅** | JUnit 5, Mockito, Testcontainers |
| **품질 게이트** | Checkstyle, PMD, SpotBugs, ArchUnit |
| **AI 통합** | Claude Code + Dynamic Hooks |
| **Cache 시스템** | JSON 기반 규칙 캐시 |

---

## 🔒 보안 고려사항

### Dynamic Hooks 안전성

⚠️ **중요**: Hook 스크립트는 사용자 권한으로 실행됩니다.

**모범 사례**:
- ✅ 실행 전 모든 hook 스크립트 검토
- ✅ Hook 스크립트 버전 관리
- ✅ Hook 변경사항에 대한 코드 리뷰
- ✅ 안전한 환경에서 먼저 테스트
- ❌ 신뢰할 수 없는 소스의 hooks 실행 금지
- ❌ Hooks에 비밀 정보 하드코딩 금지

**검증 체크리스트**:
```bash
# 스크립트 문법 확인
bash -n .claude/hooks/user-prompt-submit.sh

# 권한 확인
ls -la .claude/hooks/*.sh

# 위험한 명령어 검색
grep -E "(rm -rf|sudo|curl.*\| bash|eval|exec)" .claude/hooks/*.sh
```

자세한 내용은 [Dynamic Hooks 가이드 - 보안](docs/DYNAMIC_HOOKS_GUIDE.md#보안-고려사항)을 참조하세요.

---

## 🌟 다음 단계

### 로드맵

- [ ] **2025 Q4**: 규칙 관리를 위한 Web UI
- [ ] **2026 Q1**: 실시간 검증을 위한 VS Code 확장
- [ ] **2026 Q2**: 다중 언어 지원 (Kotlin, TypeScript)
- [ ] **2026 Q3**: 클라우드 기반 캐시 동기화

### 현재 상태

- ✅ Dynamic Hooks + Cache System (v2.0)
- ✅ JSON 캐시가 있는 90개 코딩 규칙
- ✅ 실시간 검증 시스템
- ✅ 코드 생성용 Slash 명령어
- ✅ 완전한 문서
- ⏳ 웹 기반 규칙 편집기 (계획 중)
- ⏳ IDE 플러그인 (계획 중)

---

## 📄 라이선스

© 2024 Ryu-qqq. All Rights Reserved.

---

## 🙏 감사의 말

이 프로젝트는 다음을 활용합니다:
- **Spring Boot** - 가장 인기 있는 Java 프레임워크
- **Hexagonal Architecture** - Alistair Cockburn의 패턴
- **Domain-Driven Design** - Eric Evans의 방법론
- **Claude AI** - Anthropic의 언어 모델
- **Dynamic Hooks** - AI 지원 개발에 대한 우리의 혁신적인 기여

---

## 📞 지원

- **문서**: [docs/](docs/)
- **이슈**: [GitHub Issues](https://github.com/your-org/claude-spring-standards/issues)
- **토론**: [GitHub Discussions](https://github.com/your-org/claude-spring-standards/discussions)

---

**✅ AI 기반 품질 보증으로 엔터프라이즈급 Spring 애플리케이션을 구축할 준비가 되었습니다!**

---

*최종 업데이트: 2025-10-17*
