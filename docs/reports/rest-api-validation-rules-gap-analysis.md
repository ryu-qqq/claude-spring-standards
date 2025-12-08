# REST API Layer Convention Validation Rules Gap Analysis Report

> **작성일**: 2025-12-08
> **목적**: Serena Memory `convention-rest-api-layer-validation-rules.md`와 실제 코딩 컨벤션 문서 비교 분석
> **분석 대상**: `docs/coding_convention/01-adapter-in-layer/rest-api/`

---

## 1. 전체 요약

| 카테고리 | 상태 | 기존 규칙 수 | 누락 규칙 수 | 비고 |
|----------|------|-------------|-------------|------|
| **CONTROLLER** | ✅ 대체로 일치 | 11개 | 5개 | 일부 누락/추가 필요 |
| **COMMAND_DTO** | ✅ 대체로 일치 | 10개 | 3개 | 일부 누락 |
| **QUERY_DTO** | ✅ 대체로 일치 | 8개 | 4개 | 일부 누락 |
| **RESPONSE_DTO** | ✅ 대체로 일치 | 8개 | 4개 | 일부 누락 |
| **MAPPER** | ⚠️ 누락 사항 있음 | 8개 | 8개 | 중요 규칙 누락 |
| **ERROR** | ✅ 대체로 일치 | 6개 | 3개 | 일부 추가 필요 |
| **SECURITY** | ⚠️ 대폭 누락 | 5개 | 10개 | 많은 규칙 누락 |
| **OPENAPI** | ✅ 대체로 일치 | 5개 | 5개 | 일부 누락 |
| **총계** | - | 61개 | **42개** | - |

---

## 2. 카테고리별 상세 분석

### 2.1 CONTROLLER 카테고리

#### ✅ 기존 포함된 규칙 (검증됨)
- CTRL-001: `ResponseEntity<ApiResponse<T>>` 래핑 필수
- CTRL-002: `@Valid` 어노테이션 필수
- CTRL-003: DELETE 메서드 금지
- CTRL-004: `@Transactional` 금지
- CTRL-005: 비즈니스 로직 금지
- CTRL-006: try-catch 금지
- CTRL-007: Lombok 금지
- CTRL-008: `@RestController` 필수
- CTRL-009: `@Validated` 필수 (PathVariable/RequestParam 검증시)
- CTRL-010: HTTP 상태 코드 매핑
- CTRL-011: Javadoc 필수

#### ❌ 누락된 규칙

| ID | 규칙명 | 설명 | 출처 | severity |
|----|--------|------|------|----------|
| CTRL-012 | Mapper DI 필수 | Mapper를 생성자 주입으로 DI | controller-guide.md:44 | ERROR |
| CTRL-013 | UseCase 직접 의존 | UseCase를 생성자 주입으로 직접 의존 | controller-guide.md:45 | ERROR |
| CTRL-014 | RESTful URI 설계 | 리소스 기반 URI (명사 복수형), 동사 금지 | controller-guide.md:46 | WARNING |
| CTRL-015 | Domain 직접 호출 금지 | Domain 객체 직접 생성/조작 금지 | controller-guide.md:34 | ERROR |
| CTRL-016 | Properties로 경로 관리 | 엔드포인트 하드코딩 금지, `${api.endpoints.*}` 사용 | controller-guide.md:420-425 | WARNING |

#### 추가할 JSON 규칙

