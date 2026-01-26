package com.ryuqq.adapter.in.rest.layerdependency.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.layerdependency.LayerDependencyRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.SearchLayerDependencyRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.response.LayerDependencyRuleApiResponse;
import com.ryuqq.adapter.in.rest.layerdependency.mapper.LayerDependencyRuleQueryApiMapper;
import com.ryuqq.application.layerdependency.dto.query.LayerDependencyRuleSearchParams;
import com.ryuqq.application.layerdependency.port.in.SearchLayerDependencyRulesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LayerDependencyRuleQueryController - LayerDependencyRule 조회 API
 *
 * <p>레이어 의존성 규칙 조회 엔드포인트를 제공합니다. 커서 기반 페이징과 복합 조건 필터링을 지원합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-002: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * <p>CTR-009: Controller에서 Lombok 사용 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "LayerDependencyRule", description = "레이어 의존성 규칙 조회 API")
@RestController
@RequestMapping(LayerDependencyRuleApiEndpoints.QUERY_BASE)
public class LayerDependencyRuleQueryController {

    private final SearchLayerDependencyRulesByCursorUseCase
            searchLayerDependencyRulesByCursorUseCase;
    private final LayerDependencyRuleQueryApiMapper mapper;

    /**
     * LayerDependencyRuleQueryController 생성자
     *
     * @param searchLayerDependencyRulesByCursorUseCase 복합 조건 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public LayerDependencyRuleQueryController(
            SearchLayerDependencyRulesByCursorUseCase searchLayerDependencyRulesByCursorUseCase,
            LayerDependencyRuleQueryApiMapper mapper) {
        this.searchLayerDependencyRulesByCursorUseCase = searchLayerDependencyRulesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * LayerDependencyRule 복합 조건 조회 (커서 기반)
     *
     * <p>레이어 의존성 규칙 목록을 커서 기반으로 조회합니다. 아키텍처 ID(복수), 의존성 타입(복수), 검색(필드/키워드) 필터링을 지원합니다.
     *
     * @param request 조회 요청 DTO
     * @return 레이어 의존성 규칙 슬라이스 응답
     */
    @Operation(
            summary = "LayerDependencyRule 복합 조건 조회 (커서 기반)",
            description =
                    "레이어 의존성 규칙 목록을 커서 기반으로 조회합니다. 아키텍처 ID(복수), 의존성 타입(복수), 검색(필드/키워드) 필터링을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<LayerDependencyRuleApiResponse>>>
            searchLayerDependencyRulesByCursor(
                    @Valid SearchLayerDependencyRulesCursorApiRequest request) {

        LayerDependencyRuleSearchParams searchParams = mapper.toSearchParams(request);
        var sliceResult = searchLayerDependencyRulesByCursorUseCase.execute(searchParams);
        SliceApiResponse<LayerDependencyRuleApiResponse> response =
                mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
