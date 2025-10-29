package com.ryuqq.application.example.dto.query;

import java.time.LocalDateTime;

/**
 * SearchExampleQuery - Example 검색 쿼리
 *
 * <p>CQRS 패턴의 Query 역할을 수행합니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>복잡한 조건으로 Example 검색</li>
 *   <li>페이지네이션 지원 (Offset 또는 Cursor 기반)</li>
 *   <li>정렬 지원</li>
 * </ul>
 *
 * <p><strong>페이지네이션 타입:</strong></p>
 * <ul>
 *   <li>OFFSET: 전통적인 페이지 번호 기반 (page, size) - COUNT 쿼리 필요</li>
 *   <li>CURSOR: 커서 기반 (cursor, size) - COUNT 쿼리 불필요, 고성능</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * // Offset 기반 페이지네이션
 * SearchExampleQuery query = SearchExampleQuery.of(
 *     "Hello",                     // message
 *     "ACTIVE",                    // status
 *     LocalDateTime.now().minusDays(7), // startDate
 *     LocalDateTime.now(),         // endDate
 *     PaginationType.OFFSET,       // paginationType
 *     0,                           // page
 *     null,                        // cursor
 *     20,                          // size
 *     "createdAt",                 // sortBy
 *     "DESC"                       // sortDirection
 * );
 * PageResponse<ExampleDetailResponse> response = searchExampleQueryService.search(query);
 *
 * // Cursor 기반 페이지네이션
 * SearchExampleQuery query = SearchExampleQuery.of(
 *     "Hello",                     // message
 *     "ACTIVE",                    // status
 *     null,                        // startDate
 *     null,                        // endDate
 *     PaginationType.CURSOR,       // paginationType
 *     null,                        // page
 *     "cursor-xyz",                // cursor
 *     20,                          // size
 *     "createdAt",                 // sortBy
 *     "DESC"                       // sortDirection
 * );
 * SliceResponse<ExampleDetailResponse> response = searchExampleQueryService.searchByCursor(query);
 * }</pre>
 *
 * @param message 메시지 검색 조건 (부분 검색)
 * @param status 상태 필터 (ACTIVE, INACTIVE, DELETED)
 * @param startDate 생성일 시작 범위
 * @param endDate 생성일 종료 범위
 * @param paginationType 페이지네이션 타입 (OFFSET, CURSOR)
 * @param page 페이지 번호 (OFFSET 타입 시 사용, 0부터 시작)
 * @param cursor 커서 (CURSOR 타입 시 사용)
 * @param size 페이지/슬라이스 크기 (1-100)
 * @param sortBy 정렬 필드 (createdAt, message, status)
 * @param sortDirection 정렬 방향 (ASC, DESC)
 * @author windsurf
 * @since 1.0.0
 */
public record SearchExampleQuery(
    String message,
    String status,
    LocalDateTime startDate,
    LocalDateTime endDate,
    PaginationType paginationType,
    Integer page,
    String cursor,
    Integer size,
    String sortBy,
    String sortDirection
) {

    /**
     * Compact Constructor - 검증 로직
     *
     * <p>Java 21 Record 패턴: 생성자 파라미터 검증을 Compact Constructor에서 수행합니다.</p>
     *
     * <p><strong>검증 규칙:</strong></p>
     * <ul>
     *   <li>size: 1 이상 100 이하</li>
     *   <li>page: OFFSET 타입일 때 0 이상</li>
     *   <li>cursor: CURSOR 타입일 때 null이 아님</li>
     *   <li>startDate/endDate: startDate가 endDate보다 이전</li>
     * </ul>
     *
     * @throws IllegalArgumentException 검증 실패 시
     */
    public SearchExampleQuery {
        // size 검증
        if (size != null && (size < 1 || size > 100)) {
            throw new IllegalArgumentException("size는 1 이상 100 이하여야 합니다: " + size);
        }

        // OFFSET 타입 검증
        if (paginationType == PaginationType.OFFSET) {
            if (page != null && page < 0) {
                throw new IllegalArgumentException("page는 0 이상이어야 합니다: " + page);
            }
        }

        // CURSOR 타입 검증
        if (paginationType == PaginationType.CURSOR) {
            if (cursor == null) {
                throw new IllegalArgumentException("CURSOR 타입에서는 cursor가 null일 수 없습니다");
            }
        }

        // 날짜 범위 검증
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                "startDate는 endDate보다 이전이어야 합니다: " + startDate + " > " + endDate
            );
        }
    }

    /**
     * 페이지네이션 타입
     */
    public enum PaginationType {
        /** 오프셋 기반 페이지네이션 (page, size) */
        OFFSET,
        /** 커서 기반 페이지네이션 (cursor, size) */
        CURSOR
    }

    /**
     * SearchExampleQuery 생성 (전체 파라미터)
     *
     * @param message 메시지 검색 조건
     * @param status 상태 필터
     * @param startDate 생성일 시작 범위
     * @param endDate 생성일 종료 범위
     * @param paginationType 페이지네이션 타입
     * @param page 페이지 번호
     * @param cursor 커서
     * @param size 페이지/슬라이스 크기
     * @param sortBy 정렬 필드
     * @param sortDirection 정렬 방향
     * @return SearchExampleQuery
     */
    public static SearchExampleQuery of(
        String message,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        PaginationType paginationType,
        Integer page,
        String cursor,
        Integer size,
        String sortBy,
        String sortDirection
    ) {
        return new SearchExampleQuery(
            message,
            status,
            startDate,
            endDate,
            paginationType,
            page,
            cursor,
            size,
            sortBy,
            sortDirection
        );
    }

    /**
     * Offset 기반 SearchExampleQuery 생성
     *
     * @param message 메시지 검색 조건
     * @param status 상태 필터
     * @param startDate 생성일 시작 범위
     * @param endDate 생성일 종료 범위
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sortBy 정렬 필드
     * @param sortDirection 정렬 방향
     * @return SearchExampleQuery (OFFSET 타입)
     */
    public static SearchExampleQuery ofOffset(
        String message,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
    ) {
        return new SearchExampleQuery(
            message,
            status,
            startDate,
            endDate,
            PaginationType.OFFSET,
            page,
            null,  // cursor는 null
            size,
            sortBy,
            sortDirection
        );
    }

    /**
     * Cursor 기반 SearchExampleQuery 생성
     *
     * @param message 메시지 검색 조건
     * @param status 상태 필터
     * @param startDate 생성일 시작 범위
     * @param endDate 생성일 종료 범위
     * @param cursor 커서
     * @param size 슬라이스 크기
     * @param sortBy 정렬 필드
     * @param sortDirection 정렬 방향
     * @return SearchExampleQuery (CURSOR 타입)
     */
    public static SearchExampleQuery ofCursor(
        String message,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String cursor,
        Integer size,
        String sortBy,
        String sortDirection
    ) {
        return new SearchExampleQuery(
            message,
            status,
            startDate,
            endDate,
            PaginationType.CURSOR,
            null,  // page는 null
            cursor,
            size,
            sortBy,
            sortDirection
        );
    }
}
