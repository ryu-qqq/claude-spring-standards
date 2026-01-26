package com.ryuqq.adapter.in.rest.classtype.controller.command;

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

import com.ryuqq.adapter.in.rest.classtype.ClassTypeApiEndpoints;
import com.ryuqq.adapter.in.rest.classtype.dto.request.CreateClassTypeApiRequest;
import com.ryuqq.adapter.in.rest.classtype.dto.request.UpdateClassTypeApiRequest;
import com.ryuqq.adapter.in.rest.classtype.mapper.ClassTypeCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateClassTypeApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateClassTypeApiRequestFixture;
import com.ryuqq.application.classtype.dto.command.CreateClassTypeCommand;
import com.ryuqq.application.classtype.dto.command.UpdateClassTypeCommand;
import com.ryuqq.application.classtype.port.in.CreateClassTypeUseCase;
import com.ryuqq.application.classtype.port.in.UpdateClassTypeUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ClassTypeCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ClassTypeCommandController.class)
@DisplayName("ClassTypeCommandController REST Docs")
class ClassTypeCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateClassTypeUseCase createClassTypeUseCase;

    @MockitoBean private UpdateClassTypeUseCase updateClassTypeUseCase;

    @MockitoBean private ClassTypeCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/templates/class-types - ClassType 생성")
    class CreateClassType {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateClassTypeApiRequestFixture.valid();
            var command =
                    new CreateClassTypeCommand(
                            1L, "AGGREGATE", "Aggregate", "도메인 Aggregate Root 클래스", 1);
            Long createdId = 1L;

            given(mapper.toCommand(any(CreateClassTypeApiRequest.class))).willReturn(command);
            given(createClassTypeUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ClassTypeApiEndpoints.CLASS_TYPES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "class-type-create",
                                    requestFields(
                                            fieldWithPath("categoryId")
                                                    .description("ClassTypeCategory ID")
                                                    .type(Long.class),
                                            fieldWithPath("code")
                                                    .description("클래스 타입 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("클래스 타입 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("클래스 타입 설명")
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
                                                    .description("생성된 ClassType ID")
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
            var request = CreateClassTypeApiRequestFixture.invalidWithNullCategoryId();

            // When & Then
            mockMvc.perform(
                            post(ClassTypeApiEndpoints.CLASS_TYPES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("class-type-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/templates/class-types/{classTypeId} - ClassType 수정")
    class UpdateClassType {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long classTypeId = 1L;
            var request = UpdateClassTypeApiRequestFixture.valid();
            var command =
                    new UpdateClassTypeCommand(
                            1L, "AGGREGATE", "Aggregate", "도메인 Aggregate Root 클래스", 1);

            given(mapper.toCommand(anyLong(), any(UpdateClassTypeApiRequest.class)))
                    .willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ClassTypeApiEndpoints.CLASS_TYPE_DETAIL, classTypeId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "class-type-update",
                                    pathParameters(
                                            parameterWithName("classTypeId")
                                                    .description("ClassType ID")),
                                    requestFields(
                                            fieldWithPath("code")
                                                    .description("클래스 타입 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("클래스 타입 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("클래스 타입 설명")
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
