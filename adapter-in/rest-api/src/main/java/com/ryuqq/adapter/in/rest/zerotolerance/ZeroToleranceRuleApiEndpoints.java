package com.ryuqq.adapter.in.rest.zerotolerance;

/**
 * ZeroToleranceRuleApiEndpoints - ZeroToleranceRule API Endpoint 상수
 *
 * <p>Zero-Tolerance 규칙 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/standards/zero-tolerance-rules
 *   ├── POST   /                   # 생성
 *   ├── PUT    /{id}               # 수정
 *   └── GET    /                   # 복합 조건 조회 (커서 페이징, 컨벤션 ID/탐지 방식/검색/PR 자동 거부 여부 필터링)
 * }</pre>
 *
 * <p>END-001: 도메인별 Endpoints 클래스 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ZeroToleranceRuleApiEndpoints {

    private ZeroToleranceRuleApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** API 버전 1 기본 경로 */
    public static final String API_V1 = "/api/v1";

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = API_V1 + "/standards";

    // ============================================
    // ZeroToleranceRule Endpoints
    // ============================================

    /** ZeroToleranceRule 기본 경로 */
    public static final String BASE = STANDARDS_BASE + "/zero-tolerance-rules";

    /** ZeroToleranceRule 단건 조회 경로 */
    public static final String BY_ID = BASE + "/{id}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String DETAIL = "/{id}";

    /** ID 경로 (상대경로, 수정용) */
    public static final String ID = "/{id}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** ZeroToleranceRule ID 경로 변수명 */
    public static final String PATH_ID = "id";

    /** ZeroToleranceRule ID 경로 변수명 (상세) */
    public static final String PATH_ZERO_TOLERANCE_RULE_ID = "id";
}
