# MCP Tool 통합 구현 가이드

## 개요

Spring Standards MCP 서버의 Tool을 41개에서 7개로 통합하여 토큰 효율성을 극대화합니다.

---

## 1. 아키텍처

```
┌─────────────────────────────────────────────────────────────────┐
│                        MCP Server (FastMCP)                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │ get_context │  │   search    │  │  get_rule   │              │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘              │
│         │                │                │                      │
│  ┌──────┴──────┐  ┌──────┴──────┐  ┌──────┴──────┐              │
│  │  validate   │  │  generate   │  │   detect    │              │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘              │
│         │                │                │                      │
│  ┌──────┴──────┐         │                │                      │
│  │  feedback   │         │                │                      │
│  └─────────────┘         │                │                      │
│                          │                │                      │
├──────────────────────────┼────────────────┼──────────────────────┤
│                     Service Layer                                │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │  ContextService  │  ValidationService  │  GeneratorService │  │
│  └─────────────────────────────────────────────────────────┘    │
│                          │                                       │
├──────────────────────────┼───────────────────────────────────────┤
│                     API Client Layer                             │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              ConventionApiClient (기존 유지)              │    │
│  └─────────────────────────────────────────────────────────┘    │
│                          │                                       │
└──────────────────────────┼───────────────────────────────────────┘
                           ▼
                   Spring REST API
```

---

## 2. 파일 구조

```
mcp-lambda-server/src/
├── __init__.py
├── server.py              # MCP 서버 진입점 (수정)
├── api_client.py          # API 클라이언트 (유지)
├── models.py              # 데이터 모델 (유지)
├── config.py              # 설정 (유지)
│
├── tools/                 # 🆕 새로운 Tool 모듈
│   ├── __init__.py
│   ├── get_context.py     # get_context tool
│   ├── search.py          # search tool
│   ├── get_rule.py        # get_rule tool
│   ├── validate.py        # validate tool
│   ├── generate.py        # generate tool
│   ├── detect.py          # detect tool
│   └── feedback.py        # feedback tool
│
├── services/              # 🆕 비즈니스 로직 분리
│   ├── __init__.py
│   ├── context_service.py
│   ├── validation_service.py
│   └── generator_service.py
│
├── context.py             # 기존 context 로직 (유지, 일부 이동)
├── template.py            # 기존 template 로직 (유지)
└── validation.py          # 기존 validation 로직 (유지)
```

---

## 3. Tool 상세 명세

### 3.1 get_context

```python
@mcp.tool()
def get_context(layer: str = None, class_type: str = None) -> dict:
    """컨벤션 컨텍스트 조회. layer: DOMAIN|APPLICATION|PERSISTENCE|REST_API, class_type: AGGREGATE|USE_CASE|ENTITY|CONTROLLER 등"""
```

**입력**:
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| layer | str | N | DOMAIN, APPLICATION, PERSISTENCE, REST_API |
| class_type | str | N | AGGREGATE, VALUE_OBJECT, USE_CASE, ENTITY 등 |

**출력**:
```json
{
  "layer": "DOMAIN",
  "convention_id": 1,
  "zero_tolerance_rules": [
    {"code": "DOM-001", "name": "Lombok 금지", "severity": "BLOCKER"}
  ],
  "coding_rules": [
    {"code": "DOM-002", "name": "Tell Don't Ask", "severity": "CRITICAL"}
  ],
  "class_templates": [
    {"class_type": "AGGREGATE", "naming_pattern": "{Name}"}
  ],
  "layer_dependencies": [
    {"from": "DOMAIN", "to": "APPLICATION", "allowed": false}
  ],
  "anti_patterns": [
    {"code": "AP-001", "name": "Premature Abstraction"}
  ]
}
```

**구현 로직**:
```python
def get_context(layer: str = None, class_type: str = None) -> dict:
    client = get_api_client()

    # 1. layer → convention_id 변환
    convention = client.get_convention_by_layer(layer) if layer else None
    convention_id = convention.id if convention else None

    # 2. 데이터 수집
    result = {
        "layer": layer,
        "convention_id": convention_id,
    }

    # Zero-Tolerance 규칙
    zt_rules = client.get_zero_tolerance_rules(layer)
    result["zero_tolerance_rules"] = [
        {"code": r.code, "name": r.name, "severity": r.severity}
        for r in zt_rules
    ]

    # 코딩 규칙 (convention_id 기반)
    if convention_id:
        tree = client.get_convention_tree(convention_id)
        if tree:
            result["coding_rules"] = [
                {"code": r.code, "name": r.title, "severity": r.severity}
                for r in tree.coding_rules
            ]
            result["class_templates"] = [
                {"class_type": t.type, "name": t.name}
                for t in tree.class_templates
            ]

    # 레이어 의존성
    deps = client.get_layer_dependencies()
    result["layer_dependencies"] = [
        {"from": d.source_layer, "to": d.target_layer, "allowed": d.allowed}
        for d in deps
        if layer is None or d.source_layer == layer
    ]

    # 안티패턴 (layer 필터)
    result["anti_patterns"] = _get_anti_patterns_for_layer(layer)

    return result
```

