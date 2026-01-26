package com.ryuqq.adapter.in.rest.codingrule.mapper;

import com.ryuqq.adapter.in.rest.codingrule.dto.request.CodingRuleIndexApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.request.SearchCodingRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleApiResponse;
import com.ryuqq.adapter.in.rest.codingrule.dto.response.CodingRuleIndexApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.application.codingrule.dto.query.CodingRuleIndexSearchParams;
import com.ryuqq.application.codingrule.dto.query.CodingRuleSearchParams;
import com.ryuqq.application.codingrule.dto.response.CodingRuleIndexItem;
import com.ryuqq.application.codingrule.dto.response.CodingRuleResult;
import com.ryuqq.application.codingrule.dto.response.CodingRuleSliceResult;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CodingRuleQueryApiMapper - CodingRule Query API 변환 매퍼
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
public class CodingRuleQueryApiMapper {

    /**
     * SearchCodingRulesCursorApiRequest -> CodingRuleSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CommonCursorParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return CodingRuleSearchParams 객체
     */
    public CodingRuleSearchParams toSearchParams(SearchCodingRulesCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return CodingRuleSearchParams.of(
                cursorParams,
                request.categories(),
                request.severities(),
                request.searchField(),
                request.searchWord());
    }

    /**
     * 단일 CodingRuleResult -> CodingRuleApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result CodingRuleResult
     * @return CodingRuleApiResponse
     */
    public CodingRuleApiResponse toResponse(CodingRuleResult result) {
        return new CodingRuleApiResponse(
                result.id(),
                result.conventionId(),
                result.structureId(),
                result.code(),
                result.name(),
                result.severity().name(),
                result.category().name(),
                result.description(),
                result.rationale(),
                result.autoFixable(),
                result.appliesTo(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * CodingRuleResult 목록 -> CodingRuleApiResponse 목록 변환
     *
     * @param results CodingRuleResult 목록
     * @return CodingRuleApiResponse 목록
     */
    public List<CodingRuleApiResponse> toResponses(List<CodingRuleResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * CodingRuleSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<CodingRuleApiResponse> toSliceResponse(
            CodingRuleSliceResult sliceResult) {
        List<CodingRuleApiResponse> responses = toResponses(sliceResult.codingRules());
        String nextCursor =
                sliceResult.nextCursor() != null ? sliceResult.nextCursor().toString() : null;
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }

    /**
     * CodingRuleIndexApiRequest -> CodingRuleIndexSearchParams 변환
     *
     * @param request 인덱스 조회 요청 DTO
     * @return CodingRuleIndexSearchParams 객체
     */
    public CodingRuleIndexSearchParams toIndexSearchParams(CodingRuleIndexApiRequest request) {
        return CodingRuleIndexSearchParams.of(
                request.conventionId(), request.severities(), request.categories());
    }

    /**
     * CodingRuleIndexItem -> CodingRuleIndexApiResponse 변환
     *
     * @param item 인덱스 아이템
     * @return CodingRuleIndexApiResponse
     */
    public CodingRuleIndexApiResponse toIndexResponse(CodingRuleIndexItem item) {
        return CodingRuleIndexApiResponse.of(
                item.code(), item.name(), item.severity(), item.category());
    }

    /**
     * CodingRuleIndexItem 목록 -> CodingRuleIndexApiResponse 목록 변환
     *
     * @param items 인덱스 아이템 목록
     * @return CodingRuleIndexApiResponse 목록
     */
    public List<CodingRuleIndexApiResponse> toIndexResponses(List<CodingRuleIndexItem> items) {
        return items.stream().map(this::toIndexResponse).toList();
    }
}
