# /cc:load - 최신 코딩 컨벤션 로드 (2025)

Spring Standards 프로젝트의 **최신 코딩 컨벤션**을 Serena 메모리에서 자동으로 로드합니다.

**cc** = **C**oding **C**onvention

---

## 🚀 실행 흐름

1. **프로젝트 활성화**: Serena MCP에 프로젝트 등록
2. **최신 컨벤션 로드**: Application Layer 2025 최신 규칙 로드
3. **패턴 메모리 로드**: Manager/StateManager/Facade 패턴 로드
4. **세션 준비**: Spring Standards 개발 환경 활성화

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

## 📊 Hook 시스템 통합

`/cc:load` 실행 시 자동으로:
- ✅ **Dynamic Hooks** 활성화 (키워드 감지 → 규칙 자동 주입)
- ✅ **Cache 시스템** 활성화 (98개 규칙 → JSON → O(1) 검색)
- ✅ **실시간 검증** 활성화 (validation-helper.py)
- ✅ **Hook 로깅** 활성화 (A/B 테스트 데이터 수집)

**성능 (A/B 테스트 검증)**:
- 컨벤션 위반: 40회 → 0회 (100% 제거)
- 토큰 사용량: 90% 절감
- 검증 속도: 73.6% 향상

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

1. **이 명령어는 세션 시작 시 한 번만 실행**하면 됩니다
2. **Serena 메모리는 세션 간 지속**되므로 재로드 불필요
3. **토큰 사용 없이** 최신 컨벤션 로드 가능 (Serena Memory 읽기만)
4. **Dynamic Hooks**가 자동으로 규칙을 주입하므로 수동 적용 불필요
5. **실시간 검증**이 자동으로 실행되어 위반 시 즉시 경고

### ⚡ 성능 최적화

**로딩 시간**:
- Serena Memory 로드: **3-5초** (3개 메모리 순차 로드)
- `.claude/cache/rules/` 읽기: **불필요** (Hook이 자동 주입)
- 총 예상 시간: **5초 이내**

**느리게 느껴지는 경우**:
- ❌ Hook이 APPLICATION + ENTERPRISE 레이어 규칙 주입 (자동)
- ❌ Claude가 모든 규칙을 한 번에 처리 (대용량 텍스트)
- ✅ **해결**: `/cc:load` 실행 후 **간단한 명령어로 시작** (예: "안녕")

**권장 워크플로우**:
```bash
# 1. 세션 시작 시 한 번만 실행
/cc:load

# 2. 로딩 완료 대기 (3-5초)
# ...

# 3. 간단한 인사로 Hook 트리거 초기화
"안녕"

# 4. 본격적인 작업 시작
/code-gen-domain Order
```

---

**✅ 이 명령어는 2025-11-05 기준 최신 컨벤션을 로드합니다.**

**🔥 Manager/StateManager/Facade 패턴, Transactional Outbox Pattern (Pattern A/B/C), Zero-Tolerance 규칙 강화가 모두 포함되어 있습니다!**
