package com.ryuqq.adapter.in.rest.classtypecategory.controller.command;

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

import com.ryuqq.adapter.in.rest.classtypecategory.ClassTypeCategoryApiEndpoints;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.CreateClassTypeCategoryApiRequest;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.UpdateClassTypeCategoryApiRequest;
import com.ryuqq.adapter.in.rest.classtypecategory.mapper.ClassTypeCategoryCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateClassTypeCategoryApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateClassTypeCategoryApiRequestFixture;
import com.ryuqq.application.classtypecategory.dto.command.CreateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.dto.command.UpdateClassTypeCategoryCommand;
import com.ryuqq.application.classtypecategory.port.in.CreateClassTypeCategoryUseCase;
import com.ryuqq.application.classtypecategory.port.in.UpdateClassTypeCategoryUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ClassTypeCategoryCommandController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ClassTypeCategoryCommandController.class)
@DisplayName("ClassTypeCategoryCommandController REST Docs")
class ClassTypeCategoryCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateClassTypeCategoryUseCase createClassTypeCategoryUseCase;

    @MockitoBean private UpdateClassTypeCategoryUseCase updateClassTypeCategoryUseCase;

    @MockitoBean private ClassTypeCategoryCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/templates/class-type-categories - ClassTypeCategory 생성")
    class CreateClassTypeCategory {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateClassTypeCategoryApiRequestFixture.valid();
            var command =
                    new CreateClassTypeCategoryCommand(
                            1L, "DOMAIN_TYPES", "도메인 타입", "도메인 레이어 클래스 타입", 1);
            Long createdId = 1L;

            given(mapper.toCommand(any(CreateClassTypeCategoryApiRequest.class)))
                    .willReturn(command);
            given(createClassTypeCategoryUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ClassTypeCategoryApiEndpoints.CLASS_TYPE_CATEGORIES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "class-type-category-create",
                                    requestFields(
                                            fieldWithPath("architectureId")
                                                    .description("Architecture ID")
                                                    .type(Long.class),
                                            fieldWithPath("code")
                                                    .description("카테고리 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("카테고리 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("카테고리 설명")
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
                                                    .description("생성된 ClassTypeCategory ID")
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
            var request = CreateClassTypeCategoryApiRequestFixture.invalidWithNullArchitectureId();

            // When & Then
            mockMvc.perform(
                            post(ClassTypeCategoryApiEndpoints.CLASS_TYPE_CATEGORIES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("class-type-category-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/templates/class-type-categories/{categoryId} - ClassTypeCategory 수정")
    class UpdateClassTypeCategory {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long categoryId = 1L;
            var request = UpdateClassTypeCategoryApiRequestFixture.valid();
            var command =
                    new UpdateClassTypeCategoryCommand(
                            1L, "DOMAIN_TYPES", "도메인 타입", "도메인 레이어 클래스 타입", 1);

            given(mapper.toCommand(anyLong(), any(UpdateClassTypeCategoryApiRequest.class)))
                    .willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(
                                            ClassTypeCategoryApiEndpoints
                                                    .CLASS_TYPE_CATEGORY_DETAIL,
                                            categoryId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "class-type-category-update",
                                    pathParameters(
                                            parameterWithName("categoryId")
                                                    .description("ClassTypeCategory ID")),
                                    requestFields(
                                            fieldWithPath("code")
                                                    .description("카테고리 코드")
                                                    .type(String.class),
                                            fieldWithPath("name")
                                                    .description("카테고리 이름")
                                                    .type(String.class),
                                            fieldWithPath("description")
                                                    .description("카테고리 설명")
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
