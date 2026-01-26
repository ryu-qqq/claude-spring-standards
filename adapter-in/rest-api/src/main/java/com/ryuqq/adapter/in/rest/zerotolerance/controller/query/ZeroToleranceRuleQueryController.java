package com.ryuqq.adapter.in.rest.zerotolerance.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.zerotolerance.ZeroToleranceRuleApiEndpoints;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.request.SearchZeroToleranceRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.zerotolerance.dto.response.ZeroToleranceRuleSliceApiResponse;
import com.ryuqq.adapter.in.rest.zerotolerance.mapper.ZeroToleranceRuleQueryApiMapper;
import com.ryuqq.application.zerotolerance.dto.query.ZeroToleranceRuleSearchParams;
import com.ryuqq.application.zerotolerance.dto.response.ZeroToleranceRuleSliceResult;
import com.ryuqq.application.zerotolerance.port.in.SearchZeroToleranceRulesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ZeroToleranceRuleQueryController - ZeroToleranceRule 조회 API
 *
 * <p>Zero-Tolerance 규칙 상세 조회 엔드포인트를 제공합니다. 각 규칙에 대해 RuleExample과 ChecklistItem을 함께 반환합니다.
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
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/zero-tolerance-rules).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "ZeroToleranceRule", description = "Zero-Tolerance 규칙 상세 조회 API")
@RestController
@RequestMapping(ZeroToleranceRuleApiEndpoints.BASE)
public class ZeroToleranceRuleQueryController {

    private final SearchZeroToleranceRulesByCursorUseCase searchZeroToleranceRulesByCursorUseCase;
    private final ZeroToleranceRuleQueryApiMapper mapper;

    /**
     * ZeroToleranceRuleQueryController 생성자
     *
     * @param searchZeroToleranceRulesByCursorUseCase 복합 조건 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public ZeroToleranceRuleQueryController(
            SearchZeroToleranceRulesByCursorUseCase searchZeroToleranceRulesByCursorUseCase,
            ZeroToleranceRuleQueryApiMapper mapper) {
        this.searchZeroToleranceRulesByCursorUseCase = searchZeroToleranceRulesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * Zero-Tolerance 규칙 복합 조건 조회 API (커서 기반)
     *
     * <p>Zero-Tolerance 규칙 목록을 커서 기반으로 조회합니다. 컨벤션 ID(복수), 탐지 방식(복수), 검색(필드/키워드), PR 자동 거부 여부 필터링을
     * 지원합니다.
     *
     * @param request 조회 요청 DTO
     * @return Zero-Tolerance 규칙 슬라이스 목록
     */
    @Operation(
            summary = "Zero-Tolerance 규칙 복합 조건 조회",
            description =
                    "Zero-Tolerance 규칙 목록을 커서 기반으로 조회합니다. 컨벤션 ID(복수), 탐지 방식(복수), 검색(필드/키워드), PR 자동"
                            + " 거부 여부 필터링을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ZeroToleranceRuleSliceApiResponse>>
            searchZeroToleranceRulesByCursor(
                    @Valid SearchZeroToleranceRulesCursorApiRequest request) {

        ZeroToleranceRuleSearchParams searchParams = mapper.toSearchParams(request);
        ZeroToleranceRuleSliceResult sliceResult =
                searchZeroToleranceRulesByCursorUseCase.execute(searchParams);
        ZeroToleranceRuleSliceApiResponse response = mapper.toSliceApiResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
