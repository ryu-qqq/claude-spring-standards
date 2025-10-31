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

✅ **자동 규칙 주입**: 키워드 감지 → Layer 매핑 → 규칙 자동 주입
✅ **실시간 검증**: 코드 생성 직후 즉시 컨벤션 검증
✅ **Zero-Tolerance**: Lombok 금지, Law of Demeter, Transaction 경계 자동 검증
✅ **헥사고날 아키텍처**: Domain-driven Design + CQRS 패턴

---

## 🚀 빠른 시작 (3분)

### 1️⃣ 시스템 준비 (1회만)

```bash
# 1. Cache 빌드 (98개 규칙 → JSON)
python3 .claude/hooks/scripts/build-rule-cache.py

# 2. Serena 메모리 초기화
bash .claude/hooks/scripts/setup-serena-conventions.sh

# 3. Git Hooks 설정
ln -s ../../hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

### 2️⃣ Claude Code 실행

```bash
claude code
/cc:load  # 코딩 컨벤션 로드
```

### 3️⃣ 첫 코드 생성

```bash
/code-gen-domain Order
```

**자동 실행**:
- ✅ "aggregate" 키워드 감지 → Domain 규칙 주입
- ✅ Zero-Tolerance 규칙 적용 (Lombok 금지, Law of Demeter)
- ✅ 실시간 검증 (148ms)

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
Claude: 규칙 준수 코드 생성
```

**목표 효율** (🚧 측정 진행중):
- 토큰 사용량: 90% 절감 예상
- 검증 속도: 73.6% 향상 예상
- 문서 로딩: 95% 향상 예상

**상세**: [Dynamic Hooks Guide](docs/DYNAMIC_HOOKS_GUIDE.md)

---

### 2️⃣ Serena Memory + LangFuse

**작동**:
```
setup-serena-conventions.sh (1회)
    ↓
Serena MCP: 5개 메모리 생성
    ↓
/cc:load (매 세션)
    ↓
컨텍스트 유지 + LangFuse 측정
```

**목표** (🚧 A/B 테스트 준비중):
- 컨벤션 위반 감소 목표: 78%
- 세션 시간 단축 목표: 47%

**상세**: [LangFuse 통합 가이드](docs/LANGFUSE_INTEGRATION_GUIDE.md)

---

## 🔄 워크플로우

### Claude Code + Windsurf 통합

```
1. Claude Code → PRD 작성, Jira Task 생성
2. Windsurf → Boilerplate 빠른 생성 (.windsurf/rules 자동 로드)
3. Claude Code → 비즈니스 로직 구현 (Serena 메모리 컨텍스트)
4. Claude Code → 자동 검증 (ArchUnit, Pre-commit)
```

**실제 예시**:
```bash
# 1. PRD 작성
"Order Aggregate PRD를 작성해줘"

# 2. Jira Task
/jira-analyze

# 3. Windsurf (IntelliJ)
"Order Aggregate를 생성해줘"

# 4. Claude Code
/cc:load
"Order에 placeOrder, cancelOrder 메서드 구현해줘"

# 5. 검증 & PR
/validate-architecture
```

**상세**: [사용 가이드 (필독!)](docs/USAGE_GUIDE.md)

---

## 📚 Zero-Tolerance 규칙

### 1. Lombok 금지

```java
// ❌ 금지
@Data
public class Order { }

// ✅ 필수
public class Order {
    private final OrderId id;
    public OrderId getId() { return id; }
}
```

### 2. Law of Demeter

```java
// ❌ Getter chaining
order.getCustomer().getAddress().getZip();

// ✅ Tell, Don't Ask
order.getCustomerZip();
```

### 3. Transaction 경계

```java
// ❌ Transaction 내 외부 API
@Transactional
public Order create() {
    ExternalData data = apiClient.fetch(); // ❌
    return save(data);
}

// ✅ Transaction 외부에서 호출
public Order create() {
    ExternalData data = apiClient.fetch(); // ✅
    return doSomething(data);
}
```

**상세**: [코딩 컨벤션 문서](docs/coding_convention/)

---

## 🎯 주요 Commands

### 세션 관리
```bash
/cc:load  # 코딩 컨벤션 로드 (매 세션 시작 시)
```

### 코드 생성
```bash
/code-gen-domain <name>      # Domain Aggregate
/code-gen-usecase <name>     # Application UseCase
/code-gen-controller <name>  # REST Controller
```

### 검증
```bash
/validate-domain <file>       # Domain 검증
/validate-architecture        # 전체 아키텍처 검증
```

