package com.ryuqq.adapter.in.rest.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * PageApiResponse - 페이지 조회 REST API 응답 DTO (Offset 기반)
 *
 * <p>REST API Layer 전용 응답 DTO입니다.
 *
 * <p><strong>Offset 기반 페이지네이션:</strong>
 *
 * <ul>
 *   <li>전통적인 페이지 번호 기반 페이징
 *   <li>전체 개수와 페이지 정보 제공
 *   <li>관리자 페이지에 적합
 * </ul>
 *
 * <p><strong>응답 형식:</strong>
 *
 * <pre>{@code
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 20,
 *   "totalElements": 100,
 *   "totalPages": 5,
 *   "first": true,
 *   "last": false
 * }
 * }</pre>
 *
 * @param <T> 콘텐츠 타입
 * @param content 현재 페이지의 데이터 목록
 * @param page 현재 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @param totalElements 전체 데이터 개수
 * @param totalPages 전체 페이지 수
 * @param first 첫 페이지 여부
 * @param last 마지막 페이지 여부
 * @author windsurf
 * @since 1.0.0
 */
@Schema(description = "페이지 조회 응답 (Offset 기반)")
public record PageApiResponse<T>(
        @Schema(description = "현재 페이지의 데이터 목록") List<T> content,
        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0") int page,
        @Schema(description = "페이지 크기", example = "20") int size,
        @Schema(description = "전체 데이터 개수", example = "100") long totalElements,
        @Schema(description = "전체 페이지 수", example = "5") int totalPages,
        @Schema(description = "첫 페이지 여부", example = "true") boolean first,
        @Schema(description = "마지막 페이지 여부", example = "false") boolean last) {

    /** Compact Constructor - Defensive Copy */
    public PageApiResponse {
        content = List.copyOf(content);
    }

    /**
     * 개별 파라미터를 사용하여 PageApiResponse 생성
     *
     * <p>REST API Layer에서 Domain Layer 의존 없이 응답을 생성합니다.
     *
     * @param <T> 콘텐츠 타입
     * @param content 콘텐츠 목록
     * @param page 현재 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @param totalElements 전체 데이터 개수
     * @param totalPages 전체 페이지 수
     * @param first 첫 페이지 여부
     * @param last 마지막 페이지 여부
     * @return REST API Layer의 PageApiResponse
     * @author development-team
     * @since 1.0.0
     */
    public static <T> PageApiResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last) {
        return new PageApiResponse<>(content, page, size, totalElements, totalPages, first, last);
    }
}
