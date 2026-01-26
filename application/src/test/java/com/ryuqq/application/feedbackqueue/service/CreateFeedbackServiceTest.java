package com.ryuqq.application.feedbackqueue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.factory.command.FeedbackQueueCommandFactory;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidator;
import com.ryuqq.application.feedbackqueue.internal.validator.payload.FeedbackPayloadValidatorResolver;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueuePersistenceManager;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.exception.InvalidFeedbackPayloadException;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackTargetType;
import com.ryuqq.domain.feedbackqueue.vo.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateFeedbackService 단위 테스트
 *
 * <p>Feedback 생성 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("CreateFeedbackService 단위 테스트")
class CreateFeedbackServiceTest {

    @Mock private FeedbackQueueCommandFactory feedbackQueueCommandFactory;

    @Mock private FeedbackQueuePersistenceManager feedbackQueuePersistenceManager;

    @Mock private FeedbackPayloadValidatorResolver validatorResolver;

    @Mock private FeedbackPayloadValidator payloadValidator;

    @Mock private FeedbackQueue feedbackQueue;

    private CreateFeedbackService sut;

    @BeforeEach
    void setUp() {
        sut =
                new CreateFeedbackService(
                        feedbackQueueCommandFactory,
                        feedbackQueuePersistenceManager,
                        validatorResolver);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - 유효한 Command로 Feedback 생성")
        void execute_WithValidCommand_ShouldCreateFeedback() {
            // given
            CreateFeedbackCommand command = createDefaultCommand();
            FeedbackTargetType targetType = FeedbackTargetType.valueOf(command.targetType());
            RiskLevel riskLevel = RiskLevel.SAFE;
            FeedbackQueueId savedId = FeedbackQueueId.of(1L);

            given(validatorResolver.resolve(targetType)).willReturn(payloadValidator);
            willDoNothing().given(payloadValidator).validate(command);
            given(feedbackQueueCommandFactory.create(command, riskLevel)).willReturn(feedbackQueue);
            given(feedbackQueuePersistenceManager.persist(feedbackQueue)).willReturn(savedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(savedId.value());

            then(validatorResolver).should().resolve(targetType);
            then(payloadValidator).should().validate(command);
            then(feedbackQueueCommandFactory).should().create(command, riskLevel);
            then(feedbackQueuePersistenceManager).should().persist(feedbackQueue);
        }

        @Test
        @DisplayName("실패 - 잘못된 페이로드인 경우")
        void execute_WhenInvalidPayload_ShouldThrowException() {
            // given
            CreateFeedbackCommand command = createDefaultCommand();
            FeedbackTargetType targetType = FeedbackTargetType.valueOf(command.targetType());

            given(validatorResolver.resolve(targetType)).willReturn(payloadValidator);
            willThrow(
                            new InvalidFeedbackPayloadException(
                                    targetType,
                                    command.feedbackType(),
                                    "Invalid payload structure"))
                    .given(payloadValidator)
                    .validate(command);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(InvalidFeedbackPayloadException.class);

            then(feedbackQueueCommandFactory).shouldHaveNoInteractions();
            then(feedbackQueuePersistenceManager).shouldHaveNoInteractions();
        }
    }

    private CreateFeedbackCommand createDefaultCommand() {
        return new CreateFeedbackCommand(
                "RULE_EXAMPLE",
                1L,
                "ADD",
                "{\"ruleId\":1,\"exampleType\":\"GOOD\",\"code\":\"...\"}");
    }
}
