package com.ryuqq.adapter.in.rest.classtypecategory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ClassTypeCategoryIdApiResponse - ClassTypeCategory 생성 결과 API Response
 *
 * <p>ClassTypeCategory 생성 후 ID를 반환하는 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param id 생성된 ClassTypeCategory ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ClassTypeCategory 생성 결과 응답")
public record ClassTypeCategoryIdApiResponse(
        @Schema(description = "생성된 ClassTypeCategory ID", example = "1") Long id) {

    /**
     * 정적 팩토리 메서드
     *
     * @param id ClassTypeCategory ID
     * @return ClassTypeCategoryIdApiResponse
     */
    public static ClassTypeCategoryIdApiResponse of(Long id) {
        return new ClassTypeCategoryIdApiResponse(id);
    }
}
