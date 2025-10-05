# 🚨 Exception Handling Guide

Hexagonal Architecture 기반 Spring Boot 프로젝트의 예외 처리 전략 가이드입니다.

---

## 📐 핵심 원칙

### 1. 모든 예외는 언체크 예외 (RuntimeException)

**이유**:
- Spring `@Transactional`의 기본 동작과 일치 (언체크 예외만 롤백)
- 메서드 시그니처가 깔끔해짐 (throws 불필요)
- 현대적 예외 처리 방식 (Kotlin, Rust 등 최신 언어 트렌드)
- 주요 프레임워크(Spring, Hibernate)도 언체크 예외 사용

### 2. Domain에서 비즈니스 규칙 검증 및 예외 발생

**이유**:
- Domain은 항상 유효한 상태 보장 (Invariant)
- 비즈니스 로직이 Domain Layer에 집중
- 테스트하기 쉬움

### 3. Application 예외는 거의 사용하지 않음

**이유**:
- Domain 예외로 충분한 경우가 대부분
- 레이어 경계를 명확히 유지
- 불필요한 예외 클래스 증식 방지

### 4. Adapter에서 Infrastructure 예외를 Domain 예외로 변환

**이유**:
- Domain이 인프라 기술에 의존하지 않도록
- 일관된 예외 처리 흐름 유지
- 기술 변경 시 영향 최소화

### 5. GlobalExceptionHandler로 통합 예외 처리

**이유**:
- 일관된 에러 응답 형식
- 중복 예외 처리 코드 제거
- 로깅 전략 통합 관리

---

## 🏗️ Exception Hierarchy

### Base Exception

```java
/**
 * 모든 비즈니스 예외의 기본 클래스
 * 언체크 예외(RuntimeException)로 통일
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Domain Exceptions (비즈니스 규칙 위반)

#### 특징
- 비즈니스 규칙 위반 시 발생
- Domain 객체 내부에서 발생
- 언체크 예외 (RuntimeException 상속)

#### 위치
```
domain/
└── {aggregate}/
    └── exception/
        ├── {Aggregate}NotFoundException.java         # 도메인 엔티티 조회 실패
        ├── Invalid{Aggregate}StateException.java     # 도메인 상태 규칙 위반
        ├── {Aggregate}AlreadyExistsException.java    # 중복 생성
        └── {Aggregate}RuleViolationException.java    # 비즈니스 규칙 위반
```

#### 예시: Policy Aggregate
```java
// domain/policy/exception/PolicyNotFoundException.java
public class PolicyNotFoundException extends BusinessException {
    public PolicyNotFoundException(String policyKey) {
        super("정책을 찾을 수 없습니다: " + policyKey);
    }
}

// domain/policy/exception/InvalidPolicyStateException.java
public class InvalidPolicyStateException extends BusinessException {
    public InvalidPolicyStateException(String message) {
        super(message);
    }
}

// domain/policy/exception/PolicyAlreadyExistsException.java
public class PolicyAlreadyExistsException extends BusinessException {
    public PolicyAlreadyExistsException(String policyKey) {
        super("정책이 이미 존재합니다: " + policyKey);
    }

    public PolicyAlreadyExistsException(String policyKey, Throwable cause) {
        super("정책이 이미 존재합니다: " + policyKey, cause);
    }
}
```

### Application Exceptions (거의 사용하지 않음)

#### 특징
- Application 레이어에서만 의미있는 예외
- 대부분의 경우 **불필요** (Domain Exception으로 충분)
- 정말 필요한 경우에만 생성

#### 사용하지 않는 예시
```java
// ❌ 불필요한 Application Exception
public class PolicyValidationException extends BusinessException {
    // Domain에서 검증하므로 불필요
}

