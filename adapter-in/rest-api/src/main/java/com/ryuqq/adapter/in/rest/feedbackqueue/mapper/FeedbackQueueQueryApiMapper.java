package com.ryuqq.adapter.in.rest.feedbackqueue.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.request.SearchFeedbacksCursorApiRequest;
import com.ryuqq.adapter.in.rest.feedbackqueue.dto.response.FeedbackQueueApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.feedbackqueue.dto.query.FeedbackQueueSearchParams;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueResult;
import com.ryuqq.application.feedbackqueue.dto.response.FeedbackQueueSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * FeedbackQueueQueryApiMapper - FeedbackQueue Query API 변환 매퍼
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>MAP-001: Mapper는 @Component로 등록.
 *
 * <p>MAP-003: Application Result -> API Response 변환.
 *
 * <p>MAP-004: Mapper는 필드 매핑만 수행.
 *
 * <p>MAP-006: Mapper에서 Domain 객체 직접 사용 금지.
 *
 * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
 *
 * <p>CQRS 분리: Query 전용 Mapper (CommandApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class FeedbackQueueQueryApiMapper {

    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchFeedbacksCursorApiRequest -> FeedbackQueueSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 -> Mapper에서 변환 처리.
     *
     * <p>DTO-015: Request DTO Compact Constructor 기본값 설정 금지 -> Mapper에서 처리.
     *
     * @param request 조회 요청 DTO
     * @return FeedbackQueueSearchParams 객체
     */
    public FeedbackQueueSearchParams toSearchParams(SearchFeedbacksCursorApiRequest request) {
        int size = request.size() != null && request.size() > 0 ? request.size() : DEFAULT_SIZE;
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), size);
        return FeedbackQueueSearchParams.of(
                cursorParams,
                request.statuses(),
                request.targetTypes(),
                request.feedbackTypes(),
                request.riskLevels(),
                request.actions());
    }

    /**
     * 단일 FeedbackQueueResult -> FeedbackQueueApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result FeedbackQueueResult
     * @return FeedbackQueueApiResponse
     */
    public FeedbackQueueApiResponse toResponse(FeedbackQueueResult result) {
        return new FeedbackQueueApiResponse(
                result.id(),
                result.targetType(),
                result.targetId(),
                result.feedbackType(),
                result.riskLevel(),
                result.payload(),
                result.status(),
                result.reviewNotes(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * FeedbackQueueResult 목록 -> FeedbackQueueApiResponse 목록 변환
     *
     * @param results FeedbackQueueResult 목록
     * @return FeedbackQueueApiResponse 목록
     */
    public List<FeedbackQueueApiResponse> toResponses(List<FeedbackQueueResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * FeedbackQueueSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<FeedbackQueueApiResponse> toSliceResponse(
            FeedbackQueueSliceResult sliceResult) {
        List<FeedbackQueueApiResponse> responses = toResponses(sliceResult.content());
        String nextCursor = null;
        if (sliceResult.hasNext() && !sliceResult.content().isEmpty()) {
            Long lastId = sliceResult.content().get(sliceResult.content().size() - 1).id();
            nextCursor = lastId.toString();
        }
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }
}
