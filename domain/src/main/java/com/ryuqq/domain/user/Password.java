package com.ryuqq.domain.user;

import com.ryuqq.domain.user.exception.WeakPasswordException;

import java.util.regex.Pattern;

/**
 * Password Value Object.
 *
 * <p>패스워드 강도를 검증하고 불변성을 보장합니다.</p>
 *
 * <p>비즈니스 규칙:
 * <ul>
 *   <li>최소 8자 이상</li>
 *   <li>영문 + 숫자 + 특수문자 조합 필수</li>
 * </ul>
 * </p>
 *
 * <p>Zero-Tolerance 규칙 준수:
 * <ul>
 *   <li>✅ Lombok 금지 (Java 21 Record 사용)</li>
 *   <li>✅ Immutable (Record 자동 보장)</li>
 *   <li>✅ Self-Validation (Compact Constructor)</li>
 *   <li>✅ Value Equality (Record 자동 구현)</li>
 * </ul>
 * </p>
 *
 * <p>Java 21 Record 패턴:
 * <ul>
 *   <li>Compact Constructor로 검증 로직 간결화</li>
 *   <li>equals(), hashCode() 자동 생성</li>
 *   <li>toString() 자동 생성</li>
 * </ul>
 * </p>
 *
 * @param value 패스워드 값
 * @author Claude Code
 * @since 2025-11-10
 */
public record Password(String value) {

    /**
     * 패스워드 강도 검증 패턴.
     *
     * <p>정규식 설명:
     * <ul>
     *   <li>^(?=.*[A-Za-z]) - 영문 최소 1개</li>
     *   <li>(?=.*\\d) - 숫자 최소 1개</li>
     *   <li>(?=.*[@$!%*#?&]) - 특수문자 최소 1개</li>
     *   <li>[A-Za-z\\d@$!%*#?&]{8,}$ - 8자 이상</li>
     * </ul>
     * </p>
     */
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");

    /**
     * Compact Constructor - 유효성 검증.
     *
     * <p>Java 21 Record 패턴의 핵심 기능입니다.</p>
     *
     * <p>검증 규칙:
     * <ul>
     *   <li>Null 또는 빈 값 거부</li>
     *   <li>최소 8자 이상</li>
     *   <li>영문 + 숫자 + 특수문자 조합 필수</li>
     * </ul>
     * </p>
     *
     * @throws WeakPasswordException 약한 패스워드일 경우
     */
    public Password {
        if (value == null || value.isBlank()) {
            throw new WeakPasswordException("Password cannot be empty");
        }
        if (!PASSWORD_PATTERN.matcher(value).matches()) {
            throw new WeakPasswordException(
                    "Password must be at least 8 characters with letters, numbers, and special characters"
            );
        }
    }
}
