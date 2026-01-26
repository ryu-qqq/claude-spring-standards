package com.ryuqq.adapter.in.rest.configfiletemplate.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.request.SearchConfigFileTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.configfiletemplate.dto.response.ConfigFileTemplateApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.configfiletemplate.dto.query.ConfigFileTemplateSearchParams;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateResult;
import com.ryuqq.application.configfiletemplate.dto.response.ConfigFileTemplateSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ConfigFileTemplateQueryApiMapper - ConfigFileTemplate Query API 변환 매퍼
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
public class ConfigFileTemplateQueryApiMapper {

    /**
     * SearchConfigFileTemplatesCursorApiRequest -> ConfigFileTemplateSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * <p>CommonCursorParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return ConfigFileTemplateSearchParams 객체
     */
    public ConfigFileTemplateSearchParams toSearchParams(
            SearchConfigFileTemplatesCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return ConfigFileTemplateSearchParams.of(
                cursorParams,
                request.toolTypes(),
                request.techStackIds(),
                request.architectureIds(),
                request.categories(),
                request.isRequired());
    }

    /**
     * ConfigFileTemplateResult -> ConfigFileTemplateApiResponse 변환
     *
     * <p>CFG-002: JacksonConfig 중앙 설정 대신 DateTimeFormatUtils를 사용하여 String으로 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public ConfigFileTemplateApiResponse toResponse(ConfigFileTemplateResult result) {
        return new ConfigFileTemplateApiResponse(
                result.id(),
                result.techStackId(),
                result.architectureId(),
                result.toolType(),
                result.filePath(),
                result.fileName(),
                result.content(),
                result.category(),
                result.description(),
                result.variables(),
                result.displayOrder(),
                result.isRequired(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * ConfigFileTemplateResult 목록 -> ConfigFileTemplateApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<ConfigFileTemplateApiResponse> toResponses(List<ConfigFileTemplateResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ConfigFileTemplateSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ConfigFileTemplateApiResponse> toSliceResponse(
            ConfigFileTemplateSliceResult sliceResult) {
        List<ConfigFileTemplateApiResponse> responses = toResponses(sliceResult.content());
        return SliceApiResponse.of(
                responses,
                sliceResult.sliceMeta().size(),
                sliceResult.sliceMeta().hasNext(),
                sliceResult.sliceMeta().cursor());
    }
}
