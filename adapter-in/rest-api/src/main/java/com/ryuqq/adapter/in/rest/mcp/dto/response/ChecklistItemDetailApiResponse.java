package com.ryuqq.adapter.in.rest.mcp.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ChecklistItemDetailApiResponse - 체크리스트 항목 상세 정보
 *
 * @param checkDescription 체크 설명
 * @param autoCheckable 자동 체크 가능 여부
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "체크리스트 항목 상세 정보")
public record ChecklistItemDetailApiResponse(
        @Schema(description = "체크 설명", example = "Lombok 어노테이션(@Data, @Getter 등)이 사용되지 않았는가?")
                String checkDescription,
        @Schema(description = "자동 체크 가능 여부", example = "true") boolean autoCheckable) {}
