package com.ryuqq.adapter.in.rest.classtypecategory.mapper;

import com.ryuqq.adapter.in.rest.classtypecategory.dto.request.SearchClassTypeCategoriesCursorApiRequest;
import com.ryuqq.adapter.in.rest.classtypecategory.dto.response.ClassTypeCategoryApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.application.classtypecategory.dto.query.ClassTypeCategorySearchParams;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategoryResult;
import com.ryuqq.application.classtypecategory.dto.response.ClassTypeCategorySliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTypeCategoryQueryApiMapper - ClassTypeCategory Query API 변환 매퍼
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
public class ClassTypeCategoryQueryApiMapper {

    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchClassTypeCategoriesCursorApiRequest -> ClassTypeCategorySearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * @param request 조회 요청 DTO
     * @return ClassTypeCategorySearchParams 객체
     */
    public ClassTypeCategorySearchParams toSearchParams(
            SearchClassTypeCategoriesCursorApiRequest request) {
        Long cursor = parseCursor(request.cursor());
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;
        return ClassTypeCategorySearchParams.of(
                null,
                request.architectureIds(),
                request.searchField(),
                request.searchWord(),
                cursor,
                size);
    }

    private Long parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(cursor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * ClassTypeCategoryResult 목록 -> ClassTypeCategoryApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<ClassTypeCategoryApiResponse> toResponses(List<ClassTypeCategoryResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ClassTypeCategorySliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ClassTypeCategoryApiResponse> toSliceResponse(
            ClassTypeCategorySliceResult sliceResult) {
        List<ClassTypeCategoryApiResponse> responses = toResponses(sliceResult.content());
        return SliceApiResponse.of(
                responses,
                sliceResult.sliceMeta().size(),
                sliceResult.sliceMeta().hasNext(),
                sliceResult.sliceMeta().cursor());
    }

    /**
     * ClassTypeCategoryResult -> ClassTypeCategoryApiResponse 단건 변환
     *
     * <p>MAPPER-005: DateTimeFormatUtils 사용하여 날짜 포맷 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    private ClassTypeCategoryApiResponse toResponse(ClassTypeCategoryResult result) {
        return new ClassTypeCategoryApiResponse(
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
