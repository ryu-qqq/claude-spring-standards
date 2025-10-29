package com.ryuqq.application.example.port.in;

import com.ryuqq.application.common.dto.response.PageResponse;
import com.ryuqq.application.common.dto.response.SliceResponse;
import com.ryuqq.application.example.dto.query.SearchExampleQuery;
import com.ryuqq.application.example.dto.response.ExampleDetailResponse;

/**
 * SearchExampleQueryService - Example 검색 Query Service
 *
 * <p>CQRS 패턴의 Query 처리를 담당하는 Inbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>Example 검색 요청 처리</li>
 *   <li>페이지네이션 지원 (Offset 또는 Cursor 기반)</li>
 *   <li>검색 결과 반환</li>
 * </ul>
 *
 * <p><strong>페이지네이션 타입별 사용법:</strong></p>
 * <ul>
 *   <li>Offset 기반: {@code search(query)} - PageResponse 반환</li>
 *   <li>Cursor 기반: {@code searchByCursor(query)} - SliceResponse 반환</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * @RestController
 * public class ExampleController {
 *     private final SearchExampleQueryService searchExampleQueryService;
 *
 *     // Offset 기반 페이지네이션
 *     @GetMapping("/api/v1/admin/examples/search")
 *     public ResponseEntity<ApiResponse<PageResponse<ExampleDetailResponse>>> search(
 *             @Valid @ModelAttribute ExampleSearchRequest request) {
 *         SearchExampleQuery query = exampleMapper.toSearchQuery(request);
 *         PageResponse<ExampleDetailResponse> response = searchExampleQueryService.search(query);
 *         return ResponseEntity.ok(ApiResponse.ofSuccess(response));
 *     }
 *
 *     // Cursor 기반 페이지네이션
 *     @GetMapping("/api/v1/examples")
 *     public ResponseEntity<ApiResponse<SliceResponse<ExampleDetailResponse>>> searchByCursor(
 *             @Valid @ModelAttribute ExampleSearchRequest request) {
 *         SearchExampleQuery query = exampleMapper.toSearchQuery(request);
 *         SliceResponse<ExampleDetailResponse> response = searchExampleQueryService.searchByCursor(query);
 *         return ResponseEntity.ok(ApiResponse.ofSuccess(response));
 *     }
 * }
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
public interface SearchExampleQueryService {

    /**
     * Example 검색 (Offset 기반 페이지네이션)
     *
     * <p>전통적인 페이지 번호 기반 검색입니다.</p>
     * <p>전체 개수를 포함하므로 COUNT 쿼리가 필요합니다.</p>
     *
     * @param query 검색 쿼리 (PaginationType.OFFSET)
     * @return Example 검색 결과 (페이지네이션 포함)
     */
    PageResponse<ExampleDetailResponse> search(SearchExampleQuery query);

    /**
     * Example 검색 (Cursor 기반 페이지네이션)
     *
     * <p>커서 기반 검색으로 무한 스크롤에 적합합니다.</p>
     * <p>전체 개수를 세지 않으므로 COUNT 쿼리가 불필요하여 고성능입니다.</p>
     *
     * @param query 검색 쿼리 (PaginationType.CURSOR)
     * @return Example 검색 결과 (슬라이스 포함)
     */
    SliceResponse<ExampleDetailResponse> searchByCursor(SearchExampleQuery query);
}
