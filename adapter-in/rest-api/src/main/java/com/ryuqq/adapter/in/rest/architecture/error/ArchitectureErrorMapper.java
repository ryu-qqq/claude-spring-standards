package com.ryuqq.adapter.in.rest.architecture.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.architecture.exception.ArchitectureDuplicateNameException;
import com.ryuqq.domain.architecture.exception.ArchitectureNotFoundException;
import com.ryuqq.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ArchitectureErrorMapper - Architecture 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>Architecture 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ArchitectureErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/architecture";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ArchitectureNotFoundException
                || ex instanceof ArchitectureDuplicateNameException;
    }

    @Override
    public com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper.MappedError map(
            DomainException ex, Locale locale) {
        return switch (ex) {
            case ArchitectureNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Architecture Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case ArchitectureDuplicateNameException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Architecture Duplicate Name",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-name"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Architecture Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
