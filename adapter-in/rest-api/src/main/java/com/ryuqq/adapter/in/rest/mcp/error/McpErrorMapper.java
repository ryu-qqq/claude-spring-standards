package com.ryuqq.adapter.in.rest.mcp.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.convention.exception.ConventionNotFoundException;
import com.ryuqq.domain.layer.exception.LayerNotFoundException;
import com.ryuqq.domain.module.exception.ModuleNotFoundException;
import com.ryuqq.domain.techstack.exception.TechStackNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * McpErrorMapper - MCP 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>MCP 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * <p>MCP는 여러 도메인(Module, Layer, Convention 등)의 데이터를 집계하므로, 관련 도메인의 주요 예외를 처리합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class McpErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/mcp";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ModuleNotFoundException
                || ex instanceof LayerNotFoundException
                || ex instanceof ConventionNotFoundException
                || ex instanceof TechStackNotFoundException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case ModuleNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Module Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/module-not-found"));

            case LayerNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Layer Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/layer-not-found"));

            case ConventionNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Convention Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/convention-not-found"));

            case TechStackNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "TechStack Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/techstack-not-found"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "MCP Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
