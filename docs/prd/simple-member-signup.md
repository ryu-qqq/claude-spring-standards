# PRD: 간단한 회원 가입 (Simple Member Signup)

**작성일**: 2025-11-14
**작성자**: System
**버전**: 1.0
**Jira Epic Key**: (sync-to-jira 후 추가)

---

## 📋 프로젝트 개요

### 비즈니스 목적
이메일과 비밀번호 기반의 기본적인 회원 가입 기능을 구현합니다. 사용자는 이메일 주소와 비밀번호를 입력하여 시스템에 회원으로 등록할 수 있습니다.

### 주요 사용자
- 시스템을 처음 사용하는 신규 사용자
- 계정이 필요한 모든 일반 사용자

### 성공 기준
- 중복 이메일 검증이 정확하게 작동
- 비밀번호가 안전하게 암호화되어 저장
- 회원 가입 API 응답 시간 < 500ms (P95)
- 테스트 커버리지 > 90%

---

## 🎯 Layer별 요구사항

### 1️⃣ Domain Layer

#### Member Aggregate
**책임**: 회원의 핵심 비즈니스 규칙 관리

**속성**:
- `Long id` - 회원 고유 식별자
- `Email email` - 이메일 (VO)
- `Password password` - 비밀번호 (VO)
- `LocalDateTime createdAt` - 가입일시
- `LocalDateTime updatedAt` - 수정일시

**비즈니스 규칙**:
- 이메일은 반드시 유효한 형식이어야 함
- 비밀번호는 최소 8자 이상이어야 함
- 비밀번호는 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 함
- 생성 후 이메일은 변경 불가능 (Immutable)

**Zero-Tolerance 준수**:
- ✅ Lombok 금지 - Plain Java 생성자/메서드 사용
- ✅ Law of Demeter - `member.getPassword().getValue()` 금지
- ✅ Tell Don't Ask - `member.isPasswordValid()` 대신 `member.validatePassword()`

#### Email VO
**책임**: 이메일 주소의 유효성 검증

**속성**:
- `String value` - 이메일 주소

**비즈니스 규칙**:
- RFC 5322 표준 형식 준수
- 최대 길이 320자
- `@` 기호 포함 필수
- 도메인 부분 필수

#### Password VO
**책임**: 비밀번호 정책 검증 및 암호화

**속성**:
- `String value` - 암호화된 비밀번호

**비즈니스 규칙**:
- 최소 길이 8자
- 최대 길이 100자
- 영문 대소문자 포함
- 숫자 포함
- 특수문자 포함
- BCrypt 암호화 사용

#### MemberRegistrationException
**종류**:
- `DuplicateEmailException` - 이메일 중복
- `InvalidEmailFormatException` - 이메일 형식 오류
- `InvalidPasswordFormatException` - 비밀번호 형식 오류

---

### 2️⃣ Application Layer

#### RegisterMemberUseCase
**책임**: 회원 가입 유스케이스 오케스트레이션

**입력**: `RegisterMemberCommand`
- `String email` - 이메일 주소
- `String rawPassword` - 평문 비밀번호

**출력**: `MemberResponse`
- `Long memberId` - 생성된 회원 ID
- `String email` - 이메일 주소
- `LocalDateTime createdAt` - 가입일시

**프로세스**:
1. Email VO 생성 및 검증
2. 이메일 중복 확인 (CheckDuplicateEmailQuery)
3. Password VO 생성 및 검증
4. Member Aggregate 생성
5. Member 저장 (SaveMemberCommand)
6. MemberResponse 반환

**Transaction 경계**:
```
┌─────────────────────────────────────┐
│ @Transactional (RegisterMemberUseCase) │
├─────────────────────────────────────┤
│ 1. Email 중복 확인 (Query)           │
│ 2. Member Aggregate 생성            │
│ 3. Member 저장 (Command)            │
└─────────────────────────────────────┘
```

**의존성**:
- `CheckDuplicateEmailQueryPort` (Query)
- `SaveMemberCommandPort` (Command)
- `MemberAssembler` (DTO ↔ Domain 변환)

**Zero-Tolerance 준수**:
- ✅ Transaction 내 외부 API 호출 금지
- ✅ CQRS 분리 - Command/Query Port 명확히 분리
- ✅ Assembler 사용 - DTO ↔ Domain 변환 책임 분리

---

### 3️⃣ Persistence Layer (MySQL)

