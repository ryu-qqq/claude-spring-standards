package com.ryuqq.adapter.in.rest.techstack.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.techstack.TechStackApiEndpoints;
import com.ryuqq.adapter.in.rest.techstack.dto.request.SearchTechStacksCursorApiRequest;
import com.ryuqq.adapter.in.rest.techstack.dto.response.TechStackApiResponse;
import com.ryuqq.adapter.in.rest.techstack.mapper.TechStackQueryApiMapper;
import com.ryuqq.application.techstack.dto.query.TechStackSearchParams;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import com.ryuqq.application.techstack.port.in.SearchTechStacksByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TechStackQueryController - TechStack 조회 API
 *
 * <p>기술 스택 조회 엔드포인트를 제공합니다.
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
@Tag(name = "TechStack", description = "기술 스택 관리 API")
@RestController
@RequestMapping(TechStackApiEndpoints.TECH_STACKS)
public class TechStackQueryController {

    private final SearchTechStacksByCursorUseCase searchTechStacksByCursorUseCase;
    private final TechStackQueryApiMapper mapper;

    /**
     * TechStackQueryController 생성자
     *
     * @param searchTechStacksByCursorUseCase TechStack 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public TechStackQueryController(
            SearchTechStacksByCursorUseCase searchTechStacksByCursorUseCase,
            TechStackQueryApiMapper mapper) {
        this.searchTechStacksByCursorUseCase = searchTechStacksByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * TechStack 복합 조건 조회 API (커서 기반)
     *
     * <p>기술 스택 목록을 커서 기반으로 조회합니다. 상태 및 플랫폼 타입으로 필터링할 수 있습니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (status, platformTypes 필터 포함)
     * @return TechStack 슬라이스 목록
     */
    @Operation(
            summary = "TechStack 복합 조건 조회",
            description = "기술 스택 목록을 커서 기반으로 조회합니다. 상태 및 플랫폼 타입으로 필터링할 수 있습니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<TechStackApiResponse>>>
            searchTechStacksByCursor(@Valid SearchTechStacksCursorApiRequest request) {

        TechStackSearchParams searchParams = mapper.toSearchParams(request);
        TechStackSliceResult sliceResult = searchTechStacksByCursorUseCase.execute(searchParams);
        SliceApiResponse<TechStackApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
