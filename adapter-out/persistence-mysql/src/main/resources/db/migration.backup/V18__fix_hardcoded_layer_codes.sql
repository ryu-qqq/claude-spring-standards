-- ============================================
-- V18: 하드코딩된 잘못된 레이어 코드 수정
-- PERSISTENCE → ADAPTER_OUT, REST_API → ADAPTER_IN
-- ============================================

-- 1. CLAUDE.md (id=1) - 잘못된 레이어 코드 수정 + 동적 조회 안내 강화
UPDATE config_file_template
SET content = '# {{project_name}} - Claude Code Configuration

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
│     planning_context(layers=[...])                          │
│     → 레이어는 list_tech_stacks()로 먼저 조회                 │
├─────────────────────────────────────────────────────────────┤
│  2️⃣ EXECUTION PHASE                                         │
│     module_context(module_id=N, class_type="...")           │
│     → 템플릿 + 규칙 기반 코드 생성                            │
├─────────────────────────────────────────────────────────────┤
│  3️⃣ VALIDATION PHASE                                        │
│     validation_context(layers=[...])                        │
│     → Zero-Tolerance + Checklist 검증                       │
└─────────────────────────────────────────────────────────────┘
```

### 사용 예시

```python
# 0. 먼저 레이어 목록 조회 (하드코딩 금지!)
list_tech_stacks()
# → layers: ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]

# 1. 개발 계획 수립
planning_context(layers=["DOMAIN", "APPLICATION"])

# 2. 코드 생성
module_context(module_id=1, class_type="AGGREGATE")

# 3. 코드 검증
validation_context(layers=["DOMAIN"])
```

---

## 🚨 Zero-Tolerance 규칙

> ⚠️ **중요**: 규칙은 DB에서 조회하세요.

```python
# Zero-Tolerance 규칙 조회 (레이어는 동적으로!)
validation_context(layers={{layers}})
```

### 주요 규칙 (요약)

> 상세 규칙은 MCP `validation_context()` 또는 `get_rule()` 로 조회

MCP를 통해 최신 규칙을 동적으로 조회하세요.
하드코딩된 규칙은 DB 변경 시 outdated 될 수 있습니다.

---

## 📚 MCP Tools 목록

| 분류 | Tool | 용도 |
|------|------|------|
| **워크플로우** | planning_context | 개발 계획 수립 |
| | module_context | 코드 생성 (템플릿 + 규칙) |
| | validation_context | 코드 검증 (Zero-Tolerance) |
| **컨텍스트** | get_context | 빠른 컨텍스트 조회 |
| | get_rule | 규칙 상세 + 예시 |
| **계층** | list_tech_stacks | 기술 스택 + 레이어 목록 |
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
# 1. 레이어 목록 조회
layers = list_tech_stacks()  # → ["DOMAIN", "APPLICATION", ...]

# 2. Aggregate 생성 시
planning_context(layers=["DOMAIN"])
module_context(module_id=1, class_type="AGGREGATE")
validation_context(layers=["DOMAIN"])

# 3. UseCase 생성 시
module_context(module_id=2, class_type="USE_CASE")
```
',
    updated_at = NOW()
WHERE id = 1;

-- 2. planner.md (id=4) - 하드코딩된 레이어 제거
UPDATE config_file_template
SET content = '# Planner Agent

Epic 기획 및 Task 분해 전문가. 요구사항을 분석하고 구현 전략을 수립.

## 🎯 핵심 원칙

> **MCP로 프로젝트 구조 파악 → 영향도 분석 → Task 분해**

---

## 📋 작업 워크플로우

### Phase 1: 프로젝트 구조 파악

```python
# 먼저 레이어 목록 조회
list_tech_stacks()
# → layers: ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]

# 현재 TechStack/Architecture 확인
planning_context(layers=[...])  # 조회된 레이어 사용
# → 모듈 목록, 패키지 구조, 레이어 관계 파악
```

### Phase 2: 영향도 분석

```python
# Serena로 기존 코드 검색
serena.search_for_pattern(pattern="관련_키워드")
serena.find_symbol(name_path="관련_클래스")
# → 변경 영향 범위 파악
```

### Phase 3: Task 분해

1. **컨텍스트 크기 기준**: ~15K tokens per Task
2. **레이어별 분리**: 하위 레이어 → 상위 레이어 순서
3. **의존성 순서**: Domain → Application → Adapter 순

### Phase 4: Epic 문서 작성

