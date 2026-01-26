package com.ryuqq.adapter.in.rest.packagepurpose.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.packagepurpose.PackagePurposeApiEndpoints;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.SearchPackagePurposesCursorApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.response.PackagePurposeApiResponse;
import com.ryuqq.adapter.in.rest.packagepurpose.mapper.PackagePurposeQueryApiMapper;
import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import com.ryuqq.application.packagepurpose.port.in.SearchPackagePurposesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PackagePurposeQueryController - PackagePurpose 조회 API
 *
 * <p>패키지 목적 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/package-purposes).
 *
 * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "PackagePurpose", description = "패키지 목적 조회 API")
@RestController
@RequestMapping(PackagePurposeApiEndpoints.BASE)
public class PackagePurposeQueryController {

    private final SearchPackagePurposesByCursorUseCase searchPackagePurposesByCursorUseCase;
    private final PackagePurposeQueryApiMapper mapper;

    /**
     * PackagePurposeQueryController 생성자
     *
     * @param searchPackagePurposesByCursorUseCase PackagePurpose 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public PackagePurposeQueryController(
            SearchPackagePurposesByCursorUseCase searchPackagePurposesByCursorUseCase,
            PackagePurposeQueryApiMapper mapper) {
        this.searchPackagePurposesByCursorUseCase = searchPackagePurposesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * PackagePurpose 복합 조건 조회 API (커서 기반)
     *
     * <p>PackagePurpose 목록을 커서 기반으로 조회합니다. 패키지 구조 ID(복수) 필터링과 검색(필드/키워드)을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, structureIds, searchField/searchWord 필터 포함)
     * @return PackagePurpose 슬라이스 목록
     */
    @Operation(
            summary = "PackagePurpose 복합 조건 조회",
            description = "PackagePurpose 목록을 커서 기반으로 조회합니다. 패키지 구조 ID(복수) 필터링과 검색(필드/키워드)을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<PackagePurposeApiResponse>>>
            searchPackagePurposesByCursor(@Valid SearchPackagePurposesCursorApiRequest request) {

        PackagePurposeSearchParams searchParams = mapper.toSearchParams(request);
        PackagePurposeSliceResult sliceResult =
                searchPackagePurposesByCursorUseCase.execute(searchParams);
        SliceApiResponse<PackagePurposeApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
