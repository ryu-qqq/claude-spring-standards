package com.ryuqq.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cookie 설정.
 *
 * <p>JWT 토큰 쿠키 및 기타 보안 관련 쿠키의 속성을 설정합니다.
 *
 * <p>설정 예시 (rest-api.yml):
 *
 * <pre>{@code
 * security:
 *   cookie:
 *     domain: localhost
 *     secure: false
 *     same-site: lax
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "security.cookie")
public class CookieProperties {

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
     * localhost가 아닌 커스텀 도메인 여부.
     *
     * @return 커스텀 도메인이면 true
     */
    public boolean hasCustomDomain() {
        return domain != null && !"localhost".equals(domain);
    }
}
