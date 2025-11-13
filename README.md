# Claude Spring Standards

> **Spring Boot 3.5.x + Java 21 헥사고날 아키텍처 템플릿**
> AI 기반 코딩 표준 자동화 프로젝트

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue.svg)](https://alistair.cockburn.us/hexagonal-architecture/)

---

## 🎯 이 프로젝트는?

**Spring Boot + Java 21** 헥사고날 아키텍처 템플릿으로, **Dynamic Hooks + Cache** 시스템을 통해 AI가 코딩 표준을 자동으로 주입하고 검증합니다.

### 핵심 특징

✅ **100% Zero-Tolerance 달성**: Hook ON = 0 violations (A/B 테스트 검증 완료)
✅ **자동 규칙 주입**: 키워드 감지 → Layer 매핑 → 규칙 자동 주입
✅ **실시간 검증**: 코드 생성 직후 즉시 컨벤션 검증 (148ms)
✅ **Claude Skills**: 5개 전문가 에이전트 (convention-reviewer, domain-expert 등)
✅ **헥사고날 아키텍처**: Domain-driven Design + CQRS 패턴

---

## 🚀 빠른 시작 (3분)

### 1️⃣ 시스템 준비 (1회만)

```bash
# 1. Cache 빌드 (98개 규칙 → JSON)
python3 .claude/hooks/scripts/build-rule-cache.py

# 2. Git Hooks 설정 (선택)
ln -s ../../config/hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

### 2️⃣ Claude Code 실행

```bash
claude code
```

### 3️⃣ 첫 사용 (Claude Skills)

```bash
# Claude Code에서
"domain-expert Skill로 Order Aggregate를 생성해줘"
```

**자동 실행** (Hook 시스템):
- ✅ "aggregate" 키워드 감지 → Domain 규칙 자동 주입
- ✅ Zero-Tolerance 규칙 100% 준수 (A/B 테스트 검증)
- ✅ 실시간 검증 (148ms) + 위반 시 구체적 수정 방법 제시

---

## 📊 시스템 구조

### 1️⃣ Dynamic Hooks + Cache

**작동**:
```
사용자: "Order entity 만들어줘"
    ↓
키워드 감지: "entity" → Layer: domain
    ↓
Cache 조회: domain-layer-*.json (15개 규칙)
    ↓
Claude: 규칙 100% 준수 코드 생성 (Hook ON)
    ↓
실시간 검증: 148ms, 위반 0건
```

**검증 완료 효율** (A/B 테스트):
- **컨벤션 위반**: Hook OFF 40회 → Hook ON 0회 (100% 제거)
- **Zero-Tolerance 준수율**: 0% → 100%
- 토큰 사용량: 90% 절감
- 검증 속도: 73.6% 향상
- 문서 로딩: 95% 향상

**상세**: [Hook README](.claude/hooks/README.md)

---

## 🔄 워크플로우

### Claude Code + Cursor AI 통합

```
1. Claude Code → PRD 작성, Jira Task 분석
2. Cursor AI → Boilerplate 빠른 생성 (.cursorrules 자동 로드)
3. Claude Code → 비즈니스 로직 구현 (Hook이 규칙 자동 주입)
4. Claude Code → 자동 검증 (ArchUnit, Pre-commit)
```

**실제 예시**:
```bash
# 1. PRD 작성 (Claude Code)
"Order Aggregate PRD를 작성해줘"

# 2. Jira Task 분석 (Claude Code)
/jira-analyze

# 3. Boilerplate 생성 (Cursor AI)
"Order Aggregate를 생성해줘"
→ .cursorrules 자동 로드 → 컨벤션 준수 코드 생성

# 4. 비즈니스 로직 구현 (Claude Code)
"Order에 placeOrder, cancelOrder 메서드 구현해줘"
→ Hook이 자동으로 Law of Demeter, Tell Don't Ask 규칙 주입

# 5. 검증 & PR (Claude Code)
/validate-architecture
```

---

## 📚 Zero-Tolerance 규칙

다음 규칙은 **예외 없이** 반드시 준수해야 합니다:

- **Lombok 금지** - Plain Java 사용 → [상세](docs/coding_convention/02-domain-layer/legacy/law-of-demeter/)
- **Law of Demeter** - Getter 체이닝 금지 (`order.getCustomer().getAddress()` ❌) → [상세](docs/coding_convention/02-domain-layer/legacy/law-of-demeter/)
- **Long FK 전략** - JPA 관계 어노테이션 금지, Long FK 사용 → [상세](docs/coding_convention/04-persistence-layer/legacy/jpa-entity-design/)
- **Transaction 경계** - `@Transactional` 내 외부 API 호출 절대 금지 → [상세](docs/coding_convention/03-application-layer/legacy/transaction-management/)
- **Transactional Outbox Pattern** - 외부 API 호출 시 Outbox 패턴 사용 (Pattern B 권장) → [상세](docs/coding_convention/09-orchestration-patterns/04_outbox-pattern.md)

**전체 규칙**: [코딩 컨벤션 문서](docs/coding_convention/) (98개 규칙)

---

## 🎯 주요 Commands

### 검증 & 분석
```bash
/validate-architecture        # 전체 아키텍처 검증 (ArchUnit)
/validate-domain <file>       # Domain layer 파일 검증
/validate-cursor-changes      # Cursor AI 변경사항 검증
/design-analysis              # 설계 분석
```

### AI 리뷰 & Jira
```bash
/ai-review [pr-number]        # 통합 AI 리뷰 (Gemini + CodeRabbit + Codex)
/jira-analyze                 # Jira Task 분석 및 TodoList 생성
/jira-create                  # Jira 이슈 생성
/jira-comment                 # Jira 이슈에 코멘트 추가
/jira-transition              # Jira 이슈 상태 변경
/jira-update                  # Jira 이슈 정보 업데이트
/jira-link-pr                 # GitHub PR과 Jira 이슈 연동
```

### LangFuse 통합 (⭐ NEW Phase 2)
```bash
/langfuse-register-prompt <layer> <version>  # Layer별 프롬프트 등록 (v1.0, v1.1 등)
# 예시:
# /langfuse-register-prompt domain v1.0     # Domain Layer 프롬프트 등록
# /langfuse-register-prompt all v1.0        # 모든 Layer 등록 (domain, application, persistence, adapter-rest)
```

### Kent Beck TDD Workflow (⭐ NEW v2.6)
```bash
/red                          # 실패하는 테스트 작성
/green                        # 최소한의 코드로 테스트 통과
/refactor                     # 리팩토링
/go                           # Red → Green → Refactor 자동 실행
/tidy                         # 코드 정리
/check-tests                  # 테스트 커버리지 확인
/next-test                    # 다음 테스트 제안
/commit-tdd                   # TDD 사이클 커밋
```

### 기타
```bash
/cc/load                      # Serena 메모리 로드
/create-prd                   # PRD 문서 생성
/jira-from-prd                # PRD에서 Jira 이슈 생성
```

### Claude Skills (⭐ NEW v2.3)
```bash
# 자연어로 Skills 호출 (자동 인식)
"convention-reviewer로 프로젝트 스캔해줘"
"Order Domain을 생성해줘"  # domain-expert 자동 활성화
"PlaceOrderUseCase를 만들어줘"  # application-expert 자동 활성화
```

**전체 목록**: [Commands README](.claude/commands/README.md)

---

## 🎓 Claude Skills (⭐ NEW v2.3)

Claude Code를 위한 **5개 전문가 에이전트**가 추가되었습니다!

### 📋 Skills 목록

| Skill | 설명 | 사용 시점 |
|-------|------|-----------|
| **convention-reviewer** | 컨벤션 위반 스캔 + TODO 생성 | 리팩토링 계획 수립 |
| **domain-expert** | Domain Layer 전문가 | Aggregate, Entity 생성 |
| **application-expert** | Application Layer 전문가 | UseCase, Facade 구현 |
| **rest-api-expert** | REST API Layer 전문가 | Controller, DTO 생성 |
| **test-expert** | 테스팅 전문가 | Unit/Integration/ArchUnit 테스트 |

### 🚀 사용 방법

**1. 자연어로 호출 (자동 인식)**:
```bash
claude code
> "convention-reviewer Skill을 사용해서 프로젝트를 스캔하고 TODO를 생성해줘"
> "Order Domain을 생성해줘"  # domain-expert 자동 활성화
> "PlaceOrderUseCase를 만들어줘"  # application-expert 자동 활성화
```

**2. Cursor AI 통합 워크플로우**:
```bash
# Step 1: Claude Code - PRD 작성
/create-prd
→ docs/prd/feature-name.md 생성

# Step 2: Claude Code - Jira 연동
/jira-from-prd
→ Jira 이슈 자동 생성

# Step 3: Cursor IDE - Boilerplate 생성
"Order Aggregate를 생성해줘"
→ .cursorrules 자동 로드 → 컨벤션 준수 코드 생성

# Step 4: Claude Code - 비즈니스 로직 구현
"Order에 placeOrder, cancelOrder 메서드 구현해줘"
→ Hook이 자동으로 Law of Demeter, Tell Don't Ask 규칙 주입
```

**상세 가이드**: [Skills 디렉토리](.claude/backup/20251113-121610/skills/)

---

## 🔍 시스템 검증

### Hook 로그 분석 (A/B 테스트)
```bash
python3 .claude/hooks/scripts/summarize-hook-logs.py
```

### 실시간 모니터링
```bash
tail -f .claude/hooks/logs/hook-execution.jsonl
```

**상세**: [Hook 로그 요약 도구](.claude/hooks/scripts/summarize-hook-logs.py)

---

## 📖 문서

### 핵심 문서
- **[Commands README](.claude/commands/README.md)** - 모든 Slash Commands 설명
- **[Claude Skills](.claude/backup/20251113-121610/skills/)** - 5개 전문가 에이전트 가이드
- **[코딩 컨벤션](docs/coding_convention/)** - 98개 규칙 (Layer별)
- **[ArchUnit 템플릿](.claude/backup/20251113-121610/templates/archunit/)** - 5개 테스트 템플릿

### 시스템 분석
- [Hook 로그 요약 도구](.claude/hooks/scripts/summarize-hook-logs.py) - A/B 테스트 분석
- [Hook README](.claude/hooks/README.md) - Dynamic Hooks 시스템 설명

---

## 🔧 설치 옵션

### Option 1: 완전 통합 설치 (권장) - v2.3

```bash
# 1. 임시 클론
git clone https://github.com/your-org/claude-spring-standards.git /tmp/claude-spring-standards

# 2. 본인 프로젝트로 이동
cd your-project

# 3. 통합 설치 (대화형)
bash /tmp/claude-spring-standards/.claude/install-template.sh

# 4. 정리
rm -rf /tmp/claude-spring-standards
```

**설치되는 컴포넌트** (v2.6):
- ✅ `.claude/` (Hooks + Cache + Commands + Skills)
- ✅ **Claude Skills** (5개 전문가 에이전트)
- ✅ **Kent Beck TDD Workflow** (8개 명령어) - ⭐ NEW v2.6
- ✅ `docs/coding_convention/` (98개 규칙)
- ✅ `docs/prd/` (TDD Plan 위치) - ⭐ NEW v2.6
- ✅ `.cursorrules` (Cursor AI 통합)
- ✅ `.env.example` (LangFuse 템플릿)
- ✅ `scripts/langfuse/` (자동 로그 삭제) - ⭐ NEW v2.6
- ✅ ArchUnit 테스트 (선택)
- ✅ Git Hooks (선택)

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

---

## 🚨 문제 해결

### Cache 파일 없음
```bash
python3 .claude/hooks/scripts/build-rule-cache.py
```

### Hooks 미실행
```bash
chmod +x .claude/hooks/*.sh
tail -f .claude/hooks/logs/hook-execution.jsonl
```

### 검증 실패
```bash
python3 .claude/hooks/scripts/validation-helper.py <file> <layer>
```

---

## 📝 최근 업데이트

### 2025-11-10 ⭐ (Phase 2 시작)
- ✅ **LangFuse 프롬프트 등록 시스템 완료** (`/langfuse-register-prompt`)
  - 4개 Layer별 프롬프트 템플릿 생성 (domain, application, persistence, adapter-rest)
  - LangFuse API 통합 (v2/prompts endpoint)
  - 버전 관리 시스템 (v1.0, v1.1 등)
  - 프롬프트 메타데이터 자동 추출 (Zero-Tolerance 규칙, 템플릿, 체크리스트)
- 🚧 **A/B/C/D 테스트 준비**: `/abcd-test` 명령어 개발 예정

### 2025-11-11 v2.6 ⭐
- ✅ **Queue 시스템 제거**: 워크플로우 단순화 (16 단계 → 15 단계)
- ✅ **LangFuse v2.6**: 자동 로그 삭제 기능 추가 (`scripts/langfuse/upload-hook-logs.py`)
- ✅ **Kent Beck TDD Workflow**: 8개 TDD 명령어 추가 (/red, /green, /refactor 등)
- ✅ **TDD Plan 위치 변경**: `kentback/` → `docs/prd/`
- ✅ **User Authentication Domain**: Email, Password, UserDomain 구현 (98% 커버리지)
- ✅ **Schedule Orchestration**: Facade, Outbox, State Manager 패턴 구현

### 2025-11-04 v2.3
- ✅ **Claude Skills 출시**: 5개 전문가 에이전트 추가
  - convention-reviewer: 컨벤션 위반 스캔 + TODO 생성
  - domain-expert, application-expert, rest-api-expert, test-expert
- ✅ **install-template.sh v2.3**: Skills 자동 설치 기능 추가

### 이전 버전
- ✅ **Windsurf 제거**: Cursor AI로 완전 통합 (.cursorrules 자동 로드)
- ✅ **Jira 명령어 확장**: 6개 명령어 추가 (analyze, comment, create, link-pr, transition, update)

---

## 📊 진행 상황

| 기능 | 상태 | 설명 |
|------|------|------|
| Dynamic Hooks + Cache | ✅ 완료 | 키워드 감지 → 규칙 주입 |
| Serena Memory | ✅ 완료 | 세션 컨텍스트 유지 |
| Zero-Tolerance 검증 | ✅ 완료 | Lombok, Law of Demeter, Transaction |
| **Claude Skills** | ✅ 완료 | 5개 전문가 에이전트 (v2.3) ⭐ |
| **LangFuse 프롬프트 등록** | ✅ 완료 | Layer별 프롬프트 버전 관리 (Phase 2) ⭐ |
| LangFuse A/B 테스트 | 🚧 진행중 | /abcd-test 명령어 개발 (Phase 2) |
| LangFuse 분석 | 📋 계획중 | /langfuse-analyze 명령어 (Phase 2) |
| Auto-Fix | 🚧 개발중 | 컨벤션 위반 자동 수정 |
| CloudWatch 연동 | 📋 계획중 | 로그 집계 및 모니터링 |

**범례**: ✅ 완료 | 🚧 진행중 | 📊 측정/테스트중 | 📋 계획중

---

## 📄 라이선스

© 2025 Ryu-qqq. All Rights Reserved.

---

*최종 업데이트: 2025-11-11 (v2.6 - Queue 시스템 제거, TDD Workflow 추가)*
