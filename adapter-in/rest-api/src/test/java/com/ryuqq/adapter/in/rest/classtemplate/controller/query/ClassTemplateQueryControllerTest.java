package com.ryuqq.adapter.in.rest.classtemplate.controller.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.classtemplate.ClassTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.classtemplate.dto.response.ClassTemplateApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
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
 * ClassTemplateQueryController 통합 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>HTTP 요청/응답 매핑
 *   <li>Path Variable 검증
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
@DisplayName("ClassTemplateQueryController 통합 테스트")
class ClassTemplateQueryControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("GET /api/v1/mcp/class-templates - ClassTemplate 목록 조회")
    class GetAllClassTemplates {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            String url = ClassTemplateApiEndpoints.CLASS_TEMPLATES + "?size=20";

            // When
            ResponseEntity<ApiResponse<SliceApiResponse<ClassTemplateApiResponse>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<ClassTemplateApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("size가 범위를 벗어나면 400 Bad Request 반환")
        void sizeOutOfRange_ShouldReturn400() {
            // When
            ResponseEntity<ApiResponse<SliceApiResponse<ClassTemplateApiResponse>>> response =
                    restTemplate.exchange(
                            ClassTemplateApiEndpoints.CLASS_TEMPLATES + "?size=101",
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<
                                    ApiResponse<SliceApiResponse<ClassTemplateApiResponse>>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
