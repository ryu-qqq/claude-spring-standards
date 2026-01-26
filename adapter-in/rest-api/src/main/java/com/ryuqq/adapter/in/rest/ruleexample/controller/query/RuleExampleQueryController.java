package com.ryuqq.adapter.in.rest.ruleexample.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.ruleexample.RuleExampleApiEndpoints;
import com.ryuqq.adapter.in.rest.ruleexample.dto.request.SearchRuleExamplesCursorApiRequest;
import com.ryuqq.adapter.in.rest.ruleexample.dto.response.RuleExampleApiResponse;
import com.ryuqq.adapter.in.rest.ruleexample.mapper.RuleExampleQueryApiMapper;
import com.ryuqq.application.ruleexample.dto.query.RuleExampleSearchParams;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import com.ryuqq.application.ruleexample.port.in.SearchRuleExamplesByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RuleExampleQueryController - RuleExample 조회 API
 *
 * <p>규칙 예시 조회 엔드포인트를 제공합니다.
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
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/rule-examples).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "RuleExample", description = "규칙 예시 조회 API")
@RestController
@RequestMapping(RuleExampleApiEndpoints.RULE_EXAMPLES)
public class RuleExampleQueryController {

    private final SearchRuleExamplesByCursorUseCase searchRuleExamplesByCursorUseCase;
    private final RuleExampleQueryApiMapper mapper;

    /**
     * RuleExampleQueryController 생성자
     *
     * @param searchRuleExamplesByCursorUseCase RuleExample 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public RuleExampleQueryController(
            SearchRuleExamplesByCursorUseCase searchRuleExamplesByCursorUseCase,
            RuleExampleQueryApiMapper mapper) {
        this.searchRuleExamplesByCursorUseCase = searchRuleExamplesByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * RuleExample 복합 조건 조회 API (커서 기반)
     *
     * <p>RuleExample 목록을 커서 기반으로 조회합니다. 코딩 규칙 ID, 예시 타입, 언어 필터링을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, ruleIds, exampleTypes, languages 필터 포함)
     * @return RuleExample 슬라이스 목록
     */
    @Operation(
            summary = "RuleExample 복합 조건 조회",
            description =
                    "RuleExample 목록을 커서 기반으로 조회합니다. 코딩 규칙 ID(복수), 예시 타입(복수), 언어(복수) 필터링을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<RuleExampleApiResponse>>>
            searchRuleExamplesByCursor(@Valid SearchRuleExamplesCursorApiRequest request) {

        RuleExampleSearchParams searchParams = mapper.toSearchParams(request);
        RuleExampleSliceResult sliceResult =
                searchRuleExamplesByCursorUseCase.execute(searchParams);
        SliceApiResponse<RuleExampleApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