### AI 리뷰
```bash
/ai-review [pr-number]        # 통합 AI 리뷰 (Gemini + CodeRabbit)
/jira-analyze                 # Jira Task 분석
```

**전체 목록**: [Commands README](.claude/commands/README.md)

---

## 🔍 시스템 검증

### Serena 메모리 검증
```bash
bash .claude/hooks/scripts/verify-serena-memories.sh
```

### Hook 로그 분석
```bash
python3 .claude/hooks/scripts/summarize-hook-logs.py
```

### 실시간 모니터링
```bash
tail -f .claude/hooks/logs/hook-execution.jsonl
```

**상세**: [시스템 검증 가이드](docs/SYSTEM_FLOW.md)

---

## 📖 문서

### 필수 가이드
- **[사용 가이드 (필독!)](docs/USAGE_GUIDE.md)** - 실제 사용 방법 완벽 가이드
- [Getting Started](docs/tutorials/01-getting-started.md) - 5분 튜토리얼
- [Dynamic Hooks 가이드](docs/DYNAMIC_HOOKS_GUIDE.md) - 시스템 전체 설명

### Serena + LangFuse
- [LangFuse 모니터링 가이드](docs/LANGFUSE_MONITORING_GUIDE.md) - 로그 분석
- [Slash Command 로깅](docs/LANGFUSE_SLASH_COMMAND_LOGGING.md) - Command 추적
- [텔레메트리 가이드](docs/LANGFUSE_TELEMETRY_GUIDE.md) - 익명 통계

### Windsurf IDE
- [Windsurf 가이드](.windsurf/README.md) - 14개 워크플로우
- [Windsurf Rules](.windsurf/rules/) - Layer별 규칙 (자동 로드)
- [Windsurf Workflows](.windsurf/workflows/) - 코드 생성 워크플로우

---

## 🔧 설치 옵션

### Option 1: 완전 통합 설치 (권장)

```bash
# 1. 임시 클론
git clone https://github.com/your-org/claude-spring-standards.git /tmp/claude-spring-standards

# 2. 본인 프로젝트로 이동
cd your-project

# 3. 통합 설치
bash /tmp/claude-spring-standards/scripts/install-complete-system.sh

# 4. 정리
rm -rf /tmp/claude-spring-standards
```

**설치되는 컴포넌트**:
- ✅ Claude Code (Hooks + Cache + Commands + Serena)
- ✅ Windsurf (Rules + Workflows + Templates)
- ✅ Coding Convention Docs (98개 규칙)
- ✅ Scripts (Pipeline, LangFuse)

### Option 2: Claude 설정만 복사

```bash
bash /tmp/claude-spring-standards/scripts/install-claude-hooks.sh
```

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

### 2025-10-31
- ✅ **Cascade → Pipeline 메트릭 통합**: `.cascade/` → `.pipeline-metrics/`
- ✅ **Jira 명령어 확장**: 5개 명령어 추가 (comment, create, link-pr, transition, update)
- ✅ **Hook 스크립트 추가**: 3개 (log-slash-command, summarize-hook-logs, verify-serena-memories)

### 2025-10-30
- ✅ **Windsurf Workflows 최적화**: 15개 → 12개 (중복 제거, 통합)
- 🚧 **지능형 Auto-Fix**: 컨벤션 위반 자동 수정 (개발중)
- 🚧 **Serena Memory 학습**: 위반 패턴 학습 시스템 (테스트중)

### 2025-10-22
- ✅ **Hook 키워드 확장**: 33개 추가 (커버리지 85%+)
- ✅ **Slash Commands**: 5개 레이어별 작업 모드 (`/domain`, `/application` 등)

---

## 📊 진행 상황

| 기능 | 상태 | 설명 |
|------|------|------|
| Dynamic Hooks + Cache | ✅ 완료 | 키워드 감지 → 규칙 주입 |
| Serena Memory | ✅ 완료 | 세션 컨텍스트 유지 |
| Zero-Tolerance 검증 | ✅ 완료 | Lombok, Law of Demeter, Transaction |
| LangFuse 통합 | 🚧 진행중 | Hook 로그 → LangFuse 업로드 |
| A/B 테스트 | 📊 준비중 | 효율 측정 시스템 |
| Auto-Fix | 🚧 개발중 | 컨벤션 위반 자동 수정 |
| CloudWatch 연동 | 📋 계획중 | 로그 집계 및 모니터링 |

**범례**: ✅ 완료 | 🚧 진행중 | 📊 측정/테스트중 | 📋 계획중

---

## 📄 라이선스

© 2025 Ryu-qqq. All Rights Reserved.

---

*최종 업데이트: 2025-10-31*
