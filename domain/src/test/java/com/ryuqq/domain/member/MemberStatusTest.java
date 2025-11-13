package com.ryuqq.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * MemberStatus Enum 테스트
 *
 * @author ryuqq
 * @since 1.0
 */
class MemberStatusTest {

    @Test
    @DisplayName("ACTIVE 상태가 존재한다")
    void shouldHaveActiveStatus() {
        // When
        MemberStatus status = MemberStatus.ACTIVE;

        // Then
        assertThat(status).isNotNull();
        assertThat(status.name()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("INACTIVE 상태가 존재한다")
    void shouldHaveInactiveStatus() {
        // When
        MemberStatus status = MemberStatus.INACTIVE;

        // Then
        assertThat(status).isNotNull();
        assertThat(status.name()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("LOCKED 상태가 존재한다")
    void shouldHaveLockedStatus() {
        // When
        MemberStatus status = MemberStatus.LOCKED;

        // Then
        assertThat(status).isNotNull();
        assertThat(status.name()).isEqualTo("LOCKED");
    }

    @Test
    @DisplayName("WITHDRAWN 상태가 존재한다")
    void shouldHaveWithdrawnStatus() {
        // When
        MemberStatus status = MemberStatus.WITHDRAWN;

        // Then
        assertThat(status).isNotNull();
        assertThat(status.name()).isEqualTo("WITHDRAWN");
    }

    @Test
    @DisplayName("valueOf()로 Enum을 조회할 수 있다")
    void shouldRetrieveEnumByValueOf() {
        // When
        MemberStatus active = MemberStatus.valueOf("ACTIVE");
        MemberStatus inactive = MemberStatus.valueOf("INACTIVE");
        MemberStatus locked = MemberStatus.valueOf("LOCKED");
        MemberStatus withdrawn = MemberStatus.valueOf("WITHDRAWN");

        // Then
        assertThat(active).isEqualTo(MemberStatus.ACTIVE);
        assertThat(inactive).isEqualTo(MemberStatus.INACTIVE);
        assertThat(locked).isEqualTo(MemberStatus.LOCKED);
        assertThat(withdrawn).isEqualTo(MemberStatus.WITHDRAWN);
    }

    @Test
    @DisplayName("존재하지 않는 상태 조회 시 예외 발생")
    void shouldThrowExceptionWhenInvalidStatus() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            MemberStatus.valueOf("INVALID");
        });
    }
}
