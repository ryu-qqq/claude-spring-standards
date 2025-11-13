# 🎯 TDD Plan: [MEMBER-001] Member Aggregate 개발 (Domain Layer)

## 📋 PRD 요약
- **PRD 파일**: `docs/prd/member-management.md`
- **Layer**: Domain Layer
- **Aggregate**: Member
- **핵심 기능**: 회원 가입, 로그인, 탈퇴, 계정 통합

## 🎯 주요 요구사항

### 1. Member Aggregate 설계
- **LoginType**: KAKAO, PHONE
- **MemberStatus**: ACTIVE, INACTIVE, LOCKED, WITHDRAWN
- **필수 속성**: memberId, loginType, phoneNumber, name, status
- **선택 속성**: nickname, email, gender, birthday, birthYear
- **카카오 전용**: kakaoId, profileImageUrl
- **핸드폰 전용**: password (BCrypt)
- **상태 관리**: lastLoginAt, failedLoginAttempts
- **탈퇴 관련**: withdrawalReason, withdrawnAt
- **통합 관련**: integratedAt

### 2. 비즈니스 규칙
- **로그인 실패**: 5회 초과 시 `status = LOCKED`
- **계정 잠금**: 관리자만 해제 가능
- **탈퇴**: `status = WITHDRAWN`, 1년 보관 후 삭제
- **계정 통합**: 핸드폰 → 카카오 (같은 phoneNumber)

### 3. Domain Events
- `MemberRegistered`: 회원 가입 완료
- `MemberWithdrawn`: 회원 탈퇴
- `MemberLocked`: 계정 잠금 (5회 실패)
- `MemberIntegrated`: 계정 통합 완료

---

## 🏗️ Domain Layer TDD 계획

### Test 1: Value Objects 생성 및 검증 ✅ COMPLETED

#### Red Phase (실패 테스트 작성)
**목표**: LoginType, MemberStatus, Gender Enum 테스트

**체크리스트**:
- [x] `LoginTypeTest.java` 작성
  - [x] KAKAO, PHONE 타입 존재 확인 테스트
  - [x] valueOf() 정상 작동 테스트
- [x] `MemberStatusTest.java` 작성
  - [x] ACTIVE, INACTIVE, LOCKED, WITHDRAWN 존재 확인
  - [x] 상태 전이 규칙 테스트 (ACTIVE → LOCKED 등)
- [x] `GenderTest.java` 작성
  - [x] MALE, FEMALE, OTHER 존재 확인
- [x] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/test/java/.../LoginTypeTest.java
git add domain/src/test/java/.../MemberStatusTest.java
git add domain/src/test/java/.../GenderTest.java
git commit -m "test(domain): Value Objects 테스트 추가 (Red)"
```

#### Green Phase (최소 구현)
**목표**: Enum 구현 (테스트 통과)

**체크리스트**:
- [x] `LoginType.java` 구현 (KAKAO, PHONE)
- [x] `MemberStatus.java` 구현 (ACTIVE, INACTIVE, LOCKED, WITHDRAWN)
- [x] `Gender.java` 구현 (MALE, FEMALE, OTHER)
- [x] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../LoginType.java
git add domain/src/main/java/.../MemberStatus.java
git add domain/src/main/java/.../Gender.java
git commit -m "feat(domain): Value Objects 구현 (Green)"
```

#### Refactor Phase (리팩토링)
**체크리스트**:
- [x] Enum에 Javadoc 추가
- [x] 상태 전이 규칙 메서드 추가 (`canTransitionTo()`)
- [x] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberStatus.java
git commit -m "refactor(domain): MemberStatus 상태 전이 규칙 추가"
```

---

### Test 2: Member Aggregate 생성 (Factory Method)

#### Red Phase (실패 테스트 작성)
**목표**: Member 생성 테스트 (PHONE 타입)

**체크리스트**:
- [ ] `MemberDomainFixture.java` 생성 (testFixtures/)
  - [ ] `createPhoneMember()`: 핸드폰 회원 생성
  - [ ] `createKakaoMember()`: 카카오 회원 생성
- [ ] `MemberDomainTest.java` 작성
  - [ ] `핸드폰_회원_생성_성공()` 테스트
  - [ ] 필수 속성 검증 (phoneNumber, name, password)
  - [ ] 초기 상태 검증 (status = ACTIVE, failedLoginAttempts = 0)
- [ ] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/testFixtures/java/.../MemberDomainFixture.java
git add domain/src/test/java/.../MemberDomainTest.java
git commit -m "test(domain): Member 생성 테스트 추가 (Red)"
```

#### Green Phase (최소 구현)
**목표**: MemberDomain 클래스 구현

**체크리스트**:
- [ ] `MemberDomain.java` 생성
  - [ ] Private 생성자 (Factory Method 패턴)
  - [ ] `createPhoneMember()` Factory Method
  - [ ] 필수 속성 추가 (memberId, loginType, phoneNumber, name, password, status, failedLoginAttempts)
  - [ ] Getter 메서드 (Plain Java, Lombok 금지)
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "feat(domain): MemberDomain 생성 로직 구현 (Green)"
```

#### Refactor Phase (리팩토링)
**체크리스트**:
- [ ] Javadoc 추가 (`@author`, `@since`)
- [ ] 생성자 검증 로직 추가 (null 체크)
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "refactor(domain): MemberDomain 검증 로직 추가"
```

