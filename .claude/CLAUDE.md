# Spring Standards Project - Claude Code Configuration

이 프로젝트는 **Spring Boot 3.5.x + Java 21** 기반의 헥사고날 아키텍처 엔터프라이즈 표준 프로젝트입니다.

---

## 🚀 혁신: Dynamic Hooks + Cache + Serena Memory 시스템

이 프로젝트의 핵심 차별점은 **2가지 통합 자동화 시스템**입니다:
1. **Dynamic Hooks + Cache**: 키워드 감지 → JSON Cache → 규칙 자동 주입
2. **Serena Memory + LangFuse**: 컨벤션 메모리 저장 → 컨텍스트 유지 → 효율 측정

### 시스템 아키텍처

```
docs/coding_convention/ (90개 마크다운 규칙)
         ↓
build-rule-cache.py (Cache 빌드)
         ↓
.claude/cache/rules/ (90개 JSON + index.json)
         ↓
setup-serena-conventions.sh (Serena 메모리 생성) ← NEW
         ↓
Serena MCP: 5개 메모리 저장 ← NEW
         - coding_convention_domain_layer
         - coding_convention_application_layer
         - coding_convention_persistence_layer
         - coding_convention_rest_api_layer
         - coding_convention_index
         ↓
/cc:load (세션 시작 시 실행) ← NEW
         ↓
user-prompt-submit.sh (키워드 감지 → Layer 매핑)
         ├─ Serena 메모리 자동 로드 (최우선) ← NEW
         └─ inject-rules.py (Cache 규칙 주입, 보조)
         ↓
Claude Code (규칙 준수 코드 생성)
         - Serena 메모리 우선 참조 ← NEW
         - Cache 규칙 보조 참조
         ↓
after-tool-use.sh (생성 직후 검증)
         ↓
validation-helper.py (Cache 기반 실시간 검증)
         ↓
LangFuse (효율 측정: 토큰, 위반 건수 등) ← NEW
```

### 성능 메트릭

| 메트릭 | 기존 방식 | Cache 시스템 | Serena + Cache | 최종 개선율 |
|--------|----------|-------------|----------------|-------------|
| 토큰 사용량 | 50,000 | 500-1,000 | 500-1,000 | **90% 절감** |
| 검증 속도 | 561ms | 148ms | 148ms | **73.6% 향상** |
| 문서 로딩 | 2-3초 | <100ms | <50ms | **97.5% 향상** |
| 컨벤션 위반 | 23회 | - | 5회 | **78% 감소** |
| 세션 시간 | 15분 | - | 8분 | **47% 단축** |

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
└── 08-error-handling/  (5개 규칙)
    ├── error-handling-strategy/
    ├── domain-exception-design/
    ├── global-exception-handler/
    ├── error-response-format/
    └── errorcode-management/
```

**총 90개 규칙 → JSON Cache로 변환 → O(1) 검색 및 주입**

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

### 1. Dynamic Hooks + Cache 

**위치**: `.claude/hooks/`, `.claude/cache/`, `.claude/commands/lib/`

#### Cache 빌드
```bash
# 90개 마크다운 → 90개 JSON + index.json (약 5초)
python3 .claude/hooks/scripts/build-rule-cache.py
```

#### 자동 규칙 주입 (user-prompt-submit.sh)
- **키워드 감지**: "domain", "usecase", "controller", "entity" 등
- **Layer 매핑**: domain, application, adapter-rest, adapter-persistence
- **inject-rules.py 호출**: Layer별 JSON 규칙 자동 주입

#### 실시간 검증 (after-tool-use.sh)
- **코드 생성 직후 검증**: Write/Edit 도구 사용 후 즉시 실행
- **validation-helper.py 호출**: Cache 기반 고속 검증
- **위반 시 경고**: 구체적인 수정 방법 제시

### 2. Serena Memory 자동 로드

**위치**: `.claude/hooks/scripts/setup-serena-conventions.sh`, `.claude/commands/cc/load.md`

**목적**: 코딩 컨벤션을 Serena MCP 메모리에 저장하여 세션 간 컨텍스트 유지

**cc** = **C**oding **C**onvention

#### 설정 방법

```bash
# 1. Serena 메모리 준비 (1회만 실행)
bash .claude/hooks/scripts/setup-serena-conventions.sh

