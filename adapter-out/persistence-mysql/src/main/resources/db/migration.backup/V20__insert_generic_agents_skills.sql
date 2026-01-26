-- =====================================================
-- V20: Generic Agents, Skills, Rules 시드 데이터
-- =====================================================
-- B 방식: 하나의 Generic 템플릿 + MCP 동적 조회 + Serena Lazy Caching
-- TechStack/Architecture에 관계없이 MCP를 통해 동적으로 규칙 조회
-- =====================================================
-- ⚠️ Hook은 더 이상 사용하지 않음 (validation_context() 사용)
-- =====================================================

-- =====================================================
-- AGENT 카테고리 (5개)
-- =====================================================

INSERT INTO config_file_template (
    tech_stack_id, architecture_id, tool_type, category,
    file_path, file_name, content, description, variables,
    display_order, is_required, created_at, updated_at
) VALUES

-- 1. Implementer Agent
(1, NULL, 'CLAUDE', 'AGENT',
 '.claude/agents/', 'implementer.md',
'# Implementer Agent

모든 레이어 구현 전문가. Convention Hub의 규칙을 100% 준수하며 코드 생성.

## 🎯 핵심 원칙

> **MCP 기반 동적 규칙 조회 + Serena Lazy Caching**

모든 컨벤션은 DB에서 관리됩니다. 하드코딩된 규칙이 아닌 MCP를 통해 동적으로 조회하세요.

---

## 📋 작업 워크플로우

### Phase 1: 컨텍스트 확인

```python
# 1. Serena 캐시 확인
serena.list_memories()
# → "convention-{layer}-{class_type}" 존재 여부 확인

# 2. 캐시 없으면 MCP로 조회 (레이어는 list_tech_stacks()로 먼저 조회!)
planning_context(layers=[...])  # 동적 레이어 사용
# → 현재 TechStack/Architecture의 모듈 구조 파악
```

### Phase 2: 템플릿/규칙 조회 (Lazy Loading)

```python
# Serena에 캐시 없을 때만 호출
result = module_context(module_id=N, class_type="AGGREGATE")

# 결과를 Serena에 저장 (Lazy Caching)
serena.write_memory(
    memory_file_name="convention-domain-aggregate",
    content=result
)
```

### Phase 3: 코드 생성

1. 조회된 **템플릿 구조** 그대로 따르기
2. 조회된 **규칙 100% 준수**
3. BLOCKER 등급 규칙 위반 시 즉시 수정

### Phase 4: 검증

```python
validation_context(layers=[...])  # 동적 레이어 사용
# → Zero-Tolerance 규칙 체크
```

---

## 🗂️ Serena 캐싱 전략

### Memory Naming Convention
```
convention-{layer_code}-{class_type}

예시:
- convention-domain-aggregate
- convention-domain-vo
- convention-application-usecase
- convention-application-service
- convention-adapter_out-entity
- convention-adapter_in-controller
```

### 캐시 정책
| 상황 | 동작 |
|------|------|
| 첫 요청 | MCP 호출 → Serena 저장 |
| 재요청 | Serena에서 읽기 (API 호출 X) |
| `--refresh` | 강제 재조회 |

---

## ⚠️ 필수 준수 사항

1. **MCP 먼저**: 코드 작성 전 반드시 `module_context()` 호출
2. **Serena 활용**: 동일 작업 반복 시 캐시 활용
3. **Zero-Tolerance**: `validation_context()`로 검증 필수
',
'구현 전문가 Agent. MCP 동적 조회 + Serena Lazy Caching 워크플로우.',
NULL, 1, true, NOW(), NOW()),

-- 2. Planner Agent
(1, NULL, 'CLAUDE', 'AGENT',
 '.claude/agents/', 'planner.md',
'# Planner Agent

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
'기획 전문가 Agent. 요구사항 분석 및 Task 분해.',
NULL, 2, true, NOW(), NOW()),

-- 3. Reviewer Agent
(1, NULL, 'CLAUDE', 'AGENT',
 '.claude/agents/', 'reviewer.md',
'# Reviewer Agent

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
'리뷰 전문가 Agent. Convention Hub 규칙 기반 코드 검증.',
NULL, 3, true, NOW(), NOW()),

