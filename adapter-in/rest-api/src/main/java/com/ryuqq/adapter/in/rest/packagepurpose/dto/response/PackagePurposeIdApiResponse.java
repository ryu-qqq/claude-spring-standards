package com.ryuqq.adapter.in.rest.packagepurpose.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PackagePurposeIdApiResponse - PackagePurpose ID 응답 DTO
 *
 * <p>생성된 PackagePurpose의 ID를 반환합니다.
 *
 * <p>DTO-001: API Response DTO는 Record로 정의.
 *
 * <p>DTO-004: *ApiResponse 네이밍.
 *
 * @param packagePurposeId 생성된 패키지 목적 ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "PackagePurpose ID 응답")
public record PackagePurposeIdApiResponse(
        @Schema(description = "생성된 패키지 목적 ID", example = "1") Long packagePurposeId) {

    /**
     * ID로부터 응답 객체 생성
     *
     * @param id 패키지 목적 ID
     * @return PackagePurposeIdApiResponse
     */
    public static PackagePurposeIdApiResponse of(Long id) {
        return new PackagePurposeIdApiResponse(id);
    }
}
