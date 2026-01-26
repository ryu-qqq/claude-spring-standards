package com.ryuqq.adapter.in.rest.packagepurpose.mapper;

import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.request.SearchPackagePurposesCursorApiRequest;
import com.ryuqq.adapter.in.rest.packagepurpose.dto.response.PackagePurposeApiResponse;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import com.ryuqq.application.packagepurpose.dto.query.PackagePurposeSearchParams;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeResult;
import com.ryuqq.application.packagepurpose.dto.response.PackagePurposeSliceResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PackagePurposeQueryApiMapper - PackagePurpose Query API 변환 매퍼
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
public class PackagePurposeQueryApiMapper {

    /**
     * SearchPackagePurposesCursorApiRequest -> PackagePurposeSearchParams 변환
     *
     * @param request 조회 요청 DTO (커서 기반, structureIds, searchField/searchWord 포함)
     * @return PackagePurposeSearchParams 객체
     */
    public PackagePurposeSearchParams toSearchParams(
            SearchPackagePurposesCursorApiRequest request) {
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), request.size());
        return PackagePurposeSearchParams.of(
                cursorParams, request.structureIds(), request.searchField(), request.searchWord());
    }

    /**
     * 단일 PackagePurposeResult -> PackagePurposeApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result PackagePurposeResult
     * @return PackagePurposeApiResponse
     */
    public PackagePurposeApiResponse toResponse(PackagePurposeResult result) {
        return new PackagePurposeApiResponse(
                result.id(),
                result.structureId(),
                result.code(),
                result.name(),
                result.description(),
                result.defaultAllowedClassTypes(),
                result.defaultNamingPattern(),
                result.defaultNamingSuffix(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * PackagePurposeResult 목록 -> PackagePurposeApiResponse 목록 변환
     *
     * @param results PackagePurposeResult 목록
     * @return PackagePurposeApiResponse 목록
     */
    public List<PackagePurposeApiResponse> toResponses(List<PackagePurposeResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * PackagePurposeSliceResult -> SliceApiResponse 변환
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<PackagePurposeApiResponse> toSliceResponse(
            PackagePurposeSliceResult sliceResult) {
        List<PackagePurposeApiResponse> responses = toResponses(sliceResult.packagePurposes());
        String nextCursor =
                sliceResult.nextCursor() != null ? sliceResult.nextCursor().toString() : null;
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }
}
