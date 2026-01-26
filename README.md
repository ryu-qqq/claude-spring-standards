# ConventionHub

> 팀의 코딩 컨벤션을 **한 곳에서 관리**하고, AI가 **필요할 때만 조회**하게 만드세요.

**ConventionHub**는 AI 코드 생성 도구(Claude Code, Cursor 등)가 **팀이 정의한 코딩 규칙**을 MCP(Model Context Protocol)로 조회하도록 만드는 **오픈소스 플랫폼**입니다.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## Quick Start (5분)

### 1. Docker로 실행

```bash
git clone https://github.com/ryu-qqq/claude-spring-standards.git
cd claude-spring-standards
docker compose up -d
```

자동으로 실행됩니다:
- MySQL + 예시 규칙 데이터 (164개 규칙, 100개 템플릿)
- Spring Boot API 서버 (port 8080)
- MCP 서버

### 2. Claude Code 설정

`~/.claude/settings.json`에 추가:

```json
{
  "mcpServers": {
    "conventionHub": {
      "command": "docker",
      "args": ["exec", "-i", "conventionhub-mcp", "python", "-m", "src.main"],
      "env": {
        "API_BASE_URL": "http://host.docker.internal:8080"
      }
    }
  }
}
```

### 3. 사용해보기

```
> "Product 도메인의 Aggregate를 만들어줘"

Claude가 자동으로:
1. planning_context() 호출 → 프로젝트 구조 파악
2. module_context() 호출 → 템플릿 + 규칙 조회
3. 조회된 컨벤션 기반으로 코드 생성
```

---

## 왜 필요한가요?

### 문제: 규칙이 분산되어 관리 불가

```
팀원 A: .claude/CLAUDE.md에 규칙 50개
팀원 B: .cursor/rules에 규칙 40개 (다른 버전)
팀원 C: 규칙 업데이트 안 함
새 팀원 D: 어디서 규칙 복사해야 하지?

→ 규칙 변경 시 모든 팀원이 수동으로 각자 파일 수정
→ 버전 불일치, 누락, 혼란
```

### 해결: Single Source of Truth

```
┌─────────────────────────────────────────────────────────┐
│                    ConventionHub DB                      │
│              (팀 규칙의 Single Source of Truth)           │
└─────────────────────────┬───────────────────────────────┘
                          │ MCP 조회
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
   팀원 A              팀원 B             팀원 C
  (Claude)            (Cursor)          (Copilot)

→ 규칙 변경 = DB만 수정 → 팀 전체 즉시 적용
→ 모든 AI 도구가 동일한 규칙 참조
```

### 핵심 가치: Lazy Loading

AI가 **전체 규칙을 미리 로딩하지 않고**, 작업에 필요한 규칙만 그때그때 조회합니다.

```
Static 방식 (.claude/CLAUDE.md)
├── 세션 시작 시 전체 규칙 로딩
├── 사용하지 않는 규칙도 컨텍스트 차지
└── 규칙이 많아질수록 토큰 낭비

Dynamic 방식 (MCP)
├── 필요할 때만 규칙 조회 (Lazy Loading)
├── Aggregate 만들 때 → Aggregate 규칙만
└── Controller 만들 때 → Controller 규칙만
```

---

## 점진적 개선 워크플로우

ConventionHub의 진짜 가치는 **팀의 컨벤션이 점진적으로 개선**되는 것입니다.

```
┌─────────────────────────────────────────────────────────┐
│                    학습 루프                              │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  1. AI가 코드 생성                                        │
│         ↓                                                │
│  2. 코드 리뷰에서 컨벤션 위반 발견                          │
│         ↓                                                │
│  3. 새 규칙을 DB에 등록 (feedback → approve)              │
│         ↓                                                │
│  4. 다음부터 AI가 해당 규칙 조회 → 같은 실수 방지           │
│         ↓                                                │
│  (반복) → 팀 컨벤션이 점점 정교해짐                        │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### AI 피드백 예시

```python
# AI가 코드 리뷰 중 반복되는 패턴 발견 시 규칙 제안
feedback(payload={
    "feedback_type": "NEW_RULE",
    "suggested_rule": {
        "code": "DOM-AGG-030",
        "name": "컬렉션 필드는 불변으로 반환",
        "severity": "MAJOR"
    },
    "reason": "3번의 PR에서 동일한 리뷰 코멘트 발생"
})

# Human이 검토 후 승인
approve(feedback_id=123)
```

---

## 포함된 예시 데이터

Spring Boot + Hexagonal Architecture 기반 **실무 규칙**이 포함되어 있습니다:

| 카테고리 | 수량 | 예시 |
|----------|------|------|
| **코딩 규칙** | 164개 | Lombok 금지, Setter 금지, findAll 금지 |
| **클래스 템플릿** | 100개 | Aggregate, UseCase, Repository 등 |
| **ArchUnit 테스트** | 88개 | 레이어 의존성 검증 |
| **체크리스트** | 404개 | 코드 리뷰 자동화 항목 |
| **Zero-Tolerance 규칙** | 48개 | PR 자동 거부 패턴 |

> 이 데이터는 예시입니다. 팀의 컨벤션에 맞게 자유롭게 수정하세요.

---

## 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│              AI 코드 생성 도구                                │
│         (Claude Code, Cursor, Copilot 등)                   │
└──────────────────────────┬──────────────────────────────────┘
                           │ MCP Protocol
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    MCP Server (Python)                      │
│              15개 Tool (planning_context 등)                 │
└──────────────────────────┬──────────────────────────────────┘
                           │ REST API
                           ▼
┌─────────────────────────────────────────────────────────────┐
│              Spring Boot API Server                         │
│         팀의 코딩 규칙, 템플릿, 예시 저장/관리                  │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
                       MySQL DB
               (팀 규칙의 Single Source of Truth)
```

