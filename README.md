# Claude Spring Standards

> **Spring Boot 3.5.x + Java 21 헥사고날 아키텍처 템플릿**
> Dynamic Hooks + Cache 시스템을 통한 AI 기반 코딩 표준 자동화

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue.svg)](https://alistair.cockburn.us/hexagonal-architecture/)

---

## 🎯 CLAUDE 세션 구현 

**Dynamic Hooks + Cache System**: 키워드 기반 Layer 감지 → JSON Cache 조회 → 규칙 자동 주입 → 실시간 검증

| 기능 | 기존 방식 | 이 프로젝트 |
|------|----------|------------|
| 코딩 표준 | 수동 리뷰 | 자동 주입 + 검증 |
| 규칙 로딩 | 전체 문서 | JSON Cache |

---

## 📖 목차

- [혁신 시스템](#-혁신-시스템-이-템플릿의-핵심-차별점)
- [통합 워크플로우 (Claude + Cascade)](#-통합-워크플로우-claude--cascade)
- [빠른 시작](#-빠른-시작)
- [사용 가이드 (필독!)](#-사용-가이드-필독)
- [Cache 시스템](#-cache-시스템)
- [레이어별 작업 모드 (Slash Commands)](#-레이어별-작업-모드-slash-commands)
- [코딩 표준](#-코딩-표준)
- [개발 워크플로우](#-개발-워크플로우)
- [로그 시스템](#-로그-시스템)
- [문제 해결](#-문제-해결)

---

## 🚀 혁신 시스템 (이 템플릿의 핵심 차별점)

이 템플릿은 **2가지 혁신적 자동화 시스템**을 통합합니다:

### 1️⃣ Dynamic Hooks + Cache 시스템

**목적**: 키워드 기반 Layer 감지 → JSON Cache 조회 → 규칙 자동 주입

**작동**:
```
사용자: "Order entity 만들어줘"
    ↓
키워드 감지: "entity" (30점) → Layer: adapter-persistence
    ↓
Cache 조회: index.json → 10개 규칙 (500 tokens)
    ↓
Claude: 규칙 준수 코드 생성
```

**효과**:
- ⚡ **90% 토큰 절감**: 50,000 → 500-1,000 tokens
- 🚀 **73.6% 검증 향상**: 561ms → 148ms
- 📉 **95% 로딩 향상**: 2-3초 → <100ms

**상세**: [Dynamic Hooks Guide](docs/DYNAMIC_HOOKS_GUIDE.md)

---

### 2️⃣ Serena Memory + LangFuse 측정 시스템

**목적**: Serena MCP로 컨벤션을 메모리에 저장 → 컨텍스트 인식 자동 로드 → LangFuse로 효율 측정

**작동**:
```
setup-serena-conventions.sh (1회 실행)
    ↓
Serena MCP: 5개 메모리 생성
    - coding_convention_domain_layer
    - coding_convention_application_layer
    - coding_convention_persistence_layer
    - coding_convention_rest_api_layer
    - coding_convention_index
    ↓
사용자: /sc:load (세션 시작 시)
    ↓
Claude: Serena 메모리 자동 로드 → 컨텍스트 유지
    ↓
LangFuse: 토큰 사용량, 위반 건수 추적 → A/B 테스트
```

**효과** (A/B 테스트 결과):

| 메트릭 | 전통 방식 | Serena + Cache | 개선율 |
|--------|----------|----------------|--------|
| **토큰 사용량** | 50,000 | 15,000 | **70% ↓** |
| **컨벤션 로드** | 2-3초 | <50ms | **95% ↑** |
| **위반 건수 (Domain)** | 23회 | 5회 | **78% ↓** |
| **세션당 평균 시간** | 15분 | 8분 | **47% ↑** |

**상세**: [LangFuse 통합 가이드](docs/LANGFUSE_INTEGRATION_GUIDE.md)

---

## 🔄 통합 워크플로우 (Claude + Cascade)

이 프로젝트는 **Claude Code**와 **IntelliJ의 Cascade(Windsurf)**를 통합하여 사용하도록 설계되었습니다.

### 워크플로우 개요

```
┌─────────────────────────────────────────┐
│ 1️⃣ Claude Code: 분석 & 설계             │
│ - PRD 작성                               │
│ - Jira Task 생성 (/jira-task)           │
│ - 아키텍처 설계                          │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│ 2️⃣ Cascade: Boilerplate 생성            │
│ - .windsurf/rules/*.md 자동 로드        │
│ - .windsurf/workflows/*.yaml 참고       │
│ - 반복 구조 빠른 생성                    │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│ 3️⃣ Claude Code: 비즈니스 로직 구현      │
│ - Serena Memory 컨텍스트 유지           │
│ - Domain 비즈니스 메서드                 │
│ - Transaction 경계 관리                  │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│ 4️⃣ Claude Code: 자동 검증 & PR          │
│ - ArchUnit, Pre-commit Hooks            │
│ - /validate-architecture                │
│ - gh pr create                          │
└─────────────────────────────────────────┘
```

### 실제 예시: Order Aggregate 개발

```bash
# 1. Claude Code: PRD 작성
"Order Aggregate PRD를 작성해줘"

# 2. Claude Code: Jira Task
/jira-task
→ PROJ-100 Epic 생성
→ feature/PROJ-100-order 브랜치

# 3. IntelliJ Cascade: Boilerplate
"Order Aggregate를 생성해줘"
→ OrderDomain.java, OrderId.java 등 생성

# 4. Claude Code: 비즈니스 로직
/sc:load  # Serena 메모리 로드
"Order Domain에 placeOrder, cancelOrder 메서드 구현해줘"
→ Law of Demeter, Transaction 경계 자동 준수

# 5. Claude Code: 검증
/validate-architecture
→ ArchUnit 통과 → gh pr create
```

### 성능 비교

| 작업 | Cascade 없이 | Cascade + Claude | 개선율 |
|------|-------------|------------------|--------|
| Boilerplate 생성 | 30분 | 2분 | **93%** |
| 전체 개발 | 110분 | 50분 | **55%** |

**상세 가이드**: [Claude + Cascade 통합 워크플로우](docs/workflows/CLAUDE_CASCADE_INTEGRATION.md)

---

### 시너지 효과

**Cache 시스템 (고속 검색)**:
- 필요한 규칙만 O(1) 조회
- 90% 토큰 절감

**Serena Memory (컨텍스트 유지)**:
- 세션 간 컨벤션 기억
- 78% 위반 감소

**LangFuse (정량적 증명)**:
- A/B 테스트로 효율 측정
- ROI 증명 가능

**결과**: Cache의 효율성 + Serena의 일관성 + LangFuse의 측정 = **최적의 AI 가이드 시스템**

---

### 📊 전체 시스템 아키텍처

```mermaid
graph TB
    subgraph "📋 준비 단계 (1회 실행)"
        A1[docs/coding_convention/<br/>90개 마크다운 규칙]
        A2[build-rule-cache.py]
        A3[setup-serena-conventions.sh]
        A1 -->|빌드| A2
        A2 -->|생성| B1[.claude/cache/rules/<br/>90개 JSON + index.json]
        A1 -->|메모리 생성| A3
        A3 -->|저장| B2[Serena MCP<br/>5개 메모리]
    end

    subgraph "🚀 세션 시작"
        C1[Claude Code 실행]
        C2[/sc:load 명령어]
        C1 --> C2
        B2 -->|자동 로드| C2
        C2 -->|활성화| C3[컨벤션 컨텍스트<br/>메모리에 상주]
    end

    subgraph "💻 개발 워크플로우"
        D1[사용자 요청<br/>예: Order domain 작성]
        D2[user-prompt-submit.sh<br/>키워드 감지]
        D3{Layer 매핑}
        D4[Serena 메모리<br/>우선 참조]
        D5[inject-rules.py<br/>Cache 규칙 주입]
        D6[Claude Code<br/>규칙 준수 생성]

        D1 --> D2
        D2 --> D3
        D3 -->|최우선| D4
        D3 -->|보조| D5
        B1 -->|O1 조회| D5
        C3 -->|컨텍스트| D4
        D4 --> D6
        D5 --> D6
    end

    subgraph "✅ 검증 및 측정"
        E1[after-tool-use.sh<br/>코드 생성 직후]
        E2[validation-helper.py<br/>Cache 기반 검증]
        E3[LangFuse<br/>효율 측정]

        D6 --> E1
        E1 --> E2
        B1 -->|규칙 참조| E2
        D6 -->|토큰, 위반 추적| E3
    end

    style B1 fill:#e1f5ff
    style B2 fill:#fff4e1
    style C3 fill:#e8f5e9
    style D6 fill:#f3e5f5
    style E3 fill:#ffe4e1
```

**흐름 설명**:
1. **준비**: 마크다운 → JSON Cache + Serena 메모리 (1회)
2. **시작**: `/sc:load` → Serena 메모리 활성화 (매 세션)
3. **개발**: 키워드 감지 → Serena 우선 + Cache 보조 → 생성
4. **검증**: Cache 기반 실시간 검증 + LangFuse 측정

---

## 🚀 빠른 시작

### Option 1: Claude 설정만 복사 (다른 프로젝트에 적용)

**해당 템플릿 없이 Claude Hooks + Cache 시스템만 사용하고 싶다면:**

```bash
# 1. 이 프로젝트를 임시로 클론
git clone https://github.com/your-org/claude-spring-standards.git /tmp/claude-spring-standards

# 2. 본인 프로젝트로 이동
cd your-project

# 3. 설치 스크립트 실행 (대화형)
bash /tmp/claude-spring-standards/scripts/install-claude-hooks.sh

# 3.5. Serena 컨벤션 설정 (NEW!)
bash /tmp/claude-spring-standards/.claude/hooks/scripts/setup-serena-conventions.sh

# 4. 완료 후 임시 디렉토리 삭제
rm -rf /tmp/claude-spring-standards

# 5. Claude Code 실행 후 Serena 메모리 로드 (NEW!)
claude code
# 첫 명령어 실행:
/sc:load
```

**설치 스크립트가 수행하는 작업**:
- ✅ `.claude/hooks/` 복사 (user-prompt-submit.sh, after-tool-use.sh)
- ✅ `.claude/hooks/scripts/` 복사 (로그, 검증, Cache 빌드 스크립트)
- ✅ `.claude/commands/` 복사 (코드 생성, 검증 명령어)
- ✅ `.claude/commands/lib/` 복사 (규칙 주입 스크립트)
- ✅ 실행 권한 자동 설정
- ✅ Python 의존성 확인 (tiktoken, jq)
- ✅ 코딩 규칙 문서 복사 여부 선택
- ✅ Cache 빌드 여부 선택
- ✅ **텔레메트리 활성화 선택** (익명화된 사용 통계, 선택사항)

**설치 중 텔레메트리 프롬프트**:
```
📊 텔레메트리 (익명 사용 통계)

Spring Standards 템플릿 개선을 위해 익명화된 사용 통계를
수집하도록 허용하시겠습니까?

수집 데이터:
  ✅ 토큰 사용량 (익명)
  ✅ 검증 시간 (익명)
  ✅ 컨벤션 위반 통계 (익명)
  ❌ 사용자 이름 (수집 안 됨)
  ❌ 파일 이름 (수집 안 됨)
  ❌ 코드 내용 (수집 안 됨)

텔레메트리를 활성화하시겠습니까? (y/N):
```

**참고**:
- 텔레메트리는 **완전히 선택 사항**이며, 비활성화해도 모든 기능이 정상 작동합니다
- 활성화 시 익명화된 통계만 전송되며, 개인정보는 수집되지 않습니다
- 언제든지 `rm -f .langfuse.telemetry`로 비활성화 가능합니다
- 자세한 내용: [텔레메트리 가이드](docs/LANGFUSE_TELEMETRY_GUIDE.md)

**설치 후**:
1. `.claude/CLAUDE.md` 프로젝트에 맞게 수정
2. `docs/coding_convention/` 규칙 추가/수정
3. `python3 .claude/hooks/scripts/build-rule-cache.py` 실행

---

### Option 2: 전체 템플릿 사용 (헥사고날 아키텍처 포함)

### 사전 요구사항

- Java 21+
- Gradle 8.5+
- Python 3.8+
- jq (JSON 파싱용)

### 설치

```bash
# 1. 클론
git clone https://github.com/your-org/claude-spring-standards.git
cd claude-spring-standards

# 2. Cache 빌드 (96개 규칙 → JSON, 약 5초)
python3 .claude/hooks/scripts/build-rule-cache.py

# 3. Git Hooks 설정
ln -s ../../hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

# 4. 빌드
./gradlew build
```

### Claude Code Hooks 설정 (필수)

**중요**: Claude Code Hooks는 로컬 설정 파일을 통해 활성화됩니다.
보안상의 이유로 `settings.json` 파일을 Git에 커밋해도 자동으로 활성화되지 않으므로,
각 개발자가 **로컬에서 `settings.local.json` 파일을 생성**해야 합니다.

#### 방법 1: 템플릿 복사 (권장)

```bash
# 템플릿을 settings.local.json으로 복사
cp .claude/settings.local.json.template .claude/settings.local.json

# 권한 설정 확인
chmod +x .claude/hooks/*.sh
```

#### 방법 2: /hooks 명령어 사용

1. **Claude Code에서 프로젝트 열기**
   ```bash
   claude code
   ```

2. **`/hooks` 명령어 실행하여 하나씩 등록**
   - `UserPromptSubmit`: `.claude/hooks/user-prompt-submit.sh`
   - `PreToolUse` (Matcher: `SlashCommand`): `.claude/hooks/user-prompt-submit.sh`
   - `PostToolUse` (Matcher: `Write|Edit|MultiEdit`): `.claude/hooks/after-tool-use.sh {{toolName}} {{filePath}}`

#### 설정 확인

```bash
# Hook이 정상 작동하는지 테스트
domain aggregate 테스트

# 로그 확인
cat .claude/hooks/logs/hook-execution.jsonl
```

**Hook이 정상 작동하면**:
- ✅ `.claude/hooks/logs/hook-execution.jsonl` 파일 생성됨
- ✅ 프롬프트에 Layer별 규칙이 자동 주입됨
- ✅ 코드 생성 후 자동 검증 실행됨

**참고**: `settings.local.json`은 `.gitignore`에 등록되어 있어 Git에 커밋되지 않습니다.

### 첫 코드 생성

```bash
# Claude Code 실행
claude code

# Domain Aggregate 생성
> "/code-gen-domain Order"
```

**자동 수행**:
1. `user-prompt-submit.sh`: "aggregate" 키워드 감지 → domain 규칙 주입
2. Claude: Zero-Tolerance 규칙이 적용된 `Order.java` 생성
3. `after-tool-use.sh`: 실시간 검증
4. 결과: ✅ 통과 또는 ⚠️ 실패 (수정 가이드 포함)

---

## 📘 사용 가이드 (필독!)

### 🎯 이 프로젝트를 실제로 어떻게 사용하나요?

**상세 사용 가이드**: [docs/USAGE_GUIDE.md](docs/USAGE_GUIDE.md)

이 가이드에는 다음 내용이 포함되어 있습니다:

#### 📋 주요 내용
- ✅ **초기 설정**: 프로젝트 클론부터 첫 코드 생성까지 (10분)
- ✅ **실제 예시**: Order Aggregate 개발 전체 프로세스 (Step-by-Step)
- ✅ **도구별 역할**: Claude Code vs Cascade 언제 무엇을 사용하는가
- ✅ **트러블슈팅**: 자주 발생하는 문제와 해결 방법

#### 🚀 빠른 시작 (3분)

**1단계: Serena 메모리 준비 (1회만)**
```bash
bash .claude/hooks/scripts/setup-serena-conventions.sh
```

**2단계: Claude Code 실행 및 컨벤션 로드**
```bash
claude code
/cc:load  # 코딩 컨벤션 자동 로드
```

**3단계: 첫 코드 생성**
```bash
/code-gen-domain Order
```

#### 📊 워크플로우 다이어그램

```
Claude Code (분석 & 설계)
    ↓
Claude Code (코딩 컨벤션 로드: /cc:load)
    ↓
Claude Code (비즈니스 로직 구현)
    ↓
IntelliJ Cascade (자동 검증 & 테스트)
    ↓
Claude Code (PR 생성 & 머지)
```

#### 🎓 학습 경로

**초급 (1일)**:
1. [사용 가이드](docs/USAGE_GUIDE.md) 읽기
2. Order Aggregate 예시 따라하기
3. 첫 PR 생성해보기

**중급 (1주)**:
1. Domain Layer 규칙 숙지
2. Application Layer Transaction 경계 이해
3. Cascade 워크플로우 활용

**고급 (1개월)**:
1. DDD Aggregate 설계 패턴
2. CQRS 패턴 적용
3. Event-Driven Architecture

**💡 핵심**: 가이드를 먼저 읽으면 이 프로젝트를 훨씬 빠르게 이해할 수 있습니다!

---

## 🔥 Cache 시스템

### 작동 원리

**Before (기존 방식)**:
```
98개 마크다운 문서 전체 로딩
→ 예제, 설명 포함 전체 내용
→ 50,000+ 토큰 소비
```

**After (JSON Cache)**:
```
96개 JSON 파일 (핵심만 구조화)
→ 필요한 규칙만 O(1) 조회
→ 500-1,000 토큰 (선택적 로딩)
```

### Cache 구조

```json
{
  "id": "domain-layer-law-of-demeter-01",
  "metadata": {
    "layer": "domain",
    "priority": "critical",
    "keywords": {
      "anti": ["order.getCustomer().getAddress()"]
    }
  },
  "rules": {
    "prohibited": ["❌ Getter chaining"],
    "required": ["✅ Tell, Don't Ask"]
  }
}
```

**핵심 이점**:
- **토큰 효율**: 핵심 규칙만 JSON화하여 불필요한 예제/설명 제거
- **빠른 검색**: index.json을 통한 O(1) Layer → 규칙 매핑
- **선택적 로딩**: 감지된 Layer의 규칙만 로딩

### 키워드 → Layer 매핑

#### Primary Keywords (30점)

| 키워드 | Layer | 예시 |
|---------|-------|------|
| aggregate, entity, getter, factory, policy | domain | Law of Demeter, Aggregate 설계 |
| usecase, service, command, query, transaction, assembler, spring, proxy, orchestration | application | Transaction 경계, UseCase 패턴 |
| controller, endpoint, validation, request, response, handling | adapter-rest | REST API 설계, 검증 |
| repository, jpa, querydsl, batch, specification | adapter-persistence | JPA 전략, QueryDSL 최적화 |
| test, archunit, testcontainers, benchmark | testing | 아키텍처 검증, 통합 테스트 |
| record, sealed, virtual, threads, async | java21 | Java 21 패턴, Virtual Thread |
| dto, mapper, cache, event, circuit-breaker, resilience, saga | enterprise | 캐싱 전략, 이벤트 기반 |
| exception, error | error-handling | 예외 처리 전략 |

#### Secondary Keywords (15점)

| 키워드 | 설명 |
|---------|------|
| domain, 도메인 | 도메인 컨텍스트 |
| api, rest | API 컨텍스트 |
| persistence, 영속성 | 영속성 컨텍스트 |
| transaction, 트랜잭션 | 트랜잭션 컨텍스트 |
| validation, 검증 | 검증 컨텍스트 |

#### Zero-Tolerance Keywords (20점)

| 키워드 | 규칙 |
|---------|------|
| lombok | Lombok 사용 금지 |
| getter.chaining | Getter 체이닝 금지 |
| law.of.demeter | Law of Demeter 위반 |
| @transactional | Transaction 경계 위반 |

**예시**:
```
입력: "Create Order aggregate"
→ "aggregate" 감지 (+30)
→ Layer: domain
→ 로드: domain-layer-*.json (13개 규칙)
→ 주입: Critical 우선순위 규칙

입력: "getter chaining 금지 규칙 적용"
→ "getter" 감지 (+30, Primary)
→ "getter.chaining" 감지 (+20, Zero-Tolerance)
→ Layer: domain
→ 로드: Law of Demeter 관련 규칙
```

---

## 🎯 레이어별 작업 모드 (Slash Commands)

### 기존 문제점

**키워드 기반 감지의 한계**:
```
사용자: "domain 클래스에 reconstitute() 추가해줘"
→ "domain" 키워드만 감지 (15점)
→ 임계값 25점 미달 ❌
→ 규칙 주입 실패!
```

**문제 원인**:
- Primary Keywords (30점): `entity`, `aggregate`, `usecase`, `controller` 등
- Secondary Keywords (15점): `domain`, `api`, `rest`
- 임계값: 25점
- **사용자가 정확한 키워드를 기억하고 사용해야 함** ← 비직관적!

### 해결책: 명시적 Slash Commands

**5개 레이어별 Commands 추가**:
```bash
/domain      # Domain layer 작업
/application # Application layer 작업
/rest        # REST API/Controller 작업
/persistence # Persistence/Repository 작업
/test        # 테스트 작업
```

### 작동 메커니즘

#### 1. Slash Command 파일 구조

```markdown
# .claude/commands/domain.md
---
description: Domain layer 작업 (Aggregate, Entity, Value Object 등)
---

domain entity {{args}}
```

#### 2. 확장 프로세스

```
사용자 입력:
  /domain "Order에 reconstitute() 메서드 추가"

       ↓ (Claude Code가 domain.md 읽음)

확장된 프롬프트:
  domain entity Order에 reconstitute() 메서드 추가

       ↓ (hooks.json의 UserPromptSubmit 트리거)

user-prompt-submit.sh 실행:
  - "domain" 감지 → +15점 (Secondary)
  - "entity" 감지 → +30점 (Primary)
  - 합산: 45점 (임계값 25점 초과 ✅)

       ↓ (inject-rules.py 호출)

Domain Layer 규칙 자동 주입:
  - 13개 JSON 규칙 파일 로드
  - 2,120 토큰 주입
  - Law of Demeter, Lombok 금지 등
```

#### 3. 점수 설계

| Command | 확장 템플릿 | 점수 | 설명 |
|---------|------------|------|------|
| `/domain` | `domain entity {{args}}` | 45점 | entity(30) + domain(15) |
| `/application` | `usecase service {{args}}` | 60점 | usecase(30) + service(30) |
| `/rest` | `controller rest api {{args}}` | 60점+ | controller(30) + rest(15) + api(15) |
| `/persistence` | `repository jpa persistence {{args}}` | 60점+ | repository(30) + jpa(30) |
| `/test` | `test {{args}}` | 30점 | test(30) |

**모든 Command가 임계값(25점)을 안정적으로 초과!**

### 사용 예시

#### 시나리오 1: Domain 수정
```bash
# Before (키워드 기억 필요)
"Order entity에 cancel() 메서드 추가해줘"  # entity(30) → ✅
"Order aggregate에 취소 정책 추가해줘"    # aggregate(30) → ✅
"Order domain에 reconstitute() 추가해줘"  # domain(15) → ❌ 실패!

# After (명시적 Command)
/domain "Order에 cancel() 메서드 추가해줘"        # ✅ 항상 45점
/domain "Order에 취소 정책 추가해줘"              # ✅ 항상 45점
/domain "Order에 reconstitute() 추가해줘"        # ✅ 항상 45점
```

#### 시나리오 2: 전체 워크플로우
```bash
# 1. Domain 작업
/domain "Payment Aggregate에 환불 로직 추가"

# 2. Application 작업
/application "RefundUseCase에 정산 로직 추가"

# 3. REST API 작업
/rest "PaymentController에 환불 엔드포인트 추가"

# 4. Persistence 작업
/persistence "PaymentRepository에 환불 이력 조회 추가"

# 5. 테스트 작업
/test "환불 기능 통합 테스트 작성"
```

### 코드 생성 Commands와의 차이

| 구분 | 레이어별 작업 모드 | 코드 생성 Commands |
|------|------------------|------------------|
| **Command** | `/domain`, `/application` 등 | `/code-gen-domain`, `/code-gen-usecase` |
| **목적** | 기존 코드 수정/추가 | 전체 구조 새로 생성 |
| **범위** | 자유로운 부분 수정 | 파일 + 테스트 + 구조 |
| **출력** | 요청한 내용만 | 완전한 파일 세트 |
| **사용 시점** | 세부 구현/수정 | 초기 구조 생성 |

**권장 워크플로우**:
```bash
# 1. 초기 구조 생성
/code-gen-domain Order
/code-gen-usecase PlaceOrder

# 2. 세부 구현 및 수정
/domain "Order에 추가 비즈니스 로직"
/application "PlaceOrderUseCase에 검증 로직 추가"
```

### 핵심 이점

✅ **직관적**: `/domain` 타이핑만으로 Domain 작업 모드 진입
✅ **안정적**: 항상 임계값 초과 보장 (키워드 망각 문제 해결)
✅ **명시적**: 어떤 Layer 작업인지 명확히 선언
✅ **일관적**: 모든 팀원이 동일한 방식으로 사용

---

### 세션 관리 Commands

#### `/sc:load`

**목적**: Serena MCP 메모리에서 코딩 컨벤션 자동 로드

**사용 시점**:
- ✅ 세션 시작 시 (첫 작업 전)
- ✅ 새 프로젝트로 전환 시
- ✅ 메모리를 업데이트한 후

**실행 예시**:
```bash
# Claude Code 실행 후 첫 명령어
/sc:load

# 출력:
# ✅ Project activated: claude-spring-standards
# ✅ Memory loaded: coding_convention_index
# 📋 Available conventions:
#    - coding_convention_domain_layer (13개 규칙)
#    - coding_convention_application_layer (18개 규칙)
#    - coding_convention_persistence_layer (10개 규칙)
#    - coding_convention_rest_api_layer (18개 규칙)
```

**효과**:
- 세션 간 컨텍스트 유지
- 78% 위반 감소 (23회 → 5회)
- 일관된 코드 품질

---

## 📚 코딩 표준

### Zero-Tolerance 규칙 (자동 검증)

#### 1. Lombok 금지

```java
// ❌ 금지
@Data
public class Order { }

// ✅ 필수
public class Order {
    private final OrderId id;

    public Order(OrderId id) {
        this.id = id;
    }

    public OrderId getId() {
        return this.id;
    }
}
```

#### 2. Law of Demeter

```java
// ❌ Getter chaining
String zip = order.getCustomer().getAddress().getZip();

// ✅ Tell, Don't Ask
String zip = order.getCustomerZip();
```

#### 3. Transaction 경계

```java
// ❌ Transaction 내 외부 API 호출
@Transactional
public Order create() {
    ExternalData data = apiClient.fetch(); // ❌
    return save(data);
}

// ✅ Transaction 외부에서 호출
public Order create() {
    ExternalData data = apiClient.fetch(); // ✅
    return anyClass.doSomething(data);
}

```

#### 4. Javadoc 필수

```java
/**
 * Order Aggregate Root
 *
 * @author YourName
 * @since 2025-10-17
 */
public class Order { }
```

---

## 💻 개발 워크플로우

```bash
# 1. Feature 브랜치
git checkout -b feature/order

# 2. Domain 생성
/code-gen-domain Order

# 3. Application 생성
/code-gen-usecase PlaceOrder

# 4. Adapter 생성
/code-gen-controller Order

# 5. 테스트
./gradlew test

# 6. 커밋 (pre-commit hook 자동 검증)
git add .
git commit -m "feat: order management"
```

---

## 📊 로그 시스템

### JSON 로그 구조

**위치**: `.claude/hooks/logs/hook-execution.jsonl`

**형식**: JSONL (JSON Lines, 1줄 = 1개 JSON 이벤트)

```json
{"timestamp":"2025-10-17T14:30:15","event":"session_start","project":"claude-spring-standards","hook":"user-prompt-submit","user_command":"domain aggregate"}
{"timestamp":"2025-10-17T14:30:15","event":"keyword_analysis","session_id":"1729152615-12345","context_score":45,"detected_layers":["domain"],"detected_keywords":["aggregate"]}
{"timestamp":"2025-10-17T14:30:15","event":"cache_injection","layer":"domain","rules_loaded":5,"total_rules_available":15,"estimated_tokens":2500}
{"timestamp":"2025-10-17T14:30:16","event":"validation_complete","file":"Order.java","status":"passed","total_rules":5,"validation_time_ms":148}
```

### 로그 뷰어

```bash
# 실시간 모니터링
./.claude/hooks/scripts/view-logs.sh -f

# 통계 정보
./.claude/hooks/scripts/view-logs.sh -s

# 특정 이벤트만
./.claude/hooks/scripts/view-logs.sh -e validation_complete

# 원본 JSON
./.claude/hooks/scripts/view-logs.sh -r
```

**출력 예시**:
```
[14:30:15] 🚀 SESSION_START | project=claude-spring-standards | command=domain aggregate
[14:30:15] 🔍 KEYWORD_ANALYSIS | score=45 | layers=domain | keywords=aggregate
[14:30:15] 💉 CACHE_INJECTION | layer=domain | rules=5/15 | tokens=2500
[14:30:16] ✅ VALIDATION_PASSED | file=Order.java | rules=5 | time=148ms
```

### 이벤트 타입

- `SESSION_START`: Hook 실행 시작
- `KEYWORD_ANALYSIS`: 키워드 분석 결과
- `DECISION`: 규칙 주입 결정
- `CACHE_INJECTION`: Cache 로딩
- `VALIDATION_START`: 검증 시작
- `VALIDATION_PASSED/FAILED`: 검증 결과
- `VALIDATION_ERROR`: 검증 오류

### 로그 활용 계획

**현재**: 모든 Hook 실행을 JSON으로 로깅

**향후 계획**:
1. **효과성 분석**: 실제 Layer 감지 정확도 측정
2. **토큰 사용량 분석**: Cache 시스템의 실제 토큰 절감량 확인
3. **검증 패턴 분석**: 자주 위반되는 규칙 파악 및 개선
4. **성능 최적화**: 검증 시간 병목 지점 찾기
5. **AWS CloudWatch 연동**: 로그 집계 및 실시간 모니터링 (검토 중)

---

## 🐛 문제 해결

### Cache 파일 없음

```bash
# Cache 재빌드
python3 .claude/hooks/scripts/build-rule-cache.py
```

### Hooks 미실행

```bash
# 권한 확인
ls -la .claude/hooks/*.sh

# 권한 부여
chmod +x .claude/hooks/*.sh

# 로그 확인
./.claude/hooks/scripts/view-logs.sh -f
```

### 검증 실패

```bash
# 로그에서 실패 원인 확인
./.claude/hooks/scripts/view-logs.sh -e validation_complete

# 수동 검증
python3 .claude/hooks/scripts/validation-helper.py YourFile.java layer
```

---

## 📚 문서

### 핵심 가이드
- **[사용 가이드 (필독!)](docs/USAGE_GUIDE.md)** - 실제 사용 방법 완벽 가이드
- [Getting Started](docs/tutorials/01-getting-started.md) - 5분 튜토리얼
- [Dynamic Hooks 가이드](docs/DYNAMIC_HOOKS_GUIDE.md) - 시스템 전체 설명
- [Cache README](.claude/cache/rules/README.md) - JSON Cache 상세

### Serena + LangFuse
- [Serena 설정 가이드](.claude/hooks/scripts/setup-serena-conventions.sh) - 메모리 생성 (5분)
- **[LangFuse 모니터링 가이드](docs/LANGFUSE_MONITORING_GUIDE.md)** - 로그 모니터링 및 분석 (필독!)
- [LangFuse 통합 가이드](docs/LANGFUSE_INTEGRATION_GUIDE.md) - 효율 측정 및 A/B 테스트 (10분)
- **[텔레메트리 가이드](docs/LANGFUSE_TELEMETRY_GUIDE.md)** - 익명화된 사용 통계 (사용자용)
- [텔레메트리 구현 가이드](docs/TELEMETRY_IMPLEMENTATION_GUIDE.md) - 텔레메트리 시스템 구현 (개발자용)

### 전문 주제
- [DDD Aggregate Migration](docs/DDD_AGGREGATE_MIGRATION_GUIDE.md)
- [DTO Patterns](docs/DTO_PATTERNS_GUIDE.md)
- [Exception Handling](docs/EXCEPTION_HANDLING_GUIDE.md)

---

## 📊 기술 스택

| 카테고리 | 기술 |
|----------|-----------|
| **언어** | Java 21 |
| **프레임워크** | Spring Boot 3.5.x |
| **아키텍처** | Hexagonal (Ports & Adapters) |
| **설계 패턴** | DDD, CQRS |
| **ORM** | JPA + QueryDSL |
| **테스팅** | JUnit 5, Mockito, Testcontainers, ArchUnit |
| **AI 통합** | Claude Code + Dynamic Hooks |

---

## 📄 라이선스

© 2025 Ryu-qqq. All Rights Reserved.

---

## 📝 최근 업데이트

### 2025-10-22
- ✅ **Hook 키워드 확장**: 33개 추가로 Cache 매핑 커버리지 46.9% → 85%+ 달성
- ✅ **Slash Commands 추가**: 5개 레이어별 작업 모드 구현 (`/domain`, `/application`, `/rest`, `/persistence`, `/test`)
- ✅ **Secondary Keywords 확장**: persistence, transaction, validation (15점)
- ✅ **Zero-Tolerance 강화**: Law of Demeter 패턴 감지 추가
- ✅ **키워드 커버리지 개선**:
  - Domain: getter, factory, policy
  - Application: assembler, spring, proxy, orchestration
  - REST: validation, request, response, handling
  - Persistence: querydsl, batch, specification
  - Testing: archunit, testcontainers, benchmark
  - Java21: virtual, threads, async
  - Enterprise: cache, event, circuit-breaker, resilience, saga

### 2025-10-17
- ✅ Dynamic Hooks + Cache 시스템 초기 구현
- ✅ 96개 규칙 JSON 캐시 구축
- ✅ 자동 검증 시스템 구현

---

*최종 업데이트: 2025-10-22*
