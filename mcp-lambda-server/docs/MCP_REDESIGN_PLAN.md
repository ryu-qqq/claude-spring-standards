# MCP 재설계 계획서

> **목표**: MCP를 순수 정보 브릿지로 재정의하여 토큰 효율성 극대화

## 1. 설계 철학

### 핵심 원칙

```
┌─────────────────────────────────────────────────────────────┐
│                      MCP = 정보 브릿지                       │
│                                                             │
│   Spring REST API  ←→  MCP Server  ←→  LLM (Claude)        │
│   (정보의 원천)         (순수 전달)     (판단 + 실행)         │
└─────────────────────────────────────────────────────────────┘
```

- **MCP는 판단하지 않는다**: validate, detect 같은 판단 로직 제거
- **MCP는 생성하지 않는다**: generate 같은 코드 생성 제거
- **LLM이 규칙을 조회하고 직접 판단**: 규칙 조회 → LLM 판단 → LLM 실행

### 토큰 효율화 전략

```
AS-IS: get_context(layer) → 한 번에 모든 데이터 반환 (토큰 폭발)

TO-BE: 계층적 탐색
  1. list_tech_stacks()           → ID + 이름만
  2. get_architecture(id)         → Layer 목록
  3. get_layer_detail(id)         → 상세 정보
  4. get_convention_tree(id)      → Rules + Templates
```

---

## 2. Spring REST API 현황

### 도메인 모델 구조

```
TechStack (Root)
    └── Architecture
            ├── Layer ⭐ (동적: DOMAIN, APPLICATION, PERSISTENCE, REST_API)
            │     ├── Convention → CodingRule → RuleExample, ChecklistItem, ZeroToleranceRule
            │     ├── Module → ResourceTemplate, PackageStructure → ClassTemplate, ArchUnitTest
            │     └── PackagePurpose
            └── LayerDependencyRule
```

### 구현된 REST API 엔드포인트

| 도메인 | 기본 경로 | CRUD | 상태 |
|--------|-----------|------|------|
| TechStack | `/api/v1/templates/tech-stacks` | ✅ | 구현됨 |
| Architecture | `/api/v1/templates/architectures` | ✅ | 구현됨 |
| Layer | `/api/v1/templates/layers` | ✅ | 구현됨 |
| Convention | `/api/v1/templates/conventions` | ✅ | 구현됨 |
| CodingRule | `/api/v1/templates/coding-rules` | ✅ | 구현됨 |
| ClassTemplate | `/api/v1/templates/class-templates` | ✅ | 구현됨 |
| Module | `/api/v1/templates/modules` | ✅ | 구현됨 |
| PackagePurpose | `/api/v1/templates/package-purposes` | ✅ | 구현됨 |
| PackageStructure | `/api/v1/templates/package-structures` | ✅ | 구현됨 |
| ArchUnitTest | `/api/v1/templates/archunit-tests` | ✅ | 구현됨 |
| ResourceTemplate | `/api/v1/templates/resource-templates` | ✅ | 구현됨 |
| RuleExample | `/api/v1/templates/rule-examples` | ✅ | 구현됨 |
| ChecklistItem | `/api/v1/templates/checklist-items` | ✅ | 구현됨 |
| ZeroToleranceRule | `/api/v1/templates/zero-tolerance-rules` | ✅ | 구현됨 |
| LayerDependency | `/api/v1/templates/layer-dependencies` | ✅ | 구현됨 |

### MCP 전용 엔드포인트

| 엔드포인트 | 용도 |
|------------|------|
| `GET /api/v1/templates/mcp/convention-tree` | 컨벤션 트리 조회 |
| `GET /api/v1/templates/mcp/search` | 통합 검색 |

---

## 3. 현재 MCP API Client 분석

### 구현된 엔드포인트

