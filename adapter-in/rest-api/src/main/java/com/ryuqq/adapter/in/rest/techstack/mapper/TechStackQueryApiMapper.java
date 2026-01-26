package com.ryuqq.adapter.in.rest.techstack.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.techstack.dto.request.SearchTechStacksCursorApiRequest;
import com.ryuqq.adapter.in.rest.techstack.dto.response.TechStackApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.techstack.dto.query.TechStackSearchParams;
import com.ryuqq.application.techstack.dto.response.TechStackResult;
import com.ryuqq.application.techstack.dto.response.TechStackSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * TechStackQueryApiMapper - TechStack Query API 변환 매퍼
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
public class TechStackQueryApiMapper {

    /**
     * SearchTechStacksCursorApiRequest -> TechStackSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>CommonCursorParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return TechStackSearchParams 객체
     */
    public TechStackSearchParams toSearchParams(SearchTechStacksCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return TechStackSearchParams.of(cursorParams, request.status(), request.platformTypes());
    }

    /**
     * TechStackResult -> TechStackApiResponse 변환
     *
     * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public TechStackApiResponse toResponse(TechStackResult result) {
        return new TechStackApiResponse(
                result.id(),
                result.name(),
                result.status(),
                result.languageType(),
                result.languageVersion(),
                result.languageFeatures(),
                result.frameworkType(),
                result.frameworkVersion(),
                result.frameworkModules(),
                result.platformType(),
                result.runtimeEnvironment(),
                result.buildToolType(),
                result.buildConfigFile(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * TechStackResult 목록 -> TechStackApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<TechStackApiResponse> toResponses(List<TechStackResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * TechStackSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<TechStackApiResponse> toSliceResponse(
            TechStackSliceResult sliceResult) {
        List<TechStackApiResponse> responses = toResponses(sliceResult.content());
        return SliceApiResponse.of(
                responses,
                sliceResult.sliceMeta().size(),
                sliceResult.sliceMeta().hasNext(),
                sliceResult.sliceMeta().cursor());
    }
}
