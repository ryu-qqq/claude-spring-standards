package com.ryuqq.application.feedbackqueue.assembler;

import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSummary;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueAssembler - 피드백 큐 응답 조립기
 *
 * <p>도메인 객체를 Application DTO로 변환합니다.
 *
 * <p>ASM-001: Assembler는 Domain -> Application DTO 변환만 담당.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueueAssembler {

    /**
     * FeedbackQueue 도메인 객체를 FeedbackQueueResult로 변환
     *
     * @param feedbackQueue 피드백 큐 도메인 객체
     * @return FeedbackQueueResult
     */
    public FeedbackQueueResult toResult(FeedbackQueue feedbackQueue) {
        return FeedbackQueueResult.from(feedbackQueue);
    }

    /**
     * FeedbackQueue 목록을 FeedbackQueueResult 목록으로 변환
     *
     * @param feedbackQueues 피드백 큐 도메인 객체 목록
     * @return FeedbackQueueResult 목록
     */
    public List<FeedbackQueueResult> toResults(List<FeedbackQueue> feedbackQueues) {
        return feedbackQueues.stream().map(this::toResult).toList();
    }

    /**
     * FeedbackQueue 도메인 객체를 FeedbackQueueSummary로 변환
     *
     * @param feedbackQueue 피드백 큐 도메인 객체
     * @return FeedbackQueueSummary
     */
    public FeedbackQueueSummary toSummary(FeedbackQueue feedbackQueue) {
        return FeedbackQueueSummary.from(feedbackQueue);
    }

    /**
     * FeedbackQueue 목록을 FeedbackQueueSummary 목록으로 변환
     *
     * @param feedbackQueues 피드백 큐 도메인 객체 목록
     * @return FeedbackQueueSummary 목록
     */
    public List<FeedbackQueueSummary> toSummaries(List<FeedbackQueue> feedbackQueues) {
        return feedbackQueues.stream().map(this::toSummary).toList();
    }

    /**
     * FeedbackQueue 목록을 FeedbackQueueSliceResult로 변환
     *
     * @param feedbackQueues 피드백 큐 도메인 객체 목록
     * @param requestedSize 요청한 페이지 크기
     * @return FeedbackQueueSliceResult
     */
    public FeedbackQueueSliceResult toSliceResult(
            List<FeedbackQueue> feedbackQueues, int requestedSize) {
        boolean hasNext = feedbackQueues.size() > requestedSize;
        List<FeedbackQueue> resultFeedbackQueues =
                hasNext ? feedbackQueues.subList(0, requestedSize) : feedbackQueues;
        List<FeedbackQueueResult> results = toResults(resultFeedbackQueues);
        return FeedbackQueueSliceResult.of(results, hasNext);
    }
}
