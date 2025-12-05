package com.ryuqq.adapter.in.rest.auth.paths;

import org.springframework.http.HttpMethod;

/**
 * Security Paths Constants
 *
 * <p>Spring Security 설정에서 사용할 경로 상수 및 공개 엔드포인트 정의
 *
 * <p>설계 원칙:
 *
 * <ul>
 *   <li>final 클래스, private 생성자 (인스턴스화 방지)
 *   <li>공개 엔드포인트는 {@link PublicEndpoint} 배열로 관리
 *   <li>ApiPaths를 참조하여 일관성 유지
 * </ul>
 *
 * <p>사용 위치:
 *
 * <ul>
 *   <li>SecurityConfig - HttpSecurity 설정
 *   <li>테스트 - 인증 우회 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see ApiPaths
 */
public final class SecurityPaths {

    private SecurityPaths() {
        // 인스턴스화 방지
    }

    /**
     * 공개 엔드포인트 목록
     *
     * <p>인증 없이 접근 가능한 엔드포인트
     */
    public static final PublicEndpoint[] PUBLIC_ENDPOINTS = {
        // 회원 관련 (인증 불필요)
        PublicEndpoint.of(HttpMethod.POST, ApiPaths.Member.REGISTER),
        PublicEndpoint.of(HttpMethod.POST, ApiPaths.Member.PASSWORD_RESET),

        // 인증 관련
        PublicEndpoint.of(HttpMethod.POST, ApiPaths.Auth.LOGIN),
        PublicEndpoint.of(HttpMethod.POST, ApiPaths.Auth.REFRESH),

        // OAuth2 (모든 메서드)
        PublicEndpoint.of(null, ApiPaths.OAuth2.OAUTH2),
        PublicEndpoint.of(null, ApiPaths.OAuth2.LOGIN_CALLBACK),

        // Swagger/OpenAPI
        PublicEndpoint.of(null, ApiPaths.Docs.SWAGGER_UI),
        PublicEndpoint.of(null, ApiPaths.Docs.OPENAPI),

        // Actuator (헬스체크)
        PublicEndpoint.of(null, ApiPaths.Actuator.BASE),

        // Health Check
        PublicEndpoint.of(null, ApiPaths.Health.CHECK)
    };

    /**
     * 공개 엔드포인트 정의
     *
     * <p>HTTP 메서드와 패턴을 조합하여 공개 엔드포인트 정의
     *
     * <p>method가 null이면 모든 HTTP 메서드 허용
     */
    public static final class PublicEndpoint {

        private final HttpMethod method;
        private final String pattern;

        private PublicEndpoint(HttpMethod method, String pattern) {
            this.method = method;
            this.pattern = pattern;
        }

        /**
         * PublicEndpoint 생성
         *
         * @param method HTTP 메서드 (null이면 모든 메서드 허용)
         * @param pattern URL 패턴
         * @return PublicEndpoint 인스턴스
         */
        public static PublicEndpoint of(HttpMethod method, String pattern) {
            return new PublicEndpoint(method, pattern);
        }

        /**
         * HTTP 메서드 반환
         *
         * @return HTTP 메서드 (null이면 모든 메서드)
         */
        public HttpMethod getMethod() {
            return method;
        }

        /**
         * URL 패턴 반환
         *
         * @return URL 패턴
         */
        public String getPattern() {
            return pattern;
        }

        /**
         * HTTP 메서드 지정 여부
         *
         * @return 메서드가 지정되었으면 true
         */
        public boolean hasMethod() {
            return method != null;
        }
    }
}
