# kentback TDD Plan: AESA-66

**Jira Task**: [AESA-66](https://ryuqqq.atlassian.net/browse/AESA-66) - Domain Layer Implementation - User Authentication
**Epic**: [AESA-65](https://ryuqqq.atlassian.net/browse/AESA-65) - User Authentication
**Layer**: domain
**생성일**: 2025-11-10

---

## 📋 Task 개요

### Domain Layer 요구사항

**User Aggregate**:
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

**Value Object 목록**:
- **Email**: 이메일 형식 검증 (RFC 5322)
- **Password**: 패스워드 강도 검증 (최소 8자, 영문+숫자+특수문자)
- **JwtToken**: JWT 토큰 생성 및 검증

---

## 🔴 RED Phase: 실패하는 테스트 작성

### 0. TestFixture 생성 (FIRST STEP) ⭐

**목표**: 테스트 객체 생성 표준화

**TestFixture 구조**:
```
domain/src/
├── main/java/
│   └── com/company/template/domain/
└── testFixtures/java/
    └── com/company/template/domain/fixture/
        ├── UserDomainFixture.java
        ├── EmailFixture.java
        └── PasswordFixture.java
```

**UserDomainFixture.java**:
```java
public class UserDomainFixture {
    private static final Long DEFAULT_USER_ID = 1L;
    private static final String DEFAULT_EMAIL = "test@example.com";

    public static UserDomain create() {
        return UserDomain.create(DEFAULT_USER_ID, DEFAULT_EMAIL, ...);
    }

    public static UserDomain createWithEmail(String email) {
        return UserDomain.create(DEFAULT_USER_ID, email, ...);
    }

    public static UserDomain createAdmin() {
        return UserDomain.create(..., UserRole.ADMIN);
    }

    public static UserDomain createLockedAccount() {
        UserDomain user = create();
        for (int i = 0; i < 5; i++) {
            user.recordLoginFailure();
        }
        return user;
    }

    private UserDomainFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
```

### 1. Law of Demeter 테스트

**목표**: Getter 체이닝 금지 규칙 준수 확인

**테스트 케이스** (TestFixture 사용):
```java
// UserDomainTest.java
@Test
void shouldNotAllowGetterChaining() {
    // Given - Use Fixture
    UserDomain user = UserDomainFixture.create();

    // When & Then
    // ❌ user.getEmail().toLowerCase() (Getter 체이닝)
    // ✅ user.getEmailInLowerCase() (Tell, Don't Ask)
    assertThat(user.getEmailInLowerCase()).isEqualTo("test@example.com");
}
```

### 2. Email 형식 검증 테스트

**목표**: RFC 5322 이메일 형식 검증

**테스트 케이스**:
```java
// EmailTest.java
@Test
void shouldValidateEmailFormat() {
    // Valid emails
    assertDoesNotThrow(() -> new Email("test@example.com"));
    assertDoesNotThrow(() -> new Email("user.name@domain.co.kr"));

    // Invalid emails
    assertThrows(InvalidEmailException.class, () -> new Email("invalid"));
    assertThrows(InvalidEmailException.class, () -> new Email("@example.com"));
    assertThrows(InvalidEmailException.class, () -> new Email("test@"));
}
```

### 3. Password 강도 검증 테스트

**목표**: 최소 8자, 영문+숫자+특수문자 조합 검증

**테스트 케이스**:
```java
// PasswordTest.java
@Test
void shouldValidatePasswordStrength() {
    // Valid passwords
    assertDoesNotThrow(() -> new Password("Pass123!@#"));
    assertDoesNotThrow(() -> new Password("Secure@2024"));

    // Invalid passwords
    assertThrows(WeakPasswordException.class, () -> new Password("short"));
    assertThrows(WeakPasswordException.class, () -> new Password("onlyletters"));
    assertThrows(WeakPasswordException.class, () -> new Password("12345678"));
    assertThrows(WeakPasswordException.class, () -> new Password("NoSpecial1"));
}
```

### 4. 로그인 실패 카운트 테스트

**목표**: 5회 실패 시 계정 자동 잠금

**테스트 케이스** (TestFixture 사용):
```java
// UserDomainTest.java
@Test
void shouldLockAccountAfterFiveFailedAttempts() {
    // Given - Use Fixture
    UserDomain user = UserDomainFixture.create();

    // When
    for (int i = 0; i < 5; i++) {
        user.recordLoginFailure();
    }

    // Then
    assertThat(user.isAccountLocked()).isTrue();
    assertThat(user.getLoginFailCount()).isEqualTo(5);
}

@Test
void shouldResetFailCountAfterSuccessfulLogin() {
    // Given - Use Fixture
    UserDomain user = UserDomainFixture.create();
    user.recordLoginFailure();
    user.recordLoginFailure();

    // When
    user.recordLoginSuccess();

    // Then
    assertThat(user.getLoginFailCount()).isEqualTo(0);
    assertThat(user.isAccountLocked()).isFalse();
}

@Test
void shouldUnlockAccountExplicitly() {
    // Given - Use Fixture (createLockedAccount)
    UserDomain user = UserDomainFixture.createLockedAccount();
    assertThat(user.isAccountLocked()).isTrue();

    // When
    user.unlockAccount();

    // Then
    assertThat(user.isAccountLocked()).isFalse();
    assertThat(user.getLoginFailCount()).isEqualTo(0);
}
```

### 5. Lombok 금지 검증

**목표**: Pure Java 구현 확인

**검증 방법**:
```bash
# Lombok 어노테이션 사용 여부 확인
grep -r "@Data\|@Builder\|@Getter\|@Setter" domain/src/main/java/
# 결과: 0건 (Lombok 미사용)
```

---

## 🟢 GREEN Phase: 최소 구현으로 테스트 통과

### 1. User Domain Aggregate 구현

**파일**: `domain/src/main/java/com/company/template/domain/user/UserDomain.java`

**구현 요구사항**:
- ✅ Lombok 금지 (Pure Java)
- ✅ Law of Demeter 준수
- ✅ Tell, Don't Ask 원칙

```java
public class UserDomain {
    private final Long userId;
    private final String email;
    private final String encryptedPassword;
    private final String name;
    private final UserRole role;
    private int loginFailCount;
    private boolean accountLocked;
    private final LocalDateTime createdAt;

    // Private constructor (Factory Method 패턴)
    private UserDomain(Long userId, String email, String encryptedPassword,
                      String name, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.name = name;
        this.role = role;
        this.loginFailCount = 0;
        this.accountLocked = false;
        this.createdAt = LocalDateTime.now();
    }

    // Factory Method
    public static UserDomain create(Long userId, String email, String encryptedPassword,
                                    String name, UserRole role) {
        return new UserDomain(userId, email, encryptedPassword, name, role);
    }

    // Tell, Don't Ask: Getter 체이닝 방지
    public String getEmailInLowerCase() {
        return this.email.toLowerCase();
    }

    // Business Methods
    public void recordLoginFailure() {
        this.loginFailCount++;
        if (this.loginFailCount >= 5) {
            this.accountLocked = true;
        }
    }

    public void recordLoginSuccess() {
        this.loginFailCount = 0;
        // 계정 잠금 해제는 관리자만 가능 (별도 메서드)
    }

    public void unlockAccount() {
        // TODO: 관리자 권한 체크 (Application Layer에서 처리)
        this.accountLocked = false;
        this.loginFailCount = 0;
    }

    // Getters (Pure Java)
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public UserRole getRole() { return role; }
    public int getLoginFailCount() { return loginFailCount; }
    public boolean isAccountLocked() { return accountLocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

### 2. Email Value Object 구현

**파일**: `domain/src/main/java/com/company/template/domain/user/Email.java`

```java
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("Invalid email format: " + value);
        }
    }
}
```

### 3. Password Value Object 구현

**파일**: `domain/src/main/java/com/company/template/domain/user/Password.java`

```java
public record Password(String value) {
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");

