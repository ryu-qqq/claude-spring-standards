# Coding Convention Seed Data 현황 및 작업 가이드

> MCP Server를 통해 LLM(Claude/Cursor)에 제공될 코딩 컨벤션 데이터 현황
>
> **이 문서의 목적**: 다른 세션에서 새 레이어 작업 시 참고할 수 있는 완전한 가이드

---

## 목차

1. [작업 워크플로우 가이드](#1-작업-워크플로우-가이드)
2. [도메인 연관관계 (ERD)](#2-도메인-연관관계-erd)
3. [FK 연결 전략](#3-fk-연결-전략)
4. [버전 번호 할당 규칙](#4-버전-번호-할당-규칙)
5. [REST-API 모듈 (V100번대) - 완료](#5-rest-api-모듈-v100번대---완료)
6. [Domain 모듈 (V200번대) - 예정](#6-domain-모듈-v200번대---예정)
7. [Application 모듈 (V300번대) - 예정](#7-application-모듈-v300번대---예정)
8. [Persistence 모듈 (V400번대) - 예정](#8-persistence-모듈-v400번대---예정)
9. [다음 작업](#9-다음-작업)

---

## 1. 작업 워크플로우 가이드

### 1.1 새 레이어 시드 데이터 작업 순서

**⚠️ 중요: FK 의존성 때문에 반드시 아래 순서대로 진행해야 함**

```
┌─────────────────────────────────────────────────────────────────────┐
│                    시드 데이터 삽입 순서                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1️⃣ package_purpose (레이어별 패키지 용도 정의)                      │
│       ↓                                                             │
│  2️⃣ package_structure (구체적인 패키지 경로)                         │
│       ↓                                                             │
│  3️⃣ convention (레이어 컨벤션 정의)                                  │
│       ↓                                                             │
│  4️⃣ coding_rule (코딩 규칙 - structure_id 참조)                     │
│       ↓                                                             │
│  5️⃣ class_template (클래스 템플릿 - structure_id 참조)              │
│       ↓                                                             │
│  6️⃣ rule_example (Good/Bad 예시 - rule_id 참조)                     │
│       ↓                                                             │
│  7️⃣ resource_template (yml, properties 등 - module_id 참조)        │
│       ↓                                                             │
│  8️⃣ archunit_test (아키텍처 테스트 - structure_id 참조)              │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2 마이그레이션 파일 네이밍 규칙

```
V{버전}__seed_{테이블명}_{레이어}.sql
V{버전}__add_{추가내용}.sql          # 스키마 변경 시
V{버전}__update_{수정내용}.sql       # 데이터 수정 시
V{버전}__fix_{버그내용}.sql          # 버그 수정 시
```

**예시:**
```
V200__seed_package_purpose_domain.sql
V201__seed_package_structure_domain.sql
V202__seed_convention_domain.sql
V203__seed_coding_rule_aggregate.sql
V204__seed_coding_rule_vo.sql
...
```

### 1.3 각 테이블별 필수 입력 필드

#### package_purpose
```sql
INSERT INTO package_purpose (
    layer,           -- 'DOMAIN', 'APPLICATION', 'PERSISTENCE'
    code,            -- 'AGGREGATE', 'VALUE_OBJECT', 'DOMAIN_EVENT' 등
    name,            -- 한글 이름
    description,     -- 설명
    key_classes      -- '["Aggregate", "Entity"]' (JSON 배열)
) VALUES (...);
```

#### package_structure
```sql
INSERT INTO package_structure (
    module_id,            -- module 테이블의 id (domain=2, application=3, persistence=4)
    purpose_id,           -- package_purpose의 id
    package_pattern,      -- 'com.ryuqq.domain.{bc}.aggregate'
    description,
    naming_convention,    -- '{BC}{Entity}' 등
    reference_path        -- 'domain/{bc}/aggregate/' 등
) VALUES (...);
```

#### coding_rule
```sql
INSERT INTO coding_rule (
    convention_id,        -- convention 테이블의 id
    code,                 -- 'AGG-001', 'VO-001' 등 (레이어별 Prefix)
    name,
    severity,             -- 'BLOCKER', 'CRITICAL', 'MAJOR', 'MINOR', 'INFO'
    category,             -- 'STRUCTURE', 'BEHAVIOR', 'STYLE', 'DOCUMENTATION'
    description,
    rationale,            -- 규칙의 이유/근거
    auto_fixable,         -- TRUE/FALSE
    is_zero_tolerance,    -- TRUE/FALSE (Zero-Tolerance 규칙 여부)
    applies_to,           -- '["CLASS"]', '["METHOD"]' 등
    structure_id          -- package_structure의 id
) VALUES (...);
```

### 1.4 작업 시 체크리스트

새 레이어 시드 데이터 작업 시 확인 사항:

```
□ 1. 기존 ID 매핑 테이블 확인 (package_purpose.id, package_structure.id)
□ 2. 레이어별 버전 번호 범위 확인 (V200번대 등)
□ 3. 기존 convention 데이터 확인 (이미 있으면 재사용)
□ 4. coding_rule의 code Prefix 결정 (AGG-*, VO-*, EVT-* 등)
□ 5. structure_id 매핑 테이블 작성
□ 6. 각 파일 작성 후 테이블별 레코드 수 기록
□ 7. 이 문서(SEED_DATA_STATUS.md) 업데이트
```

### 1.5 coding_rule 작성 패턴

**코드 Prefix 결정 규칙:**

| 레이어 | 대상 | Prefix | 예시 |
|--------|------|--------|------|
| REST-API | Controller | CTR-* | CTR-001 |
| REST-API | DTO | DTO-* | DTO-001 |
| REST-API | Mapper | MAP-* | MAP-001 |
| Domain | Aggregate | AGG-* | AGG-001 |
| Domain | Value Object | VO-* | VO-001 |
| Domain | Domain Event | EVT-* | EVT-001 |
| Domain | Domain Exception | EXC-* | EXC-001 |
| Application | UseCase | UC-* | UC-001 |
| Application | Service | SVC-* | SVC-001 |
| Application | Command/Query | CMD-*, QRY-* | CMD-001 |
| Persistence | Entity | ENT-* | ENT-001 |
| Persistence | Repository | REPO-* | REPO-001 |
| Persistence | Adapter | ADP-* | ADP-001 |

**규칙 작성 시 고려사항:**

1. **severity 결정 기준:**
   - `BLOCKER`: 반드시 지켜야 함, 빌드 실패 유발
   - `CRITICAL`: 매우 중요, 코드 리뷰 시 반드시 지적
   - `MAJOR`: 중요, 권장 사항
   - `MINOR`: 선호 사항
   - `INFO`: 참고 정보

2. **is_zero_tolerance 설정:**
   - Zero-Tolerance 규칙은 절대 예외 없이 지켜야 하는 규칙
   - 예: Lombok 금지, Law of Demeter, Thin Controller 등

3. **applies_to 설정:**
   - `["CLASS"]`: 클래스 레벨 규칙
   - `["METHOD"]`: 메서드 레벨 규칙
   - `["FIELD"]`: 필드 레벨 규칙
   - `["CLASS", "METHOD"]`: 복합 적용

### 1.6 archunit_test 작성 패턴

ArchUnit 테스트는 **개별 규칙 단위**로 저장 (Option B):

```sql
INSERT INTO archunit_test (
    convention_id,
    structure_id,        -- NULL이면 전체 적용
    code,                -- 'ARCH-LAYER-001' 등
    name,
    description,
    test_class_name,     -- 'RestApiArchTest'
    test_method_name,    -- 'rest_api_should_only_depend_on_application'
    test_code,           -- 실제 ArchUnit 테스트 코드
    severity
) VALUES (...);
```

**test_code 작성 예시:**
```java
@Test
@DisplayName("REST API는 Application Layer만 의존해야 한다")
void rest_api_should_only_depend_on_application() {
    JavaClasses importedClasses = new ClassFileImporter()
        .importPackages("com.ryuqq.adapter.in.rest");

    ArchRule rule = classes()
        .that().resideInAPackage("..adapter.in.rest..")
        .should().onlyDependOnClassesThat()
        .resideInAnyPackage(
            "..adapter.in.rest..",
            "..application..",
            "java..",
            "jakarta..",
            "org.springframework.."
        );

    rule.check(importedClasses);
}
```

---

## 2. 도메인 연관관계 (ERD)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           Core Domain                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  tech_stack ──► architecture ──► module ──┬──► package_purpose             │
│                                           │           │                     │
│                                           │           ▼                     │
│                                           ├──► package_structure ◄──────────┤
│                                           │           │                     │
│                                           │           ├──► coding_rule ◄────┼── sdk 필드 추가
│                                           │           │        │            │
│                                           │           │        ├──► rule_example
│                                           │           │        │        + source, feedback_id
│                                           │           │        │
│                                           │           │        └──► checklist_item
│                                           │           │                + source, feedback_id
│                                           │           │
│                                           │           ├──► class_template
│                                           │           │
│                                           │           └──► archunit_test [V125 신규]
│                                           │
│                                           └──► resource_template [V125 신규]
│                                                                             │
│  convention ──┬──► layer_dependency_rule                                   │
│               │                                                             │
│               └──► sdk_usage_rule [V125 신규]                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                           SDK Domain [V125 신규]                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  sdk_version ◄─────── sdk_setup_guide                                      │
│       │                                                                     │
│       └────────────── sdk_usage_rule (min/max_version 참조)                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                           Agent Feedback Loop [V125 신규]                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  review_feedback ──► promoted_to ──┬──► rule_example (GOOD/BAD_EXAMPLE)    │
│       │                            │                                        │
│       │                            └──► checklist_item (CHECKLIST_ITEM)    │
│       │                                                                     │
│       ├── rule_id ────► coding_rule                                        │
│       ├── structure_id ► package_structure                                 │
│       └── sdk_rule_id ─► sdk_usage_rule                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. FK 연결 전략

### 3.1 현재 방식: AUTO_INCREMENT 순서 기반 하드코딩

```sql
-- V103: package_purpose INSERT (순서대로 id=1, 2, 3...)
INSERT INTO package_purpose (...) VALUES ('ADAPTER_IN', 'CONTROLLER_COMMAND', ...);  -- id=1
INSERT INTO package_purpose (...) VALUES ('ADAPTER_IN', 'CONTROLLER_QUERY', ...);    -- id=2

-- V104: package_structure에서 purpose_id를 하드코딩
INSERT INTO package_structure (..., purpose_id, ...) VALUES (4, 1, ...);  -- purpose_id=1
INSERT INTO package_structure (..., purpose_id, ...) VALUES (4, 2, ...);  -- purpose_id=2
```

### 3.2 module 테이블 ID 매핑

| module.id | module.name | layer |
|-----------|-------------|-------|
| 1 | rest-api | ADAPTER_IN |
| 2 | domain | DOMAIN |
| 3 | application | APPLICATION |
| 4 | persistence | ADAPTER_OUT |
| 5 | integration-test | TEST |

### 3.3 REST-API 모듈 ID 매핑 (V100번대)

| 순서 | package_purpose.id | code | package_structure.id |
|------|-------------------|------|---------------------|
| 1 | 1 | CONTROLLER_COMMAND | 1 |
| 2 | 2 | CONTROLLER_QUERY | 2 |
| 3 | 3 | DTO_COMMAND | 3 |
| 4 | 4 | DTO_QUERY | 4 |
| 5 | 5 | DTO_RESPONSE | 5 |
| 6 | 6 | ERROR | 6 |
| 7 | 7 | MAPPER_COMMAND | 7 |
| 8 | 8 | MAPPER_QUERY | 8 |
| 9 | 9 | COMMON_CONTROLLER | 9 |
| 10 | 10 | COMMON_DTO | 10 |
| 11 | 11 | COMMON_ERROR | 11 |
| 12 | 12 | COMMON_MAPPER | 12 |
| 13 | 13 | COMMON_UTIL | 13 |
| 14 | 14 | CONTROLLER (Aggregate) | 14 |
| 15 | 15 | DTO (Aggregate) | 15 |
| 16 | 16 | MAPPER (Aggregate) | 16 |
| 17 | 17 | REST_DOCS_TEST | 17 |
| 18 | 18 | TEST_SUPPORT | 18 |
| 19 | 19 | CONFIG | 19 |
| 20 | 20 | ARCH_TEST | 20 |

### 3.4 REST-API coding_rule → structure_id 매핑

| 규칙 Prefix | structure_id | 대상 Structure |
|------------|--------------|---------------|
| CTR-* | 14 | Controller (Aggregate) |
| DTO-* | 15 | DTO (Aggregate) |
| MAP-* | 16 | Mapper (Aggregate) |
| TEST-* | 17 | REST_DOCS_TEST |
| OAS-* | 14 | Controller (Aggregate) |
| CFG-* | 19 | CONFIG |

---

## 4. 버전 번호 할당 규칙

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Flyway 마이그레이션 버전 할당                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  V1~V99      : 스키마 정의 (DDL)                                     │
│  V100~V199   : REST-API 레이어 시드 데이터 ✅ 완료                    │
│  V200~V299   : Domain 레이어 시드 데이터 (예정)                       │
│  V300~V399   : Application 레이어 시드 데이터 (예정)                  │
│  V400~V499   : Persistence 레이어 시드 데이터 (예정)                  │
│  V500~V599   : Integration Test 시드 데이터 (예정)                   │
│  V900~V999   : 공통/SDK 시드 데이터                                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 5. REST-API 모듈 (V100번대) - 완료

### 5.1 마이그레이션 파일 목록

| 버전 | 파일명 | 설명 | 상태 |
|-----|-------|------|-----|
| V1 | init_schema.sql | 스키마 생성 | ✅ |
| V2 | add_package_structure_purpose_fk.sql | FK 추가 | ✅ |
| V100 | seed_tech_stack.sql | 기술 스택 | ✅ |
| V101 | seed_architecture.sql | 아키텍처 | ✅ |
| V102 | seed_module.sql | 모듈 | ✅ |
| V103 | seed_package_purpose_adapter_in.sql | 패키지 목적 (20개) | ✅ |
| V104 | seed_package_structure_rest_api.sql | 패키지 구조 (20개) | ✅ |
| V105 | seed_layer_dependency_rule.sql | 레이어 의존성 (8개) | ✅ |
| V106 | seed_convention_rest_api.sql | 컨벤션 | ✅ |
| V107 | seed_coding_rule_controller.sql | Controller 규칙 (CTR-001~010) | ✅ |
| V108 | seed_coding_rule_dto.sql | DTO 규칙 (DTO-001~010) | ✅ |
| V109 | seed_coding_rule_mapper.sql | Mapper 규칙 (MAP-001~007) | ✅ |
| V113 | add_structure_id_to_coding_rule.sql | 스키마 수정 | ✅ |
| V114 | seed_aggregate_structure.sql | Aggregate 구조 | ✅ |
| V115 | update_coding_rule_structure_id.sql | FK 업데이트 | ✅ |
| V116 | add_structure_id_to_class_template.sql | 스키마 수정 | ✅ |
| V117 | fix_error_package_naming_pattern.sql | 버그 수정 | ✅ |
| V118 | seed_class_template_rest_api.sql | 클래스 템플릿 | ✅ |
| V119 | seed_class_template_common.sql | 공통 템플릿 | ✅ |
| V120 | seed_rule_example_rest_api.sql | 규칙 예시 (~30개) | ✅ |
| V121 | seed_coding_rule_testing.sql | 테스트 규칙 (TEST-001~010) | ✅ |
| V122 | seed_test_package_structure.sql | 테스트 패키지 구조 | ✅ |
| V123 | seed_class_template_test_config.sql | 테스트 템플릿 | ✅ |
| V124 | seed_rule_example_testing.sql | 테스트 예시 | ✅ |
| V125 | add_extended_convention_schema.sql | 확장 스키마 | ✅ |
| V126 | seed_resource_template_rest_api.sql | 리소스 템플릿 (9개) | ✅ |
| V127 | add_coding_rule_strict_dto_controller.sql | DTO/Controller 강화 | ✅ |
| V128 | seed_archunit_test_rest_api.sql | ArchUnit 테스트 (21개) | ✅ |

### 5.2 테이블별 데이터 현황

| 테이블 | 레코드 수 | 설명 |
|-------|----------|------|
| tech_stack | 1 | Spring Boot 3.5 + Java 21 |
| architecture | 1 | Hexagonal Architecture |
| module | 5 | rest-api, domain, application, persistence, integration-test |
| package_purpose | 20 | ADAPTER_IN 모듈 전용 |
| package_structure | 20 | rest-api 모듈 전용 |
| convention | 1 | REST_API v1.0.0 |
| layer_dependency_rule | 8 | 레이어 의존성 규칙 |
| **coding_rule** | **48개** | CTR-013개, DTO-014개, MAP-008개, TEST-010개, OAS-004개, CFG-004개 |
| class_template | ~15개 | Controller, DTO, Mapper, Test 템플릿 |
| rule_example | ~30개 | Good/Bad 예시 |
| **resource_template** | **9개** | application.yml 5개, messages 2개, logback 1개, banner 1개 |
| **archunit_test** | **21개** | Layer 3개, CTR 6개, DTO 6개, MAP 4개, CFG 2개 |

### 5.3 Coding Rule 상세 (총 48개)

#### Controller 규칙 (CTR-001 ~ CTR-013)

| 코드 | 규칙명 | Severity | Zero-Tolerance |
|------|--------|----------|----------------|
| CTR-001 | Thin Controller 패턴 | BLOCKER | ✅ |
| CTR-002 | CQRS Controller 분리 | CRITICAL | |
| CTR-003 | ResponseEntity<ApiResponse<T>> 래핑 | BLOCKER | |
| CTR-004 | @Valid 필수 | BLOCKER | ✅ |
| CTR-005 | DELETE 대신 PATCH 사용 | MAJOR | |
| CTR-006 | @Tag OpenAPI 그룹화 | MAJOR | |
| CTR-007 | @Operation 메서드 문서화 | MAJOR | |
| CTR-008 | Mapper DI 필수 | CRITICAL | |
| CTR-009 | @RestController + @RequestMapping | MAJOR | |
| CTR-010 | URI 네이밍 규칙 | MAJOR | |
| CTR-011 | List 직접 반환 금지 (Page/Slice 필수) | BLOCKER | ✅ |
| CTR-012 | 최대 페이지 사이즈 2000 제한 | CRITICAL | |
| CTR-013 | var 키워드 사용 금지 | MAJOR | |

#### DTO 규칙 (DTO-001 ~ DTO-014)

| 코드 | 규칙명 | Severity | Zero-Tolerance |
|------|--------|----------|----------------|
| DTO-001 | Java 21 Record 필수 | BLOCKER | ✅ |
| DTO-002 | @NotNull 필수 (Nullable 금지) | BLOCKER | ✅ |
| DTO-003 | @Schema 문서화 | MAJOR | |
| DTO-004 | Immutable 불변성 | BLOCKER | |
| DTO-005 | 단방향 변환 | CRITICAL | |
| DTO-006 | @JsonFormat 금지 | MAJOR | |
| DTO-007 | 네이밍 규칙 | MAJOR | |
| DTO-008 | Nested Record 허용 | INFO | |
| DTO-009 | Optional 금지 | CRITICAL | |
| DTO-010 | 빈 Record 금지 | MAJOR | |
| DTO-011 | Request DTO 메서드 금지 | BLOCKER | ✅ |
| DTO-012 | Response DTO 메서드 금지 | BLOCKER | ✅ |
| DTO-013 | Query Response 시간 필드 필수 | CRITICAL | |
| DTO-014 | ApiResponse 원시타입 래핑 금지 | BLOCKER | |

#### Mapper 규칙 (MAP-001 ~ MAP-008)

| 코드 | 규칙명 | Severity | Zero-Tolerance |
|------|--------|----------|----------------|
| MAP-001 | @Component 필수 | BLOCKER | ✅ |
| MAP-002 | 필드 매핑만 수행 | BLOCKER | |
| MAP-003 | 비즈니스 로직 금지 | BLOCKER | ✅ |
| MAP-004 | Null 안전 처리 | CRITICAL | |
| MAP-005 | Command/Query Mapper 분리 | MAJOR | |
| MAP-006 | 메서드 네이밍 규칙 | MAJOR | |
| MAP-007 | 단방향 변환 | CRITICAL | |
| MAP-008 | Null 체크 및 기본값 설정 위치 | CRITICAL | |

#### Test 규칙 (TEST-001 ~ TEST-010)

| 코드 | 규칙명 | Severity |
|------|--------|----------|
| TEST-001 | @WebMvcTest + RestDocsTestSupport | BLOCKER |
| TEST-002 | @MockitoBean Mock 필수 | BLOCKER |
| TEST-003 | document() 문서화 필수 | BLOCKER |
| TEST-004 | requestFields/responseFields | CRITICAL |
| TEST-005 | @Tag("restdocs") | MAJOR |
| TEST-006 | @DisplayName 한글 | MAJOR |
| TEST-007 | @AutoConfigureMockMvc(addFilters=false) | CRITICAL |
| TEST-008 | Given-When-Then 패턴 | INFO |
| TEST-009 | Path/Query Parameters 문서화 | CRITICAL |
| TEST-010 | *DocsTest 네이밍 | MAJOR |

#### OpenAPI 규칙 (OAS-001 ~ OAS-004)

| 코드 | 규칙명 | Severity |
|------|--------|----------|
| OAS-001 | @Schema DTO 필드 문서화 | CRITICAL |
| OAS-002 | @Operation 메서드 문서화 | CRITICAL |
| OAS-003 | @ApiResponses 응답 코드 | MAJOR |
| OAS-004 | @Tag Controller 그룹화 | MAJOR |

#### Config 규칙 (CFG-001 ~ CFG-004)

| 코드 | 규칙명 | Severity |
|------|--------|----------|
| CFG-001 | OpenApiConfig 중앙 설정 | BLOCKER |
| CFG-002 | JacksonConfig 중앙 설정 | BLOCKER |
| CFG-003 | *Config 네이밍 | MAJOR |
| CFG-004 | @Bean Javadoc | INFO |

### 5.4 Resource Template 상세 (9개)

| 파일 경로 | 타입 | 설명 |
|----------|------|------|
| application.yml | YAML | 공통 설정 (servlet, jackson, mvc, actuator) |
| application-local.yml | YAML | 로컬 개발 환경 (debug 로깅, swagger 활성화) |
| application-dev.yml | YAML | 개발 서버 환경 |
| application-staging.yml | YAML | 스테이징 환경 (swagger 비활성화) |
| application-prod.yml | YAML | 운영 환경 (보안, 성능 최적화, JSON 로깅) |
| messages.properties | PROPERTIES | 영문 메시지 |
| messages_ko.properties | PROPERTIES | 한글 메시지 |
| logback-spring.xml | XML | 프로파일별 로깅 설정 |
| banner.txt | TEXT | 애플리케이션 시작 배너 |

### 5.5 ArchUnit Test 상세 (21개)

| 코드 | 테스트명 | 카테고리 |
|------|---------|----------|
| ARCH-LAYER-001 | REST-API → Application만 의존 | Layer |
| ARCH-LAYER-002 | Domain 직접 의존 금지 | Layer |
| ARCH-LAYER-003 | 순환 의존성 금지 | Layer |
| ARCH-CTR-001 | Controller는 UseCase만 의존 | Controller |
| ARCH-CTR-002 | @RestController 필수 | Controller |
| ARCH-CTR-003 | controller 패키지 위치 | Controller |
| ARCH-CTR-004 | *Controller 네이밍 | Controller |
| ARCH-CTR-005 | List 반환 금지 | Controller |
| ARCH-CTR-006 | var 키워드 금지 (가이드용) | Controller |
| ARCH-DTO-001 | Record 타입 필수 | DTO |
| ARCH-DTO-002 | *ApiRequest/*ApiResponse 네이밍 | DTO |
| ARCH-DTO-003 | dto 패키지 위치 | DTO |
| ARCH-DTO-004 | Domain 의존 금지 | DTO |
| ARCH-DTO-005 | Request DTO 메서드 금지 | DTO |
| ARCH-DTO-006 | Response DTO 메서드 금지 | DTO |
| ARCH-MAP-001 | @Component 필수 | Mapper |
| ARCH-MAP-002 | *Mapper 네이밍 | Mapper |
| ARCH-MAP-003 | Repository 의존 금지 | Mapper |
| ARCH-MAP-004 | mapper 패키지 위치 | Mapper |
| ARCH-CFG-001 | @Configuration 필수 | Config |
| ARCH-CFG-002 | config 패키지 위치 | Config |

### 5.6 Package Purpose 상세 (20개)

```
┌─────────────────────────────────────────────────────────────────┐
│ BC(Bounded Context) 패키지                                       │
├─────────────────────────────────────────────────────────────────┤
│ 1. CONTROLLER_COMMAND   - POST/PUT/PATCH/DELETE 처리            │
│ 2. CONTROLLER_QUERY     - GET 처리                              │
│ 3. DTO_COMMAND          - Create/Update Request DTO             │
│ 4. DTO_QUERY            - Search/Get Request DTO                │
│ 5. DTO_RESPONSE         - Response DTO                          │
│ 6. ERROR                - 에러 코드, 예외 핸들러                  │
│ 7. MAPPER_COMMAND       - Request → Command 변환                 │
│ 8. MAPPER_QUERY         - Request → Query, Domain → Response    │
├─────────────────────────────────────────────────────────────────┤
│ Common 패키지                                                    │
├─────────────────────────────────────────────────────────────────┤
│ 9.  COMMON_CONTROLLER   - 전역 핸들러, API Docs                  │
│ 10. COMMON_DTO          - ApiResponse<T>, PageApiResponse       │
│ 11. COMMON_ERROR        - ErrorMapper Registry                  │
│ 12. COMMON_MAPPER       - ErrorMapper 인터페이스                 │
│ 13. COMMON_UTIL         - DateTimeFormatUtils 등                │
├─────────────────────────────────────────────────────────────────┤
│ Aggregate 패키지 (규칙 연결용)                                    │
├─────────────────────────────────────────────────────────────────┤
│ 14. CONTROLLER          - CTR-* 규칙 적용 대상                   │
│ 15. DTO                 - DTO-* 규칙 적용 대상                   │
│ 16. MAPPER              - MAP-* 규칙 적용 대상                   │
├─────────────────────────────────────────────────────────────────┤
│ Test/Config 패키지                                               │
├─────────────────────────────────────────────────────────────────┤
│ 17. REST_DOCS_TEST      - @WebMvcTest + REST Docs 테스트        │
│ 18. TEST_SUPPORT        - RestDocsTestSupport, Fixture          │
│ 19. CONFIG              - OpenApiConfig, JacksonConfig          │
│ 20. ARCH_TEST           - ArchUnit 아키텍처 테스트               │
└─────────────────────────────────────────────────────────────────┘
```

### 5.7 Class Template 현황 (~15개)

```
┌─────────────────────────────────────────────────────────────────┐
│ Controller Templates (structure_id=14)                          │
├─────────────────────────────────────────────────────────────────┤
│ - CommandController 템플릿                                      │
│ - QueryController 템플릿                                        │
├─────────────────────────────────────────────────────────────────┤
│ DTO Templates (structure_id=15)                                 │
├─────────────────────────────────────────────────────────────────┤
│ - CreateApiRequest 템플릿                                       │
│ - UpdateApiRequest 템플릿                                       │
│ - SearchApiRequest 템플릿                                       │
│ - ApiResponse 템플릿                                            │
├─────────────────────────────────────────────────────────────────┤
│ Mapper Templates (structure_id=16)                              │
├─────────────────────────────────────────────────────────────────┤
│ - CommandApiMapper 템플릿                                       │
│ - QueryApiMapper 템플릿                                         │
├─────────────────────────────────────────────────────────────────┤
│ Common Templates                                                │
├─────────────────────────────────────────────────────────────────┤
│ - ApiResponse<T> 래퍼 템플릿                                    │
│ - GlobalExceptionHandler 템플릿                                 │
│ - ErrorMapper 인터페이스 템플릿                                  │
├─────────────────────────────────────────────────────────────────┤
│ Config Templates (structure_id=19)                              │
├─────────────────────────────────────────────────────────────────┤
│ - OpenApiConfig 템플릿                                          │
│ - JacksonConfig 템플릿                                          │
│ - WebMvcConfig 템플릿                                           │
├─────────────────────────────────────────────────────────────────┤
│ Test Templates (structure_id=17, 18)                            │
├─────────────────────────────────────────────────────────────────┤
│ - RestDocsTestSupport 템플릿 (structure_id=18)                  │
│ - ControllerDocsTest 템플릿 (structure_id=17)                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 6. Domain 모듈 (V200번대) - 진행 중

### 6.1 마이그레이션 파일 목록

| 버전 | 파일명 | 설명 | 상태 |
|-----|-------|------|-----|
| V200 | seed_package_purpose_domain.sql | 패키지 목적 (10개) | ✅ |
| V201 | seed_package_structure_domain.sql | 패키지 구조 (10개) | ✅ |
| V202 | seed_convention_domain.sql | 컨벤션 | ✅ |
| V203 | seed_coding_rule_aggregate.sql | Aggregate 규칙 (26개) | ✅ |
| V204 | seed_coding_rule_id.sql | ID VO 규칙 (10개) | ✅ |
| V205 | seed_coding_rule_vo.sql | Value Object 규칙 (9개) | ✅ |
| V206 | seed_coding_rule_event.sql | Domain Event 규칙 (11개) | ✅ |
| V207 | seed_coding_rule_exception.sql | Exception 규칙 (20개) | ✅ |
| V208 | seed_coding_rule_criteria.sql | Criteria 규칙 (10개) | ✅ |
| V209 | seed_archunit_test_domain.sql | ArchUnit 테스트 (41개) | ✅ |
| V210 | seed_class_template_domain.sql | 클래스 템플릿 (18개) | ✅ |
| V211 | seed_rule_example_domain.sql | 규칙 예시 (35개) | ✅ |

### 6.2 package_purpose (ID: 21~30)

| ID | code | name | 설명 |
|----|------|------|------|
| 21 | COMMON_EVENT | Domain Event Interface | DomainEvent 마커 인터페이스 |
| 22 | COMMON_EXCEPTION | Domain Exception Base | ErrorCode, DomainException 베이스 |
| 23 | COMMON_VO | Common Value Object | DateRange, PageRequest, QueryContext 등 |
| 24 | COMMON_UTIL | Domain Utility | DomainAssertions 등 |
| 25 | AGGREGATE | Aggregate Root | forNew/reconstitute, Lombok/JPA/Spring 금지 |
| 26 | ID | ID Value Object | Long ID (forNew/isNew) vs String ID (of만) |
| 27 | VALUE_OBJECT | Value Object | Record 타입, of() 팩토리 |
| 28 | DOMAIN_EVENT | Domain Event | 과거형 네이밍, from() 팩토리 |
| 29 | DOMAIN_EXCEPTION | Domain Exception | ErrorCode Enum + Exception 클래스 |
| 30 | QUERY_CRITERIA | Query Criteria | SearchCriteria, QueryContext 필수 |

### 6.3 package_structure (ID: 21~30)

| structure_id | purpose_id | path_pattern | 설명 |
|--------------|------------|--------------|------|
| 21 | 21 | `{base}.common.event` | DomainEvent 인터페이스 |
| 22 | 22 | `{base}.common.exception` | ErrorCode, DomainException |
| 23 | 23 | `{base}.common.vo` | 공통 VO |
| 24 | 24 | `{base}.common.util` | 도메인 유틸리티 |
| 25 | 25 | `{base}.{bc}.aggregate` | Aggregate Root |
| 26 | 26 | `{base}.{bc}.id` | ID VO |
| 27 | 27 | `{base}.{bc}.vo` | 일반 VO |
| 28 | 28 | `{base}.{bc}.event` | Domain Event |
| 29 | 29 | `{base}.{bc}.exception` | BC별 Exception |
| 30 | 30 | `{base}.{bc}.query.criteria` | 조회 조건 |

### 6.4 coding_rule 현황 (총 86개)

| Prefix | 대상 | structure_id | 규칙 수 | 상태 |
|--------|------|--------------|---------|------|
| AGG-* | Aggregate | 25 | 26개 | ✅ |
| ID-* | ID Value Object | 26 | 10개 | ✅ |
| VO-* | Value Object | 27 | 9개 | ✅ |
| EVT-* | Domain Event | 28 | 11개 | ✅ |
| EXC-* | Domain Exception | 29 | 20개 | ✅ |
| CRI-* | Query Criteria | 30 | 10개 | ✅ |
| **합계** | | | **86개** | ✅ |

### 6.5 Zero-Tolerance 규칙 목록

| 규칙 | 대상 | 설명 |
|------|------|------|
| Lombok 금지 | 전체 | @Getter/@Setter/@Data 등 모든 Lombok 어노테이션 금지 |
| JPA 금지 | 전체 | @Entity/@Table/@Column 등 JPA 어노테이션 금지 |
| Spring 금지 | 전체 | @Component/@Service/@Repository 등 Spring 어노테이션 금지 |
| Law of Demeter | Aggregate | Getter 체이닝 금지 (order.getCustomer().getAddress() ❌) |
| Tell, Don't Ask | Aggregate | 상태 묻지 말고 행동 요청 |
| Setter 금지 | 전체 | 불변성 보장, 상태 변경은 명시적 메서드로 |
| LocalDateTime 금지 | 전체 | Instant 사용 필수 |

### 6.6 ArchUnit Test 상세 (41개)

| 코드 | 테스트명 | 카테고리 |
|------|---------|----------|
| **Layer 의존성 (5개)** |
| ARCH-LAYER-010 | Domain Lombok 금지 | Layer |
| ARCH-LAYER-011 | Domain JPA 금지 | Layer |
| ARCH-LAYER-012 | Domain Spring 금지 | Layer |
| ARCH-LAYER-013 | Domain Application Layer 의존 금지 | Layer |
| ARCH-LAYER-014 | Domain Adapter Layer 의존 금지 | Layer |
| **Aggregate (8개)** |
| ARCH-AGG-001 | Aggregate aggregate 패키지 위치 | Aggregate |
| ARCH-AGG-002 | forNew() 정적 팩토리 필수 | Aggregate |
| ARCH-AGG-003 | reconstitute() 정적 팩토리 필수 | Aggregate |
| ARCH-AGG-004 | Setter 금지 | Aggregate |
| ARCH-AGG-005 | LocalDateTime 금지 (Instant 사용) | Aggregate |
| ARCH-AGG-006 | public 생성자 금지 | Aggregate |
| ARCH-AGG-007 | Primitive Getter 금지 (Rich Getter) | Aggregate |
| ARCH-AGG-008 | 검증 메서드 네이밍 (validate* 금지) | Aggregate |
| **ID VO (5개)** |
| ARCH-ID-001 | ID id 패키지 위치 | ID |
| ARCH-ID-002 | ID Record 타입 필수 | ID |
| ARCH-ID-003 | *Id 네이밍 규칙 | ID |
| ARCH-ID-004 | of() 정적 팩토리 필수 | ID |
| ARCH-ID-005 | forNew()/isNew() 패턴 (Long ID) | ID |
| **Value Object (5개)** |
| ARCH-VO-001 | VO vo 패키지 위치 | VO |
| ARCH-VO-002 | VO Record 또는 Enum 필수 | VO |
| ARCH-VO-003 | VO of() 정적 팩토리 필수 | VO |
| ARCH-VO-004 | Enum displayName() 필수 | VO |
| ARCH-VO-005 | Money VO 패턴 | VO |
| **Domain Event (6개)** |
| ARCH-EVT-001 | Event event 패키지 위치 | Event |
| ARCH-EVT-002 | Event Record 타입 필수 | Event |
| ARCH-EVT-003 | *Event 네이밍 (과거형) | Event |
| ARCH-EVT-004 | from() 정적 팩토리 필수 | Event |
| ARCH-EVT-005 | DomainEvent 인터페이스 구현 | Event |
| ARCH-EVT-006 | occurredAt/aggregateId 필수 필드 | Event |
| **Exception (6개)** |
| ARCH-EXC-001 | Exception exception 패키지 위치 | Exception |
| ARCH-EXC-002 | *Exception 네이밍 | Exception |
| ARCH-EXC-003 | DomainException 상속 필수 | Exception |
| ARCH-EXC-004 | ErrorCode Enum 필수 | Exception |
| ARCH-EXC-005 | ErrorCode 인터페이스 구현 | Exception |
| ARCH-EXC-006 | getCode()/getMessage()/getStatus() 필수 | Exception |
| **Criteria (6개)** |
| ARCH-CRI-001 | Criteria query.criteria 패키지 위치 | Criteria |
| ARCH-CRI-002 | *Criteria 네이밍 | Criteria |
| ARCH-CRI-003 | Criteria Record 타입 필수 | Criteria |
| ARCH-CRI-004 | of() 정적 팩토리 필수 | Criteria |
| ARCH-CRI-005 | Criteria public 클래스 | Criteria |
| ARCH-CRI-006 | QueryContext 필드 권장 | Criteria |

### 6.7 Class Template 상세 (18개)

```
┌─────────────────────────────────────────────────────────────────┐
│ Aggregate Templates (structure_id=25)                           │
├─────────────────────────────────────────────────────────────────┤
│ - {Entity} Aggregate Root 템플릿 (forNew/reconstitute)          │
│ - {Entity}UpdateData 상태 변경 데이터 템플릿                      │
├─────────────────────────────────────────────────────────────────┤
│ ID VO Templates (structure_id=26)                               │
├─────────────────────────────────────────────────────────────────┤
│ - {Entity}Id (Long) 템플릿 (forNew/isNew/of)                    │
│ - {Entity}Id (String) 템플릿 (of만 사용)                         │
├─────────────────────────────────────────────────────────────────┤
│ Value Object Templates (structure_id=27)                        │
├─────────────────────────────────────────────────────────────────┤
│ - {Name} 단일값 VO (of + 검증)                                   │
│ - {Name} 복합 VO (여러 필드)                                     │
│ - {Name}Status Enum VO (displayName 필수)                       │
│ - Money VO (amount + currency)                                   │
├─────────────────────────────────────────────────────────────────┤
│ Domain Event Templates (structure_id=28)                        │
├─────────────────────────────────────────────────────────────────┤
│ - DomainEvent 마커 인터페이스                                    │
│ - {Entity}CreatedEvent Record (from + 과거형)                    │
├─────────────────────────────────────────────────────────────────┤
│ Exception Templates (structure_id=29)                           │
├─────────────────────────────────────────────────────────────────┤
│ - ErrorCode 인터페이스                                           │
│ - {Entity}ErrorCode Enum (ErrorCode 구현)                       │
│ - DomainException 추상 클래스                                    │
│ - {Entity}NotFoundException (DomainException 상속)               │
├─────────────────────────────────────────────────────────────────┤
│ Criteria Templates (structure_id=30)                            │
├─────────────────────────────────────────────────────────────────┤
│ - {Entity}SearchCriteria (페이징 + 필터)                         │
│ - {Entity}GetCriteria (단건 조회)                                │
└─────────────────────────────────────────────────────────────────┘
```

### 6.8 Event 과거형 정규식

```regex
^[A-Z][a-zA-Z]*(ed|ent|aid|ade|one|ept|ilt|elt|ought|aught|old|eld|own|ven|ken|ten|ung|ost|eft)Event$
```

**커버되는 패턴:**
- ed: Created, Updated, Cancelled, Confirmed, Completed, Failed
- ent: Sent, Spent, Went
- aid: Paid, Laid
- ade: Made
- one: Done, Gone
- ought/aught: Bought, Brought, Thought, Caught, Taught
- old/eld: Sold, Told, Held
- own: Shown, Grown, Known, Thrown
- ven/ken/ten: Given, Taken, Spoken, Written
- ung: Hung, Begun
- ost/eft: Lost, Left

---

## 7. Application 모듈 (V300번대) - 예정

### 7.1 예상 package_purpose (시작 ID: 31~)

| ID | code | 설명 |
|----|------|------|
| 31 | USE_CASE | Port-In 인터페이스 |
| 32 | SERVICE | UseCase 구현체 |
| 33 | COMMAND_DTO | Command DTO |
| 34 | QUERY_DTO | Query DTO |
| 35 | ASSEMBLER | Domain → Response 변환 |
| 36 | FACTORY | Request → Domain 생성 |
| 37 | PORT_OUT | Port-Out 인터페이스 |
| 38 | FACADE | 복합 UseCase 조합 |
| 39 | MANAGER | Transaction 경계 관리 |

### 7.2 예상 coding_rule Prefix

| Prefix | 대상 | 예상 규칙 수 |
|--------|------|-------------|
| UC-* | UseCase | ~8개 |
| SVC-* | Service | ~10개 |
| CMD-* | Command | ~6개 |
| QRY-* | Query | ~6개 |
| ASM-* | Assembler | ~5개 |
| FAC-* | Facade | ~5개 |

---

## 8. Persistence 모듈 (V400번대) - 예정

### 8.1 예상 package_purpose (시작 ID: 41~)

| ID | code | 설명 |
|----|------|------|
| 41 | ENTITY | JPA Entity |
| 42 | JPA_REPOSITORY | JpaRepository 인터페이스 |
| 43 | QUERYDSL_REPOSITORY | QueryDsl Repository |
| 44 | COMMAND_ADAPTER | Command Adapter |
| 45 | QUERY_ADAPTER | Query Adapter |
| 46 | ENTITY_MAPPER | Entity ↔ Domain 변환 |
| 47 | LOCK_ADAPTER | Lock Adapter |

### 8.2 예상 coding_rule Prefix

| Prefix | 대상 | 예상 규칙 수 |
|--------|------|-------------|
| ENT-* | Entity | ~10개 |
| REPO-* | Repository | ~8개 |
| ADP-* | Adapter | ~8개 |
| EMAP-* | EntityMapper | ~5개 |

---

## 9. 다음 작업

### 9.1 V125 확장 스키마 시드 데이터

| 순서 | 작업 | 버전 | 상태 |
|------|------|------|------|
| 1 | Resource Template | V126 | ✅ 완료 |
| 2 | Coding Rule 강화 | V127 | ✅ 완료 |
| 3 | ArchUnit Test | V128 | ✅ 완료 |
| 4 | SDK 시드 데이터 | V129 | ⏳ 예정 |

### 9.2 레이어별 시드 데이터

| 레이어 | 버전 범위 | 상태 |
|--------|----------|------|
| REST-API | V100~V128 | ✅ 완료 |
| Domain | V200~V211 | ✅ 완료 (86개 규칙, 41개 ArchUnit, 18개 템플릿, 35개 예시) |
| Application | V300~V399 | ❌ 미시작 |
| Persistence | V400~V499 | ❌ 미시작 |

### 9.3 MCP 서버 개발

- 시드 데이터 조회 API 개발
- LLM 에이전트 연동 테스트
- Worker/Reviewer 에이전트 피드백 루프 구현

---

*마지막 업데이트: 2026-01-13 (Domain V200~V211 완료 - 86개 coding_rule, 41개 archunit_test, 18개 class_template, 35개 rule_example)*
