package com.ryuqq.adapter.in.rest.module;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ModuleApiEndpoints - Module API Endpoint 상수
 *
 * <p>Module 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/modules
 *   ├── GET    /                    # 전체 목록 조회
 *   ├── GET    /tree                # 트리 조회
 *   ├── GET    /{moduleId}          # 단건 조회
 *   ├── POST   /                    # 생성
 *   ├── PUT    /{moduleId}          # 수정
 *   └── PATCH  /{moduleId}/delete   # 소프트 삭제
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ModuleApiEndpoints {

    private ModuleApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // Module Endpoints
    // ============================================

    /** Module 기본 경로 */
    public static final String MODULES = STANDARDS_BASE + "/modules";

    /** Module 단일 조회 경로 */
    public static final String MODULE_DETAIL = MODULES + "/{moduleId}";

    /** Module Soft Delete 경로 */
    public static final String MODULE_DELETE = MODULE_DETAIL + "/delete";

    /** Module 트리 조회 경로 */
    public static final String MODULE_TREE = MODULES + "/tree";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{moduleId}";

    /** ID + Soft Delete 경로 (상대경로) */
    public static final String ID_DELETE = "/{moduleId}/delete";

    /** 트리 경로 (상대경로) */
    public static final String TREE = "/tree";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Module ID 경로 변수명 */
    public static final String PATH_MODULE_ID = "moduleId";

    /** Layer ID 쿼리 파라미터명 */
    public static final String PARAM_LAYER_ID = "layerId";
}
