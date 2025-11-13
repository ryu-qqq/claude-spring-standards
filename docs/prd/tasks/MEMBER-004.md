# MEMBER-004: REST API Layer 구현

**Epic**: 간단한 회원 가입
**Layer**: REST API Layer (Adapter-In)
**브랜치**: feature/MEMBER-004-rest-api
**Jira URL**: (sync-to-jira 후 추가)

---

## 📝 목적

회원 가입 REST API 엔드포인트를 제공합니다.
- MemberController: HTTP 요청/응답 처리
- Request DTO: API 요청 데이터 검증
- Response DTO: API 응답 데이터 구조
- ErrorMapper: 예외 → HTTP 에러 변환

---

## 🎯 요구사항

### API 엔드포인트
- [ ] **POST /api/members** - 회원 가입
  ```
  Request:
  POST /api/members
  Content-Type: application/json

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
  Location: /api/members/1

  Error (400 Bad Request):
  {
    "errorCode": "DUPLICATE_EMAIL",
    "message": "이미 사용 중인 이메일입니다.",
    "timestamp": "2025-11-14T10:30:00"
  }
  ```

### RESTful 설계 원칙
- [ ] **POST 메서드 사용** (리소스 생성)
- [ ] **201 Created 응답** (Location 헤더 포함)
- [ ] **명확한 에러 응답** (4xx, 5xx)
- [ ] **적절한 HTTP 상태 코드**
  - 201: 생성 성공
  - 400: 잘못된 요청 (Validation, 중복 이메일)
  - 500: 서버 내부 오류

### Request DTO
- [ ] **RegisterMemberRequest** (Command DTO)
  - Record 패턴 사용
  - Jakarta Validation 적용
  ```java
  public record RegisterMemberRequest(
      @NotBlank(message = "이메일은 필수입니다.")
      @Email(message = "유효한 이메일 형식이 아닙니다.")
      String email,

      @NotBlank(message = "비밀번호는 필수입니다.")
      @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
      String password
  ) {}
  ```

### Response DTO
- [ ] **MemberResponse** (Response DTO)
  - Record 패턴 사용
  - Immutable 구조
  ```java
  public record MemberResponse(
      Long memberId,
      String email,
      LocalDateTime createdAt
  ) {}
  ```

### Error Handling
- [ ] **MemberErrorMapper** (GlobalExceptionHandler)
  - Domain Exception → HTTP Error 변환
  - 매핑 규칙:
    - `DuplicateEmailException` → 400 Bad Request
    - `InvalidEmailFormatException` → 400 Bad Request
    - `InvalidPasswordFormatException` → 400 Bad Request
    - `MethodArgumentNotValidException` → 400 Bad Request
    - `RuntimeException` → 500 Internal Server Error

- [ ] **에러 응답 구조**
  ```java
  public record ErrorResponse(
      String errorCode,
      String message,
      LocalDateTime timestamp
  ) {}
  ```

### Controller
- [ ] **MemberController 구현**
  - `@RestController`
  - `@RequestMapping("/api/members")`
  - `@Valid` 필수 적용
  - Location 헤더 반환

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙
- [ ] **MockMvc 테스트 금지**
  - ❌ `MockMvc` 사용 불가
  - ✅ `TestRestTemplate` 필수 사용

- [ ] **DTO Record 패턴**
  - ❌ Class 기반 DTO 금지
  - ✅ Record 패턴 사용 (Immutable)

- [ ] **Validation 필수**
  - ❌ 검증 없이 요청 처리 금지
  - ✅ `@Valid` + Jakarta Validation

- [ ] **RESTful 설계 준수**
  - ❌ `/api/registerMember` (동사 사용 금지)
  - ✅ `/api/members` (명사 + HTTP 메서드)

### 테스트 규칙
- [ ] ArchUnit 테스트 필수
  - Controller는 `Controller` suffix 필수
  - Request DTO는 `Request` suffix 필수
  - Response DTO는 `Response` suffix 필수

- [ ] TestFixture 사용 필수
  - `RegisterMemberRequestFixture.java`
  - `MemberResponseFixture.java`

- [ ] 테스트 커버리지 > 85%
  - Controller 정상 응답 테스트
  - Controller 에러 응답 테스트
  - DTO Validation 테스트
  - ErrorMapper 테스트

---

## ✅ 완료 조건

- [ ] 모든 요구사항 구현 완료
  - MemberController
  - RegisterMemberRequest
  - MemberResponse
  - MemberErrorMapper

- [ ] 모든 테스트 통과 (Unit + ArchUnit)
  - `MemberControllerTest.java` (TestRestTemplate)
  - `RegisterMemberRequestTest.java` (Validation)
  - `MemberErrorMapperTest.java`
  - `ControllerArchTest.java`

- [ ] Zero-Tolerance 규칙 준수
  - MockMvc 미사용 확인
  - DTO Record 패턴 확인
  - Validation 적용 확인
  - RESTful 설계 확인

- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- **PRD**: `docs/prd/simple-member-signup.md`
- **Plan**: `docs/prd/plans/MEMBER-004-rest-api-plan.md` (create-plan 후 생성)
- **Jira**: (sync-to-jira 후 추가)
- **코딩 규칙**: `docs/coding_convention/01-adapter-in-layer/rest-api/rest-api-guide.md`

---

## 📚 참고 규칙

- `docs/coding_convention/01-adapter-in-layer/rest-api/rest-api-guide.md`
- `docs/coding_convention/01-adapter-in-layer/rest-api/controller/controller-guide.md`
- `docs/coding_convention/01-adapter-in-layer/rest-api/controller/controller-test-guide.md`
- `docs/coding_convention/01-adapter-in-layer/rest-api/dto/command/command-dto-guide.md`
- `docs/coding_convention/01-adapter-in-layer/rest-api/dto/response/response-dto-guide.md`
- `docs/coding_convention/01-adapter-in-layer/rest-api/error/error-handling-strategy.md`
