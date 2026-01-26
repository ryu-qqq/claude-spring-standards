package com.ryuqq.adapter.in.rest.checklistitem.controller.query;

import com.ryuqq.adapter.in.rest.checklistitem.ChecklistItemApiEndpoints;
import com.ryuqq.adapter.in.rest.checklistitem.dto.request.SearchChecklistItemsCursorApiRequest;
import com.ryuqq.adapter.in.rest.checklistitem.dto.response.ChecklistItemApiResponse;
import com.ryuqq.adapter.in.rest.checklistitem.mapper.ChecklistItemQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.application.checklistitem.dto.query.ChecklistItemSearchParams;
import com.ryuqq.application.checklistitem.dto.response.ChecklistItemSliceResult;
import com.ryuqq.application.checklistitem.port.in.SearchChecklistItemsByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ChecklistItemQueryController - ChecklistItem 조회 API
 *
 * <p>체크리스트 항목 조회 엔드포인트를 제공합니다.
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
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/checklist-items).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "ChecklistItem", description = "체크리스트 항목 조회 API")
@RestController
@RequestMapping(ChecklistItemApiEndpoints.CHECKLIST_ITEMS)
public class ChecklistItemQueryController {

    private final SearchChecklistItemsByCursorUseCase searchChecklistItemsByCursorUseCase;
    private final ChecklistItemQueryApiMapper mapper;

    /**
     * ChecklistItemQueryController 생성자
     *
     * @param searchChecklistItemsByCursorUseCase ChecklistItem 복합 조건 조회 UseCase (커서 기반)
     * @param mapper API 매퍼
     */
    public ChecklistItemQueryController(
            SearchChecklistItemsByCursorUseCase searchChecklistItemsByCursorUseCase,
            ChecklistItemQueryApiMapper mapper) {
        this.searchChecklistItemsByCursorUseCase = searchChecklistItemsByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * ChecklistItem 복합 조건 조회 API (커서 기반)
     *
     * <p>ChecklistItem 목록을 커서 기반으로 조회합니다. 코딩 규칙 ID(복수), 체크 타입(복수), 자동화 도구(복수) 필터링을 지원합니다.
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (커서 기반, ruleIds, checkTypes, automationTools 필터 포함)
     * @return ChecklistItem 슬라이스 목록
     */
    @Operation(
            summary = "ChecklistItem 복합 조건 조회",
            description =
                    "ChecklistItem 목록을 커서 기반으로 조회합니다. 코딩 규칙 ID(복수), 체크 타입(복수), 자동화 도구(복수) 필터링을"
                            + " 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ChecklistItemApiResponse>>>
            searchChecklistItemsByCursor(@Valid SearchChecklistItemsCursorApiRequest request) {

        ChecklistItemSearchParams searchParams = mapper.toSearchParams(request);
        ChecklistItemSliceResult sliceResult =
                searchChecklistItemsByCursorUseCase.execute(searchParams);
        SliceApiResponse<ChecklistItemApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
