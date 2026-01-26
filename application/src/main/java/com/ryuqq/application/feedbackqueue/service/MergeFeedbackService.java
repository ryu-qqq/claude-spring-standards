package com.ryuqq.application.feedbackqueue.service;

import com.ryuqq.application.feedbackqueue.assembler.FeedbackQueueAssembler;
import com.ryuqq.application.feedbackqueue.dto.command.MergeFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.factory.command.FeedbackQueueCommandFactory;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategy;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategyResolver;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidator;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidatorResolver;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueuePersistenceManager;
import com.ryuqq.application.feedbackqueue.port.in.MergeFeedbackUseCase;
import com.ryuqq.application.feedbackqueue.validator.FeedbackQueueValidator;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import org.springframework.stereotype.Service;

/**
 * MergeFeedbackService - 피드백 머지 서비스
 *
 * <p>피드백 머지 유스케이스를 구현합니다. (UC-S05)
 *
 * <p>처리 흐름:
 *
 * <ol>
 *   <li>피드백 검증 및 조회
 *   <li>병합 시점 재검증 (MergeValidator) - 부모/대상 엔티티 존재 확인
 *   <li>타겟 타입별 머지 전략 조회
 *   <li>전략 실행 (대상 엔티티 생성/수정/삭제)
 *   <li>피드백 상태 MERGED로 변경
 *   <li>영속화
 * </ol>
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지 → Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Service
public class MergeFeedbackService implements MergeFeedbackUseCase {

    private final FeedbackQueueValidator feedbackQueueValidator;
    private final FeedbackQueuePersistenceManager feedbackQueuePersistenceManager;
    private final FeedbackQueueAssembler feedbackQueueAssembler;
    private final FeedbackQueueCommandFactory feedbackQueueCommandFactory;
    private final FeedbackMergeStrategyResolver feedbackMergeStrategyResolver;
    private final FeedbackMergeValidatorResolver feedbackMergeValidatorResolver;

    public MergeFeedbackService(
            FeedbackQueueValidator feedbackQueueValidator,
            FeedbackQueuePersistenceManager feedbackQueuePersistenceManager,
            FeedbackQueueAssembler feedbackQueueAssembler,
            FeedbackQueueCommandFactory feedbackQueueCommandFactory,
            FeedbackMergeStrategyResolver feedbackMergeStrategyResolver,
            FeedbackMergeValidatorResolver feedbackMergeValidatorResolver) {
        this.feedbackQueueValidator = feedbackQueueValidator;
        this.feedbackQueuePersistenceManager = feedbackQueuePersistenceManager;
        this.feedbackQueueAssembler = feedbackQueueAssembler;
        this.feedbackQueueCommandFactory = feedbackQueueCommandFactory;
        this.feedbackMergeStrategyResolver = feedbackMergeStrategyResolver;
        this.feedbackMergeValidatorResolver = feedbackMergeValidatorResolver;
    }

    @Override
    public FeedbackQueueResult execute(MergeFeedbackCommand command) {
        FeedbackQueue feedbackQueue =
                feedbackQueueValidator.getAndValidateForMerge(command.feedbackId());

        // 병합 시점 재검증: 부모/대상 엔티티 존재 확인 (실패 시 예외)
        FeedbackMergeValidator mergeValidator =
                feedbackMergeValidatorResolver.resolve(feedbackQueue.targetType());
        mergeValidator.validate(feedbackQueue);

        // 타겟 타입에 맞는 머지 전략 조회 및 실행
        FeedbackMergeStrategy strategy =
                feedbackMergeStrategyResolver.resolve(feedbackQueue.targetType());
        strategy.merge(feedbackQueue);

        // 피드백 상태를 MERGED로 변경
        feedbackQueue.merge(feedbackQueueCommandFactory.now());
        feedbackQueuePersistenceManager.persist(feedbackQueue);

        return feedbackQueueAssembler.toResult(feedbackQueue);
    }
}
