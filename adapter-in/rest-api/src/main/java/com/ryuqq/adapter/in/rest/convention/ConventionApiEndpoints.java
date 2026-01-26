package com.ryuqq.adapter.in.rest.convention;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ConventionApiEndpoints - Convention API Endpoint 상수
 *
 * <p>Convention 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/conventions
 *   ├── GET    /                    # 복합 조건 조회 (커서 기반)
 *   ├── POST   /                    # 생성
 *   └── PUT    /{conventionId}      # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ConventionApiEndpoints {

    private ConventionApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // Convention Endpoints
    // ============================================

    /** Convention 기본 경로 */
    public static final String CONVENTIONS = STANDARDS_BASE + "/conventions";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{conventionId}";

    /** Convention 단일 조회/수정 경로 */
    public static final String CONVENTION_DETAIL = CONVENTIONS + "/{conventionId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Convention ID 경로 변수명 */
    public static final String PATH_CONVENTION_ID = "conventionId";
}
