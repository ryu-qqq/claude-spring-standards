package com.ryuqq.adapter.in.rest.onboardingcontext;

import com.ryuqq.adapter.in.rest.common.ApiPaths;

/**
 * OnboardingContextApiEndpoints - OnboardingContext API Endpoint 상수
 *
 * <p>OnboardingContext 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/templates/onboarding-contexts
 *   ├── GET    /                           # 목록 조회
 *   ├── POST   /                           # 생성
 *   └── PUT    /{onboardingContextId}      # 수정
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class OnboardingContextApiEndpoints {

    private OnboardingContextApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Standards API 기본 경로 */
    public static final String STANDARDS_BASE = ApiPaths.SERVICE_BASE;

    // ============================================
    // OnboardingContext Endpoints
    // ============================================

    /** OnboardingContext 기본 경로 */
    public static final String ONBOARDING_CONTEXTS = STANDARDS_BASE + "/onboarding-contexts";

    /** OnboardingContext 단일 경로 */
    public static final String ONBOARDING_CONTEXT_DETAIL =
            ONBOARDING_CONTEXTS + "/{onboardingContextId}";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{onboardingContextId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** OnboardingContext ID 경로 변수명 */
    public static final String PATH_ONBOARDING_CONTEXT_ID = "onboardingContextId";
}
