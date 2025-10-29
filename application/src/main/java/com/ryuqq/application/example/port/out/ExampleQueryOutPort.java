package com.ryuqq.application.example.port.out;

import java.util.List;
import java.util.Optional;

import com.ryuqq.application.example.dto.query.SearchExampleQuery;
import com.ryuqq.domain.example.ExampleDomain;

/**
 * ExampleQueryOutPort - Example Query 작업 OutPort
 *
 * <p>CQRS 패턴의 Query 작업을 Persistence Layer로 전달하는 Outbound Port입니다.</p>
 *
 * <p><strong>책임:</strong></p>
 * <ul>
 *   <li>Example 단건 조회</li>
 *   <li>Example 목록 조회 (검색, 페이징)</li>
 *   <li>읽기 전용 작업 담당</li>
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong></p>
 * <ul>
 *   <li>Application Layer (Core) → Persistence Layer (Adapter) 의존성 역전</li>
 *   <li>Interface는 Application Layer에 위치</li>
 *   <li>Implementation은 Persistence Layer에 위치</li>
 * </ul>
 *
 * <p><strong>CQRS 원칙:</strong></p>
 * <ul>
 *   <li>Command와 Query 책임 분리</li>
 *   <li>Query는 조회만 담당 (데이터 변경 금지)</li>
 *   <li>변경 작업은 ExampleCommandOutPort 사용</li>
 * </ul>
 *
 * <p><strong>페이지네이션 전략:</strong></p>
 * <ul>
 *   <li>Offset 기반: search() + countBy() 조합</li>
 *   <li>Cursor 기반: searchByCursor() (size+1 조회로 hasNext 판단)</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 * @see ExampleCommandOutPort
 */
public interface ExampleQueryOutPort {

    /**
     * Example ID로 단건 조회
     *
     * <p>조회 결과가 없으면 Optional.empty()를 반환합니다.</p>
     *
     * @param id Example ID
     * @return 조회된 Example 도메인 (Optional)
     */
    Optional<ExampleDomain> findById(Long id);

    /**
     * Example 목록 조회 (Offset 기반 페이징)
     *
     * <p><strong>Offset 기반 페이징 전략:</strong></p>
     * <ul>
     *   <li>page 번호와 size로 조회</li>
     *   <li>총 개수는 countBy()로 별도 조회</li>
     *   <li>관리자 페이지 등에 적합</li>
     * </ul>
     *
     * <p><strong>사용 예시:</strong></p>
     * <pre>{@code
     * List<ExampleDomain> content = queryOutPort.search(query);
     * long totalElements = queryOutPort.countBy(query);
     * PageResponse<ExampleDetailResponse> response =
     *     assembler.toPageResponse(content, query, totalElements);
     * }</pre>
     *
     * @param query 검색 조건 (page, size, sortBy, sortDirection 포함)
     * @return Example 도메인 목록
     */
    List<ExampleDomain> search(SearchExampleQuery query);

    /**
     * Example 총 개수 조회 (Offset 기반 페이징용)
     *
     * <p>search() 메서드와 함께 사용하여 전체 페이지 수를 계산합니다.</p>
     *
     * @param query 검색 조건 (필터링 조건만 사용, page/size 무시)
     * @return 검색 조건에 맞는 총 개수
     */
    long countBy(SearchExampleQuery query);

    /**
     * Example 목록 조회 (Cursor 기반 페이징)
     *
     * <p><strong>Cursor 기반 페이징 전략:</strong></p>
     * <ul>
     *   <li>cursor(마지막 ID)와 size로 조회</li>
     *   <li>size+1개 조회하여 hasNext 판단</li>
     *   <li>COUNT 쿼리 없어 대량 데이터에 유리</li>
     *   <li>무한 스크롤 UI에 적합</li>
     * </ul>
     *
     * <p><strong>hasNext 판단 로직:</strong></p>
     * <pre>{@code
     * // Persistence Layer에서 size+1개 조회
     * List<ExampleDomain> results = queryOutPort.searchByCursor(query); // size+1개 반환
     *
     * // Service Layer에서 hasNext 판단
     * boolean hasNext = results.size() > query.size();
     * if (hasNext) {
     *     results = results.subList(0, query.size());  // 마지막 1개 제거
     * }
     * SliceResponse<ExampleDetailResponse> response =
     *     assembler.toSliceResponse(results, query, hasNext);
     * }</pre>
     *
     * @param query 검색 조건 (cursor, size, sortBy, sortDirection 포함)
     * @return Example 도메인 목록 (size+1개 반환, hasNext 판단용)
     */
    List<ExampleDomain> searchByCursor(SearchExampleQuery query);
}