#### MemberEntity
**책임**: Member Aggregate의 영속성 표현

**테이블**: `members`

**컬럼**:
- `id` BIGINT PRIMARY KEY AUTO_INCREMENT
- `email` VARCHAR(320) NOT NULL UNIQUE
- `password` VARCHAR(100) NOT NULL
- `created_at` TIMESTAMP NOT NULL
- `updated_at` TIMESTAMP NOT NULL

**인덱스**:
- `idx_members_email` UNIQUE INDEX (email)

**Zero-Tolerance 준수**:
- ✅ Lombok 금지 - Plain JPA 어노테이션만 사용
- ✅ JPA 관계 어노테이션 금지 - `@OneToMany`, `@ManyToOne` 사용 불가
- ✅ Long FK 전략 - 다른 Entity 참조 시 `Long userId` 사용

#### MemberJpaRepository
**책임**: Member Entity JPA CRUD

**메서드**:
- `Optional<MemberEntity> findById(Long id)`
- `Optional<MemberEntity> findByEmail(String email)`
- `MemberEntity save(MemberEntity entity)`
- `boolean existsByEmail(String email)`

#### MemberQueryDslRepository
**책임**: Member 복잡한 조회 쿼리

**메서드**:
- `boolean existsByEmail(String email)` - 이메일 중복 확인

**QueryDSL DTO Projection**:
```java
// ❌ Entity 조회 후 변환
MemberEntity entity = queryFactory.selectFrom(member).fetchOne();
return MemberMapper.toDto(entity);

// ✅ DTO Projection 직접 사용
return queryFactory
    .select(Projections.constructor(MemberDto.class,
        member.id,
        member.email,
        member.createdAt
    ))
    .from(member)
    .fetchOne();
```

#### SaveMemberAdapter (Command)
**책임**: Member 저장 Command 구현

**구현**:
- `SaveMemberCommandPort` 인터페이스 구현
- `MemberJpaRepository` 사용
- `MemberMapper` 사용 (Domain ↔ Entity 변환)

#### CheckDuplicateEmailAdapter (Query)
**책임**: 이메일 중복 확인 Query 구현

**구현**:
- `CheckDuplicateEmailQueryPort` 인터페이스 구현
- `MemberQueryDslRepository` 사용
- DTO Projection으로 성능 최적화

---

### 4️⃣ REST API Layer (Adapter-In)

#### MemberController
**책임**: 회원 가입 REST API 엔드포인트

**엔드포인트**:
```
POST /api/members
Content-Type: application/json

Request:
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}

Response (201 Created):
{
  "memberId": 1,
  "email": "user@example.com",
  "createdAt": "2025-11-14T10:30:00"
}

Error (400 Bad Request):
{
  "errorCode": "DUPLICATE_EMAIL",
  "message": "이미 사용 중인 이메일입니다.",
  "timestamp": "2025-11-14T10:30:00"
}
```

**RESTful 설계**:
- ✅ POST 메서드 사용 (리소스 생성)
- ✅ 201 Created 응답 (Location 헤더 포함)
- ✅ 명확한 에러 응답 (4xx, 5xx)

#### RegisterMemberRequest (Command DTO)
**책임**: 회원 가입 요청 데이터 검증

**필드**:
- `@NotBlank @Email String email`
- `@NotBlank @Size(min=8, max=100) String password`

**Validation**:
- Jakarta Validation 사용
- Controller에서 `@Valid` 적용

#### MemberResponse (Response DTO)
**책임**: 회원 정보 응답

**필드**:
- `Long memberId`
- `String email`
- `LocalDateTime createdAt`

#### MemberErrorMapper
**책임**: Domain Exception → HTTP Error 변환

**매핑**:
- `DuplicateEmailException` → 400 Bad Request
- `InvalidEmailFormatException` → 400 Bad Request
- `InvalidPasswordFormatException` → 400 Bad Request
- `IllegalArgumentException` → 400 Bad Request
- `RuntimeException` → 500 Internal Server Error

**Zero-Tolerance 준수**:
- ✅ MockMvc 테스트 금지 - `TestRestTemplate` 사용
- ✅ DTO Record 패턴 - Immutable DTO
- ✅ Validation 필수 - `@Valid` 적용

---

### 5️⃣ Integration Test

#### MemberRegistrationIntegrationTest
**책임**: 회원 가입 E2E 시나리오 검증