# 출력:
# 🚀 Serena Conventions Setup
# ✅ Python 3 확인 완료
# 📋 코딩 컨벤션 메모리 생성 시작...
# 📝 생성할 메모리:
#    - coding_convention_domain_layer
#    - coding_convention_application_layer
#    - coding_convention_persistence_layer
#    - coding_convention_rest_api_layer
#    - coding_convention_index
# ✅ 메모리 생성 준비 완료

# 2. Claude Code 실행
claude code

# 3. 코딩 컨벤션 로드 (세션 시작 시 실행)
/cc:load

# 출력:
# ✅ Project activated: claude-spring-standards
# ✅ Memory loaded: coding_convention_index
# 📋 Available conventions:
#    - coding_convention_domain_layer (Domain Layer 규칙)
#    - coding_convention_application_layer (Application Layer 규칙)
#    - coding_convention_persistence_layer (Persistence Layer 규칙)
#    - coding_convention_rest_api_layer (REST API Layer 규칙)
```

#### 자동 로드되는 메모리

1. **coding_convention_index** (마스터 인덱스)
   - 전체 컨벤션 개요
   - Zero-Tolerance 규칙 요약
   - 레이어별 메모리 접근 방법

2. **coding_convention_domain_layer**
   - Lombok 금지
   - Law of Demeter (Getter 체이닝 금지)
   - Aggregate Root 패턴
   - Tell, Don't Ask 패턴

3. **coding_convention_application_layer**
   - Transaction 경계 관리
   - Spring 프록시 제약사항
   - UseCase Single Responsibility
   - Command/Query 분리

4. **coding_convention_persistence_layer**
   - Long FK Strategy
   - Entity Immutability
   - CQRS Separation
   - N+1 문제 방지

5. **coding_convention_rest_api_layer**
   - Controller Thin
   - GlobalExceptionHandler
   - ApiResponse 표준화

#### 작동 원리

```
사용자: "domain aggregate 작업"
    ↓
user-prompt-submit.sh
    ├─ 키워드 분석: "aggregate" (30점)
    ├─ Layer 매핑: domain
    ├─ Serena 메모리 자동 로드 (최우선):
    │   read_memory("coding_convention_domain_layer")
    └─ inject-rules.py: Cache 기반 규칙 주입 (보조)
         ↓
Claude Code
    ├─ Serena 메모리 우선 참조 (컨텍스트 유지)
    ├─ Cache 규칙 보조 참조 (고속 검색)
    └─ 규칙 준수 코드 생성
         ↓
LangFuse (효율 측정)
    ├─ 토큰 사용량 추적
    ├─ 위반 건수 추적
    └─ A/B 테스트 데이터 수집
