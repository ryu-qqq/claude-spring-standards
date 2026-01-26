package com.ryuqq.adapter.in.rest.codingrule.controller.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.codingrule.CodingRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleApiResponse;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleIndexApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import java.util.List;
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
 * CodingRuleQueryController 통합 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Path Variable 검증
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
@DisplayName("CodingRuleQueryController 통합 테스트")
class CodingRuleQueryControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("GET /api/v1/mcp/coding-rules - CodingRule 목록 조회")
    class GetAllCodingRules {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            String url = CodingRuleApiEndpoints.BASE + "?size=20";

            // When
            ResponseEntity<ApiResponse<SliceApiResponse<CodingRuleApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<CodingRuleApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("size가 범위를 벗어나면 400 Bad Request 반환")
        void sizeOutOfRange_ShouldReturn400() {
            // When
            ResponseEntity<ApiResponse<SliceApiResponse<CodingRuleApiResponse>>> response =
                    restTemplate.exchange(
                            CodingRuleApiEndpoints.BASE + "?size=101",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<CodingRuleApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/templates/coding-rules/index - CodingRule 인덱스 조회")
    class GetCodingRuleIndex {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            String url = CodingRuleApiEndpoints.BASE + "/index";

            // When
            ResponseEntity<ApiResponse<List<CodingRuleIndexApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<List<CodingRuleIndexApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("severities 필터 요청 시 200 OK 반환")
        void withSeverities_ShouldReturn200() {
            // Given
            String url = CodingRuleApiEndpoints.BASE + "/index?severities=BLOCKER,CRITICAL";

            // When
            ResponseEntity<ApiResponse<List<CodingRuleIndexApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<List<CodingRuleIndexApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("categories 필터 요청 시 200 OK 반환")
        void withCategories_ShouldReturn200() {
            // Given
            String url = CodingRuleApiEndpoints.BASE + "/index?categories=ANNOTATION,STRUCTURE";

            // When
            ResponseEntity<ApiResponse<List<CodingRuleIndexApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<List<CodingRuleIndexApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("conventionId 필터 요청 시 200 OK 반환")
        void withConventionId_ShouldReturn200() {
            // Given
            String url = CodingRuleApiEndpoints.BASE + "/index?conventionId=1";

            // When
            ResponseEntity<ApiResponse<List<CodingRuleIndexApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<List<CodingRuleIndexApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }
}
