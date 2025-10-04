# DDD Aggregate 패턴 마이그레이션 가이드

## 개요

이 템플릿은 **두 가지 도메인 패키지 구조**를 모두 지원합니다:

1. **Technical Concern 패턴** (기본): 기술적 관심사별로 분리
2. **DDD Aggregate 패턴** (권장): 비즈니스 도메인 경계별로 분리

## 구조 비교

### Technical Concern 패턴 (기존)

```
domain/
├── model/              # 모든 도메인 엔티티
│   ├── User.java
│   ├── Order.java
│   └── Product.java
├── vo/                 # 모든 Value Objects
│   ├── Email.java
│   ├── Money.java
│   └── Address.java
├── service/            # 모든 도메인 서비스
│   ├── UserService.java
│   └── OrderService.java
└── exception/          # 모든 도메인 예외
    ├── UserNotFoundException.java
    └── InvalidOrderException.java
```

**장점:**
- 직관적이고 이해하기 쉬움
- 작은 프로젝트에 적합
- 기술적 분류가 명확

**단점:**
- 비즈니스 도메인 경계가 불명확
- 관련 개념들이 여러 패키지에 흩어짐
- 여러 Aggregate가 있을 때 구조적 혼란
- 도메인 변경 시 여러 패키지 수정 필요

### DDD Aggregate 패턴 (권장)

```
domain/
├── common/                    # 공유 요소
│   ├── event/
│   │   └── DomainEvent.java
│   └── exception/
│       └── DomainException.java
├── user/                      # User Aggregate
│   ├── User.java              # Aggregate Root
│   ├── UserId.java            # Entity ID
│   ├── vo/
│   │   ├── Email.java
│   │   └── UserProfile.java
│   ├── event/
│   │   ├── UserCreatedEvent.java
│   │   └── UserActivatedEvent.java
│   ├── exception/
│   │   └── UserNotFoundException.java
│   └── service/
│       └── UserDomainService.java
├── order/                     # Order Aggregate
│   ├── Order.java             # Aggregate Root
│   ├── OrderId.java
│   ├── OrderItem.java         # Entity
│   ├── vo/
│   │   ├── Money.java
│   │   └── Quantity.java
│   ├── event/
│   │   ├── OrderPlacedEvent.java
│   │   └── OrderCancelledEvent.java
│   └── exception/
│       └── InvalidOrderException.java
└── product/                   # Product Aggregate
    ├── Product.java
    ├── ProductId.java
    ├── vo/
    │   └── ProductName.java
    └── event/
        └── ProductCreatedEvent.java
```

**장점:**
- 비즈니스 도메인 경계 명확
- 높은 응집도 (관련 요소가 한 곳에)
- 낮은 결합도 (Aggregate 간 독립성)
- 확장성 (새 Aggregate 추가 용이)
- 마이크로서비스 전환 시 Aggregate 단위로 분리 가능

**단점:**
- 초기 학습 곡선
- 작은 프로젝트에는 과도할 수 있음

## 실제 예시: Policy Aggregate

```
domain/
└── policy/
    ├── UploadPolicy.java              # Aggregate Root
    ├── PolicyKey.java                 # Entity ID (복합키)
    ├── FileType.java                  # Enum
    ├── vo/
    │   ├── Dimension.java             # Value Object (비즈니스 의미 이름)
    │   ├── RateLimiting.java          # Value Object
    │   ├── ImagePolicy.java           # Value Object
    │   └── FileTypePolicies.java      # Value Object Collection
    ├── event/
    │   ├── PolicyActivatedEvent.java
    │   └── PolicyUpdatedEvent.java
    └── exception/
        ├── PolicyViolationException.java
        └── InvalidPolicyException.java
```

### Value Object 명명 규칙 변경

**변경 전 (VO suffix 강제):**
```java
// ❌ Technical한 이름
public record DimensionVO(int width, int height) {}
public record RateLimitingVO(int maxSize, Duration period) {}
```

**변경 후 (비즈니스 의미 중심):**
```java
// ✅ 비즈니스 의미를 담은 이름
public record Dimension(int width, int height) {}
public record RateLimiting(int maxSize, Duration period) {}
```

**규칙:**
- ✅ Record 사용 권장 (불변성 보장)
- ✅ Final class도 허용 (복잡한 로직 필요 시)
- ✅ 비즈니스 의미를 담은 이름 사용
- ❌ VO suffix 불필요

## 마이그레이션 단계

### 1단계: Aggregate 식별

