package com.ryuqq.adapter.in.rest.classtemplate;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ClassTemplateApiEndpoints - ClassTemplate API Endpoint 상수
 *
 * <p>ClassTemplate 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/class-templates
 *   ├── GET    /                    # 복합 조건 조회 (커서 기반)
 *   ├── POST   /                    # 생성
 *   └── PUT    /{classTemplateId}   # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ClassTemplateApiEndpoints {

    private ClassTemplateApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // ClassTemplate Endpoints
    // ============================================

    /** ClassTemplate 기본 경로 */
    public static final String CLASS_TEMPLATES = STANDARDS_BASE + "/class-templates";

    /** ClassTemplate 단일 조회/수정 경로 */
    public static final String BY_ID = CLASS_TEMPLATES + "/{classTemplateId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{classTemplateId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** ClassTemplate ID 경로 변수명 */
    public static final String PATH_CLASS_TEMPLATE_ID = "classTemplateId";
}
