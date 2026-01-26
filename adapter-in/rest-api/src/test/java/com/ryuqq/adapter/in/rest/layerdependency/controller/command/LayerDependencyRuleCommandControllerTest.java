package com.ryuqq.adapter.in.rest.layerdependency.controller.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.CreateLayerDependencyRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateLayerDependencyRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.CreateLayerDependencyRuleApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.UpdateLayerDependencyRuleApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.response.LayerDependencyRuleIdApiResponse;
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
 * LayerDependencyRuleCommandController 통합 테스트
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
@DisplayName("LayerDependencyRuleCommandController 통합 테스트")
class LayerDependencyRuleCommandControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName(
            "POST /api/v1/templates/architectures/{architectureId}/layer-dependency-rules -"
                    + " LayerDependencyRule 생성")
    class CreateLayerDependencyRule {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() {
            // Given
            Long architectureId = 1L;
            CreateLayerDependencyRuleApiRequest request =
                    CreateLayerDependencyRuleApiRequestFixture.valid();
            HttpEntity<CreateLayerDependencyRuleApiRequest> httpEntity = new HttpEntity<>(request);
            String url =
                    "/api/v1/templates/architectures/" + architectureId + "/layer-dependency-rules";

            // When
            ResponseEntity<ApiResponse<LayerDependencyRuleIdApiResponse>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<LayerDependencyRuleIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }
    }

    @Nested
    @DisplayName(
            "PATCH /api/v1/templates/architectures/{architectureId}/layer-dependency-rules/{ldrId}"
                    + " - LayerDependencyRule 수정")
    class UpdateLayerDependencyRule {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long architectureId = 1L;
            Long ldrId = 1L;
            UpdateLayerDependencyRuleApiRequest request =
                    UpdateLayerDependencyRuleApiRequestFixture.valid();
            HttpEntity<UpdateLayerDependencyRuleApiRequest> httpEntity = new HttpEntity<>(request);
            String url =
                    "/api/v1/templates/architectures/"
                            + architectureId
                            + "/layer-dependency-rules/"
                            + ldrId;

            // When
            ResponseEntity<ApiResponse<Void>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PATCH,
                            httpEntity,
                            new ParameterizedTypeReference<ApiResponse<Void>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }
    }
}
