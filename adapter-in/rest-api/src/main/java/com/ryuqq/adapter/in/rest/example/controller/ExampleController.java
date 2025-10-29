package com.ryuqq.adapter.in.rest.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import com.ryuqq.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.adapter.in.rest.example.dto.request.ExampleApiRequest;
import com.ryuqq.adapter.in.rest.example.dto.request.ExampleSearchApiRequest;
import com.ryuqq.adapter.in.rest.example.dto.response.ExampleApiResponse;
import com.ryuqq.adapter.in.rest.example.dto.response.ExampleDetailApiResponse;
import com.ryuqq.adapter.in.rest.example.dto.response.ExamplePageApiResponse;
import com.ryuqq.adapter.in.rest.example.dto.response.ExampleSliceApiResponse;
import com.ryuqq.adapter.in.rest.example.mapper.ExampleApiMapper;
import com.ryuqq.application.example.dto.command.CreateExampleCommand;
import com.ryuqq.application.example.dto.response.ExampleResponse;
import com.ryuqq.application.example.port.in.CreateExampleUseCase;
import com.ryuqq.application.example.port.in.GetExampleQueryService;
import com.ryuqq.application.example.port.in.SearchExampleQueryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Example API 컨트롤러 (CQRS 패턴 적용)
 *
 * <p>Example 도메인의 REST API 엔드포인트를 제공합니다.</p>
 * <p>CQRS 패턴을 적용하여 Command와 Query를 분리했습니다.</p>
 *
 * <p><strong>제공하는 API:</strong></p>
 * <ul>
 *   <li>POST /api/v1/examples - Example 생성 (Command)</li>
 *   <li>GET /api/v1/examples/{id} - Example 단건 조회 (Query)</li>
 *   <li>GET /api/v1/examples - Example 검색 (Cursor 기반, Query)</li>
 *   <li>GET /api/v1/admin/examples/search - Example 검색 (Offset 기반, Query)</li>
 * </ul>
 *
 * <p><strong>페이지네이션 전략:</strong></p>
 * <ul>
 *   <li>일반 사용자용: Cursor 기반 (무한 스크롤, 고성능)</li>
 *   <li>관리자용: Offset 기반 (페이지 번호, 전체 개수 제공)</li>
 * </ul>
 *
 * <p><strong>엔드포인트 경로 관리:</strong></p>
 * <ul>
 *   <li>application.yml에서 api.endpoints 설정으로 경로 중앙 관리</li>
 *   <li>@see com.ryuqq.adapter.in.rest.config.properties.ApiEndpointProperties</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Tag(
    name = "Example API",
    description = "Example 도메인 관리 API. CQRS 패턴 적용 (Command/Query 분리)"
)
@RestController
@RequestMapping("${api.endpoints.base-v1}")
@Validated
public class ExampleController {

    private final CreateExampleUseCase createExampleUseCase;
    private final GetExampleQueryService getExampleQueryService;
    private final SearchExampleQueryService searchExampleQueryService;
    private final ExampleApiMapper exampleApiMapper;

    /**
     * ExampleController 생성자
     *
     * @param createExampleUseCase Example 생성 UseCase
     * @param getExampleQueryService Example 조회 Query Service
     * @param searchExampleQueryService Example 검색 Query Service
     * @param exampleApiMapper Example Mapper
     */
    public ExampleController(
            CreateExampleUseCase createExampleUseCase,
            GetExampleQueryService getExampleQueryService,
            SearchExampleQueryService searchExampleQueryService,
            ExampleApiMapper exampleApiMapper) {
        this.createExampleUseCase = createExampleUseCase;
        this.getExampleQueryService = getExampleQueryService;
        this.searchExampleQueryService = searchExampleQueryService;
        this.exampleApiMapper = exampleApiMapper;
    }

