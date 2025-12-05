package com.ryuqq.adapter.in.rest.auth.component;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * MDC Context Holder
 *
 * <p>MDC(Mapped Diagnostic Context)에 인증 정보를 설정하는 유틸리티 컴포넌트
 *
 * <p>MDC 키:
 *
 * <ul>
 *   <li>requestId - 요청 추적 ID (MdcLoggingFilter에서 설정)
 *   <li>memberId - 인증된 회원 ID (이 클래스에서 설정)
 * </ul>
 *
 * <p>사용 위치:
 *
 * <ul>
 *   <li>JwtAuthenticationFilter - JWT 인증 성공 시
 *   <li>MdcLoggingFilter - 요청 완료 시 정리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class MdcContextHolder {

    public static final String MDC_MEMBER_ID = "memberId";

    /**
     * MDC에 memberId 설정
     *
     * @param memberId 인증된 회원 ID
     */
    public void setMemberId(String memberId) {
        if (memberId != null && !memberId.isBlank()) {
            MDC.put(MDC_MEMBER_ID, memberId);
        }
    }

    /** MDC에서 memberId 제거 */
    public void clearMemberId() {
        MDC.remove(MDC_MEMBER_ID);
    }

    /**
     * 현재 MDC에 설정된 memberId 조회
     *
     * @return memberId 또는 null
     */
    public String getMemberId() {
        return MDC.get(MDC_MEMBER_ID);
    }
}
