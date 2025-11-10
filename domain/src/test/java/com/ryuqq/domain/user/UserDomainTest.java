package com.ryuqq.domain.user;

import com.ryuqq.domain.user.fixture.UserDomainFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * User Domain Aggregate 테스트.
 *
 * <p>Kent Beck TDD RED Phase - 실패하는 테스트 작성</p>
 *
 * <p>검증 항목:
 * <ul>
 *   <li>Law of Demeter 준수 (Getter 체이닝 금지)</li>
 *   <li>Tell, Don't Ask 원칙</li>
 *   <li>로그인 실패 카운트 및 계정 잠금</li>
 * </ul>
 * </p>
 *
 * <p>TestFixture 패턴 적용:
 * <ul>
 *   <li>✅ UserDomainFixture.create() 사용</li>
 *   <li>✅ Inline object creation 제거</li>
 *   <li>✅ Private helper method 제거</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
@DisplayName("UserDomain Aggregate 테스트")
class UserDomainTest {

    /**
     * Law of Demeter 테스트: Getter 체이닝 금지.
     *
     * <p>Zero-Tolerance 규칙 검증</p>
     *
     * <p>❌ Bad: user.getEmail().toLowerCase()</p>
     * <p>✅ Good: user.getEmailInLowerCase()</p>
     */
    @Test
    @DisplayName("Law of Demeter 준수: Getter 체이닝 대신 Tell, Don't Ask 메서드 제공")
    void shouldNotAllowGetterChaining() {
        // Given - Use Fixture
        UserDomain user = UserDomainFixture.create();

        // When & Then
        // ✅ Tell, Don't Ask 원칙: Getter 체이닝 방지
        assertThat(user.getEmailInLowerCase()).isEqualTo("test@example.com");
    }

    /**
     * 로그인 실패 카운트 테스트: 5회 실패 시 계정 자동 잠금.
     *
     * <p>비즈니스 규칙:
     * <ul>
     *   <li>로그인 실패 5회 → 계정 잠금</li>
     *   <li>잠금 후 추가 실패는 카운트 증가하지 않음</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("로그인 5회 실패 시 계정 자동 잠금")
    void shouldLockAccountAfterFiveFailedAttempts() {
        // Given - Use Fixture
        UserDomain user = UserDomainFixture.create();

        // When
        for (int i = 0; i < 5; i++) {
            user.recordLoginFailure();
        }

        // Then
        assertThat(user.isAccountLocked()).isTrue();
        assertThat(user.getLoginFailCount()).isEqualTo(5);
    }

    /**
     * 로그인 성공 시 실패 카운트 리셋 테스트.
     *
     * <p>비즈니스 규칙:
     * <ul>
     *   <li>로그인 성공 → 실패 카운트 0으로 리셋</li>
     *   <li>단, 계정 잠금은 관리자만 해제 가능 (별도 메서드)</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("로그인 성공 시 실패 카운트 리셋")
    void shouldResetFailCountAfterSuccessfulLogin() {
        // Given - Use Fixture
        UserDomain user = UserDomainFixture.create();
        user.recordLoginFailure();
        user.recordLoginFailure();

        // When
        user.recordLoginSuccess();

        // Then
        assertThat(user.getLoginFailCount()).isEqualTo(0);
        assertThat(user.isAccountLocked()).isFalse();
    }

    /**
     * 관리자 권한으로 계정 잠금 해제 테스트.
     *
     * <p>비즈니스 규칙:
     * <ul>
     *   <li>계정 잠금 해제는 관리자만 가능</li>
     *   <li>해제 시 실패 카운트도 0으로 리셋</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("관리자 권한으로 계정 잠금 해제")
    void shouldUnlockAccountByAdmin() {
        // Given - Use Fixture (createLockedAccount)
        UserDomain user = UserDomainFixture.createLockedAccount();
        assertThat(user.isAccountLocked()).isTrue();

        // When
        user.unlockAccount();

        // Then
        assertThat(user.isAccountLocked()).isFalse();
        assertThat(user.getLoginFailCount()).isEqualTo(0);
    }

    /**
     * UserDomain 생성 테스트.
     *
     * <p>Factory Method 패턴 사용</p>
     * <p>Fixture를 사용하여 생성 검증</p>
     */
    @Test
    @DisplayName("UserDomain 생성 (Factory Method)")
    void shouldCreateUserDomain() {
        // When - Use Fixture
        UserDomain user = UserDomainFixture.create();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getLoginFailCount()).isEqualTo(0);
        assertThat(user.isAccountLocked()).isFalse();
        assertThat(user.getCreatedAt()).isNotNull();
    }
}
