package com.ryuqq.adapter.in.rest.feedbackqueue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.CreateFeedbackApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.RejectFeedbackApiRequest;
import com.ryuqq.adapter.in.rest.fixture.request.CreateFeedbackApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.RejectFeedbackApiRequestFixture;
import com.ryuqq.application.feedbackqueue.dto.command.CreateFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.command.MergeFeedbackCommand;
import com.ryuqq.application.feedbackqueue.dto.command.ProcessFeedbackCommand;
import com.ryuqq.domain.feedbackqueue.vo.FeedbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * FeedbackQueueCommandApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO -> Command DTO 변환
 *   <li>null 처리 (reviewNotes)
 *   <li>필드 매핑 정확성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("FeedbackQueueCommandApiMapper 단위 테스트")
class FeedbackQueueCommandApiMapperTest {

    private FeedbackQueueCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FeedbackQueueCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateFeedbackApiRequest)")
    class ToCreateCommand {

        @Test
        @DisplayName("정상 요청 변환 - 모든 필드 포함")
        void validRequest_ShouldMapAllFields() {
            // Given
            CreateFeedbackApiRequest request = CreateFeedbackApiRequestFixture.valid();

            // When
            CreateFeedbackCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.targetType()).isEqualTo("CODING_RULE");
            assertThat(command.targetId()).isEqualTo(1L);
            assertThat(command.feedbackType()).isEqualTo("CREATE");
            assertThat(command.payload())
                    .isEqualTo("{\"code\":\"AGG-001\",\"name\":\"Lombok 사용 금지\"}");
        }

        @Test
        @DisplayName("UPDATE 타입 변환")
        void updateType_ShouldMapCorrectly() {
            // Given
            CreateFeedbackApiRequest request = CreateFeedbackApiRequestFixture.validWithUpdate();

            // When
            CreateFeedbackCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.targetType()).isEqualTo("CLASS_TEMPLATE");
            assertThat(command.targetId()).isEqualTo(2L);
            assertThat(command.feedbackType()).isEqualTo("UPDATE");
        }

        @Test
        @DisplayName("DELETE 타입 변환")
        void deleteType_ShouldMapCorrectly() {
            // Given
            CreateFeedbackApiRequest request = CreateFeedbackApiRequestFixture.validWithDelete();

            // When
            CreateFeedbackCommand command = mapper.toCommand(request);

            // Then
            assertThat(command.targetType()).isEqualTo("RULE_EXAMPLE");
            assertThat(command.targetId()).isEqualTo(3L);
            assertThat(command.feedbackType()).isEqualTo("DELETE");
        }
    }

    @Nested
    @DisplayName("toLlmApproveCommand(Long)")
    class ToLlmApproveCommand {

        @Test
        @DisplayName("LLM 승인 커맨드 생성")
        void shouldCreateLlmApproveCommand() {
            // Given
            Long feedbackQueueId = 1L;

            // When
            ProcessFeedbackCommand command = mapper.toLlmApproveCommand(feedbackQueueId);

            // Then
            assertThat(command.feedbackId()).isEqualTo(1L);
            assertThat(command.action()).isEqualTo(FeedbackAction.LLM_APPROVE);
            assertThat(command.reviewNotes()).isNull();
        }
    }

    @Nested
    @DisplayName("toLlmRejectCommand(Long, RejectFeedbackApiRequest)")
    class ToLlmRejectCommand {

        @Test
        @DisplayName("LLM 거절 커맨드 생성 - reviewNotes 포함")
        void withReviewNotes_ShouldMapCorrectly() {
            // Given
            Long feedbackQueueId = 1L;
            RejectFeedbackApiRequest request = RejectFeedbackApiRequestFixture.valid();

            // When
            ProcessFeedbackCommand command = mapper.toLlmRejectCommand(feedbackQueueId, request);

            // Then
            assertThat(command.feedbackId()).isEqualTo(1L);
            assertThat(command.action()).isEqualTo(FeedbackAction.LLM_REJECT);
            assertThat(command.reviewNotes())
                    .isEqualTo("Invalid feedback content - does not follow coding standards");
        }

        @Test
        @DisplayName("LLM 거절 커맨드 생성 - request가 null")
        void nullRequest_ShouldSetReviewNotesToNull() {
            // Given
            Long feedbackQueueId = 1L;

            // When
            ProcessFeedbackCommand command = mapper.toLlmRejectCommand(feedbackQueueId, null);

            // Then
            assertThat(command.feedbackId()).isEqualTo(1L);
            assertThat(command.action()).isEqualTo(FeedbackAction.LLM_REJECT);
            assertThat(command.reviewNotes()).isNull();
        }

        @Test
        @DisplayName("LLM 거절 커맨드 생성 - reviewNotes가 null")
        void nullReviewNotes_ShouldSetReviewNotesToNull() {
            // Given
            Long feedbackQueueId = 1L;
            RejectFeedbackApiRequest request =
                    RejectFeedbackApiRequestFixture.withNullReviewNotes();

            // When
            ProcessFeedbackCommand command = mapper.toLlmRejectCommand(feedbackQueueId, request);

            // Then
            assertThat(command.reviewNotes()).isNull();
        }
    }

    @Nested
    @DisplayName("toHumanApproveCommand(Long)")
    class ToHumanApproveCommand {

        @Test
        @DisplayName("Human 승인 커맨드 생성")
        void shouldCreateHumanApproveCommand() {
            // Given
            Long feedbackQueueId = 1L;

            // When
            ProcessFeedbackCommand command = mapper.toHumanApproveCommand(feedbackQueueId);

            // Then
            assertThat(command.feedbackId()).isEqualTo(1L);
            assertThat(command.action()).isEqualTo(FeedbackAction.HUMAN_APPROVE);
            assertThat(command.reviewNotes()).isNull();
        }
    }

    @Nested
    @DisplayName("toHumanRejectCommand(Long, RejectFeedbackApiRequest)")
    class ToHumanRejectCommand {

        @Test
        @DisplayName("Human 거절 커맨드 생성 - reviewNotes 포함")
        void withReviewNotes_ShouldMapCorrectly() {
            // Given
            Long feedbackQueueId = 1L;
            RejectFeedbackApiRequest request = RejectFeedbackApiRequestFixture.valid();

            // When
            ProcessFeedbackCommand command = mapper.toHumanRejectCommand(feedbackQueueId, request);

            // Then
            assertThat(command.feedbackId()).isEqualTo(1L);
            assertThat(command.action()).isEqualTo(FeedbackAction.HUMAN_REJECT);
            assertThat(command.reviewNotes())
                    .isEqualTo("Invalid feedback content - does not follow coding standards");
        }

        @Test
        @DisplayName("Human 거절 커맨드 생성 - request가 null")
        void nullRequest_ShouldSetReviewNotesToNull() {
            // Given
            Long feedbackQueueId = 1L;

            // When
            ProcessFeedbackCommand command = mapper.toHumanRejectCommand(feedbackQueueId, null);

            // Then
            assertThat(command.feedbackId()).isEqualTo(1L);
            assertThat(command.action()).isEqualTo(FeedbackAction.HUMAN_REJECT);
            assertThat(command.reviewNotes()).isNull();
        }
    }

    @Nested
    @DisplayName("toMergeCommand(Long)")
    class ToMergeCommand {

        @Test
        @DisplayName("머지 커맨드 생성")
        void shouldCreateMergeCommand() {
            // Given
            Long feedbackQueueId = 1L;

            // When
            MergeFeedbackCommand command = mapper.toMergeCommand(feedbackQueueId);

            // Then
            assertThat(command.feedbackId()).isEqualTo(1L);
        }
    }
}
