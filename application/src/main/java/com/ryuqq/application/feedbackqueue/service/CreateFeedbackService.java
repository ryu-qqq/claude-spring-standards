package com.ryuqq.application.feedbackqueue.service;

import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.factory.command.FeedbackQueueCommandFactory;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidator;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidatorResolver;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueuePersistenceManager;
import com.ryuqq.application.feedbackqueue.port.in.CreateFeedbackUseCase;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import org.springframework.stereotype.Service;

/**
 * CreateFeedbackService - 피드백 생성 서비스
 *
 * <p>피드백 생성 유스케이스를 구현합니다. (UC-L01)
 *
 * <p>SVC-001: Service는 @Transactional 사용 금지, Manager에서 처리.
 *
 * <p>SVC-003: Domain 객체 직접 생성 금지 -> Factory 사용.
 *
 * <p>입력 시점 검증 (Stage 1):
 *
 * <ul>
 *   <li>FeedbackPayloadValidatorResolver를 통해 targetType에 맞는 검증기 조회
 *   <li>페이로드 JSON 파싱 및 부모 엔티티 존재 여부 검증
 *   <li>검증 실패 시 예외 발생
 * </ul>
 *
 * <p>RiskLevel 결정: Domain의 FeedbackType.riskLevel()에서 처리
 *
 * @author ryu-qqq
 */
@Service
public class CreateFeedbackService implements CreateFeedbackUseCase {

    private final FeedbackQueueCommandFactory feedbackQueueCommandFactory;
    private final FeedbackQueuePersistenceManager feedbackQueuePersistenceManager;
    private final FeedbackPayloadValidatorResolver validatorResolver;

    public CreateFeedbackService(
            FeedbackQueueCommandFactory feedbackQueueCommandFactory,
            FeedbackQueuePersistenceManager feedbackQueuePersistenceManager,
            FeedbackPayloadValidatorResolver validatorResolver) {
        this.feedbackQueueCommandFactory = feedbackQueueCommandFactory;
        this.feedbackQueuePersistenceManager = feedbackQueuePersistenceManager;
        this.validatorResolver = validatorResolver;
    }

    @Override
    public Long execute(CreateFeedbackCommand command) {
        FeedbackTargetType targetType = FeedbackTargetType.valueOf(command.targetType());

        validatePayload(targetType, command);

        FeedbackType feedbackType = FeedbackType.valueOf(command.feedbackType());
        RiskLevel riskLevel = feedbackType.riskLevel();

        FeedbackQueue feedbackQueue = feedbackQueueCommandFactory.create(command, riskLevel);
        FeedbackQueueId savedId = feedbackQueuePersistenceManager.persist(feedbackQueue);
        return savedId.value();
    }

    private void validatePayload(FeedbackTargetType targetType, CreateFeedbackCommand command) {
        FeedbackPayloadValidator validator = validatorResolver.resolve(targetType);
        validator.validate(command);
    }
}