```json
{
  "id": "CTRL-012",
  "name": "Mapper DI 필수",
  "severity": "ERROR",
  "description": "Mapper를 생성자 주입으로 DI해야 합니다",
  "pattern": {
    "type": "field_type",
    "required_pattern": ".*ApiMapper$"
  },
  "autofix": false
},
{
  "id": "CTRL-013",
  "name": "UseCase 직접 의존",
  "severity": "ERROR",
  "description": "UseCase를 생성자 주입으로 직접 의존해야 합니다",
  "pattern": {
    "type": "field_type",
    "required_pattern": "(UseCase|QueryService)$"
  },
  "autofix": false
},
{
  "id": "CTRL-014",
  "name": "RESTful URI 설계",
  "severity": "WARNING",
  "description": "URI는 명사 복수형 사용, 동사 금지 (/createOrder ❌ → POST /orders ✅)",
  "pattern": {
    "type": "annotation_value",
    "forbidden_pattern": "@.*Mapping.*\\(.*/(create|update|delete|get|find|search)[A-Z]"
  },
  "autofix": false
},
{
  "id": "CTRL-015",
  "name": "Domain 직접 호출 금지",
  "severity": "ERROR",
  "description": "Controller에서 Domain 객체 직접 생성/조작 금지",
  "pattern": {
    "type": "import",
    "forbidden_pattern": "import.*\\.domain\\.[a-z]+\\.aggregate\\."
  },
  "autofix": false
},
{
  "id": "CTRL-016",
  "name": "Properties로 경로 관리",
  "severity": "WARNING",
  "description": "엔드포인트는 Properties로 관리, 하드코딩 금지",
  "pattern": {
    "type": "annotation_value",
    "recommended_pattern": "@RequestMapping.*\\$\\{api\\.endpoints\\."
  },
  "autofix": false
}
```

---

### 2.2 COMMAND_DTO 카테고리

#### ✅ 기존 포함된 규칙 (검증됨)
- CMD-001~010: 대부분 일치

#### ❌ 누락된 규칙

| ID | 규칙명 | 설명 | 출처 | severity |
|----|--------|------|------|----------|
| CMD-011 | Compact Constructor 활용 | 불변 리스트 변환(`List.copyOf()`), 추가 검증 로직 | command-dto-guide.md:73-78 | WARNING |
| CMD-012 | Nested Record 사용 | 복잡한 구조는 Nested Record로 표현 | command-dto-guide.md:17 | INFO |
| CMD-013 | 액션 명확 | Command 이름에 액션 포함 (Create, Update, Cancel 등) | command-dto-guide.md:16 | WARNING |

#### 추가할 JSON 규칙

```json
{
  "id": "CMD-011",
  "name": "Compact Constructor 활용",
  "severity": "WARNING",
  "description": "불변 리스트 변환(List.copyOf()), 추가 검증 로직은 Compact Constructor에서 처리",
  "pattern": {
    "type": "code_pattern",
    "recommended": ["List.copyOf(", "public.*{$"]
  },
  "autofix": false
},
{
  "id": "CMD-012",
  "name": "Nested Record 사용",
  "severity": "INFO",
  "description": "복잡한 구조는 Nested Record로 표현 (예: OrderItemRequest)",
  "pattern": {
    "type": "structure",
    "recommended": "nested_record_for_complex_structure"
  },
  "autofix": false
},
{
  "id": "CMD-013",
  "name": "액션 명확",
  "severity": "WARNING",
  "description": "Command 이름에 액션 포함 (Create, Update, Cancel, Approve 등)",
  "pattern": {
    "type": "naming",
    "pattern": "^(Create|Update|Modify|Change|Cancel|Confirm|Approve|Reject|Complete|Register|Place|Enroll)[A-Z][a-zA-Z]*ApiRequest$"
  },
  "autofix": false
}
```

---

### 2.3 QUERY_DTO 카테고리

#### ❌ 누락된 규칙

| ID | 규칙명 | 설명 | 출처 | severity |
|----|--------|------|------|----------|
| QRY-009 | Optional 필드 | 조회 조건은 대부분 Optional (null 허용), `@NotNull` 지양 | query-dto-guide.md:12 | WARNING |
| QRY-010 | 기본값 설정 | Compact Constructor에서 기본값 설정 (page, size 등) | query-dto-guide.md:76-84 | WARNING |
| QRY-011 | 날짜 범위 검증 | startDate/endDate 범위 검증 (startDate <= endDate) | query-dto-guide.md:81-83 | WARNING |
| QRY-012 | Cursor 페이징 지원 | Cursor 기반 페이징 필드 (cursor, size) 지원 | query-dto-guide.md:180-191 | INFO |

#### 추가할 JSON 규칙

