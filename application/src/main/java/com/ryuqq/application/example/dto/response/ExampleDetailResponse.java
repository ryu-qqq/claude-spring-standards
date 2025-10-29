package com.ryuqq.application.example.dto.response;

import java.time.LocalDateTime;

/**
 * ExampleDetailResponse - Example 상세 조회 응답
 *
 * <p>Application Layer의 상세 조회용 응답 DTO입니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>Example 단건 조회 결과</li>
 *   <li>Example 검색 결과 (목록)</li>
 *   <li>생성/수정 후 상세 정보 반환</li>
 * </ul>
 *
 * <p><strong>vs ExampleResponse:</strong></p>
 * <ul>
 *   <li>ExampleResponse: 단순 생성 결과 (id, message만 포함)</li>
 *   <li>ExampleDetailResponse: 상세 정보 (status, createdAt, updatedAt 포함)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * // 단건 조회
 * GetExampleQuery query = GetExampleQuery.of(1L);
 * ExampleDetailResponse response = getExampleQueryService.getById(query);
 *
 * // 검색 (페이지네이션)
 * SearchExampleQuery query = SearchExampleQuery.ofOffset(...);
 * PageResponse<ExampleDetailResponse> pageResponse = searchExampleQueryService.search(query);
 * }</pre>
 *
 * @param id Example ID
 * @param message 메시지 내용
 * @param status 상태 (ACTIVE, INACTIVE, DELETED)
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author windsurf
 * @since 1.0.0
 */
public record ExampleDetailResponse(
    Long id,
    String message,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    /**
     * ExampleDetailResponse 생성
     *
     * @param id Example ID
     * @param message 메시지 내용
     * @param status 상태
     * @param createdAt 생성일시
     * @param updatedAt 수정일시
     * @return ExampleDetailResponse
     */
    public static ExampleDetailResponse of(
        Long id,
        String message,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new ExampleDetailResponse(id, message, status, createdAt, updatedAt);
    }
}
