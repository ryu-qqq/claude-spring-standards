package com.ryuqq.domain.user;

/**
 * 사용자 역할 Enum.
 *
 * <p>사용자 권한 레벨을 정의합니다.</p>
 *
 * <p>Zero-Tolerance 규칙 준수:
 * <ul>
 *   <li>✅ Lombok 금지 (Pure Java Enum)</li>
 *   <li>✅ Domain은 Framework 독립적</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
public enum UserRole {
    /**
     * 일반 사용자.
     */
    USER,

    /**
     * 관리자.
     */
    ADMIN
}
