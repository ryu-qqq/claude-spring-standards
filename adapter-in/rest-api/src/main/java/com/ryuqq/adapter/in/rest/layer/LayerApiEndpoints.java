package com.ryuqq.adapter.in.rest.layer;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * LayerApiEndpoints - Layer API Endpoint 상수
 *
 * <p>Layer 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/layers
 *   ├── GET    /                    # 전체 목록 조회
 *   ├── GET    /{layerId}           # 단건 조회
 *   ├── POST   /                    # 생성
 *   ├── PUT    /{layerId}           # 수정
 *   └── PATCH  /{layerId}/delete    # 소프트 삭제
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class LayerApiEndpoints {

    private LayerApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // Layer Endpoints
    // ============================================

    /** Layer 기본 경로 */
    public static final String LAYERS = STANDARDS_BASE + "/layers";

    /** Layer 단일 조회 경로 */
    public static final String LAYER_DETAIL = LAYERS + "/{layerId}";

    /** Layer Soft Delete 경로 */
    public static final String LAYER_DELETE = LAYER_DETAIL + "/delete";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{layerId}";

    /** ID + Soft Delete 경로 (상대경로) */
    public static final String ID_DELETE = "/{layerId}/delete";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Layer ID 경로 변수명 */
    public static final String PATH_LAYER_ID = "layerId";
}
