package com.ryuqq.adapter.in.rest.codingrule.controller.query;

import com.ryuqq.adapter.in.rest.codingrule.CodingRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.codingrule.dto.request.CodingRuleIndexApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.request.SearchCodingRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleApiResponse;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleIndexApiResponse;
import com.ryuqq.adapter.in.rest.codingrule.mapper.CodingRuleQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.application.codingrule.dto.query.CodingRuleIndexSearchParams;
import com.ryuqq.application.codingrule.dto.query.CodingRuleSearchParams;
import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.application.codingrule.port.in.ListCodingRuleIndexUseCase;
import com.ryuqq.application.codingrule.port.in.SearchCodingRulesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CodingRuleQueryController - CodingRule 조회 API
 *
 * <p>코딩 규칙 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/coding-rules).
 *
 * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "CodingRule", description = "코딩 규칙 조회 API")
@RestController
@RequestMapping(CodingRuleApiEndpoints.BASE)
public class CodingRuleQueryController {

    private final SearchCodingRulesByCursorUseCase searchCodingRulesByCursorUseCase;
    private final ListCodingRuleIndexUseCase listCodingRuleIndexUseCase;
    private final CodingRuleQueryApiMapper mapper;

    /**
     * CodingRuleQueryController 생성자
     *
     * @param searchCodingRulesByCursorUseCase CodingRule 복합 조건 조회 UseCase (커서 기반)
     * @param listCodingRuleIndexUseCase CodingRule 인덱스 조회 UseCase
     * @param mapper API 매퍼
     */
    public CodingRuleQueryController(
            SearchCodingRulesByCursorUseCase searchCodingRulesByCursorUseCase,
            ListCodingRuleIndexUseCase listCodingRuleIndexUseCase,
            CodingRuleQueryApiMapper mapper) {
        this.searchCodingRulesByCursorUseCase = searchCodingRulesByCursorUseCase;
        this.listCodingRuleIndexUseCase = listCodingRuleIndexUseCase;
        this.mapper = mapper;
    }

    /**
     * CodingRule 복합 조건 조회 API (커서 기반)
     *
     * <p>CodingRule 목록을 커서 기반으로 조회합니다. 카테고리, 심각도 필터링 및 필드별 검색을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, categories, severities, searchField, searchWord 필터 포함)
     * @return CodingRule 슬라이스 목록
     */
    @Operation(
            summary = "CodingRule 복합 조건 조회",
            description = "CodingRule 목록을 커서 기반으로 조회합니다. 카테고리, 심각도 필터링 및 필드별 검색을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<CodingRuleApiResponse>>>
            searchCodingRulesByCursor(@Valid SearchCodingRulesCursorApiRequest request) {

        CodingRuleSearchParams searchParams = mapper.toSearchParams(request);
        CodingRuleSliceResult sliceResult = searchCodingRulesByCursorUseCase.execute(searchParams);
        SliceApiResponse<CodingRuleApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * CodingRule 인덱스 조회 API
     *
     * <p>규칙 인덱스(code, name, severity, category)만 조회합니다.
     *
     * <p>상세 정보는 get_rule(code) API로 개별 조회합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (conventionId, severities, categories 필터 포함)
     * @return CodingRule 인덱스 목록
     */
    @Operation(
            summary = "CodingRule 인덱스 조회",
            description = "규칙 인덱스(code, name, severity, category)만 조회합니다. 캐싱 효율성을 위해 경량화된 응답입니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping(CodingRuleApiEndpoints.INDEX)
    public ResponseEntity<ApiResponse<List<CodingRuleIndexApiResponse>>> listCodingRuleIndex(
            @Valid CodingRuleIndexApiRequest request) {

        CodingRuleIndexSearchParams searchParams = mapper.toIndexSearchParams(request);
        List<CodingRuleIndexItem> items = listCodingRuleIndexUseCase.execute(searchParams);
        List<CodingRuleIndexApiResponse> response = mapper.toIndexResponses(items);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