| 메서드 | 엔드포인트 | 상태 |
|--------|------------|------|
| `get_convention_tree()` | `/mcp/convention-tree` | ✅ |
| `search()` | `/mcp/search` | ✅ |
| `get_conventions()` | `/conventions` | ✅ |
| `get_coding_rules()` | `/coding-rules` | ✅ |
| `get_coding_rule()` | `/coding-rules/{code}` | ✅ |
| `get_class_templates()` | `/class-templates` | ✅ |
| `get_layer_dependencies()` | `/layer-dependencies` | ✅ |
| `get_package_structures()` | `/package-structures` | ✅ |
| `get_rule_examples()` | `/rule-examples` | ✅ |

### 누락된 엔드포인트 (8개)

| 도메인 | 필요성 | 우선순위 |
|--------|--------|----------|
| **TechStack** | 계층 탐색 시작점 | 🔴 높음 |
| **Architecture** | 아키텍처 컨텍스트 | 🔴 높음 |
| **Layer** | 레이어 상세 + 모듈/패키지 | 🔴 높음 |
| **Module** | 모듈별 패키지 구조 | 🟡 중간 |
| **PackagePurpose** | 패키지 목적 정의 | 🟡 중간 |
| **ArchUnitTest** | 아키텍처 테스트 규칙 | 🟡 중간 |
| **ResourceTemplate** | 리소스 템플릿 | 🟢 낮음 |
| **ChecklistItem** | 체크리스트 항목 | 🟢 낮음 |

---

## 4. MCP Tools 재설계

### AS-IS (7개 Tools)

```python
# 유지
tool_get_context()      # → 재설계 (계층적 탐색으로)
tool_search()           # → 유지
tool_get_rule()         # → 유지
tool_feedback()         # → 검토 필요

# 삭제 대상
tool_validate()         # ❌ Python 하드코딩, LLM이 직접 판단
tool_detect()           # ❌ Python 하드코딩, LLM이 직접 감지
tool_generate()         # ❌ Python 하드코딩, LLM이 직접 생성
```

### TO-BE (9개 Tools)

```
┌─────────────────────────────────────────────────────────────┐
│                  Navigation Tools (경량)                     │
├─────────────────────────────────────────────────────────────┤
│ 1. list_tech_stacks()       → TechStack 목록 (ID + 이름)    │
│ 2. get_architecture(id)     → Architecture + Layer 목록    │
│ 3. get_layer_detail(id)     → Layer + Module + Purpose 상세│
│ 4. list_conventions(layer?) → Convention 목록 (필터링)     │
├─────────────────────────────────────────────────────────────┤
│                   Detail Tools (상세)                        │
├─────────────────────────────────────────────────────────────┤
│ 5. get_convention_tree(id)  → Rules + Templates + Checklist│
│ 6. get_rule_detail(code)    → Rule + Examples + Zero-Tol   │
│ 7. get_class_template(id)   → 클래스 템플릿 상세            │
│ 8. list_archunit_tests(id?) → ArchUnit 테스트 목록          │
├─────────────────────────────────────────────────────────────┤
│                   Utility Tools (검색/피드백)                │
├─────────────────────────────────────────────────────────────┤
│ 9. search(query, scope?)    → 통합 검색                     │
└─────────────────────────────────────────────────────────────┘
```

### Tool 상세 명세

#### 1. list_tech_stacks()
```python
@mcp.tool()
def list_tech_stacks() -> dict:
    """기술 스택 목록 조회 (ID + 이름만)

    Returns:
        {"tech_stacks": [{"id": 1, "name": "Spring Boot 3.5.x", "version": "3.5.x"}]}
    """
```

#### 2. get_architecture(tech_stack_id)
```python
@mcp.tool()
def get_architecture(tech_stack_id: int) -> dict:
    """아키텍처 정보 + Layer 목록 조회

    Returns:
        {
            "architecture": {"id": 1, "name": "Hexagonal", "description": "..."},
            "layers": [
                {"id": 1, "code": "DOMAIN", "name": "Domain Layer", "order": 1},
                {"id": 2, "code": "APPLICATION", "name": "Application Layer", "order": 2}
            ]
        }
    """
```

