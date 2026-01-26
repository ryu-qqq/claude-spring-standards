package com.ryuqq.adapter.in.rest.classtemplate.controller.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.adapter.in.rest.TestRestApiApplication;
import com.ryuqq.adapter.in.rest.classtemplate.ClassTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.classtemplate.dto.request.CreateClassTemplateApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.request.UpdateClassTemplateApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.response.ClassTemplateIdApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.fixture.request.CreateClassTemplateApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateClassTemplateApiRequestFixture;
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
 * ClassTemplateCommandController 통합 테스트
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
@DisplayName("ClassTemplateCommandController 통합 테스트")
class ClassTemplateCommandControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("POST /api/v1/mcp/class-templates - ClassTemplate 생성")
    class CreateClassTemplate {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() {
            // Given
            CreateClassTemplateApiRequest request = CreateClassTemplateApiRequestFixture.valid();
            HttpEntity<CreateClassTemplateApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<ClassTemplateIdApiResponse>> response =
                    restTemplate.exchange(
                            ClassTemplateApiEndpoints.CLASS_TEMPLATES,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<ClassTemplateIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().data()).isNotNull();
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() {
            // Given
            CreateClassTemplateApiRequest request =
                    CreateClassTemplateApiRequestFixture.invalidWithNullClassTypeId();
            HttpEntity<CreateClassTemplateApiRequest> httpEntity = new HttpEntity<>(request);

            // When
            ResponseEntity<ApiResponse<ClassTemplateIdApiResponse>> response =
                    restTemplate.exchange(
                            ClassTemplateApiEndpoints.CLASS_TEMPLATES,
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<
                                    ApiResponse<ClassTemplateIdApiResponse>>() {});

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/class-templates/{classTemplateId} - ClassTemplate 수정")
    class UpdateClassTemplate {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() {
            // Given
            Long classTemplateId = 1L;
            UpdateClassTemplateApiRequest request = UpdateClassTemplateApiRequestFixture.valid();
            HttpEntity<UpdateClassTemplateApiRequest> httpEntity = new HttpEntity<>(request);
            String url = ClassTemplateApiEndpoints.CLASS_TEMPLATES + "/" + classTemplateId;

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
            Long classTemplateId = 1L;
            UpdateClassTemplateApiRequest request =
                    UpdateClassTemplateApiRequestFixture.invalidWithNullClassTypeId();
            HttpEntity<UpdateClassTemplateApiRequest> httpEntity = new HttpEntity<>(request);
            String url = ClassTemplateApiEndpoints.CLASS_TEMPLATES + "/" + classTemplateId;

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
