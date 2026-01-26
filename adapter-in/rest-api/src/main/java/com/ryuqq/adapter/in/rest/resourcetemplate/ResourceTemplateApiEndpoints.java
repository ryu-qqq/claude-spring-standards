package com.ryuqq.adapter.in.rest.resourcetemplate;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ResourceTemplateApiEndpoints - ResourceTemplate API Endpoint 상수
 *
 * <p>ResourceTemplate 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/resource-templates
 *   ├── GET    /                          # 복합 조건 조회 (커서 페이징, moduleIds, categories, fileTypes 필터)
 *   ├── POST   /                          # 생성
 *   └── PUT    /{resourceTemplateId}      # 수정
 * }</pre>
 *
 * <p>END-001: 도메인별 Endpoints 클래스 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ResourceTemplateApiEndpoints {

    private ResourceTemplateApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // ResourceTemplate Endpoints
    // ============================================

    /** ResourceTemplate 기본 경로 */
    public static final String RESOURCE_TEMPLATES = STANDARDS_BASE + "/resource-templates";

    /** ResourceTemplate 단일 조회/수정 경로 */
    public static final String BY_ID = RESOURCE_TEMPLATES + "/{resourceTemplateId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{resourceTemplateId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** ResourceTemplate ID 경로 변수명 */
    public static final String PATH_RESOURCE_TEMPLATE_ID = "resourceTemplateId";
}
