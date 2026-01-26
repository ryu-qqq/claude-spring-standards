package com.ryuqq.adapter.in.rest.archunittest.controller.command;

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

import com.ryuqq.adapter.in.rest.archunittest.ArchUnitTestApiEndpoints;
import com.ryuqq.adapter.in.rest.archunittest.mapper.ArchUnitTestCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateArchUnitTestApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateArchUnitTestApiRequestFixture;
import com.ryuqq.application.archunittest.dto.command.CreateArchUnitTestCommand;
import com.ryuqq.application.archunittest.dto.command.UpdateArchUnitTestCommand;
import com.ryuqq.application.archunittest.port.in.CreateArchUnitTestUseCase;
import com.ryuqq.application.archunittest.port.in.UpdateArchUnitTestUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ArchUnitTestCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ArchUnitTestCommandController.class)
@DisplayName("ArchUnitTestCommandController REST Docs")
class ArchUnitTestCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateArchUnitTestUseCase createArchUnitTestUseCase;

    @MockitoBean private UpdateArchUnitTestUseCase updateArchUnitTestUseCase;

    @MockitoBean private ArchUnitTestCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/arch-unit-tests - ArchUnitTest 생성")
    class CreateArchUnitTest {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateArchUnitTestApiRequestFixture.valid();
            var command =
                    new CreateArchUnitTestCommand(
                            1L,
                            "ARCH-001",
                            "Lombok 사용 금지 테스트",
                            "Description",
                            "DomainLayerArchUnitTest",
                            "shouldNotUseLombok",
                            "testCode",
                            "BLOCKER");
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createArchUnitTestUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ArchUnitTestApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.archUnitTestId").value(createdId))
                    .andDo(
                            document(
                                    "arch-unit-test-create",
                                    requestFields(
                                            fieldWithPath("structureId")
                                                    .description("패키지 구조 ID")
                                                    .type(Long.class),
                                            fieldWithPath("code")
                                                    .description("테스트 코드 식별자")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("테스트 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("테스트 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("testClassName")
                                                    .description("테스트 클래스 이름")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("testMethodName")
                                                    .description("테스트 메서드 이름")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("testCode")
                                                    .description("테스트 코드 내용")
                                                    .type(String.class),
                                            fieldWithPath("severity")
                                                    .description("심각도")
                                                    .type(String.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.archUnitTestId")
                                                    .description("생성된 ArchUnitTest ID")
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
            var request = CreateArchUnitTestApiRequestFixture.invalidWithBlankCode();

            // When & Then
            mockMvc.perform(
                            post(ArchUnitTestApiEndpoints.BASE)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("arch-unit-test-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/arch-unit-tests/{archUnitTestId} - ArchUnitTest 수정")
    class UpdateArchUnitTest {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long archUnitTestId = 1L;
            var request = UpdateArchUnitTestApiRequestFixture.valid();
            var command =
                    new UpdateArchUnitTestCommand(
                            1L,
                            "ARCH-001",
                            "Lombok 사용 금지 테스트",
                            "Description",
                            "DomainLayerArchUnitTest",
                            "shouldNotUseLombok",
                            "testCode",
                            "BLOCKER");

            given(mapper.toCommand(eq(archUnitTestId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ArchUnitTestApiEndpoints.BY_ID, archUnitTestId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "arch-unit-test-update",
                                    pathParameters(
                                            parameterWithName("archUnitTestId")
                                                    .description("ArchUnitTest ID")),
                                    requestFields(
                                            fieldWithPath("code")
                                                    .description("테스트 코드 식별자")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("테스트 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("테스트 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("testClassName")
                                                    .description("테스트 클래스 이름")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("testMethodName")
                                                    .description("테스트 메서드 이름")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("testCode")
                                                    .description("테스트 코드 내용")
                                                    .type(String.class),
                                            fieldWithPath("severity")
                                                    .description("심각도")
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
