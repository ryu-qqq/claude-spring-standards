package com.ryuqq.application.feedbackqueue.manager;

import com.ryuqq.application.feedbackqueue.port.out.FeedbackQueueCommandPort;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * FeedbackQueuePersistenceManager - 피드백 큐 영속화 관리자
 *
 * <p>피드백 큐 저장 트랜잭션을 관리합니다.
 *
 * <p>MNG-001: Manager는 @Transactional을 메서드에 적용.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueuePersistenceManager {

    private final FeedbackQueueCommandPort feedbackQueueCommandPort;

    public FeedbackQueuePersistenceManager(FeedbackQueueCommandPort feedbackQueueCommandPort) {
        this.feedbackQueueCommandPort = feedbackQueueCommandPort;
    }

    /**
     * 피드백 큐 영속화 (생성 또는 수정)
     *
     * @param feedbackQueue 영속화할 피드백 큐
     * @return 영속화된 피드백 큐 ID
     */
    @Transactional
    public FeedbackQueueId persist(FeedbackQueue feedbackQueue) {
        return feedbackQueueCommandPort.persist(feedbackQueue);
    }
}
