package com.ryuqq.adapter.in.rest.auth.component;

import com.ryuqq.application.common.port.out.TokenProviderPort;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
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
 *   <li>Access Token에서 memberId 추출
 *   <li>SecurityContext에 Authentication 설정
 * </ul>
 *
 * <p>사용 위치:
 *
 * <ul>
 *   <li>JwtAuthenticationFilter - JWT 인증 성공 시
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
     * Access Token으로 SecurityContext에 인증 설정
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

    /** 현재 SecurityContext 인증 정보 해제 */
    public void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }
}
