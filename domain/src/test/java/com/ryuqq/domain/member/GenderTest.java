package com.ryuqq.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Gender Enum 테스트
 *
 * @author ryuqq
 * @since 1.0
 */
class GenderTest {

    @Test
    @DisplayName("MALE 성별이 존재한다")
    void shouldHaveMaleGender() {
        // When
        Gender gender = Gender.MALE;

        // Then
        assertThat(gender).isNotNull();
        assertThat(gender.name()).isEqualTo("MALE");
    }

    @Test
    @DisplayName("FEMALE 성별이 존재한다")
    void shouldHaveFemaleGender() {
        // When
        Gender gender = Gender.FEMALE;

        // Then
        assertThat(gender).isNotNull();
        assertThat(gender.name()).isEqualTo("FEMALE");
    }

    @Test
    @DisplayName("OTHER 성별이 존재한다")
    void shouldHaveOtherGender() {
        // When
        Gender gender = Gender.OTHER;

        // Then
        assertThat(gender).isNotNull();
        assertThat(gender.name()).isEqualTo("OTHER");
    }

    @Test
    @DisplayName("valueOf()로 Enum을 조회할 수 있다")
    void shouldRetrieveEnumByValueOf() {
        // When
        Gender male = Gender.valueOf("MALE");
        Gender female = Gender.valueOf("FEMALE");
        Gender other = Gender.valueOf("OTHER");

        // Then
        assertThat(male).isEqualTo(Gender.MALE);
        assertThat(female).isEqualTo(Gender.FEMALE);
        assertThat(other).isEqualTo(Gender.OTHER);
    }

    @Test
    @DisplayName("존재하지 않는 성별 조회 시 예외 발생")
    void shouldThrowExceptionWhenInvalidGender() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            Gender.valueOf("INVALID");
        });
    }
}
