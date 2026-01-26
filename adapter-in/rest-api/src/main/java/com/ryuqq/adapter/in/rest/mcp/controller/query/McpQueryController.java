package com.ryuqq.adapter.in.rest.mcp.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.mcp.McpApiEndpoints;
import com.ryuqq.adapter.in.rest.mcp.dto.request.GetConfigFilesApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.request.GetOnboardingApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.request.ModuleContextApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.request.PlanningContextApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.request.ValidationContextApiRequest;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ConfigFilesApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ModuleContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.OnboardingContextsApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.PlanningContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.dto.response.ValidationContextApiResponse;
import com.ryuqq.adapter.in.rest.mcp.mapper.McpQueryApiMapper;
import com.ryuqq.application.mcp.dto.query.GetConfigFilesQuery;
import com.ryuqq.application.mcp.dto.query.GetOnboardingQuery;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * McpQueryController - MCP Query API Controller
 *
 * <p>MCP (Module-Centric Planning) 워크플로우 Query 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@SuppressWarnings("PMD.ExcessiveImports")
@Tag(name = "MCP", description = "MCP 워크플로우 API")
@RestController
@RequestMapping(McpApiEndpoints.BASE)
public class McpQueryController {

    private final GetPlanningContextUseCase getPlanningContextUseCase;
    private final GetModuleContextUseCase getModuleContextUseCase;
    private final GetValidationContextUseCase getValidationContextUseCase;
    private final GetConfigFilesForMcpUseCase getConfigFilesForMcpUseCase;
    private final GetOnboardingForMcpUseCase getOnboardingForMcpUseCase;
    private final McpQueryApiMapper mapper;

