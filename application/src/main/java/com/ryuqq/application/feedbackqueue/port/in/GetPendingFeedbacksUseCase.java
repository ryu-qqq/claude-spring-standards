package com.ryuqq.application.feedbackqueue.port.in;

import com.ryuqq.application.feedbackqueue.dto.query.GetPendingFeedbacksQuery;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;

/**
 * GetPendingFeedbacksUseCase - PENDING 피드백 목록 조회 UseCase
 *
 * <p>LLM 검토 대기 중인 피드백 목록을 조회합니다. (UC-S01)
 *
 * @author ryu-qqq
 */
public interface GetPendingFeedbacksUseCase {

    /**
     * PENDING 상태 피드백 목록 조회
     *
     * @param query 조회 쿼리
     * @return 피드백 슬라이스 결과
     */
    FeedbackQueueSliceResult execute(GetPendingFeedbacksQuery query);
}
