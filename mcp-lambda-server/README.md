# Spring Standards MCP Server

Spring Boot 코딩 컨벤션 및 아키텍처 규칙을 조회하는 MCP(Model Context Protocol) 서버입니다.

**버전**: v1.1.0
**도구 개수**: 12개 (순수 정보 브릿지)

---

## 아키텍처

```
┌─────────────────┐     HTTP/JSON     ┌──────────────────┐
│   MCP Server    │ ◄───────────────► │  Spring REST API │
│   (FastMCP)     │                   │   (Backend)      │
└─────────────────┘                   └──────────────────┘
        │
        │ MCP Protocol
        ▼
┌─────────────────┐
│  Claude Desktop │
│  / Claude Code  │
└─────────────────┘
```

**핵심 원칙**: MCP는 **순수 정보 브릿지**입니다.
- MCP 도구는 정보만 전달 (규칙, 템플릿, 예시)
- 모든 판단과 결정은 LLM이 직접 수행
- 피드백은 FeedbackQueue를 통해 안전하게 처리

---

## MCP Tools (12개)

### 컨텍스트 조회 (3개)

| Tool | 설명 | 파라미터 |
|------|------|----------|
| `tool_get_context` | 레이어/클래스 타입별 컨벤션 컨텍스트 조회 | `layer`, `class_type` (선택) |
| `tool_search` | 통합 검색 (rules, templates, all) | `query`, `scope` |
| `tool_get_rule` | 규칙 상세 + 예시 조회 | `code` (예: DOM-001) |

### 계층 정보 (3개)

| Tool | 설명 | 파라미터 |
|------|------|----------|
| `tool_list_tech_stacks` | 기술 스택 목록 조회 | 없음 |
| `tool_get_architecture` | 아키텍처 상세 조회 | `architecture_id` (선택) |
| `tool_get_layer_detail` | 레이어 상세 조회 | `layer_id` 또는 `layer_code` |

### 컨벤션 조회 (2개)

| Tool | 설명 | 파라미터 |
|------|------|----------|
| `tool_list_conventions` | 컨벤션 목록 조회 | `layer` (선택) |
| `tool_get_convention_tree` | 컨벤션 전체 트리 (규칙+템플릿+예시) | `convention_id` 또는 `layer` |

### 피드백 시스템 (4개) ⭐ NEW

| Tool | 설명 | 파라미터 |
|------|------|----------|
| `tool_feedback` | 피드백 제출 → FeedbackQueue 저장 | `target_type`, `feedback_type`, `payload`, `target_id` |
| `tool_sync` | PENDING 피드백 일괄 검토 지원 | `auto_merge_safe`, `limit` |
| `tool_approve` | 피드백 승인/거절 처리 | `feedback_id`, `action`, `reviewer`, `review_notes` |
| `tool_feedback_list` | 피드백 목록 조회 | `status`, `risk_level`, `target_type`, `limit` |

---

## FeedbackQueue 시스템

### 왜 FeedbackQueue인가?

기존 방식의 문제점:
```
LLM → 직접 RuleExample INSERT → 데이터 오염 위험!
```

FeedbackQueue 방식의 장점:
```
LLM → FeedbackQueue(PENDING) → 검토 → 승인 → 실제 반영
```

### 2단계 승인 프로세스

