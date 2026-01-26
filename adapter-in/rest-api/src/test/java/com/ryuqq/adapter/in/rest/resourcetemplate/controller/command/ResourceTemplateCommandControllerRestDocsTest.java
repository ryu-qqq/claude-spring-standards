package com.ryuqq.adapter.in.rest.resourcetemplate.controller.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateResourceTemplateApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateResourceTemplateApiRequestFixture;
import com.ryuqq.adapter.in.rest.resourcetemplate.ResourceTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.resourcetemplate.mapper.ResourceTemplateCommandApiMapper;
import com.ryuqq.application.resourcetemplate.dto.command.CreateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.dto.command.UpdateResourceTemplateCommand;
import com.ryuqq.application.resourcetemplate.port.in.CreateResourceTemplateUseCase;
import com.ryuqq.application.resourcetemplate.port.in.UpdateResourceTemplateUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ResourceTemplateCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ResourceTemplateCommandController.class)
@DisplayName("ResourceTemplateCommandController REST Docs")
class ResourceTemplateCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateResourceTemplateUseCase createResourceTemplateUseCase;

    @MockitoBean private UpdateResourceTemplateUseCase updateResourceTemplateUseCase;

    @MockitoBean private ResourceTemplateCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/resource-templates - ResourceTemplate 생성")
    class CreateResourceTemplate {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateResourceTemplateApiRequestFixture.valid();
            var command =
                    new CreateResourceTemplateCommand(
                            1L,
                            "DOMAIN",
                            "src/main/java/Order.java",
                            "JAVA",
                            "Order Aggregate",
                            "public class Order {}",
                            true);
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createResourceTemplateUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ResourceTemplateApiEndpoints.RESOURCE_TEMPLATES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.resourceTemplateId").value(createdId))
                    .andDo(
                            document(
                                    "resource-template-create",
                                    requestFields(
                                            fieldWithPath("moduleId")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("category")
                                                    .description("카테고리 (DOMAIN, APPLICATION 등)")
                                                    .type(String.class),
                                            fieldWithPath("filePath")
                                                    .description("파일 경로")
                                                    .type(String.class),
                                            fieldWithPath("fileType")
                                                    .description("파일 타입 (JAVA, KOTLIN 등)")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("templateContent")
                                                    .description("템플릿 콘텐츠")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("required")
                                                    .description("필수 여부")
                                                    .type(Boolean.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.resourceTemplateId")
                                                    .description("생성된 ResourceTemplate ID")
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
            var request = CreateResourceTemplateApiRequestFixture.invalidWithBlankFilePath();

            // When & Then
            mockMvc.perform(
                            post(ResourceTemplateApiEndpoints.RESOURCE_TEMPLATES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("resource-template-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/resource-templates/{resourceTemplateId} - ResourceTemplate 수정")
    class UpdateResourceTemplate {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long resourceTemplateId = 1L;
            var request = UpdateResourceTemplateApiRequestFixture.valid();
            var command =
                    new UpdateResourceTemplateCommand(
                            1L,
                            "DOMAIN",
                            "src/main/java/Order.java",
                            "JAVA",
                            "Order Aggregate",
                            "public class Order {}",
                            true);

            given(mapper.toCommand(eq(resourceTemplateId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ResourceTemplateApiEndpoints.BY_ID, resourceTemplateId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "resource-template-update",
                                    pathParameters(
                                            parameterWithName("resourceTemplateId")
                                                    .description("ResourceTemplate ID")),
                                    requestFields(
                                            fieldWithPath("category")
                                                    .description("카테고리")
                                                    .type(String.class),
                                            fieldWithPath("filePath")
                                                    .description("파일 경로")
                                                    .type(String.class),
                                            fieldWithPath("fileType")
                                                    .description("파일 타입")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
                                                    .type(String.class),
                                            fieldWithPath("templateContent")
                                                    .description("템플릿 콘텐츠")
                                                    .type(String.class),
                                            fieldWithPath("required")
                                                    .description("필수 여부")
                                                    .type(Boolean.class)),
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
