package com.ryuqq.adapter.in.rest.archunittest.controller.query;

import com.ryuqq.adapter.in.rest.archunittest.ArchUnitTestApiEndpoints;
import com.ryuqq.adapter.in.rest.archunittest.dto.request.SearchArchUnitTestsCursorApiRequest;
import com.ryuqq.adapter.in.rest.archunittest.dto.response.ArchUnitTestApiResponse;
import com.ryuqq.adapter.in.rest.archunittest.mapper.ArchUnitTestQueryApiMapper;
import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.common.dto.SliceApiResponse;
import com.ryuqq.application.archunittest.dto.query.ArchUnitTestSearchParams;
import com.ryuqq.application.archunittest.dto.response.ArchUnitTestSliceResult;
import com.ryuqq.application.archunittest.port.in.SearchArchUnitTestsByCursorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ArchUnitTestQueryController - ArchUnitTest 조회 API
 *
 * <p>ArchUnit 테스트 조회 엔드포인트를 제공합니다.
 *
 * <p>CTR-001: @RestController 어노테이션 필수.
 *
 * <p>CTR-002: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * <p>CTR-009: Controller에서 Lombok 사용 금지.
 *
 * <p>CTR-012: URL 경로 소문자 + 복수형 (/arch-unit-tests).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "ArchUnitTest", description = "ArchUnit 테스트 조회 API")
@RestController
@RequestMapping(ArchUnitTestApiEndpoints.BASE)
public class ArchUnitTestQueryController {

    private final SearchArchUnitTestsByCursorUseCase searchArchUnitTestsByCursorUseCase;
    private final ArchUnitTestQueryApiMapper mapper;

    /**
     * ArchUnitTestQueryController 생성자
     *
     * @param getArchUnitTestUseCase 단건 조회 UseCase
     * @param getAllArchUnitTestsUseCase 전체 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public ArchUnitTestQueryController(
            SearchArchUnitTestsByCursorUseCase searchArchUnitTestsByCursorUseCase,
            ArchUnitTestQueryApiMapper mapper) {
        this.searchArchUnitTestsByCursorUseCase = searchArchUnitTestsByCursorUseCase;
        this.mapper = mapper;
    }

    /**
     * ArchUnitTest 복합 조건 조회 API (커서 기반)
     *
     * <p>ArchUnitTest 목록을 커서 기반으로 조회합니다. 패키지 구조 ID(복수), 검색(필드/키워드), 심각도(복수) 필터링을 지원합니다.
     *
     * @param request 조회 요청 DTO
     * @return ArchUnit 테스트 슬라이스 목록
     */
    @Operation(
            summary = "ArchUnitTest 복합 조건 조회",
            description =
                    "ArchUnitTest 목록을 커서 기반으로 조회합니다. 패키지 구조 ID(복수), 검색(필드/키워드), 심각도(복수) 필터링을"
                            + " 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<SliceApiResponse<ArchUnitTestApiResponse>>>
            searchArchUnitTestsByCursor(@Valid SearchArchUnitTestsCursorApiRequest request) {

        ArchUnitTestSearchParams searchParams = mapper.toSearchParams(request);
        ArchUnitTestSliceResult sliceResult =
                searchArchUnitTestsByCursorUseCase.execute(searchParams);
        SliceApiResponse<ArchUnitTestApiResponse> response = mapper.toSliceResponse(sliceResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
