package com.ryuqq.adapter.in.rest.layer.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.layer.dto.request.SearchLayersCursorApiRequest;
import com.ryuqq.adapter.in.rest.layer.dto.response.LayerApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.layer.dto.query.LayerSearchParams;
import com.ryuqq.application.layer.dto.response.LayerResult;
import com.ryuqq.application.layer.dto.response.LayerSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LayerQueryApiMapper - Layer Query API 변환 매퍼
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>MAPPER-001: Mapper는 @Component로 등록.
 *
 * <p>MAPPER-003: Application Result -> API Response 변환.
 *
 * <p>MAPPER-004: Domain 타입 직접 의존 금지.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LayerQueryApiMapper {

    /**
     * SearchLayersCursorApiRequest -> LayerSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>CommonCursorParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return LayerSearchParams 객체
     */
    public LayerSearchParams toSearchParams(SearchLayersCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return LayerSearchParams.of(
                cursorParams,
                request.architectureIds(),
                request.searchField(),
                request.searchWord());
    }

    /**
     * LayerResult 목록 -> LayerApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<LayerApiResponse> toResponses(List<LayerResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * LayerSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<LayerApiResponse> toSliceResponse(LayerSliceResult sliceResult) {
        List<LayerApiResponse> responses = toResponses(sliceResult.content());
        return SliceApiResponse.of(
                responses,
                sliceResult.sliceMeta().size(),
                sliceResult.sliceMeta().hasNext(),
                sliceResult.sliceMeta().cursor());
    }

    /**
     * LayerResult -> LayerApiResponse 단건 변환
     *
     * <p>MAPPER-005: DateTimeFormatUtils 사용하여 날짜 포맷 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    private LayerApiResponse toResponse(LayerResult result) {
        return new LayerApiResponse(
                result.id(),
                result.architectureId(),
                result.code(),
                result.name(),
                result.description(),
                result.orderIndex(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }
}
