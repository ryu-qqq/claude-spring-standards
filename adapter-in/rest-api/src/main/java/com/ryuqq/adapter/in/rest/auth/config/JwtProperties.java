package com.ryuqq.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 토큰 설정.
 *
 * <p>JWT 토큰 생성 및 검증에 필요한 설정을 관리합니다.
 *
 * <p><strong>주의:</strong> Gateway Only 아키텍처에서는 이 설정을 사용하지 않습니다.
 * JWT 검증은 Gateway에서 수행됩니다.
 *
 * <p>설정 예시 (rest-api.yml):
 *
 * <pre>{@code
 * security:
 *   jwt:
 *     secret: ${JWT_SECRET:your-secret-key}
 *     access-token-expiration: 3600
 *     refresh-token-expiration: 604800
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

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
