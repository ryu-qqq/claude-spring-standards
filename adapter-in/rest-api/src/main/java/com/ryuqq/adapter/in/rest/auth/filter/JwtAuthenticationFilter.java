package com.ryuqq.adapter.in.rest.auth.filter;

import com.ryuqq.adapter.in.rest.auth.component.MdcContextHolder;
import com.ryuqq.adapter.in.rest.auth.component.SecurityContextAuthenticator;
import com.ryuqq.adapter.in.rest.auth.component.TokenCookieWriter;
import com.ryuqq.application.common.port.out.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Authentication Filter
 *
 * <p>쿠키 또는 Authorization 헤더에서 Access Token을 추출하여 인증을 처리하는 필터
 *
 * <p>동작 방식:
 *
 * <ol>
 *   <li>쿠키/헤더에서 Access Token 추출
 *   <li>Access Token 유효 → 인증 성공
 *   <li>Access Token 만료 + Refresh Token 유효 → Silent Refresh (자동 갱신)
 *   <li>둘 다 없거나 만료 → 인증 실패 (다음 필터로 전달)
 * </ol>
 *
 * <p>Silent Refresh:
 *
 * <ul>
 *   <li>Access Token이 만료되었지만 Refresh Token이 유효한 경우
 *   <li>새로운 Access Token을 발급하여 쿠키에 설정
 *   <li>사용자는 401 없이 계속 서비스 이용 가능
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final TokenProviderPort tokenProviderPort;
    private final TokenCookieWriter tokenCookieWriter;
    private final SecurityContextAuthenticator securityContextAuthenticator;
    private final MdcContextHolder mdcContextHolder;

    public JwtAuthenticationFilter(
            TokenProviderPort tokenProviderPort,
            TokenCookieWriter tokenCookieWriter,
            SecurityContextAuthenticator securityContextAuthenticator,
            MdcContextHolder mdcContextHolder) {
        this.tokenProviderPort = tokenProviderPort;
        this.tokenCookieWriter = tokenCookieWriter;
        this.securityContextAuthenticator = securityContextAuthenticator;
        this.mdcContextHolder = mdcContextHolder;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> accessToken = extractAccessToken(request);

        if (accessToken.isPresent()) {
            String token = accessToken.get();

            if (tokenProviderPort.validateAccessToken(token)) {
                // Access Token 유효 → 인증 성공
                authenticateAndSetMdc(request, token);
            } else if (tokenProviderPort.isAccessTokenExpired(token)) {
                // Access Token 만료 → Silent Refresh 시도
                trySilentRefresh(request, response);
            }
        } else {
            // Access Token 없음 → Refresh Token으로 Silent Refresh 시도
            trySilentRefresh(request, response);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * SecurityContext 인증 및 MDC 설정
     *
     * @param request HttpServletRequest
     * @param accessToken 검증된 Access Token
     */
    private void authenticateAndSetMdc(HttpServletRequest request, String accessToken) {
        String memberId = securityContextAuthenticator.authenticate(request, accessToken);
        mdcContextHolder.setMemberId(memberId);
    }

    /**
     * Silent Refresh 시도
     *
     * <p>Refresh Token이 유효한 경우 새로운 토큰 쌍을 발급하고 쿠키에 설정
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    private void trySilentRefresh(HttpServletRequest request, HttpServletResponse response) {
        getRefreshToken(request)
                .filter(tokenProviderPort::validateRefreshToken)
                .ifPresent(
                        refreshToken -> {
                            String memberId =
                                    tokenProviderPort.extractMemberIdFromRefreshToken(refreshToken);

                            String newAccessToken = tokenProviderPort.generateAccessToken(memberId);
                            String newRefreshToken =
                                    tokenProviderPort.generateRefreshToken(memberId);

                            // 새 토큰을 쿠키에 설정
                            tokenCookieWriter.addTokenCookies(
                                    response,
                                    newAccessToken,
                                    newRefreshToken,
                                    tokenProviderPort.getAccessTokenExpirationSeconds(),
                                    tokenProviderPort.getRefreshTokenExpirationSeconds());

                            // 새 Access Token으로 인증 설정
                            authenticateAndSetMdc(request, newAccessToken);
                        });
    }

    /**
     * Access Token 추출
     *
     * <p>우선순위:
     *
     * <ol>
     *   <li>쿠키에서 추출 (access_token)
     *   <li>Authorization 헤더에서 추출 (Bearer ...)
     * </ol>
     *
     * @param request HttpServletRequest
     * @return Access Token (Optional)
     */
    private Optional<String> extractAccessToken(HttpServletRequest request) {
        // 1. 쿠키에서 추출 시도
        Optional<String> cookieToken = getCookie(request, ACCESS_TOKEN_COOKIE);
        if (cookieToken.isPresent()) {
            return cookieToken;
        }

        // 2. Authorization 헤더에서 추출 시도
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
        }

        return Optional.empty();
    }

    /**
     * Refresh Token 추출
     *
     * @param request HttpServletRequest
     * @return Refresh Token (Optional)
     */
    private Optional<String> getRefreshToken(HttpServletRequest request) {
        return getCookie(request, REFRESH_TOKEN_COOKIE);
    }

    /**
     * 쿠키에서 값 추출
     *
     * @param request HttpServletRequest
     * @param cookieName 쿠키 이름
     * @return 쿠키 값 (Optional)
     */
    private Optional<String> getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                String value = cookie.getValue();
                if (value != null && !value.isBlank()) {
                    return Optional.of(value);
                }
            }
        }

        return Optional.empty();
    }
}
