package com.ryuqq.domain.zerotolerance.vo;

/**
 * ZeroToleranceType - Zero Tolerance 규칙 유형 Value Object
 *
 * <p>프로젝트에서 절대로 허용하지 않는 규칙 유형을 정의합니다.
 *
 * @author ryu-qqq
 */
public record ZeroToleranceType(String value) {

    private static final int MAX_LENGTH = 50;

    /** Compact Constructor - 유효성 검증 */
    public ZeroToleranceType {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ZeroToleranceType must not be blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ZeroToleranceType must not exceed " + MAX_LENGTH + " characters");
        }
    }

    /** 정적 팩토리 메서드 */
    public static ZeroToleranceType of(String value) {
        return new ZeroToleranceType(value);
    }

    // 자주 사용되는 타입 상수 메서드
    public static ZeroToleranceType lombokInDomain() {
        return new ZeroToleranceType("LOMBOK_IN_DOMAIN");
    }

    public static ZeroToleranceType setterUsage() {
        return new ZeroToleranceType("SETTER_USAGE");
    }

    public static ZeroToleranceType getterChaining() {
        return new ZeroToleranceType("GETTER_CHAINING");
    }

    public static ZeroToleranceType transactionInAdapter() {
        return new ZeroToleranceType("TRANSACTION_IN_ADAPTER");
    }
}
