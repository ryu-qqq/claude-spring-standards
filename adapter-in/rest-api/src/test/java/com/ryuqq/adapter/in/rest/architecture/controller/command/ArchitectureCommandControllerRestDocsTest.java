package com.ryuqq.adapter.in.rest.architecture.controller.command;

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

import com.ryuqq.adapter.in.rest.architecture.ArchitectureApiEndpoints;
import com.ryuqq.adapter.in.rest.architecture.mapper.ArchitectureCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateArchitectureApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateArchitectureApiRequestFixture;
import com.ryuqq.application.architecture.dto.command.CreateArchitectureCommand;
import com.ryuqq.application.architecture.dto.command.UpdateArchitectureCommand;
import com.ryuqq.application.architecture.port.in.CreateArchitectureUseCase;
import com.ryuqq.application.architecture.port.in.UpdateArchitectureUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ArchitectureCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ArchitectureCommandController.class)
@DisplayName("ArchitectureCommandController REST Docs")
class ArchitectureCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateArchitectureUseCase createArchitectureUseCase;

    @MockitoBean private UpdateArchitectureUseCase updateArchitectureUseCase;

    @MockitoBean private ArchitectureCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/architectures - Architecture 생성")
    class CreateArchitecture {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateArchitectureApiRequestFixture.valid();
            var command =
                    new CreateArchitectureCommand(
                            1L,
                            "Hexagonal Architecture",
                            "HEXAGONAL",
                            "Ports and Adapters 패턴",
                            java.util.List.of("의존성 역전", "계층 분리"),
                            java.util.List.of());
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createArchitectureUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ArchitectureApiEndpoints.ARCHITECTURES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "architecture-create",
                                    requestFields(
                                            fieldWithPath("techStackId")
                                                    .description("기술 스택 ID")
                                                    .type(Long.class),
                                            fieldWithPath("name")
                                                    .description("아키텍처 이름")
                                                    .type(String.class),
                                            fieldWithPath("patternType")
                                                    .description("패턴 타입")
                                                    .type(String.class),
                                            fieldWithPath("patternDescription")
                                                    .description("패턴 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("patternPrinciples")
                                                    .description("패턴 원칙 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("referenceLinks")
                                                    .description("참조 링크 목록")
                                                    .type(java.util.List.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.id")
                                                    .description("생성된 Architecture ID")
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
            var request = CreateArchitectureApiRequestFixture.invalidWithBlankName();

            // When & Then
            mockMvc.perform(
                            post(ArchitectureApiEndpoints.ARCHITECTURES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("architecture-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/architectures/{architectureId} - Architecture 수정")
    class UpdateArchitecture {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long architectureId = 1L;
            var request = UpdateArchitectureApiRequestFixture.valid();
            var command =
                    new UpdateArchitectureCommand(
                            1L,
                            "Hexagonal Architecture",
                            "HEXAGONAL",
                            "Ports and Adapters 패턴",
                            java.util.List.of("의존성 역전", "계층 분리"),
                            java.util.List.of());

            given(mapper.toCommand(eq(architectureId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ArchitectureApiEndpoints.ARCHITECTURE_DETAIL, architectureId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "architecture-update",
                                    pathParameters(
                                            parameterWithName("architectureId")
                                                    .description("Architecture ID")),
                                    requestFields(
                                            fieldWithPath("name")
                                                    .description("아키텍처 이름")
                                                    .type(String.class),
                                            fieldWithPath("patternType")
                                                    .description("패턴 타입")
                                                    .type(String.class),
                                            fieldWithPath("patternDescription")
                                                    .description("패턴 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("patternPrinciples")
                                                    .description("패턴 원칙 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
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