#### 3. get_layer_detail(layer_id)
```python
@mcp.tool()
def get_layer_detail(layer_id: int) -> dict:
    """레이어 상세 + 관련 모듈/패키지 목적 조회

    Returns:
        {
            "layer": {"id": 1, "code": "DOMAIN", "name": "...", "description": "..."},
            "modules": [{"id": 1, "name": "domain", "gradlePath": "domain"}],
            "package_purposes": [{"id": 1, "purpose": "AGGREGATE", "pattern": "*.aggregate"}],
            "conventions": [{"id": 1, "name": "Aggregate Convention"}]
        }
    """
```

#### 4. list_conventions(layer_code?)
```python
@mcp.tool()
def list_conventions(layer_code: str = None) -> dict:
    """컨벤션 목록 조회 (레이어별 필터링 가능)

    Args:
        layer_code: DOMAIN | APPLICATION | PERSISTENCE | REST_API (선택)

    Returns:
        {"conventions": [{"id": 1, "name": "...", "layer": "DOMAIN", "rule_count": 15}]}
    """
```

#### 5. get_convention_tree(convention_id)
```python
@mcp.tool()
def get_convention_tree(convention_id: int) -> dict:
    """컨벤션 상세 + 하위 Rules/Templates/Checklist

    Returns:
        {
            "convention": {...},
            "coding_rules": [...],
            "class_templates": [...],
            "checklist_items": [...]
        }
    """
```

#### 6. get_rule_detail(rule_code)
```python
@mcp.tool()
def get_rule_detail(rule_code: str) -> dict:
    """규칙 상세 + 예시 + Zero-Tolerance 여부

    Returns:
        {
            "rule": {...},
            "examples": [{"type": "GOOD", "code": "..."}, {"type": "BAD", "code": "..."}],
            "zero_tolerance": {"pattern": "...", "message": "..."} | null
        }
    """
```

#### 7. get_class_template(template_id)
```python
@mcp.tool()
def get_class_template(template_id: int) -> dict:
    """클래스 템플릿 상세

    Returns:
        {
            "template": {
                "id": 1,
                "class_type": "AGGREGATE",
                "template_code": "...",
                "description": "..."
            }
        }
    """
```

#### 8. list_archunit_tests(package_structure_id?)
```python
@mcp.tool()
def list_archunit_tests(package_structure_id: int = None) -> dict:
    """ArchUnit 테스트 목록 조회

    Returns:
        {"archunit_tests": [{"id": 1, "test_code": "...", "description": "..."}]}
    """
```

#### 9. search(query, scope?)
```python
@mcp.tool()
def search(query: str, scope: str = "all") -> dict:
    """통합 검색

    Args:
        query: 검색어
        scope: all | rules | templates | conventions

    Returns:
        {"results": [...], "total": 10}
    """
```

---

## 5. 삭제 대상

### Python 하드코딩 모듈

| 파일/디렉토리 | 사유 |
|---------------|------|
| `src/tools/validate.py` | LLM이 규칙 조회 후 직접 판단 |
| `src/tools/detect.py` | LLM이 코드 분석 후 직접 감지 |
| `src/tools/generate.py` | LLM이 템플릿 조회 후 직접 생성 |
| `src/services/validation/` | validate 관련 서비스 전체 |
| `src/services/detection/` | detect 관련 서비스 전체 |
| `src/services/generation/` | generate 관련 서비스 전체 |

### PostgreSQL 관련 (사용 안 함)

| 파일/디렉토리 | 사유 |
|---------------|------|
| `src/db/` | PostgreSQL 연동 모듈 (미사용) |
| `src/models/feedback.py` | Feedback 모델 (미사용 시) |

---

## 6. 작업 순서

### Phase 1: API Client 확장 (1일)

```
1. TechStack 엔드포인트 추가
2. Architecture 엔드포인트 추가
3. Layer 엔드포인트 추가
4. Module 엔드포인트 추가
5. PackagePurpose 엔드포인트 추가
6. ArchUnitTest 엔드포인트 추가
7. ResourceTemplate 엔드포인트 추가
8. ChecklistItem 엔드포인트 추가
```

