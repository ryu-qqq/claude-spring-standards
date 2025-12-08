package com.ryuqq.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Security Properties (통합 설정).
 *
 * <p>Spring Security 관련 외부 설정을 통합 관리하는 Properties 클래스입니다.
 *
 * <p>설정 항목:
 *
 * <ul>
 *   <li>gateway: Gateway Only 인증 설정 (enabled, 헤더명)
 *   <li>jwt: JWT 토큰 설정 (secret, 만료 시간) - Gateway 모드에서는 미사용
 *   <li>cookie: 쿠키 설정 (domain, secure, sameSite)
 *   <li>cors: CORS 설정 (허용 도메인, 메서드, 헤더 등)
 *   <li>oauth2: OAuth2 클라이언트 설정 (소셜 로그인)
 * </ul>
 *
 * <p>사용 예시 (rest-api.yml):
 *
 * <pre>{@code
 * security:
 *   gateway:
 *     enabled: true
 *     user-id-header: X-User-Id
 *     user-roles-header: X-User-Roles
 *   cookie:
 *     domain: localhost
 *     secure: false
 *     same-site: lax
 *   cors:
 *     allowed-origins:
 *       - http://localhost:3000
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 * @see GatewayProperties
 * @see JwtProperties
 * @see CookieProperties
 * @see CorsProperties
 * @see OAuth2Properties
 */
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    @NestedConfigurationProperty
    private GatewayProperties gateway = new GatewayProperties();

    @NestedConfigurationProperty
    private JwtProperties jwt = new JwtProperties();

    @NestedConfigurationProperty
    private CookieProperties cookie = new CookieProperties();

    @NestedConfigurationProperty
    private CorsProperties cors = new CorsProperties();

    @NestedConfigurationProperty
    private OAuth2Properties oauth2 = new OAuth2Properties();

    public GatewayProperties getGateway() {
        return gateway;
    }

    public void setGateway(GatewayProperties gateway) {
        this.gateway = gateway;
    }

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

    public OAuth2Properties getOauth2() {
        return oauth2;
    }

    public void setOauth2(OAuth2Properties oauth2) {
        this.oauth2 = oauth2;
    }

    /**
     * Gateway 모드가 활성화되어 있는지 확인합니다.
     *
     * @return gateway.enabled가 true면 true
     */
    public boolean isGatewayMode() {
        return gateway != null && gateway.isEnabled();
    }

    /**
     * OAuth2가 활성화되어 있는지 확인합니다.
     *
     * @return oauth2.enabled가 true면 true
     */
    public boolean isOAuth2Enabled() {
        return oauth2 != null && oauth2.isEnabled();
    }
}
