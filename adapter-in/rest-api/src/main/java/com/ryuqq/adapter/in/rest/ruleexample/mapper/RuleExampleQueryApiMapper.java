package com.ryuqq.adapter.in.rest.ruleexample.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.ruleexample.dto.request.SearchRuleExamplesCursorApiRequest;
import com.ryuqq.adapter.in.rest.ruleexample.dto.response.RuleExampleApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.ruleexample.dto.query.RuleExampleSearchParams;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleResult;
import com.ryuqq.application.ruleexample.dto.response.RuleExampleSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RuleExampleQueryApiMapper - RuleExample Query API 변환 매퍼
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
 * <p>MAP-013: Mapper CQRS 분리 권장 (CommandApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class RuleExampleQueryApiMapper {

    /**
     * SearchRuleExamplesCursorApiRequest -> RuleExampleSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 -> Mapper에서 변환 처리.
     *
     * <p>DTO-015: Request DTO Compact Constructor 기본값 설정 금지 -> Mapper에서 처리.
     *
     * <p>QDTO-004: CommonCursorParams 생성 후 SearchParams에 포함.
     *
     * @param request 조회 요청 DTO
     * @return RuleExampleSearchParams 객체
     */
    public RuleExampleSearchParams toSearchParams(SearchRuleExamplesCursorApiRequest request) {
        String cursor = request.cursor();
        CommonCursorParams cursorParams = CommonCursorParams.of(cursor, request.size());
        return RuleExampleSearchParams.of(
                cursorParams, request.ruleIds(), request.exampleTypes(), request.languages());
    }

    /**
     * 단일 RuleExampleResult -> RuleExampleApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result RuleExampleResult
     * @return RuleExampleApiResponse
     */
    public RuleExampleApiResponse toResponse(RuleExampleResult result) {
        return new RuleExampleApiResponse(
                result.id(),
                result.ruleId(),
                result.exampleType(),
                result.code(),
                result.language(),
                result.explanation(),
                result.highlightLines(),
                result.source(),
                result.feedbackId(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * RuleExampleResult 목록 -> RuleExampleApiResponse 목록 변환
     *
     * @param results RuleExampleResult 목록
     * @return RuleExampleApiResponse 목록
     */
    public List<RuleExampleApiResponse> toResponses(List<RuleExampleResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * RuleExampleSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<RuleExampleApiResponse> toSliceResponse(
            RuleExampleSliceResult sliceResult) {
        List<RuleExampleApiResponse> responses = toResponses(sliceResult.ruleExamples());
        String nextCursor =
                sliceResult.nextCursor() != null ? sliceResult.nextCursor().toString() : null;
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }
}
