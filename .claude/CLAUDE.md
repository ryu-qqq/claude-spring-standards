# Spring Standards Project - Claude Code Configuration

이 프로젝트는 **Spring Boot 3.5.x + Java 21** 기반의 헥사고날 아키텍처 엔터프라이즈 표준 프로젝트입니다.

---

## 🚀 혁신: Kent Beck TDD + LangFuse 메트릭 추적

이 프로젝트의 핵심 철학은 **테스트 주도 개발 (TDD)**과 **작은 커밋**입니다:

### Kent Beck의 TDD 사이클

```
Red (테스트 작성) → Green (최소 구현) → Refactor (리팩토링) → Commit
         ↓                ↓                  ↓              ↓
    실패하는 테스트     테스트 통과         코드 개선      작은 변경 커밋
         ↓                ↓                  ↓              ↓
    .claude/hooks/track-tdd-cycle.sh (자동 추적)
         ↓
    LangFuse (메트릭 수집)
         ├─ TDD 사이클 시간 측정
         ├─ 커밋 크기 추적 (작을수록 좋음)
         ├─ 테스트 커버리지 변화
         └─ 리팩토링 빈도 분석
```

### 시스템 아키텍처

```
개발자: TDD 사이클 (Red → Green → Refactor → Commit)
         ↓
Claude Code: 비즈니스 로직 구현
         ↓
./gradlew test (테스트 실행)
         ↓
track-tdd-cycle.sh (Hook 트리거)
         ├─ 테스트 결과 파싱
         ├─ 커밋 정보 추출
         └─ log-to-langfuse.py 호출
         ↓
LangFuse API (메트릭 업로드)
         ├─ TDD Phase 추적 (Red/Green/Refactor)
         ├─ 커밋 크기 측정
         ├─ 테스트 성공률
         └─ ArchUnit 검증 결과
```

### 핵심 메트릭

| 메트릭 | 측정 항목 | 목표 |
|--------|----------|------|
| **TDD 사이클 시간** | Red → Commit 평균 시간 | < 15분 |
| **커밋 크기** | 파일 변경 수, 라인 수 | 작을수록 좋음 |
| **테스트 성공률** | 테스트 통과율 | > 95% |
| **리팩토링 빈도** | Green 후 Refactor 비율 | > 50% |
| **ArchUnit 준수율** | 아키텍처 규칙 위반 | 0회 |

**핵심 성과**: 테스트가 컨벤션을 강제하고, 작은 커밋이 빠른 피드백을 보장

### LangFuse 통합 (자동 메트릭 수집)

**목적**: TDD 사이클과 개발 메트릭을 자동으로 수집하여 개발 효율 분석

**자동 추적 이벤트**:
1. **tdd_commit**: Git 커밋 시
   - 커밋 메시지, 해시, 변경 파일 수, 라인 수
   - TDD Phase 자동 감지 (Red/Green/Refactor)
2. **tdd_test**: 테스트 실행 시 (`./gradlew test`)
   - 테스트 성공/실패 수, 실행 시간
3. **archunit_check**: ArchUnit 실행 시
   - 아키텍처 규칙 위반 수

**설정**:
```bash
# 환경 변수 설정 (선택적)
export LANGFUSE_PUBLIC_KEY="pk-lf-..."
export LANGFUSE_SECRET_KEY="sk-lf-..."
export LANGFUSE_HOST="https://us.cloud.langfuse.com"

# 환경 변수 없어도 작동 (로컬 JSONL 로그에만 저장)
```

**로그 파일 위치**:
- `~/.claude/logs/tdd-cycle.jsonl` (로컬 JSONL 로그)

**참고**:
- LangFuse 환경 변수 없어도 로컬 JSONL 로그는 항상 저장
- `requests` 라이브러리 없어도 작동 (로컬 로그만)

---

## 📚 코딩 규칙 (docs/coding_convention/)

### 레이어별 규칙 구조

