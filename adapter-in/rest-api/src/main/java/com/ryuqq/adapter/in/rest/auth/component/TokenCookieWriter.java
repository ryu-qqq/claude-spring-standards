package com.ryuqq.adapter.in.rest.auth.component;

import com.ryuqq.adapter.in.rest.auth.config.CookieProperties;
import com.ryuqq.adapter.in.rest.auth.config.SecurityProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Token Cookie Writer
 *
 * <p>JWT 토큰을 HttpOnly 쿠키로 관리하는 컴포넌트
 *
 * <p>쿠키 보안 설정:
 *
 * <ul>
 *   <li>HttpOnly: true - JavaScript 접근 차단 (XSS 방지)
 *   <li>Secure: 환경 설정에 따름 - HTTPS 전용 (프로덕션)
 *   <li>SameSite: Lax - CSRF 방지
 * </ul>
 *
 * <p>사용 위치:
 *
 * <ul>
 *   <li>AuthController - 로그인, 토큰 갱신, 로그아웃
 *   <li>JwtAuthenticationFilter - Silent Refresh 시
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TokenCookieWriter {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private static final String ROOT_PATH = "/";

    private final CookieProperties cookieProperties;

    public TokenCookieWriter(SecurityProperties securityProperties) {
        this.cookieProperties = securityProperties.getCookie();
    }

    /**
     * 토큰 쿠키들을 Response에 추가
     *
     * @param response HttpServletResponse
     * @param accessToken Access Token
     * @param refreshToken Refresh Token
     * @param accessTokenExpiry Access Token 만료 시간 (초)
     * @param refreshTokenExpiry Refresh Token 만료 시간 (초)
     */
    public void addTokenCookies(
            HttpServletResponse response,
            String accessToken,
            String refreshToken,
            long accessTokenExpiry,
            long refreshTokenExpiry) {
        addAccessTokenCookie(response, accessToken, accessTokenExpiry);
        addRefreshTokenCookie(response, refreshToken, refreshTokenExpiry);
    }

    /**
     * Access Token 쿠키만 추가 (토큰 갱신 시)
     *
     * @param response HttpServletResponse
     * @param accessToken Access Token
     * @param accessTokenExpiry Access Token 만료 시간 (초)
     */
    public void addAccessTokenCookie(
            HttpServletResponse response, String accessToken, long accessTokenExpiry) {
        String cookieValue =
                buildCookieValue(ACCESS_TOKEN_COOKIE, accessToken, ROOT_PATH, accessTokenExpiry);
        response.addHeader("Set-Cookie", cookieValue);
    }

    /**
     * Refresh Token 쿠키만 추가
     *
     * @param response HttpServletResponse
     * @param refreshToken Refresh Token
     * @param refreshTokenExpiry Refresh Token 만료 시간 (초)
     */
    public void addRefreshTokenCookie(
            HttpServletResponse response, String refreshToken, long refreshTokenExpiry) {
        String cookieValue =
                buildCookieValue(REFRESH_TOKEN_COOKIE, refreshToken, ROOT_PATH, refreshTokenExpiry);
        response.addHeader("Set-Cookie", cookieValue);
    }

    /**
     * 토큰 쿠키들을 Response에서 삭제
     *
     * @param response HttpServletResponse
     */
    public void deleteTokenCookies(HttpServletResponse response) {
        String accessCookie = buildCookieValue(ACCESS_TOKEN_COOKIE, "", ROOT_PATH, 0);
        String refreshCookie = buildCookieValue(REFRESH_TOKEN_COOKIE, "", ROOT_PATH, 0);
        response.addHeader("Set-Cookie", accessCookie);
        response.addHeader("Set-Cookie", refreshCookie);
    }

    /**
     * Set-Cookie 헤더 값 생성
     *
     * <p>SameSite 속성을 지원하기 위해 Set-Cookie 헤더를 직접 생성
     *
     * @param name 쿠키 이름
     * @param value 쿠키 값
     * @param path 쿠키 경로
     * @param maxAge 만료 시간 (초)
     * @return Set-Cookie 헤더 값
     */
    private String buildCookieValue(String name, String value, String path, long maxAge) {
        StringBuilder cookie = new StringBuilder();
        cookie.append(name).append("=").append(value);
        cookie.append("; Path=").append(path);
        cookie.append("; Max-Age=").append(maxAge);
        cookie.append("; HttpOnly");

        if (cookieProperties.isSecure()) {
            cookie.append("; Secure");
        }

        if (cookieProperties.hasCustomDomain()) {
            cookie.append("; Domain=").append(cookieProperties.getDomain());
        }

        cookie.append("; SameSite=").append(capitalize(cookieProperties.getSameSite()));

        return cookie.toString();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase(Locale.ROOT)
                + str.substring(1).toLowerCase(Locale.ROOT);
    }
}
