# MEMBER-005: Integration Test 구현

**Epic**: 간단한 회원 가입
**Layer**: Integration Test
**브랜치**: feature/MEMBER-005-integration
**Jira URL**: (sync-to-jira 후 추가)

---

## 📝 목적

회원 가입 전체 플로우를 E2E로 검증합니다.
- API → Controller → UseCase → Adapter → DB 전 계층 통합
- Real DB 환경에서 실제 동작 검증
- 다양한 예외 시나리오 커버

---

## 🎯 요구사항

### E2E 시나리오
- [ ] **시나리오 1: 정상 회원 가입**
  - Given: 유효한 이메일/비밀번호
  - When: POST /api/members 호출
  - Then:
    - 201 Created 응답
    - Location 헤더 포함
    - DB에 회원 저장 확인
    - 비밀번호 암호화 확인

- [ ] **시나리오 2: 이메일 중복**
  - Given: 이미 존재하는 이메일
  - When: POST /api/members 호출 (같은 이메일)
  - Then:
    - 400 Bad Request 응답
    - `DUPLICATE_EMAIL` 에러 코드
    - DB에 중복 저장되지 않음

- [ ] **시나리오 3: 이메일 형식 오류**
  - Given: 잘못된 이메일 형식
  - When: POST /api/members 호출
  - Then:
    - 400 Bad Request 응답
    - `INVALID_EMAIL_FORMAT` 에러 코드

- [ ] **시나리오 4: 비밀번호 형식 오류**
  - Given: 8자 미만 비밀번호
  - When: POST /api/members 호출
  - Then:
    - 400 Bad Request 응답
    - `INVALID_PASSWORD_FORMAT` 에러 코드

- [ ] **시나리오 5: 필수 필드 누락**
  - Given: 이메일 또는 비밀번호 누락
  - When: POST /api/members 호출
  - Then:
    - 400 Bad Request 응답
    - Validation 에러 메시지

### 테스트 환경 설정
- [ ] **@SpringBootTest 설정**
  - `webEnvironment = WebEnvironment.RANDOM_PORT`
  - 전체 Spring Context 로드
  - Real HTTP 요청/응답

- [ ] **TestRestTemplate 사용**
  - ❌ MockMvc 금지
  - ✅ TestRestTemplate 필수

- [ ] **Flyway 마이그레이션**
  - DDL 자동 실행
  - 테스트 시작 시 스키마 생성

- [ ] **H2 In-Memory DB**
  - `spring.datasource.url=jdbc:h2:mem:testdb`
  - MySQL 호환 모드: `MODE=MySQL`

- [ ] **@Sql 테스트 데이터**
  - 각 시나리오별 초기 데이터 준비
  - `/test/resources/sql/member-integration-test.sql`

### 검증 항목
- [ ] **HTTP 응답 검증**
  - 상태 코드
  - 응답 헤더 (Location)
  - 응답 본문 (JSON)

- [ ] **DB 상태 검증**
  - 회원 저장 확인
  - 이메일 중복 방지 확인
  - 비밀번호 암호화 확인

- [ ] **트랜잭션 동작 검증**
  - 정상 커밋
  - 예외 발생 시 롤백

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙
- [ ] **MockMvc 금지**
  - ❌ `MockMvc` 사용 불가
  - ✅ `TestRestTemplate` 필수

- [ ] **Flyway vs @Sql 분리**
  - ❌ DDL을 `@Sql`로 실행 금지
  - ✅ DDL은 Flyway, 테스트 데이터는 `@Sql`

- [ ] **Real DB 사용**
  - ❌ Mock Repository 사용 금지
  - ✅ H2 In-Memory DB 사용

### 테스트 규칙
- [ ] TestFixture 사용 필수
  - `MemberIntegrationTestFixture.java`

- [ ] 테스트 커버리지
  - 주요 시나리오 5개 이상
  - 정상 + 예외 케이스 모두 포함

### 성능 기준
- [ ] 각 테스트 < 5초
- [ ] 전체 Integration Test Suite < 30초

---

## ✅ 완료 조건

- [ ] 모든 요구사항 구현 완료
  - 5개 시나리오 테스트
  - 테스트 환경 설정
  - 검증 항목 구현

- [ ] 모든 테스트 통과
  - `MemberRegistrationIntegrationTest.java`
  - Flyway 마이그레이션 성공
  - @Sql 데이터 로드 성공

- [ ] Zero-Tolerance 규칙 준수
  - MockMvc 미사용 확인
  - Flyway/Sql 분리 확인
  - Real DB 사용 확인

- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- **PRD**: `docs/prd/simple-member-signup.md`
- **Plan**: `docs/prd/plans/MEMBER-005-integration-plan.md` (create-plan 후 생성)
- **Jira**: (sync-to-jira 후 추가)
- **코딩 규칙**: `docs/coding_convention/05-testing/integration-testing/01_integration-testing-overview.md`

---

## 📚 참고 규칙

- `docs/coding_convention/05-testing/integration-testing/01_integration-testing-overview.md`
- `docs/coding_convention/05-testing/test-fixtures/test-fixtures-guide.md`

---

## 📝 테스트 예시

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/sql/member-integration-test.sql")
class MemberRegistrationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Test
    @DisplayName("정상 회원 가입 - 201 Created")
    void registerMember_Success() {
        // Given
        RegisterMemberRequest request = new RegisterMemberRequest(
            "newuser@example.com",
            "SecurePass123!"
        );

        // When
        ResponseEntity<MemberResponse> response = restTemplate.postForEntity(
            "/api/members",
            request,
            MemberResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("newuser@example.com");

        // DB 검증
        MemberEntity savedMember = memberRepository.findByEmail("newuser@example.com").orElseThrow();
        assertThat(savedMember.getEmail()).isEqualTo("newuser@example.com");
        assertThat(savedMember.getPassword()).isNotEqualTo("SecurePass123!"); // 암호화 확인
    }

    @Test
    @DisplayName("이메일 중복 - 400 Bad Request")
    void registerMember_DuplicateEmail() {
        // Given: @Sql로 기존 회원 데이터 준비
        RegisterMemberRequest request = new RegisterMemberRequest(
            "existing@example.com",
            "SecurePass123!"
        );

        // When
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/members",
            request,
            ErrorResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().errorCode()).isEqualTo("DUPLICATE_EMAIL");
    }
}
```
