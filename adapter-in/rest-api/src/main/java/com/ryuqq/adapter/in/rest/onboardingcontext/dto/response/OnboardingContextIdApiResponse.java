package com.ryuqq.adapter.in.rest.onboardingcontext.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OnboardingContextIdApiResponse - OnboardingContext 생성 결과 API Response
 *
 * <p>OnboardingContext 생성 후 ID를 반환하는 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param id 생성된 OnboardingContext ID
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "OnboardingContext 생성 결과 응답")
public record OnboardingContextIdApiResponse(
        @Schema(description = "생성된 OnboardingContext ID", example = "1") Long id) {

    /**
     * 정적 팩토리 메서드
     *
     * @param id OnboardingContext ID
     * @return OnboardingContextIdApiResponse
     */
    public static OnboardingContextIdApiResponse of(Long id) {
        return new OnboardingContextIdApiResponse(id);
    }
}
