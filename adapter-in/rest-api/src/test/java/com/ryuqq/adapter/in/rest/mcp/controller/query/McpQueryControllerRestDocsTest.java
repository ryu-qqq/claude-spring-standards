package com.ryuqq.adapter.in.rest.mcp.controller.query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.adapter.in.rest.common.RestDocsTestSupport;
import com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.adapter.in.rest.mcp.McpApiEndpoints;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ArchitectureSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ChecklistItemApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ConfigFileApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ConfigFilesApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ExecutionContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.LayerSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.LayerValidationStatsApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ModuleContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ModuleContextSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ModuleSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.OnboardingApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.OnboardingContextsApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.PlanningContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.PlanningContextSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.RuleContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.TechStackSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ValidationContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ValidationContextSummaryApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ZeroToleranceRuleApiResponse;
import com.ryuqq.adapter.in.rest.mcp.mapper.McpQueryApiMapper;
import com.ryuqq.application.mcp.dto.query.ModuleContextQuery;
import com.ryuqq.application.mcp.dto.query.PlanningContextQuery;
import com.ryuqq.application.mcp.dto.query.ValidationContextQuery;
import com.ryuqq.application.mcp.dto.response.ConfigFilesResult;
import com.ryuqq.application.mcp.dto.response.ModuleContextResult;
import com.ryuqq.application.mcp.dto.response.OnboardingContextsResult;
import com.ryuqq.application.mcp.dto.response.PlanningContextResult;
import com.ryuqq.application.mcp.dto.response.ValidationContextResult;
import com.ryuqq.application.mcp.port.in.GetConfigFilesForMcpUseCase;
import com.ryuqq.application.mcp.port.in.GetModuleContextUseCase;
import com.ryuqq.application.mcp.port.in.GetOnboardingForMcpUseCase;
import com.ryuqq.application.mcp.port.in.GetPlanningContextUseCase;
import com.ryuqq.application.mcp.port.in.GetValidationContextUseCase;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * McpQueryController REST Docs 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@WebMvcTest(McpQueryController.class)
@DisplayName("McpQueryController REST Docs")
class McpQueryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean private GetPlanningContextUseCase getPlanningContextUseCase;

    @MockitoBean private GetModuleContextUseCase getModuleContextUseCase;

    @MockitoBean private GetValidationContextUseCase getValidationContextUseCase;

    @MockitoBean private GetConfigFilesForMcpUseCase getConfigFilesForMcpUseCase;

    @MockitoBean private GetOnboardingForMcpUseCase getOnboardingForMcpUseCase;

    @MockitoBean private McpQueryApiMapper mapper;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("GET /api/v1/templates/mcp/planning-context - Planning Context 조회")
    class GetPlanningContext {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var query = new PlanningContextQuery(List.of("DOMAIN", "APPLICATION"), 1L);
            var response =
                    new PlanningContextApiResponse(
                            new TechStackSummaryApiResponse(
                                    1L, "Spring Boot 3.5.x + Java 21", "Spring Boot 기반 기술 스택"),
                            new ArchitectureSummaryApiResponse(
                                    1L, "Hexagonal Architecture", "포트와 어댑터 아키텍처"),
                            List.of(),
                            new PlanningContextSummaryApiResponse(4, 12, 25, 150));

            given(
                            mapper.toQuery(
                                    any(
                                            com.ryuqq.adapter.in.rest.mcp.dto.request
                                                    .PlanningContextApiRequest.class)))
                    .willReturn(query);
            given(getPlanningContextUseCase.execute(any())).willReturn(null);
            given(mapper.toResponse((PlanningContextResult) any())).willReturn(response);

            // When & Then
            mockMvc.perform(
                            get(McpApiEndpoints.BASE + McpApiEndpoints.PLANNING_CONTEXT)
                                    .param("layers", "DOMAIN", "APPLICATION")
                                    .param("techStackId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.techStack").exists())
                    .andExpect(jsonPath("$.data.architecture").exists())
                    .andDo(
                            document(
                                    "mcp-planning-context",
                                    queryParameters(
                                            parameterWithName("layers")
                                                    .description(
                                                            "레이어 코드 목록 (필수, 예: DOMAIN, APPLICATION,"
                                                                    + " PERSISTENCE, REST_API)"),
                                            parameterWithName("techStackId")
                                                    .description("기술 스택 ID (선택, null이면 활성 스택 사용)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.techStack")
                                                    .description("기술 스택 정보")
                                                    .type(Object.class),
                                            fieldWithPath("data.techStack.id")
                                                    .description("기술 스택 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.techStack.name")
                                                    .description("기술 스택 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.techStack.description")
                                                    .description("기술 스택 설명")
                                                    .type(String.class),
                                            fieldWithPath("data.architecture")
                                                    .description("아키텍처 정보")
                                                    .type(Object.class),
                                            fieldWithPath("data.architecture.id")
                                                    .description("아키텍처 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.architecture.name")
                                                    .description("아키텍처 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.architecture.description")
                                                    .description("아키텍처 설명")
                                                    .type(String.class),
                                            fieldWithPath("data.layers")
                                                    .description("레이어 목록 (모듈 및 패키지 포함)")
                                                    .type(List.class),
                                            fieldWithPath("data.summary")
                                                    .description("요약 정보")
                                                    .type(Object.class),
                                            fieldWithPath("data.summary.totalModules")
                                                    .description("전체 모듈 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.totalPackages")
                                                    .description("전체 패키지 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.totalTemplates")
                                                    .description("전체 템플릿 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.totalRules")
                                                    .description("전체 규칙 수")
                                                    .type(Integer.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("필수 파라미터 누락 시 400 Bad Request 반환")
        void missingRequiredParameter_ShouldReturn400() throws Exception {
            // When & Then
            mockMvc.perform(get(McpApiEndpoints.BASE + McpApiEndpoints.PLANNING_CONTEXT))
                    .andExpect(status().isBadRequest())
                    .andDo(document("mcp-planning-context-validation-error"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/templates/mcp/module/{moduleId}/context - Module Context 조회")
    class GetModuleContext {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            Long moduleId = 1L;
            var query = new ModuleContextQuery(moduleId, 1L); // classTypeId = 1 (AGGREGATE)
            var response =
                    new ModuleContextApiResponse(
                            new ModuleSummaryApiResponse(
                                    1L,
                                    "domain",
                                    "도메인 모듈",
                                    new LayerSummaryApiResponse("DOMAIN", "Domain Layer")),
                            new ExecutionContextApiResponse(List.of()),
                            new RuleContextApiResponse(List.of()),
                            new ModuleContextSummaryApiResponse(5, 10, 25, 3, 8));

            given(mapper.toQuery(any(), eq(moduleId))).willReturn(query);
            given(getModuleContextUseCase.execute(any())).willReturn(null);
            given(mapper.toResponse((ModuleContextResult) any())).willReturn(response);

            // When & Then
            mockMvc.perform(
                            get(McpApiEndpoints.BASE + "/module/{moduleId}/context", moduleId)
                                    .param("classTypeId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.module").exists())
                    .andExpect(jsonPath("$.data.summary").exists())
                    .andDo(
                            document(
                                    "mcp-module-context",
                                    pathParameters(
                                            parameterWithName("moduleId").description("모듈 ID")),
                                    queryParameters(
                                            parameterWithName("classTypeId")
                                                    .description("클래스 타입 ID 필터 (선택)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.module")
                                                    .description("모듈 정보")
                                                    .type(Object.class),
                                            fieldWithPath("data.module.id")
                                                    .description("모듈 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.module.name")
                                                    .description("모듈 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.module.description")
                                                    .description("모듈 설명")
                                                    .type(String.class),
                                            fieldWithPath("data.module.layer")
                                                    .description("레이어 정보")
                                                    .type(Object.class),
                                            fieldWithPath("data.module.layer.code")
                                                    .description("레이어 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.module.layer.name")
                                                    .description("레이어 이름")
                                                    .type(String.class),
                                            fieldWithPath("data.executionContext")
                                                    .description("실행 컨텍스트")
                                                    .type(Object.class),
                                            fieldWithPath("data.executionContext.packageStructures")
                                                    .description("패키지 구조 목록")
                                                    .type(List.class),
                                            fieldWithPath("data.ruleContext")
                                                    .description("규칙 컨텍스트")
                                                    .type(Object.class),
                                            fieldWithPath("data.ruleContext.conventions")
                                                    .description("컨벤션 목록")
                                                    .type(List.class),
                                            fieldWithPath("data.summary")
                                                    .description("요약 정보")
                                                    .type(Object.class),
                                            fieldWithPath("data.summary.packageCount")
                                                    .description("패키지 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.templateCount")
                                                    .description("템플릿 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.ruleCount")
                                                    .description("규칙 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.zeroToleranceCount")
                                                    .description("Zero-Tolerance 규칙 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.archTestCount")
                                                    .description("ArchUnit 테스트 수")
                                                    .type(Integer.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/templates/mcp/validation-context - Validation Context 조회")
    class GetValidationContext {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var query =
                    new ValidationContextQuery(
                            1L,
                            1L,
                            List.of("DOMAIN", "APPLICATION"),
                            List.of("AGGREGATE", "USE_CASE")); // classTypes (String codes)
            var response =
                    new ValidationContextApiResponse(
                            List.of(
                                    new ZeroToleranceRuleApiResponse(
                                            "DOM-001",
                                            "Lombok 사용 금지",
                                            "DOMAIN",
                                            List.of("AGGREGATE", "ENTITY", "VO"),
                                            "@(Data|Getter|Setter)",
                                            "REGEX",
                                            true,
                                            "Domain Layer에서 Lombok 사용은 허용되지 않습니다.")),
                            List.of(
                                    new ChecklistItemApiResponse(
                                            "DOM-002",
                                            "Aggregate는 불변성을 유지해야 합니다.",
                                            "CRITICAL",
                                            false)),
                            new ValidationContextSummaryApiResponse(
                                    15,
                                    45,
                                    12,
                                    Map.of(
                                            "DOMAIN",
                                            new LayerValidationStatsApiResponse(8, 20),
                                            "APPLICATION",
                                            new LayerValidationStatsApiResponse(7, 25))));

            given(
                            mapper.toQuery(
                                    any(
                                            com.ryuqq.adapter.in.rest.mcp.dto.request
                                                    .ValidationContextApiRequest.class)))
                    .willReturn(query);
            given(getValidationContextUseCase.execute(any())).willReturn(null);
            given(mapper.toResponse((ValidationContextResult) any())).willReturn(response);

            // When & Then
            mockMvc.perform(
                            get(McpApiEndpoints.BASE + McpApiEndpoints.VALIDATION_CONTEXT)
                                    .param("techStackId", "1")
                                    .param("architectureId", "1")
                                    .param("layers", "DOMAIN", "APPLICATION")
                                    .param("classTypes", "AGGREGATE", "USE_CASE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.zeroToleranceRules").isArray())
                    .andExpect(jsonPath("$.data.checklist").isArray())
                    .andDo(
                            document(
                                    "mcp-validation-context",
                                    queryParameters(
                                            parameterWithName("techStackId")
                                                    .description("기술 스택 ID (필수)"),
                                            parameterWithName("architectureId")
                                                    .description("아키텍처 ID (필수)"),
                                            parameterWithName("layers")
                                                    .description(
                                                            "레이어 코드 목록 (필수, 예: DOMAIN, APPLICATION,"
                                                                    + " PERSISTENCE, REST_API)"),
                                            parameterWithName("classTypes")
                                                    .description("클래스 타입 코드 목록 (선택)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.zeroToleranceRules")
                                                    .description("Zero-Tolerance 규칙 목록")
                                                    .type(List.class),
                                            fieldWithPath("data.zeroToleranceRules[].ruleCode")
                                                    .description("규칙 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.zeroToleranceRules[].ruleTitle")
                                                    .description("규칙 제목")
                                                    .type(String.class),
                                            fieldWithPath("data.zeroToleranceRules[].layer")
                                                    .description("적용 레이어")
                                                    .type(String.class),
                                            fieldWithPath("data.zeroToleranceRules[].classTypes")
                                                    .description("적용 클래스 타입 목록")
                                                    .type(List.class),
                                            fieldWithPath(
                                                            "data.zeroToleranceRules[].detectionPattern")
                                                    .description("탐지 패턴")
                                                    .type(String.class),
                                            fieldWithPath("data.zeroToleranceRules[].detectionType")
                                                    .description("탐지 방식 (REGEX, AST, ARCHUNIT)")
                                                    .type(String.class),
                                            fieldWithPath("data.zeroToleranceRules[].autoRejectPr")
                                                    .description("PR 자동 거부 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.zeroToleranceRules[].message")
                                                    .description("위반 시 표시할 메시지")
                                                    .type(String.class),
                                            fieldWithPath("data.checklist")
                                                    .description("체크리스트 항목 목록")
                                                    .type(List.class),
                                            fieldWithPath("data.checklist[].ruleCode")
                                                    .description("규칙 코드")
                                                    .type(String.class),
                                            fieldWithPath("data.checklist[].checkDescription")
                                                    .description("체크 설명")
                                                    .type(String.class),
                                            fieldWithPath("data.checklist[].severity")
                                                    .description("심각도 (CRITICAL, MAJOR, MINOR)")
                                                    .type(String.class),
                                            fieldWithPath("data.checklist[].autoCheckable")
                                                    .description("자동 체크 가능 여부")
                                                    .type(Boolean.class),
                                            fieldWithPath("data.summary")
                                                    .description("요약 정보")
                                                    .type(Object.class),
                                            fieldWithPath("data.summary.totalZeroTolerance")
                                                    .description("전체 Zero-Tolerance 규칙 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.totalChecklist")
                                                    .description("전체 체크리스트 항목 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.autoCheckableCount")
                                                    .description("자동 체크 가능 항목 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.byLayer")
                                                    .description("레이어별 통계")
                                                    .type(Object.class),
                                            fieldWithPath("data.summary.byLayer.DOMAIN")
                                                    .description("DOMAIN 레이어 통계")
                                                    .type(Object.class),
                                            fieldWithPath(
                                                            "data.summary.byLayer.DOMAIN.zeroTolerance")
                                                    .description("Zero-Tolerance 규칙 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.byLayer.DOMAIN.checklist")
                                                    .description("체크리스트 항목 수")
                                                    .type(Integer.class),
                                            fieldWithPath("data.summary.byLayer.APPLICATION")
                                                    .description("APPLICATION 레이어 통계")
                                                    .type(Object.class),
                                            fieldWithPath(
                                                            "data.summary.byLayer.APPLICATION.zeroTolerance")
                                                    .description("Zero-Tolerance 규칙 수")
                                                    .type(Integer.class),
                                            fieldWithPath(
                                                            "data.summary.byLayer.APPLICATION.checklist")
                                                    .description("체크리스트 항목 수")
                                                    .type(Integer.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("필수 파라미터 누락 시 400 Bad Request 반환")
        void missingRequiredParameter_ShouldReturn400() throws Exception {
            // When & Then
            mockMvc.perform(get(McpApiEndpoints.BASE + McpApiEndpoints.VALIDATION_CONTEXT))
                    .andExpect(status().isBadRequest())
                    .andDo(document("mcp-validation-context-validation-error"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/templates/mcp/config-files - Config Files 조회")
    class GetConfigFiles {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var response =
                    new ConfigFilesApiResponse(
                            List.of(
                                    new ConfigFileApiResponse(
                                            1L,
                                            "CLAUDE",
                                            ".claude/",
                                            "CLAUDE.md",
                                            "Claude Code 메인 설정 파일",
                                            "# Claude Configuration\n\n...",
                                            1),
                                    new ConfigFileApiResponse(
                                            2L,
                                            "CURSOR",
                                            ".cursor/",
                                            "rules.json",
                                            "Cursor 에디터 규칙 파일",
                                            "{\"rules\": []}",
                                            2)),
                            2);

            given(
                            mapper.toQuery(
                                    any(
                                            com.ryuqq.adapter.in.rest.mcp.dto.request
                                                    .GetConfigFilesApiRequest.class)))
                    .willReturn(null);
            given(getConfigFilesForMcpUseCase.execute(any())).willReturn(null);
            given(mapper.toResponse((ConfigFilesResult) any())).willReturn(response);

            // When & Then
            mockMvc.perform(
                            get(McpApiEndpoints.BASE + McpApiEndpoints.CONFIG_FILES)
                                    .param("techStackId", "1")
                                    .param("toolTypes", "CLAUDE", "CURSOR"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.configFiles").isArray())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andDo(
                            document(
                                    "mcp-config-files",
                                    queryParameters(
                                            parameterWithName("techStackId")
                                                    .description("기술 스택 ID (필수)"),
                                            parameterWithName("architectureId")
                                                    .description("아키텍처 ID (선택)")
                                                    .optional(),
                                            parameterWithName("toolTypes")
                                                    .description(
                                                            "도구 타입 목록 (선택, 예: CLAUDE, CURSOR,"
                                                                    + " COPILOT)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.configFiles")
                                                    .description("설정 파일 템플릿 목록")
                                                    .type(List.class),
                                            fieldWithPath("data.configFiles[].id")
                                                    .description("설정 파일 템플릿 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.configFiles[].toolType")
                                                    .description("도구 타입 (CLAUDE, CURSOR, COPILOT)")
                                                    .type(String.class),
                                            fieldWithPath("data.configFiles[].filePath")
                                                    .description("파일 경로")
                                                    .type(String.class),
                                            fieldWithPath("data.configFiles[].fileName")
                                                    .description("파일명")
                                                    .type(String.class),
                                            fieldWithPath("data.configFiles[].description")
                                                    .description("설명")
                                                    .type(String.class),
                                            fieldWithPath("data.configFiles[].templateContent")
                                                    .description("템플릿 내용")
                                                    .type(String.class),
                                            fieldWithPath("data.configFiles[].priority")
                                                    .description("우선순위")
                                                    .type(Integer.class),
                                            fieldWithPath("data.totalCount")
                                                    .description("전체 개수")
                                                    .type(Integer.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("필수 파라미터 누락 시 400 Bad Request 반환")
        void missingRequiredParameter_ShouldReturn400() throws Exception {
            // When & Then
            mockMvc.perform(get(McpApiEndpoints.BASE + McpApiEndpoints.CONFIG_FILES))
                    .andExpect(status().isBadRequest())
                    .andDo(document("mcp-config-files-validation-error"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/templates/mcp/onboarding - Onboarding Context 조회")
    class GetOnboarding {

        @Test
        @DisplayName("정상 요청 시 200 OK 반환")
        void validRequest_ShouldReturn200() throws Exception {
            // Given
            var response =
                    new OnboardingContextsApiResponse(
                            List.of(
                                    new OnboardingApiResponse(
                                            1L,
                                            "SUMMARY",
                                            "프로젝트 개요",
                                            "# Project Summary\n\nSpring Boot 기반...",
                                            1),
                                    new OnboardingApiResponse(
                                            2L,
                                            "ZERO_TOLERANCE",
                                            "Zero-Tolerance 규칙",
                                            "# Zero-Tolerance Rules\n\n1. Lombok 금지",
                                            2)),
                            2);

            given(
                            mapper.toQuery(
                                    any(
                                            com.ryuqq.adapter.in.rest.mcp.dto.request
                                                    .GetOnboardingApiRequest.class)))
                    .willReturn(null);
            given(getOnboardingForMcpUseCase.execute(any())).willReturn(null);
            given(mapper.toResponse((OnboardingContextsResult) any())).willReturn(response);

            // When & Then
            mockMvc.perform(
                            get(McpApiEndpoints.BASE + McpApiEndpoints.ONBOARDING)
                                    .param("techStackId", "1")
                                    .param("contextTypes", "SUMMARY", "ZERO_TOLERANCE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.contexts").isArray())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andDo(
                            document(
                                    "mcp-onboarding",
                                    queryParameters(
                                            parameterWithName("techStackId")
                                                    .description("기술 스택 ID (필수)"),
                                            parameterWithName("architectureId")
                                                    .description("아키텍처 ID (선택)")
                                                    .optional(),
                                            parameterWithName("contextTypes")
                                                    .description(
                                                            "컨텍스트 타입 목록 (선택, 예: SUMMARY,"
                                                                + " ZERO_TOLERANCE, RULES_INDEX,"
                                                                + " MCP_USAGE)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .description("응답 데이터")
                                                    .type(Object.class),
                                            fieldWithPath("data.contexts")
                                                    .description("온보딩 컨텍스트 목록")
                                                    .type(List.class),
                                            fieldWithPath("data.contexts[].id")
                                                    .description("온보딩 컨텍스트 ID")
                                                    .type(Long.class),
                                            fieldWithPath("data.contexts[].contextType")
                                                    .description(
                                                            "컨텍스트 타입 (SUMMARY, ZERO_TOLERANCE,"
                                                                    + " RULES_INDEX, MCP_USAGE)")
                                                    .type(String.class),
                                            fieldWithPath("data.contexts[].title")
                                                    .description("제목")
                                                    .type(String.class),
                                            fieldWithPath("data.contexts[].content")
                                                    .description("내용 (Markdown)")
                                                    .type(String.class),
                                            fieldWithPath("data.contexts[].priority")
                                                    .description("우선순위")
                                                    .type(Integer.class),
                                            fieldWithPath("data.totalCount")
                                                    .description("전체 개수")
                                                    .type(Integer.class),
                                            fieldWithPath("timestamp")
                                                    .description("응답 시간")
                                                    .type(String.class),
                                            fieldWithPath("requestId")
                                                    .description("요청 ID")
                                                    .type(String.class))));
        }

        @Test
        @DisplayName("필수 파라미터 누락 시 400 Bad Request 반환")
        void missingRequiredParameter_ShouldReturn400() throws Exception {
            // When & Then
            mockMvc.perform(get(McpApiEndpoints.BASE + McpApiEndpoints.ONBOARDING))
                    .andExpect(status().isBadRequest())
                    .andDo(document("mcp-onboarding-validation-error"));
        }
    }
}