public class PolicyCreationFailedException extends BusinessException {
    // 구체적인 Domain Exception 사용
}
```

#### 사용해도 되는 예시 (드물게)
```java
// ✅ Application 레이어에서만 의미있는 예외 (드문 경우)
public class ExternalServiceUnavailableException extends BusinessException {
    public ExternalServiceUnavailableException(String serviceName, Throwable cause) {
        super("외부 서비스 호출 실패: " + serviceName, cause);
    }
}
```

### Infrastructure Exceptions (Adapter Layer)

#### 특징
- Adapter에서 발생하는 기술적 예외
- Domain Exception으로 변환하여 전파

#### 예시: Persistence Adapter
```java
@Component
public class UploadPolicyPersistenceAdapter implements SaveUploadPolicyPort {

    private final PolicyJpaRepository repository;

    public UploadPolicyPersistenceAdapter(PolicyJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UploadPolicy save(UploadPolicy policy) {
        try {
            PolicyEntity entity = PolicyEntity.from(policy);
            PolicyEntity saved = repository.save(entity);
            return saved.toDomain();
        } catch (DataIntegrityViolationException e) {
            // ✅ Infrastructure 예외 → Domain 예외로 변환
            throw new PolicyAlreadyExistsException(
                policy.getPolicyKey().getValue(),
                e
            );
        }
    }
}
```

---

## 📦 패키지 구조

### Aggregate별 예외 패키지

```
domain/
└── {aggregate}/
    ├── {AggregateRoot}.java
    ├── vo/
    ├── event/
    └── exception/              ✅ Aggregate별 예외 패키지
        ├── {Aggregate}NotFoundException.java
        ├── Invalid{Aggregate}StateException.java
        └── {Aggregate}RuleViolationException.java

application/
└── {aggregate}/
    ├── dto/
    ├── port/
    ├── service/
    └── exception/              ✅ 필요한 경우에만 (드물게)
        └── External{Service}Exception.java

adapter/
└── web/
    └── exception/
        └── GlobalExceptionHandler.java  ✅ 전역 예외 처리
```

### 예시: Policy Aggregate 패키지 구조
```
domain/policy/exception/
├── PolicyNotFoundException.java          # 조회 실패
├── InvalidPolicyStateException.java      # 상태 규칙 위반
├── PolicyAlreadyExistsException.java     # 중복 생성
└── PolicyRuleViolationException.java     # 비즈니스 규칙 위반
```

---

## 🎯 계층별 예외 책임

### Domain Layer

**책임**: 비즈니스 규칙 검증 및 예외 발생

```java
public class UploadPolicy {

    private final PolicyKey policyKey;
    private boolean isActive;

    /**
     * 정책 활성화
     * @throws InvalidPolicyStateException 이미 활성화된 경우
     */
    public UploadPolicy activate() {
        if (this.isActive) {
            throw new InvalidPolicyStateException(
                "정책이 이미 활성화 상태입니다: " + this.policyKey.getValue()
            );
        }
        this.isActive = true;
        return this;
    }

    /**
     * 정책 비활성화
     * @throws InvalidPolicyStateException 이미 비활성화된 경우
     */
    public UploadPolicy deactivate() {
        if (!this.isActive) {
            throw new InvalidPolicyStateException(
                "정책이 이미 비활성화 상태입니다: " + this.policyKey.getValue()
            );
        }
        this.isActive = false;
        return this;
    }
}
```

### Application Layer

**책임**: Domain 예외 전파 (추가 예외 처리 거의 없음)

```java
@UseCase
@Transactional
public class ActivateUploadPolicyService implements ActivateUploadPolicyUseCase {

    private final LoadUploadPolicyPort loadPort;
    private final UpdateUploadPolicyPort updatePort;

    public ActivateUploadPolicyService(
        LoadUploadPolicyPort loadPort,
        UpdateUploadPolicyPort updatePort
    ) {
        this.loadPort = loadPort;
        this.updatePort = updatePort;
    }

