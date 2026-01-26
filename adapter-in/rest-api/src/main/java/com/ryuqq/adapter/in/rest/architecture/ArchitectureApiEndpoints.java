package com.ryuqq.adapter.in.rest.architecture;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ArchitectureApiEndpoints - Architecture API Endpoint 상수
 *
 * <p>Architecture 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/architectures
 *   ├── GET    /                    # 전체 목록 조회
 *   ├── GET    /{architectureId}    # 단건 조회
 *   ├── POST   /                    # 생성
 *   └── PUT    /{architectureId}    # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ArchitectureApiEndpoints {

    private ArchitectureApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // Architecture Endpoints
    // ============================================

    /** Architecture 기본 경로 */
    public static final String ARCHITECTURES = STANDARDS_BASE + "/architectures";

    /** Architecture 단일 조회 경로 */
    public static final String ARCHITECTURE_DETAIL = ARCHITECTURES + "/{architectureId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{architectureId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Architecture ID 경로 변수명 */
    public static final String PATH_ARCHITECTURE_ID = "architectureId";
}
