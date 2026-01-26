package com.ryuqq.adapter.in.rest.packagepurpose;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * PackagePurposeApiEndpoints - PackagePurpose API Endpoint 상수
 *
 * <p>PackagePurpose 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/ref/package-purposes
 *   ├── GET    /                    # 복합 조건 조회 (커서 기반)
 *   ├── POST   /                    # 생성 (Admin)
 *   └── PATCH  /{packagePurposeId}   # 수정 (Admin)
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class PackagePurposeApiEndpoints {

    private PackagePurposeApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    /** REF API 기본 경로 (참조 데이터) */
    public static final String REF_BASE = STANDARDS_BASE + "/ref";

    // ============================================
    // PackagePurpose Endpoints
    // ============================================

    /** PackagePurpose 기본 경로 */
    public static final String BASE = REF_BASE + "/package-purposes";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{packagePurposeId}";

    /** PackagePurpose 단일 조회/수정 경로 */
    public static final String BY_ID = BASE + "/{packagePurposeId}";

    // ============================================
    // Query Parameter Names
    // ============================================

    /** Structure ID 쿼리 파라미터명 */
    public static final String PARAM_STRUCTURE_ID = "structureId";

    // ============================================
    // Path Variable Names
    // ============================================

    /** PackagePurpose ID 경로 변수명 */
    public static final String PATH_PACKAGE_PURPOSE_ID = "packagePurposeId";
}
