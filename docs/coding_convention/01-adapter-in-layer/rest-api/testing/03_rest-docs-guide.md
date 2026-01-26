# Spring REST Docs 가이드

> **목적**: API 문서 자동화를 위한 Spring REST Docs 적용 규칙

---

## 0. 문서 위치 및 접근 경로

### 디렉토리 구조

```
프로젝트 루트/
├── bootstrap/
│   └── bootstrap-web-api/
│       ├── src/
│       │   └── docs/
│       │       └── asciidoc/           ← 📝 AsciiDoc 소스
│       │           ├── index.adoc      ← 메인 문서
│       │           ├── common/         ← 공통 섹션
│       │           └── {bc}/           ← BC별 API 문서
│       └── build/
│           ├── generated-snippets/     ← 🔧 테스트 생성 스니펫
│           └── docs/asciidoc/          ← 📄 빌드된 HTML
│
└── adapter-in/
    └── rest-api/
        └── src/main/java/.../common/controller/
            └── ApiDocsController.java   ← 🌐 문서 서빙 컨트롤러
```

### 접근 경로

| 경로 | 설명 |
|------|------|
| `/docs` | API 문서 (리다이렉트) |
| `/docs/index.html` | API 문서 메인 페이지 |

### 빌드 명령어

```bash
# REST Docs 생성 (테스트 실행 + HTML 변환)
./gradlew :bootstrap:bootstrap-web-api:asciidoctor

# JAR에 문서 포함하여 빌드
./gradlew :bootstrap:bootstrap-web-api:bootJar

# 문서 확인
open bootstrap/bootstrap-web-api/build/docs/asciidoc/index.html
```

---

## 1. 개요

### Spring REST Docs vs Swagger/OpenAPI

| 항목 | Spring REST Docs | Swagger/OpenAPI |
|------|------------------|-----------------|
| **문서 생성** | 테스트 기반 | 어노테이션 기반 |
| **신뢰성** | ✅ 높음 (테스트 통과 필수) | 🔶 중간 (코드와 불일치 가능) |
| **실행 시점** | 빌드 시 생성 | 런타임 생성 |
| **출력 형식** | AsciiDoc → HTML/PDF | JSON/YAML → HTML |
| **유지보수** | 테스트 = 문서 | 별도 관리 필요 |

### 왜 Spring REST Docs인가?

```
┌─────────────────────────────────────────────────────────────┐
│  Spring REST Docs 선택 이유                                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 테스트 강제: 테스트 통과해야 문서 생성                     │
│     → 문서와 실제 API 100% 일치 보장                         │
│                                                             │
│  2. 코드 분리: 프로덕션 코드에 문서 어노테이션 없음             │
│     → 깔끔한 코드 유지                                       │
│                                                             │
│  3. 커스터마이징: AsciiDoc 기반 자유로운 문서 구성             │
│     → 기업 스타일 가이드 적용 가능                            │
│                                                             │
│  ⚠️ 단, OpenAPI 어노테이션도 함께 사용 (Swagger UI용)         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. 설정

### 2.1 Gradle 의존성

```gradle
// build.gradle (rest-api 모듈)

plugins {
    id 'org.asciidoctor.jvm.convert' version '3.3.2'
}

configurations {
    asciidoctorExt
}

dependencies {
    // Spring REST Docs
    testImplementation libs.spring.restdocs.mockmvc
    asciidoctorExt libs.spring.restdocs.asciidoctor
}

// REST Docs 스니펫 출력 위치
ext {
    snippetsDir = file('build/generated-snippets')
}

// 테스트 실행 시 스니펫 생성
test {
    outputs.dir snippetsDir
}

// AsciiDoc → HTML 변환
asciidoctor {
    inputs.dir snippetsDir
    configurations 'asciidoctorExt'
    dependsOn test

    baseDirFollowsSourceFile()

    sources {
        include '**/index.adoc'
    }
}

// 빌드 시 문서 복사
bootJar {
    dependsOn asciidoctor
    from("${asciidoctor.outputDir}") {
        into 'static/docs'
    }
}
```

### 2.2 libs.versions.toml

```toml
[versions]
spring-restdocs = "3.0.1"