    @Override
    public UploadPolicyResponse activatePolicy(PolicyKeyDto dto) {
        // ✅ Domain 예외 전파
        UploadPolicy policy = loadPort.loadByKey(dto.toDomain())
            .orElseThrow(() -> new PolicyNotFoundException(dto.toString()));

        UploadPolicy activated = policy.activate();  // Domain에서 검증
        UploadPolicy saved = updatePort.update(activated);
        return UploadPolicyResponse.from(saved);
    }
}
```

### Adapter Layer

**책임**: Infrastructure 예외를 Domain 예외로 변환

```java
// Persistence Adapter
@Component
public class UploadPolicyPersistenceAdapter implements SaveUploadPolicyPort {

    private final PolicyJpaRepository repository;

    public UploadPolicyPersistenceAdapter(PolicyJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UploadPolicy save(UploadPolicy policy) {
        try {
            PolicyEntity entity = PolicyEntity.from(policy);
            PolicyEntity saved = repository.save(entity);
            return saved.toDomain();
        } catch (DataIntegrityViolationException e) {
            // ✅ Infrastructure 예외 → Domain 예외로 변환
            throw new PolicyAlreadyExistsException(
                policy.getPolicyKey().getValue(),
                e
            );
        }
    }
}

// External API Adapter
@Component
public class PaymentGatewayAdapter implements ProcessPaymentPort {

    private final RestTemplate restTemplate;

