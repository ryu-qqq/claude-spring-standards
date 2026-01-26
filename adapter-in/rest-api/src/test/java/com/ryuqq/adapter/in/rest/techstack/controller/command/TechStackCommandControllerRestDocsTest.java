package com.ryuqq.adapter.in.rest.techstack.controller.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateTechStackApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateTechStackApiRequestFixture;
import com.ryuqq.adapter.in.rest.techstack.TechStackApiEndpoints;
import com.ryuqq.adapter.in.rest.techstack.mapper.TechStackCommandApiMapper;
import com.ryuqq.application.techstack.dto.command.CreateTechStackCommand;
import com.ryuqq.application.techstack.dto.command.UpdateTechStackCommand;
import com.ryuqq.application.techstack.port.in.CreateTechStackUseCase;
import com.ryuqq.application.techstack.port.in.UpdateTechStackUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * TechStackCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(TechStackCommandController.class)
@DisplayName("TechStackCommandController REST Docs")
class TechStackCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateTechStackUseCase createTechStackUseCase;

    @MockitoBean private UpdateTechStackUseCase updateTechStackUseCase;

    @MockitoBean private TechStackCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/tech-stacks - TechStack 생성")
    class CreateTechStack {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateTechStackApiRequestFixture.valid();
            var command =
                    new CreateTechStackCommand(
                            "Spring Boot 3.5 with Java 21",
                            "JAVA",
                            "21",
                            java.util.List.of("records", "sealed-classes"),
                            "SPRING_BOOT",
                            "3.5.0",
                            java.util.List.of("spring-web", "spring-data-jpa"),
                            "JVM",
                            "JVM",
                            "GRADLE",
                            "build.gradle",
                            java.util.List.of());
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createTechStackUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(TechStackApiEndpoints.TECH_STACKS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "tech-stack-create",
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("TechStack 이름")
                                                    .type(String.class),
                                            fieldWithPath("languageType")
                                                    .description("언어 타입")
                                                    .type(String.class),
                                            fieldWithPath("languageVersion")
                                                    .description("언어 버전")
                                                    .type(String.class),
                                            fieldWithPath("languageFeatures")
                                                    .description("언어 기능 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("frameworkType")
                                                    .description("프레임워크 타입")
                                                    .type(String.class),
                                            fieldWithPath("frameworkVersion")
                                                    .description("프레임워크 버전")
                                                    .type(String.class),
                                            fieldWithPath("frameworkModules")
                                                    .description("프레임워크 모듈 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("platformType")
                                                    .description("플랫폼 타입")
                                                    .type(String.class),
                                            fieldWithPath("runtimeEnvironment")
                                                    .description("런타임 환경")
                                                    .type(String.class),
                                            fieldWithPath("buildToolType")
                                                    .description("빌드 도구 타입")
                                                    .type(String.class),
                                            fieldWithPath("buildConfigFile")
                                                    .description("빌드 설정 파일")
                                                    .type(String.class),
                                            fieldWithPath("referenceLinks")
                                                    .description("참조 링크 목록")
                                                    .type(java.util.List.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.id")
                                                    .description("생성된 TechStack ID")
                                                    .type(Long.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() throws Exception {
            // Given
            var request = CreateTechStackApiRequestFixture.invalidWithBlankName();

            // When & Then
            mockMvc.perform(
                            post(TechStackApiEndpoints.TECH_STACKS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("tech-stack-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/tech-stacks/{techStackId} - TechStack 수정")
    class UpdateTechStack {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long techStackId = 1L;
            var request = UpdateTechStackApiRequestFixture.valid();
            var command =
                    new UpdateTechStackCommand(
                            1L,
                            "Spring Boot 3.5 with Java 21",
                            "ACTIVE",
                            "JAVA",
                            "21",
                            java.util.List.of("records", "sealed-classes"),
                            "SPRING_BOOT",
                            "3.5.0",
                            java.util.List.of("spring-web", "spring-data-jpa"),
                            "JVM",
                            "JVM",
                            "GRADLE",
                            "build.gradle",
                            java.util.List.of());

            given(mapper.toCommand(eq(techStackId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(
                                            TechStackApiEndpoints.TECH_STACKS
                                                    + TechStackApiEndpoints.ID,
                                            techStackId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "tech-stack-update",
                                    pathParameters(
                                            parameterWithName("techStackId")
                                                    .description("TechStack ID")),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("TechStack 이름")
                                                    .type(String.class),
                                            fieldWithPath("status")
                                                    .description("상태")
                                                    .type(String.class),
                                            fieldWithPath("languageType")
                                                    .description("언어 타입")
                                                    .type(String.class),
                                            fieldWithPath("languageVersion")
                                                    .description("언어 버전")
                                                    .type(String.class),
                                            fieldWithPath("languageFeatures")
                                                    .description("언어 기능 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("frameworkType")
                                                    .description("프레임워크 타입")
                                                    .type(String.class),
                                            fieldWithPath("frameworkVersion")
                                                    .description("프레임워크 버전")
                                                    .type(String.class),
                                            fieldWithPath("frameworkModules")
                                                    .description("프레임워크 모듈 목록")
                                                    .type(java.util.List.class),
                                            fieldWithPath("platformType")
                                                    .description("플랫폼 타입")
                                                    .type(String.class),
                                            fieldWithPath("runtimeEnvironment")
                                                    .description("런타임 환경")
                                                    .type(String.class),
                                            fieldWithPath("buildToolType")
                                                    .description("빌드 도구 타입")
                                                    .type(String.class),
                                            fieldWithPath("buildConfigFile")
                                                    .description("빌드 설정 파일")
                                                    .type(String.class),
                                            fieldWithPath("referenceLinks")
                                                    .description("참조 링크 목록")
                                                    .type(java.util.List.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class)
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }
}
