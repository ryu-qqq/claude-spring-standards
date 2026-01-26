package com.ryuqq.adapter.in.rest.checklistitem.controller.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.checklistitem.ChecklistItemApiEndpoints;
import com.ryuqq.adapter.in.rest.checklistitem.mapper.ChecklistItemCommandApiMapper;
import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.fixture.request.CreateChecklistItemApiRequestFixture;
import com.ryuqq.adapter.in.rest.fixture.request.UpdateChecklistItemApiRequestFixture;
import com.ryuqq.application.checklistitem.dto.command.CreateChecklistItemCommand;
import com.ryuqq.application.checklistitem.dto.command.UpdateChecklistItemCommand;
import com.ryuqq.application.checklistitem.port.in.CreateChecklistItemUseCase;
import com.ryuqq.application.checklistitem.port.in.UpdateChecklistItemUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * ChecklistItemCommandController REST Docs 테스트
 *
 * <p>REST Docs 문서화를 위한 통합 테스트입니다.
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>API 요청/응답 필드 문서화
 *   <li>Path Parameter 문서화
 *   <li>정상/예외 응답 시나리오 문서화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(ChecklistItemCommandController.class)
@DisplayName("ChecklistItemCommandController REST Docs")
class ChecklistItemCommandControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private CreateChecklistItemUseCase createChecklistItemUseCase;

    @MockitoBean private UpdateChecklistItemUseCase updateChecklistItemUseCase;

    @MockitoBean private ChecklistItemCommandApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("POST /api/v1/mcp/checklist-items - ChecklistItem 생성")
    class CreateChecklistItem {

        @Test
        @DisplayName("정상 요청 시 201 Created 반환")
        void validRequest_ShouldReturn201() throws Exception {
            // Given
            var request = CreateChecklistItemApiRequestFixture.valid();
            var command =
                    new CreateChecklistItemCommand(
                            1L,
                            1,
                            "Lombok 어노테이션 사용 여부 확인",
                            "AUTOMATED",
                            "ARCHUNIT",
                            "AGG-001-CHECK-1",
                            false);
            Long createdId = 1L;

            given(mapper.toCommand(any())).willReturn(command);
            given(createChecklistItemUseCase.execute(any())).willReturn(createdId);

            // When & Then
            mockMvc.perform(
                            post(ChecklistItemApiEndpoints.CHECKLIST_ITEMS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(createdId))
                    .andDo(
                            document(
                                    "checklist-item-create",
                                    requestFields(
                                            fieldWithPath("ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("sequenceOrder")
                                                    .description("순서 (1 이상)")
                                                    .type(Integer.class),
                                            fieldWithPath("checkDescription")
                                                    .description("체크 설명 (최대 500자)")
                                                    .type(String.class),
                                            fieldWithPath("checkType")
                                                    .description(
                                                            "체크 타입 (AUTOMATED, MANUAL, SEMI_AUTO)")
                                                    .type(String.class),
                                            fieldWithPath("automationTool")
                                                    .description("자동화 도구 (최대 50자)")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("automationRuleId")
                                                    .description("자동화 규칙 ID (최대 100자)")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("isCritical")
                                                    .description("필수 여부")
                                                    .type(Boolean.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.id")
                                                    .description("생성된 ChecklistItem ID")
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
            var request = CreateChecklistItemApiRequestFixture.invalidWithBlankCheckDescription();

            // When & Then
            mockMvc.perform(
                            post(ChecklistItemApiEndpoints.CHECKLIST_ITEMS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "checklist-item-create-validation-error",
                                    requestFields(
                                            fieldWithPath("ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("sequenceOrder")
                                                    .description("순서")
                                                    .type(Integer.class),
                                            fieldWithPath("checkDescription")
                                                    .description("체크 설명 (필수)")
                                                    .type(String.class),
                                            fieldWithPath("checkType")
                                                    .description("체크 타입")
                                                    .type(String.class),
                                            fieldWithPath("automationTool")
                                                    .description("자동화 도구")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("automationRuleId")
                                                    .description("자동화 규칙 ID")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("isCritical")
                                                    .description("필수 여부")
                                                    .type(Boolean.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("type")
                                                    .description("에러 타입 URI")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("에러 제목")
                                                    .type(String.class),
                                            fieldWithPath("status")
                                                    .description("HTTP 상태 코드")
                                                    .type(Integer.class),
                                            fieldWithPath("detail")
                                                    .description("에러 상세 설명")
                                                    .type(String.class),
                                            fieldWithPath("instance")
                                                    .description("에러 발생 URI")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("에러 코드")
                                                    .type(String.class),
                                            subsectionWithPath("errors").description("필드별 에러 메시지"),
                                            fieldWithPath("timestamp")
                                                    .description("에러 발생 시간")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("sequenceOrder가 1 미만일 때 400 Bad Request 반환")
        void sequenceOrderTooSmall_ShouldReturn400() throws Exception {
            // Given
            var request = CreateChecklistItemApiRequestFixture.invalidWithSequenceOrderTooSmall();

            // When & Then
            mockMvc.perform(
                            post(ChecklistItemApiEndpoints.CHECKLIST_ITEMS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "checklist-item-create-sequence-order-validation-error",
                                    requestFields(
                                            fieldWithPath("ruleId")
                                                    .description("코딩 규칙 ID")
                                                    .type(Long.class),
                                            fieldWithPath("sequenceOrder")
                                                    .description("순서 (1 이상)")
                                                    .type(Integer.class),
                                            fieldWithPath("checkDescription")
                                                    .description("체크 설명")
                                                    .type(String.class),
                                            fieldWithPath("checkType")
                                                    .description("체크 타입")
                                                    .type(String.class),
                                            fieldWithPath("automationTool")
                                                    .description("자동화 도구")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("automationRuleId")
                                                    .description("자동화 규칙 ID")
                                                    .type(String.class)
                                                    .optional(),
                                            fieldWithPath("isCritical")
                                                    .description("필수 여부")
                                                    .type(Boolean.class)
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("type")
                                                    .description("에러 타입 URI")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("에러 제목")
                                                    .type(String.class),
                                            fieldWithPath("status")
                                                    .description("HTTP 상태 코드")
                                                    .type(Integer.class),
                                            fieldWithPath("detail")
                                                    .description("에러 상세 설명")
                                                    .type(String.class),
                                            fieldWithPath("instance")
                                                    .description("에러 발생 URI")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("에러 코드")
                                                    .type(String.class),
                                            subsectionWithPath("errors").description("필드별 에러 메시지"),
                                            fieldWithPath("timestamp")
                                                    .description("에러 발생 시간")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/mcp/checklist-items/{id} - ChecklistItem 수정")
    class UpdateChecklistItem {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long id = 1L;
            var request = UpdateChecklistItemApiRequestFixture.valid();
            var command =
                    new UpdateChecklistItemCommand(
                            1L,
                            1,
                            "Lombok 어노테이션 사용 여부 확인",
                            "AUTOMATED",
                            "ARCHUNIT",
                            "AGG-001-CHECK-1",
                            true);

            given(mapper.toCommand(eq(id), any())).willReturn(command);

            // When & Then
            mockMvc.perform(
                            put(ChecklistItemApiEndpoints.BY_ID, id)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "checklist-item-update",
                                    pathParameters(
                                            parameterWithName("id")
                                                    .description("ChecklistItem ID")),
                                    requestFields(
                                            fieldWithPath("sequenceOrder")
                                                    .description("순서 (1 이상)")
                                                    .type(Integer.class),
                                            fieldWithPath("checkDescription")
                                                    .description("체크 설명 (최대 500자)")
                                                    .type(String.class),
                                            fieldWithPath("checkType")
                                                    .description(
                                                            "체크 타입 (AUTOMATED, MANUAL, SEMI_AUTO)")
                                                    .type(String.class),
                                            fieldWithPath("automationTool")
                                                    .description("자동화 도구 (최대 50자, null일 경우 빈 문자열)")
                                                    .type(String.class),
                                            fieldWithPath("automationRuleId")
                                                    .description(
                                                            "자동화 규칙 ID (최대 100자, null일 경우 빈 문자열)")
                                                    .type(String.class),
                                            fieldWithPath("isCritical")
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

        @Test
        @DisplayName("필수 필드 누락 시 400 Bad Request 반환")
        void missingRequiredField_ShouldReturn400() throws Exception {
            // Given
            Long id = 1L;
            var request = UpdateChecklistItemApiRequestFixture.invalidWithBlankCheckDescription();

            // When & Then
            mockMvc.perform(
                            put(ChecklistItemApiEndpoints.BY_ID, id)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "checklist-item-update-validation-error",
                                    pathParameters(
                                            parameterWithName("id")
                                                    .description("ChecklistItem ID")),
                                    requestFields(
                                            fieldWithPath("sequenceOrder")
                                                    .description("순서")
                                                    .type(Integer.class),
                                            fieldWithPath("checkDescription")
                                                    .description("체크 설명 (필수)")
                                                    .type(String.class),
                                            fieldWithPath("checkType")
                                                    .description("체크 타입")
                                                    .type(String.class),
                                            fieldWithPath("automationTool")
                                                    .description("자동화 도구")
                                                    .type(String.class),
                                            fieldWithPath("automationRuleId")
                                                    .description("자동화 규칙 ID")
                                                    .type(String.class),
                                            fieldWithPath("isCritical")
                                                    .description("필수 여부")
                                                    .type(Boolean.class)),
                                    responseFields(
                                            fieldWithPath("type")
                                                    .description("에러 타입 URI")
                                                    .type(String.class),
                                            fieldWithPath("title")
                                                    .description("에러 제목")
                                                    .type(String.class),
                                            fieldWithPath("status")
                                                    .description("HTTP 상태 코드")
                                                    .type(Integer.class),
                                            fieldWithPath("detail")
                                                    .description("에러 상세 설명")
                                                    .type(String.class),
                                            fieldWithPath("instance")
                                                    .description("에러 발생 URI")
                                                    .type(String.class),
                                            fieldWithPath("code")
                                                    .description("에러 코드")
                                                    .type(String.class),
                                            subsectionWithPath("errors").description("필드별 에러 메시지"),
                                            fieldWithPath("timestamp")
                                                    .description("에러 발생 시간")
                                                    .type(String.class))));
        }
    }
}
