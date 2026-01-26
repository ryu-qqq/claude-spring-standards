package com.ryuqq.application.feedbackqueue.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSummary;
import com.ryuqq.domain.feedbackqueue.aggregate.FeedbackQueue;
import com.ryuqq.domain.feedbackqueue.fixture.FeedbackQueueFixture;
import com.ryuqq.domain.feedbackqueue.id.FeedbackQueueId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * FeedbackQueueAssembler 단위 테스트
 *
 * <p>피드백 큐 응답 조립기의 변환 로직을 검증합니다.
 *
 * @author development-team
 */
@Tag("unit")
@Tag("assembler")
@Tag("application-layer")
@DisplayName("FeedbackQueueAssembler 단위 테스트")
class FeedbackQueueAssemblerTest {

    private FeedbackQueueAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new FeedbackQueueAssembler();
    }

    @Nested
    @DisplayName("toResult 메서드")
    class ToResult {

        @Test
        @DisplayName("성공 - FeedbackQueue를 FeedbackQueueResult로 변환")
        void toResult_WithValidFeedbackQueue_ShouldReturnResult() {
            // given
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedSafeFeedback();

            // when
            FeedbackQueueResult result = sut.toResult(feedbackQueue);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(feedbackQueue.idValue());
            assertThat(result.targetType()).isEqualTo(feedbackQueue.targetType().name());
            assertThat(result.status()).isEqualTo(feedbackQueue.status().name());
        }
    }

    @Nested
    @DisplayName("toResults 메서드")
    class ToResults {

        @Test
        @DisplayName("성공 - FeedbackQueue 목록을 FeedbackQueueResult 목록으로 변환")
        void toResults_WithValidFeedbackQueues_ShouldReturnResults() {
            // given
            FeedbackQueue feedbackQueue1 = FeedbackQueueFixture.llmApprovedSafeFeedback();
            FeedbackQueue feedbackQueue2 = FeedbackQueueFixture.pendingMediumFeedback();
            feedbackQueue2.assignId(FeedbackQueueId.of(2L));
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue1, feedbackQueue2);

            // when
            List<FeedbackQueueResult> results = sut.toResults(feedbackQueues);

            // then
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("성공 - 빈 목록 처리")
        void toResults_WithEmptyList_ShouldReturnEmptyList() {
            // given
            List<FeedbackQueue> feedbackQueues = List.of();

            // when
            List<FeedbackQueueResult> results = sut.toResults(feedbackQueues);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSummary 메서드")
    class ToSummary {

        @Test
        @DisplayName("성공 - FeedbackQueue를 FeedbackQueueSummary로 변환")
        void toSummary_WithValidFeedbackQueue_ShouldReturnSummary() {
            // given
            FeedbackQueue feedbackQueue = FeedbackQueueFixture.llmApprovedSafeFeedback();

            // when
            FeedbackQueueSummary summary = sut.toSummary(feedbackQueue);

            // then
            assertThat(summary).isNotNull();
        }
    }

    @Nested
    @DisplayName("toSummaries 메서드")
    class ToSummaries {

        @Test
        @DisplayName("성공 - FeedbackQueue 목록을 FeedbackQueueSummary 목록으로 변환")
        void toSummaries_WithValidFeedbackQueues_ShouldReturnSummaries() {
            // given
            FeedbackQueue feedbackQueue1 = FeedbackQueueFixture.llmApprovedSafeFeedback();
            FeedbackQueue feedbackQueue2 = FeedbackQueueFixture.pendingMediumFeedback();
            feedbackQueue2.assignId(FeedbackQueueId.of(2L));
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue1, feedbackQueue2);

            // when
            List<FeedbackQueueSummary> summaries = sut.toSummaries(feedbackQueues);

            // then
            assertThat(summaries).hasSize(2);
        }
    }

    @Nested
    @DisplayName("toSliceResult 메서드")
    class ToSliceResult {

        @Test
        @DisplayName("성공 - hasNext가 true인 경우")
        void toSliceResult_WhenHasNext_ShouldReturnSliceWithHasNextTrue() {
            // given
            FeedbackQueue feedbackQueue1 = FeedbackQueueFixture.llmApprovedSafeFeedback();
            FeedbackQueue feedbackQueue2 = FeedbackQueueFixture.pendingMediumFeedback();
            feedbackQueue2.assignId(FeedbackQueueId.of(2L));
            FeedbackQueue feedbackQueue3 = FeedbackQueueFixture.humanApprovedFeedback();
            List<FeedbackQueue> feedbackQueues =
                    List.of(feedbackQueue1, feedbackQueue2, feedbackQueue3);
            int requestedSize = 2;

            // when
            FeedbackQueueSliceResult result = sut.toSliceResult(feedbackQueues, requestedSize);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("성공 - hasNext가 false인 경우")
        void toSliceResult_WhenNoNext_ShouldReturnSliceWithHasNextFalse() {
            // given
            FeedbackQueue feedbackQueue1 = FeedbackQueueFixture.llmApprovedSafeFeedback();
            List<FeedbackQueue> feedbackQueues = List.of(feedbackQueue1);
            int requestedSize = 10;

            // when
            FeedbackQueueSliceResult result = sut.toSliceResult(feedbackQueues, requestedSize);

            // then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.content()).hasSize(1);
        }
    }
}
