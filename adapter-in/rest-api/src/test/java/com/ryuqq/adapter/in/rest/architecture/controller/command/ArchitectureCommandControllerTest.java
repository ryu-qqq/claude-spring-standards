package com.ryuqq.adapter.in.rest.architecture.controller.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.architecture.ArchitectureApiEndpoints;
import com.ryuqq.adapter.in.rest.architecture.dto.request.CreateArchitectureApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.request.UpdateArchitectureApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.response.ArchitectureIdApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.CreateArchitectureApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateArchitectureApiRequestFixture;
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
 * ArchitectureCommandController 통합 테스트
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
@DisplayName("ArchitectureCommandController 통합 테스트")
class ArchitectureCommandControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("POST /api/v1/mcp/architectures - Architecture 생성")
    class CreateArchitecture {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() {
            // Given
            CreateArchitectureApiRequest request = CreateArchitectureApiRequestFixture.valid();
            HttpEntity<CreateArchitectureApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<ArchitectureIdApiResponse>> response =
                    restTemplate.exchange(
                            ArchitectureApiEndpoints.ARCHITECTURES,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<ArchitectureIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() {
            // Given
            CreateArchitectureApiRequest request =
                    CreateArchitectureApiRequestFixture.invalidWithBlankName();
            HttpEntity<CreateArchitectureApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<ArchitectureIdApiResponse>> response =
                    restTemplate.exchange(
                            ArchitectureApiEndpoints.ARCHITECTURES,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<ArchitectureIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/architectures/{architectureId} - Architecture 수정")
    class UpdateArchitecture {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long architectureId = 1L;
            UpdateArchitectureApiRequest request = UpdateArchitectureApiRequestFixture.valid();
            HttpEntity<UpdateArchitectureApiRequest> httpEntity = new HttpEntity<>(request);
            String url = ArchitectureApiEndpoints.ARCHITECTURES + "/" + architectureId;

            // When
            ResponseEntity<ApiResponse<Void>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PUT,
                            httpEntity,
                            new ParameterizedTypeReference<ApiResponse<Void>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() {
            // Given
            Long architectureId = 1L;
            UpdateArchitectureApiRequest request =
                    UpdateArchitectureApiRequestFixture.invalidWithBlankName();
            HttpEntity<UpdateArchitectureApiRequest> httpEntity = new HttpEntity<>(request);
            String url = ArchitectureApiEndpoints.ARCHITECTURES + "/" + architectureId;

            // When
            ResponseEntity<ApiResponse<Void>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PUT,
                            httpEntity,
                            new ParameterizedTypeReference<ApiResponse<Void>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
