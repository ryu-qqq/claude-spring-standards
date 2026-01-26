package com.ryuqq.adapter.in.rest.archunittest.mapper;

import com.ryuqq.adapter.in.rest.archunittest.dto.request.SearchArchUnitTestsCursorApiRequest;
import com.ryuqq.adapter.in.rest.archunittest.dto.response.ArchUnitTestApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.application.archunittest.dto.query.ArchUnitTestSearchParams;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestResult;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestSliceResult;
import com.ryuqq.application.common.dto.query.CommonCursorParams;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ArchUnitTestQueryApiMapper - ArchUnitTest Query API 변환 매퍼
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
public class ArchUnitTestQueryApiMapper {

    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchArchUnitTestsCursorApiRequest -> ArchUnitTestSearchParams 변환
     *
     * <p>CTR-001/CTR-007: Controller 비즈니스 로직 금지 -> Mapper에서 변환 처리.
     *
     * <p>DTO-015: Request DTO Compact Constructor 기본값 설정 금지 -> Mapper에서 처리.
     *
     * @param request 조회 요청 DTO
     * @return ArchUnitTestSearchParams 객체
     */
    public ArchUnitTestSearchParams toSearchParams(SearchArchUnitTestsCursorApiRequest request) {
        int size = request.size() != null && request.size() > 0 ? request.size() : DEFAULT_SIZE;
        CommonCursorParams cursorParams = CommonCursorParams.of(request.cursor(), size);
        return ArchUnitTestSearchParams.of(
                cursorParams,
                request.structureIds(),
                request.searchField(),
                request.searchWord(),
                request.severities());
    }

    /**
     * 단일 ArchUnitTestResult -> ArchUnitTestApiResponse 변환
     *
     * <p>DTO-016: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result ArchUnitTestResult
     * @return ArchUnitTestApiResponse
     */
    public ArchUnitTestApiResponse toResponse(ArchUnitTestResult result) {
        return new ArchUnitTestApiResponse(
                result.archUnitTestId(),
                result.structureId(),
                result.code(),
                result.name(),
                result.description(),
                result.testClassName(),
                result.testMethodName(),
                result.testCode(),
                result.severity(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * ArchUnitTestResult 목록 -> ArchUnitTestApiResponse 목록 변환
     *
     * @param results ArchUnitTestResult 목록
     * @return ArchUnitTestApiResponse 목록
     */
    public List<ArchUnitTestApiResponse> toResponses(List<ArchUnitTestResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * ArchUnitTestSliceResult -> SliceApiResponse 변환
     *
     * <p>RDTO-009: List 직접 반환 금지 -> SliceApiResponse 페이징 필수.
     *
     * @param sliceResult Application 슬라이스 결과 DTO
     * @return API 슬라이스 응답 DTO
     */
    public SliceApiResponse<ArchUnitTestApiResponse> toSliceResponse(
            ArchUnitTestSliceResult sliceResult) {
        List<ArchUnitTestApiResponse> responses = toResponses(sliceResult.archUnitTests());
        String nextCursor =
                sliceResult.nextCursor() != null ? sliceResult.nextCursor().toString() : null;
        return SliceApiResponse.of(
                responses, sliceResult.size(), sliceResult.hasNext(), nextCursor);
    }
}
