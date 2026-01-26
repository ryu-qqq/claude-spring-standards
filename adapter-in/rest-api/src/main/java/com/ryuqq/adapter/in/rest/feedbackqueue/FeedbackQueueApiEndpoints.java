package com.ryuqq.adapter.in.rest.feedbackqueue;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * FeedbackQueueApiEndpoints - FeedbackQueue API Endpoint 상수
 *
 * <p>FeedbackQueue 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/feedback-queue
 *   ├── POST   /                           # 피드백 생성
 *   ├── GET    /                           # 전체 조회 (필터링)
 *   ├── GET    /{feedbackQueueId}          # 단건 조회
 *   ├── PATCH  /{feedbackQueueId}/llm-approve    # LLM 1차 승인
 *   ├── PATCH  /{feedbackQueueId}/llm-reject     # LLM 1차 거절
 *   ├── PATCH  /{feedbackQueueId}/human-approve  # Human 2차 승인
 *   ├── PATCH  /{feedbackQueueId}/human-reject   # Human 2차 거절
 *   └── POST   /{feedbackQueueId}/merge          # 머지
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class FeedbackQueueApiEndpoints {

    private FeedbackQueueApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // FeedbackQueue Endpoints
    // ============================================

    /** FeedbackQueue 기본 경로 */
    public static final String BASE = STANDARDS_BASE + "/feedback-queue";

    /** FeedbackQueue 단일 조회/수정 경로 */
    public static final String BY_ID = BASE + "/{feedbackQueueId}";

    /** FeedbackQueue LLM 승인 경로 */
    public static final String LLM_APPROVE = BY_ID + "/llm-approve";

    /** FeedbackQueue LLM 거절 경로 */
    public static final String LLM_REJECT = BY_ID + "/llm-reject";

    /** FeedbackQueue Human 승인 경로 */
    public static final String HUMAN_APPROVE = BY_ID + "/human-approve";

    /** FeedbackQueue Human 거절 경로 */
    public static final String HUMAN_REJECT = BY_ID + "/human-reject";

    /** FeedbackQueue 머지 경로 */
    public static final String MERGE = BY_ID + "/merge";

    // ============================================
    // Relative Paths (for @GetMapping, @PatchMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{feedbackQueueId}";

    /** LLM 승인 경로 (상대경로) */
    public static final String ID_LLM_APPROVE = "/{feedbackQueueId}/llm-approve";

    /** LLM 거절 경로 (상대경로) */
    public static final String ID_LLM_REJECT = "/{feedbackQueueId}/llm-reject";

    /** Human 승인 경로 (상대경로) */
    public static final String ID_HUMAN_APPROVE = "/{feedbackQueueId}/human-approve";

    /** Human 거절 경로 (상대경로) */
    public static final String ID_HUMAN_REJECT = "/{feedbackQueueId}/human-reject";

    /** 머지 경로 (상대경로) */
    public static final String ID_MERGE = "/{feedbackQueueId}/merge";

    // ============================================
    // Path Variable Names
    // ============================================

    /** FeedbackQueue ID 경로 변수명 */
    public static final String PATH_FEEDBACK_QUEUE_ID = "feedbackQueueId";
}