```python
# Serena Memory에 Epic 저장
serena.write_memory(
    memory_file_name="epic-{feature_name}",
    content=epic_document
)
```

---

## 📊 Task 분해 기준

| 작업 유형 | Task 단위 |
|----------|----------|
| 🆕 신규 기능 | 레이어별 1 Task |
| ➕ 기능 확장 | 변경 파일 그룹별 |
| 🔄 리팩토링 | 패턴별 |
| 🐛 버그 수정 | 원인별 |
',
    updated_at = NOW()
WHERE id = 4;

-- 3. rule-checker-hook.sh (id=14) - 잘못된 레이어 코드 수정
UPDATE config_file_template
SET content = '#!/bin/bash
# Rule Checker Hook
# Java 파일 수정 시 Zero-Tolerance 규칙 체크

FILE_PATH="$1"

# Java 파일이 아니면 스킵
if [[ ! "$FILE_PATH" =~ \\.java$ ]]; then
    exit 0
fi

# 레이어 판별 (실제 DB layer.code 기준)
if [[ "$FILE_PATH" =~ /domain/ ]]; then
    LAYER="DOMAIN"
elif [[ "$FILE_PATH" =~ /application/ ]]; then
    LAYER="APPLICATION"
elif [[ "$FILE_PATH" =~ /adapter-out/ ]] || [[ "$FILE_PATH" =~ /persistence/ ]]; then
    LAYER="ADAPTER_OUT"
elif [[ "$FILE_PATH" =~ /adapter-in/ ]] || [[ "$FILE_PATH" =~ /rest-api/ ]]; then
    LAYER="ADAPTER_IN"
else
    exit 0
fi

# Zero-Tolerance 패턴 체크
VIOLATIONS=""

# Lombok 체크 (Domain)
if [[ "$LAYER" == "DOMAIN" ]]; then
    if grep -qE "@Data|@Getter|@Setter|@Builder" "$FILE_PATH"; then
        VIOLATIONS+="❌ Lombok 사용 금지 (Zero-Tolerance)\\n"
    fi
fi

# 결과 출력
if [[ -n "$VIOLATIONS" ]]; then
    echo -e "⚠️ Zero-Tolerance 위반 발견:\\n$VIOLATIONS"
    echo "상세 규칙: validation_context(layers=[\"$LAYER\"])"
fi
',
    updated_at = NOW()
WHERE id = 14;

-- 4. reviewer.md (id=5) - 동적 조회 안내 강화
UPDATE config_file_template
SET content = '# Reviewer Agent

코드 리뷰 전문가. Convention Hub 규칙 기반 검증.

## 🎯 핵심 원칙

> **MCP로 규칙 조회 → 코드 대조 → 위반 사항 리포트**

---

## 📋 리뷰 워크플로우

### Phase 1: 레이어 및 규칙 로드

```python
# 먼저 레이어 목록 조회
list_tech_stacks()

# 변경된 레이어의 규칙 조회
validation_context(layers=[...])  # 동적으로 레이어 지정
# → Zero-Tolerance 규칙 + 체크리스트 획득

# Serena에 캐싱
serena.write_memory("review-rules", rules)
```

### Phase 2: 코드 분석

```python
# 변경 파일 분류
git diff --name-only

# 레이어별 파일 그룹핑 (경로 패턴으로 판별)
# /domain/     → DOMAIN
# /application/ → APPLICATION
# /adapter-out/ or /persistence/ → ADAPTER_OUT
# /adapter-in/ or /rest-api/     → ADAPTER_IN
```

### Phase 3: 규칙 대조

각 파일에 대해:
1. 해당 레이어/클래스타입의 규칙 조회
2. 코드와 규칙 대조
3. 위반 사항 기록

### Phase 4: 리포트 생성

```markdown
## 리뷰 결과

### 🔴 필수 수정 (Zero-Tolerance 위반)
- [ ] 규칙코드: 설명 → 파일:라인

### 🟡 권장 수정
- [ ] ...

### 🟢 통과
- ✅ 규칙 준수 항목들
```

---

## ⚠️ Zero-Tolerance 우선 체크

MCP `validation_context()` 로 최신 규칙 조회 후 체크:
- Lombok 사용 여부
- Getter 체이닝 (Law of Demeter)
- @Transactional 내 외부 API 호출
- JPA 관계 어노테이션
',
    updated_at = NOW()
WHERE id = 5;
