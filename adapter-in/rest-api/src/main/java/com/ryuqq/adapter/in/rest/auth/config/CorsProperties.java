package com.ryuqq.adapter.in.rest.auth.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CORS 설정.
 *
 * <p>Cross-Origin Resource Sharing 정책을 설정합니다.
 *
 * <p>설정 예시 (rest-api-local.yml):
 *
 * <pre>{@code
 * security:
 *   cors:
 *     allowed-origins:
 *       - http://localhost:3000
 *       - http://localhost:8080
 *     allowed-methods:
 *       - GET
 *       - POST
 *       - PUT
 *       - DELETE
 *     allowed-headers:
 *       - "*"
 *     exposed-headers:
 *       - Authorization
 *       - Set-Cookie
 *     allow-credentials: true
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "security.cors")
public class CorsProperties {

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

    /**
     * CORS 설정이 활성화되어 있는지 확인합니다.
     *
     * @return allowed-origins가 하나 이상 설정되어 있으면 true
     */
    public boolean isEnabled() {
        return allowedOrigins != null && !allowedOrigins.isEmpty();
    }
}
