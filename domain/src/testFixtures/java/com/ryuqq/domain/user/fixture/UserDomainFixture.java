package com.ryuqq.domain.user.fixture;

import com.ryuqq.domain.user.UserDomain;
import com.ryuqq.domain.user.UserRole;

/**
 * TestFixture for UserDomain.
 *
 * <p>Object Mother 패턴으로 테스트 객체를 생성합니다.</p>
 *
 * <p>생성 패턴:
 * <ul>
 *   <li>{@link #create()} - 기본 일반 사용자</li>
 *   <li>{@link #createWithEmail(String)} - 특정 이메일로 사용자 생성</li>
 *   <li>{@link #createWithId(Long)} - 특정 ID로 사용자 생성</li>
 *   <li>{@link #createAdmin()} - 관리자 사용자 생성</li>
 *   <li>{@link #createLockedAccount()} - 계정 잠금 상태 사용자 생성</li>
 * </ul>
 * </p>
 *
 * <p>Zero-Tolerance 규칙 준수:
 * <ul>
 *   <li>✅ All methods are static</li>
 *   <li>✅ Private constructor to prevent instantiation</li>
 *   <li>✅ *Fixture suffix naming convention</li>
 *   <li>✅ create*() method naming convention</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
public class UserDomainFixture {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String DEFAULT_PASSWORD = "encrypted-password";
    private static final String DEFAULT_NAME = "Test User";

    /**
     * 기본 UserDomain 생성.
     *
     * <p>일반 사용자 권한으로 생성됩니다.</p>
     *
     * @return 기본 UserDomain 인스턴스
     */
    public static UserDomain create() {
        return UserDomain.create(
                DEFAULT_USER_ID,
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD,
                DEFAULT_NAME,
                UserRole.USER
        );
    }

    /**
     * 특정 이메일로 UserDomain 생성.
     *
     * @param email 사용자 이메일
     * @return 특정 이메일을 가진 UserDomain 인스턴스
     */
    public static UserDomain createWithEmail(String email) {
        return UserDomain.create(
                DEFAULT_USER_ID,
                email,
                DEFAULT_PASSWORD,
                DEFAULT_NAME,
                UserRole.USER
        );
    }

    /**
     * 특정 ID로 UserDomain 생성.
     *
     * @param userId 사용자 ID
     * @return 특정 ID를 가진 UserDomain 인스턴스
     */
    public static UserDomain createWithId(Long userId) {
        return UserDomain.create(
                userId,
                DEFAULT_EMAIL,
                DEFAULT_PASSWORD,
                DEFAULT_NAME,
                UserRole.USER
        );
    }

    /**
     * 관리자 UserDomain 생성.
     *
     * <p>관리자 권한으로 생성됩니다.</p>
     *
     * @return 관리자 UserDomain 인스턴스
     */
    public static UserDomain createAdmin() {
        return UserDomain.create(
                DEFAULT_USER_ID,
                "admin@example.com",
                DEFAULT_PASSWORD,
                "Admin User",
                UserRole.ADMIN
        );
    }

    /**
     * 계정 잠금 상태의 UserDomain 생성.
     *
     * <p>로그인 5회 실패로 계정이 잠긴 상태입니다.</p>
     *
     * @return 계정 잠금 상태의 UserDomain 인스턴스
     */
    public static UserDomain createLockedAccount() {
        UserDomain user = create();
        // 5회 로그인 실패로 계정 잠금
        for (int i = 0; i < 5; i++) {
            user.recordLoginFailure();
        }
        return user;
    }

    /**
     * Fixture 클래스는 인스턴스화할 수 없습니다.
     *
     * @throws AssertionError 항상 발생
     */
    private UserDomainFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
