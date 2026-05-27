---
layout: default
title: 3-Phase 워크플로우
---

# 3-Phase 워크플로우

AI 가 conventionHub 의 컨벤션을 따라 코드를 생성/검증하는 권장 흐름.

---

## 3-Phase 개요

```
1️⃣ PLANNING    planning_context(layers=[...])
                → 어떤 컴포넌트를 어디에 만들지 결정

2️⃣ EXECUTION   module_context(module_id=N, class_type_id=M)
                → 템플릿 + 규칙 기반 코드 생성

3️⃣ VALIDATION  validation_context(layers=[...])
                → Zero-Tolerance 패턴 검증
```

각 단계의 MCP Tool 상세는 [mcp-tools.md](mcp-tools.md) 참고.

---

## 실제 호출 예시

```python
# 0. ID 알아내기
tech_stacks = list_tech_stacks()
# → layers: ["DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN", "BOOTSTRAP"]
# → class_types: [{id: 1, code: "AGGREGATE"}, {id: 2, code: "VALUE_OBJECT"}, ...]

# 1. 개발 계획 수립
planning = planning_context(layers=["DOMAIN"])
# → module_id=1 (domain), packages: [aggregate, vo, event, ...]

# 2. Aggregate 코드 생성
context = module_context(module_id=1, class_type_id=1)
# → 템플릿, 규칙 조회

# 3. 코드 검증
validation = validation_context(layers=["DOMAIN"])
# → Zero-Tolerance 규칙 확인
```

---

## Index + Lookup 패턴

`/sc:load` 시 가벼운 규칙 인덱스를 Serena Memory 에 캐싱. 개발 중 필요한 규칙만 lazy 조회.

```
Phase 1: /sc:load 시
  list_rules() → 경량 인덱스 (code, name, severity 만)
  ↓
  write_memory("spring_rules_index", ...)

Phase 2: 개발 중 필요 시
  read_memory("spring_rule_{code}") → 캐시 확인
  ↓ (캐시 미스 시)
  get_rule(code) → 상세 조회
  ↓
  write_memory("spring_rule_{code}", ...)
```

> 컨텍스트 비용 절감 의도였지만 [운영 회고](../README.md#-운영-회고) 의 한계 (2) 가 정확히 이 지점. 인덱스도 결국 컨텍스트를 먹고, lazy 조회도 결국 LLM 이 그걸 따를지는 별개 문제.

---

## 관련

- [MCP Tools 상세](mcp-tools.md)
- [시작하기 — /sc:init, /sc:load](setup.md#claude-code-슬래시-커맨드)
- [ADR-0005](adr/0005-operational-retrospective.md) — Index + Lookup 패턴이 본질 해결이 아니었던 이유
