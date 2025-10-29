package com.ryuqq.adapter.in.rest.example.mapper;

import java.net.URI;
import java.util.Locale;

import com.ryuqq.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.adapter.in.rest.config.properties.ApiErrorProperties;
import com.ryuqq.domain.common.DomainException;
import com.ryuqq.domain.example.ExampleErrorCode;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Example 도메인 에러 매퍼
 *
 * <p>Example 도메인에서 발생한 예외를 HTTP 응답 형식으로 변환합니다.</p>
 *
 * <p><strong>주요 기능:</strong></p>
 * <ul>
 *   <li>Example 도메인 에러 코드를 HTTP 상태 코드로 매핑</li>
 *   <li>국제화(i18n) 지원을 통한 다국어 에러 메시지</li>
 *   <li>RFC 7807 Problem Details 형식의 Type URI 생성</li>
 *   <li>에러별 상세 정보 및 문서 링크 제공</li>
 * </ul>
 *
 * <p><strong>매핑 규칙:</strong></p>
 * <ul>
 *   <li>EXAMPLE_NOT_FOUND → 404 Not Found</li>
 *   <li>EXAMPLE_ALREADY_EXISTS → 409 Conflict</li>
 *   <li>EXAMPLE_INVALID_STATUS → 400 Bad Request</li>
 *   <li>기타 → 400 Bad Request (기본값)</li>
 * </ul>
 *
 * @author windsurf
 * @since 1.0.0
 */
@Component
public class ExampleErrorApiMapper implements ErrorMapper {

    private final MessageSource messageSource;
    private final ApiErrorProperties errorProperties;

    /**
     * ExampleErrorMapper 생성자
     *
     * @param messageSource 국제화 메시지 소스
     * @param errorProperties API 에러 설정
     */
    public ExampleErrorApiMapper(MessageSource messageSource, ApiErrorProperties errorProperties) {
        this.messageSource = messageSource;
        this.errorProperties = errorProperties;
    }

    /**
     * Example 도메인 에러 코드를 지원하는지 확인
     *
     * <p>에러 코드가 "EXAMPLE_"로 시작하면 이 매퍼가 처리합니다.</p>
     *
     * @param code 에러 코드
     * @return true if 에러 코드가 "EXAMPLE_"로 시작
     */
    @Override
    public boolean supports(String code) {
        return code != null && code.startsWith("EXAMPLE_");
    }

    /**
     * Example 도메인 예외를 HTTP 응답으로 매핑
     *
     * <p>에러 코드에 따라 적절한 HTTP 상태 코드와 메시지를 반환합니다.</p>
     * <p>MessageSource를 통해 다국어 메시지를 지원합니다.</p>
     *
     * @param ex 도메인 예외
     * @param locale 로케일 (다국어 지원)
     * @return HTTP 응답으로 매핑된 에러 정보
     */
    @Override
    public MappedError map(DomainException ex, Locale locale) {
        String code = ex.code();

        // ExampleErrorCode enum에 정의된 코드인지 확인
        ExampleErrorCode errorCode = findErrorCode(code);

        if (errorCode == null) {
            // 정의되지 않은 에러 코드는 기본 매핑
            return createDefaultMapping(ex, locale);
        }

        return switch (errorCode) {
            case EXAMPLE_NOT_FOUND -> new MappedError(
                HttpStatus.NOT_FOUND,
                "Not Found",
                getLocalizedMessage("error.example.not_found", ex.args(), locale, ex.getMessage()),
                URI.create(errorProperties.buildTypeUri("example-not-found"))
            );

            case EXAMPLE_ALREADY_EXISTS -> new MappedError(
                HttpStatus.CONFLICT,
                "Conflict",
                getLocalizedMessage("error.example.already_exists", ex.args(), locale, ex.getMessage()),
                URI.create(errorProperties.buildTypeUri("example-already-exists"))
            );

            case EXAMPLE_INVALID_STATUS -> new MappedError(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                getLocalizedMessage("error.example.invalid_status", ex.args(), locale, ex.getMessage()),
                URI.create(errorProperties.buildTypeUri("example-invalid-status"))
            );

            case EXAMPLE_EMPTY_MESSAGE -> new MappedError(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                getLocalizedMessage("error.example.empty_message", ex.args(), locale, ex.getMessage()),
                URI.create(errorProperties.buildTypeUri("example-empty-message"))
            );

            case EXAMPLE_MESSAGE_TOO_LONG -> new MappedError(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                getLocalizedMessage("error.example.message_too_long", ex.args(), locale, ex.getMessage()),
                URI.create(errorProperties.buildTypeUri("example-message-too-long"))
            );

            case EXAMPLE_CANNOT_DELETE -> new MappedError(
                HttpStatus.CONFLICT,
                "Conflict",
                getLocalizedMessage("error.example.cannot_delete", ex.args(), locale, ex.getMessage()),
                URI.create(errorProperties.buildTypeUri("example-cannot-delete"))
            );
        };
    }

    /**
     * 에러 코드 문자열을 ExampleErrorCode enum으로 변환
     *
     * @param code 에러 코드 문자열
     * @return ExampleErrorCode enum 또는 null
     */
    private ExampleErrorCode findErrorCode(String code) {
        try {
            return ExampleErrorCode.valueOf(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 국제화 메시지 조회
     *
     * <p>MessageSource에서 메시지를 찾을 수 없으면 fallback 메시지를 반환합니다.</p>
     *
     * @param key 메시지 키 (예: "error.example.not_found")
     * @param args 메시지 템플릿 파라미터
     * @param locale 로케일
     * @param fallback 메시지를 찾을 수 없을 때 기본값
     * @return 국제화된 메시지
     */
    private String getLocalizedMessage(String key, java.util.Map<String, Object> args, Locale locale, String fallback) {
        try {
            // args Map을 Object[] 배열로 변환
            Object[] argsArray = args.values().toArray();
            return messageSource.getMessage(key, argsArray, locale);
        } catch (NoSuchMessageException e) {
            // 메시지를 찾을 수 없으면 fallback 반환
            return fallback != null ? fallback : "An error occurred";
        }
    }

    /**
     * 기본 에러 매핑 (정의되지 않은 에러 코드)
     *
     * @param ex 도메인 예외
     * @param locale 로케일
     * @return 기본 매핑 결과
     */
    private MappedError createDefaultMapping(DomainException ex, Locale locale) {
        return new MappedError(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage() != null ? ex.getMessage() : "Invalid request",
            URI.create(errorProperties.buildTypeUri("bad-request"))
        );
    }
}
