package com.ryuqq.adapter.in.rest.ruleexample;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * RuleExampleApiEndpoints - RuleExample API Endpoint 상수
 *
 * <p>RuleExample 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/rule-examples
 *   ├── GET    /                          # 복합 조건 조회 (커서 페이징, ruleIds, exampleTypes, languages 필터)
 *   ├── POST   /                          # 생성
 *   └── PUT    /{ruleExampleId}           # 수정
 * }</pre>
 *
 * <p>END-001: 도메인별 Endpoints 클래스 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class RuleExampleApiEndpoints {

    private RuleExampleApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // RuleExample Endpoints
    // ============================================

    /** RuleExample 기본 경로 */
    public static final String RULE_EXAMPLES = STANDARDS_BASE + "/rule-examples";

    /** RuleExample 단일 조회/수정 경로 */
    public static final String BY_ID = RULE_EXAMPLES + "/{ruleExampleId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{ruleExampleId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** RuleExample ID 경로 변수명 */
    public static final String PATH_RULE_EXAMPLE_ID = "ruleExampleId";
}