### Phase 2: MCP Tools 재설계 (1일)

```
1. 기존 tool_get_context() → list_tech_stacks() + 계층 tools로 분리
2. 기존 tool_get_rule() → get_rule_detail()로 리네임
3. 기존 tool_search() 유지
4. 새 tools 추가: get_architecture, get_layer_detail, list_conventions 등
```

### Phase 3: 불필요 코드 삭제 (0.5일)

```
1. validate, detect, generate tool 및 서비스 삭제
2. PostgreSQL 관련 코드 삭제 (미사용 시)
3. 테스트 코드 정리
```

### Phase 4: CLAUDE.md 업데이트 (0.5일)

```
1. LLM 워크플로우 가이드 추가
2. MCP Tool 사용 예시 추가
3. 판단 로직 가이드 추가
```

---

## 7. LLM 워크플로우 가이드

### 코드 작성 시

```
1. list_tech_stacks() → 기술 스택 확인
2. get_architecture(id) → 레이어 목록 확인
3. get_layer_detail(layer_id) → 작업할 레이어 컨텍스트
4. get_convention_tree(conv_id) → 적용할 규칙들 조회
5. [LLM 직접] → 규칙 기반 코드 생성
```

### 코드 검증 시

```
1. get_layer_detail(layer_id) → 레이어 규칙 컨텍스트
2. get_convention_tree(conv_id) → Zero-Tolerance 규칙 조회
3. [LLM 직접] → 코드와 규칙 대조하여 위반 검사
4. [LLM 직접] → 위반 항목 수정 제안
```

### 리팩토링 시

```
1. search("키워드") → 관련 규칙 검색
2. get_rule_detail(code) → 규칙 상세 + 예시 확인
3. [LLM 직접] → GOOD 예시 참고하여 리팩토링
```

---

## 8. 기대 효과

### 토큰 효율화

| 시나리오 | AS-IS | TO-BE | 절감 |
|----------|-------|-------|------|
| Layer 규칙 조회 | ~15K tokens | ~3K tokens | 80% |
| 특정 규칙 조회 | ~8K tokens | ~1K tokens | 87% |
| 클래스 생성 | ~20K tokens | ~5K tokens | 75% |

### 유지보수성

- **규칙 변경 시**: Spring DB만 수정 → 즉시 반영
- **새 규칙 추가 시**: Python 코드 변경 불필요
- **버그 수정 시**: 단일 책임 원칙으로 디버깅 용이

### 확장성

- **새 도메인 추가**: API Client 메서드만 추가
- **새 Tool 추가**: 간단한 브릿지 함수만 작성
- **다른 LLM 지원**: MCP 표준 프로토콜 유지

---

## 9. 체크리스트

### API Client 확장
- [ ] TechStack 엔드포인트
- [ ] Architecture 엔드포인트
- [ ] Layer 엔드포인트
- [ ] Module 엔드포인트
- [ ] PackagePurpose 엔드포인트
- [ ] ArchUnitTest 엔드포인트
- [ ] ResourceTemplate 엔드포인트
- [ ] ChecklistItem 엔드포인트

### MCP Tools 재설계
- [ ] list_tech_stacks()
- [ ] get_architecture()
- [ ] get_layer_detail()
- [ ] list_conventions()
- [ ] get_convention_tree() (기존 유지/개선)
- [ ] get_rule_detail() (기존 리네임)
- [ ] get_class_template()
- [ ] list_archunit_tests()
- [ ] search() (기존 유지)

### 삭제
- [ ] tool_validate() 및 관련 서비스
- [ ] tool_detect() 및 관련 서비스
- [ ] tool_generate() 및 관련 서비스
- [ ] PostgreSQL 모듈 (미사용 시)

### 문서화
- [ ] CLAUDE.md LLM 워크플로우 가이드
- [ ] README.md Tool 사용 예시
- [ ] 테스트 시나리오 문서

---

*작성일: 2026-01-20*
*버전: 1.0.0*
