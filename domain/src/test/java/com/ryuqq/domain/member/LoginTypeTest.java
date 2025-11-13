package com.ryuqq.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * LoginType Enum 테스트
 *
 * @author ryuqq
 * @since 1.0
 */
class LoginTypeTest {

    @Test
    @DisplayName("KAKAO 타입이 존재한다")
    void shouldHaveKakaoType() {
        // When
        LoginType loginType = LoginType.KAKAO;

        // Then
        assertThat(loginType).isNotNull();
        assertThat(loginType.name()).isEqualTo("KAKAO");
    }

    @Test
    @DisplayName("PHONE 타입이 존재한다")
    void shouldHavePhoneType() {
        // When
        LoginType loginType = LoginType.PHONE;

        // Then
        assertThat(loginType).isNotNull();
        assertThat(loginType.name()).isEqualTo("PHONE");
    }

    @Test
    @DisplayName("valueOf()로 Enum을 조회할 수 있다")
    void shouldRetrieveEnumByValueOf() {
        // When
        LoginType kakao = LoginType.valueOf("KAKAO");
        LoginType phone = LoginType.valueOf("PHONE");

        // Then
        assertThat(kakao).isEqualTo(LoginType.KAKAO);
        assertThat(phone).isEqualTo(LoginType.PHONE);
    }

    @Test
    @DisplayName("존재하지 않는 타입 조회 시 예외 발생")
    void shouldThrowExceptionWhenInvalidType() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            LoginType.valueOf("INVALID");
        });
    }
}
