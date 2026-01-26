package com.ryuqq.adapter.in.rest.zerotolerance.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.codingrule.exception.CodingRuleNotFoundException;
import com.ryuqq.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ZeroToleranceRuleErrorMapper - ZeroToleranceRule 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>Zero-Tolerance 규칙 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * <p>Zero-Tolerance 규칙 조회 시 발생하는 CodingRuleNotFoundException을 처리합니다.
 *
 * <p>ERR-001: 도메인별 ErrorMapper 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ZeroToleranceRuleErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/zero-tolerance-rule";

    /**
     * 이 매퍼가 처리할 수 있는 예외인지 확인
     *
     * <p>Zero-Tolerance 규칙 조회 시 CodingRule을 찾지 못하면 CodingRuleNotFoundException이 발생합니다.
     *
     * @param ex 도메인 예외
     * @return CodingRuleNotFoundException이면 true
     */
    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof CodingRuleNotFoundException;
    }

    /**
     * DomainException을 HTTP 응답용 MappedError로 변환
     *
     * @param ex 도메인 예외
     * @param locale 다국어 지원을 위한 로케일
     * @return RFC 7807 호환 에러 정보
     */
    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case CodingRuleNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Zero-Tolerance Rule Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Zero-Tolerance Rule Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