---

### Test 3: 카카오 회원 생성

#### Red Phase
**체크리스트**:
- [ ] `카카오_회원_생성_성공()` 테스트 추가
- [ ] 필수 속성 검증 (kakaoId, phoneNumber, name)
- [ ] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/test/java/.../MemberDomainTest.java
git commit -m "test(domain): 카카오 회원 생성 테스트 추가 (Red)"
```

#### Green Phase
**체크리스트**:
- [ ] `createKakaoMember()` Factory Method 추가
- [ ] 카카오 전용 속성 추가 (kakaoId, profileImageUrl)
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "feat(domain): 카카오 회원 생성 로직 구현 (Green)"
```

#### Refactor Phase
**체크리스트**:
- [ ] MemberDomainFixture에 `createKakaoMember()` 추가
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/testFixtures/java/.../MemberDomainFixture.java
git commit -m "refactor(domain): MemberDomainFixture 카카오 생성 추가"
```

---

### Test 4: 로그인 실패 횟수 관리 (Business Method)

#### Red Phase
**목표**: 로그인 실패 처리 및 계정 잠금

**체크리스트**:
- [ ] `로그인_실패_횟수_증가()` 테스트 추가
- [ ] `로그인_5회_실패_시_계정_잠금()` 테스트 추가
- [ ] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/test/java/.../MemberDomainTest.java
git commit -m "test(domain): 로그인 실패 처리 테스트 추가 (Red)"
```

