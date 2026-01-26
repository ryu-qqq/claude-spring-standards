package com.ryuqq.application.feedbackqueue.port.out;

import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;

/**
 * FeedbackQueueCommandPort - 피드백 큐 명령 포트
 *
 * <p>피드백 큐의 생성/수정을 위한 아웃바운드 포트입니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain) 메서드 제공
 *
 * <p>Hard delete 금지 정책에 따라 delete 메서드는 제공하지 않습니다. 피드백 거절은 ProcessFeedbackUseCase의 REJECT 액션을 사용합니다.
 *
 * @author ryu-qqq
 */
public interface FeedbackQueueCommandPort {

    /**
     * 피드백 큐 영속화 (생성/수정)
     *
     * @param feedbackQueue 영속화할 FeedbackQueue
     * @return 영속화된 FeedbackQueue ID
     */
    FeedbackQueueId persist(FeedbackQueue feedbackQueue);
}
