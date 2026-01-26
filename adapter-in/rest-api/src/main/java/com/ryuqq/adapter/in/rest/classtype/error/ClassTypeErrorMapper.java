package com.ryuqq.adapter.in.rest.classtype.error;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.domain.classtype.exception.ClassTypeDuplicateCodeException;
import com.ryuqq.domain.classtype.exception.ClassTypeNotFoundException;
import com.ryuqq.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ClassTypeErrorMapper - ClassType 도메인 예외를 HTTP 응답으로 변환
 *
 * <p>ClassType 관련 DomainException을 RFC 7807 호환 에러 응답으로 변환합니다.
 *
 * <p>ERR-001: 도메인별 ErrorMapper 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ClassTypeErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_BASE = "/errors/class-type";

    /**
     * 이 매퍼가 처리할 수 있는 예외인지 확인
     *
     * @param ex 도메인 예외
     * @return ClassType 관련 예외이면 true
     */
    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ClassTypeNotFoundException
                || ex instanceof ClassTypeDuplicateCodeException;
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
            case ClassTypeNotFoundException e ->
                    new MappedError(
                            HttpStatus.NOT_FOUND,
                            "ClassType Not Found",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/not-found"));

            case ClassTypeDuplicateCodeException e ->
                    new MappedError(
                            HttpStatus.CONFLICT,
                            "ClassType Code Already Exists",
                            e.getMessage(),
                            URI.create(ERROR_TYPE_BASE + "/duplicate-code"));

            default ->
                    new MappedError(
                            HttpStatus.BAD_REQUEST,
                            "ClassType Error",
                            ex.getMessage(),
                            URI.create(ERROR_TYPE_BASE));
        };
    }
}