[libraries]
spring-restdocs-mockmvc = { module = "org.springframework.restdocs:spring-restdocs-mockmvc", version.ref = "spring-restdocs" }
spring-restdocs-asciidoctor = { module = "org.springframework.restdocs:spring-restdocs-asciidoctor", version.ref = "spring-restdocs" }
```

---

## 3. 테스트 작성

### 3.1 REST Docs 테스트 기본 구조

> **중요**: REST Docs는 MockMvc 기반이지만, **통합 테스트와 별도로** 문서화 전용 테스트를 작성합니다.

```java
package com.ryuqq.adapter.in.rest.order.docs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Order API 문서화 테스트
 *
 * <p><strong>목적:</strong> Spring REST Docs를 통한 API 문서 자동 생성
 *
 * <p><strong>주의:</strong> 이 테스트는 문서화 목적이며,
 * 실제 API 검증은 {@link OrderApiIntegrationTest}에서 수행합니다.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(RestDocumentationExtension.class)
@DisplayName("Order API 문서화")
class OrderApiDocsTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(documentationConfiguration(restDocumentation)
                .uris()
                    .withScheme("https")
                    .withHost("api.example.com")
                    .withPort(443))
            .build();
    }

    @Test
    @Sql("/sql/orders-test-data.sql")
    @DisplayName("주문 단건 조회 API")
    void getOrder() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}", 100L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("order-get",
                pathParameters(
                    parameterWithName("orderId").description("주문 ID")
                ),
                responseFields(
                    fieldWithPath("success").description("성공 여부"),
                    fieldWithPath("data.orderId").description("주문 ID"),
                    fieldWithPath("data.customerId").description("고객 ID"),
                    fieldWithPath("data.status").description("주문 상태 (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)"),
                    fieldWithPath("data.totalAmount").description("총 주문 금액"),
                    fieldWithPath("data.orderDate").description("주문 일자 (yyyy-MM-dd)")
                )
            ));
    }
}
```

### 3.2 테스트 파일 위치

```
src/test/java/com/ryuqq/adapter/in/rest/
├── order/
│   ├── OrderApiIntegrationTest.java    # 통합 테스트 (필수)
│   └── docs/
│       └── OrderApiDocsTest.java       # 문서화 테스트 (REST Docs)
├── customer/
│   ├── CustomerApiIntegrationTest.java
│   └── docs/
│       └── CustomerApiDocsTest.java
```

---

## 4. 문서화 스니펫

### 4.1 Path Parameters

```java
.andDo(document("order-get",
    pathParameters(
        parameterWithName("orderId").description("주문 ID")
    )
));
```

### 4.2 Query Parameters

```java
.andDo(document("orders-list",
    queryParameters(
        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
        parameterWithName("size").description("페이지 크기 (기본값: 20)").optional(),
        parameterWithName("status").description("주문 상태 필터").optional(),
        parameterWithName("startDate").description("시작일 (yyyy-MM-dd)").optional(),
        parameterWithName("endDate").description("종료일 (yyyy-MM-dd)").optional()
    )
));
```

### 4.3 Request Body

```java
.andDo(document("order-create",
    requestFields(
        fieldWithPath("customerId").description("고객 ID"),
        fieldWithPath("productId").description("상품 ID"),
        fieldWithPath("quantity").description("주문 수량 (1~1000)")
    )
));
```

### 4.4 Response Body

```java
.andDo(document("order-create",
    responseFields(
        fieldWithPath("success").description("성공 여부"),
        fieldWithPath("data").description("응답 데이터"),
        fieldWithPath("data.orderId").description("생성된 주문 ID"),
        fieldWithPath("data.status").description("주문 상태"),
        fieldWithPath("data.createdAt").description("생성 시각")
    )
));
```

### 4.5 Request Headers

```java
.andDo(document("order-create",
    requestHeaders(
        headerWithName("Authorization").description("Bearer JWT 토큰"),
        headerWithName("Content-Type").description("application/json")
    )
));
```

---

## 5. AsciiDoc 문서 구성

### 5.1 디렉토리 구조

```
src/docs/asciidoc/
├── index.adoc              # 메인 문서
├── common/
│   ├── overview.adoc       # API 개요
│   └── errors.adoc         # 에러 코드
├── order/
│   ├── order-create.adoc   # 주문 생성
│   ├── order-get.adoc      # 주문 조회
│   └── order-list.adoc     # 주문 목록
└── customer/
    └── customer.adoc       # 고객 API
```

### 5.2 index.adoc (메인 문서)

```asciidoc
= API 문서
:doctype: book
:icons: font
:source-highlighter: highlightjs
:toc: left
:toclevels: 3
:sectlinks:

[[overview]]
== 개요

본 문서는 API 명세를 제공합니다.

include::common/overview.adoc[]

[[resources]]
== 리소스

include::order/order-create.adoc[]
include::order/order-get.adoc[]
include::order/order-list.adoc[]

