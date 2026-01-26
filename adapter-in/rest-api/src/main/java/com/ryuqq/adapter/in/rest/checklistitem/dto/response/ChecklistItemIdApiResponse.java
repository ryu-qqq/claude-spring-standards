package com.ryuqq.adapter.in.rest.checklistitem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ChecklistItemIdApiResponse - ChecklistItem ID API Response
 *
 * <p>ChecklistItem 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param id 생성된 ChecklistItem ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "체크리스트 항목 ID 응답 DTO")
public record ChecklistItemIdApiResponse(
        @Schema(description = "생성된 체크리스트 항목 ID", example = "1") Long id) {

    /**
     * 팩토리 메서드
     *
     * @param id ChecklistItem ID
     * @return ChecklistItemIdApiResponse
     */
    public static ChecklistItemIdApiResponse of(Long id) {
        return new ChecklistItemIdApiResponse(id);
    }
}
