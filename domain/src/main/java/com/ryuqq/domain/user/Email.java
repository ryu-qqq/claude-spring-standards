package com.ryuqq.domain.user;

import com.ryuqq.domain.user.exception.InvalidEmailException;

import java.util.regex.Pattern;

/**
 * Email Value Object.
 *
 * <p>RFC 5322 이메일 형식을 검증하고 불변성을 보장합니다.</p>
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
 * @param value 이메일 값
 * @author Claude Code
 * @since 2025-11-10
 */
public record Email(String value) {

    /**
     * RFC 5322 이메일 형식 패턴.
     *
     * <p>간소화된 이메일 검증 정규식입니다.</p>
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * Compact Constructor - 유효성 검증.
     *
     * <p>Java 21 Record 패턴의 핵심 기능입니다.</p>
     *
     * <p>검증 규칙:
     * <ul>
     *   <li>Null 또는 빈 값 거부</li>
     *   <li>RFC 5322 이메일 형식 준수</li>
     * </ul>
     * </p>
     *
     * @throws InvalidEmailException 무효한 이메일 형식일 경우
     */
    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException("Invalid email format: " + value);
        }
    }
}