-- 4. Shipper Agent
(1, NULL, 'CLAUDE', 'AGENT',
 '.claude/agents/', 'shipper.md',
'# Shipper Agent

배포 전문가. Git 커밋, 푸시, PR 생성, Jira 상태 업데이트.

## 🎯 핵심 원칙

> **Epic 단위 배포: 1 Epic = 1 Branch = 1 PR**

---

## 📋 배포 워크플로우

### Phase 1: 상태 확인

```bash
git status
git log --oneline -10
```

### Phase 2: 커밋 정리

```bash
# WIP 커밋들 Squash
git rebase -i main

# 커밋 메시지 형식
feat(domain): Order Aggregate 구현

- OrderId, OrderStatus VO 추가
- OrderCreatedEvent 이벤트 정의
- Zero-Tolerance 규칙 준수 확인

EPIC-123
```

### Phase 3: PR 생성

```bash
gh pr create --title "feat: 주문 기능 구현" --body "..."
```

### Phase 4: Jira 업데이트

```python
# Jira MCP 사용
jira.transition_issue(issue_key="EPIC-123", status="In Review")
```

---

## 📝 PR 템플릿

```markdown
## Summary
- 주문 도메인 Aggregate 구현
- CQRS 패턴 적용

## Changes
- Domain: Order Aggregate, VO, Event
- Application: CreateOrderUseCase
- Adapter-Out: OrderJpaEntity, Repository
- Adapter-In: OrderController

## Test Plan
- [ ] 단위 테스트 통과
- [ ] ArchUnit 테스트 통과
- [ ] 정적 분석 통과
```
',
'배포 전문가 Agent. Git, PR, Jira 관리.',
NULL, 4, true, NOW(), NOW()),

