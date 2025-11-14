package com.ryuqq.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Email VO 테스트")
class EmailTest {

    @Test
    @DisplayName("유효한 이메일 형식으로 Email 생성")
    void shouldCreateEmailWithValidFormat() {
        // Given
        String validEmail = "user@example.com";

        // When
        Email email = new Email(validEmail);

        // Then
        assertThat(email.value()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("@ 기호가 없는 이메일은 예외 발생")
    void shouldThrowExceptionWhenMissingAtSign() {
        // Given
        String invalidEmail = "userexample.com";

        // When & Then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("도메인이 없는 이메일은 예외 발생")
    void shouldThrowExceptionWhenMissingDomain() {
        // Given
        String invalidEmail = "user@";

        // When & Then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("320자를 초과하는 이메일은 예외 발생")
    void shouldThrowExceptionWhenExceedingMaxLength() {
        // Given
        String longEmail = "a".repeat(310) + "@example.com"; // 총 323자

        // When & Then
        assertThatThrownBy(() -> new Email(longEmail))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("320자");
    }

    @Test
    @DisplayName("로컬 부분이 없는 이메일은 예외 발생")
    void shouldThrowExceptionWhenMissingLocalPart() {
        // Given
        String invalidEmail = "@example.com";

        // When & Then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("빈 문자열 이메일은 예외 발생")
    void shouldThrowExceptionWhenEmpty() {
        // Given
        String emptyEmail = "";

        // When & Then
        assertThatThrownBy(() -> new Email(emptyEmail))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("null 이메일은 예외 발생")
    void shouldThrowExceptionWhenNull() {
        // When & Then
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailFormatException.class);
    }
}
