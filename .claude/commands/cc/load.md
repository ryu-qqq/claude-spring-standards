# /cc:load - 최신 코딩 컨벤션 로드 (2025)

Spring Standards 프로젝트의 **핵심 컨벤션 요약본**을 Serena 메모리에서 빠르게 로드합니다.

**cc** = **C**oding **C**onvention

---

## 🎯 시스템 아키텍처 (중요!)

이 프로젝트는 **3-Tier 컨벤션 로딩 시스템**을 사용합니다:

### Tier 1: Serena Memory (핵심 요약본) ⭐ `/cc:load`
- **3개 Memory 파일** (935줄, 약 30KB)
- **목적**: 세션 초기화, 핵심 패턴 빠른 참조
- **로딩 시간**: 3-5초
- **내용**: Application Layer, Manager Pattern, Outbox Pattern

### Tier 2: Dynamic Hooks (실시간 자동 주입) 🔥
- **146개 Cache Rules** (98개 규칙 → JSON)
- **목적**: 키워드 감지 → 필요한 규칙만 실시간 주입
- **로딩 시간**: 즉시 (O(1) Cache 검색)
- **내용**: 전체 레이어별 상세 규칙 (Domain, Application, Persistence, REST API 등)

### Tier 3: 원본 문서 (참조용)
- **152개 Markdown 파일** (docs/coding_convention/)
- **목적**: 상세 규칙 참조, 수동 검색
- **로딩 시간**: 불필요 (Hook이 자동 주입)
- **내용**: 전체 규칙 상세 설명

**핵심**: `/cc:load`는 **요약본만** 로드! **전체 규칙**은 **Hook이 자동 주입**합니다!

---

## 🚀 실행 흐름

1. **프로젝트 활성화**: Serena MCP에 프로젝트 등록
2. **핵심 요약본 로드**: 3개 Memory 파일 (Application, Manager, Outbox)
3. **세션 준비**: Spring Standards 개발 환경 활성화
4. **Hook 활성화**: 이후 키워드 감지 시 자동으로 상세 규칙 주입

---

## 📝 사용법

```bash
/cc:load
```

---

## 📚 자동 로드되는 메모리

### 1. Application Layer 최신 컨벤션 (2025-11-04)
**메모리**: `application-layer-conventions-2025`

**포함 내용**:
- ✅ **Manager/StateManager/Facade 패턴** (NEW)
- ✅ **Outbox Pattern 통합** (NEW)
- ✅ **Zero-Tolerance 규칙 강화** (Lombok 금지, Assembler 필수)
- ✅ **Transaction 경계 규칙** (외부 API 호출 분리)
- ✅ **패키지 구조** (manager/ 디렉토리 포함)
- ✅ **UseCase Method 네이밍** (execute/query 패턴)
- ✅ **검증 체크리스트** (아키텍처, Transaction, Spring Proxy, 네이밍)

### 2. Manager 패턴 상세 가이드
**메모리**: `manager-statemanager-facade-pattern`

**포함 내용**:
- ✅ **StateManager**: 단일 Bounded Context 상태 관리
- ✅ **Manager**: 2-3개 StateManager 조율
- ✅ **Facade**: 여러 Manager 통합
- ✅ **OutboxStateManager**: Outbox 엔트리 상태 관리
- ✅ **실제 예시 코드** (Order, Payment, Outbox)

### 3. Transactional Outbox Pattern (2025-11-05) ⭐ NEW
**메모리**: `transactional-outbox-pattern-2025`

**포함 내용**:
- ✅ **Pattern A (Direct Event)**: 지양 - 결제/금융엔 부적합
- ✅ **Pattern B (Outbox + Event Wake-up)**: 권장 기본 패턴
- ✅ **Pattern C (MQ Integration)**: MQ 고도화
- ✅ **FOR UPDATE SKIP LOCKED**: 동시성 제어
- ✅ **Decision Tree**: 패턴 선택 가이드
- ✅ **senario.txt 패턴**: 동기 API 요청 + Outbox Relay
- ✅ **OutboxStatus State Machine**: PENDING → PUBLISHED → COMPLETED/FAILED

---

## 🔧 실행 내용

아래 작업들이 자동으로 수행됩니다:

```python
# 1. Serena 프로젝트 활성화
mcp__serena__activate_project("/Users/sangwon-ryu/claude-spring-standards")

# 2. 사용 가능한 메모리 목록 확인
memories = mcp__serena__list_memories()
# Result: ['application-layer-conventions-2025', 'manager-statemanager-facade-pattern', 'transactional-outbox-pattern-2025']

# 3. 최신 Application Layer 컨벤션 로드
app_conventions = mcp__serena__read_memory("application-layer-conventions-2025")

# 4. Manager 패턴 상세 가이드 로드
manager_patterns = mcp__serena__read_memory("manager-statemanager-facade-pattern")

# 5. Transactional Outbox Pattern 로드 ⭐ NEW
outbox_pattern = mcp__serena__read_memory("transactional-outbox-pattern-2025")

# 6. 세션 컨텍스트 확인 및 복원
onboarding_status = mcp__serena__check_onboarding_performed()
```