```

#### Cache vs Serena Memory

| 특성 | Cache 시스템 | Serena Memory |
|------|-------------|---------------|
| **목적** | 고속 규칙 조회 | 세션 컨텍스트 유지 |
| **형식** | JSON 파일 (90개) | Serena MCP 메모리 (5개) |
| **검색** | O(1) 인덱스 | MCP read_memory() |
| **우선순위** | 보조 | 최우선 |
| **효과** | 90% 토큰 절감 | 78% 위반 감소 |

**시너지**: Cache의 고속 검색 + Serena의 컨텍스트 유지 = 최적의 AI 가이드

### 3. Slash Commands

**세션 시작**:
- `/cc:load` - 코딩 컨벤션 자동 로드 (세션 시작 시 실행)

**코드 생성**:
- `/code-gen-domain <name>` - Domain Aggregate 생성 (규칙 자동 주입 + 검증)
- `/code-gen-usecase <name>` - Application UseCase 생성
- `/code-gen-controller <name>` - REST Controller 생성

**검증**:
- `/validate-domain <file>` - Domain layer 파일 검증
- `/validate-architecture [dir]` - 전체 또는 특정 모듈 아키텍처 검증

**AI 리뷰**:
- `/ai-review [pr-number]` - 통합 AI 리뷰 (Gemini + CodeRabbit + Codex, 병렬 실행)
- `/gemini-review [pr-number]` - Gemini 전용 (Deprecated, `/ai-review --bots gemini` 사용 권장)

**기타**:
- `/jira-task` - Jira 태스크 분석 및 브랜치 생성

### 3. Git Pre-commit Hooks (별도 시스템)

**위치**: `hooks/pre-commit`, `hooks/validators/`

- **트랜잭션 경계 검증**: `@Transactional` 내 외부 API 호출 차단
- **프록시 제약사항 검증**: Private/Final 메서드 `@Transactional` 차단
- **최종 안전망 역할**: 커밋 시 강제 검증

### 4. ArchUnit Tests

**위치**: `application/src/test/java/com/company/template/architecture/`

- **아키텍처 규칙 자동 검증**: 레이어 의존성, 네이밍 규칙
- **빌드 시 자동 실행**: 위반 시 빌드 실패

---

## 🎯 개발 워크플로우 (Cache 시스템 활용)

### 1. 코드 생성 워크플로우

```bash
# 1. Slash Command로 코드 생성 (자동 규칙 주입)
/code-gen-domain Order

# 2. 자동 실행 흐름:
#    - inject-rules.py: Domain layer 규칙 주입
#    - Claude: 규칙 준수 코드 생성
#    - after-tool-use.sh: 즉시 검증
#    - validation-helper.py: Cache 기반 검증

# 3. 검증 결과 확인
# ✅ Validation Passed: 모든 규칙 준수
# ❌ Validation Failed: 위반 규칙 상세 표시
```

### 2. 수동 검증 워크플로우

```bash
# 특정 파일 검증
/validate-domain domain/src/main/java/.../Order.java

# 전체 프로젝트 검증
/validate-architecture

# 특정 모듈만 검증
/validate-architecture domain
```

### 3. Cache 업데이트 워크플로우

```bash
# 1. 규칙 문서 수정
vim docs/coding_convention/02-domain-layer/law-of-demeter/01_getter-chaining-prohibition.md

# 2. Cache 재빌드
python3 .claude/hooks/scripts/build-rule-cache.py

