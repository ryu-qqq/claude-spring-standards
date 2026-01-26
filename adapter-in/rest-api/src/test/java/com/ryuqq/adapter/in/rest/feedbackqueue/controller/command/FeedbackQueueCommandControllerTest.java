package com.ryuqq.adapter.in.rest.feedbackqueue.controller.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.FeedbackQueueApiEndpoints;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.CreateFeedbackApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.RejectFeedbackApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueIdApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.CreateFeedbackApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.RejectFeedbackApiRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * FeedbackQueueCommandController 통합 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Request DTO Validation
 *   <li>Response DTO 직렬화
 *   <li>HTTP Status Code
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TestRestApiApplication.class)
@ActiveProfiles("test")
@DisplayName("FeedbackQueueCommandController 통합 테스트")
class FeedbackQueueCommandControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("POST /api/v1/templates/feedback-queue - 피드백 생성")
    class CreateFeedback {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() {
            // Given
            CreateFeedbackApiRequest request = CreateFeedbackApiRequestFixture.valid();
            HttpEntity<CreateFeedbackApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<FeedbackQueueIdApiResponse>> response =
                    restTemplate.exchange(
                            FeedbackQueueApiEndpoints.BASE,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<FeedbackQueueIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() {
            // Given
            CreateFeedbackApiRequest request =
                    CreateFeedbackApiRequestFixture.invalidWithBlankTargetType();
            HttpEntity<CreateFeedbackApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<FeedbackQueueIdApiResponse>> response =
                    restTemplate.exchange(
                            FeedbackQueueApiEndpoints.BASE,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<FeedbackQueueIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("PATCH /{feedbackQueueId}/llm-approve - LLM 1차 승인")
    class LlmApprove {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long feedbackQueueId = 1L;
            String url = FeedbackQueueApiEndpoints.BASE + "/" + feedbackQueueId + "/llm-approve";

            // When
            ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PATCH,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<FeedbackQueueApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }
    }

    @Nested
    @DisplayName("PATCH /{feedbackQueueId}/llm-reject - LLM 1차 거절")
    class LlmReject {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long feedbackQueueId = 1L;
            RejectFeedbackApiRequest request = RejectFeedbackApiRequestFixture.valid();
            HttpEntity<RejectFeedbackApiRequest> httpEntity = new HttpEntity<>(request);
            String url = FeedbackQueueApiEndpoints.BASE + "/" + feedbackQueueId + "/llm-reject";

            // When
            ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PATCH,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<FeedbackQueueApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        @DisplayName("reviewNotes 없이도 200 OK 반환")
        void withoutReviewNotes_ShouldReturn200() {
            // Given
            Long feedbackQueueId = 1L;
            String url = FeedbackQueueApiEndpoints.BASE + "/" + feedbackQueueId + "/llm-reject";

            // When
            ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PATCH,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<FeedbackQueueApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("PATCH /{feedbackQueueId}/human-approve - Human 2차 승인")
    class HumanApprove {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long feedbackQueueId = 1L;
            String url = FeedbackQueueApiEndpoints.BASE + "/" + feedbackQueueId + "/human-approve";

            // When
            ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PATCH,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<FeedbackQueueApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
    }

    @Nested
    @DisplayName("PATCH /{feedbackQueueId}/human-reject - Human 2차 거절")
    class HumanReject {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long feedbackQueueId = 1L;
            RejectFeedbackApiRequest request = RejectFeedbackApiRequestFixture.valid();
            HttpEntity<RejectFeedbackApiRequest> httpEntity = new HttpEntity<>(request);
            String url = FeedbackQueueApiEndpoints.BASE + "/" + feedbackQueueId + "/human-reject";

            // When
            ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PATCH,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<FeedbackQueueApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
    }

    @Nested
    @DisplayName("POST /{feedbackQueueId}/merge - 피드백 머지")
    class Merge {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long feedbackQueueId = 1L;
            String url = FeedbackQueueApiEndpoints.BASE + "/" + feedbackQueueId + "/merge";

            // When
            ResponseEntity<ApiResponse<FeedbackQueueApiResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<FeedbackQueueApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
    }
}
