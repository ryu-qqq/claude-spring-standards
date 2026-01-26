package com.ryuqq.adapter.in.rest.codingrule;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * CodingRuleApiEndpoints - CodingRule API Endpoint 상수
 *
 * <p>CodingRule 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/coding-rules
 *   ├── GET    /                    # 복합 조건 조회 (커서 기반)
 *   ├── POST   /                    # 생성
 *   └── PUT    /{codingRuleId}      # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class CodingRuleApiEndpoints {

    private CodingRuleApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // CodingRule Endpoints
    // ============================================

    /** CodingRule 기본 경로 */
    public static final String BASE = STANDARDS_BASE + "/coding-rules";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{codingRuleId}";

    /** ID 경로 (절대경로) */
    public static final String BY_ID = BASE + ID;

    /** 인덱스 경로 (상대경로) */
    public static final String INDEX = "/index";

    // ============================================
    // Path Variable Names
    // ============================================

    /** CodingRule ID 경로 변수명 */
    public static final String PATH_CODING_RULE_ID = "codingRuleId";
}
