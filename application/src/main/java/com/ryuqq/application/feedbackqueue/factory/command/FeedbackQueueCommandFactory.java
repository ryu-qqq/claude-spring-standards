package com.ryuqq.application.feedbackqueue.factory.command;

import com.ryuqq.application.common.time.TimeProvider;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackPayload;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueCommandFactory - 피드백 큐 커맨드 팩토리
 *
 * <p>피드백 큐 생성에 필요한 도메인 객체를 생성합니다.
 *
 * <p>FCT-001: Factory는 도메인 객체 생성만 담당.
 *
 * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
 *
 * @author ryu-qqq
 */
@Component
public class FeedbackQueueCommandFactory {

    private final TimeProvider timeProvider;

    public FeedbackQueueCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * CreateFeedbackCommand로부터 FeedbackQueue 도메인 객체 생성
     *
     * @param command 생성 커맨드
     * @return 새로운 FeedbackQueue 인스턴스
     */
    public FeedbackQueue create(CreateFeedbackCommand command) {
        Instant now = timeProvider.now();
        FeedbackTargetType targetType = FeedbackTargetType.valueOf(command.targetType());
        FeedbackType feedbackType = FeedbackType.valueOf(command.feedbackType());
        FeedbackPayload payload = FeedbackPayload.of(command.payload());

        return FeedbackQueue.forNew(targetType, command.targetId(), feedbackType, payload, now);
    }

    /**
     * CreateFeedbackCommand로부터 FeedbackQueue 도메인 객체 생성 (RiskLevel 명시)
     *
     * <p>입력 시점 검증을 통해 RiskLevel이 동적으로 결정된 경우 사용합니다.
     *
     * @param command 생성 커맨드
     * @param riskLevel 검증을 통해 결정된 RiskLevel
     * @return 새로운 FeedbackQueue 인스턴스
     */
    public FeedbackQueue create(CreateFeedbackCommand command, RiskLevel riskLevel) {
        Instant now = timeProvider.now();
        FeedbackTargetType targetType = FeedbackTargetType.valueOf(command.targetType());
        FeedbackType feedbackType = FeedbackType.valueOf(command.feedbackType());
        FeedbackPayload payload = FeedbackPayload.of(command.payload());

        return FeedbackQueue.forNew(
                targetType, command.targetId(), feedbackType, payload, riskLevel, now);
    }

    /**
     * 현재 시간 반환 (Domain 업데이트 시 사용)
     *
     * <p>SVC-004: Service에서 TimeProvider 직접 의존 금지, Factory에서 처리.
     *
     * @return 현재 Instant
     */
    public Instant now() {
        return timeProvider.now();
    }
}
