package com.ryuqq.adapter.in.rest.packagepurpose.controller.command;

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
import com.ryuqq.adapter.in.rest.fixture.request.CreatePackagePurposeApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdatePackagePurposeApiRequestFixture;
import com.ryuqq.adapter.in.rest.packagepurpose.PackagePurposeApiEndpoints;
import com.ryuqq.adapter.in.rest.packagepurpose.mapper.PackagePurposeCommandApiMapper;
import com.ryuqq.application.packagepurpose.dto.command.CreatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.dto.command.UpdatePackagePurposeCommand;
import com.ryuqq.application.packagepurpose.port.in.CreatePackagePurposeUseCase;
import com.ryuqq.application.packagepurpose.port.in.UpdatePackagePurposeUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * PackagePurposeCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(PackagePurposeCommandController.class)
@DisplayName("PackagePurposeCommandController REST Docs")
class PackagePurposeCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreatePackagePurposeUseCase createPackagePurposeUseCase;

    @MockitoBean private UpdatePackagePurposeUseCase updatePackagePurposeUseCase;

    @MockitoBean private PackagePurposeCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/ref/package-purposes - PackagePurpose 생성")
    class CreatePackagePurpose {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreatePackagePurposeApiRequestFixture.valid();
            var command =
                    new CreatePackagePurposeCommand(
                            1L, "AGGREGATE", "Aggregate Root", "DDD Aggregate Root 패키지");
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createPackagePurposeUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(PackagePurposeApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.packagePurposeId").value(createdId))
                    .andDo(
                            document(
                                    "package-purpose-create",
                                    requestFields(
                                            fieldWithPath("structureId")
                                                    .description("구조 ID")
                                                    .type(Long.class),
                                            fieldWithPath("code")
                                                    .description("목적 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("목적 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("설명")
                                                    .type(String.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.packagePurposeId")
                                                    .description("생성된 PackagePurpose ID")
                                                    .type(Long.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("PATCH /api/ref/package-purposes/{packagePurposeId} - PackagePurpose 수정")
    class UpdatePackagePurpose {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long packagePurposeId = 1L;
            var request = UpdatePackagePurposeApiRequestFixture.valid();
            var command =
                    new UpdatePackagePurposeCommand(
                            1L, "AGGREGATE", "Aggregate Root", "DDD Aggregate Root 패키지");

            given(mapper.toCommand(eq(packagePurposeId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            patch(PackagePurposeApiEndpoints.BY_ID, packagePurposeId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "package-purpose-update",
                                    pathParameters(
                                            parameterWithName("packagePurposeId")
                                                    .description("PackagePurpose ID")),
                                    requestFields(
                                            fieldWithPath("code")
                                                    .description("목적 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("목적 이름")
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