    /**
     * Example을 생성합니다. (CQRS Command)
     *
     * <p><strong>Request Example:</strong></p>
     * <pre>{@code
     * POST /api/v1/examples
     * {
     *   "message": "Hello World"
     * }
     * }</pre>
     *
     * <p><strong>Response Example:</strong></p>
     * <pre>{@code
     * HTTP 201 Created
     * {
     *   "success": true,
     *   "data": {
     *     "message": "Hello World"
     *   }
     * }
     * }</pre>
     *
     * @param request Example 생성 요청 DTO
     * @return Example 생성 결과 (201 Created)
     */
    @Operation(
        summary = "Example 생성",
        description = "새로운 Example을 생성합니다. (CQRS Command)",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "생성 성공",
                content = @Content(schema = @Schema(implementation = ExampleApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (Validation 실패)"
            )
        }
    )
    @PostMapping("${api.endpoints.example.base}")
    public ResponseEntity<ApiResponse<ExampleApiResponse>> createExample(
            @Parameter(description = "Example 생성 요청", required = true)
            @RequestBody @Valid ExampleApiRequest request) {
        CreateExampleCommand command = exampleApiMapper.toCreateCommand(request);
        ExampleResponse result = createExampleUseCase.execute(command);
        ExampleApiResponse response = exampleApiMapper.toApiResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(response));
    }

    /**
     * Example ID로 단건 조회합니다. (CQRS Query)
     *
     * <p><strong>Request Example:</strong></p>
     * <pre>{@code
     * GET /api/v1/examples/123
     * }</pre>
     *
     * <p><strong>Response Example:</strong></p>
     * <pre>{@code
     * HTTP 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "id": 123,
     *     "message": "Hello World",
     *     "status": "ACTIVE",
     *     "createdAt": "2025-10-28T10:30:00",
     *     "updatedAt": "2025-10-28T10:30:00"
     *   }
     * }
     * }</pre>
     *
     * @param id Example ID (양수)
     * @return Example 상세 정보 (200 OK)
     */
    @Operation(
        summary = "Example 단건 조회",
        description = "Example ID로 상세 정보를 조회합니다. (CQRS Query)",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ExampleDetailApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Example을 찾을 수 없음"
            )
        }
    )
    @GetMapping("${api.endpoints.example.base}${api.endpoints.example.by-id}")
    public ResponseEntity<ApiResponse<ExampleDetailApiResponse>> getExample(
            @Parameter(description = "Example ID (양수)", required = true, example = "123")
            @PathVariable @Positive(message = "ID는 양수여야 합니다") Long id) {
        var query = exampleApiMapper.toGetQuery(id);
        var appResponse = getExampleQueryService.getById(query);
        var apiResponse = exampleApiMapper.toDetailApiResponse(appResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Example 검색 (Cursor 기반 페이지네이션, 일반 사용자용)
     *
     * <p>무한 스크롤 UI에 적합한 커서 기반 검색입니다.</p>
     * <p>COUNT 쿼리가 없어 대량 데이터 조회 시 고성능입니다.</p>
     *
     * <p><strong>Request Example:</strong></p>
     * <pre>{@code
     * GET /api/v1/examples?cursor=xyz&size=20&sortBy=createdAt&sortDirection=DESC
     * }</pre>
     *
     * <p><strong>Response Example:</strong></p>
     * <pre>{@code
     * HTTP 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "content": [
     *       {
     *         "id": 1,
     *         "message": "Example 1",
     *         "status": "ACTIVE",
     *         "createdAt": "2025-10-28T10:30:00",
     *         "updatedAt": "2025-10-28T10:30:00"
     *       }
     *     ],
     *     "size": 20,
     *     "hasNext": true,
     *     "nextCursor": "abc"
     *   }
     * }
     * }</pre>
     *
     * @param searchRequest 검색 조건 (cursor 기반)
     * @return Example 검색 결과 (Slice 형식, 200 OK)
     */
    @GetMapping("${api.endpoints.example.base}")
    public ResponseEntity<ApiResponse<ExampleSliceApiResponse>> searchExamplesByCursor(
            @Valid @ModelAttribute ExampleSearchApiRequest searchRequest) {
        var query = exampleApiMapper.toSearchQuery(searchRequest);
        var appSliceResponse = searchExampleQueryService.searchByCursor(query);
        var apiSliceResponse = exampleApiMapper.toSliceApiResponse(appSliceResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiSliceResponse));
    }

    /**
     * Example 검색 (Offset 기반 페이지네이션, 관리자용)
     *
     * <p>전통적인 페이지 번호 기반 검색으로 관리자 페이지에 적합합니다.</p>
     * <p>전체 개수와 페이지 정보를 제공하여 페이지 네비게이션이 가능합니다.</p>
     *
     * <p><strong>검색 조건:</strong></p>
     * <ul>
     *   <li>message: 메시지 부분 검색</li>
     *   <li>status: 상태 필터 (ACTIVE, INACTIVE, DELETED)</li>
     *   <li>startDate, endDate: 생성일 범위</li>
     *   <li>page, size: 페이징</li>
     *   <li>sortBy, sortDirection: 정렬</li>
     * </ul>
     *
     * <p><strong>Request Example:</strong></p>
     * <pre>{@code
     * GET /api/v1/admin/examples/search?message=hello&status=ACTIVE&page=0&size=20&sortBy=createdAt&sortDirection=DESC
     * }</pre>
     *
     * <p><strong>Response Example:</strong></p>
     * <pre>{@code
     * HTTP 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "content": [
     *       {
     *         "id": 1,
     *         "message": "Hello World",
     *         "status": "ACTIVE",
     *         "createdAt": "2025-10-28T10:30:00",
     *         "updatedAt": "2025-10-28T10:30:00"
     *       }
     *     ],
     *     "page": 0,
     *     "size": 20,
     *     "totalElements": 1,
     *     "totalPages": 1,
     *     "first": true,
     *     "last": true
     *   }
     * }
     * }</pre>
     *
     * @param searchRequest 검색 조건 (page 기반)
     * @return 검색된 Example 목록 (Page 형식, 200 OK)
     */
    @GetMapping("${api.endpoints.example.admin-search}")
    public ResponseEntity<ApiResponse<ExamplePageApiResponse>> searchExamplesByPage(
            @Valid @ModelAttribute ExampleSearchApiRequest searchRequest) {
        var query = exampleApiMapper.toSearchQuery(searchRequest);
        var appPageResponse = searchExampleQueryService.search(query);
        var apiPageResponse = exampleApiMapper.toPageApiResponse(appPageResponse);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiPageResponse));
    }

}
