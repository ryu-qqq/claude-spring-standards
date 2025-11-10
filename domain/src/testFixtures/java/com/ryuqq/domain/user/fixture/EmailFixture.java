package com.ryuqq.domain.user.fixture;

import com.ryuqq.domain.user.Email;

/**
 * TestFixture for Email Value Object.
 *
 * <p>Object Mother 패턴으로 테스트용 Email 객체를 생성합니다.</p>
 *
 * <p>생성 패턴:
 * <ul>
 *   <li>{@link #create()} - 기본 이메일 (test@example.com)</li>
 *   <li>{@link #createWithValue(String)} - 특정 이메일 값</li>
 *   <li>{@link #createAdmin()} - 관리자 이메일</li>
 *   <li>{@link #createGmail()} - Gmail 이메일</li>
 *   <li>{@link #createCompanyEmail()} - 회사 이메일</li>
 * </ul>
 * </p>
 *
 * <p>Zero-Tolerance 규칙 준수:
 * <ul>
 *   <li>✅ All methods are static</li>
 *   <li>✅ Private constructor to prevent instantiation</li>
 *   <li>✅ *Fixture suffix naming convention</li>
 *   <li>✅ create*() method naming convention</li>
 *   <li>✅ Java 21 Record pattern (Email is Record)</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
public class EmailFixture {

    private static final String DEFAULT_EMAIL = "test@example.com";

    /**
     * 기본 Email 생성.
     *
     * <p>test@example.com 이메일을 반환합니다.</p>
     *
     * @return 기본 Email 인스턴스
     */
    public static Email create() {
        return new Email(DEFAULT_EMAIL);
    }

    /**
     * 특정 이메일 값으로 Email 생성.
     *
     * @param value 이메일 값
     * @return 특정 값을 가진 Email 인스턴스
     */
    public static Email createWithValue(String value) {
        return new Email(value);
    }

    /**
     * 관리자 Email 생성.
     *
     * <p>admin@example.com 이메일을 반환합니다.</p>
     *
     * @return 관리자 Email 인스턴스
     */
    public static Email createAdmin() {
        return new Email("admin@example.com");
    }

    /**
     * Gmail Email 생성.
     *
     * <p>test.user@gmail.com 이메일을 반환합니다.</p>
     *
     * @return Gmail Email 인스턴스
     */
    public static Email createGmail() {
        return new Email("test.user@gmail.com");
    }

    /**
     * 회사 Email 생성.
     *
     * <p>employee@company.com 이메일을 반환합니다.</p>
     *
     * @return 회사 Email 인스턴스
     */
    public static Email createCompanyEmail() {
        return new Email("employee@company.com");
    }

    /**
     * Fixture 클래스는 인스턴스화할 수 없습니다.
     *
     * @throws AssertionError 항상 발생
     */
    private EmailFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
