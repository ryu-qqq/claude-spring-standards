package com.ryuqq.adapter.in.rest.feedbackqueue.controller.query;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.FeedbackQueueApiEndpoints;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.SearchFeedbacksCursorApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueApiResponse;
import com.ryuqq.adapter.in.rest.feedbackqueue.mapper.FeedbackQueueQueryApiMapper;
import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import com.ryuqq.application.feedbackqueue.port.in.SearchFeedbacksByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FeedbackQueueQueryController - FeedbackQueue 조회 API
 *
 * <p>피드백 큐 조회 엔드포인트를 제공합니다.
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
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/feedback-queue).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "FeedbackQueue", description = "피드백 큐 조회 API")
@RestController
@RequestMapping(FeedbackQueueApiEndpoints.BASE)
public class FeedbackQueueQueryController {

    private final SearchFeedbacksByCursorUseCase searchFeedbacksByCursorUseCase;
    private final FeedbackQueueQueryApiMapper mapper;

    /**
     * FeedbackQueueQueryController 생성자
     *
     * @param searchFeedbacksByCursorUseCase 복합 조건 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public FeedbackQueueQueryController(
            SearchFeedbacksByCursorUseCase searchFeedbacksByCursorUseCase,
            FeedbackQueueQueryApiMapper mapper) {
        this.searchFeedbacksByCursorUseCase = searchFeedbacksByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * FeedbackQueue 복합 조건 조회 (커서 기반 페이징)
     *
     * <p>피드백 큐 목록을 커서 기반으로 조회합니다. 상태/대상 타입/피드백 타입/리스크/액션 필터(복수)를 지원합니다.
     *
     * @param request 조회 요청 DTO
     * @return 피드백 큐 슬라이스 목록
     */
    @Operation(
            summary = "피드백 복합 조건 조회 (커서 기반)",
            description = "피드백 큐 목록을 커서 기반으로 조회합니다. 상태/대상 타입/피드백 타입/리스크/액션 필터(복수)를 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<FeedbackQueueApiResponse>>>
            searchFeedbacksByCursor(@Valid SearchFeedbacksCursorApiRequest request) {

        FeedbackQueueSearchParams searchParams = mapper.toSearchParams(request);
        FeedbackQueueSliceResult sliceResult = searchFeedbacksByCursorUseCase.execute(searchParams);
        SliceApiResponse<FeedbackQueueApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
