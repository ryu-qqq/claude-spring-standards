# 🧩 도메인 주도 헥사고날 + CQRS 아키텍처 템플릿

## 0. 일반 원칙
- 아키텍처 스타일: **DDD (Domain-Driven Design) + 헥사고날 아키텍처 (Ports & Adapters) + CQRS.**
- 의존성 방향은 항상 **밖 → 안** (adapter → application → domain).
- Application 서비스끼리 **직접 호출 금지**.
  - Command UseCase가 조회가 필요할 때는 **QueryPort** 사용.
- 각 클래스는 단일 책임 원칙(SRP)을 따른다.
- Domain 계층은 **순수 자바 코드**만 사용 (Spring, JPA 등 의존 금지).

---

## 1. 패키지 구조 (템플릿)
```
com.company.project
├─ adapter
│  ├─ in
│  │  └─ rest
│  │     ├─ controller/
│  │     ├─ dto/
│  │     │  ├─ request/
│  │     │  └─ response/
│  │     └─ mapper/
│  └─ out
│     ├─ persistence/
│     ├─ messaging/
│     └─ external/
│
├─ application
│  ├─ [context]/
│  │  ├─ dto/
│  │  ├─ port/
│  │  │  ├─ in/          # UseCase 인터페이스
│  │  │  └─ out/         # 외부 의존 (DB, API 등)
│  │  ├─ assembler/      # Command → Domain 변환기
│  │  └─ service/
│  │     ├─ command/     # 쓰기 UseCase 구현 (@Transactional)
│  │     └─ query/       # 읽기 UseCase 구현 (@Transactional(readOnly=true))
│
└─ domain
   ├─ shared/
   │  ├─ id/
   │  ├─ vo/
   │  ├─ event/
   │  ├─ exception/
   │  └─ policy/
   └─ [boundedContext]/
      ├─ [aggregateName]/
      │  ├─ [AggregateRoot].java
      │  ├─ vo/
      │  ├─ event/
      │  ├─ exception/
      │  ├─ policy/
      │  ├─ service/
      │  ├─ repository/
      │  └─ factory/
```

---

## 2. REST 어댑터 규칙
- 엔드포인트는 RESTful 원칙을 따른다.
- DTO는 `adapter.in.rest.dto` 아래에 둔다.
- DTO ↔ Command ↔ Domain 변환 위치:
  - DTO ↔ Command → `adapter.in.rest.mapper.*`
  - Command ↔ Domain → `application.[context].assembler.*`
- 검증 규칙:
  - DTO에서만 `@Valid` 사용.
  - 변환/포맷 오류 → **400 Bad Request**
  - 정책 위반 → **422 Unprocessable Entity**
  - 리소스 없음 → **404 Not Found**
  - 멱등성/충돌 → **409 Conflict**

---

## 3. Application 계층 (UseCase & Service)
### Command 사이드
- 쓰기 UseCase (생성, 수정, 완료 등) 구현
- `@Transactional`
- Outbound Port(Repository, API 등)만 호출
- Assembler로 도메인 객체 생성
- 트랜잭션 커밋 후 도메인 이벤트 발행 (Outbox 사용 권장)

### Query 사이드
- 조회/검색/상태 조회 등
- `@Transactional(readOnly = true)`
- QueryPort를 통해 데이터 읽기 (캐시, Read Replica 사용 가능)

### CQRS 명명 규칙
| 타입 | 인터페이스 | 구현체 | 포트(out) |
|------|-------------|----------|------------|
| 쓰기 | `CreateXxxUseCase`, `UpdateXxxUseCase` | `XxxCommandService` | `XxxCommandPort` |
| 읽기 | `GetXxxUseCase`, `FindXxxUseCase` | `XxxQueryService` | `XxxQueryPort` |

---

## 4. Ports & Adapters
- **Port-In**: UseCase 인터페이스
- **Port-Out**: 외부 의존 인터페이스 (Repository, API 등)
- **Adapter-Out**: Port-Out 구현체 (JPA, Redis, HTTP Client 등)
- **Adapter-In**: REST, 메시지 컨슈머, 스케줄러 등 진입점

---

## 5. 예외 및 에러 처리 규칙

### 기본 예외 구조
```java
public class BaseException extends RuntimeException {
    private final ErrorCategory category;
    private final ErrorCode code;
    private final Map<String, Object> details;
}
```

#### ErrorCategory → HTTP 매핑
| Category | HTTP | 예시 |
|-----------|------|------|
| BAD_REQUEST | 400 | 포맷/검증 오류 |
| NOT_FOUND | 404 | 리소스 없음 |
| CONFLICT | 409 | 멱등성 충돌 |
| UNPROCESSABLE | 422 | 비즈니스 규칙 위반 |
| INTERNAL | 500 | 내부 오류 |

