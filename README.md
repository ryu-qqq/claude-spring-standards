# conventionHub

> AI가 생성하는 코드도 **우리 팀의 컨벤션**을 따라야 합니다.

**conventionHub**는 AI 코드 생성 도구(Claude Code, Cursor 등)가 **팀이 정의한 코딩 규칙**을 동적으로 조회하고 준수하도록 만드는 **플랫폼**입니다.

---

## 왜 필요한가요?

AI 코드 생성 도구는 강력하지만, 팀마다 다른 코딩 컨벤션을 알지 못합니다.

```
❌ AI가 우리 팀 규칙과 다른 코드를 생성
❌ 리뷰어가 매번 컨벤션 위반을 지적
❌ 팀원마다 AI에게 규칙을 다르게 설명
```

conventionHub는 이 문제를 해결합니다:

```
✅ 팀의 코딩 규칙을 DB에 등록 (Single Source of Truth)
✅ AI가 코드 생성 전에 MCP로 규칙을 조회
✅ 모든 AI 도구가 동일한 규칙을 100% 준수
```

---

## 핵심 컨셉

### 동적 규칙 시스템

conventionHub는 **고정된 규칙을 제공하는 것이 아니라**, 팀이 자신만의 규칙을 정의하고 AI가 이를 따르게 하는 **플랫폼**입니다.

```
┌─────────────────────────────────────────────────────────────┐
│  팀이 정의하는 것 (동적)                                      │
├─────────────────────────────────────────────────────────────┤
│  • 기술 스택 (Spring Boot 3.x, Node.js, Python 등)          │
│  • 아키텍처 (Hexagonal, Layered, Clean Architecture 등)    │
│  • 레이어 구조 (Domain, Application, Infrastructure 등)     │
│  • 클래스 타입 (Aggregate, UseCase, Repository 등)          │
│  • 코딩 규칙 (팀의 컨벤션에 맞게 자유롭게)                    │
│  • 코드 템플릿 (팀의 보일러플레이트)                          │
│  • Zero-Tolerance 규칙 (절대 위반 금지 규칙)                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  시스템이 제공하는 것 (고정)                                   │
├─────────────────────────────────────────────────────────────┤
│  • 계층 구조: TechStack → Architecture → Layer → Rule       │
│  • MCP 프로토콜: AI 도구와 규칙 DB 연결                       │
│  • 조회 API: planning_context, module_context 등            │
│  • 피드백 시스템: AI가 새 규칙 제안 → Human 승인              │
└─────────────────────────────────────────────────────────────┘
```

### 예시: 팀별로 다른 규칙

```yaml
# A팀 (Spring + Hexagonal)
- "Lombok 사용 금지"
- "@Transactional 내 외부 API 호출 금지"
- "MockMvc 대신 TestRestTemplate 사용"

# B팀 (Spring + Layered)
- "Lombok @Data 허용, @Setter 금지"
- "Service 레이어에만 @Transactional"
- "MockMvc 사용 권장"

# C팀 (Node.js + Clean Architecture)
- "any 타입 사용 금지"
- "UseCase는 단일 public 메서드만"
- "Repository는 interface로 정의"
```

→ 각 팀이 자신의 규칙을 DB에 등록하면, AI가 해당 규칙을 조회해서 준수합니다.

---

## 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│              AI 코드 생성 도구                                │
│         (Claude Code, Cursor, Copilot 등)                   │
│                                                             │
│   1. "이 프로젝트의 코딩 규칙 알려줘"                          │
│   2. 규칙 수신 → 100% 준수하며 코드 생성                      │
└──────────────────────────┬──────────────────────────────────┘
                           │ MCP Protocol
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    MCP Server (Python)                      │
│                    순수 정보 전달 역할                        │
│                                                             │
│   • 15개 Tool 제공 (planning_context, list_rules 등)        │
│   • 규칙 조회/캐싱/피드백 처리                                │
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

## 데이터 모델

팀이 정의하는 규칙의 계층 구조:

