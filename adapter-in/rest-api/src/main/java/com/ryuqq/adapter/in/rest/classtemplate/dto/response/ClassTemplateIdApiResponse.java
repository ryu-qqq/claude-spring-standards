package com.ryuqq.adapter.in.rest.classtemplate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ClassTemplateIdApiResponse - ClassTemplate ID API Response
 *
 * <p>ClassTemplate 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param classTemplateId 생성된 ClassTemplate ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "클래스 템플릿 ID 응답")
public record ClassTemplateIdApiResponse(
        @Schema(description = "생성된 클래스 템플릿 ID", example = "1") Long classTemplateId) {

    /**
     * 팩토리 메서드
     *
     * @param classTemplateId ClassTemplate ID
     * @return ClassTemplateIdApiResponse
     */
    public static ClassTemplateIdApiResponse of(Long classTemplateId) {
        return new ClassTemplateIdApiResponse(classTemplateId);
    }
}
