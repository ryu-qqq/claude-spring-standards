package com.ryuqq.adapter.in.rest.archunittest;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ArchUnitTestApiEndpoints - ArchUnitTest API Endpoint 상수
 *
 * <p>ArchUnitTest 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/arch-unit-tests
 *   ├── GET    /                          # 전체 목록 조회 (커서 페이징)
 *   ├── GET    /{archUnitTestId}          # 단건 조회
 *   ├── POST   /                          # 생성
 *   └── PUT    /{archUnitTestId}          # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ArchUnitTestApiEndpoints {

    private ArchUnitTestApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // ArchUnitTest Endpoints
    // ============================================

    /** ArchUnitTest 기본 경로 */
    public static final String BASE = STANDARDS_BASE + "/arch-unit-tests";

    /** ArchUnitTest 단일 조회/수정 경로 */
    public static final String BY_ID = BASE + "/{archUnitTestId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{archUnitTestId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** ArchUnitTest ID 경로 변수명 */
    public static final String PATH_ARCH_UNIT_TEST_ID = "archUnitTestId";
}
