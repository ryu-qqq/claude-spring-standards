# PRD: User Authentication

**작성일**: 2025-11-10
**작성자**: Claude Code
**상태**: Draft

---

## 📋 프로젝트 개요

### 비즈니스 목적
사용자 인증 시스템 구축 (JWT 기반)

### 주요 사용자
- 일반 사용자 (회원가입, 로그인)
- 관리자 (사용자 관리)

### 성공 기준
- 로그인 응답 시간 < 500ms
- JWT 토큰 만료 시간: 1시간
- 보안: HTTPS 필수, 패스워드 BCrypt 암호화
- 회원가입 성공률 > 95%

---

## 🏗️ Layer별 요구사항

### 1. Domain Layer

#### Aggregate 목록
- **User**
  - 속성:
    - userId: Long (PK)
    - email: String (unique)
    - password: String (BCrypt 암호화)
    - name: String
    - role: UserRole (ENUM: USER, ADMIN)
    - loginFailCount: Integer
    - accountLocked: Boolean
    - createdAt: LocalDateTime
  - 비즈니스 규칙:
    - 이메일 중복 불가
    - 패스워드 최소 8자 이상, 영문+숫자+특수문자 조합
    - 로그인 실패 5회 시 계정 잠금
    - 계정 잠금 해제는 관리자만 가능

#### Value Object 목록
- **Email**: 이메일 형식 검증 (RFC 5322)
- **Password**: 패스워드 강도 검증 (최소 8자, 영문+숫자+특수문자)
- **JwtToken**: JWT 토큰 생성 및 검증

#### Zero-Tolerance 규칙 준수
- ✅ Law of Demeter (Getter 체이닝 금지)
- ✅ Lombok 금지 (Pure Java/Record 사용)
- ✅ Long FK 전략 (JPA 관계 어노테이션 금지)

### 2. Application Layer

#### UseCase 목록

**Command UseCase**:
- **RegisterUserUseCase**: 회원가입
  - Input: RegisterUserCommand (email, password, name)
  - Output: Long userId
  - Transaction: Yes
  - 외부 API: 이메일 인증 발송 (트랜잭션 밖에서)

- **LoginUserUseCase**: 로그인
  - Input: LoginCommand (email, password)
  - Output: LoginResponse (jwtToken, userId, expiresIn)
  - Transaction: Yes
  - 외부 API: None

- **LockUserAccountUseCase**: 계정 잠금 (5회 실패 시)
  - Input: Long userId
  - Output: void
  - Transaction: Yes
  - 외부 API: None

**Query UseCase**:
- **GetUserProfileUseCase**: 프로필 조회
  - Input: Long userId
  - Output: UserProfileResponse (userId, email, name, role)
  - Transaction: ReadOnly

- **ValidateTokenUseCase**: JWT 토큰 검증
  - Input: String jwtToken
  - Output: TokenValidationResponse (valid, userId)
  - Transaction: None (외부 서비스)

#### Zero-Tolerance 규칙 준수
- ✅ Command/Query 분리 (CQRS)
- ✅ Transaction 경계 엄격 관리 (이메일 발송은 트랜잭션 밖)

### 3. Persistence Layer

#### JPA Entity 목록
- **UserEntity**
  - 테이블: users
  - 필드:
    - user_id (BIGINT, PK, AUTO_INCREMENT)
    - email (VARCHAR(100), UNIQUE)
    - encrypted_password (VARCHAR(255))
    - name (VARCHAR(50))
    - role (VARCHAR(20))
    - login_fail_count (INT, DEFAULT 0)
    - account_locked (BOOLEAN, DEFAULT false)
    - created_at (TIMESTAMP)
  - 인덱스:
    - UNIQUE INDEX idx_user_email (email)

#### Repository 목록
- **UserRepository**
  - 메서드:
    - findByEmail(String email): Optional<UserEntity>
    - existsByEmail(String email): boolean
    - save(UserEntity user): UserEntity
    - findById(Long userId): Optional<UserEntity>

#### QueryDSL 쿼리
- 사용자 목록 조회 (관리자용, 페이징)
- 계정 잠금 사용자 목록

#### Zero-Tolerance 규칙 준수
- ✅ Long FK 전략 (관계 어노테이션 금지)
- ✅ QueryDSL 최적화 (N+1 방지)

