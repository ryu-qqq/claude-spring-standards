---
layout: default
title: 데이터 모델
---

# 데이터 모델

conventionHub 가 컨벤션을 저장하는 계층 구조와 ERD.

---

## 계층 구조

```
TechStack (기술 스택)
│   예: "Spring Boot 3.5 + Java 21", "Node.js 20 + TypeScript"
│
└── Architecture (아키텍처)
    │   예: "Hexagonal", "Layered", "Clean Architecture"
    │
    ├── LayerDependencyRule (레이어 의존 규칙)
    │   예: "Domain → 어디에도 의존 금지", "Application → Domain 만 의존"
    │
    ├── ClassTypeCategory (클래스 타입 카테고리)
    │   │   예: "DOMAIN_TYPES", "APPLICATION_TYPES", "ADAPTER_TYPES"
    │   │
    │   └── ClassType (클래스 타입)
    │       예: "AGGREGATE", "VALUE_OBJECT", "USE_CASE", "PORT_IN"
    │
    └── Layer (레이어)
        │   예: "DOMAIN", "APPLICATION", "ADAPTER_OUT", "ADAPTER_IN"
        │
        ├── Convention (컨벤션 그룹)
        │   │   예: "Aggregate 규칙", "UseCase 규칙", "Repository 규칙"
        │   │
        │   └── CodingRule (코딩 규칙)
        │       │   예: "Lombok 금지", "Setter 금지", "findAll 금지"
        │       │
        │       ├── RuleExample (GOOD/BAD 예시)
        │       ├── ChecklistItem (체크리스트)
        │       └── ZeroTolerancePattern (필수 규칙 패턴)
        │
        └── Module (Gradle/NPM 모듈)
            │   예: "domain", "adapter-in/rest-api"
            │
            ├── PackageStructure (패키지 구조)
            │       → PackagePurpose (패키지 목적)
            │
            └── ClassTemplate (클래스 템플릿)
                    → ClassType FK 참조
```

---

## ERD 요약

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

## 핵심 엔티티 요약

| 엔티티 | 역할 |
|---|---|
| `TechStack` | 기술 스택 정의 (언어 + 프레임워크) |
| `Architecture` | 아키텍처 패턴 (Hexagonal, Layered 등) |
| `Layer` | 아키텍처의 레이어 (DOMAIN, APPLICATION 등) |
| `ClassTypeCategory` | 클래스 타입 그룹 (DOMAIN_TYPES 등) |
| `ClassType` | 구체 클래스 타입 (AGGREGATE, USE_CASE 등) |
| `Module` | Gradle/NPM 모듈 단위 |
| `Convention` | 컨벤션 그룹 (Aggregate 규칙 묶음 등) |
| `CodingRule` | 단일 코딩 규칙 |
| `RuleExample` | 규칙의 GOOD/BAD 예시 |
| `ZeroTolerancePattern` | 자동 reject 가능한 detection regex |

---

## 관련

- [REST API Endpoints](api.md) — 이 엔티티들의 CRUD API
- [setup.md — DB seed SQL](setup.md#규칙-데이터-등록) — 초기 데이터 일괄 등록