```json
{
  "id": "QRY-009",
  "name": "Optional 필드",
  "severity": "WARNING",
  "description": "조회 조건은 대부분 Optional (null 허용), 필수 필드에만 @NotNull 사용",
  "pattern": {
    "type": "annotation",
    "discouraged_pattern": "대부분의 필드에 @NotNull"
  },
  "autofix": false
},
{
  "id": "QRY-010",
  "name": "기본값 설정",
  "severity": "WARNING",
  "description": "Compact Constructor에서 기본값 설정 (page=0, size=20 등)",
  "pattern": {
    "type": "code_pattern",
    "recommended": ["page = page == null", "size = size == null"]
  },
  "autofix": false
},
{
  "id": "QRY-011",
  "name": "날짜 범위 검증",
  "severity": "WARNING",
  "description": "startDate/endDate 범위 검증 (startDate.isAfter(endDate) 체크)",
  "pattern": {
    "type": "code_pattern",
    "recommended": ["startDate != null && endDate != null && startDate.isAfter(endDate)"]
  },
  "autofix": false
},
{
  "id": "QRY-012",
  "name": "Cursor 페이징 지원",
  "severity": "INFO",
  "description": "Cursor 기반 페이징 필드 (cursor, size) 지원 권장",
  "pattern": {
    "type": "field_name",
    "recommended": ["cursor", "size"]
  },
  "autofix": false
}
```

---

### 2.4 RESPONSE_DTO 카테고리

#### ❌ 누락된 규칙

| ID | 규칙명 | 설명 | 출처 | severity |
|----|--------|------|------|----------|
| RSP-009 | Compact Constructor | 불변 컬렉션 (`List.copyOf()`) 적용 | response-dto-guide.md:78-80 | WARNING |
| RSP-010 | SliceApiResponse 사용 | Cursor 기반 페이징 응답에 SliceApiResponse 사용 | response-dto-guide.md:119-182 | ERROR |
| RSP-011 | PageApiResponse 사용 | Offset 기반 페이징 응답에 PageApiResponse 사용 | response-dto-guide.md:184-229 | ERROR |
| RSP-012 | Domain 직접 노출 금지 | Domain Entity를 Response DTO로 직접 반환 금지 | response-dto-guide.md:27 | ERROR |

#### 추가할 JSON 규칙

```json
{
  "id": "RSP-009",
  "name": "Compact Constructor",
  "severity": "WARNING",
  "description": "불변 컬렉션 적용 (List.copyOf()) - Compact Constructor에서 처리",
  "pattern": {
    "type": "code_pattern",
    "recommended": ["List.copyOf(", "items = List.copyOf(items)"]
  },
  "autofix": false
},
{
  "id": "RSP-010",
  "name": "SliceApiResponse 사용",
  "severity": "ERROR",
  "description": "Cursor 기반 페이징 응답에 SliceApiResponse 사용 (무한 스크롤)",
  "pattern": {
    "type": "type_usage",
    "required_for": "cursor_pagination",
    "required": ["SliceApiResponse<"]
  },
  "autofix": false
},
{
  "id": "RSP-011",
  "name": "PageApiResponse 사용",
  "severity": "ERROR",
  "description": "Offset 기반 페이징 응답에 PageApiResponse 사용 (관리자 테이블)",
  "pattern": {
    "type": "type_usage",
    "required_for": "offset_pagination",
    "required": ["PageApiResponse<"]
  },
  "autofix": false
},
{
  "id": "RSP-012",
  "name": "Domain 직접 노출 금지",
  "severity": "ERROR",
  "description": "Domain Entity를 Response DTO로 직접 반환 금지",
  "pattern": {
    "type": "return_type",
    "forbidden_pattern": "ResponseEntity<.*(?!ApiResponse).*>"
  },
  "autofix": false
}
```

---

### 2.5 MAPPER 카테고리 ⚠️ (중요)

#### ❌ 누락된 규칙 (8개)

