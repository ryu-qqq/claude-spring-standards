package com.ryuqq.adapter.in.rest.architecture.mapper;

import com.ryuqq.adapter.in.rest.architecture.dto.request.SearchArchitecturesCursorApiRequest;
import com.ryuqq.adapter.in.rest.architecture.dto.response.ArchitectureApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.application.architecture.dto.query.ArchitectureSearchParams;
import com.ryuqq.application.architecture.dto.response.ArchitectureResult;
import com.ryuqq.application.architecture.dto.response.ArchitectureSliceResult;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ArchitectureQueryApiMapper - Architecture Query API 변환 매퍼
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
public class ArchitectureQueryApiMapper {

    /**
     * SearchArchitecturesCursorApiRequest -> ArchitectureSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>CommonCursorParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return ArchitectureSearchParams 객체
     */
    public ArchitectureSearchParams toSearchParams(SearchArchitecturesCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return ArchitectureSearchParams.of(cursorParams, request.techStackIds());
    }

    /**
     * ArchitectureResult -> ArchitectureApiResponse 변환
     *
     * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public ArchitectureApiResponse toResponse(ArchitectureResult result) {
        return new ArchitectureApiResponse(
                result.id(),
                result.techStackId(),
                result.name(),
                result.patternType(),
                result.patternDescription(),
                result.patternPrinciples(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * ArchitectureResult 목록 -> ArchitectureApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<ArchitectureApiResponse> toResponses(List<ArchitectureResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ArchitectureSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ArchitectureApiResponse> toSliceResponse(
            ArchitectureSliceResult sliceResult) {
        List<ArchitectureApiResponse> responses = toResponses(sliceResult.content());
        return SliceApiResponse.of(
                responses,
                sliceResult.sliceMeta().size(),
                sliceResult.sliceMeta().hasNext(),
                sliceResult.sliceMeta().cursor());
    }
}
