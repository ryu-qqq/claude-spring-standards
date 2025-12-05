package com.ryuqq.adapter.in.rest.auth.filter;

import com.ryuqq.adapter.in.rest.auth.component.MdcContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * MDC Logging Filter
 *
 * <p>요청 추적을 위한 MDC(Mapped Diagnostic Context) 설정 필터
 *
 * <p>Gateway에서 전달된 X-Request-Id를 MDC에 설정하고, 없으면 새로 생성
 *
 * <p>설정되는 MDC 키:
 *
 * <ul>
 *   <li>requestId: 요청 추적 ID (Gateway에서 전달 또는 자체 생성)
 *   <li>memberId: 인증된 사용자 ID (JwtAuthenticationFilter에서 MdcContextHolder를 통해 설정)
 * </ul>
 *
 * <p>필터 순서: 가장 먼저 실행되어야 함 (Ordered.HIGHEST_PRECEDENCE)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcLoggingFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String MDC_REQUEST_ID = "requestId";

    private final MdcContextHolder mdcContextHolder;

    public MdcLoggingFilter(MdcContextHolder mdcContextHolder) {
        this.mdcContextHolder = mdcContextHolder;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Gateway에서 전달된 Request ID 사용, 없으면 생성
            String requestId = request.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isBlank()) {
                requestId = generateRequestId();
            }

            // MDC에 requestId 설정
            MDC.put(MDC_REQUEST_ID, requestId);

            // Response Header에도 추가 (클라이언트 디버깅용)
            response.setHeader(REQUEST_ID_HEADER, requestId);

            filterChain.doFilter(request, response);
        } finally {
            // 요청 완료 후 MDC 정리
            MDC.remove(MDC_REQUEST_ID);
            mdcContextHolder.clearMemberId();
        }
    }

    /**
     * Request ID 생성
     *
     * <p>UUID 기반으로 짧은 ID 생성 (앞 8자리만 사용)
     *
     * @return 생성된 Request ID
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