| ID | 규칙명 | 설명 | 출처 | severity |
|----|--------|------|------|----------|
| MAP-009 | 기본값 설정 금지 | 기본값 설정은 Controller 책임 | mapper-guide.md:145-149 | ERROR |
| MAP-010 | 검증 로직 금지 | 검증은 Bean Validation 사용 | mapper-guide.md:152-158 | ERROR |
| MAP-011 | 비즈니스 로직 금지 | 비즈니스 로직은 UseCase 책임 | mapper-guide.md:161-168 | ERROR |
| MAP-012 | 상태 변환 로직 금지 | 상태 변환은 Application Layer 책임 | mapper-guide.md:362-370 | ERROR |
| MAP-013 | 계산 로직 금지 | 계산은 Domain/UseCase 책임 | mapper-guide.md:372-379 | ERROR |
| MAP-014 | 조건부 필드 설정 금지 | 조건부 필드 설정은 Application Layer 책임 | mapper-guide.md:381-387 | ERROR |
| MAP-015 | Cursor/Offset 자동 판단 | 페이징 타입 자동 설정 (cursor 유무로 판단) | mapper-guide.md:209-235 | INFO |
| MAP-016 | Private Helper 메서드 허용 | Nested 변환용 Private Helper 메서드 허용 | mapper-guide.md:566-589 | INFO |

#### 추가할 JSON 규칙

```json
{
  "id": "MAP-009",
  "name": "기본값 설정 금지",
  "severity": "ERROR",
  "description": "Mapper에서 기본값 설정 금지. Controller 또는 DTO Compact Constructor 책임",
  "pattern": {
    "type": "code_pattern",
    "forbidden": ["!= null \\? .* : .*default", "== null \\? .* :"]
  },
  "autofix": false
},
{
  "id": "MAP-010",
  "name": "검증 로직 금지",
  "severity": "ERROR",
  "description": "Mapper에서 검증 로직 금지. Bean Validation (@Valid, @NotNull 등) 사용",
  "pattern": {
    "type": "code_pattern",
    "forbidden": ["if.*<.*throw", "if.*>.*throw", "if.*==.*throw", "if.*!=.*throw"]
  },
  "autofix": false
},
{
  "id": "MAP-011",
  "name": "비즈니스 로직 금지",
  "severity": "ERROR",
  "description": "Mapper에서 비즈니스 로직 금지. UseCase 책임",
  "pattern": {
    "type": "code_pattern",
    "forbidden": ["\\.multiply\\(", "\\.add\\(", "\\.subtract\\(", "calculate", "compute"]
  },
  "autofix": false
},
{
  "id": "MAP-012",
  "name": "상태 변환 로직 금지",
  "severity": "ERROR",
  "description": "Mapper에서 상태 변환 로직 금지 (switch status). Application Layer 책임",
  "pattern": {
    "type": "code_pattern",
    "forbidden": ["switch.*status", "case \"PLACED\"", "case \"CANCELLED\""]
  },
  "autofix": false
},
{
  "id": "MAP-013",
  "name": "계산 로직 금지",
  "severity": "ERROR",
  "description": "Mapper에서 계산 로직 금지. Domain/UseCase 책임",
  "pattern": {
    "type": "code_pattern",
    "forbidden": ["\\* 0\\.", "/ 100", "Math\\."]
  },
  "autofix": false
},
{
  "id": "MAP-014",
  "name": "조건부 필드 설정 금지",
  "severity": "ERROR",
  "description": "Mapper에서 조건부 필드 설정 금지. Application Layer 책임",
  "pattern": {
    "type": "code_pattern",
    "forbidden": ["\\.equals\\(.*\\) \\?", "status\\.equals.*\\?.*:.*null"]
  },
  "autofix": false
},
{
  "id": "MAP-015",
  "name": "Cursor/Offset 자동 판단",
  "severity": "INFO",
  "description": "Cursor 유무에 따라 페이징 타입 자동 설정 (ofCursor/ofOffset)",
  "pattern": {
    "type": "code_pattern",
    "recommended": ["request.cursor() != null", "isCursor", "ofCursor", "ofOffset"]
  },
  "autofix": false
},
{
  "id": "MAP-016",
  "name": "Private Helper 메서드 허용",
  "severity": "INFO",
  "description": "Nested 변환용 Private Helper 메서드 허용 (toCustomerInfo, toLineItems 등)",
  "pattern": {
    "type": "method_modifier",
    "allowed": ["private.*to[A-Z]"]
  },
  "autofix": false
}
```

