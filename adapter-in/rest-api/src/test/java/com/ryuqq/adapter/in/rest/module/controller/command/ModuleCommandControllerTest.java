package com.ryuqq.adapter.in.rest.module.controller.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.CreateModuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateModuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.module.ModuleApiEndpoints;
import com.ryuqq.adapter.in.rest.module.dto.request.CreateModuleApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.request.UpdateModuleApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.response.ModuleIdApiResponse;
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
 * ModuleCommandController 통합 테스트
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
@DisplayName("ModuleCommandController 통합 테스트")
class ModuleCommandControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("POST /api/v1/mcp/modules - Module 생성")
    class CreateModule {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() {
            // Given
            CreateModuleApiRequest request = CreateModuleApiRequestFixture.valid();
            HttpEntity<CreateModuleApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<ModuleIdApiResponse>> response =
                    restTemplate.exchange(
                            ModuleApiEndpoints.MODULES,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<ApiResponse<ModuleIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() {
            // Given
            CreateModuleApiRequest request = CreateModuleApiRequestFixture.invalidWithBlankName();
            HttpEntity<CreateModuleApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<ModuleIdApiResponse>> response =
                    restTemplate.exchange(
                            ModuleApiEndpoints.MODULES,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<ApiResponse<ModuleIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/modules/{moduleId} - Module 수정")
    class UpdateModule {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long moduleId = 1L;
            UpdateModuleApiRequest request = UpdateModuleApiRequestFixture.valid();
            HttpEntity<UpdateModuleApiRequest> httpEntity = new HttpEntity<>(request);
            String url = ModuleApiEndpoints.MODULES + "/" + moduleId;

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
            Long moduleId = 1L;
            UpdateModuleApiRequest request = UpdateModuleApiRequestFixture.invalidWithBlankName();
            HttpEntity<UpdateModuleApiRequest> httpEntity = new HttpEntity<>(request);
            String url = ModuleApiEndpoints.MODULES + "/" + moduleId;

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
