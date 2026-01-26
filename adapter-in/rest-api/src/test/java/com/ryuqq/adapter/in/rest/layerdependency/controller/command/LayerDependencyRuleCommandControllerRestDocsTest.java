package com.ryuqq.adapter.in.rest.layerdependency.controller.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateLayerDependencyRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateLayerDependencyRuleApiRequestFixture;
import com.ryuqq.adapter.in.rest.layerdependency.LayerDependencyRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.layerdependency.mapper.LayerDependencyRuleCommandApiMapper;
import com.ryuqq.application.layerdependency.dto.command.CreateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.dto.command.UpdateLayerDependencyRuleCommand;
import com.ryuqq.application.layerdependency.port.in.CreateLayerDependencyRuleUseCase;
import com.ryuqq.application.layerdependency.port.in.UpdateLayerDependencyRuleUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * LayerDependencyRuleCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(LayerDependencyRuleCommandController.class)
@DisplayName("LayerDependencyRuleCommandController REST Docs")
class LayerDependencyRuleCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateLayerDependencyRuleUseCase createLayerDependencyRuleUseCase;

    @MockitoBean private UpdateLayerDependencyRuleUseCase updateLayerDependencyRuleUseCase;

    @MockitoBean private LayerDependencyRuleCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName(
            "POST /api/v1/mcp/architectures/{architectureId}/layer-dependency-rules -"
                    + " LayerDependencyRule 생성")
    class CreateLayerDependencyRule {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            Long architectureId = 1L;
            var request = CreateLayerDependencyRuleApiRequestFixture.valid();
            var command =
                    new CreateLayerDependencyRuleCommand(
                            1L, "DOMAIN", "APPLICATION", "ALLOWED", null);
            Long createdId = 1L;

            given(mapper.toCommand(eq(architectureId), any())).willReturn(command);
            given(createLayerDependencyRuleUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(LayerDependencyRuleApiEndpoints.BASE, architectureId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.layerDependencyRuleId").value(createdId))
                    .andDo(
                            document(
                                    "layer-dependency-rule-create",
                                    pathParameters(
                                            parameterWithName("architectureId")
                                                    .description("Architecture ID")),
                                    requestFields(
                                            fieldWithPath("fromLayer")
                                                    .description("소스 레이어")
                                                    .type(String.class),
                                            fieldWithPath("toLayer")
                                                    .description("타겟 레이어")
                                                    .type(String.class),
                                            fieldWithPath("dependencyType")
                                                    .description(
                                                            "의존성 타입 (ALLOWED, FORBIDDEN,"
                                                                    + " CONDITIONAL)")
                                                    .type(String.class),
                                            fieldWithPath("conditionDescription")
                                                    .description("조건 설명 (CONDITIONAL인 경우)")
                                                    .type(String.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.layerDependencyRuleId")
                                                    .description("생성된 LayerDependencyRule ID")
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
            Long architectureId = 1L;
            var request = CreateLayerDependencyRuleApiRequestFixture.invalidWithBlankFromLayer();

            // When & Then
            mockMvc.perform(
                            post(LayerDependencyRuleApiEndpoints.BASE, architectureId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("layer-dependency-rule-create-validation-error"));
        }
    }

    @Nested
    @DisplayName(
            "PATCH /api/v1/mcp/architectures/{architectureId}/layer-dependency-rules/{ldrId} -"
                    + " LayerDependencyRule 수정")
    class UpdateLayerDependencyRule {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long architectureId = 1L;
            Long ldrId = 1L;
            var request = UpdateLayerDependencyRuleApiRequestFixture.valid();
            var command =
                    new UpdateLayerDependencyRuleCommand(
                            1L, 1L, "DOMAIN", "APPLICATION", "FORBIDDEN", "조건 설명");

            given(mapper.toCommand(eq(architectureId), eq(ldrId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            patch(LayerDependencyRuleApiEndpoints.BY_ID, architectureId, ldrId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "layer-dependency-rule-update",
                                    pathParameters(
                                            parameterWithName("architectureId")
                                                    .description("Architecture ID"),
                                            parameterWithName("ldrId")
                                                    .description("LayerDependencyRule ID")),
                                    requestFields(
                                            fieldWithPath("fromLayer")
                                                    .description("소스 레이어")
                                                    .type(String.class),
                                            fieldWithPath("toLayer")
                                                    .description("타겟 레이어")
                                                    .type(String.class),
                                            fieldWithPath("dependencyType")
                                                    .description("의존성 타입")
                                                    .type(String.class),
                                            fieldWithPath("conditionDescription")
                                                    .description("조건 설명")
                                                    .type(String.class)
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
