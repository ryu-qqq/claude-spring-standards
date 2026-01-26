package com.ryuqq.adapter.in.rest.packagestructure.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.packagestructure.PackageStructureApiEndpoints;
import com.ryuqq.adapter.in.rest.packagestructure.dto.request.SearchPackageStructuresCursorApiRequest;
import com.ryuqq.adapter.in.rest.packagestructure.dto.response.PackageStructureApiResponse;
import com.ryuqq.adapter.in.rest.packagestructure.mapper.PackageStructureQueryApiMapper;
import com.ryuqq.application.packagestructure.dto.query.PackageStructureSearchParams;
import com.ryuqq.application.packagestructure.dto.response.PackageStructureSliceResult;
import com.ryuqq.application.packagestructure.port.in.SearchPackageStructuresByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PackageStructureQueryController - PackageStructure 조회 API
 *
 * <p>패키지 구조 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/package-structures).
 *
 * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "PackageStructure", description = "패키지 구조 조회 API")
@RestController
@RequestMapping(PackageStructureApiEndpoints.PACKAGE_STRUCTURES)
public class PackageStructureQueryController {

    private final SearchPackageStructuresByCursorUseCase searchPackageStructuresByCursorUseCase;
    private final PackageStructureQueryApiMapper mapper;

    /**
     * PackageStructureQueryController 생성자
     *
     * @param searchPackageStructuresByCursorUseCase PackageStructure 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public PackageStructureQueryController(
            SearchPackageStructuresByCursorUseCase searchPackageStructuresByCursorUseCase,
            PackageStructureQueryApiMapper mapper) {
        this.searchPackageStructuresByCursorUseCase = searchPackageStructuresByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * PackageStructure 복합 조건 조회 API (커서 기반)
     *
     * <p>PackageStructure 목록을 커서 기반으로 조회합니다. 모듈 ID 필터링을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, moduleIds 필터 포함)
     * @return PackageStructure 슬라이스 목록
     */
    @Operation(
            summary = "PackageStructure 복합 조건 조회",
            description = "PackageStructure 목록을 커서 기반으로 조회합니다. 모듈 ID 필터링을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<PackageStructureApiResponse>>>
            searchPackageStructuresByCursor(
                    @Valid SearchPackageStructuresCursorApiRequest request) {

        PackageStructureSearchParams searchParams = mapper.toSearchParams(request);
        PackageStructureSliceResult sliceResult =
                searchPackageStructuresByCursorUseCase.execute(searchParams);
        SliceApiResponse<PackageStructureApiResponse> response =
                mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