```
┌────────────────────────────────────────────────────────────────────────┐
│                         FeedbackQueue 워크플로우                        │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  feedback() ─────► PENDING                                             │
│                       │                                                │
│                       ▼                                                │
│           ┌─── LLM 1차 검토 ───┐                                       │
│           │                    │                                       │
│           ▼                    ▼                                       │
│     LLM_APPROVED         LLM_REJECTED                                  │
│           │                    │                                       │
│     ┌─────┴─────┐              └─────► (종료)                          │
│     │           │                                                      │
│   SAFE       MEDIUM                                                    │
│     │           │                                                      │
│     │           ▼                                                      │
│     │    ┌─── Human 2차 검토 ───┐                                      │
│     │    │                      │                                      │
│     │    ▼                      ▼                                      │
│     │  HUMAN_APPROVED     HUMAN_REJECTED                               │
│     │    │                      │                                      │
│     └────┴──────► MERGED        └─────► (종료)                         │
│                     │                                                  │
│                     ▼                                                  │
│              실제 데이터 반영                                           │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### 리스크 레벨

| Level | 설명 | 승인 요건 | 예시 |
|-------|------|-----------|------|
| **SAFE** | 저위험 작업 | LLM 승인 → 자동 머지 | RULE_EXAMPLE 추가 |
| **MEDIUM** | 중위험 작업 | LLM 승인 → Human 승인 → 머지 | CODING_RULE 수정/삭제 |

### 대상 타입 (Target Type)

| Type | 설명 | 리스크 레벨 |
|------|------|-------------|
| `RULE_EXAMPLE` | 규칙 예시 (GOOD/BAD) | SAFE |
| `CLASS_TEMPLATE` | 클래스 템플릿 | ADD: SAFE, MODIFY/DELETE: MEDIUM |
| `CODING_RULE` | 코딩 규칙 | MEDIUM |
| `CHECKLIST_ITEM` | 체크리스트 항목 | MEDIUM |
| `ARCH_UNIT_TEST` | ArchUnit 테스트 | MEDIUM |

### 피드백 유형 (Feedback Type)

| Type | 설명 | target_id 필요 | 별칭 |
|------|------|----------------|------|
| `ADD` | 새로 추가 | ❌ (자동 0) | CREATE |
| `MODIFY` | 기존 수정 | ✅ | UPDATE |
| `DELETE` | 삭제 요청 | ✅ | - |

---

## 사용 예시

### 컨텍스트 조회

```python
# DOMAIN 레이어 컨벤션 조회
tool_get_context(layer="DOMAIN")

# Aggregate 클래스 타입 컨텍스트
tool_get_context(layer="DOMAIN", class_type="AGGREGATE")

# Lombok 관련 규칙 검색
tool_search(query="Lombok", scope="rules")

# AGG-001 규칙 상세
tool_get_rule(code="AGG-001")
```

### 계층 정보 조회

```python
# 전체 기술 스택
tool_list_tech_stacks()

# 아키텍처 정보
tool_get_architecture(architecture_id=1)

# DOMAIN 레이어 상세
tool_get_layer_detail(layer_code="DOMAIN")
```

### 컨벤션 조회

```python
# 전체 컨벤션 목록
tool_list_conventions()

# APPLICATION 레이어 컨벤션
tool_list_conventions(layer="APPLICATION")

# 컨벤션 전체 트리 (규칙+템플릿+예시 한번에)
tool_get_convention_tree(layer="DOMAIN")
```

### 피드백 시스템

```python
# 1. 피드백 제출 (RULE_EXAMPLE 추가)
tool_feedback(
    target_type="RULE_EXAMPLE",
    feedback_type="ADD",  # 또는 "CREATE" (하위 호환)
    payload={
        "codingRuleId": 123,
        "exampleType": "GOOD",
        "code": "// Good example code",
        "language": "java",
        "explanation": "이 예시는 Law of Demeter를 준수합니다."
    }
)
# → PENDING 상태로 FeedbackQueue에 저장
# → feedback_queue_id 반환

# 2. PENDING 피드백 일괄 검토
tool_sync(auto_merge_safe=True, limit=20)
# → pending_feedbacks: 검토할 피드백 목록 + 컨텍스트
# → awaiting_human: Human 승인 대기 목록
# → auto_merged: SAFE 레벨 자동 머지 건수

# 3. LLM 승인/거절
tool_approve(
    feedback_id=1,
    action="approve",  # 또는 "reject"
    reviewer="llm",
    review_notes="예시가 규칙을 잘 설명하고 있음"
)
# → SAFE 레벨이면 자동 머지
# → MEDIUM 레벨이면 LLM_APPROVED → Human 승인 대기

# 4. Human 승인 (MEDIUM 레벨만)
tool_approve(
    feedback_id=3,
    action="approve",
    reviewer="human"
)
# → MERGED → 실제 데이터 반영

# 5. 피드백 목록 조회
tool_feedback_list(status="PENDING", limit=20)
tool_feedback_list(risk_level="MEDIUM", status="LLM_APPROVED")
```

---

## 설치 및 실행

### uv 사용 (권장)

```bash
cd mcp-lambda-server
uv run spring-standards-mcp
```

### 직접 실행

```bash
python -m src.server
```

### 전역 설치

```bash
cd mcp-lambda-server && uv pip install -e .
spring-standards-mcp
```

---

## Claude Code 설정

`~/.claude/settings.json`:

```json
{
  "mcpServers": {
    "spring-standards": {
      "command": "uv",
      "args": ["--directory", "/path/to/mcp-lambda-server", "run", "spring-standards-mcp"],
      "env": {
        "API_BASE_URL": "https://api.set-of.com"
      }
    }
  }
}
```

또는 전역 설치 후:

```json
{
  "mcpServers": {
    "spring-standards": {
      "command": "spring-standards-mcp",
      "env": {
        "API_BASE_URL": "https://api.set-of.com"
      }
    }
  }
}
```

---

## 환경 설정

`.env` 파일:

```env
# Spring REST API Configuration
API_BASE_URL=https://api.set-of.com
API_TIMEOUT=30.0