    @Override
    public PaymentResult process(Payment payment) {
        try {
            return restTemplate.postForObject(
                "/api/payments",
                payment,
                PaymentResult.class
            );
        } catch (HttpClientErrorException e) {
            // ✅ Infrastructure 예외 → Application/Domain 예외로 변환
            throw new ExternalServiceUnavailableException("Payment Gateway", e);
        }
    }
}
```

---

## 🌐 GlobalExceptionHandler 패턴

### 통합 예외 처리

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Domain: 엔티티 조회 실패
     */
    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePolicyNotFound(
            PolicyNotFoundException e
    ) {
        log.warn("정책 조회 실패: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of("POLICY_NOT_FOUND", e.getMessage()));
    }

    /**
     * Domain: 비즈니스 규칙 위반
     */
    @ExceptionHandler(InvalidPolicyStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPolicyState(
            InvalidPolicyStateException e
    ) {
        log.warn("정책 상태 오류: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of("INVALID_POLICY_STATE", e.getMessage()));
    }

    /**
     * Domain: 중복 생성
     */
    @ExceptionHandler(PolicyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePolicyAlreadyExists(
            PolicyAlreadyExistsException e
    ) {
        log.warn("정책 중복: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse.of("POLICY_ALREADY_EXISTS", e.getMessage()));
    }

    /**
     * Web: Bean Validation 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));

        log.warn("입력 검증 실패: {}", message);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of("VALIDATION_FAILED", message));
    }

    /**
     * Application: 외부 서비스 오류
     */
    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceError(
            ExternalServiceUnavailableException e
    ) {
        log.error("외부 서비스 오류", e);
        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(ErrorResponse.of("EXTERNAL_SERVICE_ERROR", e.getMessage()));
    }

    /**
     * 기타: 예상하지 못한 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception e) {
        log.error("예상하지 못한 오류", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다"));
    }
}
```

### Error Response DTO

```java
/**
 * 표준 에러 응답 DTO
 */
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, LocalDateTime.now());
    }
}
```

**응답 예시**:
```json
{
  "code": "POLICY_NOT_FOUND",
  "message": "정책을 찾을 수 없습니다: tenant1/SELLER/IMAGE",
  "timestamp": "2024-01-01T10:30:00"
}
```

---

## 📊 로깅 전략

### 로그 레벨 가이드

| 예외 종류 | HTTP Status | 로그 레벨 | 예시 |
|----------|-------------|----------|------|
| NotFound | 404 | WARN | `PolicyNotFoundException` |
| Validation | 400 | WARN | `InvalidPolicyStateException` |
| Conflict | 409 | WARN | `PolicyAlreadyExistsException` |
| External | 502/503 | ERROR | `ExternalServiceUnavailableException` |
| Unexpected | 500 | ERROR | `Exception` |

### 원칙

- **WARN**: 사용자 실수, 비즈니스 규칙 위반 (정상 흐름의 일부)
- **ERROR**: 시스템 오류, 예상하지 못한 예외 (비정상 흐름)

### 예외 메시지 작성 가이드

#### ✅ Good: 문제 상황과 컨텍스트 포함
```java
throw new PolicyNotFoundException(
    "정책을 찾을 수 없습니다: " + policyKey.getValue()
);

throw new InvalidPolicyStateException(
    "정책이 이미 활성화 상태입니다: " + this.policyKey.getValue()
);

throw new PolicyAlreadyExistsException(
    "정책이 이미 존재합니다: " + policyKey.getValue()
);
```

#### ❌ Bad: 불충분한 정보
```java
throw new PolicyNotFoundException("Not found");

throw new InvalidPolicyStateException("Invalid state");
```

#### ❌ Bad: 너무 장황
```java
throw new PolicyNotFoundException(
    "The requested upload policy with policy key " + policyKey.getValue() +
    " could not be found in the database. Please check if the policy exists."
);
```

---

## 🔄 완전한 예시: Policy 생성 Flow

### 1. Domain Exception 정의
```java
// domain/policy/exception/PolicyNotFoundException.java
public class PolicyNotFoundException extends BusinessException {
    public PolicyNotFoundException(String policyKey) {
        super("정책을 찾을 수 없습니다: " + policyKey);
    }
}

// domain/policy/exception/InvalidPolicyStateException.java
public class InvalidPolicyStateException extends BusinessException {
    public InvalidPolicyStateException(String message) {
        super(message);
    }
}
```

### 2. Domain에서 예외 발생
```java
// domain/policy/UploadPolicy.java
public class UploadPolicy {

    private final PolicyKey policyKey;
    private boolean isActive;

    public UploadPolicy activate() {
        if (this.isActive) {
            throw new InvalidPolicyStateException(
                "정책이 이미 활성화 상태입니다: " + this.policyKey.getValue()
            );
        }
        this.isActive = true;
        return this;
    }
}
```

### 3. Application Service에서 예외 전파
```java
// application/policy/service/ActivateUploadPolicyService.java
@UseCase
@Transactional
public class ActivateUploadPolicyService implements ActivateUploadPolicyUseCase {

    private final LoadUploadPolicyPort loadPort;
    private final UpdateUploadPolicyPort updatePort;

    @Override
    public UploadPolicyResponse activatePolicy(PolicyKeyDto dto) {
        UploadPolicy policy = loadPort.loadByKey(dto.toDomain())
            .orElseThrow(() -> new PolicyNotFoundException(dto.toString()));

        UploadPolicy activated = policy.activate();  // Domain에서 검증
        UploadPolicy saved = updatePort.update(activated);
        return UploadPolicyResponse.from(saved);
    }
}
```

### 4. Adapter에서 Infrastructure 예외 변환
```java
// adapter/out/persistence/policy/UploadPolicyPersistenceAdapter.java
@Component
public class UploadPolicyPersistenceAdapter implements UpdateUploadPolicyPort {

    private final PolicyJpaRepository repository;

    @Override
    public UploadPolicy update(UploadPolicy policy) {
        try {
            PolicyEntity entity = PolicyEntity.from(policy);
            PolicyEntity updated = repository.save(entity);
            return updated.toDomain();
        } catch (DataIntegrityViolationException e) {
            throw new PolicyAlreadyExistsException(
                policy.getPolicyKey().getValue(),
                e
            );
        }
    }
}
```

### 5. GlobalExceptionHandler에서 처리
```java
// adapter/in/web/exception/GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePolicyNotFound(
            PolicyNotFoundException e
    ) {
        log.warn("정책 조회 실패: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of("POLICY_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(InvalidPolicyStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(
            InvalidPolicyStateException e
    ) {
        log.warn("정책 상태 오류: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of("INVALID_POLICY_STATE", e.getMessage()));
    }
}
```

---

## ✅ 체크리스트

### Domain Exception 작성 시
- [ ] `BusinessException`을 상속하는가?
- [ ] 언체크 예외(RuntimeException)인가?
- [ ] Aggregate별 exception 패키지에 위치하는가?
- [ ] 명확한 예외 메시지를 포함하는가?
- [ ] 필요한 컨텍스트 정보(ID, 상태 등)를 포함하는가?

### Application Service 작성 시
- [ ] Domain 예외를 그대로 전파하는가?
- [ ] 불필요한 try-catch가 없는가?
- [ ] Application 예외를 만들지 않았는가? (정말 필요한 경우가 아니라면)

### Adapter 작성 시
- [ ] Infrastructure 예외를 Domain 예외로 변환하는가?
- [ ] 적절한 Domain 예외를 선택했는가?
- [ ] 원인 예외(cause)를 함께 전달하는가?

### GlobalExceptionHandler 작성 시
- [ ] 적절한 HTTP Status를 반환하는가?
- [ ] 일관된 에러 응답 형식을 사용하는가?
- [ ] 적절한 로그 레벨(WARN/ERROR)을 사용하는가?
- [ ] 사용자에게 유의미한 메시지를 제공하는가?

---

## 📚 참고 문서

- [CODING_STANDARDS.md](CODING_STANDARDS.md) - 전체 코딩 표준
- [DDD_AGGREGATE_MIGRATION_GUIDE.md](DDD_AGGREGATE_MIGRATION_GUIDE.md) - Aggregate 설계 가이드
- [JAVA_RECORD_GUIDE.md](JAVA_RECORD_GUIDE.md) - Record 사용 가이드
- [DTO_PATTERNS_GUIDE.md](DTO_PATTERNS_GUIDE.md) - DTO 변환 패턴 가이드 (예정)

---

## 🔍 자주 묻는 질문

### Q1: 체크 예외(Checked Exception)는 왜 사용하지 않나요?

**A**: 다음 이유로 모든 예외를 언체크 예외로 통일합니다:
1. Spring `@Transactional`이 기본적으로 언체크 예외만 롤백
2. 메서드 시그니처가 깔끔해짐 (throws 불필요)
3. 현대적 언어 트렌드 (Kotlin, Rust 등은 체크 예외 없음)
4. 주요 프레임워크(Spring, Hibernate)도 언체크 예외 사용

### Q2: Application Layer에 예외를 만들어도 되나요?

**A**: 거의 사용하지 않습니다. Domain Exception으로 충분한 경우가 대부분입니다. 정말 Application 레이어에서만 의미있는 예외(예: 외부 서비스 호출 실패)인 경우에만 제한적으로 사용합니다.

### Q3: Infrastructure 예외를 그대로 전파하면 안 되나요?

**A**: 안 됩니다. Infrastructure 예외(예: `DataIntegrityViolationException`, `HttpClientErrorException`)를 Domain 예외로 변환해야 합니다. 그래야 Domain이 인프라 기술에 의존하지 않고, 기술 변경 시 영향을 최소화할 수 있습니다.

### Q4: 예외 메시지는 어떻게 작성해야 하나요?

**A**: 다음 원칙을 따릅니다:
- 문제 상황을 명확히 설명
- 컨텍스트 정보 포함 (ID, 상태 등)
- 간결하고 명확하게 (1-2문장)
- 사용자에게 유의미한 정보 제공

### Q5: GlobalExceptionHandler에서 모든 예외를 처리해야 하나요?

**A**: 가능한 모든 Domain/Application 예외에 대한 핸들러를 작성하고, 마지막에 `Exception.class`를 처리하는 핸들러로 예상치 못한 예외를 잡습니다. 이렇게 하면 일관된 에러 응답을 보장할 수 있습니다.