---

### 3.2 search

```python
@mcp.tool()
def search(query: str, scope: str = "all") -> dict:
    """통합 검색. scope: rules|templates|all"""
```

**입력**:
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| query | str | Y | 검색 키워드 |
| scope | str | N | rules, templates, all (기본값: all) |

**출력**:
```json
{
  "query": "Lombok",
  "total_count": 3,
  "rules": [
    {"code": "DOM-001", "name": "Lombok 금지", "matched_field": "name"}
  ],
  "templates": [],
  "modules": []
}
```

---

### 3.3 get_rule

```python
@mcp.tool()
def get_rule(code: str) -> dict:
    """규칙 상세 + 예시 조회. code: DOM-001, APP-002 등"""
```

**입력**:
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| code | str | Y | 규칙 코드 (예: DOM-001) |

**출력**:
```json
{
  "code": "DOM-001",
  "name": "Lombok 금지",
  "severity": "BLOCKER",
  "category": "ANNOTATION",
  "description": "Domain 레이어에서 Lombok 사용 금지",
  "rationale": "도메인 객체의 명시적 설계를 위해...",
  "examples": {
    "good": [
      {"code": "public class Order { ... }", "explanation": "명시적 생성자"}
    ],
    "bad": [
      {"code": "@Data public class Order { ... }", "explanation": "Lombok 사용"}
    ]
  }
}
```

---

### 3.4 validate

```python
@mcp.tool()
def validate(code: str, layer: str = None, class_type: str = None) -> dict:
    """코드 검증. 위반 규칙 + 수정 제안 반환"""
```

**입력**:
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| code | str | Y | 검증할 Java 코드 |
| layer | str | N | 레이어 힌트 |
| class_type | str | N | 클래스 타입 힌트 |

**출력**:
```json
{
  "valid": false,
  "layer": "DOMAIN",
  "class_type": "AGGREGATE",
  "violations": [
    {
      "rule_code": "DOM-001",
      "severity": "BLOCKER",
      "message": "Lombok @Data 사용 금지",
      "line": 5,
      "suggestion": "@Data 제거 후 명시적 생성자/메서드 구현"
    }
  ],
  "warnings": [],
  "passed_rules": ["DOM-002", "DOM-003"]
}
```

---

### 3.5 generate

```python
@mcp.tool()
def generate(
    class_type: str,
    name: str,
    package_name: str = None,
    fields: list = None,
    methods: list = None
) -> dict:
    """스켈레톤 코드 생성. class_type: AGGREGATE|VALUE_OBJECT|USE_CASE|ENTITY|CONTROLLER 등"""
```

**입력**:
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| class_type | str | Y | AGGREGATE, USE_CASE, ENTITY 등 |
| name | str | Y | 클래스명 (예: Order, Payment) |
| package_name | str | N | 패키지명 |
| fields | list | N | 필드 정의 `[{"name": "id", "type": "Long"}]` |
| methods | list | N | 메서드 정의 `[{"name": "process", "return_type": "void"}]` |

**지원 class_type**:
```
DOMAIN:      AGGREGATE, VALUE_OBJECT, DOMAIN_EVENT, DOMAIN_EXCEPTION
APPLICATION: USE_CASE, PORT_IN, PORT_OUT, COMMAND_SERVICE, QUERY_SERVICE
PERSISTENCE: ENTITY, REPOSITORY, ADAPTER
REST_API:    CONTROLLER, REQUEST_DTO, RESPONSE_DTO, MAPPER
```

**출력**:
```json
{
  "class_type": "AGGREGATE",
  "name": "Order",
  "layer": "DOMAIN",
  "code": "package com.example.domain.order;\n\npublic class Order {\n    ...\n}",
  "applied_rules": ["DOM-001", "DOM-002"],
  "file_path_suggestion": "domain/src/main/java/com/example/domain/order/Order.java"
}
```

---

### 3.6 detect

```python
@mcp.tool()
def detect(file_path: str = None, code_snippet: str = None) -> dict:
    """파일/코드에서 layer, class_type 자동 감지"""
```

**입력**:
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| file_path | str | N | 파일 경로 |
| code_snippet | str | N | 코드 스니펫 |

**출력**:
```json
{
  "layer": "DOMAIN",
  "class_type": "AGGREGATE",
  "confidence": 0.95,
  "reasoning": "경로 패턴 매칭: domain/, 클래스명 suffix 없음, 어노테이션 없음",
  "suggested_rules": ["DOM-001", "DOM-002", "DOM-003"]
}
```

