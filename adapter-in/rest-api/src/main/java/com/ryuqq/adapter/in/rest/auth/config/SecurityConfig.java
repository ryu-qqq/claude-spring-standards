package com.ryuqq.adapter.in.rest.auth.config;

import com.ryuqq.adapter.in.rest.auth.component.MdcContextHolder;
import com.ryuqq.adapter.in.rest.auth.component.SecurityContextAuthenticator;
import com.ryuqq.adapter.in.rest.auth.component.TokenCookieWriter;
import com.ryuqq.adapter.in.rest.auth.filter.GatewayHeaderAuthFilter;
import com.ryuqq.adapter.in.rest.auth.filter.JwtAuthenticationFilter;
import com.ryuqq.adapter.in.rest.auth.handler.AuthenticationErrorHandler;
import com.ryuqq.adapter.in.rest.auth.paths.SecurityPaths;
import com.ryuqq.adapter.in.rest.auth.paths.SecurityPaths.PublicEndpoint;
import com.ryuqq.application.common.port.out.TokenProviderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p>Spring Security 설정 (Gateway 모드 / JWT 모드 분기)
 *
 * <p>인증 방식:
 *
 * <ul>
 *   <li><b>Gateway 모드</b>: Gateway에서 JWT 검증 후 헤더(X-User-Id, X-User-Roles)로 사용자 정보 전달
 *   <li><b>JWT 모드</b>: 서비스 자체에서 JWT 검증 (로컬 개발용)
 * </ul>
 *
 * <p>모드 전환 설정 (rest-api.yml):
 *
 * <pre>{@code
 * security:
 *   gateway:
 *     enabled: true  # Gateway 모드 (운영 환경)
 *     enabled: false # JWT 모드 (로컬 개발)
 * }</pre>
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
 * @see GatewayHeaderAuthFilter
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final TokenProviderPort tokenProviderPort;
    private final TokenCookieWriter tokenCookieWriter;
    private final SecurityContextAuthenticator securityContextAuthenticator;
    private final MdcContextHolder mdcContextHolder;
    private final AuthenticationErrorHandler authenticationErrorHandler;
    private final SecurityProperties securityProperties;
    private final GatewayHeaderAuthFilter gatewayHeaderAuthFilter;

    public SecurityConfig(
            TokenProviderPort tokenProviderPort,
            TokenCookieWriter tokenCookieWriter,
            SecurityContextAuthenticator securityContextAuthenticator,
            MdcContextHolder mdcContextHolder,
            AuthenticationErrorHandler authenticationErrorHandler,
            SecurityProperties securityProperties,
            GatewayHeaderAuthFilter gatewayHeaderAuthFilter) {
        this.tokenProviderPort = tokenProviderPort;
        this.tokenCookieWriter = tokenCookieWriter;
        this.securityContextAuthenticator = securityContextAuthenticator;
        this.mdcContextHolder = mdcContextHolder;
        this.authenticationErrorHandler = authenticationErrorHandler;
        this.securityProperties = securityProperties;
        this.gatewayHeaderAuthFilter = gatewayHeaderAuthFilter;
    }

    /**
     * Security Filter Chain 설정
     *
     * <p>Gateway 모드와 JWT 모드에 따라 다른 인증 필터를 적용합니다.
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
                                        .accessDeniedHandler(authenticationErrorHandler));

        // Gateway 모드 vs JWT 모드에 따라 필터 선택
        configureAuthenticationFilter(http);

        return http.build();
    }

    /**
     * 인증 모드에 따른 필터 설정
     *
     * <p>Gateway 모드가 활성화되면 GatewayHeaderAuthFilter를 사용하고,
     * 비활성화되면 JwtAuthenticationFilter를 사용합니다.
     *
     * @param http HttpSecurity
     */
    private void configureAuthenticationFilter(HttpSecurity http) {
        if (securityProperties.isGatewayMode()) {
            log.info("Security 인증 모드: Gateway (Gateway에서 JWT 검증 완료)");
            http.addFilterBefore(gatewayHeaderAuthFilter, UsernamePasswordAuthenticationFilter.class);
        } else {
            log.info("Security 인증 모드: JWT (서비스 자체 JWT 검증)");
            http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }
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
        CorsProperties corsProps = securityProperties.getCors();

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
