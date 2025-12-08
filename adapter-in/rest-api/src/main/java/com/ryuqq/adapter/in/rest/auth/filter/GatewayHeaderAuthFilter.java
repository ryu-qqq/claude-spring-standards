package com.ryuqq.adapter.in.rest.auth.filter;

import com.ryuqq.adapter.in.rest.auth.component.GatewayUser;
import com.ryuqq.adapter.in.rest.auth.component.GatewayUserResolver;
import com.ryuqq.adapter.in.rest.auth.component.SecurityContextAuthenticator;
import com.ryuqq.adapter.in.rest.auth.config.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Gateway 헤더 인증 필터.
 *
 * <p>Gateway Only 아키텍처에서 Gateway가 JWT 검증 후 전달한
 * X-User-Id, X-User-Roles 헤더를 읽어 SecurityContext에 인증 정보를 설정합니다.
 *
 * <p>동작 방식:
 *
 * <ol>
 *   <li>Gateway 모드 활성화 여부 확인 (security.gateway.enabled)
 *   <li>비활성화 시 필터 스킵 (로컬 개발 환경)
 *   <li>활성화 시 Gateway 헤더에서 사용자 정보 추출
 *   <li>추출된 정보로 SecurityContext 인증 설정
 * </ol>
 *
 * <p>설정 예시 (rest-api-prod.yml):
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
 * @see GatewayUserResolver
 * @see GatewayUser
 * @see com.ryuqq.adapter.in.rest.auth.config.GatewayProperties
 */
@Component
public class GatewayHeaderAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayHeaderAuthFilter.class);

    private final SecurityProperties securityProperties;
    private final GatewayUserResolver gatewayUserResolver;
    private final SecurityContextAuthenticator securityContextAuthenticator;

    public GatewayHeaderAuthFilter(
            SecurityProperties securityProperties,
            GatewayUserResolver gatewayUserResolver,
            SecurityContextAuthenticator securityContextAuthenticator) {
        this.securityProperties = securityProperties;
        this.gatewayUserResolver = gatewayUserResolver;
        this.securityContextAuthenticator = securityContextAuthenticator;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!securityProperties.getGateway().isEnabled()) {
            log.debug("Gateway 인증 모드 비활성화 - 필터 스킵");
            filterChain.doFilter(request, response);
            return;
        }

        GatewayUser gatewayUser = gatewayUserResolver.resolve(request);

        if (gatewayUser != null) {
            UUID userId = securityContextAuthenticator.authenticate(request, gatewayUser);
            log.debug("Gateway 인증 성공 - userId: {}, roles: {}",
                    userId, gatewayUser.roles());
        } else {
            log.debug("Gateway 헤더 없음 - 인증 없이 진행 (Public 엔드포인트 가능)");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Gateway 모드 비활성화 시 이 필터를 완전히 스킵합니다.
     *
     * <p>로컬 개발 환경에서 Gateway 없이 동작할 때 사용됩니다.
     *
     * @param request HTTP 요청
     * @return Gateway 모드 비활성화 시 true
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !securityProperties.getGateway().isEnabled();
    }
}
