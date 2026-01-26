package com.ryuqq.adapter.in.rest.techstack.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TechStackIdApiResponse - TechStack 생성 결과 API Response
 *
 * <p>TechStack 생성 후 ID를 반환하는 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param id 생성된 TechStack ID
 * @author ryu-qqq
 */
@Schema(description = "TechStack 생성 결과 응답")
public record TechStackIdApiResponse(
        @Schema(description = "생성된 TechStack ID", example = "1") Long id) {

    /**
     * 정적 팩토리 메서드
     *
     * @param id TechStack ID
     * @return TechStackIdApiResponse
     */
    public static TechStackIdApiResponse of(Long id) {
        return new TechStackIdApiResponse(id);
    }
}
