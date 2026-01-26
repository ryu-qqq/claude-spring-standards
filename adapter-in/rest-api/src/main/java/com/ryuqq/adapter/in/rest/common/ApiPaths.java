package com.ryuqq.adapter.in.rest.common;

/**
 * ApiPaths - 서비스 공통 API 경로 상수
 *
 * <p>API Gateway 라우팅 패턴: /api/v1/templates/** → 이 서버
 *
 * <p>prefix strip 없이 그대로 전달되므로 모든 경로가 /api/v1/templates/로 시작합니다.
 *
 * <p><strong>경로 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates
 *   ├── /api-docs          # OpenAPI 문서
 *   ├── /swagger-ui.html   # Swagger UI
 *   ├── /docs/**           # REST Docs
 *   ├── /architectures/**  # 아키텍처 API
 *   ├── /conventions/**    # 컨벤션 API
 *   ├── /coding-rules/**   # 코딩 규칙 API
 *   └── ...
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@SuppressWarnings("PMD.DataClass") // 상수만 포함하는 유틸리티 클래스
public final class ApiPaths {

    private ApiPaths() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** API 버전 1 기본 경로 */
    public static final String API_V1 = "/api/v1";

    /** 서비스 기본 경로 (Gateway 라우팅 매칭) */
    public static final String SERVICE_BASE = API_V1 + "/templates";

    // ============================================
    // Documentation Paths
    // ============================================

    /** OpenAPI 문서 경로 */
    public static final String API_DOCS = SERVICE_BASE + "/api-docs";

    /** Swagger UI 경로 */
    public static final String SWAGGER_UI = SERVICE_BASE + "/swagger-ui.html";

    /** REST Docs 기본 경로 */
    public static final String DOCS = SERVICE_BASE + "/docs";

    /** REST Docs 리소스 경로 패턴 */
    public static final String DOCS_PATTERN = DOCS + "/**";

    // ============================================
    // Domain Resource Paths
    // ============================================

    /** 아키텍처 API 경로 */
    public static final String ARCHITECTURES = SERVICE_BASE + "/architectures";

    /** 컨벤션 API 경로 */
    public static final String CONVENTIONS = SERVICE_BASE + "/conventions";

    /** 코딩 규칙 API 경로 */
    public static final String CODING_RULES = SERVICE_BASE + "/coding-rules";

    /** 클래스 템플릿 API 경로 */
    public static final String CLASS_TEMPLATES = SERVICE_BASE + "/class-templates";

    /** ArchUnit 테스트 API 경로 */
    public static final String ARCHUNIT_TESTS = SERVICE_BASE + "/archunit-tests";

    /** 레이어 의존성 규칙 API 경로 */
    public static final String LAYER_DEPENDENCY_RULES = SERVICE_BASE + "/layer-dependency-rules";

    /** 모듈 API 경로 */
    public static final String MODULES = SERVICE_BASE + "/modules";

    /** 모듈 타입 API 경로 */
    public static final String MODULE_TYPES = SERVICE_BASE + "/module-types";

    /** 규칙 예제 API 경로 */
    public static final String RULE_EXAMPLES = SERVICE_BASE + "/rule-examples";

    /** 리소스 템플릿 API 경로 */
    public static final String RESOURCE_TEMPLATES = SERVICE_BASE + "/resource-templates";

    /** 기술 스택 API 경로 */
    public static final String TECH_STACKS = SERVICE_BASE + "/tech-stacks";

    /** 체크리스트 항목 API 경로 */
    public static final String CHECKLIST_ITEMS = SERVICE_BASE + "/checklist-items";

    /** 패키지 구조 API 경로 */
    public static final String PACKAGE_STRUCTURES = SERVICE_BASE + "/package-structures";

    /** Zero-Tolerance 규칙 API 경로 */
    public static final String ZERO_TOLERANCE_RULES = SERVICE_BASE + "/zero-tolerance-rules";

    /** 패키지 목적 API 경로 */
    public static final String PACKAGE_PURPOSES = SERVICE_BASE + "/package-purposes";

    /** MCP (Convention Tree 등) API 경로 */
    public static final String MCP = SERVICE_BASE + "/mcp";
}
