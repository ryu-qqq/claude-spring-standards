# ConventionHub

> 팀의 코딩 컨벤션을 **한 곳에서 관리**하고, AI가 MCP로 조회하게 만드세요.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## Quick Start

```bash
git clone https://github.com/ryu-qqq/claude-spring-standards.git
cd claude-spring-standards
docker compose up -d
```

`~/.claude/settings.json`에 추가:

```json
{
  "mcpServers": {
    "spring-standards": {
      "command": "docker",
      "args": ["exec", "-i", "conventionhub-mcp", "python", "-m", "src.main"],
      "env": {
        "API_BASE_URL": "http://host.docker.internal:8080"
      }
    }
  }
}
```

---

## 왜 필요한가요?

```
문제: 규칙이 분산됨
├── 팀원 A: .claude/CLAUDE.md (규칙 50개)
├── 팀원 B: .cursor/rules (다른 버전)
└── 규칙 변경 시 → 모든 팀원이 수동 동기화

해결: Single Source of Truth
├── DB에서 규칙 중앙 관리
├── 규칙 변경 = DB 수정 → 팀 전체 즉시 적용
└── 모든 AI 도구가 동일한 규칙 조회
```

---

## 포함된 예시 데이터

| 카테고리 | 수량 |
|----------|------|
| 코딩 규칙 | 164개 |
| 클래스 템플릿 | 100개 |
| ArchUnit 테스트 | 88개 |
| 체크리스트 | 404개 |
| Zero-Tolerance | 48개 |

---

## 아키텍처

```
AI (Claude, Cursor, Copilot)
         │ MCP
         ▼
    MCP Server (Python)
         │ REST API
         ▼
    Spring Boot API
         │
         ▼
      MySQL DB
```

---

## MCP Tools

| Tool | 용도 |
|------|------|
| `planning_context` | 모듈/패키지 구조 조회 |
| `module_context` | 템플릿 + 규칙 조회 |
| `validation_context` | Zero-Tolerance 검증 |
| `get_rule` | 규칙 상세 조회 |
| `feedback` | AI가 새 규칙 제안 |
| `approve` | Human이 피드백 승인 |

---

## API 문서

| 문서 | URL |
|------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI Spec** | http://localhost:8080/v3/api-docs |
| **REST Docs** | `adapter-in/rest-api/docs/v1/` |

### 주요 Endpoints

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/templates/mcp/planning-context` | Planning Context |
| GET | `/api/v1/templates/mcp/module-context` | Module Context |
| GET | `/api/v1/templates/mcp/validation-context` | Validation Context |
| GET | `/api/v1/templates/coding-rules` | 규칙 목록 |
| POST | `/api/v1/templates/coding-rules` | 규칙 생성 |

---

## 로컬 개발

```bash
# MySQL
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=conventionhub -p 3306:3306 mysql:8.0

# Spring Boot
./gradlew :bootstrap:bootstrap-web-api:bootRun

# MCP Server
cd mcp-lambda-server && pip install -r requirements.txt && python -m src.main
```

---

## 프로젝트 구조

```
├── mcp-lambda-server/              # MCP Server (Python)
├── adapter-in/rest-api/            # REST API + Docs
├── adapter-out/persistence-mysql/  # JPA/QueryDSL + Flyway
├── application/                    # UseCase
├── domain/                         # Domain Model
└── bootstrap/bootstrap-web-api/    # Spring Boot App
```

---

## License

MIT License

## Credits

- [Model Context Protocol](https://modelcontextprotocol.io/) by Anthropic
- [FastMCP](https://github.com/jlowin/fastmcp)
