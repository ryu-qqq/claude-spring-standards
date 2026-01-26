package com.ryuqq.adapter.in.rest.convention.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ConventionIdApiResponse - Convention 생성 결과 API Response
 *
 * <p>Convention 생성 후 ID를 반환하는 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param id 생성된 Convention ID
 * @author ryu-qqq
 */
@Schema(description = "컨벤션 생성 결과")
public record ConventionIdApiResponse(@Schema(description = "생성된 컨벤션 ID", example = "1") Long id) {

    /**
     * 정적 팩토리 메서드
     *
     * @param id Convention ID
     * @return ConventionIdApiResponse
     */
    public static ConventionIdApiResponse of(Long id) {
        return new ConventionIdApiResponse(id);
    }
}