---

### 2.6 ERROR 카테고리

#### ❌ 누락된 규칙

| ID | 규칙명 | 설명 | 출처 | severity |
|----|--------|------|------|----------|
| ERR-007 | 로깅 레벨 전략 | 5xx → ERROR, 404 → DEBUG, 기타 4xx → WARN | error-guide.md:420-425 | WARNING |
| ERR-008 | OCP 준수 | ErrorMapperRegistry 패턴으로 확장성 보장 | error-guide.md:19 | ERROR |
| ERR-009 | ErrorMapper supports() | 예외 타입 또는 에러 코드 prefix로 매칭 | error-guide.md:247-250 | ERROR |

#### 추가할 JSON 규칙

```json
{
  "id": "ERR-007",
  "name": "로깅 레벨 전략",
  "severity": "WARNING",
  "description": "5xx → ERROR (스택트레이스 포함), 404 → DEBUG, 기타 4xx → WARN",
  "pattern": {
    "type": "code_pattern",
    "required": ["log.error.*5xx", "log.debug.*NOT_FOUND", "log.warn.*4xx"]
  },
  "autofix": false
},
{
  "id": "ERR-008",
  "name": "OCP 준수",
  "severity": "ERROR",
  "description": "ErrorMapperRegistry 패턴으로 확장성 보장. GlobalExceptionHandler 직접 분기 금지",
  "pattern": {
    "type": "code_pattern",
    "forbidden": ["if.*ex\\.code\\(\\)\\.equals"],
    "required": ["errorMapperRegistry.map"]
  },
  "autofix": false
},
{
  "id": "ERR-009",
  "name": "ErrorMapper supports()",
  "severity": "ERROR",
  "description": "ErrorMapper.supports()에서 예외 타입 또는 에러 코드 prefix로 매칭",
  "pattern": {
    "type": "method_implementation",
    "required": ["supports(DomainException ex)"]
  },
  "autofix": false
}
```

---

### 2.7 SECURITY 카테고리 ⚠️ (대폭 누락)

#### ❌ 누락된 규칙 (10개)

| ID | 규칙명 | 설명 | 출처 | severity |
|----|--------|------|------|----------|
| SEC-006 | JWT Silent Refresh | Access Token 만료 시 Refresh Token으로 자동 갱신 | security-guide.md:329-424 | ERROR |
| SEC-007 | MdcLoggingFilter | Request ID 추적 필수 (X-Request-Id 헤더) | security-guide.md:729-808 | ERROR |
| SEC-008 | SecurityContextAuthenticator | SecurityContext 인증 설정 분리 | security-guide.md:396-399 | WARNING |
| SEC-009 | Method Security | `@PreAuthorize` 리소스 소유자 검증 | security-guide.md:813-878 | ERROR |
| SEC-010 | AuthenticationErrorHandler | 인증/인가 에러 RFC 7807 ProblemDetail 처리 | security-guide.md:453-602 | ERROR |
| SEC-011 | SessionCreationPolicy.STATELESS | JWT 기반 Stateless 인증 | security-guide.md:273-275 | ERROR |
| SEC-012 | CSRF 비활성화 | JWT 사용 시 CSRF 비활성화 | security-guide.md:270-271 | ERROR |
| SEC-013 | Cookie SameSite=Lax | CSRF 방지를 위한 SameSite 설정 | security-guide.md:616-714 | ERROR |
| SEC-014 | Cookie Secure=true | 운영 환경 HTTPS 전용 쿠키 | security-guide.md:703-705 | ERROR |
| SEC-015 | SecurityPaths 그룹화 | 보안 정책별 경로 그룹화 (PUBLIC, ADMIN 등) | security-guide.md:161-221 | WARNING |

