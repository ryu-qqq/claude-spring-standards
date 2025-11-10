package com.ryuqq.domain.user;

import java.time.LocalDateTime;

/**
 * User Domain Aggregate Root.
 *
 * <p>사용자 인증 관련 비즈니스 로직을 캡슐화합니다.</p>
 *
 * <p>비즈니스 규칙:
 * <ul>
 *   <li>로그인 실패 5회 시 계정 자동 잠금</li>
 *   <li>로그인 성공 시 실패 카운트 리셋</li>
 *   <li>계정 잠금 해제는 관리자만 가능</li>
 * </ul>
 * </p>
 *
 * <p>Zero-Tolerance 규칙 준수:
 * <ul>
 *   <li>✅ Lombok 금지 (Pure Java getter/setter)</li>
 *   <li>✅ Law of Demeter 준수 (Tell, Don't Ask)</li>
 *   <li>✅ Immutable ID (userId는 final)</li>
 *   <li>✅ Factory Method 패턴 (Private Constructor)</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
public class UserDomain {

    private static final int MAX_LOGIN_FAIL_COUNT = 5;

    private final Long userId;
    private final String email;
    private final String encryptedPassword;
    private final String name;
    private final UserRole role;
    private int loginFailCount;
    private boolean accountLocked;
    private final LocalDateTime createdAt;

    /**
     * Private Constructor - Factory Method 패턴.
     *
     * <p>직접 생성을 방지하고 Factory Method를 통해서만 생성 가능합니다.</p>
     *
     * @param userId 사용자 ID
     * @param email 이메일
     * @param encryptedPassword 암호화된 패스워드
     * @param name 사용자 이름
     * @param role 사용자 역할
     */
    private UserDomain(
            Long userId,
            String email,
            String encryptedPassword,
            String name,
            UserRole role
    ) {
        this.userId = userId;
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.name = name;
        this.role = role;
        this.loginFailCount = 0;
        this.accountLocked = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Factory Method - 새로운 UserDomain 생성.
     *
     * <p>Named Constructor 패턴으로 생성 의도를 명확히 합니다.</p>
     *
     * @param userId 사용자 ID
     * @param email 이메일
     * @param encryptedPassword 암호화된 패스워드
     * @param name 사용자 이름
     * @param role 사용자 역할
     * @return 새로운 UserDomain 인스턴스
     */
    public static UserDomain create(
            Long userId,
            String email,
            String encryptedPassword,
            String name,
            UserRole role
    ) {
        return new UserDomain(userId, email, encryptedPassword, name, role);
    }

    /**
     * 로그인 실패 기록.
     *
     * <p>비즈니스 규칙:
     * <ul>
     *   <li>실패 카운트 증가</li>
     *   <li>5회 실패 시 계정 자동 잠금</li>
     * </ul>
     * </p>
     */
    public void recordLoginFailure() {
        this.loginFailCount++;
        if (this.loginFailCount >= MAX_LOGIN_FAIL_COUNT) {
            this.accountLocked = true;
        }
    }

    /**
     * 로그인 성공 기록.
     *
     * <p>비즈니스 규칙:
     * <ul>
     *   <li>실패 카운트 리셋</li>
     *   <li>단, 계정 잠금 해제는 관리자만 가능 (별도 메서드)</li>
     * </ul>
     * </p>
     */
    public void recordLoginSuccess() {
        this.loginFailCount = 0;
        // 계정 잠금 해제는 unlockAccount() 메서드로만 가능
    }

    /**
     * 계정 잠금 해제.
     *
     * <p>비즈니스 규칙:
     * <ul>
     *   <li>관리자만 가능 (Application Layer에서 권한 체크)</li>
     *   <li>실패 카운트도 0으로 리셋</li>
     * </ul>
     * </p>
     */
    public void unlockAccount() {
        this.accountLocked = false;
        this.loginFailCount = 0;
    }

    /**
     * Law of Demeter 준수 - Tell, Don't Ask 패턴.
     *
     * <p>❌ Bad: user.getEmail().toLowerCase()</p>
     * <p>✅ Good: user.getEmailInLowerCase()</p>
     *
     * <p>Getter 체이닝을 방지하고 비즈니스 의도를 명확히 합니다.</p>
     *
     * @return 소문자로 변환된 이메일
     */
    public String getEmailInLowerCase() {
        return this.email.toLowerCase();
    }

    /**
     * 계정 잠금 여부 확인.
     *
     * <p>Tell, Don't Ask 원칙: 상태 확인 메서드</p>
     *
     * @return 계정 잠금 여부
     */
    public boolean isAccountLocked() {
        return accountLocked;
    }

    // ========== Pure Java Getters (Lombok 금지) ==========

    /**
     * 사용자 ID 조회.
     *
     * @return 사용자 ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 이메일 조회.
     *
     * @return 이메일
     */
    public String getEmail() {
        return email;
    }

    /**
     * 사용자 이름 조회.
     *
     * @return 사용자 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 사용자 역할 조회.
     *
     * @return 사용자 역할
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * 로그인 실패 횟수 조회.
     *
     * @return 로그인 실패 횟수
     */
    public int getLoginFailCount() {
        return loginFailCount;
    }

    /**
     * 생성 시각 조회.
     *
     * @return 생성 시각
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
