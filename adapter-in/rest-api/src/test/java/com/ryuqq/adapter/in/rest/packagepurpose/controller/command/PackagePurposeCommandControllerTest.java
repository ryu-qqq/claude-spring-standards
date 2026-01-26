package com.ryuqq.adapter.in.rest.packagepurpose.controller.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.CreatePackagePurposeApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdatePackagePurposeApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagepurpose.PackagePurposeApiEndpoints;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.CreatePackagePurposeApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.UpdatePackagePurposeApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.response.PackagePurposeIdApiResponse;
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
 * PackagePurposeCommandController 통합 테스트
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
@DisplayName("PackagePurposeCommandController 통합 테스트")
class PackagePurposeCommandControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("POST /api/ref/package-purposes - PackagePurpose 생성")
    class CreatePackagePurpose {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() {
            // Given
            CreatePackagePurposeApiRequest request = CreatePackagePurposeApiRequestFixture.valid();
            HttpEntity<CreatePackagePurposeApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<PackagePurposeIdApiResponse>> response =
                    restTemplate.exchange(
                            PackagePurposeApiEndpoints.BASE,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<PackagePurposeIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() {
            // Given
            CreatePackagePurposeApiRequest request =
                    CreatePackagePurposeApiRequestFixture.invalidWithNullStructureId();
            HttpEntity<CreatePackagePurposeApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<PackagePurposeIdApiResponse>> response =
                    restTemplate.exchange(
                            PackagePurposeApiEndpoints.BASE,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<PackagePurposeIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("code 길이 초과 시 400 Bad Request 반환")
        void codeTooLong_ShouldReturn400() {
            // Given
            CreatePackagePurposeApiRequest request =
                    CreatePackagePurposeApiRequestFixture.invalidWithLongCode();
            HttpEntity<CreatePackagePurposeApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<PackagePurposeIdApiResponse>> response =
                    restTemplate.exchange(
                            PackagePurposeApiEndpoints.BASE,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<PackagePurposeIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("PATCH /api/ref/package-purposes/{id} - PackagePurpose 수정")
    class UpdatePackagePurpose {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long packagePurposeId = 1L;
            UpdatePackagePurposeApiRequest request = UpdatePackagePurposeApiRequestFixture.valid();
            HttpEntity<UpdatePackagePurposeApiRequest> httpEntity = new HttpEntity<>(request);
            String url = PackagePurposeApiEndpoints.BASE + "/" + packagePurposeId;

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

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() {
            // Given
            Long packagePurposeId = 1L;
            UpdatePackagePurposeApiRequest request =
                    UpdatePackagePurposeApiRequestFixture.invalidWithBlankCode();
            HttpEntity<UpdatePackagePurposeApiRequest> httpEntity = new HttpEntity<>(request);
            String url = PackagePurposeApiEndpoints.BASE + "/" + packagePurposeId;

            // When
            ResponseEntity<ApiResponse<Void>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PATCH,
                            httpEntity,
                            new ParameterizedTypeReference<ApiResponse<Void>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
