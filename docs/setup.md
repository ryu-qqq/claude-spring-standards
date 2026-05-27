---
layout: default
title: 시작하기
---

# 시작하기

conventionHub 로컬 실행, 규칙 데이터 등록, Claude Code 연동 흐름.

---

## 로컬 실행

### 방법 1: Docker Compose (권장)

```bash
docker-compose up -d
```

Spring API + MySQL + (옵션) Python MCP 서버 한 번에 실행.

### 방법 2: Spring 단독

```bash
./gradlew :bootstrap:bootstrap-web-api:bootRun
```

별도로 MySQL 실행 필요. `application.yml` 의 DB 설정 확인.

---

## 규칙 데이터 등록

DB 에 팀의 컨벤션을 등록.

### Seed SQL (권장)

이 저장소에는 Spring Boot + Hexagonal 기준 162개 예시 규칙이 Seed SQL 로 포함되어 있다. Flyway 가 부팅 시 자동 적용.

### 수동 등록 예시

```sql
-- 기술 스택
INSERT INTO tech_stack (name, language_type, framework_type, ...)
VALUES ('my-team-stack', 'JAVA', 'SPRING_BOOT', ...);

-- 아키텍처
INSERT INTO architecture (tech_stack_id, name, pattern_type, ...)
VALUES (1, 'hexagonal', 'HEXAGONAL', ...);

-- 클래스 타입 카테고리
INSERT INTO class_type_category (architecture_id, code, name, order_index, ...)
VALUES (1, 'DOMAIN_TYPES', '도메인 타입', 1, ...);

-- 클래스 타입
INSERT INTO class_type (category_id, code, name, order_index, ...)
VALUES (1, 'AGGREGATE', 'Aggregate', 1, ...);

-- 레이어
INSERT INTO layer (architecture_id, code, name, ...)
VALUES (1, 'DOMAIN', 'Domain Layer', ...);

-- 코딩 규칙
INSERT INTO coding_rule (convention_id, code, name, severity, ...)
VALUES (1, 'NO-LOMBOK', 'Lombok 사용 금지', 'BLOCKER', ...);
```

또는 REST API 로 등록:

```bash
curl -X POST http://localhost:8080/api/v1/templates/coding-rules \
  -H 'Content-Type: application/json' \
  -d '{ "conventionId": 1, "code": "NO-LOMBOK", "severity": "BLOCKER", ... }'
```

---

## Claude Code 에 MCP 연결

### 방법 1: 로컬 MCP 서버

`~/.claude/settings.json`:

```json
{
  "mcpServers": {
    "conventionHub": {
      "command": "uv",
      "args": ["--directory", "/path/to/mcp-lambda-server", "run", "conventionhub-mcp"],
      "env": {
        "API_BASE_URL": "http://localhost:8080"
      }
    }
  }
}
```

### 방법 2: AWS Lambda URL (배포 후)

```json
{
  "mcpServers": {
    "conventionHub": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "mcp-remote", "https://your-lambda-url.lambda-url.region.on.aws/"]
    }
  }
}
```

Lambda 배포는 `mcp-lambda-server/` 의 Terraform 모듈 참고.

---

## Claude Code 슬래시 커맨드

### `/sc:init` — 프로젝트 초기화

새 프로젝트에 conventionHub 설정 파일을 생성.

```bash
/sc:init [--tech-stack <id>] [--architecture <id>]
```

수행 흐름:

1. `list_tech_stacks()` 호출 → 기술 스택/아키텍처/레이어 정보 조회
2. 기존 `.claude/` 백업 (`.claude.backup.{timestamp}`)
3. `get_config_files()` 호출 → 설정 파일 템플릿 조회
4. 변수 치환 후 `.claude/` 디렉토리 생성

생성되는 파일:

```
.claude/
├── CLAUDE.md                    # 프로젝트 가이드 (동적 생성)
├── settings.local.json          # 로컬 설정
├── agents/                      # Agent 정의
├── skills/                      # Skill 정의
└── rules/                       # 규칙 가이드
```

### `/sc:load` — 규칙 인덱스 로딩

세션 시작 시 규칙을 Serena Memory 에 캐싱.

```bash
/sc:load [--refresh]
```

수행 흐름:

1. Serena 프로젝트 활성화 (`activate_project`)
2. `list_rules()` 호출 → 규칙 인덱스 조회
3. Serena Memory 에 캐싱 (`write_memory`)

> 컨텍스트 비용 절감을 위해 도입한 Index + Lookup 패턴. 운영해보니 본질적 해결은 아니었다 — [운영 회고](../README.md#-운영-회고) 참고.

상세 흐름은 [workflow.md](workflow.md).

---

## 관련

- [데이터 모델](data-model.md)
- [REST API Endpoints](api.md)
- [3-Phase 워크플로우](workflow.md)
