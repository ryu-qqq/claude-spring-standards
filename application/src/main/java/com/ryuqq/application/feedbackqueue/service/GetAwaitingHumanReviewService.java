package com.ryuqq.application.feedbackqueue.service;

import com.ryuqq.application.feedbackqueue.assembler.FeedbackQueueAssembler;
import com.ryuqq.application.feedbackqueue.dto.query.GetAwaitingHumanReviewQuery;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import com.ryuqq.application.feedbackqueue.factory.query.FeedbackQueueQueryFactory;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueueReadManager;
import com.ryuqq.application.feedbackqueue.port.in.GetAwaitingHumanReviewUseCase;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetAwaitingHumanReviewService - Human 승인 대기 피드백 조회 서비스
 *
 * <p>LLM_APPROVED + MEDIUM 리스크 피드백 조회 유스케이스를 구현합니다. (UC-H01)
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-007: Service에 비즈니스 로직 금지, Factory 패턴 사용.
 *
 * @author ryu-qqq
 */
@Service
public class GetAwaitingHumanReviewService implements GetAwaitingHumanReviewUseCase {

    private final FeedbackQueueQueryFactory feedbackQueueQueryFactory;
    private final FeedbackQueueReadManager feedbackQueueReadManager;
    private final FeedbackQueueAssembler feedbackQueueAssembler;

    public GetAwaitingHumanReviewService(
            FeedbackQueueQueryFactory feedbackQueueQueryFactory,
            FeedbackQueueReadManager feedbackQueueReadManager,
            FeedbackQueueAssembler feedbackQueueAssembler) {
        this.feedbackQueueQueryFactory = feedbackQueueQueryFactory;
        this.feedbackQueueReadManager = feedbackQueueReadManager;
        this.feedbackQueueAssembler = feedbackQueueAssembler;
    }

    @Override
    public FeedbackQueueSliceResult execute(GetAwaitingHumanReviewQuery query) {
        FeedbackQueueSliceCriteria criteria = feedbackQueueQueryFactory.toSliceCriteria(query);
        List<FeedbackQueue> feedbackQueues = feedbackQueueReadManager.findBySliceCriteria(criteria);
        return feedbackQueueAssembler.toSliceResult(feedbackQueues, query.size());
    }
}
