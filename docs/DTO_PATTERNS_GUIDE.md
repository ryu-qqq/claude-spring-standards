# 📦 DTO Conversion Patterns Guide

Hexagonal Architecture 기반 Spring Boot 프로젝트의 DTO 변환 패턴 및 Validation 전략 가이드입니다.

---

## 📐 핵심 원칙

### 1. 일관된 변환 메서드 네이밍

**이유**:
- 코드 탐색 시 혼란 방지 (IDE 자동완성에서 일관된 패턴)
- 신입 개발자 온보딩 용이
- API 설계의 일관성

### 2. 계층별 Validation 책임 분리

**이유**:
- 각 계층의 책임이 명확
- Domain은 항상 유효한 상태 보장 (Invariant)
- DTO는 단순 전달 역할에 집중

### 3. Record 기반 DTO 사용

**이유**:
- 불변성 보장
- 간결한 코드
- Pattern Matching 지원 (Java 17+)

---

## 🔄 DTO Conversion Naming Conventions

### 표준 네이밍 규칙

| 변환 방향 | 메서드 | 위치 | 예시 |
|----------|--------|------|------|
| Domain → DTO | `static from(Domain)` | DTO 클래스 | `PolicyKeyDto.from(policyKey)` |
| DTO → Domain | `toDomain()` | DTO 클래스 | `dto.toDomain()` |
| Request → Command | `toCommand()` | Request 클래스 | `request.toCommand()` |
| Response ← DTO | `static from(DTO)` | Response 클래스 | `PolicyResponse.from(dto)` |
| Domain → Entity | `static from(Domain)` | Entity 클래스 | `PolicyEntity.from(domain)` |
| Entity → Domain | `toDomain()` | Entity 클래스 | `entity.toDomain()` |

### 핵심 원칙

✅ **from()**: 정적 팩토리 메서드, 외부 객체를 받아서 생성
```java
// ✅ Good
public static PolicyKeyDto from(PolicyKey domain) {
    return new PolicyKeyDto(
        domain.getTenantId(),
        domain.getUserType(),
        domain.getServiceType()
    );
}
```

✅ **toXxx()**: 인스턴스 메서드, 자기 자신을 다른 타입으로 변환
```java
// ✅ Good
public PolicyKey toDomain() {
    return PolicyKey.of(tenantId, userType, serviceType);
}

public CreateUploadPolicyCommand toCommand() {
    return new CreateUploadPolicyCommand(
        new PolicyKeyDto(tenantId, userType, serviceType)
    );
}
```

❌ **of()**: 변환이 아닌 생성에만 사용
```java
// ❌ Bad: 변환에 of() 사용
public static PolicyKeyDto of(PolicyKey domain) { ... }

// ✅ Good: 생성에 of() 사용
public static PolicyKey of(String tenant, String user, String service) { ... }
```

❌ **asXxx()**: 타입 캐스팅/뷰 변경에만 사용
```java
// ❌ Bad: 변환에 as() 사용
public PolicyKey asDomain() { ... }

// ✅ Good: 뷰 변경에 as() 사용
public List<String> asList() { ... }
```

---

## 🎯 Layer-by-Layer Conversion Patterns

### 1. Domain ↔ DTO (Application Layer)

#### Domain → DTO
```java
// Application DTO
public record PolicyKeyDto(
    String tenantId,
    String userType,
    String serviceType
) {
    /**
     * Domain → DTO 변환
     */
    public static PolicyKeyDto from(PolicyKey domain) {
        return new PolicyKeyDto(
            domain.getTenantId(),
            domain.getUserType(),
            domain.getServiceType()
        );
    }

    /**
     * DTO → Domain 변환
     */
    public PolicyKey toDomain() {
        return PolicyKey.of(tenantId, userType, serviceType);
    }
}
```

#### 사용 예시
```java
@UseCase
@Transactional
public class CreateUploadPolicyService implements CreateUploadPolicyUseCase {

    @Override
    public UploadPolicyResponse createPolicy(CreateUploadPolicyCommand command) {
        // DTO → Domain
        PolicyKey policyKey = command.policyKey().toDomain();

        UploadPolicy policy = UploadPolicy.create(policyKey, /* ... */);
        UploadPolicy saved = savePort.save(policy);

        // Domain → DTO
        return UploadPolicyResponse.from(saved);
    }
}
```

### 2. Request → Command (Web → Application)