```
🏗️ TechStack (기술 스택)
│   예: "Spring Boot 3.5 + Java 21", "Node.js 20 + TypeScript"
│
└── 📐 Architecture (아키텍처)
    │   예: "Hexagonal", "Layered", "Clean Architecture"
    │
    ├── 📏 LayerDependencyRule (레이어 의존 규칙)
    │   예: "Domain → 어디에도 의존 금지", "Application → Domain만 의존"
    │
    ├── 🏷️ ClassTypeCategory (클래스 타입 카테고리)
    │   │   예: "DOMAIN_TYPES", "APPLICATION_TYPES", "ADAPTER_TYPES"
    │   │
    │   └── 🔖 ClassType (클래스 타입)
    │       예: "AGGREGATE", "VALUE_OBJECT", "USE_CASE", "PORT_IN"
    │
    └── 📦 Layer (레이어)
        │   예: "DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN"
        │
        ├── 📋 Convention (컨벤션 그룹)
        │   │   예: "Aggregate 규칙", "UseCase 규칙", "Repository 규칙"
        │   │
        │   └── 📜 CodingRule (코딩 규칙)
        │       │   예: "Lombok 금지", "Setter 금지", "findAll 금지"
        │       │
        │       ├── 💡 RuleExample (GOOD/BAD 예시)
        │       ├── ✅ ChecklistItem (체크리스트)
        │       └── 🚨 ZeroTolerancePattern (필수 규칙 패턴)
        │
        └── 🗂️ Module (Gradle/NPM 모듈)
            │   예: "domain", "adapter-in/rest-api"
            │
            ├── 📁 PackageStructure (패키지 구조)
            │       → PackagePurpose (패키지 목적)
            │
            └── 🧩 ClassTemplate (클래스 템플릿)
                    → ClassType FK 참조
```

### ERD 요약

```
┌──────────────┐    ┌──────────────────────┐    ┌─────────────┐
│  TechStack   │───▶│    Architecture      │───▶│    Layer    │
└──────────────┘    └──────────────────────┘    └─────────────┘
                              │                        │
                              ▼                        ▼
                    ┌──────────────────┐       ┌─────────────┐
                    │ClassTypeCategory │       │   Module    │
                    └──────────────────┘       └─────────────┘
                              │                        │
                              ▼                        ▼
                    ┌──────────────────┐       ┌───────────────────┐
                    │    ClassType     │◀──FK──│  ClassTemplate    │
                    └──────────────────┘       └───────────────────┘
```

---

## MCP Tools (15개)

AI 도구가 규칙을 조회하는 인터페이스입니다. 각 도구의 상세 설명과 파라미터를 확인하세요.

### 워크플로우 도구

| Tool | 용도 | 주요 파라미터 |
|------|------|--------------|
| `planning_context` | 개발 계획 수립 (모듈/패키지 구조 조회) | `layers`: 레이어 코드 목록 |
| `module_context` | 코드 생성용 컨텍스트 (템플릿 + 규칙) | `module_id`, `class_type_id` |
| `validation_context` | 코드 검증 (Zero-Tolerance + Checklist) | `layers`, `class_types` |

#### `planning_context` 상세

개발 계획 수립 시 사용합니다. 지정된 레이어의 모듈/패키지 구조를 조회합니다.

```python
# 파라미터
planning_context(
    layers=["DOMAIN", "APPLICATION"]  # 조회할 레이어 코드 목록
)

# 응답 예시
{
    "tech_stack": {"id": 1, "name": "Spring Boot 3.5"},
    "architecture": {"id": 1, "name": "Hexagonal"},
    "layers": [
        {
            "code": "DOMAIN",
            "modules": [
                {
                    "id": 1,
                    "name": "domain",
                    "packages": [
                        {"path": "aggregate", "allowed_class_types": ["AGGREGATE"]}
                    ]
                }
            ]
        }
    ]
}
```

#### `module_context` 상세

특정 모듈에서 코드 생성 시 필요한 템플릿과 규칙을 조회합니다.

```python
# 파라미터
module_context(
    module_id=1,              # 모듈 ID (필수)
    class_type_id=1           # 클래스 타입 ID 필터 (선택)
)

# 응답 예시
{
    "module": {"id": 1, "name": "domain", "layer": "DOMAIN"},
    "execution_context": {
        "package_structures": [
            {
                "path": "aggregate",
                "templates": [
                    {"id": 1, "class_type_id": 1, "name": "Aggregate", "body": "..."}
                ]
            }
        ]
    },
    "rule_context": {
        "conventions": [
            {
                "id": 1,
                "coding_rules": [
                    {"code": "AGG-001", "name": "Lombok 금지", "severity": "BLOCKER"}
                ]
            }
        ]
    }
}
```

#### `validation_context` 상세

코드 검증 시 Zero-Tolerance 규칙과 체크리스트를 조회합니다.

```python
# 파라미터
validation_context(
    layers=["DOMAIN"],           # 레이어 코드 목록
    class_types=["AGGREGATE"]    # 클래스 타입 목록 (선택)
)

# 응답 예시
{
    "zero_tolerance_rules": [
        {
            "code": "AGG-001",
            "name": "Lombok 금지",
            "detection_pattern": "@(Data|Getter|Setter)",
            "auto_reject_pr": true
        }
    ],
    "checklist_items": [
        {"description": "ID 필드가 있는가?", "has_automation": true}
    ]
}
```

