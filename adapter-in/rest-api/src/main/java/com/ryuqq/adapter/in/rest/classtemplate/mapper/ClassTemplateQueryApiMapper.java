package com.ryuqq.adapter.in.rest.classtemplate.mapper;

import com.ryuqq.adapter.in.rest.classtemplate.dto.request.SearchClassTemplatesCursorApiRequest;
import com.ryuqq.adapter.in.rest.classtemplate.dto.response.ClassTemplateApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.application.classtemplate.dto.query.ClassTemplateSearchParams;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateResult;
import com.ryuqq.application.classtemplate.dto.response.ClassTemplateSliceResult;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateQueryApiMapper - ClassTemplate Query API 변환 매퍼
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
public class ClassTemplateQueryApiMapper {

    /**
     * SearchClassTemplatesCursorApiRequest -> ClassTemplateSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CommonCursorParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return ClassTemplateSearchParams 객체
     */
    public ClassTemplateSearchParams toSearchParams(SearchClassTemplatesCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return ClassTemplateSearchParams.of(
                cursorParams, request.structureIds(), request.classTypeIds());
    }

    /**
     * 단일 ClassTemplateResult -> ClassTemplateApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result ClassTemplateResult
     * @return ClassTemplateApiResponse
     */
    public ClassTemplateApiResponse toResponse(ClassTemplateResult result) {
        return new ClassTemplateApiResponse(
                result.id(),
                result.structureId(),
                result.classTypeId(),
                result.templateCode(),
                result.namingPattern(),
                result.description(),
                result.requiredAnnotations(),
                result.forbiddenAnnotations(),
                result.requiredInterfaces(),
                result.forbiddenInheritance(),
                result.requiredMethods(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * ClassTemplateResult 목록 -> ClassTemplateApiResponse 목록 변환
     *
     * @param results ClassTemplateResult 목록
     * @return ClassTemplateApiResponse 목록
     */
    public List<ClassTemplateApiResponse> toResponses(List<ClassTemplateResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ClassTemplateSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ClassTemplateApiResponse> toSliceResponse(
            ClassTemplateSliceResult sliceResult) {
        List<ClassTemplateApiResponse> responses = toResponses(sliceResult.classTemplates());
        String nextCursor =
                sliceResult.nextCursor() != null ? sliceResult.nextCursor().toString() : null;
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }
}
