package com.ryuqq.adapter.in.rest.layerdependency.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.layerdependency.dto.request.SearchLayerDependencyRulesCursorApiRequest;
import com.ryuqq.adapter.in.rest.layerdependency.dto.response.LayerDependencyRuleApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.layerdependency.dto.query.LayerDependencyRuleSearchParams;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleResult;
import com.ryuqq.application.layerdependency.dto.response.LayerDependencyRuleSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LayerDependencyRuleQueryApiMapper - LayerDependencyRule Query API 변환 매퍼
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
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LayerDependencyRuleQueryApiMapper {

    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchLayerDependencyRulesCursorApiRequest -> LayerDependencyRuleSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 -> Mapper에서 변환 처리.
     *
     * <p>DTO-015: Request DTO Compact Constructor 기본값 설정 금지 -> Mapper에서 처리.
     *
     * @param request 조회 요청 DTO
     * @return LayerDependencyRuleSearchParams 객체
     */
    public LayerDependencyRuleSearchParams toSearchParams(
            SearchLayerDependencyRulesCursorApiRequest request) {
        int size = request.size() != null && request.size() > 0 ? request.size() : DEFAULT_SIZE;
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), size);
        return LayerDependencyRuleSearchParams.of(
                cursorParams,
                request.architectureIds(),
                request.dependencyTypes(),
                request.searchField(),
                request.searchWord());
    }

    /**
     * 단일 LayerDependencyRuleResult -> LayerDependencyRuleApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result LayerDependencyRuleResult
     * @return LayerDependencyRuleApiResponse
     */
    public LayerDependencyRuleApiResponse toResponse(LayerDependencyRuleResult result) {
        return new LayerDependencyRuleApiResponse(
                result.id(),
                result.architectureId(),
                result.fromLayer().name(),
                result.toLayer().name(),
                result.dependencyType().name(),
                result.conditionDescription(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * LayerDependencyRuleResult 목록 -> LayerDependencyRuleApiResponse 목록 변환
     *
     * @param results LayerDependencyRuleResult 목록
     * @return LayerDependencyRuleApiResponse 목록
     */
    public List<LayerDependencyRuleApiResponse> toResponses(
            List<LayerDependencyRuleResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * LayerDependencyRuleSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<LayerDependencyRuleApiResponse> toSliceResponse(
            LayerDependencyRuleSliceResult sliceResult) {
        List<LayerDependencyRuleApiResponse> responses = toResponses(sliceResult.rules());
        String nextCursor =
                sliceResult.nextCursor() != null ? String.valueOf(sliceResult.nextCursor()) : null;
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }
}
