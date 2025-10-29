package com.ryuqq.adapter.in.rest.example.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Example 복잡한 검색 요청 DTO
 *
 * <p>다양한 검색 조건과 두 가지 페이지네이션 방식을 지원합니다.</p>
 *
 * <p><strong>페이지네이션 타입:</strong></p>
 * <ul>
 *   <li>Offset 기반: {@code page + size} 사용 (전통적인 페이지 번호 방식)</li>
 *   <li>Cursor 기반: {@code cursor + size} 사용 (무한 스크롤, 고성능)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * // Offset 기반
 * GET /api/v1/admin/examples/search?page=0&size=20&sortBy=createdAt&sortDirection=DESC
 *
 * // Cursor 기반
 * GET /api/v1/examples?cursor=xyz&size=20&sortBy=createdAt&sortDirection=DESC
 * }</pre>
 *
 * @param message 메시지 검색어 (부분 일치)
 * @param status 상태 필터 (ACTIVE, INACTIVE, DELETED)
 * @param startDate 생성일 시작 범위
 * @param endDate 생성일 종료 범위
 * @param page 페이지 번호 (0부터 시작, Offset 기반 시 사용)
 * @param cursor 커서 (Cursor 기반 시 사용)
 * @param size 페이지/슬라이스 크기 (1-100)
 * @param sortBy 정렬 필드 (createdAt, message, status)
 * @param sortDirection 정렬 방향 (ASC, DESC)
 * @author windsurf
 * @since 1.0.0
 */
public record ExampleSearchApiRequest(
    @Size(max = 100, message = "검색어는 최대 100자까지 입력 가능합니다")
    String message,

    @Pattern(regexp = "ACTIVE|INACTIVE|DELETED", message = "상태는 ACTIVE, INACTIVE, DELETED 중 하나여야 합니다")
    String status,

    LocalDateTime startDate,

    LocalDateTime endDate,

    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    Integer page,

    String cursor,

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    Integer size,

    @Pattern(regexp = "createdAt|message|status", message = "정렬 필드는 createdAt, message, status 중 하나여야 합니다")
    String sortBy,

    @Pattern(regexp = "ASC|DESC", message = "정렬 방향은 ASC 또는 DESC여야 합니다")
    String sortDirection
) {
    /**
     * Compact Constructor - 기본값 설정 및 검증
     */
    public ExampleSearchApiRequest {
        // 페이지네이션 타입 검증 (page와 cursor 동시 제공 불가)
        // NOTE: 기본값 설정 전에 검증해야 원본 값 판별 가능
        boolean hasPage = page != null;
        boolean hasCursor = cursor != null && !cursor.isBlank();
        if (hasPage && hasCursor) {
            throw new IllegalArgumentException("page와 cursor는 동시에 사용할 수 없습니다. 하나만 제공해주세요.");
        }

        // 기본값 설정
        page = (page == null) ? 0 : page;
        size = (size == null) ? 20 : size;
        sortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
        sortDirection = (sortDirection == null || sortDirection.isBlank()) ? "DESC" : sortDirection;

        // 날짜 범위 검증
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다");
        }
    }
}
