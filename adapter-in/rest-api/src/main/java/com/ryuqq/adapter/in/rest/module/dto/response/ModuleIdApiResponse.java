package com.ryuqq.adapter.in.rest.module.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModuleIdApiResponse - Module 생성 결과 API Response
 *
 * <p>Module 생성 후 ID를 반환하는 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param moduleId 생성된 Module ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Module 생성 결과 응답")
public record ModuleIdApiResponse(
        @Schema(description = "생성된 Module ID", example = "1") Long moduleId) {

    /**
     * 정적 팩토리 메서드
     *
     * @param moduleId Module ID
     * @return ModuleIdApiResponse
     */
    public static ModuleIdApiResponse of(Long moduleId) {
        return new ModuleIdApiResponse(moduleId);
    }
}
