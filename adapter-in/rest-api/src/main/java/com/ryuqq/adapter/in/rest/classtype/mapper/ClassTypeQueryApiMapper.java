package com.ryuqq.adapter.in.rest.classtype.mapper;

import com.ryuqq.adapter.in.rest.classtype.dto.request.SearchClassTypesCursorApiRequest;
import com.ryuqq.adapter.in.rest.classtype.dto.response.ClassTypeApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.application.classtype.dto.query.ClassTypeSearchParams;
import com.ryuqq.application.classtype.dto.response.ClassTypeResult;
import com.ryuqq.application.classtype.dto.response.ClassTypeSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ClassTypeQueryApiMapper - ClassType Query API 변환 매퍼
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
public class ClassTypeQueryApiMapper {

    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchClassTypesCursorApiRequest -> ClassTypeSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 로직 처리.
     *
     * @param request 조회 요청 DTO
     * @return ClassTypeSearchParams 객체
     */
    public ClassTypeSearchParams toSearchParams(SearchClassTypesCursorApiRequest request) {
        Long cursor = parseCursor(request.cursor());
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;
        return ClassTypeSearchParams.of(
                null,
                request.categoryIds(),
                null,
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
     * ClassTypeResult 목록 -> ClassTypeApiResponse 목록 변환
     *
     * @param results Application 결과 DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<ClassTypeApiResponse> toResponses(List<ClassTypeResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ClassTypeSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ClassTypeApiResponse> toSliceResponse(
            ClassTypeSliceResult sliceResult) {
        List<ClassTypeApiResponse> responses = toResponses(sliceResult.content());
        return SliceApiResponse.of(
                responses,
                sliceResult.sliceMeta().size(),
                sliceResult.sliceMeta().hasNext(),
                sliceResult.sliceMeta().cursor());
    }

    /**
     * ClassTypeResult -> ClassTypeApiResponse 단건 변환
     *
     * <p>MAPPER-005: DateTimeFormatUtils 사용하여 날짜 포맷 변환.
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    private ClassTypeApiResponse toResponse(ClassTypeResult result) {
        return new ClassTypeApiResponse(
                result.id(),
                result.categoryId(),
                result.code(),
                result.name(),
                result.description(),
                result.orderIndex(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }
}
