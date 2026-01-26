package com.ryuqq.application.feedbackqueue.service;

import com.ryuqq.application.feedbackqueue.assembler.FeedbackQueueAssembler;
import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import com.ryuqq.application.feedbackqueue.factory.query.FeedbackQueueQueryFactory;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueueReadManager;
import com.ryuqq.application.feedbackqueue.port.in.SearchFeedbacksByCursorUseCase;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.query.FeedbackQueueSliceCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * SearchFeedbacksByCursorService - FeedbackQueue 복합 조건 조회 서비스 (커서 기반)
 *
 * <p>FeedbackQueue 목록을 커서 기반으로 복합 조건(상태/대상 타입/피드백 타입/리스크/액션)으로 조회합니다.
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class SearchFeedbacksByCursorService implements SearchFeedbacksByCursorUseCase {

    private final FeedbackQueueQueryFactory queryFactory;
    private final FeedbackQueueReadManager readManager;
    private final FeedbackQueueAssembler assembler;

    public SearchFeedbacksByCursorService(
            FeedbackQueueQueryFactory queryFactory,
            FeedbackQueueReadManager readManager,
            FeedbackQueueAssembler assembler) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public FeedbackQueueSliceResult execute(FeedbackQueueSearchParams searchParams) {
        FeedbackQueueSliceCriteria criteria = queryFactory.createSliceCriteria(searchParams);
        List<FeedbackQueue> feedbackQueues = readManager.findBySliceCriteria(criteria);
        return assembler.toSliceResult(feedbackQueues, criteria.size());
    }
}