# MCP Server Configuration
MCP_SERVER_NAME=spring-standards
MCP_SERVER_VERSION=1.1.0
```

---

## Spring REST API 엔드포인트

### 기존 조회 API

| Endpoint | 설명 |
|----------|------|
| `GET /api/v1/mcp/conventions` | 컨벤션 목록 조회 |
| `GET /api/v1/mcp/coding-rules` | 코딩 규칙 목록 조회 |
| `GET /api/v1/mcp/coding-rules/{id}` | 코딩 규칙 상세 조회 |
| `GET /api/v1/mcp/class-templates` | 클래스 템플릿 목록 |
| `GET /api/v1/mcp/convention-tree/{id}` | 컨벤션 트리 조회 |
| `GET /api/v1/mcp/tech-stacks` | 기술 스택 목록 |
| `GET /api/v1/mcp/architectures` | 아키텍처 목록 |
| `GET /api/v1/mcp/layers` | 레이어 목록 |

### FeedbackQueue API ⭐ NEW

| Endpoint | 설명 |
|----------|------|
| `POST /api/v1/templates/feedback-queue` | 피드백 생성 |
| `GET /api/v1/templates/feedback-queue` | 피드백 목록 조회 |
| `GET /api/v1/templates/feedback-queue/{id}` | 피드백 상세 조회 |
| `POST /api/v1/templates/feedback-queue/{id}/llm-approve` | LLM 승인 |
| `POST /api/v1/templates/feedback-queue/{id}/llm-reject` | LLM 거절 |
| `POST /api/v1/templates/feedback-queue/{id}/human-approve` | Human 승인 |
| `POST /api/v1/templates/feedback-queue/{id}/human-reject` | Human 거절 |
| `POST /api/v1/templates/feedback-queue/{id}/merge` | 실제 데이터 반영 |

---

## 프로젝트 구조

```
mcp-lambda-server/
├── src/
│   ├── __init__.py
│   ├── config.py              # API/서버 설정
│   ├── models.py              # Pydantic 응답 모델 + FeedbackQueue 모델
│   ├── api_client.py          # Spring REST API 클라이언트 + FeedbackQueue API
│   ├── server.py              # FastMCP 서버 (12 Tools)
│   └── tools/
│       ├── __init__.py
│       ├── get_context.py     # 컨텍스트 조회
│       ├── search.py          # 통합 검색
│       ├── get_rule.py        # 규칙 상세
│       ├── list_tech_stacks.py
│       ├── get_architecture.py
│       ├── get_layer_detail.py
│       ├── list_conventions.py
│       ├── get_convention_tree.py
│       ├── feedback.py        # 피드백 제출 → FeedbackQueue
│       ├── sync.py            # 일괄 검토 지원 ⭐ NEW
│       ├── approve.py         # 승인/거절 처리 ⭐ NEW
│       └── feedback_list.py   # 피드백 목록 조회 ⭐ NEW
├── pyproject.toml
├── requirements.txt
├── .env.example
└── README.md
```

---

## 의존성

- `fastmcp>=2.0.0`: MCP 서버 프레임워크
- `httpx>=0.27.0`: HTTP 클라이언트
- `pydantic>=2.0.0`: 데이터 검증
- `python-dotenv>=1.0.0`: 환경변수 관리

---

## 버전 히스토리

### v1.1.0 (Current)
- FeedbackQueue 시스템 도입
- 4개 피드백 도구 추가 (feedback, sync, approve, feedback_list)
- 2단계 승인 프로세스 (LLM → Human)
- 리스크 레벨 기반 자동/수동 머지

### v1.0.0
- 8개 조회 도구 제공
- 순수 정보 브릿지 아키텍처

---

## 지원 레이어

- `DOMAIN`: Aggregate, VO, Event, Exception
- `APPLICATION`: UseCase, Service, DTO
- `PERSISTENCE`: Entity, Repository, Adapter
- `REST_API`: Controller, Request/Response DTO
- `SCHEDULER`: Thin Scheduler 패턴
