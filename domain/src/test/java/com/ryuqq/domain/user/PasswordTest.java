package com.ryuqq.domain.user;

import com.ryuqq.domain.user.exception.WeakPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Password Value Object 테스트.
 *
 * <p>Kent Beck TDD RED Phase - 실패하는 테스트 작성</p>
 *
 * <p>검증 항목:
 * <ul>
 *   <li>최소 8자 이상</li>
 *   <li>영문 + 숫자 + 특수문자 조합</li>
 *   <li>불변성 (Immutable)</li>
 *   <li>Self-Validation (생성 시 검증)</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
@DisplayName("Password Value Object 테스트")
class PasswordTest {

    /**
     * 유효한 패스워드 강도 테스트.
     *
     * <p>최소 8자 + 영문 + 숫자 + 특수문자</p>
     */
    @Test
    @DisplayName("유효한 패스워드 강도 검증")
    void shouldValidateStrongPasswords() {
        // Valid passwords
        assertDoesNotThrow(() -> new Password("Pass123!@#"));
        assertDoesNotThrow(() -> new Password("Secure@2024"));
        assertDoesNotThrow(() -> new Password("MyP@ssw0rd"));
        assertDoesNotThrow(() -> new Password("Test1234!"));
    }

    /**
     * 무효한 패스워드 강도 테스트.
     *
     * <p>예외 발생 확인</p>
     */
    @Test
    @DisplayName("무효한 패스워드 강도 예외 발생")
    void shouldRejectWeakPasswords() {
        // Too short
        assertThrows(WeakPasswordException.class, () -> new Password("short"));
        assertThrows(WeakPasswordException.class, () -> new Password("Pass1!"));

        // Missing numbers
        assertThrows(WeakPasswordException.class, () -> new Password("OnlyLetters!"));

        // Missing letters
        assertThrows(WeakPasswordException.class, () -> new Password("12345678!"));

        // Missing special characters
        assertThrows(WeakPasswordException.class, () -> new Password("NoSpecial123"));

        // Only numbers
        assertThrows(WeakPasswordException.class, () -> new Password("12345678"));

        // Only letters
        assertThrows(WeakPasswordException.class, () -> new Password("onlyletters"));
    }

    /**
     * Null/Empty 패스워드 테스트.
     *
     * <p>Self-Validation 검증</p>
     */
    @Test
    @DisplayName("Null 또는 빈 패스워드 예외 발생")
    void shouldRejectNullOrEmptyPassword() {
        assertThrows(WeakPasswordException.class, () -> new Password(null));
        assertThrows(WeakPasswordException.class, () -> new Password(""));
        assertThrows(WeakPasswordException.class, () -> new Password("   "));
    }

    /**
     * 최소 길이 미달 테스트.
     *
     * <p>8자 미만 거부</p>
     */
    @Test
    @DisplayName("최소 8자 미만 패스워드 거부")
    void shouldRejectPasswordShorterThanEightCharacters() {
        assertThrows(WeakPasswordException.class, () -> new Password("Pass1!"));
        assertThrows(WeakPasswordException.class, () -> new Password("Ab1!"));
    }

    /**
     * 영문 누락 테스트.
     *
     * <p>영문 필수</p>
     */
    @Test
    @DisplayName("영문 누락 시 패스워드 거부")
    void shouldRejectPasswordWithoutLetters() {
        assertThrows(WeakPasswordException.class, () -> new Password("12345678!@#"));
    }

    /**
     * 숫자 누락 테스트.
     *
     * <p>숫자 필수</p>
     */
    @Test
    @DisplayName("숫자 누락 시 패스워드 거부")
    void shouldRejectPasswordWithoutNumbers() {
        assertThrows(WeakPasswordException.class, () -> new Password("Password!@#$"));
    }

    /**
     * 특수문자 누락 테스트.
     *
     * <p>특수문자 필수</p>
     */
    @Test
    @DisplayName("특수문자 누락 시 패스워드 거부")
    void shouldRejectPasswordWithoutSpecialCharacters() {
        assertThrows(WeakPasswordException.class, () -> new Password("Password1234"));
    }
}