# 3. 확인
cat .claude/cache/rules/domain-layer-law-of-demeter-01_getter-chaining-prohibition.json
```

---

## 🚨 Zero-Tolerance 규칙

다음 규칙은 **예외 없이** 반드시 준수해야 합니다:

### 1. Lombok 금지
- ❌ `@Data`, `@Builder`, `@Getter`, `@Setter` 등 모두 금지
- ✅ Pure Java getter/setter 직접 작성
- **검증**: validation-helper.py가 자동 감지

### 2. Law of Demeter (Getter 체이닝 금지)
- ❌ `order.getCustomer().getAddress().getZip()`
- ✅ `order.getCustomerZipCode()` (Tell, Don't Ask)
- **검증**: Anti-pattern 정규식 매칭

### 3. Long FK 전략 (JPA 관계 금지)
- ❌ `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`
- ✅ `private Long userId;` (Long FK 사용)
- **검증**: JPA 관계 어노테이션 감지

### 4. Transaction 경계
- ❌ `@Transactional` 내 외부 API 호출 (RestTemplate, WebClient 등)
- ✅ 트랜잭션은 짧게 유지, 외부 호출은 트랜잭션 밖에서
- **검증**: Git pre-commit hook

### 5. Javadoc 필수
- ❌ `@author`, `@since` 없는 public 클래스/메서드
- ✅ 모든 public 클래스/메서드에 Javadoc 포함
- **검증**: Checkstyle

### 6. Scope 준수
- ❌ 요청하지 않은 추가 기능 구현
- ✅ 요청된 코드만 정확히 작성
- **검증**: 수동 코드 리뷰

---

## 🔧 통합 워크플로우: Claude Code + IntelliJ Cascade

이 프로젝트는 **Claude Code**와 **IntelliJ의 Cascade(Windsurf)**를 통합하여 사용하도록 설계되었습니다.

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
│ 2️⃣ Cascade (IntelliJ 내장): 빠른 Boilerplate 생성            │
├─────────────────────────────────────────────────────────────┤
│ - .windsurf/rules/*.md 자동 로드 (Zero-Tolerance 규칙)       │
│ - .windsurf/workflows/*.yaml 참고 (체계적인 가이드)          │
│ - .windsurf/templates/*.java 패턴 학습                       │
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
- **Dynamic Hooks**: 키워드 감지 → 규칙 자동 주입
- **Cache 시스템**: 90개 규칙 → JSON → O(1) 검색 (90% 토큰 절감)
- **Serena Memory**: 코딩 컨벤션 세션 유지 (78% 위반 감소)
- **Git Pre-commit Hooks**: 트랜잭션 경계 자동 검증
- **ArchUnit**: 빌드 시 아키텍처 자동 검증

**Slash Commands**:
```bash
/cc:load                    # 코딩 컨벤션 로드 (세션 시작 시)
/code-gen-domain <name>     # Domain Aggregate 생성
/code-gen-usecase <name>    # Application UseCase 생성
/code-gen-controller <name> # REST Controller 생성
/validate-domain <file>     # Domain layer 검증
/validate-architecture      # 전체 아키텍처 검증
/ai-review [pr-number]      # 통합 AI 리뷰 (Gemini + CodeRabbit + Codex)
/jira-task                  # Jira Task 분석 및 브랜치 생성
```

**성능**:
- 토큰 사용량: 90% 절감
- 검증 속도: 73.6% 향상
- 컨벤션 위반: 78% 감소
- 세션 시간: 47% 단축

#### Cascade (`.windsurf/`) 🚀

**역할**: Boilerplate 빠른 생성

**가이드 시스템**:
- **rules/**: Cascade가 자동으로 읽는 규칙 (Markdown, 6000자 제한)
- **workflows/**: 체계적인 코드 생성 가이드 (YAML, 참고용)
- **templates/**: Java 코드 예제 (패턴 학습용)

**사용 방법** (IntelliJ Cascade에서):
```
사용자: "Order Aggregate를 생성해줘"