도메인 모델에서 Aggregate Root를 식별합니다:

```
질문:
1. 이 엔티티는 독립적인 생명주기를 가지는가?
2. 다른 엔티티와 강한 일관성 경계를 가지는가?
3. 트랜잭션 단위로 관리되는가?

예시:
✅ User → User Aggregate
✅ Order → Order Aggregate
✅ Product → Product Aggregate
❌ OrderItem → Order Aggregate의 일부
❌ Address → User Aggregate의 일부 (또는 공유 VO)
```

### 2단계: 패키지 재구성

각 Aggregate별로 디렉토리를 생성합니다:

```bash
# Before
domain/model/User.java
domain/vo/EmailVO.java
domain/exception/UserNotFoundException.java

# After
domain/user/User.java
domain/user/vo/Email.java
domain/user/exception/UserNotFoundException.java
```

### 3단계: Import 경로 수정

```java
// Before
import com.company.template.domain.model.User;
import com.company.template.domain.vo.EmailVO;

// After
import com.company.template.domain.user.User;
import com.company.template.domain.user.vo.Email;
```

### 4단계: ArchUnit 테스트 실행

```bash
./gradlew :domain:test --tests HexagonalArchitectureTest
```

## ArchUnit 테스트 변경 사항

### 1. Value Object 규칙 완화

**변경 전:**
```java
@Test
void valueObjectsMustFollowNamingConvention() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..vo..")
        .should().haveSimpleNameEndingWith("VO")  // ❌ VO suffix 강제
        .orShould().beRecords();
}
```

**변경 후:**
```java
@Test
void valueObjectsMustBeImmutable() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..vo..")
        .should().beRecords()                     // ✅ Record 권장
        .orShould().haveModifier(JavaModifier.FINAL)  // ✅ Final class 허용
        .because("Value Objects must be immutable");
}
```

### 2. 도메인 패키지 구조 규칙 완화

**변경 전:**
```java
@Test
void domainModelMustBeInCorrectPackage() {
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..")
        .should().resideInAPackage("..domain.model..")  // ❌ Technical Concern만 허용
        .orShould().resideInAPackage("..domain..vo..")
        .orShould().resideInAPackage("..domain..service..");
}
```

**변경 후:**
```java
@Test
void domainPackageStructureShouldBeValid() {
    // ✅ Technical Concern과 DDD Aggregate 모두 허용
    ArchRule rule = classes()
        .that().resideInAPackage("..domain..")
        .should().resideInAPackage("..domain..")
        .because("Domain classes can follow either Technical Concern or DDD Aggregate structure");
}
```

### 3. Aggregate 순환 의존성 검증 추가

```java
@Test
void aggregatesShouldNotHaveCyclicDependencies() {
    ArchRule rule = slices()
        .matching("..domain.(*)..")
        .should().beFreeOfCycles()
        .because("Each DDD Aggregate should be independent bounded context");
}
```

## 선택 가이드라인

### Technical Concern 패턴을 선택하는 경우

- 프로젝트 규모가 작음 (< 10개 엔티티)
- 단일 Bounded Context
- 팀원들이 DDD에 익숙하지 않음
- 빠른 프로토타이핑 필요

### DDD Aggregate 패턴을 선택하는 경우

- 프로젝트 규모가 중대형 (> 10개 엔티티)
- 여러 Bounded Context 존재
- 마이크로서비스 전환 예정
- 도메인 경계가 명확함
- 팀이 DDD에 익숙함

## 혼합 사용 가능

두 패턴을 혼합하여 사용할 수도 있습니다:

```
domain/
├── common/           # 공유 요소
├── order/            # DDD Aggregate (복잡한 도메인)
│   ├── Order.java
│   ├── vo/
│   └── event/
└── model/            # Technical Concern (단순한 엔티티)
    └── SimpleEntity.java
```

## 추가 리소스

- [DDD Reference - Aggregates](https://www.domainlanguage.com/ddd/reference/)
- [Implementing Domain-Driven Design by Vaughn Vernon](https://vaughnvernon.com/)
- Issue #1: DDD Aggregate 기반 패키지 구조 권장
- Issue #2: Value Object와 Domain Event에 Java Record 사용 권장
- Issue #3: ArchUnit 테스트를 DDD Aggregate 패턴에 맞게 수정

## 관련 문서

- `CODING_STANDARDS.md`: 전체 코딩 표준
- `CUSTOMIZATION_GUIDE.md`: 템플릿 커스터마이징 가이드
