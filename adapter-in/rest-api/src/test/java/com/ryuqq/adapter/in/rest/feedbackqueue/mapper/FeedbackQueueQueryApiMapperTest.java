package com.ryuqq.adapter.in.rest.feedbackqueue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.SearchFeedbacksCursorApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.SearchFeedbacksCursorApiRequestFixture;
import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * FeedbackQueueQueryApiMapper 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Request DTO -> Query DTO 변환
 *   <li>Result DTO -> Response DTO 변환
 *   <li>SliceResult -> SliceApiResponse 변환
 *   <li>null 처리 및 기본값 설정
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("FeedbackQueueQueryApiMapper 단위 테스트")
class FeedbackQueueQueryApiMapperTest {

    private FeedbackQueueQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FeedbackQueueQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams(SearchFeedbacksCursorApiRequest)")
    class ToSearchParams {

        @Test
        @DisplayName("정상 요청 변환 - size가 있으면 그대로 사용")
        void validRequest_ShouldMapSize() {
            // Given
            SearchFeedbacksCursorApiRequest request =
                    SearchFeedbacksCursorApiRequestFixture.valid();

            // When
            FeedbackQueueSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.cursor()).isNull();
            assertThat(searchParams.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("size가 null이면 기본값 20 사용")
        void nullSize_ShouldUseDefaultSize() {
            // Given
            SearchFeedbacksCursorApiRequest request =
                    SearchFeedbacksCursorApiRequestFixture.withNullSize();

            // When
            FeedbackQueueSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("size가 0이면 기본값 20 사용")
        void zeroSize_ShouldUseDefaultSize() {
            // Given
            SearchFeedbacksCursorApiRequest request =
                    SearchFeedbacksCursorApiRequestFixture.withZeroSize();

            // When
            FeedbackQueueSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("모든 필터 적용")
        void withAllFilters_ShouldMapCorrectly() {
            // Given
            SearchFeedbacksCursorApiRequest request =
                    SearchFeedbacksCursorApiRequestFixture.withAllFilters();

            // When
            FeedbackQueueSearchParams searchParams = mapper.toSearchParams(request);

            // Then
            assertThat(searchParams.targetTypes()).contains("CODING_RULE");
            assertThat(searchParams.statuses()).contains("PENDING", "LLM_APPROVED");
            assertThat(searchParams.riskLevels()).contains("SAFE", "MEDIUM");
            assertThat(searchParams.feedbackTypes()).contains("ADD", "MODIFY");
            assertThat(searchParams.actions()).contains("LLM_APPROVE", "HUMAN_REJECT");
        }
    }

    @Nested
    @DisplayName("toResponse(FeedbackQueueResult)")
    class ToResponse {

        @Test
        @DisplayName("정상 변환 - 모든 필드 매핑")
        void validResult_ShouldMapAllFields() {
            // Given
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "PENDING_LLM",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));

            // When
            FeedbackQueueApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.feedbackQueueId()).isEqualTo(1L);
            assertThat(response.targetType()).isEqualTo("CODING_RULE");
            assertThat(response.targetId()).isEqualTo(1L);
            assertThat(response.feedbackType()).isEqualTo("CREATE");
            assertThat(response.riskLevel()).isEqualTo("LOW");
            assertThat(response.payload()).isEqualTo("{\"code\":\"AGG-001\"}");
            assertThat(response.status()).isEqualTo("PENDING_LLM");
            assertThat(response.reviewNotes()).isNull();
            assertThat(response.createdAt()).isEqualTo("2024-01-01T09:00:00+09:00");
            assertThat(response.updatedAt()).isEqualTo("2024-01-01T09:00:00+09:00");
        }

        @Test
        @DisplayName("reviewNotes가 있으면 변환")
        void withReviewNotes_ShouldMapReviewNotes() {
            // Given
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "LLM_REJECTED",
                            "Invalid format",
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));

            // When
            FeedbackQueueApiResponse response = mapper.toResponse(result);

            // Then
            assertThat(response.reviewNotes()).isEqualTo("Invalid format");
        }
    }

    @Nested
    @DisplayName("toResponses(List<FeedbackQueueResult>)")
    class ToResponses {

        @Test
        @DisplayName("다중 항목 변환")
        void multipleItems_ShouldMapAll() {
            // Given
            var result1 =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "PENDING_LLM",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var result2 =
                    new FeedbackQueueResult(
                            2L,
                            "CLASS_TEMPLATE",
                            2L,
                            "UPDATE",
                            "MEDIUM",
                            "{\"classType\":\"AGGREGATE\"}",
                            "LLM_APPROVED",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));

            // When
            List<FeedbackQueueApiResponse> responses =
                    mapper.toResponses(List.of(result1, result2));

            // Then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).feedbackQueueId()).isEqualTo(1L);
            assertThat(responses.get(1).feedbackQueueId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 리스트 처리")
        void emptyList_ShouldReturnEmptyList() {
            // When
            List<FeedbackQueueApiResponse> responses = mapper.toResponses(List.of());

            // Then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toSliceResponse(FeedbackQueueSliceResult)")
    class ToSliceResponse {

        @Test
        @DisplayName("정상 변환 - 다음 페이지 있음")
        void withNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "PENDING_LLM",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var sliceResult = FeedbackQueueSliceResult.of(List.of(result), true);

            // When
            SliceApiResponse<FeedbackQueueApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("1");
        }

        @Test
        @DisplayName("다음 페이지 없음")
        void withoutNextPage_ShouldMapCorrectly() {
            // Given
            var result =
                    new FeedbackQueueResult(
                            1L,
                            "CODING_RULE",
                            1L,
                            "CREATE",
                            "LOW",
                            "{\"code\":\"AGG-001\"}",
                            "PENDING_LLM",
                            null,
                            Instant.parse("2024-01-01T00:00:00Z"),
                            Instant.parse("2024-01-01T00:00:00Z"));
            var sliceResult = FeedbackQueueSliceResult.of(List.of(result), false);

            // When
            SliceApiResponse<FeedbackQueueApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }

        @Test
        @DisplayName("빈 결과")
        void emptyResult_ShouldMapCorrectly() {
            // Given
            var sliceResult = FeedbackQueueSliceResult.empty();

            // When
            SliceApiResponse<FeedbackQueueApiResponse> response =
                    mapper.toSliceResponse(sliceResult);

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }
    }
}