### 규칙 조회 도구

| Tool | 용도 | 주요 파라미터 |
|------|------|--------------|
| `list_rules` | 규칙 인덱스 조회 (경량, 캐싱용) | `layer_code`, `severity` |
| `get_rule` | 규칙 상세 + 예시 조회 | `rule_code` |
| `get_context` | 빠른 컨텍스트 조회 | `context_type` |

#### `list_rules` 상세

```python
# 파라미터
list_rules(
    layer_code="DOMAIN",     # 레이어 필터 (선택)
    severity="BLOCKER"       # 심각도 필터 (선택)
)

# 응답: 규칙 인덱스 (code, name, severity만 포함)
[
    {"code": "AGG-001", "name": "Lombok 금지", "severity": "BLOCKER"},
    {"code": "AGG-002", "name": "Setter 금지", "severity": "BLOCKER"}
]
```

#### `get_rule` 상세

```python
# 파라미터
get_rule(rule_code="AGG-001")

# 응답: 규칙 상세 (예시, Zero-Tolerance 패턴 포함)
{
    "code": "AGG-001",
    "name": "Lombok 금지",
    "description": "Aggregate에서 Lombok 사용을 금지합니다.",
    "severity": "BLOCKER",
    "examples": [
        {"type": "BAD", "code": "@Data class User {...}", "explanation": "..."},
        {"type": "GOOD", "code": "class User { private final ... }", "explanation": "..."}
    ],
    "zero_tolerance": {
        "detection_pattern": "@(Data|Getter|Setter|Builder)",
        "auto_reject_pr": true
    }
}
```

### 계층 정보 도구

| Tool | 용도 | 주요 파라미터 |
|------|------|--------------|
| `list_tech_stacks` | 기술 스택 + 아키텍처 + 레이어 목록 | - |
| `get_architecture` | 아키텍처 상세 (의존성 규칙 포함) | `architecture_id` |
| `get_layer_detail` | 레이어 상세 (모듈, 컨벤션 포함) | `layer_code` |

#### `list_tech_stacks` 상세

```python
# 파라미터: 없음
list_tech_stacks()

# 응답: 기술 스택 전체 구조
{
    "tech_stacks": [
        {
            "id": 1,
            "name": "Spring Boot 3.5",
            "architectures": [
                {
                    "id": 1,
                    "name": "Hexagonal",
                    "layers": ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN"],
                    "class_type_categories": [
                        {
                            "id": 1,
                            "code": "DOMAIN_TYPES",
                            "class_types": [
                                {"id": 1, "code": "AGGREGATE"},
                                {"id": 2, "code": "VALUE_OBJECT"}
                            ]
                        }
                    ]
                }
            ]
        }
    ]
}
```

### 설정 도구

| Tool | 용도 | 주요 파라미터 |
|------|------|--------------|
| `get_onboarding_contexts` | 프로젝트 온보딩 컨텍스트 | `tool_type` |
| `get_config_files` | 설정 파일 템플릿 (.claude/ 등) | `tool_type` |

#### `get_config_files` 상세

```python
# 파라미터
get_config_files(tool_type="CLAUDE_CODE")

# 응답: 설정 파일 템플릿
[
    {
        "file_path": ".claude/CLAUDE.md",
        "content": "# {{project_name}}\n\n{{architecture_overview}}..."
    },
    {
        "file_path": ".claude/settings.local.json",
        "content": "{ \"mcpServers\": {...} }"
    }
]
```

### 피드백 도구

| Tool | 용도 | 주요 파라미터 |
|------|------|--------------|
| `get_feedback_schema` | 피드백 JSON 스키마 조회 | `feedback_type` |
| `feedback` | AI가 새 규칙 제안 | `payload` (JSON) |
| `approve` | Human이 피드백 승인 | `feedback_id` |

#### `feedback` 상세

```python
# 파라미터 (get_feedback_schema로 스키마 확인 후 사용)
feedback(
    payload={
        "feedback_type": "NEW_RULE",
        "layer_code": "DOMAIN",
        "suggested_rule": {
            "code": "AGG-010",
            "name": "불변 컬렉션 사용",
            "description": "Aggregate 내 컬렉션은 불변으로...",
            "severity": "MAJOR"
        },
        "reason": "코드 리뷰 중 발견된 패턴"
    }
)

# 응답
{
    "feedback_id": "fb-12345",
    "status": "PENDING_REVIEW",
    "message": "피드백이 등록되었습니다. Human 승인 대기 중입니다."
}
```

