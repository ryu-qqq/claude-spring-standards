package com.ryuqq.application.feedbackqueue.port.in;

import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;

/**
 * SearchFeedbacksByCursorUseCase - FeedbackQueue 복합 조건 조회 UseCase (커서 기반)
 *
 * <p>FeedbackQueue 목록을 커서 기반으로 복합 조건(상태/대상 타입/피드백 타입/리스크/액션)으로 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface SearchFeedbacksByCursorUseCase {

    /**
     * FeedbackQueue 복합 조건 조회 실행 (커서 기반)
     *
     * @param searchParams 조회 SearchParams DTO
     * @return 피드백 슬라이스 결과
     */
    FeedbackQueueSliceResult execute(FeedbackQueueSearchParams searchParams);
}
