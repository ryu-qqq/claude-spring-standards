package com.ryuqq.adapter.in.rest.archunittest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ArchUnitTestIdApiResponse - ArchUnitTest ID API Response
 *
 * <p>ArchUnitTest 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param archUnitTestId 생성된 ArchUnitTest ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "ArchUnitTest 생성 응답")
public record ArchUnitTestIdApiResponse(
        @Schema(description = "생성된 ArchUnitTest ID", example = "1") Long archUnitTestId) {

    /**
     * 팩토리 메서드
     *
     * @param archUnitTestId ArchUnitTest ID
     * @return ArchUnitTestIdApiResponse
     */
    public static ArchUnitTestIdApiResponse of(Long archUnitTestId) {
        return new ArchUnitTestIdApiResponse(archUnitTestId);
    }
}
