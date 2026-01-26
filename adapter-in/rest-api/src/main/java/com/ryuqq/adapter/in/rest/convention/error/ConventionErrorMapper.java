package com.ryuqq.adapter.in.rest.convention.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.convention.exception.ConventionDuplicateException;
import com.ryuqq.domain.convention.exception.ConventionNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ConventionErrorMapper - Convention 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>Convention 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ConventionErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/convention";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ConventionNotFoundException
                || ex instanceof ConventionDuplicateException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case ConventionNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Convention Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case ConventionDuplicateException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Convention Duplicate",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Convention Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
