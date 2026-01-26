package com.ryuqq.application.feedbackqueue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.application.feedbackqueue.assembler.FeedbackQueueAssembler;
import com.ryuqq.application.feedbackqueue.dto.command.MergeFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.factory.command.FeedbackQueueCommandFactory;
import com.ryuqq.application.feedbackqueue.fixture.FeedbackQueueResultFixture;
import com.ryuqq.application.feedbackqueue.fixture.MergeFeedbackCommandFixture;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategy;
import com.ryuqq.application.feedbackqueue.internal.strategy.FeedbackMergeStrategyResolver;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidator;
import com.ryuqq.application.feedbackqueue.internal.validator.merge.FeedbackMergeValidatorResolver;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueuePersistenceManager;
import com.ryuqq.application.feedbackqueue.validator.FeedbackQueueValidator;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.fixture.FeedbackQueueFixture;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * MergeFeedbackService 단위 테스트
 *
 * <p>피드백 머지 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("MergeFeedbackService 단위 테스트")
class MergeFeedbackServiceTest {

    @Mock private FeedbackQueueValidator feedbackQueueValidator;

    @Mock private FeedbackQueuePersistenceManager feedbackQueuePersistenceManager;

    @Mock private FeedbackQueueAssembler feedbackQueueAssembler;

    @Mock private FeedbackQueueCommandFactory feedbackQueueCommandFactory;

    @Mock private FeedbackMergeStrategyResolver feedbackMergeStrategyResolver;

    @Mock private FeedbackMergeValidatorResolver feedbackMergeValidatorResolver;

    @Mock private FeedbackMergeStrategy mergeStrategy;

    @Mock private FeedbackMergeValidator mergeValidator;

    private MergeFeedbackService sut;

    @BeforeEach
    void setUp() {
        sut =
                new MergeFeedbackService(
                        feedbackQueueValidator,
                        feedbackQueuePersistenceManager,
                        feedbackQueueAssembler,
                        feedbackQueueCommandFactory,
                        feedbackMergeStrategyResolver,
                        feedbackMergeValidatorResolver);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - SAFE 위험도 피드백 머지")
        void execute_WithSafeFeedback_ShouldMerge() {
            // given
            Long feedbackId = 1L;
            MergeFeedbackCommand command = MergeFeedbackCommandFixture.withFeedbackId(feedbackId);
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedSafeFeedback();
            FeedbackQueueResult expectedResult = FeedbackQueueResultFixture.defaultResult();
            Instant now = Instant.now();

            given(feedbackQueueValidator.getAndValidateForMerge(feedbackId))
                    .willReturn(feedbackQueue);
            given(feedbackMergeValidatorResolver.resolve(feedbackQueue.targetType()))
                    .willReturn(mergeValidator);
            willDoNothing().given(mergeValidator).validate(feedbackQueue);
            given(feedbackMergeStrategyResolver.resolve(feedbackQueue.targetType()))
                    .willReturn(mergeStrategy);
            given(mergeStrategy.merge(feedbackQueue)).willReturn(100L);
            given(feedbackQueueCommandFactory.now()).willReturn(now);
            given(feedbackQueueAssembler.toResult(feedbackQueue)).willReturn(expectedResult);

            // when
            FeedbackQueueResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(feedbackQueueValidator).should().getAndValidateForMerge(feedbackId);
            then(feedbackMergeValidatorResolver).should().resolve(feedbackQueue.targetType());
            then(mergeValidator).should().validate(feedbackQueue);
            then(feedbackMergeStrategyResolver).should().resolve(feedbackQueue.targetType());
            then(mergeStrategy).should().merge(feedbackQueue);
            then(feedbackQueuePersistenceManager).should().persist(feedbackQueue);
            then(feedbackQueueAssembler).should().toResult(feedbackQueue);
        }

        @Test
        @DisplayName("성공 - MEDIUM 위험도 피드백 머지 (사람 승인 후)")
        void execute_WithHumanApprovedFeedback_ShouldMerge() {
            // given
            Long feedbackId = 2L;
            MergeFeedbackCommand command = MergeFeedbackCommandFixture.withFeedbackId(feedbackId);
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.humanApprovedFeedback();
            FeedbackQueueResult expectedResult = FeedbackQueueResultFixture.defaultResult();
            Instant now = Instant.now();

            given(feedbackQueueValidator.getAndValidateForMerge(feedbackId))
                    .willReturn(feedbackQueue);
            given(feedbackMergeValidatorResolver.resolve(feedbackQueue.targetType()))
                    .willReturn(mergeValidator);
            willDoNothing().given(mergeValidator).validate(feedbackQueue);
            given(feedbackMergeStrategyResolver.resolve(feedbackQueue.targetType()))
                    .willReturn(mergeStrategy);
            given(mergeStrategy.merge(feedbackQueue)).willReturn(200L);
            given(feedbackQueueCommandFactory.now()).willReturn(now);
            given(feedbackQueueAssembler.toResult(feedbackQueue)).willReturn(expectedResult);

            // when
            FeedbackQueueResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(feedbackQueuePersistenceManager).should().persist(feedbackQueue);
        }
    }
}
