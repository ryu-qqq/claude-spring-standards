package com.ryuqq.adapter.in.rest.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Gateway Only 인증 설정.
 *
 * <p>Gateway에서 JWT 검증 후 헤더로 사용자 정보를 전달하는 패턴에서 사용됩니다.
 *
 * <p>설정 예시 (rest-api.yml):
 *
 * <pre>{@code
 * security:
 *   gateway:
 *     enabled: true
 *     user-id-header: X-User-Id
 *     user-roles-header: X-User-Roles
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 * @see <a href="../../security/gateway-only-architecture.md">Gateway Only Architecture</a>
 */
@ConfigurationProperties(prefix = "security.gateway")
public class GatewayProperties {

    private boolean enabled = true;
    private String userIdHeader = "X-User-Id";
    private String userRolesHeader = "X-User-Roles";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUserIdHeader() {
        return userIdHeader;
    }

    public void setUserIdHeader(String userIdHeader) {
        this.userIdHeader = userIdHeader;
    }

    public String getUserRolesHeader() {
        return userRolesHeader;
    }

    public void setUserRolesHeader(String userRolesHeader) {
        this.userRolesHeader = userRolesHeader;
    }
}
