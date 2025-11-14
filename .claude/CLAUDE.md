# Spring Standards Project - Claude Code Configuration

이 프로젝트는 **Spring Boot 3.5.x + Java 21** 기반의 헥사고날 아키텍처 엔터프라이즈 표준 프로젝트입니다.

---

##  Kent Beck TDD + LangFuse 메트릭 추적

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
├── 00-project-setup/  (2개 규칙)
│   ├── multi-module-structure.md
│   └── version-management.md
│
├── 01-adapter-in-layer/rest-api/  (22개 규칙)
│   ├── controller/  (4개)
│   │   ├── controller-guide.md
│   │   ├── controller-test-guide.md
│   │   ├── controller-test-restdocs-guide.md
│   │   └── controller-archunit.md
│   ├── dto/
│   │   ├── command/  (3개: guide, test-guide, archunit)
│   │   ├── query/    (3개: guide, test-guide, archunit)
│   │   └── response/ (3개: guide, test-guide, archunit)
│   ├── error/  (2개)
│   │   ├── error-handling-strategy.md
│   │   └── error-mapper-implementation-guide.md
│   ├── mapper/  (3개: guide, test-guide, archunit)
│   ├── config/  (1개: endpoint-properties-guide)
│   └── rest-api-guide.md
│
├── 02-domain-layer/  (12개 규칙)
│   ├── aggregate/  (3개: guide, test-guide, archunit)
│   ├── exception/  (3개: guide, test-guide, archunit-guide)
│   ├── vo/  (3개: guide, test-guide, archunit)
│   ├── event/  (디렉토리만 존재, 파일 없음)
│   └── domain-guide.md
│
├── 03-application-layer/  (26개 규칙)
│   ├── assembler/  (3개: guide, test-guide, archunit)
│   ├── dto/
│   │   ├── command/  (1개: command-dto-guide)
│   │   ├── query/    (1개: query-dto-guide)
│   │   ├── response/ (1개: response-dto-guide)
│   │   ├── dto-record-archunit.md
│   │   └── 06_archunit-dto-record-rules.md
│   ├── facade/  (2개: guide, test-guide)
│   ├── manager/  (2개: transaction-manager-guide, test-guide)
│   ├── port/
│   │   ├── in/
│   │   │   ├── command/  (2개: guide, archunit)
│   │   │   └── query/    (2개: guide, archunit)
│   │   └── out/
│   │       ├── command/  (2개: guide, archunit)
│   │       └── query/    (2개: guide, archunit)
│   ├── listener/  (디렉토리만 존재)
│   ├── scheduler/  (디렉토리만 존재)
│   ├── service/  (디렉토리만 존재)
│   └── application-guide.md
│
├── 04-persistence-layer/  (23개 규칙)
│   ├── mysql/  (18개)
│   │   ├── adapter/
│   │   │   ├── command/  (3개: guide, test-guide, archunit)
│   │   │   └── query/    (7개)
│   │   │       ├── query-adapter-guide.md
│   │   │       ├── query-adapter-test-guide.md
│   │   │       ├── query-adapter-integration-testing.md
│   │   │       ├── query-adapter-archunit.md
│   │   │       ├── lock-query-adapter-guide.md
│   │   │       ├── lock-query-adapter-test-guide.md
│   │   │       └── lock-query-adapter-archunit.md
│   │   ├── config/  (2개: flyway-testing, hikaricp-configuration)
│   │   ├── entity/  (3개: guide, test-guide, archunit)
│   │   ├── mapper/  (3개: guide, test-guide, archunit)
│   │   ├── repository/  (5개)
│   │   │   ├── jpa-repository-guide.md
│   │   │   ├── jpa-repository-archunit.md
│   │   │   ├── querydsl-repository-guide.md
│   │   │   ├── querydsl-repository-test-guide.md
│   │   │   └── querydsl-repository-archunit.md
│   │   └── persistence-mysql-guide.md
│   └── redis/  (5개)
│       ├── adapter/  (3개: guide, test-guide, archunit)
│       ├── config/  (1개: cache-configuration)
│       └── persistence-redis-guide.md
│
└── 05-testing/  (3개 규칙)
    ├── integration-testing/  (1개: 01_integration-testing-overview)
    └── test-fixtures/  (2개: guide, archunit)
```

**총 88개 규칙** (README.md 포함)

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
