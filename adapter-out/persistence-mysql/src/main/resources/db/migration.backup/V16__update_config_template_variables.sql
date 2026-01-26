-- ============================================
-- V16: config_file_template Variables 확장
-- CLAUDE.md의 하드코딩된 값을 동적 변수로 대체
-- ============================================

-- CLAUDE.md (id=1) 업데이트
UPDATE config_file_template
SET
    variables = JSON_OBJECT(
        'project_name', '프로젝트명 (init 시 치환)',
        'tech_stack', 'DYNAMIC - tech_stack 테이블에서 조회',
        'architecture', 'DYNAMIC - architecture 테이블에서 조회',
        'layers_diagram', 'DYNAMIC - layer 테이블에서 조회하여 다이어그램 생성'
    ),
    content = '# {{project_name}} - Claude Code Configuration

이 프로젝트는 **{{tech_stack.framework_type}} {{tech_stack.framework_version}} + {{tech_stack.language_type}} {{tech_stack.language_version}}** 기반의 **{{architecture.name}}** 프로젝트입니다.

---

## 🏗️ 아키텍처 개요

{{layers_diagram}}

---

## 🧰 MCP 도구 사용법

이 프로젝트의 코딩 컨벤션은 **Convention Hub DB**에서 관리됩니다.
코드 작성 시 반드시 MCP 도구를 사용하여 규칙을 조회하세요.

### 3-Phase 워크플로우

```
┌─────────────────────────────────────────────────────────────┐
│  1️⃣ PLANNING PHASE                                          │
│     planning_context(layers=["DOMAIN", "APPLICATION"])      │
│     → 어떤 컴포넌트를 어디에 만들지 결정                       │
├─────────────────────────────────────────────────────────────┤
│  2️⃣ EXECUTION PHASE                                         │
│     module_context(module_id=1, class_type="AGGREGATE")     │
│     → 템플릿 + 규칙 기반 코드 생성                            │
├─────────────────────────────────────────────────────────────┤
│  3️⃣ VALIDATION PHASE                                        │
│     validation_context(layers=["DOMAIN"])                   │
│     → Zero-Tolerance + Checklist 검증                       │
└─────────────────────────────────────────────────────────────┘
```

### 사용 예시

```python
# 1. 개발 계획 수립
planning_context(layers=["DOMAIN", "APPLICATION"])
# → 기술스택, 아키텍처, 모듈 목록, 패키지 구조 요약

# 2. 코드 생성
module_context(module_id=1, class_type="AGGREGATE")
# → 템플릿 + 규칙 + 예시 한방 조회

# 3. 코드 검증
validation_context(layers=["DOMAIN"])
# → Zero-Tolerance 패턴 + 체크리스트
```

---

## 🚨 Zero-Tolerance 규칙

> ⚠️ **중요**: 규칙은 DB에서 조회하세요. 아래는 주요 항목 요약입니다.

```python
# Zero-Tolerance 규칙 조회
validation_context(layers=["DOMAIN", "APPLICATION", "PERSISTENCE", "REST_API"])
```

### 주요 규칙 (요약)

> 상세 규칙은 MCP `validation_context()` 또는 `get_rule()` 로 조회

| 레이어 | 규칙 |
|--------|------|
| Domain | Lombok 금지, Getter 체이닝 금지, Tell Don''t Ask |
| Application | @Transactional 내 외부 API 금지, DTO는 Record |
| Persistence | JPA 관계 어노테이션 금지, Long FK 전략 |
| REST API | MockMvc 금지, @Valid 필수 |

---

## 📚 MCP Tools 목록

| 분류 | Tool | 용도 |
|------|------|------|
| **워크플로우** | planning_context | 개발 계획 수립 |
| | module_context | 코드 생성 (템플릿 + 규칙) |
| | validation_context | 코드 검증 (Zero-Tolerance) |
| **컨텍스트** | get_context | 빠른 컨텍스트 조회 |
| | get_rule | 규칙 상세 + 예시 |
| **계층** | list_tech_stacks | 기술 스택 목록 |
| | get_architecture | 아키텍처 상세 |
| | get_layer_detail | 레이어 상세 |

---

## 🔧 설계 원칙

MCP 서버는 **순수 정보 브릿지**로 설계되었습니다:
- MCP = 규칙/템플릿 전달 (Spring API → LLM)
- **LLM은 규칙을 반드시 준수**하며 코드 생성
- 규칙을 "판단"하지 않고 **100% 준수**

---

## ⚡ 빠른 시작

```python
# 1. Aggregate 생성 시
planning_context(layers=["DOMAIN"])
module_context(module_id=1, class_type="AGGREGATE")
# → 템플릿 따라 코드 작성
validation_context(layers=["DOMAIN"])
# → Zero-Tolerance 검증

# 2. UseCase 생성 시
module_context(module_id=2, class_type="USE_CASE")
# → Application 레이어 템플릿 + 규칙 조회
```
',
    updated_at = NOW()
WHERE id = 1;
