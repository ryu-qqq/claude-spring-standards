package com.ryuqq.adapter.in.rest.resourcetemplate.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.request.SearchResourceTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.resourcetemplate.dto.response.ResourceTemplateApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.resourcetemplate.dto.query.ResourceTemplateSearchParams;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateResult;
import com.ryuqq.application.resourcetemplate.dto.response.ResourceTemplateSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateQueryApiMapper - ResourceTemplate Query API 변환 매퍼
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
public class ResourceTemplateQueryApiMapper {

    /**
     * SearchResourceTemplatesCursorApiRequest -> ResourceTemplateSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 -> Mapper에서 변환 처리.
     *
     * <p>DTO-015: Request DTO Compact Constructor 기본값 설정 금지 -> Mapper에서 처리.
     *
     * <p>QDTO-004: CommonCursorParams 생성 후 SearchParams에 포함.
     *
     * @param request 조회 요청 DTO
     * @return ResourceTemplateSearchParams 객체
     */
    public ResourceTemplateSearchParams toSearchParams(
            SearchResourceTemplatesCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return ResourceTemplateSearchParams.of(
                cursorParams, request.moduleIds(), request.categories(), request.fileTypes());
    }

    /**
     * 단일 ResourceTemplateResult -> ResourceTemplateApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result ResourceTemplateResult
     * @return ResourceTemplateApiResponse
     */
    public ResourceTemplateApiResponse toResponse(ResourceTemplateResult result) {
        return new ResourceTemplateApiResponse(
                result.id(),
                result.moduleId(),
                result.category(),
                result.filePath(),
                result.fileType(),
                result.description(),
                result.templateContent(),
                result.required(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * ResourceTemplateResult 목록 -> ResourceTemplateApiResponse 목록 변환
     *
     * @param results ResourceTemplateResult 목록
     * @return ResourceTemplateApiResponse 목록
     */
    public List<ResourceTemplateApiResponse> toResponses(List<ResourceTemplateResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ResourceTemplateSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ResourceTemplateApiResponse> toSliceResponse(
            ResourceTemplateSliceResult sliceResult) {
        List<ResourceTemplateApiResponse> responses = toResponses(sliceResult.resourceTemplates());
        String nextCursor =
                sliceResult.nextCursor() != null ? sliceResult.nextCursor().toString() : null;
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }
}