---

## MCP Tools

AI 도구가 사용하는 핵심 도구들:

### 워크플로우 도구

| Tool | 용도 |
|------|------|
| `planning_context` | 개발 계획 수립 (모듈/패키지 구조 조회) |
| `module_context` | 코드 생성용 컨텍스트 (템플릿 + 규칙) |
| `validation_context` | 코드 검증 (Zero-Tolerance + Checklist) |

### 3-Phase 워크플로우

```
1️⃣ PLANNING    → planning_context(layers=["DOMAIN"])
                  어떤 컴포넌트를 어디에 만들지 결정

2️⃣ EXECUTION   → module_context(module_id=1, class_type_id=1)
                  템플릿 + 규칙 기반 코드 생성

3️⃣ VALIDATION  → validation_context(layers=["DOMAIN"])
                  Zero-Tolerance 패턴 검증
```

### 기타 도구

| Tool | 용도 |
|------|------|
| `list_rules` | 규칙 인덱스 조회 (경량) |
| `get_rule` | 규칙 상세 + 예시 조회 |
| `list_tech_stacks` | 기술 스택 구조 조회 |
| `get_config_files` | 설정 파일 템플릿 조회 |
| `feedback` | AI가 새 규칙 제안 |
| `approve` | Human이 피드백 승인 |

---

## 데이터 모델

팀이 정의하는 규칙의 계층 구조:

```
TechStack (기술 스택)
│   예: "Spring Boot 3.5 + Java 21"
│
└── Architecture (아키텍처)
    │   예: "Hexagonal"
    │
    └── Layer (레이어)
        │   예: "DOMAIN", "APPLICATION"
        │
        ├── Convention (컨벤션 그룹)
        │   └── CodingRule (코딩 규칙)
        │       ├── RuleExample (GOOD/BAD 예시)
        │       ├── ChecklistItem (체크리스트)
        │       └── ZeroTolerancePattern
        │
        └── Module (Gradle 모듈)
            ├── PackageStructure (패키지 구조)
            └── ClassTemplate (클래스 템플릿)
```

---

## 프로젝트 구조

```
claude-spring-standards/
├── docker-compose.yml              # 원클릭 실행
├── mcp-lambda-server/              # MCP Server (Python)
│   ├── src/
│   │   ├── server.py               # FastMCP 서버
│   │   └── tools/                  # 15개 Tool 구현
│   └── Dockerfile
│
├── adapter-in/rest-api/            # REST API 컨트롤러
├── adapter-out/persistence-mysql/  # JPA/QueryDSL + Flyway
├── application/                    # UseCase 서비스
├── domain/                         # 도메인 모델
└── bootstrap/bootstrap-web-api/    # Spring Boot App
```

---

## 개발 환경 설정

### 로컬 개발 (Docker 없이)

```bash
# 1. MySQL 실행 (로컬 또는 Docker)
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=conventionhub -p 3306:3306 mysql:8.0

# 2. Spring Boot 실행
./gradlew :bootstrap:bootstrap-web-api:bootRun

# 3. MCP Server 실행
cd mcp-lambda-server
pip install -r requirements.txt
python -m src.main
```

### 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/conventionhub` | DB URL |
| `API_BASE_URL` | `http://localhost:8080` | MCP→API 연결 |

---

## 팀 규칙 커스터마이징

### 방법 1: SQL 직접 수정

`adapter-out/persistence-mysql/src/main/resources/db/migration/V2__seed_data.sql` 수정

### 방법 2: API로 등록

```bash
curl -X POST http://localhost:8080/api/v1/templates/coding-rules \
  -H "Content-Type: application/json" \
  -d '{
    "conventionId": 1,
    "code": "MY-RULE-001",
    "name": "우리 팀 규칙",
    "severity": "BLOCKER",
    "description": "설명..."
  }'
```

### 방법 3: AI 피드백 (권장)

```python
# AI가 반복되는 리뷰 패턴 발견 시 규칙 제안
feedback(payload={
    "feedback_type": "NEW_RULE",
    "suggested_rule": {
        "code": "MY-RULE-002",
        "name": "발견된 패턴",
        "severity": "MAJOR"
    },
    "reason": "코드 리뷰 중 발견된 패턴"
})
```

---

## API 문서

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/templates/mcp/planning-context` | Planning Context |
| GET | `/api/v1/templates/mcp/module-context` | Module Context |
| GET | `/api/v1/templates/mcp/validation-context` | Validation Context |
| GET | `/api/v1/templates/coding-rules` | 규칙 목록 |
| GET | `/api/v1/templates/coding-rules/{id}` | 규칙 상세 |
| POST | `/api/v1/templates/coding-rules` | 규칙 생성 |

---

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

MIT License - 자유롭게 사용, 수정, 배포하세요.

---

## Credits

- [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) by Anthropic
- [FastMCP](https://github.com/jlowin/fastmcp) for Python MCP implementation
