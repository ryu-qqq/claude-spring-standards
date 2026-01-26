package com.ryuqq.adapter.in.rest.layer.controller.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.ryuqq.adapter.in.rest.fixture.request.CreateLayerApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateLayerApiRequestFixture;
import com.ryuqq.adapter.in.rest.layer.LayerApiEndpoints;
import com.ryuqq.adapter.in.rest.layer.dto.request.CreateLayerApiRequest;
import com.ryuqq.adapter.in.rest.layer.dto.request.UpdateLayerApiRequest;
import com.ryuqq.adapter.in.rest.layer.mapper.LayerCommandApiMapper;
import com.ryuqq.application.layer.dto.command.CreateLayerCommand;
import com.ryuqq.application.layer.dto.command.UpdateLayerCommand;
import com.ryuqq.application.layer.port.in.CreateLayerUseCase;
import com.ryuqq.application.layer.port.in.UpdateLayerUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * LayerCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(LayerCommandController.class)
@DisplayName("LayerCommandController REST Docs")
class LayerCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateLayerUseCase createLayerUseCase;

    @MockitoBean private UpdateLayerUseCase updateLayerUseCase;

    @MockitoBean private LayerCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/templates/layers - Layer 생성")
    class CreateLayer {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateLayerApiRequestFixture.valid();
            var command =
                    new CreateLayerCommand(
                            1L, "DOMAIN", "Domain Layer", "Domain Layer Description", 1);
            Long createdId = 1L;

            given(mapper.toCommand(any(CreateLayerApiRequest.class))).willReturn(command);
            given(createLayerUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(LayerApiEndpoints.LAYERS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "layer-create",
                                    requestFields(
                                            fieldWithPath("architectureId")
                                                    .description("아키텍처 ID")
                                                    .type(Long.class),
                                            fieldWithPath("code")
                                                    .description("레이어 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("레이어 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("레이어 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("orderIndex")
                                                    .description("정렬 순서")
                                                    .type(Integer.class)),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.id")
                                                    .description("생성된 Layer ID")
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
            var request = CreateLayerApiRequestFixture.invalidWithNullArchitectureId();

            // When & Then
            mockMvc.perform(
                            post(LayerApiEndpoints.LAYERS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("layer-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/templates/layers/{layerId} - Layer 수정")
    class UpdateLayer {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long layerId = 1L;
            var request = UpdateLayerApiRequestFixture.valid();
            var command =
                    new UpdateLayerCommand(
                            1L,
                            "APPLICATION",
                            "Application Layer",
                            "Application Layer Description",
                            2);

            given(mapper.toCommand(anyLong(), any(UpdateLayerApiRequest.class)))
                    .willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(LayerApiEndpoints.LAYER_DETAIL, layerId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "layer-update",
                                    pathParameters(
                                            parameterWithName("layerId").description("Layer ID")),
                                    requestFields(
                                            fieldWithPath("code")
                                                    .description("레이어 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("레이어 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("레이어 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("orderIndex")
                                                    .description("정렬 순서")
                                                    .type(Integer.class)),
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
