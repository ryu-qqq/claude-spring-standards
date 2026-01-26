package com.ryuqq.adapter.in.rest.resourcetemplate.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.resourcetemplate.exception.ResourceTemplateNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ResourceTemplateErrorMapper - ResourceTemplate 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>ResourceTemplate 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * <p>ERR-001: 도메인별 ErrorMapper 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ResourceTemplateErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/resource-template";

    /**
     * 이 매퍼가 처리할 수 있는 예외인지 확인
     *
     * @param ex 도메인 예외
     * @return ResourceTemplate 관련 예외이면 true
     */
    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ResourceTemplateNotFoundException;
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
        if (ex instanceof ResourceTemplateNotFoundException) {
            return new MappedError(
                    HttpStatus.NOT_FOUND,
                    "ResourceTemplate Not Found",
                    ex.getMessage(),
                    URI.create(ERROR_TYPE_BASE + "/not-found"));
        }
        return new MappedError(
                HttpStatus.BAD_REQUEST,
                "ResourceTemplate Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_BASE));
    }
}
