package com.ryuqq.adapter.in.rest.auth.paths;

/**
 * API Paths Constants
 *
 * <p>API 엔드포인트 경로 상수 정의
 *
 * <p>설계 원칙:
 *
 * <ul>
 *   <li>final 클래스, private 생성자 (인스턴스화 방지)
 *   <li>static final String 필드로 경로 정의
 *   <li>버전별 Nested Class로 그룹화
 * </ul>
 *
 * <p>사용 위치:
 *
 * <ul>
 *   <li>Controller - {@code @RequestMapping} 경로 지정
 *   <li>SecurityConfig - 공개 엔드포인트 설정
 *   <li>테스트 - API 호출 경로
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SecurityPaths
 */
public final class ApiPaths {

    private ApiPaths() {
        // 인스턴스화 방지
    }

    /** API 버전 Prefix */
    public static final String API_V1 = "/api/v1";

    /**
     * Member API 경로
     *
     * <p>회원 관련 엔드포인트
     */
    public static final class Member {

        private Member() {}

        /** 회원 Base Path */
        public static final String BASE = API_V1 + "/members";

        /** 회원 가입 */
        public static final String REGISTER = BASE;

        /** 회원 조회 (by ID) */
        public static final String BY_ID = BASE + "/{memberId}";

        /** 내 정보 조회 */
        public static final String ME = BASE + "/me";

        /** 비밀번호 재설정 요청 */
        public static final String PASSWORD_RESET = BASE + "/password/reset";

        /** 비밀번호 변경 */
        public static final String PASSWORD_CHANGE = BASE + "/password";
    }

    /**
     * Auth API 경로
     *
     * <p>인증 관련 엔드포인트
     */
    public static final class Auth {

        private Auth() {}

        /** 인증 Base Path */
        public static final String BASE = API_V1 + "/auth";

        /** 로그인 */
        public static final String LOGIN = BASE + "/login";

        /** 로그아웃 */
        public static final String LOGOUT = BASE + "/logout";

        /** 토큰 갱신 */
        public static final String REFRESH = BASE + "/refresh";
    }

    /**
     * Health Check API 경로
     *
     * <p>시스템 상태 확인용 엔드포인트
     */
    public static final class Health {

        private Health() {}

        /** Health Check */
        public static final String CHECK = API_V1 + "/health";
    }

    /**
     * Swagger/OpenAPI 경로
     *
     * <p>API 문서 관련 엔드포인트
     */
    public static final class Docs {

        private Docs() {}

        /** Swagger UI */
        public static final String SWAGGER_UI = "/swagger-ui/**";

        /** OpenAPI Docs */
        public static final String OPENAPI = "/v3/api-docs/**";
    }

    /**
     * Actuator 경로
     *
     * <p>Spring Boot Actuator 엔드포인트
     */
    public static final class Actuator {

        private Actuator() {}

        /** Actuator Base */
        public static final String BASE = "/actuator/**";
    }

    /**
     * OAuth2 경로
     *
     * <p>OAuth2 로그인 관련 엔드포인트
     */
    public static final class OAuth2 {

        private OAuth2() {}

        /** OAuth2 Authorization */
        public static final String OAUTH2 = "/oauth2/**";

        /** OAuth2 Login Callback */
        public static final String LOGIN_CALLBACK = "/login/oauth2/**";
    }
}
