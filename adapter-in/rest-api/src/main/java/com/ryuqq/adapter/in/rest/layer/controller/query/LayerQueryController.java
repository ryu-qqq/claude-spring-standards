package com.ryuqq.adapter.in.rest.layer.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.layer.LayerApiEndpoints;
import com.ryuqq.adapter.in.rest.layer.dto.request.SearchLayersCursorApiRequest;
import com.ryuqq.adapter.in.rest.layer.dto.response.LayerApiResponse;
import com.ryuqq.adapter.in.rest.layer.mapper.LayerQueryApiMapper;
import com.ryuqq.application.layer.dto.query.LayerSearchParams;
import com.ryuqq.application.layer.dto.response.LayerSliceResult;
import com.ryuqq.application.layer.port.in.SearchLayersByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LayerQueryController - Layer 조회 API
 *
 * <p>레이어 조회 엔드포인트를 제공합니다.
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
@Tag(name = "Layer", description = "레이어 관리 API")
@RestController
@RequestMapping(LayerApiEndpoints.LAYERS)
public class LayerQueryController {

    private final SearchLayersByCursorUseCase searchLayersByCursorUseCase;
    private final LayerQueryApiMapper mapper;

    /**
     * LayerQueryController 생성자
     *
     * @param searchLayersByCursorUseCase Layer 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public LayerQueryController(
            SearchLayersByCursorUseCase searchLayersByCursorUseCase, LayerQueryApiMapper mapper) {
        this.searchLayersByCursorUseCase = searchLayersByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * Layer 복합 조건 조회 API (커서 기반)
     *
     * <p>레이어 목록을 커서 기반으로 조회합니다. Architecture ID 필터링 및 필드별 검색을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, architectureIds, searchField, searchWord 필터 포함)
     * @return Layer 슬라이스 목록
     */
    @Operation(
            summary = "Layer 복합 조건 조회",
            description = "레이어 목록을 커서 기반으로 조회합니다. Architecture ID 필터링 및 필드별 검색을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<LayerApiResponse>>> searchLayersByCursor(
            @Valid SearchLayersCursorApiRequest request) {

        LayerSearchParams searchParams = mapper.toSearchParams(request);
        LayerSliceResult sliceResult = searchLayersByCursorUseCase.execute(searchParams);
        SliceApiResponse<LayerApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