```
docs/coding_convention/
├── 01-adapter-rest-api-layer/  (18개 규칙)
│   ├── controller-design/
│   ├── dto-patterns/
│   ├── exception-handling/
│   ├── mapper-patterns/
│   ├── package-guide/
│   └── testing/
│
├── 02-domain-layer/  (15개 규칙)
│   ├── aggregate-design/
│   ├── law-of-demeter/  ⭐ Law of Demeter 엄격 적용
│   ├── package-guide/
│   └── testing/
│
├── 03-application-layer/  (18개 규칙)
│   ├── assembler-pattern/
│   ├── dto-patterns/
│   ├── package-guide/
│   ├── testing/
│   ├── transaction-management/  ⭐ Transaction 경계 엄격 관리
│   └── usecase-design/
│
├── 04-persistence-layer/  (10개 규칙)
│   ├── jpa-entity-design/  ⭐ Long FK 전략 (관계 어노테이션 금지)
│   ├── package-guide/
│   ├── querydsl-optimization/
│   ├── repository-patterns/
│   └── testing/
│
├── 05-testing/  (12개 규칙)
│   ├── archunit-rules/
│   └── integration-testing/
│
├── 06-java21-patterns/  (8개 규칙)
│   ├── record-patterns/
│   ├── sealed-classes/
│   └── virtual-threads/
│
├── 07-enterprise-patterns/  (5개 규칙)
│   ├── caching/
│   ├── event-driven/
│   └── resilience/
│
├── 08-error-handling/  (5개 규칙)
│   ├── error-handling-strategy/
│   ├── domain-exception-design/
│   ├── global-exception-handler/
│   ├── error-response-format/
│   └── errorcode-management/
│
└── 09-orchestration-patterns/  (8개 규칙) ⭐ NEW
    ├── overview/  (3-Phase Lifecycle, Idempotency, WAL)
    ├── command-pattern/  (Record 패턴, Compact Constructor)
    ├── idempotency-handling/  (IdemKey, Race Condition 방지)
    ├── write-ahead-log/  (크래시 복구, Finalizer/Reaper)
    ├── outcome-modeling/  (Sealed interface, Pattern matching)
    ├── quick-start-guide/  (10분 실습)
    ├── security-guide/  (Rate Limiting, DoS 방지)
    └── automation-analysis/  (80-85% 자동화)
```

**총 98개 규칙** (기존 90개 + Orchestration 8개)

---

## 🏗️ 프로젝트 핵심 원칙

### 1. 아키텍처 패턴
- **헥사고날 아키텍처** (Ports & Adapters) - 의존성 역전
- **도메인 주도 설계** (DDD) - Aggregate 중심 설계
- **CQRS** - Command/Query 분리

### 2. 코드 품질 규칙 (Zero-Tolerance)
- **Lombok 금지** - Plain Java 사용 (Domain layer에서 특히 엄격)
- **Law of Demeter** - Getter 체이닝 금지 (`order.getCustomer().getAddress()` ❌)
- **Long FK 전략** - JPA 관계 어노테이션 금지, Long userId 사용
- **Transaction 경계** - `@Transactional` 내 외부 API 호출 절대 금지

### 3. Spring 프록시 제약사항 (중요!)
⚠️ **다음 경우 `@Transactional`이 작동하지 않습니다:**
- Private 메서드
- Final 클래스/메서드
- 같은 클래스 내부 호출 (`this.method()`)

---

## 🔧 자동화 시스템

### 1. TDD Workflow Tracking

**위치**: `.claude/hooks/track-tdd-cycle.sh`, `.claude/scripts/log-to-langfuse.py`

**목적**: Kent Beck TDD 사이클 자동 추적 및 메트릭 수집

#### 작동 원리

```
개발자: TDD 사이클 수행
    ↓
Red: 테스트 작성 (실패하는 테스트)
    ↓
Green: 최소 구현 (테스트 통과)
    ↓
Refactor: 코드 개선
    ↓
Commit: 작은 변경 커밋
    ↓
track-tdd-cycle.sh (자동 감지)
    ├─ git commit 감지 → TDD Phase 분석
    ├─ ./gradlew test 감지 → 테스트 결과 파싱
    └─ ArchUnit 감지 → 아키텍처 규칙 검증
         ↓
log-to-langfuse.py (메트릭 저장)
    ├─ JSONL 로그 (항상 작동)
    └─ LangFuse 업로드 (선택적)
         ↓
LangFuse Dashboard (분석)
    ├─ TDD 사이클 시간 분석
    ├─ 커밋 크기 추적
    ├─ 테스트 성공률 모니터링
    └─ 리팩토링 빈도 분석
```

#### 메트릭 수집