Cascade:
1. .windsurf/rules/01-domain-layer/*.md 자동 로드
   → Lombok 금지, Law of Demeter 등 자동 적용

2. "@workflows/01-domain/create-aggregate.yaml 참고"
   → 구조화된 단계별 가이드 읽기

3. templates/domain/*.java 패턴 학습
   → 프로젝트 표준 스타일 반영

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

# 3. IntelliJ Cascade: Boilerplate 생성
"Order Aggregate를 생성해줘"
→ OrderDomain.java, OrderId.java, OrderStatus.java 등 생성

# 4. Claude Code: 비즈니스 로직 구현
/cc:load  # 코딩 컨벤션 로드
"Order Domain에 비즈니스 메서드를 구현해줘:
- placeOrder(): 주문 생성
- cancelOrder(): 주문 취소 (PLACED 상태만 가능)
- confirmOrder(): 주문 확인"
→ 비즈니스 로직 구현 (Law of Demeter, Tell Don't Ask 준수)

# 5. Claude Code: 검증 및 PR
/validate-architecture
→ ArchUnit 테스트 통과
→ gh pr create 자동 실행
```

#### 예시 2: UseCase 개발

```bash
# 1. IntelliJ Cascade: UseCase Boilerplate
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

### 🔧 통합 스크립트

프로젝트는 이 워크플로우를 자동화하는 스크립트를 제공합니다:

```bash
# Epic 단위 통합 개발 (Cascade + Claude Code)
./scripts/integrated-squad-start.sh PROJ-100 Order --sequential

# 워크플로우:
# 1. Epic 분석 (Jira)
# 2. Layer별 실행:
#    - cascade-generate-boilerplate.sh (Cascade로 Boilerplate)
#    - claude-implement-business-logic.sh (Claude로 로직)
#    - integrated-validation.sh (검증)
# 3. Git Commit & PR 생성
```

### 📊 비교표

| 항목 | Claude Code | IntelliJ Cascade |
|------|-------------|------------------|
| **역할** | 설계, 로직, 검증 | Boilerplate 생성 |
| **자동화** | Hooks, Cache, Serena | Rules (자동 로드) |
| **강점** | 컨텍스트 유지, 복잡한 로직 | 빠른 구조 생성 |
| **검증** | Pre-commit, ArchUnit | 수동 |
| **사용 시점** | 분석, 로직, 검증 | 반복 구조 생성 |

### 💡 핵심 원칙

1. **Claude Code First**: 항상 Claude Code로 분석 및 설계 시작
2. **Cascade for Speed**: 반복 구조는 Cascade로 빠르게 생성
3. **Claude Code for Logic**: 중요한 비즈니스 로직은 Claude Code에 위임
4. **Automatic Validation**: 모든 코드는 자동 검증 통과 필수

**✅ 이 워크플로우는 두 도구의 강점을 최대한 활용하도록 설계되었습니다.**

---

## 📖 참고 문서

### 튜토리얼
- [Getting Started](../docs/tutorials/01-getting-started.md) - 시작 가이드 (5분)

### Dynamic Hooks 시스템
- [DYNAMIC_HOOKS_GUIDE.md](../docs/DYNAMIC_HOOKS_GUIDE.md) - 전체 시스템 가이드
- [Cache README](./.claude/cache/rules/README.md) - Cache 시스템 상세
- [Validation Helper](./hooks/scripts/validation-helper.py) - 검증 엔진

### Serena + LangFuse
- [Serena 설정 가이드](./hooks/scripts/setup-serena-conventions.sh) - 메모리 생성
- [/cc:load 명령어](./commands/cc/load.md) - 코딩 컨벤션 자동 로드
- [LangFuse 통합 가이드](../docs/LANGFUSE_INTEGRATION_GUIDE.md) - 효율 측정 및 A/B 테스트

### Slash Commands
- [Commands README](./commands/README.md) - 모든 명령어 설명
- [Code Gen Domain](./commands/code-gen-domain.md) - Domain 생성
- [Validate Domain](./commands/validate-domain.md) - Domain 검증

### 코딩 규칙
- [Coding Convention](../docs/coding_convention/) - 90개 규칙 (Layer별)

### Windsurf IDE
- [Windsurf 가이드](../.windsurf/README.md) - Windsurf 사용 가이드
- [Windsurf Rules](../.windsurf/rules/) - Layer별 규칙 (Windsurf 자동 로드)
- [Windsurf Workflows](../.windsurf/workflows/) - 코드 생성 워크플로우 (참고용)
- [Windsurf Templates](../.windsurf/templates/) - Java 코드 예제 (학습용)

---

## 🎓 학습 경로

### Day 1: 시스템 이해
1. README.md 읽기 (프로젝트 개요)
2. docs/tutorials/01-getting-started.md (실습)
3. Cache 빌드 및 첫 코드 생성 테스트

### Week 1: 핵심 규칙 숙지
1. Domain Layer 규칙 (Law of Demeter, Lombok 금지)
2. Application Layer 규칙 (Transaction 경계)
3. Persistence Layer 규칙 (Long FK 전략)

### Month 1: 고급 패턴
1. DDD Aggregate 설계
2. CQRS 패턴 적용
3. Event-Driven Architecture

---

**✅ 이 프로젝트의 모든 코드는 위 표준을 따라야 합니다.**

**💡 핵심**: Dynamic Hooks + Cache 시스템이 자동으로 규칙을 주입하고 검증하므로, 개발자는 비즈니스 로직에 집중할 수 있습니다!