---

## 3-Phase 워크플로우

AI가 코드를 생성할 때 권장하는 워크플로우:

```
┌─────────────────────────────────────────────────────────────┐
│  1️⃣ PLANNING PHASE                                          │
│     planning_context(layers=["DOMAIN", "APPLICATION"])      │
│     → 어떤 컴포넌트를 어디에 만들지 결정                       │
├─────────────────────────────────────────────────────────────┤
│  2️⃣ EXECUTION PHASE                                         │
│     module_context(module_id=1, class_type_id=1)            │
│     → 템플릿 + 규칙 기반 코드 생성                            │
├─────────────────────────────────────────────────────────────┤
│  3️⃣ VALIDATION PHASE                                        │
│     validation_context(layers=["DOMAIN"])                   │
│     → Zero-Tolerance 패턴 검증                               │
└─────────────────────────────────────────────────────────────┘
```

### 실제 사용 예시

```python
# 1. 먼저 기술 스택/아키텍처/레이어 정보 조회
tech_stacks = list_tech_stacks()
# → layers: ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]
# → class_types: [{id: 1, code: "AGGREGATE"}, {id: 2, code: "VALUE_OBJECT"}, ...]

# 2. 개발 계획 수립
planning = planning_context(layers=["DOMAIN"])
# → module_id=1 (domain), packages: [aggregate, vo, event, ...]

# 3. Aggregate 코드 생성
context = module_context(module_id=1, class_type_id=1)  # class_type_id=1 은 AGGREGATE
# → 템플릿, 규칙 조회

# 4. 코드 검증
validation = validation_context(layers=["DOMAIN"])
# → Zero-Tolerance 규칙 확인
```

---

## Claude Code 연동

### /sc:init - 프로젝트 초기화

새 프로젝트에 conventionHub 설정 파일을 생성:

```bash
/sc:init [--tech-stack <id>] [--architecture <id>]
```

**수행 작업**:
1. `list_tech_stacks()` 호출 → 기술 스택/아키텍처/레이어 정보 조회
2. 기존 `.claude/` 백업 (`.claude.backup.{timestamp}`)
3. `get_config_files()` 호출 → 설정 파일 템플릿 조회
4. 변수 치환 후 `.claude/` 디렉토리 생성

**생성되는 파일**:
```
.claude/
├── CLAUDE.md                    # 프로젝트 가이드 (동적 생성)
├── settings.local.json          # 로컬 설정
├── agents/                      # Agent 정의
├── skills/                      # Skill 정의
└── rules/                       # 규칙 가이드
```

### /sc:load - 규칙 인덱스 로딩

세션 시작 시 규칙을 Serena Memory에 캐싱:

```bash
/sc:load [--refresh]
```

**수행 작업**:
1. Serena 프로젝트 활성화 (`activate_project`)
2. `list_rules()` 호출 → 규칙 인덱스 조회
3. Serena Memory에 캐싱 (`write_memory`)

**Index + Lookup 패턴**:
```
┌─────────────────────────────────────────────────────────────┐
│  Phase 1: /sc:load 시                                       │
│                                                             │
│  list_rules() → 경량 인덱스 (code, name, severity만)         │
│         ↓                                                   │
│  write_memory("spring_rules_index", ...)                    │
├─────────────────────────────────────────────────────────────┤
│  Phase 2: 개발 중 필요 시                                     │
│                                                             │
│  read_memory("spring_rule_{code}") → 캐시 확인              │
│         ↓ (캐시 미스 시)                                     │
│  get_rule(code) → 상세 조회 (description, examples 등)       │
│         ↓                                                   │
│  write_memory("spring_rule_{code}", ...)                    │
└─────────────────────────────────────────────────────────────┘
```

---

## MCP Server 설정

### 방법 1: Lambda URL (권장)

`~/.claude/settings.json`:

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

### 방법 2: 로컬 실행

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

---

## 시작하기

### 1. Spring API 실행

```bash
# 로컬 개발
./gradlew :bootstrap:bootstrap-web-api:bootRun

# 또는 Docker
docker-compose up -d
```

### 2. 규칙 데이터 등록

DB에 팀의 규칙을 등록합니다:

```sql
-- 기술 스택 등록
INSERT INTO tech_stack (name, language_type, framework_type, ...)
VALUES ('my-team-stack', 'JAVA', 'SPRING_BOOT', ...);

-- 아키텍처 등록
INSERT INTO architecture (tech_stack_id, name, pattern_type, ...)
VALUES (1, 'hexagonal', 'HEXAGONAL', ...);

-- 클래스 타입 카테고리 등록
INSERT INTO class_type_category (architecture_id, code, name, order_index, ...)
VALUES (1, 'DOMAIN_TYPES', '도메인 타입', 1, ...);

-- 클래스 타입 등록
INSERT INTO class_type (category_id, code, name, order_index, ...)
VALUES (1, 'AGGREGATE', 'Aggregate', 1, ...);

-- 레이어 등록
INSERT INTO layer (architecture_id, code, name, ...)
VALUES (1, 'DOMAIN', 'Domain Layer', ...);

-- 코딩 규칙 등록
INSERT INTO coding_rule (convention_id, code, name, severity, ...)
VALUES (1, 'NO-LOMBOK', 'Lombok 사용 금지', 'BLOCKER', ...);
```

또는 Seed SQL 파일을 사용하여 일괄 등록할 수 있습니다.

### 3. Claude Code에서 사용

```bash
# 프로젝트 초기화 (최초 1회)
/sc:init

# 세션 시작 시
/sc:load

# 코드 생성 시 (class_type_id는 list_tech_stacks()로 조회)
module_context(module_id=1, class_type_id=1)

# 검증
validation_context(layers=["DOMAIN"])
```

---

## 프로젝트 구조

```
conventionHub/
├── mcp-lambda-server/              # MCP Server (Python)
│   ├── src/
│   │   ├── server.py               # FastMCP 서버
│   │   ├── api_client.py           # API 클라이언트
│   │   └── tools/                  # 15개 Tool 구현
│   └── pyproject.toml
│
├── adapter-in/rest-api/            # REST API 컨트롤러
├── adapter-out/persistence-mysql/  # JPA/QueryDSL
├── application/                    # UseCase 서비스
├── domain/                         # 도메인 모델
├── bootstrap/bootstrap-web-api/    # Spring Boot App
│
└── terraform/                      # 인프라 코드 (AWS)
```

---

## API Endpoints

### CodingRule API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/templates/coding-rules` | 규칙 목록 (커서 기반) |
| GET | `/api/v1/templates/coding-rules/index` | 규칙 인덱스 (경량) |
| GET | `/api/v1/templates/coding-rules/{id}` | 규칙 상세 |
| POST | `/api/v1/templates/coding-rules` | 규칙 생성 |
| PUT | `/api/v1/templates/coding-rules/{id}` | 규칙 수정 |

### ClassType API (NEW)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/templates/class-types` | 클래스 타입 목록 (커서 기반) |
| POST | `/api/v1/templates/class-types` | 클래스 타입 생성 |
| PUT | `/api/v1/templates/class-types/{id}` | 클래스 타입 수정 |

### ClassTypeCategory API (NEW)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/templates/class-type-categories` | 카테고리 목록 (커서 기반) |
| POST | `/api/v1/templates/class-type-categories` | 카테고리 생성 |
| PUT | `/api/v1/templates/class-type-categories/{id}` | 카테고리 수정 |

### MCP API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/templates/mcp/planning-context` | Planning Context |
| GET | `/api/v1/templates/mcp/module-context` | Module Context |
| GET | `/api/v1/templates/mcp/validation-context` | Validation Context |
| GET | `/api/v1/templates/mcp/config-files` | 설정 파일 템플릿 |
| GET | `/api/v1/templates/mcp/feedback-schema` | 피드백 스키마 |

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **Backend** | Spring Boot 3.5.x, Java 21 |
| **Architecture** | Hexagonal Architecture |
| **Database** | MySQL 8.0, Flyway |
| **Query** | QueryDSL 5.x |
| **MCP Server** | Python 3.12, FastMCP |
| **Infra** | AWS Lambda, ECS, Terraform |
| **AI Integration** | Claude Code, Cursor, Serena MCP |

---

## 예시 데이터

이 저장소에는 **Spring Boot + Hexagonal Architecture** 기반의 예시 규칙 데이터가 포함되어 있습니다:

| Layer | Rules | BLOCKER | 설명 |
|-------|-------|---------|------|
| DOMAIN | 53 | 40 | Aggregate, VO, Event 규칙 |
| APPLICATION | 37 | 10 | UseCase, Service, Port 규칙 |
| ADAPTER_OUT | 16 | 10 | Entity, Repository 규칙 |
| ADAPTER_IN | 46 | 3 | Controller, DTO 규칙 |
| **합계** | **162** | **58** | - |

이 데이터는 **예시**이며, 실제 사용 시 팀의 컨벤션에 맞게 수정하거나 새로 등록할 수 있습니다.

---

## 라이선스

MIT License