**자동 수집되는 메트릭**:
- **TDD Phase**: 커밋 메시지로 Red/Green/Refactor 자동 분류
- **Commit Size**: 변경된 파일 수, 라인 수
- **Test Results**: 통과/실패 테스트 수, 실행 시간
- **ArchUnit**: 아키텍처 규칙 위반 수

**로그 위치**:
- `~/.claude/logs/tdd-cycle.jsonl` (항상 저장)
- LangFuse Cloud (환경 변수 설정 시)

### 2. Kent Beck TDD 커맨드 (/kb)

**목적**: Plan 파일 기반으로 짧은 TDD 사이클(5-15분)을 실행하는 Layer별 커맨드

**핵심 개념**:
- **Plan 파일 기반**: `docs/prd/plans/{ISSUE-KEY}-{layer}-plan.md` 파일에서 다음 테스트 읽기
- **TDD 4단계**: Red (테스트 작성) → Green (최소 구현) → Refactor (리팩토링) → Tidy (정리)
- **TestFixture 필수**: 모든 레이어에서 Object Mother 패턴 사용
- **짧은 커밋 주기**: 테스트 하나당 커밋 (Red, Green, Refactor 각각 커밋)
- **Zero-Tolerance 자동 준수**: 각 레이어별 규칙 자동 검증

**Layer별 TDD 커맨드**:
```bash
# Domain Layer TDD
/kb/domain/go          # Plan 파일에서 다음 테스트 실행
/kb/domain/red         # Red: 테스트 작성 → 실패 확인
/kb/domain/green       # Green: 최소 구현 → 테스트 통과
/kb/domain/refactor    # Refactor: 코드 개선
/kb/domain/tidy        # Tidy: TestFixture 정리

# Application Layer TDD
/kb/application/go     # UseCase TDD 실행
/kb/application/red    # Transaction 경계 주의
/kb/application/green  # 최소 구현
/kb/application/refactor
/kb/application/tidy

# Persistence Layer TDD
/kb/persistence/go     # Repository/Adapter TDD 실행
/kb/persistence/red    # Long FK 전략 준수
/kb/persistence/green  # QueryDSL DTO Projection
/kb/persistence/refactor
/kb/persistence/tidy

# REST API Layer TDD
/kb/rest-api/go        # Controller TDD 실행
/kb/rest-api/red       # MockMvc 테스트 작성
/kb/rest-api/green     # RESTful 설계 준수
/kb/rest-api/refactor
/kb/rest-api/tidy

# Integration Tests
/kb/integration/go     # E2E 테스트 실행
```

**워크플로우 예시**:
```bash
# 1. Plan 파일 생성 (PRD → Plan)
docs/prd/plans/MEMBER-001-domain-plan.md

# 2. TDD 사이클 실행
/kb/domain/go
→ Plan 파일 읽기 → 다음 테스트 찾기
→ Red: 테스트 작성 → 실패 확인 → 커밋
→ Green: 최소 구현 → 통과 확인 → 커밋
→ Refactor: 코드 개선 → 통과 확인 → 커밋
→ Plan 파일에 완료 표시

# 3. 다음 테스트로 이동
/kb/domain/go (반복)
```

**Layer별 Zero-Tolerance 규칙**:
- **Domain**: Lombok 금지, Law of Demeter, Tell Don't Ask
- **Application**: Transaction 경계, CQRS 분리, Assembler 사용
- **Persistence**: Long FK 전략, QueryDSL DTO Projection, Lombok 금지
- **REST API**: RESTful 설계, DTO 패턴, Validation 필수

### 3. 실시간 메트릭 모니터링

**JSONL 로그 확인**:
```bash
# TDD 사이클 로그 실시간 모니터링
tail -f ~/.claude/logs/tdd-cycle.jsonl

# 출력 예시:
# {"timestamp":"2025-11-13T12:34:56Z","event_type":"tdd_commit","data":{"project":"claude-spring-standards","commit_hash":"a1b2c3d","commit_msg":"test: Order 생성 테스트 추가","tdd_phase":"red","files_changed":"2 files changed","lines_changed":"45 insertions","timestamp":"2025-11-13T12:34:56Z"}}
# {"timestamp":"2025-11-13T12:38:12Z","event_type":"tdd_test","data":{"project":"claude-spring-standards","test_status":"failed","tests_passed":"0","tests_failed":"1","duration_seconds":"3","timestamp":"2025-11-13T12:38:12Z"}}
# {"timestamp":"2025-11-13T12:45:23Z","event_type":"tdd_commit","data":{"project":"claude-spring-standards","commit_hash":"d4e5f6g","commit_msg":"impl: Order 생성 로직 구현","tdd_phase":"green","files_changed":"1 file changed","lines_changed":"28 insertions","timestamp":"2025-11-13T12:45:23Z"}}
```