include::common/errors.adoc[]
```

### 5.3 API 문서 템플릿

```asciidoc
[[order-create]]
=== 주문 생성

새로운 주문을 생성합니다.

==== HTTP Request

include::{snippets}/order-create/http-request.adoc[]

==== Path Parameters

include::{snippets}/order-create/path-parameters.adoc[]

==== Request Fields

include::{snippets}/order-create/request-fields.adoc[]

==== HTTP Response

include::{snippets}/order-create/http-response.adoc[]

==== Response Fields

include::{snippets}/order-create/response-fields.adoc[]

==== Example

===== Request

include::{snippets}/order-create/curl-request.adoc[]

===== Response

include::{snippets}/order-create/response-body.adoc[]
```

---

## 6. 스니펫 커스터마이징

### 6.1 공통 필드 재사용

```java
// 공통 응답 필드 정의
public class ApiDocumentUtils {

    public static FieldDescriptor[] commonResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("success").description("성공 여부"),
            fieldWithPath("timestamp").description("응답 시각"),
            fieldWithPath("data").description("응답 데이터")
        };
    }

    public static FieldDescriptor[] pageResponseFields() {
        return new FieldDescriptor[] {
            fieldWithPath("data.content[]").description("데이터 목록"),
            fieldWithPath("data.hasNext").description("다음 페이지 존재 여부"),
            fieldWithPath("data.number").description("현재 페이지 번호"),
            fieldWithPath("data.size").description("페이지 크기")
        };
    }
}
```

### 6.2 테스트에서 활용

```java
.andDo(document("orders-list",
    responseFields(
        ApiDocumentUtils.commonResponseFields(),
        ApiDocumentUtils.pageResponseFields(),
        fieldWithPath("data.content[].orderId").description("주문 ID"),
        fieldWithPath("data.content[].status").description("주문 상태")
    )
));
```

---

## 7. 빌드 및 확인

### 7.1 문서 생성

```bash
# 테스트 실행 + 문서 생성
./gradlew :adapter-in:rest-api:asciidoctor

# 결과 확인
open adapter-in/rest-api/build/docs/asciidoc/index.html
```

### 7.2 CI/CD 통합

```yaml
# GitHub Actions 예시
- name: Build with REST Docs
  run: ./gradlew :adapter-in:rest-api:asciidoctor

- name: Upload API Docs
  uses: actions/upload-artifact@v3
  with:
    name: api-docs
    path: adapter-in/rest-api/build/docs/asciidoc/
```

---

## 8. OpenAPI 어노테이션과 함께 사용

### 8.1 병행 사용 전략

```java
/**
 * 주문 생성 API
 *
 * @apiNote REST Docs로 상세 문서 생성, OpenAPI로 Swagger UI 제공
 */
@Operation(
    summary = "주문 생성",
    description = "새로운 주문을 생성합니다"
)
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
})
@PostMapping("/orders")
public ResponseEntity<ApiResponse<OrderApiResponse>> createOrder(
    @Valid @RequestBody PlaceOrderApiRequest request
) {
    // ...
}
```

### 8.2 역할 분담

| 도구 | 용도 |
|------|------|
| **REST Docs** | 정적 HTML 문서 (배포용, 인쇄용) |
| **OpenAPI/Swagger** | 대화형 API 탐색기 (개발자용) |

---

## 9. 체크리스트

### 설정

- [ ] Gradle 의존성 추가 (`spring-restdocs-mockmvc`)
- [ ] Asciidoctor 플러그인 설정
- [ ] 스니펫 출력 디렉토리 설정

### 테스트 작성

- [ ] `@ExtendWith(RestDocumentationExtension.class)` 추가
- [ ] MockMvc 설정 (documentationConfiguration)
- [ ] `document()` 호출로 스니펫 생성
- [ ] Path/Query/Request/Response 필드 문서화

### AsciiDoc

- [ ] `index.adoc` 메인 문서 작성
- [ ] 각 API별 문서 파일 분리
- [ ] 공통 필드 재사용

### 빌드

- [ ] `./gradlew asciidoctor` 정상 실행
- [ ] 생성된 HTML 확인
- [ ] CI/CD 파이프라인 통합

---

## 10. 참고 문서

- [Spring REST Docs 공식 문서](https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/)
- [REST API 통합 테스트 가이드](./01_rest-api-testing-guide.md)
- [OpenAPI 가이드](../openapi/openapi-guide.md)

---

**작성자**: Development Team
**최종 수정일**: 2025-12-08
**버전**: 1.0.0
