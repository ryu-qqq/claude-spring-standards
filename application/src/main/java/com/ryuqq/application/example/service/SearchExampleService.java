package com.ryuqq.application.example.service;

import java.util.List;
import java.util.stream.Collectors;

import com.ryuqq.application.common.dto.response.PageResponse;
import com.ryuqq.application.common.dto.response.SliceResponse;
import com.ryuqq.application.example.assembler.ExampleAssembler;
import com.ryuqq.application.example.dto.query.SearchExampleQuery;
import com.ryuqq.application.example.dto.response.ExampleDetailResponse;
import com.ryuqq.application.example.port.in.SearchExampleQueryService;
import com.ryuqq.application.example.port.out.ExampleQueryOutPort;
import com.ryuqq.domain.example.ExampleDomain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SearchExampleService - Example 검색 서비스
 *
 * <p>CQRS 패턴의 Query 처리를 담당하는 Application Service입니다.</p>
 * <p>Offset 기반과 Cursor 기반 두 가지 페이징 전략을 지원합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Example 목록 조회 (검색, 필터링)</li>
 *   <li>Offset 기반 페이징 (관리자 페이지용)</li>
 *   <li>Cursor 기반 페이징 (무한 스크롤용)</li>
 *   <li>읽기 전용 트랜잭션 관리</li>
 * </ul>
 *
 * <p><strong>페이지네이션 전략:</strong></p>
 * <ul>
 *   <li><strong>Offset 기반</strong>: 페이지 번호, 전체 개수 제공 (COUNT 쿼리 필요)</li>
 *   <li><strong>Cursor 기반</strong>: 다음 페이지 존재 여부만 제공 (COUNT 쿼리 불필요, 성능 우수)</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Service
public class SearchExampleService implements SearchExampleQueryService {

    private final ExampleAssembler exampleAssembler;
    private final ExampleQueryOutPort queryOutPort;

    /**
     * SearchExampleService 생성자
     *
     * <p>Constructor Injection을 통해 의존성을 주입받습니다.</p>
     *
     * @param exampleAssembler Domain-DTO 변환 Assembler
     * @param queryOutPort Example 조회 Query OutPort
     */
    public SearchExampleService(
            ExampleAssembler exampleAssembler,
            ExampleQueryOutPort queryOutPort) {
        this.exampleAssembler = exampleAssembler;
        this.queryOutPort = queryOutPort;
    }

    /**
     * Example 검색 (Offset 기반 페이징)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>QueryOutPort로 목록 조회 (page, size 기반)</li>
     *   <li>QueryOutPort로 전체 개수 조회 (COUNT 쿼리)</li>
     *   <li>Domain 목록 → DetailResponse 목록 변환</li>
     *   <li>PageResponse 생성 (totalPages, first, last 계산)</li>
     * </ol>
     *
     * <p><strong>사용 시나리오:</strong></p>
     * <ul>
     *   <li>관리자 페이지 (페이지 번호 네비게이션)</li>
     *   <li>전체 개수가 필요한 경우</li>
     *   <li>임의의 페이지로 점프가 필요한 경우</li>
     * </ul>
     *
     * <p><strong>성능 고려사항:</strong></p>
     * <ul>
     *   <li>COUNT 쿼리 실행으로 인한 성능 비용</li>
     *   <li>대량 데이터에서는 Cursor 기반 권장</li>
     * </ul>
     *
     * @param query 검색 조건 (page, size, 필터, 정렬)
     * @return Example 페이지 응답
     */
    @Transactional(readOnly = true)
    @Override
    public PageResponse<ExampleDetailResponse> search(SearchExampleQuery query) {
        // 1. QueryOutPort로 목록 조회
        List<ExampleDomain> domains = queryOutPort.search(query);

        // 2. 총 개수 조회 (COUNT 쿼리)
        long totalElements = queryOutPort.countBy(query);

        // 3. Domain 목록 → DetailResponse 목록 변환
        List<ExampleDetailResponse> content = domains.stream()
                .map(exampleAssembler::toDetailResponse)
                .collect(Collectors.toList());

        // 4. PageResponse 생성
        int totalPages = (int) Math.ceil((double) totalElements / query.size());
        boolean first = query.page() == 0;
        boolean last = query.page() >= totalPages - 1 || totalElements == 0;

        return PageResponse.of(
                content,
                query.page(),
                query.size(),
                totalElements,
                totalPages,
                first,
                last
        );
    }

    /**
     * Example 검색 (Cursor 기반 페이징)
     *
     * <p><strong>처리 흐름:</strong></p>
     * <ol>
     *   <li>QueryOutPort로 size+1개 조회 (hasNext 판단용)</li>
     *   <li>결과가 size+1개면 hasNext=true, 마지막 1개 제거</li>
     *   <li>Domain 목록 → DetailResponse 목록 변환</li>
     *   <li>nextCursor 계산 (마지막 항목의 ID)</li>
     *   <li>SliceResponse 생성</li>
     * </ol>
     *
     * <p><strong>hasNext 판단 로직:</strong></p>
     * <pre>{@code
     * // Persistence Layer에서 size+1개 조회
     * // size=20이면 21개를 조회
     * List<ExampleDomain> results = queryOutPort.searchByCursor(query);
     *
     * // 21개가 조회되면 hasNext=true
     * boolean hasNext = results.size() > query.size();
     * if (hasNext) {
     *     // 마지막 1개 제거하여 20개만 반환
     *     results = results.subList(0, query.size());
     * }
     * }</pre>
     *
     * <p><strong>사용 시나리오:</strong></p>
     * <ul>
     *   <li>무한 스크롤 UI</li>
     *   <li>실시간 피드, 타임라인</li>
     *   <li>대량 데이터 (COUNT 쿼리 부담 큰 경우)</li>
     * </ul>
     *
     * <p><strong>성능 이점:</strong></p>
     * <ul>
     *   <li>COUNT 쿼리 불필요 (성능 향상)</li>
     *   <li>인덱스 활용 효율적</li>
     *   <li>실시간 데이터에 적합</li>
     * </ul>
     *
     * @param query 검색 조건 (cursor, size, 필터, 정렬)
     * @return Example 슬라이스 응답
     */
    @Transactional(readOnly = true)
    @Override
    public SliceResponse<ExampleDetailResponse> searchByCursor(SearchExampleQuery query) {
        // 1. QueryOutPort로 size+1개 조회 (hasNext 판단용)
        List<ExampleDomain> domains = queryOutPort.searchByCursor(query);

        // 2. hasNext 판단 (size+1개 조회되었는지 확인)
        boolean hasNext = domains.size() > query.size();
        if (hasNext) {
            // 마지막 1개 제거하여 실제 size만큼만 반환
            domains = domains.subList(0, query.size());
        }

        // 3. Domain 목록 → DetailResponse 목록 변환
        List<ExampleDetailResponse> content = domains.stream()
                .map(exampleAssembler::toDetailResponse)
                .collect(Collectors.toList());

        // 4. nextCursor 계산 (마지막 항목의 ID를 cursor로 사용)
        String nextCursor = hasNext && !content.isEmpty()
                ? String.valueOf(content.get(content.size() - 1).id())
                : null;

        // 5. SliceResponse 생성
        return SliceResponse.of(
                content,
                query.size(),
                hasNext,
                nextCursor
        );
    }
}
