package com.ryuqq.adapter.in.rest.layer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * LayerIdApiResponse - Layer 생성 결과 API Response
 *
 * <p>Layer 생성 후 ID를 반환하는 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param id 생성된 Layer ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "Layer 생성 결과 응답")
public record LayerIdApiResponse(@Schema(description = "생성된 Layer ID", example = "1") Long id) {

    /**
     * 정적 팩토리 메서드
     *
     * @param id Layer ID
     * @return LayerIdApiResponse
     */
    public static LayerIdApiResponse of(Long id) {
        return new LayerIdApiResponse(id);
    }
}
