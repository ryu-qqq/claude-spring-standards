package com.ryuqq.adapter.in.rest.resourcetemplate.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.resourcetemplate.ResourceTemplateApiEndpoints;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.SearchResourceTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.response.ResourceTemplateApiResponse;
import com.ryuqq.adapter.in.rest.resourcetemplate.mapper.ResourceTemplateQueryApiMapper;
import com.ryuqq.application.resourcetemplate.dto.query.ResourceTemplateSearchParams;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import com.ryuqq.application.resourcetemplate.port.in.SearchResourceTemplatesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ResourceTemplateQueryController - ResourceTemplate 조회 API
 *
 * <p>리소스 템플릿 조회 엔드포인트를 제공합니다.
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
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/resource-templates).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "ResourceTemplate", description = "리소스 템플릿 조회 API")
@RestController
@RequestMapping(ResourceTemplateApiEndpoints.RESOURCE_TEMPLATES)
public class ResourceTemplateQueryController {

    private final SearchResourceTemplatesByCursorUseCase searchResourceTemplatesByCursorUseCase;
    private final ResourceTemplateQueryApiMapper mapper;

    /**
     * ResourceTemplateQueryController 생성자
     *
     * @param getResourceTemplateUseCase 단건 조회 UseCase
     * @param getAllResourceTemplatesUseCase 전체 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public ResourceTemplateQueryController(
            SearchResourceTemplatesByCursorUseCase searchResourceTemplatesByCursorUseCase,
            ResourceTemplateQueryApiMapper mapper) {
        this.searchResourceTemplatesByCursorUseCase = searchResourceTemplatesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * ResourceTemplate 복합 조건 조회 API (커서 기반)
     *
     * <p>ResourceTemplate 목록을 커서 기반으로 조회합니다. 모듈 ID(복수), 카테고리(복수), 파일 타입(복수) 필터링을 지원합니다.
     *
     * @param request 조회 요청 DTO
     * @return 리소스 템플릿 슬라이스 목록
     */
    @Operation(
            summary = "ResourceTemplate 복합 조건 조회",
            description =
                    "ResourceTemplate 목록을 커서 기반으로 조회합니다. 모듈 ID(복수), 카테고리(복수), 파일 타입(복수) 필터링을"
                            + " 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ResourceTemplateApiResponse>>>
            searchResourceTemplatesByCursor(
                    @Valid SearchResourceTemplatesCursorApiRequest request) {

        ResourceTemplateSearchParams searchParams = mapper.toSearchParams(request);
        ResourceTemplateSliceResult sliceResult =
                searchResourceTemplatesByCursorUseCase.execute(searchParams);
        SliceApiResponse<ResourceTemplateApiResponse> response =
                mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
