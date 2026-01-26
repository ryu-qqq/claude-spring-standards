package com.ryuqq.adapter.in.rest.classtemplate.controller.command;

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

import com.ryuqq.adapter.in.rest.classtemplate.ClassTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.classtemplate.mapper.ClassTemplateCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateClassTemplateApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateClassTemplateApiRequestFixture;
import com.ryuqq.application.classtemplate.dto.command.CreateClassTemplateCommand;
import com.ryuqq.application.classtemplate.dto.command.UpdateClassTemplateCommand;
import com.ryuqq.application.classtemplate.port.in.CreateClassTemplateUseCase;
import com.ryuqq.application.classtemplate.port.in.UpdateClassTemplateUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ClassTemplateCommandController REST Docs 테스트
 *
 * <p>REST Docs 문서화를 위한 통합 테스트입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ClassTemplateCommandController.class)
@DisplayName("ClassTemplateCommandController REST Docs")
class ClassTemplateCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateClassTemplateUseCase createClassTemplateUseCase;

    @MockitoBean private UpdateClassTemplateUseCase updateClassTemplateUseCase;

    @MockitoBean private ClassTemplateCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/class-templates - ClassTemplate 생성")
    class CreateClassTemplate {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateClassTemplateApiRequestFixture.valid();
            var command =
                    new CreateClassTemplateCommand(
                            1L,
                            1L, // classTypeId
                            "public class {ClassName} { ... }",
                            ".*Aggregate",
                            "Aggregate Root 클래스 템플릿",
                            java.util.List.of("@Entity"),
                            java.util.List.of("@Data"),
                            java.util.List.of(),
                            java.util.List.of(),
                            java.util.List.of());
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createClassTemplateUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ClassTemplateApiEndpoints.CLASS_TEMPLATES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.classTemplateId").value(createdId))
                    .andDo(
                            document(
                                    "class-template-create",
                                    requestFields(
                                            fieldWithPath("structureId")
                                                    .description("패키지 구조 ID")
                                                    .type(Long.class),
                                            fieldWithPath("classTypeId")
                                                    .description("클래스 타입 ID")
                                                    .type(Long.class),
                                            fieldWithPath("templateCode")
                                                    .description("템플릿 코드")
                                                    .type(String.class),
                                            fieldWithPath("namingPattern")
                                                    .description("네이밍 패턴")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .description("템플릿 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("requiredAnnotations")
                                                    .description("필수 어노테이션 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("forbiddenAnnotations")
                                                    .description("금지 어노테이션 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("requiredInterfaces")
                                                    .description("필수 인터페이스 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("forbiddenInheritance")
                                                    .description("금지 상속 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("requiredMethods")
                                                    .description("필수 메서드 목록")
                                                    .type(java.util.List.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.classTemplateId")
                                                    .description("생성된 ClassTemplate ID")
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
            var request = CreateClassTemplateApiRequestFixture.invalidWithBlankTemplateCode();

            // When & Then
            mockMvc.perform(
                            post(ClassTemplateApiEndpoints.CLASS_TEMPLATES)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(document("class-template-create-validation-error"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/class-templates/{classTemplateId} - ClassTemplate 수정")
    class UpdateClassTemplate {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long classTemplateId = 1L;
            var request = UpdateClassTemplateApiRequestFixture.valid();
            var command =
                    new UpdateClassTemplateCommand(
                            1L,
                            1L, // classTypeId
                            "public class {ClassName} { ... }",
                            ".*Aggregate",
                            "Aggregate Root 클래스 템플릿",
                            java.util.List.of("@Entity"),
                            java.util.List.of("@Data"),
                            java.util.List.of(),
                            java.util.List.of(),
                            java.util.List.of());

            given(mapper.toCommand(eq(classTemplateId), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ClassTemplateApiEndpoints.BY_ID, classTemplateId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "class-template-update",
                                    pathParameters(
                                            parameterWithName("classTemplateId")
                                                    .description("ClassTemplate ID")),
                                    requestFields(
                                            fieldWithPath("classTypeId")
                                                    .description("클래스 타입 ID")
                                                    .type(Long.class),
                                            fieldWithPath("templateCode")
                                                    .description("템플릿 코드")
                                                    .type(String.class),
                                            fieldWithPath("namingPattern")
                                                    .description("네이밍 패턴")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("description")
                                                    .description("템플릿 설명")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("requiredAnnotations")
                                                    .description("필수 어노테이션 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("forbiddenAnnotations")
                                                    .description("금지 어노테이션 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("requiredInterfaces")
                                                    .description("필수 인터페이스 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("forbiddenInheritance")
                                                    .description("금지 상속 목록")
                                                    .type(java.util.List.class)
                                                    .optional(),
                                            fieldWithPath("requiredMethods")
                                                    .description("필수 메서드 목록")
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