### 4. REST API Layer

#### API 엔드포인트

| Method | Path | Description | Request DTO | Response DTO | Status Code |
|--------|------|-------------|-------------|--------------|-------------|
| POST | /api/v1/auth/register | 회원가입 | RegisterRequest | UserResponse | 201 Created |
| POST | /api/v1/auth/login | 로그인 | LoginRequest | LoginResponse | 200 OK |
| GET | /api/v1/users/me | 내 프로필 조회 | - (JWT) | UserProfileResponse | 200 OK |
| POST | /api/v1/auth/validate | 토큰 검증 | TokenRequest | TokenValidationResponse | 200 OK |

#### Request DTO
- **RegisterRequest**: email, password, name
- **LoginRequest**: email, password
- **TokenRequest**: jwtToken

#### Response DTO
- **UserResponse**: userId, email, name
- **LoginResponse**: jwtToken, userId, expiresIn (3600초)
- **UserProfileResponse**: userId, email, name, role
- **TokenValidationResponse**: valid (boolean), userId

#### Zero-Tolerance 규칙 준수
- ✅ RESTful 설계 원칙
- ✅ 일관된 Error Response 형식

---

## ⚠️ 제약사항

### 비기능 요구사항
- **성능**: 로그인 응답 시간 < 500ms
- **보안**:
  - HTTPS 필수
  - BCrypt 암호화 (cost factor: 10)
  - JWT 토큰 만료: 1시간
  - Refresh Token 지원 (선택사항)
- **확장성**:
  - 동시 사용자: 1,000명
  - Redis 캐시 활용 (JWT 블랙리스트)

---

## 🧪 테스트 전략

### Unit Test
- **Domain**:
  - 이메일 중복 검증 테스트
  - 패스워드 강도 검증 테스트
  - 로그인 실패 카운트 테스트 (5회 → 잠금)
- **Application**:
  - RegisterUserUseCase 테스트 (중복 이메일 → 예외)
  - LoginUserUseCase 테스트 (잘못된 패스워드 → 실패 카운트 증가)

### Integration Test
- **Persistence**:
  - UserRepository 테스트 (Testcontainers)
  - QueryDSL 쿼리 테스트
- **REST API**:
  - POST /api/v1/auth/register → 201 Created
  - POST /api/v1/auth/login → 200 OK (JWT 토큰 반환)
  - GET /api/v1/users/me → 401 Unauthorized (토큰 없음)

### E2E Test
- 회원가입 → 로그인 → 프로필 조회 전체 시나리오

---

## 🚀 개발 계획

### Phase 1: Domain Layer (예상: 1주)
- [ ] User Domain Aggregate 구현
- [ ] Email, Password Value Object 구현
- [ ] Domain Unit Test (Law of Demeter, Lombok 금지 검증)

### Phase 2: Application Layer (예상: 1주)
- [ ] RegisterUserUseCase 구현
- [ ] LoginUserUseCase 구현
- [ ] GetUserProfileUseCase 구현
- [ ] Command/Query DTO 구현
- [ ] Application Unit Test (Transaction 경계 검증)

### Phase 3: Persistence Layer (예상: 3일)
- [ ] UserEntity 구현
- [ ] UserRepository 구현
- [ ] QueryDSL 쿼리 구현
- [ ] Integration Test (Testcontainers)

### Phase 4: REST API Layer (예상: 3일)
- [ ] AuthController 구현
- [ ] UserController 구현
- [ ] Request/Response DTO 구현
- [ ] JWT 필터 구현 (Spring Security)
- [ ] Exception Handling 구현

### Phase 5: Integration Test (예상: 2일)
- [ ] E2E Test 작성
- [ ] 성능 테스트 (JMeter)

---

## 📚 참고 문서

- [Domain Layer 규칙](../../docs/coding_convention/02-domain-layer/)
- [Application Layer 규칙](../../docs/coding_convention/03-application-layer/)
- [Persistence Layer 규칙](../../docs/coding_convention/04-persistence-layer/)
- [REST API Layer 규칙](../../docs/coding_convention/01-adapter-rest-api-layer/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7519)
- [BCrypt Algorithm](https://en.wikipedia.org/wiki/Bcrypt)

---

**다음 단계**: `/jira-from-prd docs/prd/user-authentication.md`