- 각 도메인 예외는 `BaseException` 상속.
- `@RestControllerAdvice` 에서 HTTP 응답으로 변환:
  ```json
  {
    "code": "POLICY_VIOLATION",
    "message": "파일 크기가 허용 범위를 초과했습니다.",
    "path": "/api/v1/upload/sessions",
    "traceId": "abc123",
    "timestamp": "2025-10-16T16:20:00Z"
  }
  ```

---

## 6. VO(Value Object) 및 Enum 규칙

### VO 정의 기준
- 식별자 없음 (값으로 동일성 판단)
- 불변 (Immutable)
- 검증 또는 동작(행동)을 포함
- 단위, 범위, 포맷을 나타냄

**예시:**  
`PolicyKey`, `IdempotencyKey`, `FileName`, `ContentType`, `FileSize`, `Checksum`, `ETag`, `Url`, `Money`, `Period`

**규칙:**
- Java 17+에서는 `record` 권장
- 정적 팩토리: `of(...)`, `parse(...)`
- 생성 시 검증 수행
- 잘못된 값은 도메인 예외 발생

### Enum
- Enum은 VO가 아님 (유한한 분류값)
- 위치: `domain.[context].vo` 또는 `domain.[context].type`
- 예시: `FileType`, `UploadStatus`, `ChecksumAlgorithm`

---

## 7. 도메인 계층 규칙
- **Aggregate Root**가 트랜잭션 경계를 정의
- Entity, VO는 Aggregate 하위에 위치
- 생성은 팩토리/정적 메서드에서 통제
- **Domain Service**는 순수 로직만 포함 (I/O 없음)
- **Event**는 상태 전이 시 불변 객체로 발행
- **Repository**는 인터페이스로만 존재 (구현은 Adapter-Out)

---

## 8. Assembler (Command → Domain 변환기)
```java
@Component
public class CreateOrderAssembler {
    public Order toDomain(CreateOrderCommand cmd) {
        var id = OrderId.generate();
        var customer = CustomerId.of(cmd.customerId());
        var items = cmd.items().stream().map(ItemVO::of).toList();
        return Order.create(id, customer, items);
    }
}
```

- Application 계층에 위치
- 외부 DTO와 Domain 사이의 변환 책임을 분리

---

## 9. 트랜잭션 및 외부 호출
- 외부 I/O(HTTP, S3, Kafka 등)는 트랜잭션 밖에서 수행
- DB 트랜잭션은 짧고 Aggregate 단위로 유지
- 일반 플로우: 저장 → 커밋 → 이벤트 발행

---

## 10. 네이밍 규칙
| 개념 | 명명 규칙 | 예시 |
|------|------------|------|
| UseCase | 동사 + Aggregate + `UseCase` | `CreateUserUseCase` |
| Service Impl | Aggregate + Command/Query + `Service` | `UserCommandService` |
| Port-Out | Aggregate + Command/Query + `Port` | `UserCommandPort` |
| Adapter-Out | 기술명 + Adapter | `JpaUserRepositoryAdapter` |
| DTO | 명사 + `Request` / `Response` | `CreateUserRequest` |
| Mapper | `XxxDtoMapper` | `UserDtoMapper` |
| VO | 명사 (record) | `UserEmail`, `Money` |
| Enum | PascalCase | `UserStatus`, `OrderType` |

---

## 11. 테스트 원칙
- **Domain**: 순수 단위 테스트 (mock 금지)
- **Application**: outbound port mock 사용
- **Adapter**: 실제 인프라 통합 테스트 (DB, S3 등)
- `@SpringBootTest`는 end-to-end 테스트에만 사용

---

## 12. Command/Query 처리 흐름 예시
1. Controller: 요청 수신 → DTO → Command
2. Mapper: Request DTO → Command
3. Service(Command): 정책 검증 (PolicyValidationPort)
4. Assembler: Command → Domain 객체
5. Domain: 불변성 검증 및 Aggregate 생성
6. CommandPort.save(aggregate): 저장
7. Response DTO 반환

---

## ✅ 최소 체크리스트
- [ ] Adapter / Application / Domain 3계층 구조 생성
- [ ] CQRS 패키지 분리 (command / query)
- [ ] UseCase 인터페이스 정의
- [ ] Command/Query Service 구현
- [ ] Mapper 및 Assembler 생성
- [ ] BaseException + ErrorCategory + GlobalExceptionHandler 구현
- [ ] VO 및 Enum 규칙 준수
- [ ] Domain 계층은 순수 자바 코드 유지
- [ ] Controller는 표준 ErrorResponse 반환
- [ ] 트랜잭션 및 외부 호출 경계 명확히 유지
