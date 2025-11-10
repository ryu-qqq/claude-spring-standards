package com.ryuqq.domain.user;

import com.ryuqq.domain.user.exception.InvalidEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Email Value Object 테스트.
 *
 * <p>Kent Beck TDD RED Phase - 실패하는 테스트 작성</p>
 *
 * <p>검증 항목:
 * <ul>
 *   <li>RFC 5322 이메일 형식 검증</li>
 *   <li>불변성 (Immutable)</li>
 *   <li>Self-Validation (생성 시 검증)</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
@DisplayName("Email Value Object 테스트")
class EmailTest {

    /**
     * 유효한 이메일 형식 테스트.
     *
     * <p>RFC 5322 형식 준수</p>
     */
    @Test
    @DisplayName("유효한 이메일 형식 검증")
    void shouldValidateValidEmailFormats() {
        // Valid emails
        assertDoesNotThrow(() -> new Email("test@example.com"));
        assertDoesNotThrow(() -> new Email("user.name@domain.co.kr"));
        assertDoesNotThrow(() -> new Email("user+tag@example.com"));
        assertDoesNotThrow(() -> new Email("user_name@subdomain.example.com"));
    }

    /**
     * 무효한 이메일 형식 테스트.
     *
     * <p>예외 발생 확인</p>
     */
    @Test
    @DisplayName("무효한 이메일 형식 예외 발생")
    void shouldRejectInvalidEmailFormats() {
        // Invalid emails
        assertThrows(InvalidEmailException.class, () -> new Email("invalid"));
        assertThrows(InvalidEmailException.class, () -> new Email("@example.com"));
        assertThrows(InvalidEmailException.class, () -> new Email("test@"));
        assertThrows(InvalidEmailException.class, () -> new Email("test@@example.com"));
        assertThrows(InvalidEmailException.class, () -> new Email("test @example.com"));
    }

    /**
     * Null/Empty 이메일 테스트.
     *
     * <p>Self-Validation 검증</p>
     */
    @Test
    @DisplayName("Null 또는 빈 이메일 예외 발생")
    void shouldRejectNullOrEmptyEmail() {
        assertThrows(InvalidEmailException.class, () -> new Email(null));
        assertThrows(InvalidEmailException.class, () -> new Email(""));
        assertThrows(InvalidEmailException.class, () -> new Email("   "));
    }

    /**
     * Email 불변성 테스트.
     *
     * <p>Record 패턴 사용 시 자동 보장</p>
     */
    @Test
    @DisplayName("Email 불변성 검증")
    void shouldBeImmutable() {
        // Given
        Email email = new Email("test@example.com");

        // Then
        assertThat(email.value()).isEqualTo("test@example.com");
    }

    /**
     * Email 동등성 테스트.
     *
     * <p>Value Object는 값으로 비교</p>
     */
    @Test
    @DisplayName("Email 값 동등성 검증")
    void shouldCompareByValue() {
        // Given
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");

        // Then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
}
