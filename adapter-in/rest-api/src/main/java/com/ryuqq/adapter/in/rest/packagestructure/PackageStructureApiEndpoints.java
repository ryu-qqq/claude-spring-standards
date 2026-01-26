package com.ryuqq.adapter.in.rest.packagestructure;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * PackageStructureApiEndpoints - PackageStructure API Endpoint 상수
 *
 * <p>PackageStructure 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/package-structures
 *   ├── GET    /                    # 복합 조건 조회 (커서 기반)
 *   ├── POST   /                    # 생성
 *   └── PUT    /{packageStructureId}   # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class PackageStructureApiEndpoints {

    private PackageStructureApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // PackageStructure Endpoints
    // ============================================

    /** PackageStructure 기본 경로 */
    public static final String PACKAGE_STRUCTURES = STANDARDS_BASE + "/package-structures";

    /** PackageStructure 단일 조회/수정 경로 */
    public static final String BY_ID = PACKAGE_STRUCTURES + "/{packageStructureId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{packageStructureId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** PackageStructure ID 경로 변수명 */
    public static final String PATH_PACKAGE_STRUCTURE_ID = "packageStructureId";
}
