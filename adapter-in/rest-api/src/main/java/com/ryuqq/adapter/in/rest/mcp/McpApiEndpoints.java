package com.ryuqq.adapter.in.rest.mcp;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * McpApiEndpoints - MCP API Endpoint 상수
 *
 * <p>MCP (Module-Centric Planning) 워크플로우 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/mcp
 *   ├── GET /planning-context      # Planning Phase - 계획 컨텍스트 조회
 *   ├── GET /module/{moduleId}/context  # Execution Phase - Module 컨텍스트 조회
 *   └── GET /validation-context    # Validation Phase - 검증 컨텍스트 조회
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class McpApiEndpoints {

    private McpApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** MCP API 기본 경로 */
    public static final String BASE = ApiPaths.MCP;

    // ============================================
    // Planning Phase Endpoints
    // ============================================

    /** Planning Context 조회 경로 (상대경로) */
    public static final String PLANNING_CONTEXT = "/planning-context";

    /** Planning Context 조회 전체 경로 */
    public static final String PLANNING_CONTEXT_FULL = BASE + PLANNING_CONTEXT;

    // ============================================
    // Execution Phase Endpoints
    // ============================================

    /** Module Context 조회 경로 (상대경로) */
    public static final String MODULE_CONTEXT = "/module/{moduleId}/context";

    /** Module Context 조회 전체 경로 */
    public static final String MODULE_CONTEXT_FULL = BASE + MODULE_CONTEXT;

    // ============================================
    // Validation Phase Endpoints
    // ============================================

    /** Validation Context 조회 경로 (상대경로) */
    public static final String VALIDATION_CONTEXT = "/validation-context";

    /** Validation Context 조회 전체 경로 */
    public static final String VALIDATION_CONTEXT_FULL = BASE + VALIDATION_CONTEXT;

    // ============================================
    // Convention Hub Endpoints (Phase 2)
    // ============================================

    /** Config Files 조회 경로 (상대경로) - init_project Tool용 */
    public static final String CONFIG_FILES = "/config-files";

    /** Config Files 조회 전체 경로 */
    public static final String CONFIG_FILES_FULL = BASE + CONFIG_FILES;

    /** Onboarding Context 조회 경로 (상대경로) - get_onboarding_context Tool용 */
    public static final String ONBOARDING = "/onboarding";

    /** Onboarding Context 조회 전체 경로 */
    public static final String ONBOARDING_FULL = BASE + ONBOARDING;

    // ============================================
    // Path Variable Names
    // ============================================

    /** Module ID 경로 변수명 */
    public static final String PATH_MODULE_ID = "moduleId";
}