---

## ✅ 출력 예시

```
🚀 프로젝트 활성화: claude-spring-standards
✅ 사용 가능한 메모리: 3개

📚 최신 코딩 컨벤션 로드 완료:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1️⃣ Application Layer 컨벤션 (2025-11-04)
   ✅ Manager/StateManager/Facade 패턴
   ✅ Outbox Pattern 통합
   ✅ Zero-Tolerance 규칙 강화
   ✅ Transaction 경계 엄격 관리

2️⃣ Manager 패턴 가이드
   ✅ StateManager: 단일 Bounded Context 상태 관리
   ✅ Manager: 2-3개 StateManager 조율
   ✅ Facade: 여러 Manager 통합
   ✅ OutboxStateManager: Outbox 엔트리 상태 관리

3️⃣ Transactional Outbox Pattern (2025-11-05) ⭐ NEW
   ✅ Pattern A (Direct Event): 지양
   ✅ Pattern B (Outbox + Event Wake-up): 권장 기본
   ✅ Pattern C (MQ Integration): MQ 고도화
   ✅ FOR UPDATE SKIP LOCKED, Decision Tree

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🎯 Zero-Tolerance 규칙 (Application Layer):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

❌ 금지 (절대 위반 불가):
1. Lombok 사용 (@RequiredArgsConstructor, @Data 등 모두 금지)
2. @Transactional 내 외부 API 호출
3. Private/Final 메서드에 @Transactional
4. 같은 클래스 내부 호출 (this.method())
5. OutboxScheduler.pollOutbox()에 @Transactional

✅ 필수 (반드시 준수):
1. Plain Java Constructor 직접 작성
2. Assembler를 통한 DTO ↔ Domain 변환
3. StateManager로 상태 관리
4. 각 상태 변경은 별도 @Transactional
5. 외부 API 호출은 Outbox + Scheduler 사용

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📖 상세 메모리 확인:
- mcp__serena__read_memory("application-layer-conventions-2025")
- mcp__serena__read_memory("manager-statemanager-facade-pattern")
- mcp__serena__read_memory("transactional-outbox-pattern-2025")
```

---

## 🎯 다음 단계

세션 로드 후 다음 작업을 수행할 수 있습니다:

### 1️⃣ 코드 생성 (최신 패턴 적용)

```bash
# Domain Aggregate 생성
/code-gen-domain Order

# Application UseCase 생성 (StateManager 패턴 자동 적용)
/code-gen-usecase PlaceOrder

# REST Controller 생성
/code-gen-controller Order

# Orchestration Pattern 생성
/code-gen-orchestrator Order PlacementConfirmed
```

**자동 적용되는 규칙**:
- ✅ Plain Java Constructor (Lombok 없음)
- ✅ Assembler 패턴
- ✅ StateManager/Manager/Facade 계층 구조
- ✅ Transaction 경계 분리

### 2️⃣ 코드 검증

```bash
# Domain layer 검증
/validate-domain domain/src/.../Order.java

# 전체 아키텍처 검증 (ArchUnit)
/validate-architecture

# 특정 모듈 검증
/validate-architecture application
```

### 3️⃣ AI 리뷰

```bash
# 통합 AI 리뷰 (Gemini + CodeRabbit + Codex)
/ai-review [pr-number]

# Jira Task 분석
/jira-task
```

### 4️⃣ 분석

```bash
# 모듈 분석
/sc:analyze adapter-out/persistence-mysql

# 아키텍처 설계
/sc:design business-model
```

---

## 📦 패키지 구조 (최신)

로드된 컨벤션을 따르면 다음과 같은 구조가 생성됩니다:

```
application/
├── order/
│   ├── port/
│   │   ├── in/                     # UseCase 인터페이스
│   │   └── out/                    # Port 인터페이스
│   ├── service/                    # UseCase 구현체 (접미사: Service)
│   ├── manager/                    # ⭐ 상태 관리 및 조율 계층 (NEW)
│   │   ├── OrderStateManager.java      # 단일 Context 상태 관리
│   │   ├── PaymentStateManager.java
│   │   ├── OutboxStateManager.java     # Outbox 상태 관리
│   │   └── OrderPaymentManager.java    # 2-3개 StateManager 조율
│   ├── facade/                     # 복잡한 워크플로우
│   │   └── CheckoutFacade.java
│   ├── assembler/                  # DTO ↔ Domain 변환
│   └── dto/
│       ├── command/                # Command DTO (Write)
│       ├── query/                  # Query DTO (Read)
│       └── result/                 # Response DTO
```

---

## 🔄 계층 구조 (최신)

```
UseCase Service (접미사: Service)
    ↓ 의존
Facade (복잡한 워크플로우)
    ↓ 의존
Manager (2-3개 StateManager 조율)
    ↓ 의존
StateManager (단일 Bounded Context 상태 관리)
    ↓ 의존
Port (Out)
```

---

## 📊 Hook 시스템 통합 (전체 규칙 자동 로딩)