#### 추가할 JSON 규칙

```json
{
  "id": "SEC-006",
  "name": "JWT Silent Refresh",
  "severity": "ERROR",
  "description": "Access Token 만료 시 Refresh Token으로 자동 갱신 (Silent Refresh)",
  "pattern": {
    "type": "code_pattern",
    "required": ["isAccessTokenExpired", "trySilentRefresh", "validateRefreshToken"]
  },
  "autofix": false
},
{
  "id": "SEC-007",
  "name": "MdcLoggingFilter",
  "severity": "ERROR",
  "description": "Request ID 추적 필수 (X-Request-Id 헤더, MDC 설정)",
  "pattern": {
    "type": "class_exists",
    "required": ["MdcLoggingFilter"]
  },
  "autofix": false
},
{
  "id": "SEC-008",
  "name": "SecurityContextAuthenticator",
  "severity": "WARNING",
  "description": "SecurityContext 인증 설정 로직 분리 (별도 컴포넌트)",
  "pattern": {
    "type": "class_exists",
    "recommended": ["SecurityContextAuthenticator"]
  },
  "autofix": false
},
{
  "id": "SEC-009",
  "name": "Method Security",
  "severity": "ERROR",
  "description": "@PreAuthorize로 리소스 소유자 검증 (Controller에서 직접 인가 로직 금지)",
  "pattern": {
    "type": "annotation",
    "required_for": "owner_verification",
    "required": ["@PreAuthorize"],
    "forbidden": ["authentication.getName()", "principal."]
  },
  "autofix": false
},
{
  "id": "SEC-010",
  "name": "AuthenticationErrorHandler",
  "severity": "ERROR",
  "description": "인증/인가 에러 RFC 7807 ProblemDetail 형식으로 처리",
  "pattern": {
    "type": "interface_implementation",
    "required": ["AuthenticationEntryPoint", "AccessDeniedHandler"]
  },
  "autofix": false
},
{
  "id": "SEC-011",
  "name": "SessionCreationPolicy.STATELESS",
  "severity": "ERROR",
  "description": "JWT 기반 Stateless 인증 설정",
  "pattern": {
    "type": "code_pattern",
    "required": ["SessionCreationPolicy.STATELESS"]
  },
  "autofix": false
},
{
  "id": "SEC-012",
  "name": "CSRF 비활성화",
  "severity": "ERROR",
  "description": "JWT 사용 시 CSRF 비활성화 (AbstractHttpConfigurer::disable)",
  "pattern": {
    "type": "code_pattern",
    "required": [".csrf(AbstractHttpConfigurer::disable)"]
  },
  "autofix": false
},
{
  "id": "SEC-013",
  "name": "Cookie SameSite=Lax",
  "severity": "ERROR",
  "description": "CSRF 방지를 위한 SameSite=Lax 설정",
  "pattern": {
    "type": "code_pattern",
    "required": ["SameSite=Lax", "SameSite=Strict"]
  },
  "autofix": false
},
{
  "id": "SEC-014",
  "name": "Cookie Secure=true (Production)",
  "severity": "ERROR",
  "description": "운영 환경에서 Secure=true 설정 (HTTPS 전용)",
  "pattern": {
    "type": "config_property",
    "production_required": ["security.cookie.secure=true"]
  },
  "autofix": false
},
{
  "id": "SEC-015",
  "name": "SecurityPaths 그룹화",
  "severity": "WARNING",
  "description": "보안 정책별 경로 그룹화 (PUBLIC_ENDPOINTS, ADMIN_ENDPOINTS 등)",
  "pattern": {
    "type": "class_exists",
    "recommended": ["SecurityPaths"]
  },
  "autofix": false
}
```

---

### 2.8 OPENAPI 카테고리

#### ❌ 누락된 규칙