**시나리오**:
1. **정상 가입 시나리오**
   - Given: 유효한 이메일/비밀번호
   - When: POST /api/members 호출
   - Then: 201 Created, DB에 저장 확인

2. **이메일 중복 시나리오**
   - Given: 이미 존재하는 이메일
   - When: POST /api/members 호출
   - Then: 400 Bad Request, DUPLICATE_EMAIL 에러

3. **이메일 형식 오류 시나리오**
   - Given: 잘못된 이메일 형식
   - When: POST /api/members 호출
   - Then: 400 Bad Request, INVALID_EMAIL_FORMAT 에러

4. **비밀번호 형식 오류 시나리오**
   - Given: 8자 미만 비밀번호
   - When: POST /api/members 호출
   - Then: 400 Bad Request, INVALID_PASSWORD_FORMAT 에러

**테스트 환경**:
- `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- `TestRestTemplate` 사용
- Flyway 마이그레이션 실행
- H2 In-Memory DB 사용

**Zero-Tolerance 준수**:
- ✅ MockMvc 금지 - `TestRestTemplate` 필수
- ✅ Flyway vs @Sql 분리 - DDL은 Flyway, 테스트 데이터는 `@Sql`
- ✅ Real DB 사용 - H2 In-Memory

---

## ⚠️ 제약사항

### 보안
- 비밀번호는 반드시 BCrypt로 암호화
- 평문 비밀번호는 로그에 남기지 않음
- SQL Injection 방지 (Prepared Statement)

### 성능
- 이메일 중복 확인 쿼리 < 100ms
- 회원 가입 API 응답 시간 < 500ms (P95)
- DB 인덱스 활용 (email UNIQUE INDEX)

### 확장성
- 향후 소셜 로그인 추가 가능하도록 설계
- 이메일 인증 기능 추가 고려

---

## 📊 테스트 전략

### Unit Tests
- **Domain Layer**: 100% 커버리지
  - Member Aggregate 생성 테스트
  - Email VO 검증 테스트
  - Password VO 검증 테스트
  - Exception 발생 테스트

- **Application Layer**: 90% 커버리지
  - UseCase 정상 시나리오
  - UseCase 예외 시나리오
  - Assembler 변환 테스트

- **Persistence Layer**: 90% 커버리지
  - Repository 저장/조회 테스트
  - QueryDSL DTO Projection 테스트
  - Adapter 통합 테스트

- **REST API Layer**: 90% 커버리지
  - Controller 정상 응답
  - Controller 에러 응답
  - DTO Validation 테스트

### Integration Tests
- E2E 시나리오 4개 (정상/중복/이메일오류/비밀번호오류)
- Real DB 사용 (H2)
- TestRestTemplate 사용

### ArchUnit Tests
- 레이어 의존성 검증
- 네이밍 규칙 검증
- Zero-Tolerance 규칙 검증

---

## 📈 성공 메트릭

### 개발 메트릭
- TDD 사이클 시간 < 15분
- 커밋 크기 < 100 라인
- 테스트 성공률 > 95%
- ArchUnit 위반 0회

### 기술 메트릭
- 테스트 커버리지 > 90%
- API 응답 시간 < 500ms (P95)
- DB 쿼리 시간 < 100ms

### 비즈니스 메트릭
- 회원 가입 성공률 > 95%
- 중복 이메일 차단률 100%

---

## 🎯 Epic/Task 구조 (sync-to-jira 후 생성)

```
Epic: 간단한 회원 가입
├── Task 1: Domain Layer 구현 (MEMBER-001)
├── Task 2: Application Layer 구현 (MEMBER-002)
├── Task 3: Persistence Layer 구현 (MEMBER-003)
├── Task 4: REST API Layer 구현 (MEMBER-004)
└── Task 5: Integration Test 구현 (MEMBER-005)
```

---

## 📝 참고 문서

### 코딩 규칙
- `docs/coding_convention/02-domain-layer/aggregate/aggregate-guide.md`
- `docs/coding_convention/03-application-layer/application-guide.md`
- `docs/coding_convention/04-persistence-layer/mysql/persistence-mysql-guide.md`
- `docs/coding_convention/01-adapter-in-layer/rest-api/rest-api-guide.md`
- `docs/coding_convention/05-testing/integration-testing/01_integration-testing-overview.md`

### 아키텍처
- 헥사고날 아키텍처 (Ports & Adapters)
- DDD Aggregate 패턴
- CQRS (Command/Query 분리)

---

**End of PRD**
