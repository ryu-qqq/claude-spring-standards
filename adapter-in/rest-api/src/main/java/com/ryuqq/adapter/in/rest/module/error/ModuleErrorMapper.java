package com.ryuqq.adapter.in.rest.module.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.module.exception.ModuleDuplicateNameException;
import com.ryuqq.domain.module.exception.ModuleNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ModuleErrorMapper - Module 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>Module 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ModuleErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/module";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ModuleNotFoundException || ex instanceof ModuleDuplicateNameException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case ModuleNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Module Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case ModuleDuplicateNameException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Module Duplicate Name",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-name"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Module Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
