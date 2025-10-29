package com.ryuqq.application.example.dto.query;

/**
 * GetExampleQuery - Example 단건 조회 쿼리
 *
 * <p>CQRS 패턴의 Query 역할을 수행합니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>ID로 Example 단건 조회</li>
 *   <li>상세 정보 조회</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * GetExampleQuery query = GetExampleQuery.of(1L);
 * ExampleDetailResponse response = getExampleQueryService.getById(query);
 * }</pre>
 *
 * @param id Example ID
 * @author windsurf
 * @since 1.0.0
 */
public record GetExampleQuery(
    Long id
) {

    /**
     * GetExampleQuery 생성
     *
     * @param id Example ID
     * @return GetExampleQuery
     */
    public static GetExampleQuery of(Long id) {
        return new GetExampleQuery(id);
    }
}
