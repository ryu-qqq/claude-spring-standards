package com.ryuqq.adapter.in.rest.classtypecategory.controller.query;

import com.ryuqq.adapter.in.rest.classtypecategory.ClassTypeCategoryApiEndpoints;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.SearchClassTypeCategoriesCursorApiRequest;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.response.ClassTypeCategoryApiResponse;
import com.ryuqq.adapter.in.rest.classtypecategory.mapper.ClassTypeCategoryQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.application.classtypecategory.dto.query.ClassTypeCategorySearchParams;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategorySliceResult;
import com.ryuqq.application.classtypecategory.port.in.SearchClassTypeCategoriesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassTypeCategoryQueryController - ClassTypeCategory 조회 API
 *
 * <p>클래스 타입 카테고리 조회 엔드포인트를 제공합니다.
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
@Tag(name = "ClassTypeCategory", description = "클래스 타입 카테고리 관리 API")
@RestController
@RequestMapping(ClassTypeCategoryApiEndpoints.CLASS_TYPE_CATEGORIES)
public class ClassTypeCategoryQueryController {

    private final SearchClassTypeCategoriesByCursorUseCase searchClassTypeCategoriesByCursorUseCase;
    private final ClassTypeCategoryQueryApiMapper mapper;

    /**
     * ClassTypeCategoryQueryController 생성자
     *
     * @param searchClassTypeCategoriesByCursorUseCase ClassTypeCategory 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public ClassTypeCategoryQueryController(
            SearchClassTypeCategoriesByCursorUseCase searchClassTypeCategoriesByCursorUseCase,
            ClassTypeCategoryQueryApiMapper mapper) {
        this.searchClassTypeCategoriesByCursorUseCase = searchClassTypeCategoriesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * ClassTypeCategory 복합 조건 조회 API (커서 기반)
     *
     * <p>클래스 타입 카테고리 목록을 커서 기반으로 조회합니다. Architecture ID 필터링 및 필드별 검색을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO
     * @return ClassTypeCategory 슬라이스 목록
     */
    @Operation(
            summary = "ClassTypeCategory 복합 조건 조회",
            description =
                    "클래스 타입 카테고리 목록을 커서 기반으로 조회합니다. " + "Architecture ID 필터링 및 필드별 검색을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ClassTypeCategoryApiResponse>>>
            searchClassTypeCategoriesByCursor(
                    @Valid SearchClassTypeCategoriesCursorApiRequest request) {

        ClassTypeCategorySearchParams searchParams = mapper.toSearchParams(request);
        ClassTypeCategorySliceResult sliceResult =
                searchClassTypeCategoriesByCursorUseCase.execute(searchParams);
        SliceApiResponse<ClassTypeCategoryApiResponse> response =
                mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
