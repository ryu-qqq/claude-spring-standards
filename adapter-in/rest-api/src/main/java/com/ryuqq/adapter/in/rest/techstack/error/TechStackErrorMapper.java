package com.ryuqq.adapter.in.rest.techstack.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.techstack.exception.TechStackDuplicateNameException;
import com.ryuqq.domain.techstack.exception.TechStackHasChildrenException;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * TechStackErrorMapper - TechStack 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>TechStack 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class TechStackErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/tech-stack";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof TechStackNotFoundException
                || ex instanceof TechStackDuplicateNameException
                || ex instanceof TechStackHasChildrenException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case TechStackNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "TechStack Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case TechStackDuplicateNameException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "TechStack Duplicate Name",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-name"));

            case TechStackHasChildrenException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "TechStack Has Children",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/has-children"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "TechStack Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
