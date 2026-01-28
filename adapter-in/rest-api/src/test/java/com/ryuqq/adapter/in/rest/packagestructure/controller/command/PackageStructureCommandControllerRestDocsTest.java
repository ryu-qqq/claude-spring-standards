package com.ryuqq.adapter.in.rest.packagestructure.controller.command;

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
import com.ryuqq.adapter.in.rest.fixture.request.CreatePackageStructureApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdatePackageStructureApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagestructure.PackageStructureApiEndpoints;
import com.ryuqq.adapter.in.rest.packagestructure.mapper.PackageStructureCommandApiMapper;
import com.ryuqq.application.packagestructure.dto.command.CreatePackageStructureCommand;
import com.ryuqq.application.packagestructure.dto.command.UpdatePackageStructureCommand;
import com.ryuqq.application.packagestructure.port.in.CreatePackageStructureUseCase;
import com.ryuqq.application.packagestructure.port.in.UpdatePackageStructureUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * PackageStructureCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(PackageStructureCommandController.class)
@DisplayName("PackageStructureCommandController REST Docs")
class PackageStructureCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreatePackageStructureUseCase createPackageStructureUseCase;

    @MockitoBean private UpdatePackageStructureUseCase updatePackageStructureUseCase;

    @MockitoBean private PackageStructureCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/package-structures - PackageStructure 생성")
    class CreatePackageStructure {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreatePackageStructureApiRequestFixture.valid();
            var command =
                    new CreatePackageStructureCommand(
                            1L, "{base}.domain.{bc}.aggregate", "Aggregate Root 패키지");
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createPackageStructureUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(PackageStructureApiEndpoints.PACKAGE_STRUCTURES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.packageStructureId").value(createdId))
                    .andDo(
                            document(
                                    "package-structure-create",
                                    requestFields(
                                            fieldWithPath("moduleId")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("pathPattern")
                                                    .description("경로 패턴")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.packageStructureId")
                                                    .description("생성된 PackageStructure ID")
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
            var request = CreatePackageStructureApiRequestFixture.invalidWithBlankPathPattern();

            // When & Then
            mockMvc.perform(
                            post(PackageStructureApiEndpoints.PACKAGE_STRUCTURES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("package-structure-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/package-structures/{packageStructureId} - PackageStructure 수정")
    class UpdatePackageStructure {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long packageStructureId = 1L;
            var request = UpdatePackageStructureApiRequestFixture.valid();
            var command =
                    new UpdatePackageStructureCommand(
                            1L, "{base}.domain.{bc}.aggregate", "Aggregate Root 패키지");

            given(mapper.toCommand(eq(packageStructureId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(PackageStructureApiEndpoints.BY_ID, packageStructureId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "package-structure-update",
                                    pathParameters(
                                            parameterWithName("packageStructureId")
                                                    .description("PackageStructure ID")),
                                    requestFields(
                                            fieldWithPath("pathPattern")
                                                    .description("경로 패턴")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
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
