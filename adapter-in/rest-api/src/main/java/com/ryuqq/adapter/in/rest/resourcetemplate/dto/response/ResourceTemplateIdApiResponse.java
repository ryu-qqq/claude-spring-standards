package com.ryuqq.adapter.in.rest.resourcetemplate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ResourceTemplateIdApiResponse - ResourceTemplate ID API Response
 *
 * <p>ResourceTemplate 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param resourceTemplateId 생성된 ResourceTemplate ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "리소스 템플릿 ID 응답 DTO")
public record ResourceTemplateIdApiResponse(
        @Schema(description = "생성된 리소스 템플릿 ID", example = "1") Long resourceTemplateId) {

    /**
     * 팩토리 메서드
     *
     * @param resourceTemplateId ResourceTemplate ID
     * @return ResourceTemplateIdApiResponse
     */
    public static ResourceTemplateIdApiResponse of(Long resourceTemplateId) {
        return new ResourceTemplateIdApiResponse(resourceTemplateId);
    }
}
