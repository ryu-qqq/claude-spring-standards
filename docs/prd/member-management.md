# PRD: 회원 관리 (Member Management)

**작성일**: 2025-11-13
**작성자**: Claude Code (User Requirements 기반)
**버전**: 1.0.0

---

## 📋 목차

1. [프로젝트 개요](#프로젝트-개요)
2. [Domain Layer](#domain-layer)
3. [Application Layer](#application-layer)
4. [Persistence Layer](#persistence-layer)
5. [REST API Layer](#rest-api-layer)
6. [구현 가이드](#구현-가이드)

---

## 📌 프로젝트 개요

### 목적
- **비즈니스 목적**: 커머스 사용자 인증 및 권한 관리, 사용자 정보 인증
- **기술 목적**: Spring Boot 3.5.x + Java 21 기반 헥사고날 아키텍처로 확장 가능한 회원 관리 시스템 구축

### 대상 사용자
- **일반 사용자**: 회원 가입, 로그인, 탈퇴
- **관리자**: 계정 잠금 해제, 회원 상태 관리

### 성공 기준
- ✅ 회원 가입 완료 (카카오 소셜 로그인, 핸드폰 로그인 두 가지 방식)
- ✅ 로그인 성공 (JWT 토큰 발급)
- ✅ 회원 탈퇴 처리 (1년 보관)
- ✅ 계정 통합 (핸드폰 → 카카오)

---

## 🏗️ Domain Layer

### 1. Member Aggregate 설계

#### 1.1 속성 정의

##### 필수 속성 (Required)
| 속성 | 타입 | 설명 | 제약조건 |
|------|------|------|----------|
| `memberId` | Long | 회원 고유 ID | PK, Auto Increment |
| `loginType` | LoginType | 로그인 타입 | KAKAO, PHONE |
| `phoneNumber` | String | 핸드폰 번호 | 필수, Unique |
| `name` | String | 회원 이름 | 필수 |
| `status` | MemberStatus | 회원 상태 | ACTIVE, INACTIVE, LOCKED, WITHDRAWN |

##### 선택 속성 (Optional)
| 속성 | 타입 | 설명 | 제약조건 |
|------|------|------|----------|
| `nickname` | String | 닉네임 | 수정 불가 (회원 정보 수정 전체 불가) |
| `email` | String | 이메일 | - |
| `gender` | Gender | 성별 | MALE, FEMALE, OTHER |
| `birthday` | LocalDate | 생년월일 | - |
| `birthYear` | Integer | 출생연도 | - |

##### 카카오 전용 속성
| 속성 | 타입 | 설명 | 제약조건 |
|------|------|------|----------|
| `kakaoId` | String | 카카오 회원 ID | Unique (카카오 로그인 시 필수) |
| `profileImageUrl` | String | 프로필 이미지 URL | - |

##### 핸드폰 로그인 전용 속성
| 속성 | 타입 | 설명 | 제약조건 |
|------|------|------|----------|
| `password` | String | 비밀번호 | BCrypt 암호화, 강력한 조합 검증 |

##### 상태 관리 속성
| 속성 | 타입 | 설명 | 제약조건 |
|------|------|------|----------|
| `lastLoginAt` | LocalDateTime | 마지막 로그인 시각 | - |
| `failedLoginAttempts` | Integer | 로그인 실패 횟수 | 5회 초과 시 LOCKED |

##### 탈퇴 관련 속성
| 속성 | 타입 | 설명 | 제약조건 |
|------|------|------|----------|
| `withdrawalReason` | String | 탈퇴 사유 | 탈퇴 시 필수 |
| `withdrawnAt` | LocalDateTime | 탈퇴 시각 | - |

##### 계정 통합 속성
| 속성 | 타입 | 설명 | 제약조건 |
|------|------|------|----------|
| `integratedAt` | LocalDateTime | 계정 통합 시각 | 핸드폰 → 카카오 통합 시 |

---

### 2. Value Objects

#### LoginType (Enum)
```java
public enum LoginType {
    KAKAO,  // 카카오 소셜 로그인
    PHONE   // 핸드폰 번호 + 비밀번호
}
```

#### MemberStatus (Enum)
```java
public enum MemberStatus {
    ACTIVE,     // 정상 활성 회원
    INACTIVE,   // 6개월 미로그인
    LOCKED,     // 5회 로그인 실패로 잠금
    WITHDRAWN   // 탈퇴 (1년 보관)
}
```

**상태 전이 (State Transition)**:
```
ACTIVE → INACTIVE (6개월 미로그인)
ACTIVE → LOCKED (5회 로그인 실패)
LOCKED → ACTIVE (관리자 해제)
ACTIVE → WITHDRAWN (탈퇴)
WITHDRAWN → (1년 후 물리 삭제)
```

#### Gender (Enum)
```java
public enum Gender {
    MALE,
    FEMALE,
    OTHER
}
```

---

### 3. 비즈니스 규칙 (Business Rules)

#### 3.1 회원 가입 규칙

##### 중복 방지 전략
| 시나리오 | 처리 방법 |
|----------|-----------|
| **카카오 중복 가입** | `kakaoId`로 기존 회원 조회 → 기존 회원으로 **로그인 처리** |
| **핸드폰 중복 가입** | `phoneNumber`로 기존 회원 조회 → **가입 불가**, "이미 회원가입된 아이디가 있습니다" 안내 |

##### 계정 통합 전략
| 시나리오 | 처리 방법 |
|----------|-----------|
| **핸드폰 회원 → 카카오 로그인 시도** | 같은 `phoneNumber` 확인 → **통합 유도** (IntegrateMemberAccountUseCase) |
| **카카오 회원 → 핸드폰 로그인 시도** | **가입 불가**, "이미 카카오로 가입되어 있습니다" 안내 |

##### 필수 정보 검증
| 로그인 타입 | 필수 정보 |
|-------------|-----------|
| **KAKAO** | `name`, `phoneNumber` (카카오에서 제공하지 않는 경우 추가 입력) |
| **PHONE** | `phoneNumber`, `password` (강력한 조합 검증) |

**비밀번호 강력한 조합 규칙**:
- 최소 8자 이상
- 영문 대소문자, 숫자, 특수문자 중 3가지 이상 포함
- 연속된 문자 3개 이상 금지 (예: "123", "abc")

##### 가입 완료 후처리
- ✅ 회원 정보 DB 저장
- ✅ `MemberRegistered` 이벤트 발행 → **Outbox 저장**
- ✅ Outbox Poller → **알림톡 발송** (SQS)

---

#### 3.2 로그인 규칙

##### 로그인 실패 횟수 관리
| 상태 | 조건 | 처리 |
|------|------|------|
| **정상** | `failedLoginAttempts < 5` | 로그인 시도 허용 |
| **계정 잠금** | `failedLoginAttempts >= 5` | `status = LOCKED`, "5회 실패로 계정 잠금. CS 연락 요청" |
| **잠금 해제** | 관리자 승인 | `status = ACTIVE`, `failedLoginAttempts = 0` |

##### JWT 토큰 전략
| 토큰 | 만료 시간 | 저장 위치 | 용도 |
|------|----------|----------|------|
| **Access Token** | 1시간 | HTTP Header (`Authorization: Bearer {token}`) | API 인증 |
| **Refresh Token** | 7일 | Cookie (HttpOnly) + Redis | 토큰 갱신 |

**저장소 전략**:
- **DB**: Refresh Token (7일, 장기 저장)
- **Redis**: Refresh Token (7일, 빠른 조회) + Active Session (1시간, 동시 로그인 방지)

##### 동시 로그인 방지
- ❌ **동시 로그인 허용 불가**
- ✅ 새 로그인 시 기존 토큰 무효화 (Redis `active_session:{memberId}` 갱신)

##### 이상 로그인 감지
- ⚠️ 비정상적인 IP/디바이스 패턴 감지 (구체적 기준은 추후 정의)
- ⚠️ 감지 시 알림 발송 (이메일/SMS)

---

#### 3.3 탈퇴 규칙

##### 탈퇴 조건
- ❌ **진행중인 주문 없음** (현재 주문 없으므로 체크 제외)
- ✅ **즉시 탈퇴 가능**

##### 탈퇴 후처리
| 항목 | 처리 방법 |
|------|----------|
| **회원 상태** | `status = WITHDRAWN` |
| **탈퇴 사유** | `withdrawalReason` 수집 (필수) |
| **탈퇴 시각** | `withdrawnAt` 기록 |
| **데이터 보관** | 1년 보관 후 물리 삭제 (Batch Job) |

##### 재가입 처리
| 시나리오 | 처리 방법 |
|----------|-----------|
| **유효기간 내 재가입** | 포인트/쿠폰 사용 가능 (유효기간 내) |
| **유효기간 만료 후** | 포인트/쿠폰 만료, 사용 불가 |

---

#### 3.4 회원 정보 수정 규칙

| 항목 | 수정 가능 여부 | 비고 |
|------|---------------|------|
| `nickname` | ❌ 불가 | 회원 정보 수정 전체 불가 |
| `email` | ❌ 불가 | 회원 정보 수정 전체 불가 |
| `gender` | ❌ 불가 | 회원 정보 수정 전체 불가 |
| `phoneNumber` | ❌ 불가 | 변경 시 새 회원으로 인식 (재가입 필요) |

**Note**: 회원 정보 수정 기능 전체 제외 (UpdateProfileUseCase 삭제)

---

#### 3.5 계정 통합 규칙

| 항목 | 처리 방법 |
|------|----------|
| **통합 조건** | 핸드폰 회원 + 카카오 로그인 시도 (같은 `phoneNumber`) |
| **타입 변경** | `loginType = PHONE → KAKAO` |
| **정보 업데이트** | 카카오 제공 정보로 업데이트 (`kakaoId`, `name`, `email`, `profileImageUrl`) |
| **통합 이력** | `integratedAt` 기록 |
| **포인트/쿠폰** | 합산 제외 (기존 데이터 유지) |

---

### 4. Domain Events

#### MemberRegistered (회원 가입 완료)
```java
public record MemberRegistered(
    Long memberId,
    LoginType loginType,
    String phoneNumber,
    String name,
    LocalDateTime registeredAt
) {}
```

**발행 시점**: 회원 가입 완료 후 (DB 저장 직후)
**용도**: Outbox 저장 → SQS 발행 → 알림톡 발송

---

#### MemberWithdrawn (회원 탈퇴)
```java
public record MemberWithdrawn(
    Long memberId,
    String withdrawalReason,
    LocalDateTime withdrawnAt
) {}
```

**발행 시점**: 회원 탈퇴 처리 후
**용도**: 탈퇴 이력 추적, 통계 수집

---

#### MemberLocked (계정 잠금)
```java
public record MemberLocked(
    Long memberId,
    String reason,  // "5회 로그인 실패"
    LocalDateTime lockedAt
) {}
```

**발행 시점**: 로그인 실패 5회 도달 시
**용도**: 관리자 알림, CS 대응

---

#### MemberIntegrated (계정 통합)
```java
public record MemberIntegrated(
    Long memberId,
    LoginType oldType,  // PHONE
    LoginType newType,  // KAKAO
    LocalDateTime integratedAt
) {}
```

**발행 시점**: 핸드폰 회원 → 카카오 통합 완료 후
**용도**: 통합 이력 추적

---

## 🔧 Application Layer

### 1. UseCases 정의

#### 1.1 RegisterMemberUseCase (회원 가입)

**책임**:
1. 회원 정보 저장 (DB 트랜잭션)
2. 카카오 API 호출 (트랜잭션 외부)
3. `MemberRegistered` 이벤트 발행 → Outbox 저장

**트랜잭션 경계**:
```java
// 트랜잭션 내부
@Transactional
public void executeInTransaction(RegisterMemberCommand cmd) {
    // 1. 중복 체크 (phoneNumber, kakaoId)
    // 2. Member Aggregate 생성
    // 3. Member 저장
    // 4. Outbox 저장 (MemberRegistered 이벤트)
}

// 트랜잭션 외부
public void executeExternal(RegisterMemberCommand cmd) {
    // 카카오 API 호출 (토큰 검증, 사용자 정보 조회)
}
```

**처리 순서**:
1. 카카오 API 호출 (외부)
2. 회원 정보 저장 + Outbox 저장 (트랜잭션)
3. Outbox Poller → SQS 발행 → 알림톡 발송

---

#### 1.2 LoginUseCase (로그인)

**책임**:
1. 인증 (Authentication): 사용자 인증 정보 검증
2. JWT 토큰 발급: Access Token (1시간) + Refresh Token (7일)
3. Redis에 Refresh Token 저장
4. 동시 로그인 체크: 기존 토큰 무효화
5. 로그인 실패 시: `failedLoginAttempts` 증가, 5회 도달 시 `status = LOCKED`

**트랜잭션 경계**:
```java
@Transactional
public LoginResponse execute(LoginCommand cmd) {
    // 1. 인증 검증 (비밀번호 or 카카오 토큰)
    // 2. 로그인 실패 시: failedLoginAttempts 증가
    // 3. 로그인 성공 시: lastLoginAt 업데이트, failedLoginAttempts = 0
    // 4. JWT 토큰 발급
    // 5. Redis에 Refresh Token 저장
    // 6. 기존 토큰 무효화 (동시 로그인 방지)
}
```

**로그인 실패 처리**:
```java
if (failedLoginAttempts >= 5) {
    member.lock();  // status = LOCKED
    throw new MemberLockedException("5회 로그인 실패로 계정 잠금");
}
```

---

#### 1.3 WithdrawMemberUseCase (회원 탈퇴)

**책임**:
1. 회원 상태 변경: `status = WITHDRAWN`
2. 탈퇴 사유 저장: `withdrawalReason`
3. 탈퇴 시각 기록: `withdrawnAt`

**트랜잭션 경계**:
```java
@Transactional
public void execute(WithdrawMemberCommand cmd) {
    // 1. Member 조회
    // 2. 탈퇴 가능 여부 확인 (진행중인 주문 체크 제외)
    // 3. 회원 상태 변경 (ACTIVE → WITHDRAWN)
    // 4. 탈퇴 정보 저장
}
```

**Note**: 진행중인 주문 확인 제외 (현재 주문 Aggregate 없음)

---

#### 1.4 IntegrateMemberAccountUseCase (계정 통합)

**책임**:
1. 회원 타입 변경: `PHONE → KAKAO`
2. 카카오 제공 정보로 업데이트: `kakaoId`, `name`, `email`, `profileImageUrl`
3. 통합 이력 저장: `integratedAt`

**트랜잭션 경계**:
```java
@Transactional
public void execute(IntegrateMemberAccountCommand cmd) {
    // 1. 기존 PHONE 회원 조회 (phoneNumber)
    // 2. loginType = KAKAO로 변경
    // 3. 카카오 정보로 업데이트
    // 4. integratedAt 기록
    // 5. MemberIntegrated 이벤트 발행
}
```

**Note**: 포인트/쿠폰 합산 제외 (기존 데이터 유지)

---

### 2. Commands 정의

#### RegisterMemberCommand (회원 가입)
```java
public record RegisterMemberCommand(
    LoginType loginType,        // KAKAO, PHONE
    String phoneNumber,          // 필수
    String name,                 // 필수
    String password,             // PHONE 타입만
    String kakaoId,              // KAKAO 타입만
    String email,                // 선택
    String nickname,             // 선택
    Gender gender,               // 선택
    LocalDate birthday,          // 선택
    Integer birthYear            // 선택
) {
    // Compact Constructor: 유효성 검증
    public RegisterMemberCommand {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("phoneNumber는 필수입니다");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name은 필수입니다");
        }
        if (loginType == LoginType.PHONE && (password == null || password.isBlank())) {
            throw new IllegalArgumentException("PHONE 타입은 password가 필수입니다");
        }
        if (loginType == LoginType.KAKAO && (kakaoId == null || kakaoId.isBlank())) {
            throw new IllegalArgumentException("KAKAO 타입은 kakaoId가 필수입니다");
        }
    }
}
```

---

#### LoginCommand (로그인)
```java
public record LoginCommand(
    LoginType loginType,         // KAKAO, PHONE
    String phoneNumber,          // PHONE 타입
    String password,             // PHONE 타입
    String kakaoToken            // KAKAO 타입
) {
    // Compact Constructor: 유효성 검증
    public LoginCommand {
        if (loginType == LoginType.PHONE) {
            if (phoneNumber == null || phoneNumber.isBlank()) {
                throw new IllegalArgumentException("PHONE 타입은 phoneNumber가 필수입니다");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("PHONE 타입은 password가 필수입니다");
            }
        } else if (loginType == LoginType.KAKAO) {
            if (kakaoToken == null || kakaoToken.isBlank()) {
                throw new IllegalArgumentException("KAKAO 타입은 kakaoToken이 필수입니다");
            }
        }
    }
}
```

---

#### WithdrawMemberCommand (탈퇴)
```java
public record WithdrawMemberCommand(
    Long memberId,
    String withdrawalReason
) {
    // Compact Constructor: 유효성 검증
    public WithdrawMemberCommand {
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("memberId는 필수입니다");
        }
        if (withdrawalReason == null || withdrawalReason.isBlank()) {
            throw new IllegalArgumentException("withdrawalReason은 필수입니다");
        }
    }
}
```

---

#### IntegrateMemberAccountCommand (계정 통합)
```java
public record IntegrateMemberAccountCommand(
    String phoneNumber,          // 기존 PHONE 회원 식별
    String kakaoId,              // 카카오 ID
    String kakaoName,
    String kakaoEmail,
    String kakaoProfileImageUrl
) {
    // Compact Constructor: 유효성 검증
    public IntegrateMemberAccountCommand {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("phoneNumber는 필수입니다");
        }
        if (kakaoId == null || kakaoId.isBlank()) {
            throw new IllegalArgumentException("kakaoId는 필수입니다");
        }
    }
}
```

---

### 3. Responses 정의

#### LoginResponse (로그인 응답)
```java
public record LoginResponse(
    String accessToken,
    String refreshToken,
    Long memberId,
    String name
) {}
```

**Response 예시**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "memberId": 1,
  "name": "홍길동"
}
```

**Note**: `refreshToken`은 HTTP-Only Cookie로 전달 (Response Body에는 포함하지 않음)

---

#### RegisterMemberResponse (회원 가입 응답)
```java
public record RegisterMemberResponse(
    Long memberId,
    LoginType loginType,
    String phoneNumber
) {}
```

**Response 예시**:
```json
{
  "memberId": 1,
  "loginType": "KAKAO",
  "phoneNumber": "01012345678"
}
```

---

### 4. Outbox Pattern

#### Outbox Entity
```java
@Entity
@Table(name = "member_joined_message_outbox")
public class MemberJoinedMessageOutboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Column(name = "message_payload", columnDefinition = "TEXT")
    private String messagePayload;  // JSON

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed", nullable = false)
    private boolean processed;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    // Getter/Setter (Lombok 금지)
}
```

---

#### 이벤트 발행 순서

```
1. RegisterMemberUseCase.executeInTransaction()
   ├─ Member 저장
   └─ MemberJoinedMessageOutbox 저장 (동일 트랜잭션)

2. Outbox Poller (별도 Batch)
   ├─ SELECT * FROM member_joined_message_outbox WHERE processed = false
   ├─ SQS 메시지 발행
   └─ UPDATE processed = true

3. 알림톡 서비스 (SQS Consumer)
   └─ 알림톡 발송
```

**트랜잭션 경계**:
- ✅ **트랜잭션 내부**: Member 저장 + Outbox 저장
- ✅ **트랜잭션 외부**: SQS 발행 (Outbox Poller)

---

## 💾 Persistence Layer

### 1. JPA Entity 설계

#### 1.1 Member Entity

```java
@Entity
@Table(
    name = "member",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "phone_number"),
        @UniqueConstraint(columnNames = "kakao_id")
    }
)
public class MemberJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, length = 20)
    private LoginType loginType;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;  // Unique Index

    @Column(name = "kakao_id", length = 100)
    private String kakaoId;  // Unique Index

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "withdrawal_reason", length = 500)
    private String withdrawalReason;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "integrated_at")
    private LocalDateTime integratedAt;

    // Getter/Setter (Lombok 금지)
    // ...
}
```

**인덱스 전략** (최소화):
- ✅ `phone_number`: Unique Index (가입/로그인 조회 필수)
- ✅ `kakao_id`: Unique Index (카카오 로그인 조회 필수)

---

#### 1.2 MemberJoinedMessageOutbox Entity

```java
@Entity
@Table(
    name = "member_joined_message_outbox",
    indexes = {
        @Index(name = "idx_processed_created", columnList = "processed, created_at")
    }
)
public class MemberJoinedMessageOutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, length = 20)
    private LoginType loginType;

    @Column(name = "message_payload", columnDefinition = "TEXT")
    private String messagePayload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed", nullable = false)
    private boolean processed = false;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    // Getter/Setter (Lombok 금지)
    // ...
}
```

**인덱스 전략**:
- ✅ `(processed, created_at)`: Composite Index (Outbox Poller 조회용)

---

### 2. Repository 설계

#### 2.1 Command Adapter (저장용)

```java
@Component
public class MemberCommandPersistenceAdapter implements SaveMemberPort {