```java
// Web Request
public record CreatePolicyRequest(
    @NotBlank(message = "tenantId는 필수입니다")
    String tenantId,

    @NotBlank(message = "userType은 필수입니다")
    String userType,

    @NotBlank(message = "serviceType는 필수입니다")
    String serviceType
) {
    /**
     * Request → Command 변환
     */
    public CreateUploadPolicyCommand toCommand() {
        return new CreateUploadPolicyCommand(
            new PolicyKeyDto(tenantId, userType, serviceType)
        );
    }
}

// Controller
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final CreateUploadPolicyUseCase useCase;

    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(
        @Valid @RequestBody CreatePolicyRequest request
    ) {
        // ✅ Request → Command
        CreateUploadPolicyCommand command = request.toCommand();
        UploadPolicyResponse response = useCase.createPolicy(command);

        return ResponseEntity.ok(PolicyResponse.from(response));
    }
}
```

### 3. Response ← DTO (Application → Web)

```java
// Web Response
public record PolicyResponse(
    String policyKey,
    boolean active,
    LocalDateTime createdAt
) {
    /**
     * Application DTO → Web Response 변환
     */
    public static PolicyResponse from(UploadPolicyResponse dto) {
        return new PolicyResponse(
            dto.policyKey(),
            dto.isActive(),
            dto.createdAt()
        );
    }
}

// Controller
@GetMapping("/{policyKey}")
public ResponseEntity<PolicyResponse> getPolicy(@PathVariable String policyKey) {
    UploadPolicyResponse dto = useCase.getPolicy(policyKey);

    // ✅ DTO → Response
    return ResponseEntity.ok(PolicyResponse.from(dto));
}
```

### 4. Domain ↔ Entity (Domain → Persistence)

```java
// Persistence Entity
@Entity
@Table(name = "upload_policy")
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String policyKey;

    @Column(nullable = false)
    private boolean active;

    /**
     * Domain → Entity 변환
     */
    public static PolicyEntity from(UploadPolicy domain) {
        PolicyEntity entity = new PolicyEntity();
        entity.policyKey = domain.getPolicyKey().getValue();
        entity.active = domain.isActive();
        return entity;
    }

    /**
     * Entity → Domain 변환
     */
    public UploadPolicy toDomain() {
        return UploadPolicy.of(
            PolicyKey.from(policyKey),
            active
        );
    }
}

// Persistence Adapter
@Component
public class UploadPolicyPersistenceAdapter implements SaveUploadPolicyPort {

    private final PolicyJpaRepository repository;

    @Override
    public UploadPolicy save(UploadPolicy domain) {
        // ✅ Domain → Entity
        PolicyEntity entity = PolicyEntity.from(domain);
        PolicyEntity saved = repository.save(entity);

        // ✅ Entity → Domain
        return saved.toDomain();
    }
}
```

---

## ✅ Validation Strategy

### 계층별 Validation 책임

```
Web Layer → Application Layer → Domain Layer
   ①              ②                 ③
```

1. **① Web Layer (Request)**: 형식 검증만 (Bean Validation)
2. **② Application Layer (Command/Query DTO)**: 검증 없음, 단순 전달
3. **③ Domain Layer**: 비즈니스 규칙 검증

### 1. Web Layer: Bean Validation

```java
// ✅ Request에서 형식 검증 (Bean Validation)
public record CreatePolicyRequest(
    @NotBlank(message = "tenantId는 필수입니다")
    @Size(min = 1, max = 50, message = "tenantId는 1-50자여야 합니다")
    String tenantId,

    @NotBlank(message = "userType은 필수입니다")
    String userType,

    @NotBlank(message = "serviceType는 필수입니다")
    String serviceType,

    @NotNull(message = "이미지 정책은 필수입니다")
    @Valid  // 중첩된 객체 검증
    ImagePolicyRequest imagePolicy
) {
    public CreateUploadPolicyCommand toCommand() {
        return new CreateUploadPolicyCommand(
            new PolicyKeyDto(tenantId, userType, serviceType),
            imagePolicy.toDto()
        );
    }
}

// Controller에서 @Valid 사용
@PostMapping("/policies")
public ResponseEntity<PolicyResponse> createPolicy(
    @Valid @RequestBody CreatePolicyRequest request  // ✅ 형식 검증
) {
    CreateUploadPolicyCommand command = request.toCommand();
    UploadPolicyResponse response = useCase.createPolicy(command);
    return ResponseEntity.ok(PolicyResponse.from(response));
}
```

### 2. Application Layer: 검증 없음