**LangFuse 대시보드** (환경 변수 설정 시):
- TDD 사이클 시간 차트
- 커밋 크기 분포
- 테스트 성공률 트렌드
- Phase별 시간 소요 분석

### 5. Git Pre-commit Hooks (별도 시스템)

**위치**: `hooks/pre-commit`, `hooks/validators/`

- **트랜잭션 경계 검증**: `@Transactional` 내 외부 API 호출 차단
- **프록시 제약사항 검증**: Private/Final 메서드 `@Transactional` 차단
- **최종 안전망 역할**: 커밋 시 강제 검증

### 6. ArchUnit Tests

**위치**: `application/src/test/java/com/company/template/architecture/`

- **아키텍처 규칙 자동 검증**: 레이어 의존성, 네이밍 규칙
- **빌드 시 자동 실행**: 위반 시 빌드 실패

---

## 🎯 개발 워크플로우 (Kent Beck TDD)

### 1. TDD 사이클 워크플로우

```bash
# Red Phase: 실패하는 테스트 작성
vim domain/src/test/java/.../OrderTest.java
# → 테스트 작성 후 커밋
git add .
git commit -m "test: Order 생성 테스트 추가"
# → track-tdd-cycle.sh 자동 실행 (Phase: red)

# Green Phase: 최소 구현
vim domain/src/main/java/.../Order.java
# → 테스트 통과할 만큼만 구현
./gradlew test
# → track-tdd-cycle.sh 자동 실행 (test_status: success)
git add .
git commit -m "impl: Order 생성 로직 구현"
# → track-tdd-cycle.sh 자동 실행 (Phase: green)

# Refactor Phase: 코드 개선
vim domain/src/main/java/.../Order.java
# → 리팩토링 수행
./gradlew test
# → 테스트 여전히 통과 확인
git add .
git commit -m "refactor: Order 생성 로직 개선"
# → track-tdd-cycle.sh 자동 실행 (Phase: refactor)

# 결과: LangFuse에 3개 커밋 메트릭 자동 수집
```

### 2. 검증 워크플로우

```bash
# 특정 파일 검증
/validate-domain domain/src/main/java/.../Order.java

# 전체 프로젝트 검증
/validate-architecture

# ArchUnit 실행 (빌드 시 자동)
./gradlew test
# → track-tdd-cycle.sh가 ArchUnit 결과 자동 수집
```

### 3. 메트릭 분석 워크플로우

```bash
# JSONL 로그 확인
cat ~/.claude/logs/tdd-cycle.jsonl | jq .

# LangFuse 대시보드 확인 (환경 변수 설정 시)
# → https://us.cloud.langfuse.com
# → TDD 사이클 시간, 커밋 크기, 테스트 성공률 확인
```

---

## 🚨 Zero-Tolerance 규칙

다음 규칙은 **예외 없이** 반드시 준수해야 합니다:

### 1. Lombok 금지
- ❌ `@Data`, `@Builder`, `@Getter`, `@Setter` 등 모두 금지
- ✅ Pure Java getter/setter 직접 작성
- **검증**: ArchUnit (빌드 시)