`/cc:load` 실행 후, **사용자가 코드를 작성할 때** 자동으로 작동:

### 실시간 규칙 주입 (Dynamic Hooks)
```
사용자: "domain aggregate 작업"
    ↓
Hook: "domain" 키워드 감지 (30점)
    ↓
Cache: Domain Layer 규칙 15개 자동 주입 (O(1) 검색)
    ↓
Claude: Domain Layer 규칙 100% 준수 코드 생성
```

**자동으로 주입되는 규칙**:
- ✅ **Domain Layer**: Law of Demeter, Lombok 금지, Aggregate 설계 등 (15개 규칙)
- ✅ **Application Layer**: Transaction 경계, Manager 패턴, Assembler 등 (18개 규칙)
- ✅ **Persistence Layer**: Long FK 전략, QueryDSL, N+1 방지 등 (10개 규칙)
- ✅ **REST API Layer**: Controller 설계, DTO 패턴, Exception 처리 등 (18개 규칙)
- ✅ **Orchestration**: Outbox Pattern, Idempotency, WAL 등 (8개 규칙)

**총 146개 Cache Rules → 필요한 규칙만 실시간 주입!**

**성능 (A/B 테스트 검증)**:
- 컨벤션 위반: 40회 → 0회 (100% 제거) ✅
- 토큰 사용량: 90% 절감 (전체 로드 vs Hook 주입)
- 검증 속도: 73.6% 향상 (Cache O(1) 검색)

---

## 🧪 LangFuse 통합 (선택 사항)

LangFuse로 세션 메트릭을 추적하려면 다음 환경 변수를 설정하세요:

```bash
export LANGFUSE_PUBLIC_KEY="pk-lf-..."
export LANGFUSE_SECRET_KEY="sk-lf-..."
export LANGFUSE_HOST="https://us.cloud.langfuse.com"
```

**추적되는 메트릭**:
- 메모리 로드 시간
- 컨벤션 참조 횟수
- 토큰 사용량
- 컨벤션 위반 건수
- Manager/StateManager 패턴 적용 횟수

---

## 📚 참고 문서

### 최신 Skills 문서
- `.claude/skills/application-expert/SKILL.md` - Application Layer 전문가 가이드
- `.claude/skills/domain-expert/SKILL.md` - Domain Layer 전문가 가이드

### 코딩 컨벤션 문서
- `docs/coding_convention/03-application-layer/` - Application Layer 상세 규칙 (18개)
- `docs/coding_convention/09-orchestration-patterns/` - Orchestration Pattern (8개)

### Hook 시스템
- `.claude/hooks/user-prompt-submit.sh` - 키워드 감지 및 규칙 자동 주입
- `.claude/hooks/after-tool-use.sh` - 실시간 검증
- `.claude/hooks/scripts/build-rule-cache.py` - Cache 빌드 도구

---

## 💡 중요 참고사항

### 📌 핵심 개념

**Q: `/cc:load`는 3개만 로드하는데 전체 규칙은?**
A: **Hook이 자동으로 146개 규칙 주입!** 수동 로드 불필요 ✅

**Q: 152개 마크다운 파일은 언제 읽어?**
A: **읽지 않음!** Cache 시스템이 이미 JSON으로 변환 완료 ✅

**Q: 느린 이유는?**
A: **Hook이 실시간 주입 중**. 정상 동작! (3-5초) ✅

### 🎯 사용 가이드

1. **이 명령어는 세션 시작 시 한 번만 실행**하면 됩니다
2. **Serena 메모리는 세션 간 지속**되므로 재로드 불필요
3. **전체 146개 규칙은 Hook이 자동 주입** (사용자 개입 불필요)
4. **실시간 검증**이 자동으로 실행되어 위반 시 즉시 경고

### ⚡ 성능 최적화

**로딩 시간**:
- **Serena Memory 로드**: 3-5초 (3개 요약본 순차 로드)
- **Cache Rules 주입**: 즉시 (Hook이 O(1) 검색으로 자동 주입)
- **총 예상 시간**: 5초 이내

**3-Tier 시스템의 장점**:
- ✅ **Tier 1 (요약본)**: 3-5초 로드 vs 전체 로드 시 30초+
- ✅ **Tier 2 (Hook 주입)**: 필요한 규칙만 주입 → 토큰 90% 절감
- ✅ **Tier 3 (원본 문서)**: 참조용으로만 사용 → 로드 불필요

**권장 워크플로우**:
```bash
# 1. 세션 시작 시 한 번만 실행
/cc:load

# 2. 로딩 완료 대기 (3-5초)
# ...

# 3. 작업 시작 (Hook이 자동으로 규칙 주입)
/code-gen-domain Order
# → Hook이 자동으로 Domain Layer 15개 규칙 주입!
```

---

**✅ 이 명령어는 2025-11-05 기준 최신 컨벤션을 로드합니다.**

**🔥 Manager/StateManager/Facade 패턴, Transactional Outbox Pattern (Pattern A/B/C), Zero-Tolerance 규칙 강화가 모두 포함되어 있습니다!**
