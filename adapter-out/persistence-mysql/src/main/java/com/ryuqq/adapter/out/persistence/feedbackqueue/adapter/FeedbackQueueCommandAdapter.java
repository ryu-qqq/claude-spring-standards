package com.ryuqq.adapter.out.persistence.feedbackqueue.adapter;

import com.ryuqq.adapter.out.persistence.feedbackqueue.entity.FeedbackQueueJpaEntity;
import com.ryuqq.adapter.out.persistence.feedbackqueue.mapper.FeedbackQueueJpaEntityMapper;
import com.ryuqq.adapter.out.persistence.feedbackqueue.repository.FeedbackQueueJpaRepository;
import com.ryuqq.application.feedbackqueue.port.out.FeedbackQueueCommandPort;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueCommandAdapter - 피드백 큐 명령 어댑터
 *
 * <p>FeedbackQueueCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>CPRT-002: CommandPort는 persist(Domain), delete(ID) 메서드 제공
 *
 * <p>QADP-002: Adapter에서 @Transactional 금지
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueueCommandAdapter implements FeedbackQueueCommandPort {

    private final FeedbackQueueJpaRepository repository;
    private final FeedbackQueueJpaEntityMapper mapper;

    /**
     * 생성자 주입
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public FeedbackQueueCommandAdapter(
            FeedbackQueueJpaRepository repository, FeedbackQueueJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * FeedbackQueue 영속화 (생성/수정)
     *
     * <p>Domain의 상태에 따라 적절한 영속화를 수행합니다.
     *
     * @param feedbackQueue 영속화할 FeedbackQueue
     * @return 영속화된 FeedbackQueue ID
     */
    @Override
    public FeedbackQueueId persist(FeedbackQueue feedbackQueue) {
        FeedbackQueueJpaEntity entity = mapper.toEntity(feedbackQueue);
        FeedbackQueueJpaEntity saved = repository.save(entity);
        return FeedbackQueueId.of(saved.getId());
    }
}
