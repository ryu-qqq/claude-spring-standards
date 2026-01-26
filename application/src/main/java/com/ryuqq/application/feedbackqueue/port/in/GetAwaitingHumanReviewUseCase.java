package com.ryuqq.application.feedbackqueue.port.in;

import com.ryuqq.application.feedbackqueue.dto.query.GetAwaitingHumanReviewQuery;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;

/**
 * GetAwaitingHumanReviewUseCase - Human 승인 대기 피드백 조회 UseCase
 *
 * <p>LLM_APPROVED 상태이면서 MEDIUM 리스크인 피드백을 조회합니다. (UC-H01)
 *
 * @author ryu-qqq
 */
public interface GetAwaitingHumanReviewUseCase {

    /**
     * Human 승인 대기 피드백 목록 조회
     *
     * @param query 조회 쿼리
     * @return 피드백 슬라이스 결과
     */
    FeedbackQueueSliceResult execute(GetAwaitingHumanReviewQuery query);
}
