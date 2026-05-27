---
layout: default
title: MCP Tools 상세
---

# MCP Tools 상세

conventionHub 의 MCP 서버 (`mcp-lambda-server/`, Python + FastMCP) 가 제공하는 15개 Tool 의 파라미터와 응답 예시.

전체 흐름은 [docs/workflow.md](workflow.md) 의 3-Phase 워크플로우 참고.

---

## 워크플로우 도구

### `planning_context`

개발 계획 수립 시 사용. 지정된 레이어의 모듈/패키지 구조를 조회.

```python
planning_context(
    layers=["DOMAIN", "APPLICATION"]
)
```

응답:

```json
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

### `module_context`

특정 모듈에서 코드 생성 시 필요한 템플릿과 규칙을 조회.

```python
module_context(
    module_id=1,
    class_type_id=1
)
```

응답:

```json
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

### `validation_context`

코드 검증 시 Zero-Tolerance 규칙과 체크리스트를 조회.

```python
validation_context(
    layers=["DOMAIN"],
    class_types=["AGGREGATE"]
)
```

응답:

```json
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

---

## 규칙 조회 도구

### `list_rules`

규칙 인덱스 조회 (경량). 캐싱용.

```python
list_rules(
    layer_code="DOMAIN",
    severity="BLOCKER"
)
```

응답 (code, name, severity 만):

```json
[
    {"code": "AGG-001", "name": "Lombok 금지", "severity": "BLOCKER"},
    {"code": "AGG-002", "name": "Setter 금지", "severity": "BLOCKER"}
]
```

> 컨텍스트 비용을 줄이려는 의도로 추가했지만, 운영해보니 본질적 해결은 아니었다. 상세는 [운영 회고](../README.md#-운영-회고).

### `get_rule`

규칙 상세 + 예시 조회.

```python
get_rule(rule_code="AGG-001")
```

응답:

```json
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

### `get_context`

빠른 컨텍스트 조회. 다른 도구의 응답을 합쳐서 한 번에 받고 싶을 때.

---

## 계층 정보 도구

### `list_tech_stacks`

기술 스택 + 아키텍처 + 레이어 + 클래스 타입 전체 구조 조회. 다른 도구 호출 전 ID 를 알아내기 위해 먼저 호출.

```python
list_tech_stacks()
```

응답:

```json
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

### `get_architecture`

아키텍처 상세 (의존성 규칙 포함).

### `get_layer_detail`

레이어 상세 (모듈, 컨벤션 포함).

---

## 설정 도구

### `get_onboarding_contexts`

프로젝트 온보딩 컨텍스트. `tool_type` (예: `CLAUDE_CODE`, `CURSOR`) 별로 다른 응답.

### `get_config_files`

설정 파일 템플릿. `.claude/CLAUDE.md`, `.claude/settings.local.json` 등을 변수 치환 가능한 형태로 제공.

```python
get_config_files(tool_type="CLAUDE_CODE")
```

응답:

```json
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

---

## 피드백 도구

### `get_feedback_schema`

피드백 스키마 + 유효값 조회. CODING_RULE 조회 시 `conventionId` 와 `appliesTo` 의 유효값을 동적으로 반환.

```python
get_feedback_schema(target_type="CODING_RULE")
```

응답:

```json
{
    "add_schema": {
        "conventionId": {
            "type": "Long", "required": true,
            "valid_values": [
                {"id": 16, "module_name": "domain", "layer_code": "DOMAIN"},
                {"id": 19, "module_name": "rest-api", "layer_code": "ADAPTER_IN"}
            ]
        },
        "appliesTo": {
            "type": "List[String]", "required": true,
            "valid_values": [
                {"code": "AGGREGATE_ROOT", "name": "Aggregate Root"},
                {"code": "REQUEST_DTO", "name": "Request DTO"}
            ]
        }
    }
}
```

### `feedback`

AI 가 새 규칙을 제안. CODING_RULE ADD 시 Python 레벨에서 사전 검증 수행. 잘못된 `conventionId` 나 `appliesTo` 를 입력하면 유효값 힌트와 함께 친절한 에러 반환.

정상 요청:

```python
feedback(
    target_type="CODING_RULE",
    feedback_type="ADD",
    payload={
        "conventionId": 19,
        "code": "API-DTO-010",
        "name": "불변 컬렉션 사용",
        "severity": "MAJOR",
        "appliesTo": ["REQUEST_DTO"]
    }
)
# → {"success": true, "feedback_queue_id": 123, "status": "PENDING"}
```

잘못된 입력 시 사전 검증 실패:

```json
{
    "success": false,
    "error": "conventionId=999는 존재하지 않습니다. appliesTo 값 ['INVALID_TYPE']는 유효한 class_type 코드가 아닙니다.",
    "hints": {
        "available_conventions": [
            {"id": 16, "module_name": "domain", "layer_code": "DOMAIN"},
            {"id": 19, "module_name": "rest-api", "layer_code": "ADAPTER_IN"}
        ],
        "suggested_convention": {"id": 19, "reason": "코드 prefix 'API' 기반 추천"},
        "available_class_types": [
            {"code": "AGGREGATE_ROOT", "name": "Aggregate Root"},
            {"code": "REQUEST_DTO", "name": "Request DTO"}
        ]
    },
    "tip": "get_feedback_schema('CODING_RULE')로 유효한 값을 먼저 확인하세요."
}
```

### `approve`

Human 이 피드백을 승인. `feedback_queue_id` 로 PENDING 항목을 APPROVED 로 전환 후 실제 DB 반영.

### `suggest_convention`

3가지 전략으로 적절한 convention 을 자동 추천:

| 전략 | 입력 | 신뢰도 | 방식 |
|---|---|---|---|
| `code_prefix` | `code` | 0.9 | `API-*` → ADAPTER_IN, `DOM-*` → DOMAIN 매핑 |
| `applies_to_layer` | `applies_to` | 0.85 | class_type → 레이어 역추적 |
| `description_keywords` | `description` | 0.5 ~ 0.8 | 키워드 매칭 |

여러 전략이 동일 convention 을 추천하면 confidence 보너스가 부여됨.

```python
suggest_convention(
    code="API-DTO-SEARCH-002",
    applies_to="REQUEST_DTO,CONTROLLER",
    description="REST API 검색 DTO 규칙"
)
```

응답:

```json
{
    "success": true,
    "suggested_convention": {
        "id": 19,
        "module_name": "rest-api",
        "layer_code": "ADAPTER_IN",
        "confidence": 0.95,
        "reason": "코드 prefix 'API' → ADAPTER_IN 매핑 + appliesTo 'REQUEST_DTO' → ADAPTER_IN 레이어 매핑"
    },
    "existing_similar_rules": [
        {"code": "API-DTO-001", "name": "API DTO 기본 규칙"},
        {"code": "API-DTO-SEARCH-001", "name": "Search ApiRequest DTO 규칙"}
    ]
}
```

---

## 호출 흐름 요약

```
list_tech_stacks()          # 1회: ID 알아내기
    ↓
planning_context(layers)    # 계획
    ↓
module_context(module_id, class_type_id)   # 생성
    ↓
validation_context(layers)  # 검증
```

자세한 흐름은 [docs/workflow.md](workflow.md).

---

## 관련

- [README — 운영 회고](../README.md#-운영-회고) — MCP 호출 흐름이 실제 운영에서 어떻게 부딪혔는지
- [ADR-0005](adr/0005-operational-retrospective.md) — 운영 회고 상세 + 각 한계에 대한 우회 시도