#### Green Phase
**체크리스트**:
- [ ] `failLogin()` 메서드 추가 (failedLoginAttempts++)
- [ ] 5회 도달 시 `status = LOCKED` (Tell, Don't Ask)
- [ ] `lock()` private 메서드 추가
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "feat(domain): 로그인 실패 처리 로직 구현 (Green)"
```

#### Refactor Phase
**체크리스트**:
- [ ] `isLocked()` 메서드 추가 (상태 확인)
- [ ] MemberLocked 이벤트 발행 로직 추가 (준비)
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "refactor(domain): 계정 잠금 상태 확인 메서드 추가"
```

---

### Test 5: 로그인 성공 처리

#### Red Phase
**체크리스트**:
- [ ] `로그인_성공_시_lastLoginAt_업데이트()` 테스트 추가
- [ ] `로그인_성공_시_failedLoginAttempts_초기화()` 테스트 추가
- [ ] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/test/java/.../MemberDomainTest.java
git commit -m "test(domain): 로그인 성공 처리 테스트 추가 (Red)"
```

#### Green Phase
**체크리스트**:
- [ ] `successLogin()` 메서드 추가
  - [ ] `lastLoginAt = LocalDateTime.now()`
  - [ ] `failedLoginAttempts = 0`
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "feat(domain): 로그인 성공 처리 로직 구현 (Green)"
```

---

### Test 6: 회원 탈퇴 처리

#### Red Phase
**체크리스트**:
- [ ] `회원_탈퇴_성공()` 테스트 추가
- [ ] 탈퇴 후 상태 검증 (status = WITHDRAWN)
- [ ] 탈퇴 사유 검증 (withdrawalReason 필수)
- [ ] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/test/java/.../MemberDomainTest.java
git commit -m "test(domain): 회원 탈퇴 처리 테스트 추가 (Red)"
```

#### Green Phase
**체크리스트**:
- [ ] `withdraw(String withdrawalReason)` 메서드 추가
  - [ ] `status = WITHDRAWN`
  - [ ] `withdrawalReason` 저장
  - [ ] `withdrawnAt = LocalDateTime.now()`
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "feat(domain): 회원 탈퇴 처리 로직 구현 (Green)"
```

#### Refactor Phase
**체크리스트**:
- [ ] MemberWithdrawn 이벤트 발행 로직 추가 (준비)
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "refactor(domain): 탈퇴 이벤트 발행 준비"
```

---

### Test 7: 계정 통합 (PHONE → KAKAO)

#### Red Phase
**체크리스트**:
- [ ] `핸드폰_회원_카카오_통합_성공()` 테스트 추가
- [ ] 통합 후 타입 변경 확인 (PHONE → KAKAO)
- [ ] 카카오 정보 업데이트 확인 (kakaoId, name, email)
- [ ] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/test/java/.../MemberDomainTest.java
git commit -m "test(domain): 계정 통합 처리 테스트 추가 (Red)"
```

#### Green Phase
**체크리스트**:
- [ ] `integrateWithKakao()` 메서드 추가
  - [ ] `loginType = KAKAO`
  - [ ] 카카오 정보 업데이트 (kakaoId, name, email, profileImageUrl)
  - [ ] `integratedAt = LocalDateTime.now()`
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "feat(domain): 계정 통합 처리 로직 구현 (Green)"
```

#### Refactor Phase
**체크리스트**:
- [ ] MemberIntegrated 이벤트 발행 로직 추가 (준비)
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "refactor(domain): 통합 이벤트 발행 준비"
```

---

### Test 8: Domain Events 구현

#### Red Phase
**체크리스트**:
- [ ] `MemberRegistered` Record 테스트 추가
- [ ] `MemberWithdrawn` Record 테스트 추가
- [ ] `MemberLocked` Record 테스트 추가
- [ ] `MemberIntegrated` Record 테스트 추가
- [ ] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/test/java/.../event/MemberDomainEventTest.java
git commit -m "test(domain): Domain Events 테스트 추가 (Red)"
```

#### Green Phase
**체크리스트**:
- [ ] `MemberRegistered.java` Record 구현
- [ ] `MemberWithdrawn.java` Record 구현
- [ ] `MemberLocked.java` Record 구현
- [ ] `MemberIntegrated.java` Record 구현
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../event/MemberRegistered.java
git add domain/src/main/java/.../event/MemberWithdrawn.java
git add domain/src/main/java/.../event/MemberLocked.java
git add domain/src/main/java/.../event/MemberIntegrated.java
git commit -m "feat(domain): Domain Events 구현 (Green)"
```

#### Refactor Phase
**체크리스트**:
- [ ] MemberDomain에 이벤트 발행 메서드 추가
  - [ ] `registerDomainEvent(Object event)`
  - [ ] `getDomainEvents()` → List 반환
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "refactor(domain): Domain Events 발행 메커니즘 추가"
```

---

### Test 9: Law of Demeter 검증

#### Red Phase
**체크리스트**:
- [ ] Getter 체이닝 금지 확인 테스트
- [ ] Tell, Don't Ask 패턴 적용 확인
- [ ] **실행**: `./gradlew test` → **실패 확인** ❌

**커밋**:
```bash
git add domain/src/test/java/.../MemberDomainTest.java
git commit -m "test(domain): Law of Demeter 검증 테스트 추가 (Red)"
```

#### Green Phase
**체크리스트**:
- [ ] 모든 상태 변경 메서드를 Business Method로 캡슐화
- [ ] Getter는 조회 목적만 (상태 변경 X)
- [ ] **실행**: `./gradlew test` → **통과 확인** ✅

**커밋**:
```bash
git add domain/src/main/java/.../MemberDomain.java
git commit -m "feat(domain): Tell Don't Ask 패턴 적용 (Green)"
```

---

### Test 10: 최종 검증

#### Tidy Phase
**목표**: Zero-Tolerance 규칙 최종 검증

**체크리스트**:
- [ ] **No Lombok**: `@Data`, `@Builder`, `@Getter`, `@Setter` 없음
- [ ] **No JPA 어노테이션**: `@Entity`, `@ManyToOne` 등 없음
- [ ] **Law of Demeter**: Getter 체이닝 없음
- [ ] **Javadoc**: 모든 public 클래스/메서드에 작성
- [ ] **ArchUnit**: Domain Layer 규칙 통과

**실행**:
```bash
# ArchUnit 실행
./gradlew test --tests "*DomainLayerArchitectureTest"

# validation-helper.py 실행 (선택)
python3 .claude/hooks/scripts/validation-helper.py domain/src/main/java/.../MemberDomain.java
```

**커밋**:
```bash
git add domain/
git commit -m "docs(domain): Javadoc 작성 및 최종 검증 완료"
```

---

## ✅ 최종 검증 체크리스트

### 자동 검증 (3-Tier)
- [ ] **Tier 1**: validation-helper.py 실시간 검증 통과
- [ ] **Tier 2**: Git pre-commit hooks 통과 (트랜잭션 경계는 Application Layer)
- [ ] **Tier 3**: ArchUnit 테스트 통과 (빌드 시)

### Zero-Tolerance 규칙
- [ ] ❌ Lombok 사용 없음
- [ ] ✅ Law of Demeter 준수 (no `obj.getX().getY()`)
- [ ] ✅ Tell Don't Ask 패턴 적용
- [ ] ✅ Factory Method 패턴 사용
- [ ] ✅ Encapsulation 엄격 (private setter)
- [ ] ✅ Javadoc 작성 (`@author`, `@since`)

### 테스트 커버리지
- [ ] 모든 Business Methods 테스트 완료
- [ ] 모든 Factory Methods 테스트 완료
- [ ] 모든 Domain Events 테스트 완료
- [ ] Edge Cases 테스트 완료

---

## 🚀 다음 단계

Domain Layer 완료 후:
1. **Application Layer TDD** 시작 (`MEMBER-001-application-plan.md`)
2. **Persistence Layer TDD** 시작 (`MEMBER-001-persistence-plan.md`)
3. **REST API Layer TDD** 시작 (`MEMBER-001-rest-api-plan.md`)

---

**💡 Tip**: `/kb/domain/go` 커맨드로 각 테스트를 순서대로 진행하세요!
