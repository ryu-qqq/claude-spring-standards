package com.ryuqq.application.feedbackqueue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.application.feedbackqueue.assembler.FeedbackQueueAssembler;
import com.ryuqq.application.feedbackqueue.dto.command.ProcessFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.factory.command.FeedbackQueueCommandFactory;
import com.ryuqq.application.feedbackqueue.fixture.FeedbackQueueResultFixture;
import com.ryuqq.application.feedbackqueue.fixture.ProcessFeedbackCommandFixture;
import com.ryuqq.application.feedbackqueue.manager.FeedbackQueuePersistenceManager;
import com.ryuqq.application.feedbackqueue.validator.FeedbackQueueValidator;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.fixture.FeedbackQueueFixture;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
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
 * ProcessFeedbackService 단위 테스트
 *
 * <p>피드백 처리 서비스의 오케스트레이션 로직을 검증합니다.
 *
 * @author development-team
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
@Tag("application-layer")
@DisplayName("ProcessFeedbackService 단위 테스트")
class ProcessFeedbackServiceTest {

    @Mock private FeedbackQueueValidator feedbackQueueValidator;

    @Mock private FeedbackQueuePersistenceManager feedbackQueuePersistenceManager;

    @Mock private FeedbackQueueAssembler feedbackQueueAssembler;

    @Mock private FeedbackQueueCommandFactory feedbackQueueCommandFactory;

    private ProcessFeedbackService sut;

    @BeforeEach
    void setUp() {
        sut =
                new ProcessFeedbackService(
                        feedbackQueueValidator,
                        feedbackQueuePersistenceManager,
                        feedbackQueueAssembler,
                        feedbackQueueCommandFactory);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공 - LLM_APPROVE 액션 처리")
        void execute_WithLlmApprove_ShouldProcessFeedback() {
            // given
            Long feedbackId = 1L;
            ProcessFeedbackCommand command =
                    ProcessFeedbackCommandFixture.llmApproveCommand(feedbackId);
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.pendingSafeFeedback();
            feedbackQueue.assignId(FeedbackQueueId.of(feedbackId));
            FeedbackQueueResult expectedResult = FeedbackQueueResultFixture.defaultResult();
            Instant now = Instant.now();

            given(
                            feedbackQueueValidator.getAndValidateForProcess(
                                    feedbackId, FeedbackAction.LLM_APPROVE))
                    .willReturn(feedbackQueue);
            given(feedbackQueueCommandFactory.now()).willReturn(now);
            given(feedbackQueueAssembler.toResult(feedbackQueue)).willReturn(expectedResult);

            // when
            FeedbackQueueResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(feedbackQueueValidator)
                    .should()
                    .getAndValidateForProcess(feedbackId, FeedbackAction.LLM_APPROVE);
            then(feedbackQueuePersistenceManager).should().persist(feedbackQueue);
            then(feedbackQueueAssembler).should().toResult(feedbackQueue);
        }

        @Test
        @DisplayName("성공 - LLM_REJECT 액션 처리")
        void execute_WithLlmReject_ShouldProcessFeedback() {
            // given
            Long feedbackId = 1L;
            ProcessFeedbackCommand command =
                    ProcessFeedbackCommandFixture.llmRejectCommand(feedbackId);
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.pendingSafeFeedback();
            feedbackQueue.assignId(FeedbackQueueId.of(feedbackId));
            FeedbackQueueResult expectedResult = FeedbackQueueResultFixture.defaultResult();
            Instant now = Instant.now();

            given(
                            feedbackQueueValidator.getAndValidateForProcess(
                                    feedbackId, FeedbackAction.LLM_REJECT))
                    .willReturn(feedbackQueue);
            given(feedbackQueueCommandFactory.now()).willReturn(now);
            given(feedbackQueueAssembler.toResult(feedbackQueue)).willReturn(expectedResult);

            // when
            FeedbackQueueResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(feedbackQueuePersistenceManager).should().persist(feedbackQueue);
        }

        @Test
        @DisplayName("성공 - HUMAN_APPROVE 액션 처리")
        void execute_WithHumanApprove_ShouldProcessFeedback() {
            // given
            Long feedbackId = 2L;
            ProcessFeedbackCommand command =
                    ProcessFeedbackCommandFixture.humanApproveCommand(feedbackId);
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedMediumFeedback();
            FeedbackQueueResult expectedResult = FeedbackQueueResultFixture.defaultResult();
            Instant now = Instant.now();

            given(
                            feedbackQueueValidator.getAndValidateForProcess(
                                    feedbackId, FeedbackAction.HUMAN_APPROVE))
                    .willReturn(feedbackQueue);
            given(feedbackQueueCommandFactory.now()).willReturn(now);
            given(feedbackQueueAssembler.toResult(feedbackQueue)).willReturn(expectedResult);

            // when
            FeedbackQueueResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(feedbackQueuePersistenceManager).should().persist(feedbackQueue);
        }

        @Test
        @DisplayName("성공 - HUMAN_REJECT 액션 처리")
        void execute_WithHumanReject_ShouldProcessFeedback() {
            // given
            Long feedbackId = 2L;
            ProcessFeedbackCommand command =
                    ProcessFeedbackCommandFixture.humanRejectCommand(feedbackId);
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedMediumFeedback();
            FeedbackQueueResult expectedResult = FeedbackQueueResultFixture.defaultResult();
            Instant now = Instant.now();

            given(
                            feedbackQueueValidator.getAndValidateForProcess(
                                    feedbackId, FeedbackAction.HUMAN_REJECT))
                    .willReturn(feedbackQueue);
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
