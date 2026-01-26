package com.ryuqq.adapter.in.rest.convention.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.convention.dto.request.SearchConventionsCursorApiRequest;
import com.ryuqq.adapter.in.rest.convention.dto.response.ConventionApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.convention.dto.query.ConventionSearchParams;
import com.ryuqq.application.convention.dto.response.ConventionResult;
import com.ryuqq.application.convention.dto.response.ConventionSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ConventionQueryApiMapper - Convention Query API 변환 매퍼
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-003: Application Result -> API Response 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * <p>MAPPER-005: DateTimeFormatUtils 사용하여 날짜 포맷 변환.
 *
 * <p>CQRS 분리: Query 전용 Mapper (CommandApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConventionQueryApiMapper {

    /**
     * SearchConventionsCursorApiRequest -> ConventionSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CommonCursorParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return ConventionSearchParams 객체
     */
    public ConventionSearchParams toSearchParams(SearchConventionsCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return ConventionSearchParams.of(cursorParams, request.moduleIds());
    }

    /**
     * Result 목록 -> Response 목록 변환
     *
     * @param results ConventionResult 목록
     * @return ConventionApiResponse 목록
     */
    public List<ConventionApiResponse> toResponses(List<ConventionResult> results) {
        return results.stream()
                .map(
                        result ->
                                new ConventionApiResponse(
                                        result.id(),
                                        result.moduleId(),
                                        result.version(),
                                        result.description(),
                                        result.active(),
                                        DateTimeFormatUtils.formatIso8601(result.createdAt()),
                                        DateTimeFormatUtils.formatIso8601(result.updatedAt())))
                .toList();
    }

    /**
     * ConventionSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ConventionApiResponse> toSliceResponse(
            ConventionSliceResult sliceResult) {
        List<ConventionApiResponse> responses = toResponses(sliceResult.content());
        return SliceApiResponse.of(
                responses,
                sliceResult.sliceMeta().size(),
                sliceResult.sliceMeta().hasNext(),
                sliceResult.sliceMeta().cursor());
    }
}