---

### 3.7 feedback

```python
@mcp.tool()
def feedback(
    rule_code: str,
    feedback_type: str,
    code_snippet: str = None,
    explanation: str = None
) -> dict:
    """피드백 제출. 규칙 위반 사례 또는 새 예시 수집"""
```

**입력**:
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| rule_code | str | Y | 규칙 코드 |
| feedback_type | str | Y | violation, good_example, bad_example |
| code_snippet | str | N | 코드 예시 |
| explanation | str | N | 설명 |

**출력**:
```json
{
  "success": true,
  "feedback_id": 123,
  "message": "피드백이 등록되었습니다."
}
```

---

## 4. 구현 순서

### Phase 1: 기반 구조 (Day 1)
1. `src/tools/` 디렉토리 생성
2. `src/services/` 디렉토리 생성
3. 각 Tool 파일 스켈레톤 생성

### Phase 2: Core Tools (Day 1-2)
1. `get_context` 구현 (가장 중요)
2. `search` 구현
3. `get_rule` 구현

### Phase 3: Generation & Validation (Day 2-3)
1. `detect` 구현
2. `generate` 구현 (기존 로직 통합)
3. `validate` 구현 (기존 로직 통합)

### Phase 4: Feedback & Integration (Day 3)
1. `feedback` 구현
2. `server.py` 통합
3. 기존 Tool deprecation 처리

### Phase 5: 테스트 & 문서화 (Day 4)
1. 단위 테스트 작성
2. 통합 테스트
3. README 업데이트

---

## 5. 마이그레이션 전략

### 5.1 병행 운영 기간
```python
# server.py
from .tools import (
    get_context,
    search,
    get_rule,
    validate,
    generate,
    detect,
    feedback,
)

# 새 Tool 등록
mcp.tool()(get_context)
mcp.tool()(search)
# ...

# 기존 Tool은 deprecated 표시 후 유지
@mcp.tool()
def get_coding_rules_by_layer(layer: str) -> dict:
    """[DEPRECATED] get_context() 사용 권장"""
    return get_context(layer=layer)
```

### 5.2 완전 전환
- 새 Tool 안정화 확인 후 기존 Tool 제거
- 버전 번호 업데이트 (1.x → 2.0)

---

## 6. 성능 최적화

### 6.1 캐싱 전략
```python
from functools import lru_cache

@lru_cache(maxsize=32)
def _get_convention_id(layer: str) -> int:
    """레이어 → convention_id 캐싱"""
    convention = get_api_client().get_convention_by_layer(layer)
    return convention.id if convention else None
```

### 6.2 Lazy Loading
```python
def get_context(layer: str = None, class_type: str = None) -> dict:
    result = {"layer": layer}

    # 필요한 데이터만 로드
    if layer:
        result["zero_tolerance_rules"] = _load_zt_rules(layer)
        result["coding_rules"] = _load_coding_rules(layer)

    if class_type:
        result["class_templates"] = _load_templates(class_type)

    return result
```

---

## 7. 테스트 전략

### 7.1 단위 테스트
```python
# tests/tools/test_get_context.py
def test_get_context_with_layer():
    result = get_context(layer="DOMAIN")
    assert result["layer"] == "DOMAIN"
    assert "zero_tolerance_rules" in result
    assert len(result["zero_tolerance_rules"]) > 0

def test_get_context_with_class_type():
    result = get_context(class_type="AGGREGATE")
    assert "class_templates" in result
```

### 7.2 통합 테스트
```python
# tests/test_integration.py
def test_full_workflow():
    # 1. 컨텍스트 감지
    detected = detect(file_path="domain/order/Order.java")
    assert detected["layer"] == "DOMAIN"

    # 2. 컨텍스트 조회
    context = get_context(layer=detected["layer"])
    assert len(context["zero_tolerance_rules"]) > 0

    # 3. 코드 생성
    generated = generate(class_type="AGGREGATE", name="Order")
    assert "public class Order" in generated["code"]

    # 4. 검증
    validation = validate(code=generated["code"], layer="DOMAIN")
    assert validation["valid"] == True
```

---

## 8. 체크리스트

### 구현 완료 기준
- [ ] 모든 7개 Tool 구현 완료
- [ ] 기존 41개 Tool 기능 커버리지 100%
- [ ] 단위 테스트 통과
- [ ] 통합 테스트 통과
- [ ] Description 한 줄로 간소화
- [ ] 토큰 사용량 80% 이상 감소 확인
- [ ] Auto-compacting 미발생 확인

### 문서화 완료 기준
- [ ] README.md 업데이트
- [ ] API 명세 업데이트
- [ ] 마이그레이션 가이드 작성
- [ ] CHANGELOG 업데이트
