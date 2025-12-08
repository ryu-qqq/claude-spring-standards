package com.ryuqq.adapter.in.rest.auth.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OAuth2 클라이언트 설정.
 *
 * <p>OAuth2 소셜 로그인 (Kakao, Google, Naver 등) 관련 설정을 관리합니다.
 *
 * <p><strong>⚠️ 보안 주의:</strong>
 *
 * <ul>
 *   <li>운영 환경에서는 반드시 환경 변수를 사용하세요.
 *   <li>client-secret은 절대 Git에 커밋하지 마세요.
 * </ul>
 *
 * <p>설정 예시 (rest-api-prod.yml):
 *
 * <pre>{@code
 * security:
 *   oauth2:
 *     enabled: true
 *     clients:
 *       kakao:
 *         client-id: ${KAKAO_CLIENT_ID}
 *         client-secret: ${KAKAO_CLIENT_SECRET}
 *         redirect-uri: ${KAKAO_REDIRECT_URI}
 *       google:
 *         client-id: ${GOOGLE_CLIENT_ID}
 *         client-secret: ${GOOGLE_CLIENT_SECRET}
 *         redirect-uri: ${GOOGLE_REDIRECT_URI}
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "security.oauth2")
public class OAuth2Properties {

    private boolean enabled = false;
    private Map<String, OAuth2ClientProperties> clients = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, OAuth2ClientProperties> getClients() {
        return clients;
    }

    public void setClients(Map<String, OAuth2ClientProperties> clients) {
        this.clients = clients;
    }

    /**
     * 특정 OAuth2 클라이언트 설정을 가져옵니다.
     *
     * @param provider 제공자명 (kakao, google, naver 등)
     * @return OAuth2ClientProperties 또는 null
     */
    public OAuth2ClientProperties getClient(String provider) {
        return clients.get(provider);
    }

    /**
     * OAuth2 클라이언트 개별 설정.
     */
    public static class OAuth2ClientProperties {

        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String scope;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        /**
         * 필수 설정이 모두 존재하는지 확인합니다.
         *
         * @return clientId와 clientSecret이 설정되어 있으면 true
         */
        public boolean isConfigured() {
            return clientId != null && !clientId.isBlank()
                    && clientSecret != null && !clientSecret.isBlank();
        }
    }
}