### 2. Law of Demeter (Getter 체이닝 금지)
- ❌ `order.getCustomer().getAddress().getZip()`
- ✅ `order.getCustomerZipCode()` (Tell, Don't Ask)
- **검증**: 테스트 + 코드 리뷰

### 3. Long FK 전략 (JPA 관계 금지)
- ❌ `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`
- ✅ `private Long userId;` (Long FK 사용)
- **검증**: ArchUnit (빌드 시)

### 4. Transaction 경계
- ❌ `@Transactional` 내 외부 API 호출 (RestTemplate, WebClient 등)
- ✅ 트랜잭션은 짧게 유지, 외부 호출은 트랜잭션 밖에서
- **검증**: Git pre-commit hook

### 5. Javadoc 필수
- ❌ `@author`, `@since` 없는 public 클래스/메서드
- ✅ 모든 public 클래스/메서드에 Javadoc 포함
- **검증**: Checkstyle (빌드 시)

### 6. Scope 준수
- ❌ 요청하지 않은 추가 기능 구현
- ✅ 요청된 코드만 정확히 작성
- **검증**: 코드 리뷰

### 7. Orchestration Pattern (NEW) ⭐
- ❌ `executeInternal()`에 `@Transactional` 사용
- ✅ `executeInternal()`에 `@Async` 필수, 트랜잭션 밖에서 외부 API 호출
- ❌ Command에 Lombok (`@Data`, `@Builder` 등)
- ✅ Command는 Record 패턴 사용 (`public record XxxCommand`)
- ❌ Operation Entity에 IdemKey Unique 제약 없음
- ✅ `@UniqueConstraint(columnNames = {"idem_key"})` 필수
- ❌ Orchestrator가 `boolean`/`void` 반환 또는 Exception throw
- ✅ Orchestrator는 `Outcome` (Ok/Retry/Fail) 반환
- **검증**: ArchUnit (빌드 시) + Git pre-commit hook

---

## 🔧 통합 워크플로우: Claude Code + Cursor IDE

이 프로젝트는 **Claude Code**와 **Cursor IDE**를 통합하여 사용하도록 설계되었습니다.

### 🎯 설계 의도 (Design Intent)

```
┌─────────────────────────────────────────────────────────────┐
│ 1️⃣ Claude Code: 빠른 분석 & 설계                             │
├─────────────────────────────────────────────────────────────┤
│ - PRD (Product Requirements Document) 작성                   │
│ - Jira Task 분석 및 브랜치 생성                               │
│ - Technical Spec 작성 (Domain 모델, API 명세)                │
│ - 아키텍처 설계                                               │
└─────────────────────────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────────────────────────┐
│ 2️⃣ Cursor IDE: 빠른 Boilerplate 생성                         │
├─────────────────────────────────────────────────────────────┤
│ - .cursorrules 자동 로드 (Zero-Tolerance 규칙)               │
│ - 프로젝트 컨벤션 학습                                         │
│ - 코딩 표준 패턴 적용                                          │
│ - 반복적인 구조 코드 빠른 생성                                │
└─────────────────────────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────────────────────────┐
│ 3️⃣ Claude Code: 중요한 비즈니스 로직 구현                     │
├─────────────────────────────────────────────────────────────┤
│ - Serena Memory 기반 컨텍스트 유지                            │
│ - Domain 비즈니스 메서드 구현                                 │
│ - UseCase Transaction 경계 관리                              │
│ - 복잡한 Query 최적화                                         │
└─────────────────────────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────────────────────────┐
│ 4️⃣ Claude Code: 자동 검증 & PR 생성                          │
├─────────────────────────────────────────────────────────────┤
│ - /validate-architecture (ArchUnit)                         │
│ - Git Pre-commit Hooks (Transaction 경계)                   │
│ - gh pr create (자동 PR 생성)                                │
└─────────────────────────────────────────────────────────────┘
```

### 📁 시스템 역할 분리

#### Claude Code (`.claude/`) ⭐

**역할**: 설계, 비즈니스 로직, 검증, 자동화

**자동화 시스템**:
- **Dynamic Hooks**: 키워드 감지 → Layer 매핑 → 규칙 자동 주입 (A/B 테스트 검증 완료)
- **Cache 시스템**: 98개 규칙 → JSON → O(1) 검색 (90% 토큰 절감)
- **Hook 로깅**: hook-execution.jsonl → A/B 테스트 데이터 수집
- **Git Pre-commit Hooks**: 트랜잭션 경계 + Orchestration 자동 검증
- **ArchUnit**: 빌드 시 아키텍처 + Orchestration 자동 검증 (12개 규칙)
- **Orchestration 자동화**: 10개 파일 80-85% 자동 생성 (75% 시간 단축)

**Slash Commands**:
```bash
/code-gen-domain <name>          # Domain Aggregate 생성
/code-gen-usecase <name>         # Application UseCase 생성
/code-gen-controller <name>      # REST Controller 생성
/code-gen-orchestrator <Domain> <EventType>  # Orchestration Pattern 생성
/validate-domain <file>          # Domain layer 검증
/validate-architecture           # 전체 아키텍처 검증
/ai-review [pr-number]           # 통합 AI 리뷰 (Gemini + CodeRabbit + Codex)
/jira-task                       # Jira Task 분석 및 브랜치 생성
```

**성능 (A/B 테스트 검증)**:
- **컨벤션 위반**: 40회 → 0회 (100% 제거) ✅
- **Zero-Tolerance 준수율**: 0% → 100% ✅
- 토큰 사용량: 90% 절감
- 검증 속도: 73.6% 향상
- Orchestration 생성: 75% 시간 단축 (8분 → 2분)
- Orchestration 위반: 83-100% 감소 (12회 → 0-2회)

#### Cursor IDE 🚀

**역할**: Boilerplate 빠른 생성

**통합 방식**:
- **.cursorrules**: Cursor IDE가 자동으로 읽는 프로젝트 규칙
- **프로젝트 컨텍스트**: 기존 코드 패턴 자동 학습
- **AI 기반 생성**: 규칙 준수 코드 자동 생성

**사용 방법** (Cursor IDE에서):
```
사용자: "Order Aggregate를 생성해줘"

Cursor IDE:
1. .cursorrules 자동 로드
   → Lombok 금지, Law of Demeter 등 자동 적용

2. 프로젝트 기존 코드 패턴 분석
   → 프로젝트 표준 스타일 학습

3. AI 기반 코드 생성
   → 규칙 준수 Boilerplate 생성
```

### 🔄 실제 워크플로우 예시

#### 예시 1: Order Aggregate 개발

```bash
# 1. Claude Code: PRD 작성
"Order Aggregate PRD를 작성해줘. 주문 생성, 취소, 상태 변경이 필요해."
→ PRD 문서 생성: docs/prd/order-aggregate.md

# 2. Claude Code: Jira Task 분석
/jira-task
→ PROJ-123 분석 → feature/PROJ-123-order 브랜치 생성

# 3. Cursor IDE: Boilerplate 생성
"Order Aggregate를 생성해줘"
→ OrderDomain.java, OrderId.java, OrderStatus.java 등 생성

# 4. Claude Code: 비즈니스 로직 구현
"Order Domain에 비즈니스 메서드를 구현해줘:
- placeOrder(): 주문 생성
- cancelOrder(): 주문 취소 (PLACED 상태만 가능)
- confirmOrder(): 주문 확인"
→ 비즈니스 로직 구현 (Hook이 자동으로 Law of Demeter, Tell Don't Ask 규칙 주입)

# 5. Claude Code: 검증 및 PR
/validate-architecture
→ ArchUnit 테스트 통과
→ gh pr create 자동 실행
```

#### 예시 2: UseCase 개발

```bash
# 1. Cursor IDE: UseCase Boilerplate
"PlaceOrderUseCase를 생성해줘"
→ PlaceOrderUseCase.java (port/in/)
→ PlaceOrderCommand.java (dto/command/)
→ OrderResponse.java (dto/response/)

# 2. Claude Code: Transaction 경계 관리
"PlaceOrderUseCase에 비즈니스 로직을 구현해줘.
외부 결제 API 호출이 필요해."
→ executeInTransaction() 분리
→ 외부 API 호출은 트랜잭션 밖에서
→ Git Pre-commit Hook 자동 검증
```

#### 예시 3: Orchestration Pattern 개발 (NEW) ⭐

```bash
# 1. Claude Code: Orchestrator 자동 생성
/code-gen-orchestrator Order PlacementConfirmed

# 자동 생성 결과 (10개 파일, 80-85% 완성):
# application/
#   └── orchestration/
#       └── order/
#           ├── command/
#           │   └── OrderPlacementConfirmedCommand.java (Record)
#           ├── entity/
#           │   └── OrderPlacementConfirmedOperationEntity.java (@UniqueConstraint)
#           ├── finalizer/
#           │   └── OrderPlacementConfirmedFinalizer.java (@Scheduled)
#           ├── mapper/
#           │   └── OrderPlacementConfirmedMapper.java
#           ├── orchestrator/
#           │   └── OrderPlacementConfirmedOrchestrator.java (@Async)
#           ├── outcome/
#           │   └── OrderPlacementConfirmedOutcome.java (Sealed)
#           ├── reaper/
#           │   └── OrderPlacementConfirmedReaper.java (@Scheduled)
#           ├── repository/
#           │   └── OrderPlacementConfirmedOperationRepository.java
#           ├── status/
#           │   └── OrderPlacementConfirmedOperationStatus.java (Enum)
#           └── wal/
#               └── OrderPlacementConfirmedWriteAheadLog.java

# 2. 개발자 작업 (15-20% 비즈니스 로직):
# - executeInternal() 구현: 외부 API 호출 로직
# - Mapper 구현: Command → Domain Entity 변환
# - Outcome 구현: 성공/재시도/실패 조건

# 3. 자동 검증 (3-Tier):
# Tier 1: validation-helper.py (실시간)
# Tier 2: Git pre-commit hook (커밋 시)
# Tier 3: ArchUnit (빌드 시)

# 예상 효율:
# - 생성 시간: 8분 → 2분 (75% 단축)
# - 컨벤션 위반: 평균 12회 → 0-2회 (83-100% 감소)
# - 개발자 집중: Boilerplate → 비즈니스 로직
```

### 📊 비교표

| 항목 | Claude Code | Cursor IDE |
|------|-------------|------------|
| **역할** | 설계, 로직, 검증 | Boilerplate 생성 |
| **자동화** | Hooks, Cache, Serena | .cursorrules (자동 로드) |
| **강점** | 컨텍스트 유지, 복잡한 로직 | 빠른 구조 생성 |
| **검증** | Pre-commit, ArchUnit | AI 지원 검증 |
| **사용 시점** | 분석, 로직, 검증 | 반복 구조 생성 |

### 💡 핵심 원칙

1. **Claude Code First**: 항상 Claude Code로 분석 및 설계 시작
2. **Cursor IDE for Speed**: 반복 구조는 Cursor IDE로 빠르게 생성
3. **Claude Code for Logic**: 중요한 비즈니스 로직은 Claude Code에 위임
4. **Automatic Validation**: 모든 코드는 자동 검증 통과 필수

**✅ 이 워크플로우는 두 도구의 강점을 최대한 활용하도록 설계되었습니다.**

---

## 📖 참고 문서

### 튜토리얼
- [Getting Started](../docs/tutorials/01-getting-started.md) - 시작 가이드 (5분)

### TDD Workflow 시스템
- [track-tdd-cycle.sh](./.claude/hooks/track-tdd-cycle.sh) - TDD 사이클 자동 추적
- [log-to-langfuse.py](./.claude/scripts/log-to-langfuse.py) - LangFuse 메트릭 업로드
- [LangFuse 통합 가이드](../docs/LANGFUSE_USAGE_GUIDE.md) - 효율 측정 및 모니터링

### Slash Commands
- [Commands README](./commands/README.md) - 모든 명령어 설명
- [Code Gen Domain](./commands/code-gen-domain.md) - Domain 생성
- [Code Gen Orchestrator](./commands/code-gen-orchestrator.md) - Orchestration Pattern 생성 (NEW) ⭐
- [Validate Domain](./commands/validate-domain.md) - Domain 검증

### 코딩 규칙
- [Coding Convention](../docs/coding_convention/) - 98개 규칙 (Layer별, Orchestration 포함)

---

## 🎓 학습 경로

### Day 1: 시스템 이해
1. README.md 읽기 (프로젝트 개요)
2. docs/tutorials/01-getting-started.md (실습)
3. TDD 사이클 첫 실습 (Red → Green → Refactor)

### Week 1: TDD + 핵심 규칙
1. Kent Beck TDD 철학 이해
2. Domain Layer 규칙 (Law of Demeter, Lombok 금지)
3. Application Layer 규칙 (Transaction 경계)
4. Persistence Layer 규칙 (Long FK 전략)
5. Orchestration Pattern 기초 (3-Phase Lifecycle, Idempotency) ⭐ NEW

### Month 1: 고급 패턴 + 메트릭 분석
1. DDD Aggregate 설계
2. CQRS 패턴 적용
3. Event-Driven Architecture
4. Orchestration Pattern 실전 (WAL, Outcome Modeling, Crash Recovery) ⭐ NEW
5. LangFuse 메트릭 분석 및 TDD 사이클 개선

---

**✅ 이 프로젝트의 모든 코드는 위 표준을 따라야 합니다.**

**💡 핵심**: TDD 테스트가 컨벤션을 강제하고, 작은 커밋이 빠른 피드백을 보장하며, LangFuse 메트릭으로 지속적 개선!
