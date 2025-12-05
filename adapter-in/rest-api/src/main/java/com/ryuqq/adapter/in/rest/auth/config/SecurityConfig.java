package com.ryuqq.adapter.in.rest.auth.config;

import com.ryuqq.adapter.in.rest.auth.component.MdcContextHolder;
import com.ryuqq.adapter.in.rest.auth.component.SecurityContextAuthenticator;
import com.ryuqq.adapter.in.rest.auth.component.TokenCookieWriter;
import com.ryuqq.adapter.in.rest.auth.filter.JwtAuthenticationFilter;
import com.ryuqq.adapter.in.rest.auth.handler.AuthenticationErrorHandler;
import com.ryuqq.adapter.in.rest.auth.paths.SecurityPaths;
import com.ryuqq.adapter.in.rest.auth.paths.SecurityPaths.PublicEndpoint;
import com.ryuqq.application.common.port.out.TokenProviderPort;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security Configuration
 *
 * <p>Spring Security + JWT 설정
 *
 * <p>인증 방식:
 *
 * <ul>
 *   <li>JWT 기반 Stateless 인증
 *   <li>Access Token: 쿠키 또는 Authorization 헤더
 *   <li>Refresh Token: 쿠키 기반 Silent Refresh
 * </ul>
 *
 * <p>에러 처리:
 *
 * <ul>
 *   <li>401 Unauthorized: AuthenticationErrorHandler (인증 실패)
 *   <li>403 Forbidden: AuthenticationErrorHandler (인가 실패)
 *   <li>RFC 7807 ProblemDetail 형식으로 응답
 * </ul>
 *
 * <p>공개 엔드포인트:
 *
 * <ul>
 *   <li>{@link SecurityPaths#PUBLIC_ENDPOINTS} 상수로 정의
 *   <li>SpEL 대신 Constants 패턴 사용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SecurityProperties
 * @see SecurityPaths
 * @see AuthenticationErrorHandler
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private final TokenProviderPort tokenProviderPort;
    private final TokenCookieWriter tokenCookieWriter;
    private final SecurityContextAuthenticator securityContextAuthenticator;
    private final MdcContextHolder mdcContextHolder;
    private final AuthenticationErrorHandler authenticationErrorHandler;
    private final SecurityProperties securityProperties;

    public SecurityConfig(
            TokenProviderPort tokenProviderPort,
            TokenCookieWriter tokenCookieWriter,
            SecurityContextAuthenticator securityContextAuthenticator,
            MdcContextHolder mdcContextHolder,
            AuthenticationErrorHandler authenticationErrorHandler,
            SecurityProperties securityProperties) {
        this.tokenProviderPort = tokenProviderPort;
        this.tokenCookieWriter = tokenCookieWriter;
        this.securityContextAuthenticator = securityContextAuthenticator;
        this.mdcContextHolder = mdcContextHolder;
        this.authenticationErrorHandler = authenticationErrorHandler;
        this.securityProperties = securityProperties;
    }

    /**
     * Security Filter Chain 설정
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 설정 실패 시
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용)
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용 안함 (Stateless)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 인가 설정 (Constants 기반)
                .authorizeHttpRequests(this::configurePublicEndpoints)

                // 에러 핸들러 설정 (401/403 응답을 RFC 7807 형식으로)
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(authenticationErrorHandler)
                                        .accessDeniedHandler(authenticationErrorHandler))

                // JWT 필터 추가
                .addFilterBefore(
                        jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 공개 엔드포인트 설정
     *
     * <p>{@link SecurityPaths#PUBLIC_ENDPOINTS} 상수에서 정의된 공개 엔드포인트 등록
     *
     * @param auth AuthorizeHttpRequestsConfigurer
     */
    private void configurePublicEndpoints(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
                    auth) {

        for (PublicEndpoint endpoint : SecurityPaths.PUBLIC_ENDPOINTS) {
            if (endpoint.hasMethod()) {
                auth.requestMatchers(endpoint.getMethod(), endpoint.getPattern()).permitAll();
            } else {
                auth.requestMatchers(endpoint.getPattern()).permitAll();
            }
        }

        // 그 외 모든 요청은 인증 필요
        auth.anyRequest().authenticated();
    }

    /**
     * JWT 인증 필터 Bean
     *
     * @return JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(
                tokenProviderPort,
                tokenCookieWriter,
                securityContextAuthenticator,
                mdcContextHolder);
    }

    /**
     * CORS 설정
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        SecurityProperties.CorsProperties corsProps = securityProperties.getCors();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProps.getAllowedOrigins());
        configuration.setAllowedMethods(corsProps.getAllowedMethods());
        configuration.setAllowedHeaders(corsProps.getAllowedHeaders());
        configuration.setExposedHeaders(corsProps.getExposedHeaders());
        configuration.setAllowCredentials(corsProps.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
