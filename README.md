# claude-spring-standards — conventionHub

**MCP 서버를 직접 만들어 운영해본 경험**. 아이디어는 "기술스택이 같은 프로젝트들이 컨벤션을 단일 원천으로 공유하면, 같은 스택의 모든 코드가 일관된 컨벤션을 가질 수 있겠다" 였고, 그걸 MCP 로 AI 도구가 조회하게 만들었다.

운영해본 결론은 README 맨 아래 [운영 회고](#-운영-회고) — 발상은 옳지만 "AI 100% 준수" 는 LLM 의 현재 메커니즘 한계로 실패. 그래도 직접 만들어 운영하면서 AI 인프라가 어떻게 동작하고 어떤 한계를 가지는지 코드로 학습한 가치는 남았다.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5+-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Python](https://img.shields.io/badge/Python-3.12-blue.svg)](https://www.python.org/)
[![MCP](https://img.shields.io/badge/MCP-FastMCP-purple.svg)](https://github.com/jlowin/fastmcp)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## ⚠️ 먼저 알아둘 것

### 처음 동기

원래 발상은 단순했다.

> "프로젝트별로 기술스택이 똑같은데 컨벤션이 각자 흩어져 있다. 단일 원천으로 관리하면 같은 기술스택의 모든 프로젝트가 일관된 컨벤션을 가질 수 있지 않을까?"

거기서 MCP 가 떠올랐다. AI 코드 생성 도구 (Claude Code, Cursor 등) 가 코드 생성 전에 MCP 로 컨벤션을 조회 → 그 규칙을 따라 코드 작성. 어느 프로젝트에서 작업하든 같은 기술스택은 같은 컨벤션을 가지게 됨.

이게 conventionHub 의 시작점.

### 운영 결과 요약

직접 운영해봤다. 솔직한 결과:

- 컨벤션 **단일 원천** 자체는 잘 동작 — DB 등록 + MCP 조회/캐싱 인프라는 안정적
- **AI 가 100% 준수** 는 실패 — 컨벤션이 컨텍스트에 들어가도 LLM 이 일관되게 따르지 못함. 게다가 162개 규칙 로딩 자체가 컨텍스트 비용이 커서 정작 코드 생성에 쓸 공간이 줄어듦
- 발상의 핵심 — **Single Source of Truth + MCP 조회 인터페이스** — 은 여전히 유효

상세는 [운영 회고](#-운영-회고).

---

## ⚡ TL;DR

- 컨벤션을 **TechStack → Architecture → Layer → Rule** 계층으로 DB 에 등록
- AI 도구가 **MCP 통해 컨벤션 조회** (15개 Tool)
- **3-Phase 워크플로우**: `planning_context` (계획) → `module_context` (생성) → `validation_context` (검증)
- 162개 예시 규칙 포함 (Spring Boot + Hexagonal Architecture 기준)
- Spring Boot 3.5 (Java 21) + Python MCP (FastMCP) + AWS Lambda 배포 가능
- CodeRabbit AI review 통합 — `.coderabbit.yaml` 로 PR 자동 review

---

## 📋 목차

- [먼저 알아둘 것](#️-먼저-알아둘-것)
- [TL;DR](#-tldr)
- [결정 배경 Q&A](#-결정-배경-qa)
- [아키텍처](#-아키텍처)
- [데이터 모델](#-데이터-모델)
- [MCP Tools (15개)](#-mcp-tools-15개)
- [3-Phase 워크플로우](#-3-phase-워크플로우)
- [시작하기](#️-시작하기)
- [기술 스택](#-기술-스택)
- [예시 데이터](#-예시-데이터)
- [운영 회고](#-운영-회고)
- [ADR 목록](#-adr-목록)

---

## 🤔 결정 배경 Q&A

### Q1. Cursor Rules / Claude.md / `.github/copilot-instructions.md` 쓰면 되잖아요?

이들은 **프로젝트 로컬 파일** — 프로젝트마다 다시 작성해야 함. 같은 기술스택 5개 프로젝트면 컨벤션을 5번 작성하고 5번 관리.

conventionHub 는 **기술스택 단위 단일 원천** — DB 한 번 등록하면 같은 스택의 모든 프로젝트가 MCP 로 조회. 다만 운영 회고에서 보듯 "AI 가 그걸 100% 따르는지" 는 별개 문제.

### Q2. 왜 헥사고날 + Java + Python 두 언어?

Spring API (Java) 가 컨벤션 CRUD + 비즈니스 로직. Python (FastMCP) 가 MCP 프로토콜 클라이언트. FastMCP 가 Python 에서 가장 성숙해서 분리.

운영 부담 (두 언어 + 두 배포 + 두 의존성 관리) 은 명백한 비용이고 over-engineering 가능성 있다. 그래도 MCP 생태계가 Python 중심이고 Spring 의 비즈니스 로직 표현력은 Java 가 자연스러워서 현재는 분리 유지.

### Q3. AI 가 진짜 컨벤션을 따르나요?

운영해봤는데 100% 는 아니다. 두 가지 메커니즘적 한계가 컸다:

1. **메모리에 있어도 일관 적용이 안 됨** — 컨벤션을 MCP 로 조회해서 컨텍스트에 넣어도 LLM 이 코드 생성 시 그걸 일관되게 따르지 못한다. Instruction following 자체의 한계
2. **컨벤션 로딩의 컨텍스트 비용** — 162개 규칙을 다 컨텍스트에 넣으면 그 자체가 context window 의 큰 부분을 차지. 정작 코드 생성에 쓸 컨텍스트가 줄어듦

이게 conventionHub 의 시스템적 한계인지, LLM 본질의 한계인지 black box 라 단정하기 어렵다. 자세한 운영 결과 + 시도한 우회들: [ADR-0005](docs/adr/0005-operational-retrospective.md).

### Q4. 왜 헥사고날 아키텍처? 단순 Layered 도 되잖아요?

이 시스템 자체에는 헥사고날이 over-engineering 일 수 있다 — CRUD + 조회 위주라 단순 Layered 로도 충분.

다만 conventionHub 가 **"Spring Boot + Hexagonal" 컨벤션의 예시 데이터를 162개 포함** 하므로, 시스템 자체가 그 컨벤션을 따르는 것이 dogfooding 차원에서 의미 있음.

---

## 🏗 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│              AI 코드 생성 도구                                │
│         (Claude Code, Cursor, Copilot 등)                   │
└──────────────────────────┬──────────────────────────────────┘
                           │ MCP Protocol (stdio)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    MCP Server (Python, FastMCP)             │
│   15개 Tool — 컨벤션 조회/캐싱/피드백/convention 추천         │
└──────────────────────────┬──────────────────────────────────┘
                           │ REST API
                           ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Boot API Server                         │
│         Hexagonal (Java 21, QueryDSL, Flyway)               │
└──────────────────────────┬──────────────────────────────────┘
                           ▼
                       MySQL 8.0
               (컨벤션의 Single Source of Truth)
```

배포 옵션:
- **로컬**: `docker-compose up` → Spring API + MySQL + Python MCP
- **AWS**: Spring API → ECS, Python MCP → Lambda URL, MySQL → RDS (Terraform 포함)

---

## 📊 데이터 모델

컨벤션의 계층 구조:

```
TechStack (예: Spring Boot 3.5 + Java 21)
  └── Architecture (예: Hexagonal)
       ├── LayerDependencyRule (Domain → 어디에도 의존 금지)
       ├── ClassTypeCategory → ClassType (AGGREGATE, USE_CASE 등)
       └── Layer (DOMAIN, APPLICATION, ADAPTER_*)
            ├── Convention → CodingRule → RuleExample / ChecklistItem / ZeroTolerancePattern
            └── Module → PackageStructure → ClassTemplate
```

상세 ERD + 테이블 설명: [docs/data-model.md](docs/data-model.md).

---

## 🛠 MCP Tools (15개)

AI 도구가 컨벤션을 조회/피드백하는 인터페이스.

| 분류 | Tool | 용도 |
|---|---|---|
| **워크플로우** | `planning_context` | 개발 계획 (모듈/패키지 구조 조회) |
| | `module_context` | 코드 생성 (템플릿 + 규칙) |
| | `validation_context` | 코드 검증 (Zero-Tolerance + Checklist) |
| **규칙 조회** | `list_rules` | 규칙 인덱스 (경량, 캐싱용) |
| | `get_rule` | 규칙 상세 + 예시 |
| | `get_context` | 빠른 컨텍스트 조회 |
| **계층 정보** | `list_tech_stacks` | 기술 스택 + 아키텍처 + 레이어 목록 |
| | `get_architecture` | 아키텍처 상세 (의존성 규칙) |
| | `get_layer_detail` | 레이어 상세 (모듈, 컨벤션) |
| **설정** | `get_onboarding_contexts` | 프로젝트 온보딩 컨텍스트 |
| | `get_config_files` | 설정 파일 템플릿 (`.claude/` 등) |
| **피드백** | `get_feedback_schema` | 피드백 스키마 + 유효값 |
| | `feedback` | AI 가 새 규칙 제안 (사전 검증 포함) |
| | `approve` | Human 이 피드백 승인 |
| | `suggest_convention` | 코드/appliesTo 기반 convention 자동 추천 (3가지 전략 + confidence) |

각 Tool 의 파라미터/응답 예시 + 사용 흐름: [docs/mcp-tools.md](docs/mcp-tools.md).

---

## 🔄 3-Phase 워크플로우

AI 가 코드를 생성할 때 권장 흐름:

```
1️⃣ PLANNING   planning_context(layers=["DOMAIN", "APPLICATION"])
              → 어떤 컴포넌트를 어디에 만들지 결정

2️⃣ EXECUTION  module_context(module_id=1, class_type_id=1)
              → 템플릿 + 규칙 기반 코드 생성

3️⃣ VALIDATION validation_context(layers=["DOMAIN"])
              → Zero-Tolerance 패턴 검증
```

Claude Code 통합 (`/sc:init`, `/sc:load`) 포함 상세: [docs/workflow.md](docs/workflow.md).

---

## ⚙️ 시작하기

### 로컬 실행

```bash
# 1. Spring API + MySQL
docker-compose up -d

# 2. Spring 단독 (DB 별도)
./gradlew :bootstrap:bootstrap-web-api:bootRun
```

### 규칙 데이터 등록

DB 에 팀의 컨벤션을 등록. 162개 예시 규칙 (Spring + Hexagonal) 는 Seed SQL 로 일괄 등록 가능.

상세 SQL 예시 + 등록 흐름: [docs/setup.md](docs/setup.md).

### Claude Code 에 MCP 연결

`~/.claude/settings.json`:

```json
{
  "mcpServers": {
    "conventionHub": {
      "command": "uv",
      "args": ["--directory", "/path/to/mcp-lambda-server", "run", "conventionhub-mcp"],
      "env": { "API_BASE_URL": "http://localhost:8080" }
    }
  }
}
```

AWS Lambda URL 사용 옵션 + `/sc:init` `/sc:load` 슬래시 커맨드: [docs/setup.md](docs/setup.md).

---

## 🖥 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Spring Boot 3.5.x, Java 21 |
| Architecture | Hexagonal Architecture |
| Database | MySQL 8.0, Flyway |
| Query | QueryDSL 5.x |
| MCP Server | Python 3.12, FastMCP |
| Infra | AWS Lambda, ECS, Terraform |
| AI Integration | Claude Code, Cursor, Serena MCP |
| Code Review | CodeRabbit AI (`.coderabbit.yaml` 통합) |

---

## 📦 예시 데이터 + 컨벤션 문서

이 저장소에는 두 가지 형태의 자산이 같이 들어 있다:

1. **DB 에 등록되는 예시 규칙 데이터 162개** — MCP 가 동적으로 조회하는 컨벤션 (BLOCKER, MAJOR 등 severity 포함)
2. **`docs/coding_convention/` 의 146개 문서** — 사람이 직접 읽도록 정리된 컨벤션 모음. Jekyll GitHub Pages 사이트로 배포 가능 (`docs/index.md` 참고)

두 자산은 동일한 컨벤션 영역을 다루지만 소비자가 다르다 — (1) 은 AI 도구가 MCP 로 조회, (2) 는 사람이 리뷰 시 참고. AI 자동 적용의 한계가 운영 회고에서 확인된 만큼, (2) 의 사람 친화적 문서가 보조 가이드로 함께 유지된다.

### DB 예시 규칙 데이터 (162개)



| Layer | Rules | BLOCKER | 설명 |
|---|---|---|---|
| DOMAIN | 53 | 40 | Aggregate, VO, Event |
| APPLICATION | 37 | 10 | UseCase, Service, Port |
| ADAPTER_OUT | 16 | 10 | Entity, Repository |
| ADAPTER_IN | 46 | 3 | Controller, DTO |
| **합계** | **162** | **58** | - |

예시 데이터일 뿐이며, 실제 사용 시 팀의 컨벤션에 맞게 수정/추가.

---

## 🪞 운영 회고

직접 운영해본 솔직한 평가.

### 잘 동작한 것

- **컨벤션 단일 원천 자체** — DB 등록 + MCP 조회/캐싱 인프라는 안정적이었음
- **MCP 인터페이스** — Claude Code, Cursor 등 여러 AI 도구에서 동일하게 조회 가능
- **suggest_convention** — code prefix / appliesTo / description keyword 3가지 전략으로 convention 추천 자체는 만족스러웠음
- **CodeRabbit 통합** — `.coderabbit.yaml` 로 PR 자동 review 가 의미 있는 보조 역할

### 잘 안 된 것: "AI 가 컨벤션을 100% 준수" 라는 목표

직접 운영해보니 100% 는 아니었다. 일반론적 변동 (LLM 도구 다양성, 모델 업데이트) 도 있지만, 더 본질적인 메커니즘 차원의 벽 세 가지:

**1. 메모리에 있는데 적용을 못 한다**

컨벤션을 MCP 로 조회해서 컨텍스트에 잘 넣어도 LLM 이 코드 생성 시 그걸 일관되게 따르지 못하는 경우가 많았다. 컨벤션이 "참고 자료" 로는 인식되는데 코드 생성의 직접적 제약으로 작동하지 않는 느낌. Instruction following 의 한계인 듯한데 정확히 어떤 메커니즘인지는 잡기 어려움.

**2. 컨벤션 로딩 자체가 컨텍스트 비용**

162개 규칙을 다 컨텍스트에 넣으면 그 자체가 context window 의 상당 부분을 차지한다. 결과적으로 정작 코드 생성에 쓸 컨텍스트 (사용자 요구사항, 기존 코드, 도메인 지식) 가 줄어듦. 컨벤션 적용을 위해 코드 생성 품질을 깎는 트레이드오프가 됨. `list_rules` 의 경량 인덱스 + `get_rule` 로 lazy 조회 방식도 도입했지만 본질적 해결은 아님.

**3. LLM 메커니즘이 black box 라 시스템적 개선이 어렵다**

왜 어떤 컨벤션은 잘 따르고 어떤 건 안 따르는지 패턴이 잡히지 않았다. Severity 별, 규칙 길이 별, 예시 유무 별로 실험해봐도 일관된 결론이 안 나옴. Attention 메커니즘이나 instruction following 의 정확한 동작이 black box 라 "이렇게 바꾸면 더 잘 따른다" 의 시스템적 개선 방향 자체를 잡기 어려웠다.

### 그래서 무엇이 남았나

- **발상 자체는 여전히 옳다** — 기술스택이 같으면 컨벤션을 단일 원천으로 관리하는 게 자연스러움. 이 명제는 LLM 100% 준수와 무관하게 살아있다
- **AI 가 100% 준수하지 않아도** MCP 조회 + suggest_convention + CodeRabbit 자동 review 의 조합으로 **사람 리뷰어의 부담을 줄이는 보조 도구** 로는 충분히 유용했다
- **"100% 준수" 라는 목표 자체가 LLM 의 현재 한계와 맞지 않다** — LLM 의 도구 사용/instruction following 이 더 정형화되거나, 컨벤션의 컨텍스트 효율적 표현 방식이 나와야 가능. 지금은 "단일 원천 + 보조 가이드" 가 현실적

### 이 프로젝트의 위치

거대한 "AI 컨벤션 강제 플랫폼" 이라기보다 **MCP 서버를 직접 만들어 운영해본 학습 흔적** 에 가깝다. Spring API (Java) + Python MCP 서버 + AWS Lambda 배포 + CodeRabbit 통합까지 직접 짜고 굴려보면서 AI 인프라가 어떻게 동작하고 어디서 깨지는지 코드로 학습한 것이 진짜 자산이다.

자세한 결정 흐름: [ADR-0005](docs/adr/0005-operational-retrospective.md).

---

## 📐 ADR

본격 ADR 로 분리할 가치가 있는 건 운영 회고 하나. 나머지 의사결정 (Single Source of Truth / 헥사고날 / Java+Python 분리 / 3-Phase 워크플로우) 은 README 의 결정 배경 Q&A 와 docs/ 에 정리.

| # | 제목 |
|---|---|
| [0005](docs/adr/0005-operational-retrospective.md) | 운영 회고 — "AI 100% 컨벤션 준수" 의 한계와 잔존 가치 |

---

## 📚 참고 자료

### 비교군 (다른 AI 컨벤션 도구)

- [Cursor Rules](https://docs.cursor.com/context/rules-for-ai) — 프로젝트 로컬 `.cursorrules` 파일
- [Claude.md](https://claude.com/code) — Claude Code 의 프로젝트별 컨벤션 파일
- [GitHub Copilot Custom Instructions](https://docs.github.com/en/copilot/customizing-copilot) — 레포 단위 컨벤션
- [Continue Dev Rules](https://continue.dev/docs/customization/rules) — IDE 통합

이들의 공통 특징은 **프로젝트 로컬 파일 기반** — conventionHub 는 **기술스택 단위 중앙 DB** 라는 차이.

### MCP 생태계

- [Model Context Protocol 사양](https://modelcontextprotocol.io/)
- [FastMCP (Python)](https://github.com/jlowin/fastmcp)
- [공식 MCP Servers 모음](https://github.com/modelcontextprotocol/servers)

---

## 라이선스

MIT License — © 2024-2026 ryu-qqq.