```java
// ✅ Application Command/Query DTO: 검증 없음, 단순 전달
public record PolicyKeyDto(
    String tenantId,
    String userType,
    String serviceType
) {
    // ❌ Compact constructor 검증 불필요

    // ✅ Domain 생성 시 검증 위임
    public PolicyKey toDomain() {
        return PolicyKey.of(tenantId, userType, serviceType);  // 여기서 검증
    }
}

public record CreateUploadPolicyCommand(
    PolicyKeyDto policyKey,
    ImagePolicyDto imagePolicy
) {
    // ❌ 검증 없음
}
```

### 3. Domain Layer: 비즈니스 규칙 검증

```java
// ✅ Domain에서 비즈니스 규칙 검증
public class PolicyKey {

    private final String tenantId;
    private final String userType;
    private final String serviceType;

    private PolicyKey(String tenantId, String userType, String serviceType) {
        this.tenantId = tenantId;
        this.userType = userType;
        this.serviceType = serviceType;
    }

    public static PolicyKey of(String tenantId, String userType, String serviceType) {
        // ✅ 여기서 검증
        validateNotBlank(tenantId, "tenantId");
        validateNotBlank(userType, "userType");
        validateNotBlank(serviceType, "serviceType");

        return new PolicyKey(tenantId, userType, serviceType);
    }

    private static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                fieldName + " must not be null or blank"
            );
        }
    }

    public String getValue() {
        return String.format("%s/%s/%s", tenantId, userType, serviceType);
    }
}
```

### Validation 계층별 비교

| 계층 | Validation 종류 | 방법 | 예시 |
|------|----------------|------|------|
| **Web** | 형식 검증 | Bean Validation (`@NotBlank`, `@Valid`) | Request 필드 검증 |
| **Application** | ❌ 없음 | 단순 전달 | Command/Query DTO |
| **Domain** | 비즈니스 규칙 | 정적 팩토리 메서드 | `PolicyKey.of()` |

**장점**:
- 각 계층의 책임이 명확
- Domain은 항상 유효한 상태 보장 (Invariant)
- DTO는 단순 전달 역할에 집중
- 테스트하기 쉬움

---

## 🔄 Complete Example: Policy Creation Flow

### 1. Web Request (형식 검증)
```java
// adapter/in/web/policy/request/CreatePolicyRequest.java
public record CreatePolicyRequest(
    @NotBlank(message = "tenantId는 필수입니다")
    String tenantId,

    @NotBlank(message = "userType은 필수입니다")
    String userType,

    @NotBlank(message = "serviceType는 필수입니다")
    String serviceType,

    @NotNull(message = "이미지 정책은 필수입니다")
    @Valid
    ImagePolicyRequest imagePolicy
) {
    /**
     * Request → Command 변환
     */
    public CreateUploadPolicyCommand toCommand() {
        return new CreateUploadPolicyCommand(
            new PolicyKeyDto(tenantId, userType, serviceType),
            imagePolicy.toDto()
        );
    }
}

public record ImagePolicyRequest(
    @NotNull @Min(1) @Max(10) Integer maxCount,
    @NotNull @Min(1) @Max(100) Long maxSizeMb,
    @NotEmpty List<String> allowedFormats
) {
    public ImagePolicyDto toDto() {
        return new ImagePolicyDto(maxCount, maxSizeMb, allowedFormats);
    }
}
```

### 2. Application Command (검증 없음)
```java
// application/policy/dto/CreateUploadPolicyCommand.java
public record CreateUploadPolicyCommand(
    PolicyKeyDto policyKey,
    ImagePolicyDto imagePolicy
) {
    // ❌ 검증 없음
}

public record PolicyKeyDto(
    String tenantId,
    String userType,
    String serviceType
) {
    /**
     * DTO → Domain 변환 (Domain에서 검증)
     */
    public PolicyKey toDomain() {
        return PolicyKey.of(tenantId, userType, serviceType);
    }
}

public record ImagePolicyDto(
    Integer maxCount,
    Long maxSizeMb,
    List<String> allowedFormats
) {
    /**
     * DTO → Domain 변환 (Domain에서 검증)
     */
    public ImagePolicy toDomain() {
        return ImagePolicy.of(maxCount, maxSizeMb, allowedFormats);
    }
}
```

