package com.ryuqq.adapter.in.rest.config;

import org.springframework.context.annotation.Configuration;

/**
 * 에러 핸들링 설정
 *
 * <p>도메인 예외를 HTTP 응답으로 변환하는 ErrorMapper 관련 설정입니다.
 *
 * <p><strong>Bean 등록 방식:</strong>
 *
 * <ul>
 *   <li>ErrorMapperRegistry - {@code @Component}로 자동 등록
 *   <li>ErrorMapper 구현체 - {@code @Component}로 자동 등록 후 Registry에 주입
 * </ul>
 *
 * <p><strong>사용 방법:</strong>
 *
 * <pre>{@code
 * // 1. ErrorMapper 구현체 생성
 * @Component
 * public class OrderErrorMapper implements ErrorMapper {
 *     @Override
 *     public boolean supports(DomainException ex) {
 *         return ex instanceof OrderException;
 *     }
 *
 *     @Override
 *     public MappedError map(DomainException ex, Locale locale) {
 *         // 매핑 로직
 *     }
 * }
 *
 * // 2. 자동으로 ErrorMapperRegistry에 등록됨
 * // 3. GlobalExceptionHandler에서 자동으로 사용 가능
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 * @see com.ryuqq.adapter.in.rest.common.error.ErrorMapperRegistry
 * @see com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper
 */
@Configuration
public class ErrorHandlingConfig {
    // ErrorMapperRegistry는 @Component로 자동 등록됨
    // 추가 설정이 필요한 경우 여기에 @Bean 메서드 추가
}
