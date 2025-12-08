package com.ryuqq.adapter.in.rest.auth.component;

import com.ryuqq.application.common.port.out.TokenProviderPort;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

/**
 * Security Context Authenticator
 *
 * <p>SecurityContext에 인증 정보를 설정하는 컴포넌트
 *
 * <p>역할:
 *
 * <ul>
 *   <li>Access Token에서 memberId 추출 (JWT 모드)
 *   <li>Gateway 헤더에서 사용자 정보 추출 (Gateway 모드)
 *   <li>SecurityContext에 Authentication 설정
 * </ul>
 *
 * <p>사용 위치:
 *
 * <ul>
 *   <li>JwtAuthenticationFilter - JWT 인증 성공 시
 *   <li>GatewayHeaderAuthFilter - Gateway 헤더 인증 시
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class SecurityContextAuthenticator {

    private final TokenProviderPort tokenProviderPort;

    public SecurityContextAuthenticator(TokenProviderPort tokenProviderPort) {
        this.tokenProviderPort = tokenProviderPort;
    }

    /**
     * Access Token으로 SecurityContext에 인증 설정 (JWT 모드)
     *
     * @param request HttpServletRequest (인증 세부정보 설정용)
     * @param accessToken 검증된 Access Token
     * @return 인증된 memberId
     */
    public String authenticate(HttpServletRequest request, String accessToken) {
        String memberId = tokenProviderPort.extractMemberId(accessToken);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(memberId, null, Collections.emptyList());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return memberId;
    }

    /**
     * Gateway 사용자 정보로 SecurityContext에 인증 설정 (Gateway 모드)
     *
     * <p>Gateway에서 JWT 검증 후 헤더로 전달한 사용자 정보를 사용합니다.
     *
     * @param request HttpServletRequest (인증 세부정보 설정용)
     * @param gatewayUser Gateway에서 전달받은 사용자 정보
     * @return 인증된 userId (UUIDv7)
     * @see GatewayUser
     * @see GatewayUserResolver
     */
    public UUID authenticate(HttpServletRequest request, GatewayUser gatewayUser) {
        var authorities = gatewayUser.roles().stream()
                .map(role -> (org.springframework.security.core.GrantedAuthority) () -> role)
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        gatewayUser.userId().toString(),
                        null,
                        authorities);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return gatewayUser.userId();
    }

    /** 현재 SecurityContext 인증 정보 해제 */
    public void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
