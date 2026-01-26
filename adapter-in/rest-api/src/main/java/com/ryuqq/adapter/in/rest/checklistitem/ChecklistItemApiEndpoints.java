package com.ryuqq.adapter.in.rest.checklistitem;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * ChecklistItemApiEndpoints - ChecklistItem API Endpoint 상수
 *
 * <p>ChecklistItem 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/checklist-items
 *   ├── GET    /                       # 복합 조건 조회 (커서 페이징, ruleIds, checkTypes, automationTools 필터)
 *   ├── POST   /                       # 생성
 *   └── PUT    /{id}                   # 수정
 * }</pre>
 *
 * <p>END-001: 도메인별 Endpoints 클래스 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ChecklistItemApiEndpoints {

    private ChecklistItemApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // ChecklistItem Endpoints
    // ============================================

    /** ChecklistItem 기본 경로 */
    public static final String CHECKLIST_ITEMS = STANDARDS_BASE + "/checklist-items";

    /** ChecklistItem 단일 조회/수정 경로 */
    public static final String BY_ID = CHECKLIST_ITEMS + "/{id}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{id}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** ChecklistItem ID 경로 변수명 */
    public static final String PATH_ID = "id";
}
