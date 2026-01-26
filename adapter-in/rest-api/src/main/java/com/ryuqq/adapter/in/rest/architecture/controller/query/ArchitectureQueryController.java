package com.ryuqq.adapter.in.rest.architecture.controller.query;

import com.ryuqq.adapter.in.rest.architecture.ArchitectureApiEndpoints;
import com.ryuqq.adapter.in.rest.architecture.dto.request.SearchArchitecturesCursorApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.response.ArchitectureApiResponse;
import com.ryuqq.adapter.in.rest.architecture.mapper.ArchitectureQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.application.architecture.dto.query.ArchitectureSearchParams;
import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import com.ryuqq.application.architecture.port.in.SearchArchitecturesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ArchitectureQueryController - Architecture 조회 API
 *
 * <p>아키텍처 조회 엔드포인트를 제공합니다.
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
@Tag(name = "Architecture", description = "아키텍처 관리 API")
@RestController
@RequestMapping(ArchitectureApiEndpoints.ARCHITECTURES)
public class ArchitectureQueryController {

    private final SearchArchitecturesByCursorUseCase searchArchitecturesByCursorUseCase;
    private final ArchitectureQueryApiMapper mapper;

    /**
     * ArchitectureQueryController 생성자
     *
     * @param searchArchitecturesByCursorUseCase Architecture 복합 조건 조회 UseCase
     * @param mapper API 매퍼
     */
    public ArchitectureQueryController(
            SearchArchitecturesByCursorUseCase searchArchitecturesByCursorUseCase,
            ArchitectureQueryApiMapper mapper) {
        this.searchArchitecturesByCursorUseCase = searchArchitecturesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * Architecture 복합 조건 조회 API (커서 기반)
     *
     * <p>TechStack ID 필터를 지원하여 아키텍처 목록을 커서 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (커서 기반, techStackIds 필터 포함)
     * @return Architecture 슬라이스 목록
     */
    @Operation(
            summary = "Architecture 복합 조건 조회",
            description = "TechStack ID 필터를 지원하여 아키텍처 목록을 커서 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ArchitectureApiResponse>>>
            searchArchitecturesByCursor(@Valid SearchArchitecturesCursorApiRequest request) {

        ArchitectureSearchParams searchParams = mapper.toSearchParams(request);
        ArchitectureSliceResult sliceResult =
                searchArchitecturesByCursorUseCase.execute(searchParams);
        SliceApiResponse<ArchitectureApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
