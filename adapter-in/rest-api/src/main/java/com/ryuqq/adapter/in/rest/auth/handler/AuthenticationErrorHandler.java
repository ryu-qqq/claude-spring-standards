package com.ryuqq.adapter.in.rest.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Authentication Error Handler
 *
 * <p>인증/인가 에러를 RFC 7807 ProblemDetail 형식으로 처리
 *
 * <p>역할:
 *
 * <ul>
 *   <li>AuthenticationEntryPoint: 인증 실패 (401 Unauthorized)
 *   <li>AccessDeniedHandler: 인가 실패 (403 Forbidden)
 * </ul>
 *
 * <p>응답 형식:
 *
 * <ul>
 *   <li>RFC 7807 ProblemDetail 표준 준수
 *   <li>GlobalExceptionHandler와 동일한 형식 유지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class AuthenticationErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationErrorHandler.class);

    private final ObjectMapper objectMapper;

    public AuthenticationErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 인증 실패 처리 (401 Unauthorized)
     *
     * <p>JWT 토큰이 없거나 유효하지 않은 경우 호출
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param authException 인증 예외
     * @throws IOException 응답 작성 실패 시
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        log.debug("Authentication failed: {}", authException.getMessage());

        ProblemDetail problemDetail =
                buildProblemDetail(
                        request,
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
                        "AUTH_REQUIRED");

        writeResponse(response, HttpStatus.UNAUTHORIZED, problemDetail);
    }

    /**
     * 인가 실패 처리 (403 Forbidden)
     *
     * <p>인증은 되었지만 해당 리소스에 접근 권한이 없는 경우 호출
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param accessDeniedException 접근 거부 예외
     * @throws IOException 응답 작성 실패 시
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        log.warn("Access denied: {}", accessDeniedException.getMessage());

        ProblemDetail problemDetail =
                buildProblemDetail(
                        request,
                        HttpStatus.FORBIDDEN,
                        "Forbidden",
                        "해당 리소스에 대한 접근 권한이 없습니다.",
                        "ACCESS_DENIED");

        writeResponse(response, HttpStatus.FORBIDDEN, problemDetail);
    }

    /**
     * RFC 7807 ProblemDetail 생성
     *
     * <p>GlobalExceptionHandler와 동일한 형식으로 생성
     *
     * @param request HttpServletRequest
     * @param status HTTP 상태
     * @param title 에러 제목
     * @param detail 에러 상세 메시지
     * @param code 에러 코드
     * @return ProblemDetail
     */
    private ProblemDetail buildProblemDetail(
            HttpServletRequest request,
            HttpStatus status,
            String title,
            String detail,
            String code) {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));

        // timestamp 추가
        pd.setProperty("timestamp", Instant.now().toString());

        // code 추가
        pd.setProperty("code", code);

        // instance (요청 경로) 설정
        String uri = request.getRequestURI();
        if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
            uri = uri + "?" + request.getQueryString();
        }
        pd.setInstance(URI.create(uri));

        // tracing 정보 추가 (MDC에서 가져옴)
        String traceId = MDC.get("traceId");
        String requestId = MDC.get("requestId");
        if (traceId != null) {
            pd.setProperty("traceId", traceId);
        }
        if (requestId != null) {
            pd.setProperty("requestId", requestId);
        }

        return pd;
    }

    /**
     * JSON 응답 작성
     *
     * @param response HttpServletResponse
     * @param status HTTP 상태
     * @param problemDetail 응답 바디
     * @throws IOException 응답 작성 실패 시
     */
    private void writeResponse(
            HttpServletResponse response, HttpStatus status, ProblemDetail problemDetail)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
