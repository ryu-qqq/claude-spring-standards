package com.ryuqq.adapter.in.rest.module.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.module.ModuleApiEndpoints;
import com.ryuqq.adapter.in.rest.module.dto.request.SearchModulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.module.dto.response.ModuleApiResponse;
import com.ryuqq.adapter.in.rest.module.dto.response.ModuleTreeApiResponse;
import com.ryuqq.adapter.in.rest.module.mapper.ModuleQueryApiMapper;
import com.ryuqq.application.module.dto.response.ModuleSliceResult;
import com.ryuqq.application.module.dto.response.ModuleTreeResult;
import com.ryuqq.application.module.port.in.GetModuleTreeUseCase;
import com.ryuqq.application.module.port.in.SearchModulesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ModuleQueryController - Module 조회 API
 *
 * <p>모듈 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/modules).
 *
 * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "Module", description = "모듈 조회 API")
@RestController
@RequestMapping(ModuleApiEndpoints.MODULES)
public class ModuleQueryController {

    private final SearchModulesByCursorUseCase searchModulesByCursorUseCase;
    private final GetModuleTreeUseCase getModuleTreeUseCase;
    private final ModuleQueryApiMapper mapper;

    /**
     * ModuleQueryController 생성자
     *
     * @param searchModulesByCursorUseCase Module 복합 조건 조회 UseCase (커서 기반)
     * @param getModuleTreeUseCase 모듈 트리 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public ModuleQueryController(
            SearchModulesByCursorUseCase searchModulesByCursorUseCase,
            GetModuleTreeUseCase getModuleTreeUseCase,
            ModuleQueryApiMapper mapper) {
        this.searchModulesByCursorUseCase = searchModulesByCursorUseCase;
        this.getModuleTreeUseCase = getModuleTreeUseCase;
        this.mapper = mapper;
    }

    /**
     * Module 복합 조건 조회 API (커서 기반)
     *
     * <p>Module 목록을 커서 기반으로 조회합니다. layerIds로 필터링 가능합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, layerIds 필터 포함)
     * @return Module 슬라이스 목록
     */
    @Operation(
            summary = "Module 복합 조건 조회",
            description = "Module 목록을 커서 기반으로 조회합니다. layerIds로 필터링 가능합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ModuleApiResponse>>> searchModulesByCursor(
            @Valid SearchModulesCursorApiRequest request) {

        com.ryuqq.application.module.dto.query.ModuleSearchParams searchParams =
                mapper.toSearchParams(request);
        ModuleSliceResult sliceResult = searchModulesByCursorUseCase.execute(searchParams);
        SliceApiResponse<ModuleApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * Module 트리 조회 (레이어별)
     *
     * <p>특정 레이어의 모든 모듈을 트리 구조로 조회합니다.
     *
     * <p>서버에서 트리 구조로 변환하여 반환하므로 클라이언트에서 바로 사용할 수 있습니다.
     *
     * @param layerId 레이어 ID
     * @return 해당 레이어의 Module 트리 구조 (루트 노드들, children 포함)
     */
    @Operation(
            summary = "Module 트리 조회",
            description = "특정 레이어의 모든 모듈을 트리 구조로 조회합니다. children 필드에 중첩된 자식 모듈이 포함됩니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Layer를 찾을 수 없음")
    })
    @GetMapping(ModuleApiEndpoints.TREE)
    public ResponseEntity<ApiResponse<List<ModuleTreeApiResponse>>> searchModulesAsTree(
            @Parameter(description = "Layer ID", required = true)
                    @RequestParam(ModuleApiEndpoints.PARAM_LAYER_ID)
                    Long layerId) {

        List<ModuleTreeResult> treeResults = getModuleTreeUseCase.execute(layerId);
        List<ModuleTreeApiResponse> responses = mapper.toTreeResponses(treeResults);

        return ResponseEntity.ok(ApiResponse.of(responses));
    }
}