    public Password {
        if (value == null || value.isBlank()) {
            throw new WeakPasswordException("Password cannot be empty");
        }
        if (!PASSWORD_PATTERN.matcher(value).matches()) {
            throw new WeakPasswordException(
                "Password must be at least 8 characters with letters, numbers, and special characters"
            );
        }
    }
}
```

### 4. UserRole Enum 구현

**파일**: `domain/src/main/java/com/company/template/domain/user/UserRole.java`

```java
public enum UserRole {
    USER,
    ADMIN
}
```

---

## 🔄 REFACTOR Phase: 코드 개선

### 1. Java 21 Record 패턴 적용

**Before**:
```java
public class Email {
    private final String value;

    public Email(String value) {
        // validation
        this.value = value;
    }

    public String getValue() { return value; }
}
```

**After (Java 21 Record)**:
```java
public record Email(String value) {
    public Email {
        // Compact Constructor
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email cannot be empty");
        }
        // validation...
    }
}
```

### 2. Tell, Don't Ask 원칙 강화

**Before** (Law of Demeter 위반):
```java
// Controller Layer
String email = user.getEmail().toLowerCase();
if (email.contains("admin")) {
    // ...
}
```

**After** (Tell, Don't Ask):
```java
// Domain Layer에 비즈니스 메서드 추가
public boolean isAdminEmail() {
    return this.email.toLowerCase().contains("admin");
}

