package com.ryuqq.adapter.in.rest.classtemplate.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateDuplicateCodeException;
import com.ryuqq.domain.classtemplate.exception.ClassTemplateNotFoundException;
import com.ryuqq.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ClassTemplateErrorMapper - ClassTemplate 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>ClassTemplate 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * <p>ERR-001: 도메인별 ErrorMapper 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTemplateErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/class-template";

    /**
     * 이 매퍼가 처리할 수 있는 예외인지 확인
     *
     * @param ex 도메인 예외
     * @return ClassTemplate 관련 예외이면 true
     */
    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ClassTemplateNotFoundException
                || ex instanceof ClassTemplateDuplicateCodeException;
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
            case ClassTemplateNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "ClassTemplate Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case ClassTemplateDuplicateCodeException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "ClassTemplate Code Already Exists",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-code"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "ClassTemplate Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