    private final MemberJpaRepository memberRepository;
    private final MemberEntityMapper mapper;

    @Override
    public Member save(Member member) {
        MemberJpaEntity entity = mapper.toEntity(member);
        MemberJpaEntity saved = memberRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

---

#### 2.2 Query Adapter (조회용)

```java
@Component
public class MemberQueryPersistenceAdapter implements LoadMemberPort {

    private final MemberJpaRepository memberRepository;
    private final MemberEntityMapper mapper;

    @Override
    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Member> findByKakaoId(String kakaoId) {
        return memberRepository.findByKakaoId(kakaoId)
            .map(mapper::toDomain);
    }

    @Override
    public List<Member> findInactiveMembers(LocalDateTime threshold) {
        return memberRepository.findByStatusAndLastLoginAtBefore(
            MemberStatus.ACTIVE, threshold
        ).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
```

**Note**: QueryDSL DTO Projection 제외 → Entity 조회 → Domain 변환

---

### 3. Redis 전략

#### 3.1 Refresh Token 저장

```java
@Component
public class RefreshTokenRedisAdapter {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(Long memberId, String token) {
        String key = "refresh_token:" + memberId;
        redisTemplate.opsForValue().set(key, token, 7, TimeUnit.DAYS);
    }

    public Optional<String> getRefreshToken(Long memberId) {
        String key = "refresh_token:" + memberId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void deleteRefreshToken(Long memberId) {
        String key = "refresh_token:" + memberId;
        redisTemplate.delete(key);
    }
}
```

**Redis Key 전략**:
- Key: `refresh_token:{memberId}`
- Value: `{tokenValue}`
- TTL: 7일

---

#### 3.2 Active Session (동시 로그인 방지)

```java
@Component
public class ActiveSessionRedisAdapter {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveActiveSession(Long memberId, String accessToken) {
        String key = "active_session:" + memberId;
        redisTemplate.opsForValue().set(key, accessToken, 1, TimeUnit.HOURS);
    }

    public Optional<String> getActiveSession(Long memberId) {
        String key = "active_session:" + memberId;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void deleteActiveSession(Long memberId) {
        String key = "active_session:" + memberId;
        redisTemplate.delete(key);
    }
}
```

**Redis Key 전략**:
- Key: `active_session:{memberId}`
- Value: `{accessToken}`
- TTL: 1시간

---

### 4. Batch 작업 (탈퇴 회원 삭제)

```java
@Component
public class WithdrawnMemberCleanupBatch {

    private final MemberJpaRepository memberRepository;

    @Scheduled(cron = "0 0 2 * * ?")  // 매일 새벽 2시
    @Transactional
    public void deleteWithdrawnMembers() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        List<MemberJpaEntity> withdrawn = memberRepository
            .findByStatusAndWithdrawnAtBefore(MemberStatus.WITHDRAWN, oneYearAgo);

        memberRepository.deleteAll(withdrawn);

        log.info("Deleted {} withdrawn members", withdrawn.size());
    }
}
```

**실행 주기**: 매일 새벽 2시
**삭제 조건**: `status = WITHDRAWN AND withdrawnAt < 현재시각 - 1년`

---

## 🌐 REST API Layer

### 1. API 엔드포인트 설계

#### 1.1 회원 가입 (핸드폰/PW만)

**카카오 로그인**은 Spring Security OAuth2.0으로 자동 처리 (명시적 API 불필요)

```
POST /api/v1/members/register
```

**Request**:
```json
{
  "loginType": "PHONE",
  "phoneNumber": "01012345678",
  "name": "홍길동",
  "password": "SecureP@ss123",
  "email": "hong@example.com",
  "nickname": "길동이"
}
```

**Response** (201 Created):
```json
{
  "memberId": 1,
  "loginType": "PHONE",
  "phoneNumber": "01012345678"
}
```

---

#### 1.2 로그인 (핸드폰/PW만)

```
POST /api/v1/members/login
```

**Request**:
```json
{
  "loginType": "PHONE",
  "phoneNumber": "01012345678",
  "password": "SecureP@ss123"
}
```

**Response** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "memberId": 1,
  "name": "홍길동"
}
```

**Cookie (HttpOnly)**:
```
Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; HttpOnly; Secure; Max-Age=604800
```

**Note**: `refreshToken`은 Cookie에만 저장 (Response Body 제외)

---

#### 1.3 회원 탈퇴

```
DELETE /api/v1/members/{memberId}
```

**Request**:
```json
{
  "withdrawalReason": "상품 가격이 비싸요"
}
```

**Response** (204 No Content):
```
(empty body)
```

---

#### 1.4 계정 통합

```
POST /api/v1/members/integrate
```

**Request**:
```json
{
  "phoneNumber": "01012345678",
  "kakaoId": "12345678",
  "kakaoName": "홍길동",
  "kakaoEmail": "hong@kakao.com",
  "kakaoProfileImageUrl": "https://..."
}
```

**Response** (200 OK):
```json
{
  "memberId": 1,
  "loginType": "KAKAO",
  "integratedAt": "2025-11-13T12:34:56"
}
```

---

### 2. 에러 응답 형식

#### ErrorInfo (기존 공통 DTO 사용)

```java
public record ErrorInfo(
    String errorCode,
    String message
) {}
```

**에러 응답 예시**:
```json
{
  "errorCode": "MEMBER_ALREADY_EXISTS",
  "message": "이미 가입된 회원입니다"
}
```

---

#### 에러 코드 정의

| 에러 코드 | 메시지 | HTTP Status |
|----------|--------|-------------|
| `MEMBER_ALREADY_EXISTS` | 이미 가입된 회원입니다 | 409 Conflict |
| `MEMBER_NOT_FOUND` | 존재하지 않는 회원입니다 | 404 Not Found |
| `MEMBER_LOCKED` | 5회 로그인 실패로 계정 잠금 | 403 Forbidden |
| `INVALID_PASSWORD` | 비밀번호가 올바르지 않습니다 | 401 Unauthorized |
| `INVALID_KAKAO_TOKEN` | 유효하지 않은 카카오 토큰입니다 | 401 Unauthorized |
| `MEMBER_ALREADY_WITHDRAWN` | 이미 탈퇴한 회원입니다 | 400 Bad Request |
| `MEMBER_INTEGRATION_FAILED` | 계정 통합에 실패했습니다 | 500 Internal Server Error |

---

### 3. 인증/인가 전략

#### 3.1 Spring Security 설정

**JWT 검증 필터**:
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) {
        // 1. Authorization 헤더에서 JWT 토큰 추출
        String token = extractToken(request);

        // 2. 토큰 검증
        if (token != null && jwtProvider.validateToken(token)) {
            // 3. 인증 정보 설정
            Authentication auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
```

---

#### 3.2 인증 불필요 API

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/members/register").permitAll()
                .requestMatchers("/api/v1/members/login").permitAll()
                .requestMatchers("/oauth2/**").permitAll()  // 카카오 OAuth2
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

---

#### 3.3 JWT 토큰 갱신 (자동 처리)

**Cookie에서 Refresh Token 자동 추출 → Access Token 갱신**

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) {
        String accessToken = extractToken(request);

        // Access Token 만료 시
        if (accessToken != null && jwtProvider.isExpired(accessToken)) {
            // Cookie에서 Refresh Token 추출
            String refreshToken = extractRefreshTokenFromCookie(request);

            // Refresh Token 검증 → 새 Access Token 발급
            if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
                String newAccessToken = jwtProvider.refreshAccessToken(refreshToken);
                response.setHeader("Authorization", "Bearer " + newAccessToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

**Note**: 명시적인 `/refresh` 엔드포인트 불필요 (필터에서 자동 처리)

---

### 4. API 문서화

#### 4.1 Swagger/OpenAPI 설정

```yaml
openapi: 3.0.0
info:
  title: Member Management API
  version: 1.0.0
  description: 회원 관리 (가입, 로그인, 탈퇴, 통합)

servers:
  - url: http://localhost:8080
    description: Local development server

paths:
  /api/v1/members/register:
    post:
      summary: 회원 가입 (핸드폰/PW)
      tags:
        - Member
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegisterMemberRequest'
      responses:
        '201':
          description: Created
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/RegisterMemberResponse'
        '409':
          description: Conflict - 이미 가입된 회원
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorInfo'
```

---

#### 4.2 REST Docs (Spring REST Docs)

```java
@Test
void registerMember() throws Exception {
    mockMvc.perform(post("/api/v1/members/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(document("member-register",
            requestFields(
                fieldWithPath("loginType").description("로그인 타입 (PHONE)"),
                fieldWithPath("phoneNumber").description("핸드폰 번호"),
                fieldWithPath("name").description("회원 이름"),
                fieldWithPath("password").description("비밀번호")
            ),
            responseFields(
                fieldWithPath("memberId").description("회원 ID"),
                fieldWithPath("loginType").description("로그인 타입"),
                fieldWithPath("phoneNumber").description("핸드폰 번호")
            )
        ));
}
```

---

## 🚀 구현 가이드

### 1. 개발 순서

#### Phase 1: Domain Layer (1-2일)
1. ✅ Value Objects (LoginType, MemberStatus, Gender)
2. ✅ Member Aggregate (속성, 비즈니스 메서드)
3. ✅ Domain Events (MemberRegistered, MemberWithdrawn, etc.)
4. ✅ Domain Unit Tests (TDD Red → Green → Refactor)

---

#### Phase 2: Persistence Layer (1-2일)
1. ✅ JPA Entities (MemberJpaEntity, MemberJoinedMessageOutboxEntity)
2. ✅ Repositories (MemberJpaRepository)
3. ✅ Mappers (MemberEntityMapper)
4. ✅ Adapters (MemberCommandPersistenceAdapter, MemberQueryPersistenceAdapter)
5. ✅ Redis Adapters (RefreshTokenRedisAdapter, ActiveSessionRedisAdapter)
6. ✅ Persistence Integration Tests (Testcontainers)

---

#### Phase 3: Application Layer (2-3일)
1. ✅ Commands (RegisterMemberCommand, LoginCommand, etc.)
2. ✅ Responses (LoginResponse, RegisterMemberResponse)
3. ✅ Ports (SaveMemberPort, LoadMemberPort)
4. ✅ UseCases (RegisterMemberUseCase, LoginUseCase, WithdrawMemberUseCase, IntegrateMemberAccountUseCase)
5. ✅ Outbox Service (MemberJoinedMessageOutboxService)
6. ✅ Application Service Unit Tests (Mocking)

---

#### Phase 4: REST API Layer (1-2일)
1. ✅ Request/Response DTOs
2. ✅ Controllers (MemberController)
3. ✅ API Mappers (MemberApiMapper)
4. ✅ Error Mappers (MemberErrorApiMapper)
5. ✅ Controller Unit Tests (MockMvc)
6. ✅ API Integration Tests (TestRestTemplate)

---

#### Phase 5: Infrastructure (1일)
1. ✅ Spring Security 설정 (JWT 필터)
2. ✅ Redis 설정
3. ✅ Batch 작업 (WithdrawnMemberCleanupBatch)
4. ✅ Outbox Poller (SQS 발행)

---

#### Phase 6: 문서화 & 배포 (1일)
1. ✅ Swagger/OpenAPI 문서
2. ✅ REST Docs 생성
3. ✅ README 작성
4. ✅ 배포 스크립트

---

### 2. 테스트 전략

#### 2.1 Domain Layer Tests (단위 테스트)

```java
@Test
void 회원_가입_성공() {
    // Given
    Member member = Member.create(
        LoginType.PHONE,
        "01012345678",
        "홍길동",
        "SecureP@ss123"
    );

    // When
    member.register();

    // Then
    assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    assertThat(member.getFailedLoginAttempts()).isEqualTo(0);
}

@Test
void 로그인_5회_실패_시_계정_잠금() {
    // Given
    Member member = Member.create(...);
    member.register();

    // When
    for (int i = 0; i < 5; i++) {
        member.failLogin();
    }

    // Then
    assertThat(member.getStatus()).isEqualTo(MemberStatus.LOCKED);
}
```

---

#### 2.2 Persistence Layer Tests (통합 테스트)

```java
@DataJpaTest
@Testcontainers
class MemberJpaRepositoryTest {

    @Container
    static MySQLContainer mysql = new MySQLContainer("mysql:8.0");

    @Test
    void phoneNumber로_회원_조회() {
        // Given
        MemberJpaEntity member = new MemberJpaEntity(...);
        memberRepository.save(member);

        // When
        Optional<MemberJpaEntity> found =
            memberRepository.findByPhoneNumber("01012345678");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("홍길동");
    }
}
```

---

#### 2.3 Application Layer Tests (단위 테스트, Mocking)

```java
@ExtendWith(MockitoExtension.class)
class RegisterMemberUseCaseTest {

    @Mock
    private SaveMemberPort saveMemberPort;

    @Mock
    private LoadMemberPort loadMemberPort;

    @InjectMocks
    private RegisterMemberService registerMemberService;

    @Test
    void 회원_가입_성공() {
        // Given
        RegisterMemberCommand cmd = new RegisterMemberCommand(...);
        when(loadMemberPort.findByPhoneNumber(anyString()))
            .thenReturn(Optional.empty());

        // When
        RegisterMemberResponse response = registerMemberService.execute(cmd);

        // Then
        assertThat(response.memberId()).isNotNull();
        verify(saveMemberPort, times(1)).save(any(Member.class));
    }
}
```

---

#### 2.4 REST API Tests (API 통합 테스트)

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void 회원_가입_API_성공() {
        // Given
        RegisterMemberApiRequest request = new RegisterMemberApiRequest(...);

        // When
        ResponseEntity<RegisterMemberApiResponse> response =
            restTemplate.postForEntity(
                "/api/v1/members/register",
                request,
                RegisterMemberApiResponse.class
            );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().memberId()).isNotNull();
    }
}
```

---

### 3. Zero-Tolerance 규칙 체크리스트

#### Domain Layer
- [ ] ❌ Lombok 사용 없음 (`@Data`, `@Builder`, `@Getter`, `@Setter` 금지)
- [ ] ✅ Law of Demeter 준수 (Getter 체이닝 금지)
- [ ] ✅ Tell, Don't Ask 패턴 적용
- [ ] ✅ 모든 public 메서드에 Javadoc 작성

#### Application Layer
- [ ] ✅ `@Transactional` 내 외부 API 호출 없음
- [ ] ✅ Transaction 경계 명확히 분리 (executeInTransaction vs executeExternal)
- [ ] ✅ Command는 Record 패턴 사용
- [ ] ✅ UseCase는 단일 책임 원칙 준수

#### Persistence Layer
- [ ] ❌ JPA 관계 어노테이션 사용 없음 (`@ManyToOne`, `@OneToMany` 금지)
- [ ] ✅ Long FK 전략 사용 (`private Long userId;`)
- [ ] ✅ Entity Mapper 사용 (Domain ↔ Entity 변환)
- [ ] ✅ QueryDSL DTO Projection 제외 (Entity → Domain 변환)

#### REST API Layer
- [ ] ✅ Request/Response DTO는 Record 패턴
- [ ] ✅ Controller는 UseCase 위임만 (비즈니스 로직 없음)
- [ ] ✅ API Mapper 사용 (API DTO ↔ Application Command/Response)
- [ ] ✅ ErrorInfo 사용 (에러 응답 통일)

---

### 4. ArchUnit 검증 규칙

```java
@Test
void Domain_Layer는_Lombok을_사용하지_않는다() {
    noClasses()
        .that().resideInPackage("..domain..")
        .should().dependOnClassesThat().resideInPackage("lombok..")
        .check(importedClasses);
}

@Test
void Application_Layer는_Transactional_내_외부_API_호출_없음() {
    noMethods()
        .that().areAnnotatedWith(Transactional.class)
        .should().callMethodWhere(/* 외부 API 호출 검증 */)
        .check(importedClasses);
}

@Test
void Persistence_Layer는_JPA_관계_어노테이션_사용_없음() {
    noFields()
        .that().areDeclaredInClassesThat().resideInPackage("..persistence..")
        .should().beAnnotatedWith(OneToMany.class)
        .orShould().beAnnotatedWith(ManyToOne.class)
        .check(importedClasses);
}
```

---

## ✅ 완료 기준 (Definition of Done)

### 기능 완료
- [ ] 회원 가입 (핸드폰/PW, 카카오 OAuth2.0) 정상 작동
- [ ] 로그인 (JWT 토큰 발급) 정상 작동
- [ ] 회원 탈퇴 (1년 보관) 정상 작동
- [ ] 계정 통합 (핸드폰 → 카카오) 정상 작동
- [ ] Outbox → SQS → 알림톡 발송 정상 작동
- [ ] Batch 작업 (탈퇴 회원 삭제) 정상 작동

### 테스트 완료
- [ ] Domain Unit Tests (TDD) 통과
- [ ] Application Unit Tests (Mocking) 통과
- [ ] Persistence Integration Tests (Testcontainers) 통과
- [ ] API Integration Tests (TestRestTemplate) 통과
- [ ] ArchUnit Tests 통과 (Zero-Tolerance 규칙 검증)

### 문서화 완료
- [ ] Swagger/OpenAPI 문서 생성
- [ ] REST Docs 생성
- [ ] README 작성 (Quick Start, API 명세)

### 배포 준비
- [ ] Git Pre-commit Hooks 통과 (Transaction 경계 검증)
- [ ] Checkstyle 통과 (Javadoc, 네이밍 규칙)
- [ ] 빌드 성공 (`./gradlew build`)
- [ ] PR 생성 (`gh pr create`)

---

## 📚 참고 자료

### 프로젝트 문서
- [Spring Standards README](../../README.md)
- [Coding Convention](../coding_convention/)
- [TDD Workflow Guide](../LANGFUSE_USAGE_GUIDE.md)

### 아키텍처 패턴
- [Hexagonal Architecture (Ports & Adapters)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design (DDD)](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)

### Spring Boot 3.5.x + Java 21
- [Spring Boot 3.5.x Documentation](https://docs.spring.io/spring-boot/docs/3.5.x/reference/html/)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)

---

**이 PRD는 구현 가이드이며, 실제 구현 시 비즈니스 요구사항 변경에 따라 조정될 수 있습니다.**
