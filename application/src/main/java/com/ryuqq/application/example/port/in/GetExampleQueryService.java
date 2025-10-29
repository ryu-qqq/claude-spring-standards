package com.ryuqq.application.example.port.in;

import com.ryuqq.application.example.dto.query.GetExampleQuery;
import com.ryuqq.application.example.dto.response.ExampleDetailResponse;

/**
 * GetExampleQueryService - Example 단건 조회 Query Service
 *
 * <p>CQRS 패턴의 Query 처리를 담당하는 Inbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>Example 단건 조회 요청 처리</li>
 *   <li>상세 정보 반환</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * @RestController
 * public class ExampleController {
 *     private final GetExampleQueryService getExampleQueryService;
 *
 *     @GetMapping("/api/v1/examples/{id}")
 *     public ResponseEntity<ApiResponse<ExampleDetailResponse>> get(@PathVariable Long id) {
 *         GetExampleQuery query = GetExampleQuery.of(id);
 *         ExampleDetailResponse response = getExampleQueryService.getById(query);
 *         return ResponseEntity.ok(ApiResponse.ofSuccess(response));
 *     }
 * }
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
public interface GetExampleQueryService {

    /**
     * ID로 Example 단건 조회
     *
     * @param query 조회 쿼리
     * @return Example 상세 정보
     */
    ExampleDetailResponse getById(GetExampleQuery query);
}