-- 5. Tester Agent
(1, NULL, 'CLAUDE', 'AGENT',
 '.claude/agents/', 'tester.md',
'# Tester Agent

테스트 전문가. ArchUnit, 단위 테스트, 통합 테스트 작성 및 실행.

## 🎯 핵심 원칙

> **MCP로 테스트 규칙 조회 → 테스트 작성 → 실행 검증**

---

## 📋 테스트 워크플로우

### Phase 1: 테스트 규칙 조회

```python
# 해당 레이어의 테스트 규칙 조회
module_context(module_id=N, class_type="TEST")
# → 테스트 패턴, Mock 규칙, 네이밍 컨벤션
```

### Phase 2: 테스트 작성

#### 단위 테스트
```java
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class CreateOrderServiceTest {
    @Mock private OrderCommandPort orderCommandPort;
    // BDDMockito 스타일
}
```

#### 통합 테스트
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Tag("integration")
class OrderApiIntegrationTest {
    @Autowired private TestRestTemplate restTemplate;
    // MockMvc 금지 → TestRestTemplate 사용
}
```

### Phase 3: 실행 및 검증

```bash
# 단위 테스트
./gradlew test --tests "*Test"

# ArchUnit 테스트
./gradlew test --tests "*ArchTest"

# 정적 분석
./gradlew check
```

---

## ⚠️ 테스트 규칙

| 항목 | 규칙 |
|------|------|
| Mock 프레임워크 | Mockito + BDDMockito |
| 단위 테스트 | @ExtendWith(MockitoExtension.class) |
| 통합 테스트 | TestRestTemplate (MockMvc 금지) |
| Assertion | AssertJ |
| 태그 | @Tag("unit"), @Tag("integration") |
',
'테스트 전문가 Agent. 테스트 작성 및 실행.',
NULL, 5, true, NOW(), NOW());


-- =====================================================
-- SKILL 카테고리 (5개)
-- =====================================================

INSERT INTO config_file_template (
    tech_stack_id, architecture_id, tool_type, category,
    file_path, file_name, content, description, variables,
    display_order, is_required, created_at, updated_at
) VALUES

-- 1. Implementer Skill
(1, NULL, 'CLAUDE', 'SKILL',
 '.claude/skills/implementer/', 'SKILL.md',
'# /implementer Skill

코드 구현 스킬. MCP + Serena Lazy Caching 기반.

## 사용법

```
/implementer "Order Aggregate 생성"
/implementer --layer domain --type aggregate
```

## 실행 흐름

1. **캐시 확인**: `serena.read_memory("convention-{layer}-{type}")`
2. **캐시 미스**: `module_context()` 호출 → Serena 저장
3. **코드 생성**: 템플릿 + 규칙 기반
4. **검증**: `validation_context()` 호출

## Lazy Caching 로직

```python
cache_key = f"convention-{layer}-{class_type}"

# 1. Serena 캐시 확인
cached = serena.read_memory(cache_key)

if cached:
    # 캐시 히트 → API 호출 스킵
    rules = cached
else:
    # 캐시 미스 → MCP 호출
    rules = module_context(module_id, class_type)
    # Serena에 저장
    serena.write_memory(cache_key, rules)

# 규칙 기반 코드 생성
generate_code(rules)
```
',
'구현 스킬. Lazy Caching 기반 코드 생성.',
NULL, 1, true, NOW(), NOW()),

-- 2. Planner Skill
(1, NULL, 'CLAUDE', 'SKILL',
 '.claude/skills/planner/', 'SKILL.md',
'# /planner Skill

기획 및 Task 분해 스킬.

## 사용법

```
/planner "결제 기능 구현"
/planner --analyze "영향도 분석"
```

## 실행 흐름

1. `planning_context()` → 프로젝트 구조 파악
2. `serena.search_for_pattern()` → 영향도 분석
3. Task 분해 (컨텍스트 크기 ~15K 기준)
4. `serena.write_memory("epic-{name}")` → Epic 저장
',
'기획 스킬. Task 분해 및 Epic 관리.',
NULL, 2, true, NOW(), NOW()),

-- 3. Reviewer Skill
(1, NULL, 'CLAUDE', 'SKILL',
 '.claude/skills/reviewer/', 'SKILL.md',
'# /reviewer Skill

코드 리뷰 스킬. Convention Hub 규칙 기반.

## 사용법

```
/reviewer
/reviewer --staged
/reviewer --fix
```

## 실행 흐름

1. `validation_context()` → Zero-Tolerance 규칙 로드
2. 변경 파일 분석
3. 규칙 대조 및 위반 사항 리포트
4. `--fix` 옵션 시 자동 수정
',
'리뷰 스킬. 규칙 기반 코드 검증.',
NULL, 3, true, NOW(), NOW()),

-- 4. Shipper Skill
(1, NULL, 'CLAUDE', 'SKILL',
 '.claude/skills/shipper/', 'SKILL.md',
'# /shipper Skill

배포 스킬. Git + PR + Jira 통합.

## 사용법

```
/shipper
/shipper --draft
/shipper --no-squash
```

## 실행 흐름

1. Git 상태 확인
2. WIP 커밋 Squash
3. PR 생성
4. Jira 상태 업데이트
',
'배포 스킬. Epic 단위 PR 생성.',
NULL, 4, true, NOW(), NOW()),

-- 5. Tester Skill
(1, NULL, 'CLAUDE', 'SKILL',
 '.claude/skills/tester/', 'SKILL.md',
'# /tester Skill

테스트 스킬. 테스트 작성 및 실행.

## 사용법

```
/tester
/tester --unit
/tester --arch
```

## 실행 흐름

1. `module_context(class_type="TEST")` → 테스트 규칙 조회
2. 테스트 코드 작성
3. `./gradlew test` 실행
4. 결과 리포트
',
'테스트 스킬. 테스트 작성 및 실행.',
NULL, 5, true, NOW(), NOW());


-- =====================================================
-- RULE 카테고리 (1개 - MCP 동적 조회 가이드)
-- =====================================================

INSERT INTO config_file_template (
    tech_stack_id, architecture_id, tool_type, category,
    file_path, file_name, content, description, variables,
    display_order, is_required, created_at, updated_at
) VALUES

(1, NULL, 'CLAUDE', 'RULE',
 '.claude/rules/', 'convention-guide.md',
'# Convention Guide

> ⚠️ **규칙은 하드코딩되지 않습니다. MCP를 통해 동적으로 조회하세요.**

## 규칙 조회 방법

### 1. 레이어 목록 먼저 조회
```python
list_tech_stacks()
# → layers: ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]
```

### 2. 전체 규칙 개요
```python
validation_context(layers=[...])  # 조회된 레이어 사용
```

### 3. 레이어별 상세 규칙
```python
get_layer_detail(layer_code="DOMAIN")
```

### 4. 특정 규칙 상세
```python
get_rule(rule_code="DOM-AGG-001")
```

### 5. 클래스별 템플릿 + 규칙
```python
module_context(module_id=1, class_type="AGGREGATE")
```

## Serena 캐싱

조회된 규칙은 Serena Memory에 캐싱하여 재사용:

```python
# 캐시 키: convention-{layer}-{class_type}
serena.write_memory("convention-domain-aggregate", rules)
serena.read_memory("convention-domain-aggregate")
```

## Zero-Tolerance 빠른 참조

> 상세 규칙은 `validation_context()`로 조회

| Layer | 핵심 규칙 |
|-------|----------|
| DOMAIN | Lombok 금지, Getter 체이닝 금지 |
| APPLICATION | @Transactional 내 외부 API 금지 |
| ADAPTER_OUT | JPA 관계 어노테이션 금지 |
| ADAPTER_IN | MockMvc 금지 |
',
'Convention 가이드. MCP 동적 조회 안내.',
NULL, 1, true, NOW(), NOW());


-- =====================================================
-- HOOK 카테고리 제거됨
-- validation_context()가 동일한 역할 수행
-- =====================================================
