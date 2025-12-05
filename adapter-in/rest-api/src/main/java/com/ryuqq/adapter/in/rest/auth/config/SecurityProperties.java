package com.ryuqq.adapter.in.rest.auth.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security Properties
 *
 * <p>Spring Security 관련 외부 설정을 관리하는 Properties 클래스
 *
 * <p>설정 항목:
 *
 * <ul>
 *   <li>jwt: JWT 토큰 관련 설정 (secret, 만료 시간)
 *   <li>cookie: 쿠키 관련 설정 (domain, secure, sameSite)
 *   <li>cors: CORS 관련 설정 (허용 도메인, 메서드, 헤더 등)
 * </ul>
 *
 * <p>사용 예시 (application.yml):
 *
 * <pre>{@code
 * security:
 *   jwt:
 *     secret: ${JWT_SECRET:your-secret-key}
 *     access-token-expiration: 3600
 *     refresh-token-expiration: 604800
 *   cookie:
 *     domain: localhost
 *     secure: false
 *     same-site: lax
 *   cors:
 *     allowed-origins:
 *       - http://localhost:3000
 *     allowed-methods:
 *       - GET
 *       - POST
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private JwtProperties jwt = new JwtProperties();
    private CookieProperties cookie = new CookieProperties();
    private CorsProperties cors = new CorsProperties();

    public JwtProperties getJwt() {
        return jwt;
    }

    public void setJwt(JwtProperties jwt) {
        this.jwt = jwt;
    }

    public CookieProperties getCookie() {
        return cookie;
    }

    public void setCookie(CookieProperties cookie) {
        this.cookie = cookie;
    }

    public CorsProperties getCors() {
        return cors;
    }

    public void setCors(CorsProperties cors) {
        this.cors = cors;
    }

    /** JWT 관련 설정 */
    public static class JwtProperties {

        private String secret = "your-256-bit-secret-key-for-jwt-signing-must-be-changed";
        private long accessTokenExpiration = 3600L;
        private long refreshTokenExpiration = 604800L;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getAccessTokenExpiration() {
            return accessTokenExpiration;
        }

        public void setAccessTokenExpiration(long accessTokenExpiration) {
            this.accessTokenExpiration = accessTokenExpiration;
        }

        public long getRefreshTokenExpiration() {
            return refreshTokenExpiration;
        }

        public void setRefreshTokenExpiration(long refreshTokenExpiration) {
            this.refreshTokenExpiration = refreshTokenExpiration;
        }
    }

    /**
     * Cookie 관련 설정
     *
     * <p>JWT 토큰 쿠키의 보안 속성 설정
     */
    public static class CookieProperties {

        private String domain = "localhost";
        private boolean secure = false;
        private String sameSite = "lax";

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public boolean isSecure() {
            return secure;
        }

        public void setSecure(boolean secure) {
            this.secure = secure;
        }

        public String getSameSite() {
            return sameSite;
        }

        public void setSameSite(String sameSite) {
            this.sameSite = sameSite;
        }

        /**
         * localhost가 아닌 커스텀 도메인 여부
         *
         * @return 커스텀 도메인이면 true
         */
        public boolean hasCustomDomain() {
            return domain != null && !"localhost".equals(domain);
        }
    }

    /** CORS 관련 설정 */
    public static class CorsProperties {

        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>();
        private List<String> allowedHeaders = new ArrayList<>();
        private List<String> exposedHeaders = new ArrayList<>();
        private boolean allowCredentials = true;

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public List<String> getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public List<String> getExposedHeaders() {
            return exposedHeaders;
        }

        public void setExposedHeaders(List<String> exposedHeaders) {
            this.exposedHeaders = exposedHeaders;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }
    }
}