| ID | 규칙명 | 설명 | 출처 | severity |
|----|--------|------|------|----------|
| OAI-006 | description 한국어 | 사용자 친화적 한국어 문서화 | openapi-guide.md:19 | WARNING |
| OAI-007 | Enum @Schema(enumAsRef) | Enum 참조 방식 통일 | openapi-guide.md:192-213 | WARNING |
| OAI-008 | example 필수 | 사용 예시 필수 | openapi-guide.md:28 | WARNING |
| OAI-009 | @Parameter | PathVariable/RequestParam 설명 필수 | openapi-guide.md:119-133 | WARNING |
| OAI-010 | OpenApiConfig | 전역 OpenAPI 설정 클래스 필수 | openapi-guide.md:286-314 | INFO |

#### 추가할 JSON 규칙

```json
{
  "id": "OAI-006",
  "name": "description 한국어",
  "severity": "WARNING",
  "description": "description은 한국어로 작성 (사용자 친화적 문서화)",
  "pattern": {
    "type": "annotation_attribute",
    "recommended": ["description = \".*[가-힣].*\""]
  },
  "autofix": false
},
{
  "id": "OAI-007",
  "name": "Enum @Schema(enumAsRef)",
  "severity": "WARNING",
  "description": "Enum에 @Schema(enumAsRef = true) 적용으로 참조 방식 통일",
  "pattern": {
    "type": "annotation",
    "target": "enum",
    "required": ["@Schema(enumAsRef = true)"]
  },
  "autofix": false
},
{
  "id": "OAI-008",
  "name": "example 필수",
  "severity": "WARNING",
  "description": "@Schema에 example 속성 필수 (사용 예시 제공)",
  "pattern": {
    "type": "annotation_attribute",
    "required": ["example = "]
  },
  "autofix": false
},
{
  "id": "OAI-009",
  "name": "@Parameter",
  "severity": "WARNING",
  "description": "PathVariable/RequestParam에 @Parameter(description, example) 적용",
  "pattern": {
    "type": "method_parameter_annotation",
    "target": ["@PathVariable", "@RequestParam"],
    "required": ["@Parameter"]
  },
  "autofix": false
},
{
  "id": "OAI-010",
  "name": "OpenApiConfig",
  "severity": "INFO",
  "description": "전역 OpenAPI 설정 클래스 필수 (Info, Servers, SecuritySchemes)",
  "pattern": {
    "type": "class_exists",
    "recommended": ["OpenApiConfig"]
  },
  "autofix": false
}
```

---

## 3. 우선순위별 보완 권장

| 우선순위 | 카테고리 | 보완 필요 규칙 수 | 권장 조치 |
|----------|----------|------------------|-----------|
| 🔴 **높음** | SECURITY | 10개 | JWT, Silent Refresh, Method Security 등 핵심 보안 규칙 즉시 추가 |
| 🔴 **높음** | MAPPER | 8개 | "금지" 규칙들 (기본값, 검증, 비즈니스 로직 금지) 추가 |
| 🟡 **중간** | CONTROLLER | 5개 | DI, RESTful URI, Properties 관리 규칙 추가 |
| 🟡 **중간** | RESPONSE_DTO | 4개 | Slice/Page ApiResponse, Domain 노출 금지 규칙 추가 |
| 🟢 **낮음** | QUERY_DTO | 4개 | Optional 필드, 기본값 설정 규칙 추가 |
| 🟢 **낮음** | OPENAPI | 5개 | 한국어 description, example 규칙 추가 |
| 🟢 **낮음** | ERROR | 3개 | 로깅 전략, OCP 규칙 추가 |
| 🟢 **낮음** | COMMAND_DTO | 3개 | Compact Constructor, 액션 명확 규칙 추가 |

---

## 4. 다음 단계

1. **Phase 1**: SECURITY, MAPPER 카테고리 규칙 추가 (높음 우선순위)
2. **Phase 2**: CONTROLLER, RESPONSE_DTO 카테고리 규칙 추가 (중간 우선순위)
3. **Phase 3**: 나머지 카테고리 규칙 추가 (낮음 우선순위)
4. **Phase 4**: 검증 결과 JSON 출력 스키마 업데이트

---

**작성자**: Claude Code Analysis
**최종 수정일**: 2025-12-08
**버전**: 1.0.0