    /**
     * McpQueryController 생성자
     *
     * @param getPlanningContextUseCase Planning Context 조회 UseCase
     * @param getModuleContextUseCase Module Context 조회 UseCase
     * @param getValidationContextUseCase Validation Context 조회 UseCase
     * @param getConfigFilesForMcpUseCase Config Files 조회 UseCase
     * @param getOnboardingForMcpUseCase Onboarding Context 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public McpQueryController(
            GetPlanningContextUseCase getPlanningContextUseCase,
            GetModuleContextUseCase getModuleContextUseCase,
            GetValidationContextUseCase getValidationContextUseCase,
            GetConfigFilesForMcpUseCase getConfigFilesForMcpUseCase,
            GetOnboardingForMcpUseCase getOnboardingForMcpUseCase,
            McpQueryApiMapper mapper) {
        this.getPlanningContextUseCase = getPlanningContextUseCase;
        this.getModuleContextUseCase = getModuleContextUseCase;
        this.getValidationContextUseCase = getValidationContextUseCase;
        this.getConfigFilesForMcpUseCase = getConfigFilesForMcpUseCase;
        this.getOnboardingForMcpUseCase = getOnboardingForMcpUseCase;
        this.mapper = mapper;
    }

    /**
     * Planning Context 조회 API
     *
     * <p>개발 계획 수립에 필요한 컨텍스트를 조회합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CTR-002: ResponseEntity<ApiResponse<T>> 래핑 필수.
     *
     * @param request Planning Context 조회 요청
     * @return Planning Context 응답
     */
    @Operation(
            summary = "Planning Context 조회",
            description = "개발 계획 수립에 필요한 컨텍스트를 조회합니다. layers 파라미터는 필수입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (layers 필수)")
    })
    @GetMapping(McpApiEndpoints.PLANNING_CONTEXT)
    public ResponseEntity<ApiResponse<PlanningContextApiResponse>> getPlanningContext(
            @Valid @ModelAttribute PlanningContextApiRequest request) {

        PlanningContextQuery query = mapper.toQuery(request);
        PlanningContextResult result = getPlanningContextUseCase.execute(query);
        PlanningContextApiResponse response = mapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * Module Context 조회 API
     *
     * <p>코드 생성에 필요한 Module 전체 컨텍스트를 조회합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CTR-002: ResponseEntity<ApiResponse<T>> 래핑 필수.
     *
     * @param moduleId 모듈 ID (Path Variable)
     * @param request Module Context 조회 요청
     * @return Module Context 응답
     */
    @Operation(summary = "Module Context 조회", description = "코드 생성에 필요한 Module 전체 컨텍스트를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Module을 찾을 수 없음")
    })
    @GetMapping(McpApiEndpoints.MODULE_CONTEXT)
    public ResponseEntity<ApiResponse<ModuleContextApiResponse>> getModuleContext(
            @Parameter(description = "모듈 ID", example = "1", required = true)
                    @PathVariable(McpApiEndpoints.PATH_MODULE_ID)
                    Long moduleId,
            @ModelAttribute ModuleContextApiRequest request) {

        ModuleContextQuery query = mapper.toQuery(request, moduleId);
        ModuleContextResult result = getModuleContextUseCase.execute(query);
        ModuleContextApiResponse response = mapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * Validation Context 조회 API
     *
     * <p>코드 검증에 필요한 Zero-Tolerance + Checklist를 조회합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CTR-002: ResponseEntity<ApiResponse<T>> 래핑 필수.
     *
     * @param request Validation Context 조회 요청
     * @return Validation Context 응답
     */
    @Operation(
            summary = "Validation Context 조회",
            description = "코드 검증에 필요한 Zero-Tolerance + Checklist를 조회합니다. layers 파라미터는 필수입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (layers 필수)")
    })
    @GetMapping(McpApiEndpoints.VALIDATION_CONTEXT)
    public ResponseEntity<ApiResponse<ValidationContextApiResponse>> getValidationContext(
            @Valid @ModelAttribute ValidationContextApiRequest request) {

        ValidationContextQuery query = mapper.toQuery(request);
        ValidationContextResult result = getValidationContextUseCase.execute(query);
        ValidationContextApiResponse response = mapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    // ========================================
    // Convention Hub Endpoints (Phase 2)
    // ========================================

    /**
     * Config Files 조회 API (init_project Tool용)
     *
     * <p>설정 파일 템플릿 목록을 조회합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CTR-002: ResponseEntity<ApiResponse<T>> 래핑 필수.
     *
     * @param request Config Files 조회 요청
     * @return Config Files 응답
     */
    @Operation(
            summary = "Config Files 조회",
            description = "init_project Tool용 설정 파일 템플릿 목록을 조회합니다. techStackId는 필수입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (techStackId 필수)")
    })
    @GetMapping(McpApiEndpoints.CONFIG_FILES)
    public ResponseEntity<ApiResponse<ConfigFilesApiResponse>> getConfigFiles(
            @Valid @ModelAttribute GetConfigFilesApiRequest request) {

        GetConfigFilesQuery query = mapper.toQuery(request);
        ConfigFilesResult result = getConfigFilesForMcpUseCase.execute(query);
        ConfigFilesApiResponse response = mapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * Onboarding Context 조회 API (get_onboarding_context Tool용)
     *
     * <p>온보딩 컨텍스트 목록을 조회합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CTR-002: ResponseEntity<ApiResponse<T>> 래핑 필수.
     *
     * @param request Onboarding Context 조회 요청
     * @return Onboarding Context 응답
     */
    @Operation(
            summary = "Onboarding Context 조회",
            description = "get_onboarding_context Tool용 온보딩 컨텍스트 목록을 조회합니다. techStackId는 필수입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (techStackId 필수)")
    })
    @GetMapping(McpApiEndpoints.ONBOARDING)
    public ResponseEntity<ApiResponse<OnboardingContextsApiResponse>> getOnboarding(
            @Valid @ModelAttribute GetOnboardingApiRequest request) {

        GetOnboardingQuery query = mapper.toQuery(request);
        OnboardingContextsResult result = getOnboardingForMcpUseCase.execute(query);
        OnboardingContextsApiResponse response = mapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
