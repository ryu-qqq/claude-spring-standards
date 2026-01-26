package com.ryuqq.adapter.in.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * REST API Security Configuration
 *
 * <p>Gateway-Only 아키텍처에 맞춘 Security 설정입니다.
 *
 * <p><strong>인증 아키텍처:</strong>
 *
 * <ul>
 *   <li>JWT 검증: API Gateway에서 처리
 *   <li>OAuth2: API Gateway에서 처리
 *   <li>CORS: API Gateway에서 처리
 *   <li>이 서비스: Gateway가 전달하는 헤더(X-User-Id, X-User-Roles)만 파싱
 * </ul>
 *
 * <p><strong>보안 정책:</strong>
 *
 * <ul>
 *   <li>CSRF 비활성화 (Stateless API, Gateway에서 처리)
 *   <li>CORS 비활성화 (Gateway에서 처리)
 *   <li>모든 요청 permitAll (Gateway 인증 후 도달하므로 신뢰)
 *   <li>Stateless 세션 관리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security Filter Chain 설정
     *
     * <p>Gateway-Only 아키텍처에서 모든 요청을 신뢰합니다. 네트워크 정책으로 Gateway만 이 서비스에 접근 가능합니다.
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 설정 실패 시
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
