package com.ryuqq.adapter.in.rest.layer.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.layer.exception.LayerDuplicateCodeException;
import com.ryuqq.domain.layer.exception.LayerNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * LayerErrorMapper - Layer 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>Layer 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LayerErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/layer";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof LayerNotFoundException || ex instanceof LayerDuplicateCodeException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        return switch (ex) {
            case LayerNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "Layer Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case LayerDuplicateCodeException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "Layer Duplicate Code",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-code"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Layer Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
