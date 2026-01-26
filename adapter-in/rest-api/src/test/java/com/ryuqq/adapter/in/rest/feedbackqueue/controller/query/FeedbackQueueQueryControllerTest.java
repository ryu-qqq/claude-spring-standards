package com.ryuqq.adapter.in.rest.feedbackqueue.controller.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.FeedbackQueueApiEndpoints;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * FeedbackQueueQueryController 통합 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Query Parameter 검증
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
@DisplayName("FeedbackQueueQueryController 통합 테스트")
class FeedbackQueueQueryControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("GET / - FeedbackQueue 복합 조건 조회 (커서 기반)")
    class SearchFeedbacksByCursor {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            String url = FeedbackQueueApiEndpoints.BASE + "?size=20";

            // When
            ResponseEntity<ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("status 필터 적용 시 200 OK 반환")
        void withStatusFilter_ShouldReturn200() {
            // Given
            String url = FeedbackQueueApiEndpoints.BASE + "?statuses=PENDING&size=20";

            // When
            ResponseEntity<ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("riskLevel 필터 적용 시 200 OK 반환")
        void withRiskLevelFilter_ShouldReturn200() {
            // Given
            String url = FeedbackQueueApiEndpoints.BASE + "?riskLevels=MEDIUM&size=20";

            // When
            ResponseEntity<ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("targetType 필터 적용 시 200 OK 반환")
        void withTargetTypeFilter_ShouldReturn200() {
            // Given
            String url = FeedbackQueueApiEndpoints.BASE + "?targetTypes=CODING_RULE&size=20";

            // When
            ResponseEntity<ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("size가 범위를 벗어나면 400 Bad Request 반환")
        void sizeOutOfRange_ShouldReturn400() {
            // When
            ResponseEntity<ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>> response =
                    restTemplate.exchange(
                            FeedbackQueueApiEndpoints.BASE + "?size=101",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
