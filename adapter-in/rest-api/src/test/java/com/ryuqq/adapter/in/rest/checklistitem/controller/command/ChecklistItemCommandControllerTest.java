package com.ryuqq.adapter.in.rest.checklistitem.controller.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.checklistitem.ChecklistItemApiEndpoints;
import com.ryuqq.adapter.in.rest.checklistitem.dto.request.CreateChecklistItemApiRequest;
import com.ryuqq.adapter.in.rest.checklistitem.dto.request.UpdateChecklistItemApiRequest;
import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemIdApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.CreateChecklistItemApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateChecklistItemApiRequestFixture;
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
 * ChecklistItemCommandController 통합 테스트
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
@DisplayName("ChecklistItemCommandController 통합 테스트")
class ChecklistItemCommandControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("POST /api/v1/mcp/checklist-items - ChecklistItem 생성")
    class CreateChecklistItem {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() {
            // Given
            CreateChecklistItemApiRequest request = CreateChecklistItemApiRequestFixture.valid();
            HttpEntity<CreateChecklistItemApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<ChecklistItemIdApiResponse>> response =
                    restTemplate.exchange(
                            ChecklistItemApiEndpoints.CHECKLIST_ITEMS,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<ChecklistItemIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() {
            // Given
            CreateChecklistItemApiRequest request =
                    CreateChecklistItemApiRequestFixture.invalidWithBlankCheckDescription();
            HttpEntity<CreateChecklistItemApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<ChecklistItemIdApiResponse>> response =
                    restTemplate.exchange(
                            ChecklistItemApiEndpoints.CHECKLIST_ITEMS,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<ChecklistItemIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/checklist-items/{id} - ChecklistItem 수정")
    class UpdateChecklistItem {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long id = 1L;
            UpdateChecklistItemApiRequest request = UpdateChecklistItemApiRequestFixture.valid();
            HttpEntity<UpdateChecklistItemApiRequest> httpEntity = new HttpEntity<>(request);
            String url = ChecklistItemApiEndpoints.CHECKLIST_ITEMS + "/" + id;

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
            Long id = 1L;
            UpdateChecklistItemApiRequest request =
                    UpdateChecklistItemApiRequestFixture.invalidWithBlankCheckDescription();
            HttpEntity<UpdateChecklistItemApiRequest> httpEntity = new HttpEntity<>(request);
            String url = ChecklistItemApiEndpoints.CHECKLIST_ITEMS + "/" + id;

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