### 3. Domain (비즈니스 규칙 검증)
```java
// domain/policy/PolicyKey.java
public class PolicyKey {

    private final String tenantId;
    private final String userType;
    private final String serviceType;

    private PolicyKey(String tenantId, String userType, String serviceType) {
        this.tenantId = tenantId;
        this.userType = userType;
        this.serviceType = serviceType;
    }

    public static PolicyKey of(String tenantId, String userType, String serviceType) {
        // ✅ 비즈니스 규칙 검증
        validateNotBlank(tenantId, "tenantId");
        validateNotBlank(userType, "userType");
        validateNotBlank(serviceType, "serviceType");

        return new PolicyKey(tenantId, userType, serviceType);
    }

    private static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                fieldName + " must not be null or blank"
            );
        }
    }

    public String getValue() {
        return String.format("%s/%s/%s", tenantId, userType, serviceType);
    }
}

// domain/policy/ImagePolicy.java
public class ImagePolicy {

    private final Integer maxCount;
    private final Long maxSizeMb;
    private final List<String> allowedFormats;

    private ImagePolicy(Integer maxCount, Long maxSizeMb, List<String> allowedFormats) {
        this.maxCount = maxCount;
        this.maxSizeMb = maxSizeMb;
        this.allowedFormats = List.copyOf(allowedFormats);  // 불변 리스트
    }

    public static ImagePolicy of(Integer maxCount, Long maxSizeMb, List<String> allowedFormats) {
        // ✅ 비즈니스 규칙 검증
        validateMaxCount(maxCount);
        validateMaxSizeMb(maxSizeMb);
        validateAllowedFormats(allowedFormats);

        return new ImagePolicy(maxCount, maxSizeMb, allowedFormats);
    }

    private static void validateMaxCount(Integer maxCount) {
        if (maxCount == null || maxCount < 1 || maxCount > 10) {
            throw new IllegalArgumentException(
                "maxCount must be between 1 and 10"
            );
        }
    }

    private static void validateMaxSizeMb(Long maxSizeMb) {
        if (maxSizeMb == null || maxSizeMb < 1 || maxSizeMb > 100) {
            throw new IllegalArgumentException(
                "maxSizeMb must be between 1 and 100"
            );
        }
    }

    private static void validateAllowedFormats(List<String> formats) {
        if (formats == null || formats.isEmpty()) {
            throw new IllegalArgumentException(
                "allowedFormats must not be empty"
            );
        }
    }
}
```

### 4. Complete Flow
```java
// ① Web Controller (형식 검증)
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final CreateUploadPolicyUseCase useCase;

    @PostMapping
    public ResponseEntity<PolicyResponse> createPolicy(
        @Valid @RequestBody CreatePolicyRequest request  // ① 형식 검증
    ) {
        // ② Request → Command (검증 없음)
        CreateUploadPolicyCommand command = request.toCommand();

        // ③ UseCase 실행 (Domain 검증)
        UploadPolicyResponse response = useCase.createPolicy(command);

        // ④ Response 반환
        return ResponseEntity.ok(PolicyResponse.from(response));
    }
}

// ② Application Service
@UseCase
@Transactional
public class CreateUploadPolicyService implements CreateUploadPolicyUseCase {

    private final SaveUploadPolicyPort savePort;

    @Override
    public UploadPolicyResponse createPolicy(CreateUploadPolicyCommand command) {
        // ③ Command → Domain (Domain에서 검증)
        PolicyKey policyKey = command.policyKey().toDomain();  // 여기서 검증!
        ImagePolicy imagePolicy = command.imagePolicy().toDomain();  // 여기서 검증!

        UploadPolicy policy = UploadPolicy.create(policyKey, imagePolicy);
        UploadPolicy saved = savePort.save(policy);

        return UploadPolicyResponse.from(saved);
    }
}
```

---

## ❌ Common Mistakes

### 1. 변환 메서드 네이밍 혼재

```java
// ❌ Bad: 네이밍 일관성 없음
PolicyKeyDto.from(PolicyKey domain)      // from
PolicyKeyDto.of(PolicyKey domain)        // of (혼재)
PolicyKeyDto.fromDomain(PolicyKey domain) // fromDomain (장황)

policyKeyDto.toDomain()                  // toDomain
policyKeyDto.convert()                   // convert (모호)
policyKeyDto.asDomain()                  // asDomain (부적절)

// ✅ Good: 일관된 네이밍
PolicyKeyDto.from(PolicyKey domain)      // from (정적 팩토리)
policyKeyDto.toDomain()                  // toDomain (변환)
```

### 2. Compact Constructor에서 검증

```java
// ❌ Bad: DTO에서 검증
public record PolicyKeyDto(String tenantId, String userType, String serviceType) {
    public PolicyKeyDto {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId must not be blank");
        }
        if (userType == null || userType.isBlank()) {
            throw new IllegalArgumentException("userType must not be blank");
        }
        if (serviceType == null || serviceType.isBlank()) {
            throw new IllegalArgumentException("serviceType must not be blank");
        }
    }
}

// ✅ Good: Domain에서 검증
public record PolicyKeyDto(String tenantId, String userType, String serviceType) {
    // 검증 없음

    public PolicyKey toDomain() {
        return PolicyKey.of(tenantId, userType, serviceType);  // Domain에서 검증
    }
}
```

