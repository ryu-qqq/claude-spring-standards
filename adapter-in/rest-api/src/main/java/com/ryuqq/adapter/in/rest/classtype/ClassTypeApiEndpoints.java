package com.ryuqq.adapter.in.rest.classtype;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ClassTypeApiEndpoints - ClassType API Endpoint 상수
 *
 * <p>ClassType 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/class-types
 *   ├── GET    /                    # 전체 목록 조회
 *   ├── POST   /                    # 생성
 *   └── PUT    /{classTypeId}       # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ClassTypeApiEndpoints {

    private ClassTypeApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // ClassType Endpoints
    // ============================================

    /** ClassType 기본 경로 */
    public static final String CLASS_TYPES = STANDARDS_BASE + "/class-types";

    /** ClassType 단일 조회 경로 */
    public static final String CLASS_TYPE_DETAIL = CLASS_TYPES + "/{classTypeId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{classTypeId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** ClassType ID 경로 변수명 */
    public static final String PATH_CLASS_TYPE_ID = "classTypeId";
}
