package com.ryuqq.adapter.in.rest.onboardingcontext.controller.command;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.onboardingcontext.OnboardingContextApiEndpoints;
import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.CreateOnboardingContextApiRequest;
import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.UpdateOnboardingContextApiRequest;
import com.ryuqq.adapter.in.rest.onboardingcontext.dto.response.OnboardingContextIdApiResponse;
import com.ryuqq.adapter.in.rest.onboardingcontext.mapper.OnboardingContextCommandApiMapper;
import com.ryuqq.application.onboardingcontext.dto.command.CreateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.dto.command.UpdateOnboardingContextCommand;
import com.ryuqq.application.onboardingcontext.port.in.CreateOnboardingContextUseCase;
import com.ryuqq.application.onboardingcontext.port.in.UpdateOnboardingContextUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OnboardingContextCommandController - OnboardingContext 생성/수정 API
 *
 * <p>온보딩 컨텍스트 생성/수정 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-003: @Valid 필수 적용.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "OnboardingContext", description = "Serena 온보딩 컨텍스트 관리 API")
@RestController
@RequestMapping(OnboardingContextApiEndpoints.ONBOARDING_CONTEXTS)
public class OnboardingContextCommandController {

    private final CreateOnboardingContextUseCase createOnboardingContextUseCase;
    private final UpdateOnboardingContextUseCase updateOnboardingContextUseCase;
    private final OnboardingContextCommandApiMapper mapper;

    /**
     * OnboardingContextCommandController 생성자
     *
     * @param createOnboardingContextUseCase OnboardingContext 생성 UseCase
     * @param updateOnboardingContextUseCase OnboardingContext 수정 UseCase
     * @param mapper API 매퍼
     */
    public OnboardingContextCommandController(
            CreateOnboardingContextUseCase createOnboardingContextUseCase,
            UpdateOnboardingContextUseCase updateOnboardingContextUseCase,
            OnboardingContextCommandApiMapper mapper) {
        this.createOnboardingContextUseCase = createOnboardingContextUseCase;
        this.updateOnboardingContextUseCase = updateOnboardingContextUseCase;
        this.mapper = mapper;
    }

    /**
     * OnboardingContext 생성 API
     *
     * <p>새로운 온보딩 컨텍스트를 생성합니다.
     *
     * @param request 생성 요청 DTO
     * @return 생성된 OnboardingContext ID
     */
    @Operation(summary = "OnboardingContext 생성", description = "새로운 Serena 온보딩 컨텍스트를 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "TechStack을 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<OnboardingContextIdApiResponse>> create(
            @Valid @RequestBody CreateOnboardingContextApiRequest request) {

        CreateOnboardingContextCommand command = mapper.toCommand(request);
        Long id = createOnboardingContextUseCase.execute(command);

        OnboardingContextIdApiResponse response = OnboardingContextIdApiResponse.of(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    /**
     * OnboardingContext 수정 API
     *
     * <p>기존 온보딩 컨텍스트의 정보를 수정합니다.
     *
     * @param onboardingContextId OnboardingContext ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (200 OK)
     */
    @Operation(summary = "OnboardingContext 수정", description = "기존 Serena 온보딩 컨텍스트의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "OnboardingContext를 찾을 수 없음")
    })
    @PutMapping(OnboardingContextApiEndpoints.ID)
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "OnboardingContext ID", required = true)
                    @PathVariable(OnboardingContextApiEndpoints.PATH_ONBOARDING_CONTEXT_ID)
                    Long onboardingContextId,
            @Valid @RequestBody UpdateOnboardingContextApiRequest request) {

        UpdateOnboardingContextCommand command = mapper.toCommand(onboardingContextId, request);
        updateOnboardingContextUseCase.execute(command);

        return ResponseEntity.ok(ApiResponse.of());
    }
}