### 3. Application Layer에서 검증

```java
// ❌ Bad: Application Service에서 검증
@UseCase
@Transactional
public class CreateUploadPolicyService {
    public UploadPolicyResponse createPolicy(CreateUploadPolicyCommand command) {
        // ❌ Application에서 검증
        if (command.policyKey().tenantId() == null) {
            throw new IllegalArgumentException("tenantId required");
        }

        PolicyKey policyKey = PolicyKey.of(/* ... */);
        // ...
    }
}

// ✅ Good: Domain에서 검증
@UseCase
@Transactional
public class CreateUploadPolicyService {
    public UploadPolicyResponse createPolicy(CreateUploadPolicyCommand command) {
        // ✅ Domain 생성 시 검증
        PolicyKey policyKey = command.policyKey().toDomain();  // 여기서 검증
        // ...
    }
}
```

---

## ✅ 체크리스트

### DTO 작성 시
- [ ] Record 기반으로 작성했는가?
- [ ] 변환 메서드 네이밍이 일관되는가? (`from()` / `toXxx()`)
- [ ] Compact Constructor에 검증 로직이 없는가?
- [ ] toDomain() 메서드가 Domain 생성을 위임하는가?

### Web Request 작성 시
- [ ] Bean Validation 애노테이션을 사용했는가? (`@NotBlank`, `@Valid`)
- [ ] toCommand() 메서드를 제공하는가?
- [ ] Controller에서 `@Valid`를 사용하는가?

### Application DTO 작성 시
- [ ] 검증 로직이 없는가?
- [ ] 단순 전달 역할만 하는가?
- [ ] toDomain() 메서드가 Domain 검증을 위임하는가?

### Domain 작성 시
- [ ] 정적 팩토리 메서드(`of()`)에서 검증하는가?
- [ ] 비즈니스 규칙을 검증하는가?
- [ ] 항상 유효한 상태를 보장하는가? (Invariant)

---

## 📚 참고 문서

- [CODING_STANDARDS.md](CODING_STANDARDS.md) - 전체 코딩 표준
- [JAVA_RECORD_GUIDE.md](JAVA_RECORD_GUIDE.md) - Record 사용 가이드
- [EXCEPTION_HANDLING_GUIDE.md](EXCEPTION_HANDLING_GUIDE.md) - 예외 처리 가이드
- [DDD_AGGREGATE_MIGRATION_GUIDE.md](DDD_AGGREGATE_MIGRATION_GUIDE.md) - Aggregate 설계 가이드

---

## 🔍 자주 묻는 질문

### Q1: of()와 from()의 차이는 무엇인가요?

**A**:
- **of()**: 생성 메서드. 원시 타입이나 간단한 값으로 객체 생성
  ```java
  PolicyKey.of(String tenant, String user, String service)
  ```
- **from()**: 변환 메서드. 다른 객체로부터 변환하여 생성
  ```java
  PolicyKeyDto.from(PolicyKey domain)
  ```

### Q2: DTO에서 Compact Constructor로 검증하면 안 되나요?

**A**: 권장하지 않습니다. DTO는 단순 전달 객체이고, 비즈니스 규칙 검증은 Domain의 책임입니다. DTO에서 검증하면:
- 계층 간 책임이 불명확해짐
- Domain이 항상 유효함을 보장할 수 없음
- 테스트하기 어려워짐

### Q3: Web Request에서 Bean Validation만으로는 부족한 경우는?

**A**: Bean Validation은 형식 검증(필수 여부, 길이, 패턴)만 담당합니다. 비즈니스 규칙(예: "활성 정책은 수정 불가")은 Domain에서 검증해야 합니다.

### Q4: Application DTO와 Domain DTO를 분리해야 하나요?

**A**: 네, 분리합니다:
- **Application DTO**: 레이어 간 데이터 전달 (Command, Query, Response)
- **Domain DTO**: 없음. Domain은 순수 비즈니스 객체만 포함

### Q5: 모든 변환 메서드에 from/to 패턴을 적용해야 하나요?

**A**: 네, 일관성이 중요합니다. 예외를 두면 코드베이스 전체가 혼란스러워집니다. 팀 내에서 합의한 네이밍 컨벤션을 엄격히 지켜야 합니다.
