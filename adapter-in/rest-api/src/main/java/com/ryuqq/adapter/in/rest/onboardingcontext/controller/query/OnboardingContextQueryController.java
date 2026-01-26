package com.ryuqq.adapter.in.rest.onboardingcontext.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.onboardingcontext.OnboardingContextApiEndpoints;
import com.ryuqq.adapter.in.rest.onboardingcontext.dto.request.SearchOnboardingContextsCursorApiRequest;
import com.ryuqq.adapter.in.rest.onboardingcontext.dto.response.OnboardingContextApiResponse;
import com.ryuqq.adapter.in.rest.onboardingcontext.mapper.OnboardingContextQueryApiMapper;
import com.ryuqq.application.onboardingcontext.dto.query.OnboardingContextSearchParams;
import com.ryuqq.application.onboardingcontext.dto.response.OnboardingContextSliceResult;
import com.ryuqq.application.onboardingcontext.port.in.SearchOnboardingContextsByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OnboardingContextQueryController - OnboardingContext 조회 API
 *
 * <p>온보딩 컨텍스트 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: Controller는 @RestController로 정의.
 *
 * <p>CTR-002: Controller는 UseCase만 주입받음.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 처리.
 *
 * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "OnboardingContext", description = "Serena 온보딩 컨텍스트 관리 API")
@RestController
@RequestMapping(OnboardingContextApiEndpoints.ONBOARDING_CONTEXTS)
public class OnboardingContextQueryController {

    private final SearchOnboardingContextsByCursorUseCase searchOnboardingContextsByCursorUseCase;
    private final OnboardingContextQueryApiMapper mapper;

    /**
     * OnboardingContextQueryController 생성자
     *
     * @param searchOnboardingContextsByCursorUseCase OnboardingContext 복합 조건 조회 UseCase
     * @param mapper API 매퍼
     */
    public OnboardingContextQueryController(
            SearchOnboardingContextsByCursorUseCase searchOnboardingContextsByCursorUseCase,
            OnboardingContextQueryApiMapper mapper) {
        this.searchOnboardingContextsByCursorUseCase = searchOnboardingContextsByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * OnboardingContext 복합 조건 조회 API (커서 기반)
     *
     * <p>TechStack ID, Architecture ID, 컨텍스트 타입 필터를 지원하여 온보딩 컨텍스트 목록을 커서 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (커서 기반, 필터 포함)
     * @return OnboardingContext 슬라이스 목록
     */
    @Operation(
            summary = "OnboardingContext 복합 조건 조회",
            description =
                    "TechStack ID, Architecture ID, 컨텍스트 타입 필터를 지원하여 "
                            + "온보딩 컨텍스트 목록을 커서 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<OnboardingContextApiResponse>>>
            searchOnboardingContextsByCursor(
                    @Valid SearchOnboardingContextsCursorApiRequest request) {

        OnboardingContextSearchParams searchParams = mapper.toSearchParams(request);
        OnboardingContextSliceResult sliceResult =
                searchOnboardingContextsByCursorUseCase.execute(searchParams);
        SliceApiResponse<OnboardingContextApiResponse> response =
                mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
