package com.ryuqq.adapter.in.rest.layerdependency;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * LayerDependencyRuleApiEndpoints - LayerDependencyRule API Endpoint 상수
 *
 * <p>LayerDependencyRule 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * Command (Architecture Sub-resource):
 * /api/v1/templates/architectures/{architectureId}/layer-dependency-rules
 *   ├── POST   /                    # 생성
 *   └── PATCH  /{ldrId}             # 수정
 *
 * Query (독립 조회):
 * /api/v1/templates/layer-dependency-rules
 *   └── GET    /                    # 복합 조건 조회 (커서 기반)
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class LayerDependencyRuleApiEndpoints {

    private LayerDependencyRuleApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    /** Architecture 기본 경로 */
    public static final String ARCHITECTURES_BASE = STANDARDS_BASE + "/architectures";

    // ============================================
    // Command Endpoints (Architecture Sub-resource)
    // ============================================

    /** LayerDependencyRule Command 기본 경로 (Architecture Sub-resource) */
    public static final String BASE =
            ARCHITECTURES_BASE + "/{architectureId}/layer-dependency-rules";

    /** LayerDependencyRule 단일 수정 경로 */
    public static final String BY_ID = BASE + "/{ldrId}";

    // ============================================
    // Query Endpoints (독립 조회)
    // ============================================

    /** LayerDependencyRule Query 기본 경로 (독립 조회) */
    public static final String QUERY_BASE = STANDARDS_BASE + "/layer-dependency-rules";

    // ============================================
    // Relative Paths (for @GetMapping, @PostMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{ldrId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Architecture ID 경로 변수명 */
    public static final String PATH_ARCHITECTURE_ID = "architectureId";

    /** LayerDependencyRule ID 경로 변수명 */
    public static final String PATH_LDR_ID = "ldrId";
}
