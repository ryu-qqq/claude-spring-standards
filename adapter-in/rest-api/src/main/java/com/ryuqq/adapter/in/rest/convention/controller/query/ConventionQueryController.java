package com.ryuqq.adapter.in.rest.convention.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.convention.ConventionApiEndpoints;
import com.ryuqq.adapter.in.rest.convention.dto.request.SearchConventionsCursorApiRequest;
import com.ryuqq.adapter.in.rest.convention.dto.response.ConventionApiResponse;
import com.ryuqq.adapter.in.rest.convention.mapper.ConventionQueryApiMapper;
import com.ryuqq.application.convention.dto.query.ConventionSearchParams;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import com.ryuqq.application.convention.port.in.SearchConventionsByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ConventionQueryController - Convention 조회 API
 *
 * <p>코딩 컨벤션 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/conventions).
 *
 * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 */
@Tag(name = "Convention", description = "코딩 컨벤션 조회 API")
@RestController
@RequestMapping(ConventionApiEndpoints.CONVENTIONS)
public class ConventionQueryController {

    private final SearchConventionsByCursorUseCase searchConventionsByCursorUseCase;
    private final ConventionQueryApiMapper mapper;

    /**
     * ConventionQueryController 생성자
     *
     * @param searchConventionsByCursorUseCase Convention 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public ConventionQueryController(
            SearchConventionsByCursorUseCase searchConventionsByCursorUseCase,
            ConventionQueryApiMapper mapper) {
        this.searchConventionsByCursorUseCase = searchConventionsByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * Convention 복합 조건 조회 API (커서 기반)
     *
     * <p>Convention 목록을 커서 기반으로 조회합니다. 모듈 ID 필터링을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, moduleIds 필터 포함)
     * @return Convention 슬라이스 목록
     */
    @Operation(
            summary = "Convention 복합 조건 조회",
            description = "Convention 목록을 커서 기반으로 조회합니다. 모듈 ID 필터링을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ConventionApiResponse>>>
            searchConventionsByCursor(@Valid SearchConventionsCursorApiRequest request) {

        ConventionSearchParams searchParams = mapper.toSearchParams(request);
        ConventionSliceResult sliceResult = searchConventionsByCursorUseCase.execute(searchParams);
        SliceApiResponse<ConventionApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
