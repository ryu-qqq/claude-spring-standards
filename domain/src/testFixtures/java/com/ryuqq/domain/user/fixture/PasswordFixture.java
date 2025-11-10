package com.ryuqq.domain.user.fixture;

import com.ryuqq.domain.user.Password;

/**
 * TestFixture for Password Value Object.
 *
 * <p>Object Mother 패턴으로 테스트용 Password 객체를 생성합니다.</p>
 *
 * <p>생성 패턴:
 * <ul>
 *   <li>{@link #create()} - 기본 패스워드 (Test123!@#)</li>
 *   <li>{@link #createWithValue(String)} - 특정 패스워드 값</li>
 *   <li>{@link #createStrong()} - 강력한 패스워드</li>
 *   <li>{@link #createMedium()} - 중간 강도 패스워드</li>
 *   <li>{@link #createMinimal()} - 최소 요구사항 패스워드</li>
 * </ul>
 * </p>
 *
 * <p>패스워드 규칙:
 * <ul>
 *   <li>최소 8자 이상</li>
 *   <li>영문 + 숫자 + 특수문자 조합 필수</li>
 * </ul>
 * </p>
 *
 * <p>Zero-Tolerance 규칙 준수:
 * <ul>
 *   <li>✅ All methods are static</li>
 *   <li>✅ Private constructor to prevent instantiation</li>
 *   <li>✅ *Fixture suffix naming convention</li>
 *   <li>✅ create*() method naming convention</li>
 *   <li>✅ Java 21 Record pattern (Password is Record)</li>
 * </ul>
 * </p>
 *
 * @author Claude Code
 * @since 2025-11-10
 */
public class PasswordFixture {

    private static final String DEFAULT_PASSWORD = "Test123!@#";

    /**
     * 기본 Password 생성.
     *
     * <p>Test123!@# 패스워드를 반환합니다.</p>
     *
     * @return 기본 Password 인스턴스
     */
    public static Password create() {
        return new Password(DEFAULT_PASSWORD);
    }

    /**
     * 특정 패스워드 값으로 Password 생성.
     *
     * @param value 패스워드 값 (최소 8자, 영문+숫자+특수문자)
     * @return 특정 값을 가진 Password 인스턴스
     * @throws com.ryuqq.domain.user.exception.WeakPasswordException 약한 패스워드일 경우
     */
    public static Password createWithValue(String value) {
        return new Password(value);
    }

    /**
     * 강력한 Password 생성.
     *
     * <p>VerySecure@2024!@# 패스워드를 반환합니다.</p>
     *
     * @return 강력한 Password 인스턴스
     */
    public static Password createStrong() {
        return new Password("VerySecure@2024!@#");
    }

    /**
     * 중간 강도 Password 생성.
     *
     * <p>Medium1!@# 패스워드를 반환합니다.</p>
     *
     * @return 중간 강도 Password 인스턴스
     */
    public static Password createMedium() {
        return new Password("Medium1!@#");
    }

    /**
     * 최소 요구사항 Password 생성.
     *
     * <p>Pass123! 패스워드를 반환합니다.</p>
     * <p>최소 8자, 영문+숫자+특수문자 조합을 만족하는 최소 조건입니다.</p>
     *
     * @return 최소 요구사항 Password 인스턴스
     */
    public static Password createMinimal() {
        return new Password("Pass123!");
    }

    /**
     * Fixture 클래스는 인스턴스화할 수 없습니다.
     *
     * @throws AssertionError 항상 발생
     */
    private PasswordFixture() {
        throw new AssertionError("Fixture 클래스는 인스턴스화할 수 없습니다.");
    }
}
