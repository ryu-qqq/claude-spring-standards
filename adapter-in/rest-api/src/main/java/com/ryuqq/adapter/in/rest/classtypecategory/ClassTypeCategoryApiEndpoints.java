package com.ryuqq.adapter.in.rest.classtypecategory;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ClassTypeCategoryApiEndpoints - ClassTypeCategory API Endpoint 상수
 *
 * <p>ClassTypeCategory 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/class-type-categories
 *   ├── GET    /                    # 전체 목록 조회
 *   ├── POST   /                    # 생성
 *   └── PUT    /{categoryId}        # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ClassTypeCategoryApiEndpoints {

    private ClassTypeCategoryApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // ClassTypeCategory Endpoints
    // ============================================

    /** ClassTypeCategory 기본 경로 */
    public static final String CLASS_TYPE_CATEGORIES = STANDARDS_BASE + "/class-type-categories";

    /** ClassTypeCategory 단일 조회 경로 */
    public static final String CLASS_TYPE_CATEGORY_DETAIL = CLASS_TYPE_CATEGORIES + "/{categoryId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{categoryId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** ClassTypeCategory ID 경로 변수명 */
    public static final String PATH_CATEGORY_ID = "categoryId";
}
