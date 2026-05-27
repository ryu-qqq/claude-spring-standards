---
layout: default
title: REST API Endpoints
---

# REST API Endpoints

conventionHub 의 Spring API 가 노출하는 REST endpoints. MCP 서버 (`mcp-lambda-server/`) 가 내부적으로 이 API 를 호출.

---

## CodingRule API

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/v1/templates/coding-rules` | 규칙 목록 (커서 기반) |
| GET | `/api/v1/templates/coding-rules/index` | 규칙 인덱스 (경량) |
| GET | `/api/v1/templates/coding-rules/{id}` | 규칙 상세 |
| POST | `/api/v1/templates/coding-rules` | 규칙 생성 |
| PUT | `/api/v1/templates/coding-rules/{id}` | 규칙 수정 |

---

## ClassType API

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/v1/templates/class-types` | 클래스 타입 목록 (커서 기반) |
| POST | `/api/v1/templates/class-types` | 클래스 타입 생성 |
| PUT | `/api/v1/templates/class-types/{id}` | 클래스 타입 수정 |

---

## ClassTypeCategory API

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/v1/templates/class-type-categories` | 카테고리 목록 (커서 기반) |
| POST | `/api/v1/templates/class-type-categories` | 카테고리 생성 |
| PUT | `/api/v1/templates/class-type-categories/{id}` | 카테고리 수정 |

---

## MCP API (MCP 서버 전용)

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/v1/templates/mcp/planning-context` | Planning Context |
| GET | `/api/v1/templates/mcp/module-context` | Module Context |
| GET | `/api/v1/templates/mcp/validation-context` | Validation Context |
| GET | `/api/v1/templates/mcp/config-files` | 설정 파일 템플릿 |
| GET | `/api/v1/templates/mcp/feedback-schema` | 피드백 스키마 (유효값 포함) |

MCP 서버는 이 endpoints 의 응답을 가공해서 [MCP Tools](mcp-tools.md) 로 노출.

---

## 페이지네이션

목록 API 는 **커서 기반** 페이지네이션을 사용. `?cursor={lastId}&size=20` 형태.

---

## 관련

- [데이터 모델](data-model.md) — API 가 다루는 엔티티 구조
- [MCP Tools](mcp-tools.md) — 위 API 를 가공해서 노출하는 MCP 레이어
