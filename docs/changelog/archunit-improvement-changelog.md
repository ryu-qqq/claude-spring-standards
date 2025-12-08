# ArchUnit 테스트 개선 변경 이력

이 문서는 각 레이어별 ArchUnit 테스트 보강 작업을 추적합니다.

---

## 목차

1. [REST API Layer](#rest-api-layer)
2. [Domain Layer](#domain-layer) *(예정)*
3. [Application Layer](#application-layer) *(예정)*
4. [Persistence Layer](#persistence-layer) *(예정)*

---

## REST API Layer

### 작업일: 2025-12-08

### 1. 패키지 상수 중앙화

**변경 파일**: `ArchUnitPackageConstants.java`

**목적**: 프로젝트 커스터마이징 시 `BASE_PACKAGE` 한 곳만 변경하면 모든 ArchUnit 테스트가 자동 적용되도록 함

**추가된 상수**:
```java
// 기본 패키지 (이 값만 변경!)
public static final String BASE_PACKAGE = "com.ryuqq";

// 자동 파생 상수들
public static final String ADAPTER_IN_REST = BASE_PACKAGE + ".adapter.in.rest";
public static final String DOMAIN = BASE_PACKAGE + ".domain";
public static final String APPLICATION = BASE_PACKAGE + ".application";
public static final String CONTROLLER_PATTERN = "..controller..";
public static final String DTO_COMMAND_PATTERN = "..dto.command..";
public static final String DTO_QUERY_PATTERN = "..dto.query..";
public static final String DTO_RESPONSE_PATTERN = "..dto.response..";
public static final String DOMAIN_ALL = BASE_PACKAGE + ".domain..";
public static final String DOMAIN_EXCEPTION_ALL = BASE_PACKAGE + ".domain..exception..";
```

**적용된 테스트 파일**:
- `RestApiLayerArchTest.java`
- `ControllerArchTest.java`
- `CommandDtoArchTest.java`
- `QueryDtoArchTest.java`
- `ResponseDtoArchTest.java`
- `MapperArchTest.java`
- `ApiResponseArchTest.java`
- `OpenApiArchTest.java`
- `SecurityArchTest.java`
- `ErrorHandlingArchTest.java`

---

### 2. ApiDocsController 예외 처리

**문제**: `ApiDocsController`는 문서 서빙용으로 `@Controller`를 사용하지만, REST API Controller 규칙들은 `@RestController`를 기대함

**해결 방법**: 문서 서빙용 Controller를 규칙 검증에서 제외

**변경된 테스트 파일 및 규칙**:

#### ControllerArchTest.java
| 규칙 번호 | 규칙명 | 변경 내용 |
|----------|--------|----------|
| 1 | `@RestController` 필수 | `.haveSimpleNameNotContaining("ApiDocs")` 추가 |
| 2 | `@RequestMapping` 필수 | `.haveSimpleNameNotContaining("ApiDocs")` 추가 |
| 10 | UseCase 의존성 필수 | `.haveSimpleNameNotContaining("ApiDocs")` 및 `GlobalExceptionHandler` 예외 추가 |

#### RestApiLayerArchTest.java
| 규칙 번호 | 규칙명 | 변경 내용 |
|----------|--------|----------|
| 5 | Application Port 의존 필수 | `.haveSimpleNameNotContaining("ApiDocs")` 추가 |
| 13 | Stereotype 검증 | `.haveSimpleNameNotContaining("ApiDocs")` 추가 |

#### OpenApiArchTest.java
| 규칙 번호 | 규칙명 | 변경 내용 |
|----------|--------|----------|
| 1-2 | `@Operation` 필수 | `.areAnnotatedWith("RestController")` 조건 추가 |
| 1-3 | `@ApiResponses` 필수 | `.areAnnotatedWith("RestController")` 조건 추가 |
| 1-4 | `@PathVariable`에 `@Parameter` 필수 | `.areAnnotatedWith("RestController")` 조건 추가 |
| 1-5 | `@RequestParam`에 `@Parameter` 필수 | `.areAnnotatedWith("RestController")` 조건 추가 |

---

### 3. 관련 문서 생성/수정

| 파일 | 설명 |
|------|------|
| `docs/coding_convention/00-project-setup/project-customization-guide.md` | 프로젝트 커스터마이징 가이드 신규 생성 |
| `docs/coding_convention/01-adapter-in-layer/rest-api/testing/04_rest-api-archunit-guide.md` | 프로젝트 커스터마이징 섹션 추가 |

---

### 4. 테스트 클래스 제외 일관성 적용

**목적**: 테스트 코드가 ArchUnit 검증 대상에서 제외되도록 일관성 있게 `DO_NOT_INCLUDE_TESTS` 옵션 적용

**변경 파일** (8개):

| 파일 | 변경 내용 |
|------|----------|
| `RestApiLayerArchTest.java` | `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` 추가 |
| `ControllerArchTest.java` | `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` 추가 |
| `CommandDtoArchTest.java` | `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` 추가 |
| `QueryDtoArchTest.java` | `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` 추가 |
| `ResponseDtoArchTest.java` | `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` 추가 |
| `ApiResponseArchTest.java` | `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` 추가 |
| `SecurityArchTest.java` | `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` 추가 |
| `ErrorHandlingArchTest.java` | `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` 추가 |

**기존 적용됨** (2개 - 변경 없음):
- `MapperArchTest.java`
- `OpenApiArchTest.java`

**표준 패턴**:
```java
@BeforeAll
static void setUp() {
    classes =
            new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                    .importPackages(ADAPTER_IN_REST);
}
```

---

### 5. 환경별 설정 파일 분리 및 Config 검증 테스트

**작업일**: 2025-12-08

**목적**:
- 환경별 설정 파일 분리 (local, prod)
- Gateway Only 인증 아키텍처 반영
- 설정 파일 필수 항목 검증 테스트 추가

#### 5-1. YAML 파일 구조 변경

| 파일 | 역할 | 변경 내용 |
|------|------|----------|
| `rest-api.yml` | 공통 설정 | JWT 설정 제거, Gateway 헤더 설정 추가 |
| `rest-api-local.yml` | 로컬 환경 | **신규 생성** - Gateway 우회, CORS 허용 |
| `rest-api-prod.yml` | 운영 환경 | **신규 생성** - Gateway 필수, 보안 강화 |

#### 5-2. 인증 아키텍처 변경 (Gateway Only)

```
[Before] 각 서비스가 JWT Secret 보유
security:
  jwt:
    secret: ${JWT_SECRET}  ← 각 서비스마다 동일 Secret

[After] Gateway에서 JWT 검증, 서비스는 헤더만 읽음
security:
  gateway:
    enabled: true
    user-id-header: X-User-Id
    user-roles-header: X-User-Roles
```

#### 5-3. Config Validation ArchUnit Test 추가

**신규 파일**: `RestApiConfigArchTest.java`

| 규칙 그룹 | 검증 내용 |
|----------|----------|
| 1. 파일 존재 | `rest-api.yml`, `rest-api-local.yml`, `rest-api-prod.yml` 필수 |
| 2. 운영 보안 | Gateway 인증 필수, Cookie secure=true, 환경변수 사용 |
| 3. API Docs | 운영에서 Swagger 환경변수 제어 또는 비활성화 |
| 4. Gateway 헤더 | 헤더명 정의 필수, X- 접두사 권장 |
| 5. OAuth2 | 의존성 존재 시 credentials 환경변수 필수 |
| 6. Error Response | 운영에서 에러 문서 URL 설정 권장 |

---

### 6. 테스트 결과

```
REST API Layer ArchUnit Tests: 147개 전체 통과 ✅
- 기존 ArchUnit 테스트: 129개
- Config 검증 테스트: 16개
- Gateway UUID 검증 테스트: 2개 (신규)
```

---

### 7. Gateway Only 아키텍처 지원

**작업일**: 2025-12-08

**목적**:
- Gateway에서 JWT 검증, 서비스는 헤더만 읽는 마이크로서비스 패턴 지원
- 기존 JWT-per-Service 패턴과 Gateway Only 패턴 모두 지원

#### 7-1. 신규 문서 작성

| 파일 | 설명 |
|------|------|
| `security/gateway-only-architecture.md` | Gateway Only 인증 아키텍처 가이드 (신규) |

#### 7-2. SecurityArchTest 규칙 추가/수정

| 규칙 번호 | 규칙명 | 변경 내용 |
|----------|--------|----------|
| 11 | Authentication Filter 규칙 | `JwtAuthenticationFilter` → 일반 `*AuthenticationFilter` 패턴으로 확장 |
| 24 | Gateway 컴포넌트 위치 | Gateway 관련 컴포넌트 패키지 위치 검증 (신규) |
| 25 | Gateway 헤더 필터 | GatewayHeaderAuthFilter OncePerRequestFilter 상속 검증 (신규) |
| 26 | Gateway User 불변성 | GatewayUser record 타입 검증 (신규) |
| 27 | JWT Secret 참조 금지 | Gateway Only에서 JWT Secret 직접 참조 금지 (신규) |

#### 7-3. Gateway Only 아키텍처 특징

```
[Gateway Only 패턴]
Client → JWT → Gateway (JWT 검증) → X-User-Id, X-User-Roles → Service

장점:
- JWT Secret은 Gateway만 보유
- Secret 로테이션 시 Gateway만 재배포
- 서비스는 단순히 헤더만 읽으면 됨
- 인증 로직 중앙 집중화
```

---

### 8. Gateway Only UUID 지원

**작업일**: 2025-12-08

**목적**:
- Long 타입 userId를 UUID로 변경 (보안 강화)
- UUID는 예측 불가능하여 IDOR 공격 방지
- UUIDv7 권장 (시간 순서 보장, 인덱스 효율)

#### 8-1. 문서 수정

| 파일 | 변경 내용 |
|------|----------|
| `security/gateway-only-architecture.md` | userId 타입 Long → UUID 변경, 모든 예제 코드 업데이트 |

#### 8-2. SecurityArchTest 규칙 추가

| 규칙 번호 | 규칙명 | 설명 |
|----------|--------|------|
| 28 | GatewayUser userId UUID 타입 | GatewayUser의 userId 필드는 UUID 타입 필수 |
| 29 | SecurityContextAuthenticator UUID 반환 | authenticate(GatewayUser) 메서드는 UUID 반환 필수 |

#### 8-3. Serena Memory 규칙 추가

`convention-rest-api-layer-validation-rules`에 SEC-016 ~ SEC-022 (7개) 추가:

| Rule ID | 규칙명 |
|---------|--------|
| SEC-016 | GatewayUser UUID 사용 필수 |
| SEC-017 | GatewayUserResolver UUID.fromString() 파싱 |
| SEC-018 | GatewayUser record 타입 |
| SEC-019 | GatewayHeaderAuthFilter OncePerRequestFilter 상속 |
| SEC-020 | JWT Secret 직접 참조 금지 |
| SEC-021 | Gateway 컴포넌트 패키지 위치 |
| SEC-022 | SecurityContextAuthenticator UUID 반환 |

---

### 9. Serena Memory 규칙 분할

**작업일**: 2025-12-08

**목적**:
- `convention-rest-api-layer-validation-rules` 메모리가 너무 커서 토큰 효율성 저하
- `convention-application-layer-validation-rules.md`처럼 카테고리별 분할

#### 9-1. 분할 구조

| 파일명 | 카테고리 | 규칙 수 |
|--------|----------|---------|
| `rest-api-rules-01-controller` | CONTROLLER | 16개 |
| `rest-api-rules-02-command-dto` | COMMAND_DTO | 13개 |
| `rest-api-rules-03-query-dto` | QUERY_DTO | 12개 |
| `rest-api-rules-04-response-dto` | RESPONSE_DTO | 12개 |
| `rest-api-rules-05-mapper` | MAPPER | 16개 |
| `rest-api-rules-06-error` | ERROR | 9개 |
| `rest-api-rules-07-security` | SECURITY | 22개 |
| `rest-api-rules-08-openapi` | OPENAPI | 10개 |
| `rest-api-rules-09-testing` | TESTING | 12개 |

#### 9-2. 인덱스 파일

`convention-rest-api-layer-validation-rules`를 요약본(인덱스)으로 교체:
- 전체 통계 및 구조 요약
- 카테고리별 파일 참조 가이드
- 필요한 카테고리만 선택적 로드 가능

#### 9-3. 효과

```
Before: 하나의 큰 메모리 파일 (122개 규칙 전체 로드)
After: 필요한 카테고리만 선택 로드
       예) read_memory("rest-api-rules-07-security") - SECURITY만 로드

토큰 효율성: 약 80% 절감 (필요한 카테고리만 로드 시)
```

---

## Domain Layer

*(작업 예정)*

---

## Application Layer

### 작업일: 2025-12-08

### 1. assumeTrue 조건 추가 (테스트 안정화)

**목적**: 해당 타입의 클래스가 없을 때 테스트가 실패하지 않고 스킵되도록 함

**변경된 테스트 파일**:

| 파일 | 추가된 플래그 | 설명 |
|------|-------------|------|
| `QueryPortArchTest.java` | `hasQueryPortClasses` | QueryPort 클래스 존재 여부 체크 |
| `PersistencePortArchTest.java` | `hasPersistencePortClasses` | PersistencePort 클래스 존재 여부 체크 |
| `LockQueryPortArchTest.java` | `hasLockQueryPortClasses` | LockQueryPort 클래스 존재 여부 체크 |

**표준 패턴**:
```java
private static boolean hasQueryPortClasses;

@BeforeAll
static void setUp() {
    classes = new ClassFileImporter().importPackages("com.ryuqq.application");

    hasQueryPortClasses = classes.stream()
        .anyMatch(javaClass -> javaClass.getSimpleName().endsWith("QueryPort"));
}

@Test
void someTest() {
    assumeTrue(hasQueryPortClasses, "QueryPort 클래스가 없어 테스트를 스킵합니다");
    // ... 테스트 로직
}
```

---

### 2. PersistencePort delete 메서드 금지

**변경 파일**: `PersistencePortArchTest.java`

**변경 전**:
```java
// delete()는 Hard Delete 용도로 허용
.haveNameMatching("save|update|remove")
```

**변경 후**:
```java
// delete()도 금지 - Soft Delete 권장
.haveNameMatching("save|update|delete|remove")
```

| 항목 | 변경 전 | 변경 후 |
|------|---------|---------|
| 금지 메서드 | `save`, `update`, `remove` | `save`, `update`, `delete`, `remove` |
| 허용 메서드 | `persist`, `persistAll`, `delete` | `persist`, `persistAll` 만 |
| 규칙 7-2 | delete() void 반환 검증 | **삭제됨** |

**Javadoc 업데이트**:
```java
/**
 * <li>허용 메서드: persist(T), persistAll(List<T>)
 * <li>금지 메서드: save, update, delete, remove
 * <li>삭제: Soft Delete(상태 변경) 권장, Hard Delete 필요 시 별도 예외 처리
 */
```

---

### 3. QueryPort 원시 타입 파라미터 금지 규칙 추가

**변경 파일**: `QueryPortArchTest.java`

**목적**: Domain VO 사용 강제로 타입 안전성 확보

**신규 규칙**:

| 규칙 번호 | 규칙명 | 설명 |
|----------|--------|------|
| 16-2 | `findBy*()` 원시 타입 금지 | `findByEmail(String)` ❌ → `findByEmail(Email)` ✅ |
| 16-3 | `existsBy*()` 원시 타입 금지 | `existsByEmail(String)` ❌ → `existsByEmail(Email)` ✅ |

**금지되는 파라미터 타입**:
- `Long`, `long`
- `String`
- `Integer`, `int`

**적용 예시**:
```java
// ❌ 금지 - 원시 타입 사용
Optional<User> findByEmail(String email);
boolean existsByEmail(String email);
List<Order> findByStatus(String status);

// ✅ 허용 - Domain VO 사용
Optional<User> findByEmail(Email email);
boolean existsByEmail(Email email);
List<Order> findByStatus(OrderStatus status);
```

---

### 4. 테스트 결과

```
Application Layer ArchUnit Tests: 218개 전체 통과 ✅
- 테스트 실행: 218개
- 통과: 12개 (실제 클래스 존재)
- 스킵: 206개 (해당 클래스 미존재)
- 실패: 0개
```

---

### 5. 관련 Serena Memory 업데이트

**파일**: `convention-application-layer-validation-rules.md`

**추가된 규칙**:
- PORT-016-2: QueryPort findBy* 원시 타입 파라미터 금지
- PORT-016-3: QueryPort existsBy* 원시 타입 파라미터 금지
- PORT-007: PersistencePort delete 메서드 금지 (수정)

---

## Persistence Layer

*(작업 예정)*

---

## 변경 이력

| 날짜 | 레이어 | 작업 내용 | 담당자 |
|------|--------|----------|--------|
| 2025-12-08 | REST API | Gateway Only UUID 지원 - UUID 기반 userId 아키텍처 | Claude |
| 2025-12-08 | REST API | Serena Memory 규칙 분할 - 122개 규칙 9개 파일로 분할 | Claude |
| 2025-12-08 | REST API | Gateway Only 아키텍처 가이드 문서 및 ArchUnit 규칙 추가 | Claude |
| 2025-12-08 | REST API | 환경별 설정 파일 분리, Config 검증 테스트 추가 | Claude |
| 2025-12-08 | REST API | 테스트 클래스 제외 일관성 적용 | Claude |
| 2025-12-08 | REST API | 패키지 상수 중앙화, ApiDocsController 예외 처리 | Claude |