// Controller Layer
if (user.isAdminEmail()) {
    // ...
}
```

### 3. 불변성 강화

**개선 사항**:
- `final` 키워드 적극 활용
- Setter 메서드 제거
- 상태 변경은 비즈니스 메서드를 통해서만

---

## ✅ Zero-Tolerance 체크리스트

- [ ] Law of Demeter 준수 (Getter 체이닝 금지)
- [ ] Lombok 미사용 (Pure Java/Record)
- [ ] Long FK 전략 (JPA 관계 어노테이션 금지)
- [ ] Tell, Don't Ask 원칙
- [ ] 비즈니스 규칙 Domain Layer에 구현
- [ ] Value Object는 Record 패턴 사용

---

## 🚀 실행 계획

### 1. 브랜치 생성
```bash
git checkout -b feature/AESA-66-domain-user-authentication
```

### 2. RED Phase 실행
```bash
# 테스트 작성
touch domain/src/test/java/com/company/template/domain/user/UserDomainTest.java
touch domain/src/test/java/com/company/template/domain/user/EmailTest.java
touch domain/src/test/java/com/company/template/domain/user/PasswordTest.java

# 테스트 실행 (실패 확인)
./gradlew :domain:test
```

### 3. GREEN Phase 실행
```bash
# Domain 구현
touch domain/src/main/java/com/company/template/domain/user/UserDomain.java
touch domain/src/main/java/com/company/template/domain/user/Email.java
touch domain/src/main/java/com/company/template/domain/user/Password.java
touch domain/src/main/java/com/company/template/domain/user/UserRole.java

# 테스트 실행 (통과 확인)
./gradlew :domain:test
```

### 4. REFACTOR Phase 실행
```bash
# Record 패턴 적용
# Tell, Don't Ask 원칙 강화

# 최종 테스트
./gradlew :domain:test
```

### 5. 검증
```bash
# ArchUnit 테스트
./gradlew test --tests "*ArchitectureTest"

# Lombok 사용 여부 확인
grep -r "@Data\|@Builder\|@Getter\|@Setter" domain/src/main/java/

# Law of Demeter 위반 확인
grep -r "\.get.*()\.get.*(" domain/src/main/java/
```

---

**다음 Task**: [AESA-67](https://ryuqqq.atlassian.net/browse/AESA-67) - Application Layer Implementation
