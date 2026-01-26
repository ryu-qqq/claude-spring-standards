package com.ryuqq.adapter.in.rest.packagestructure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PackageStructureIdApiResponse - PackageStructure ID API Response
 *
 * <p>PackageStructure 생성 후 ID만 반환하는 API Response입니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-014: ApiResponse 원시타입 래핑 금지 -> Response DTO 사용.
 *
 * @param packageStructureId 생성된 PackageStructure ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "PackageStructure ID 응답 DTO")
public record PackageStructureIdApiResponse(
        @Schema(description = "생성된 PackageStructure ID", example = "1") Long packageStructureId) {

    /**
     * 팩토리 메서드
     *
     * @param packageStructureId PackageStructure ID
     * @return PackageStructureIdApiResponse
     */
    public static PackageStructureIdApiResponse of(Long packageStructureId) {
        return new PackageStructureIdApiResponse(packageStructureId);
    }
}
