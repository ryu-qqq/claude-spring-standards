package com.ryuqq.domain.template2.vo;

/**
 * Product Name Value Object
 *
 * <p><strong>도메인 규칙</strong>:
 *
 * <ul>
 *   <li>상품명: 1자 이상 100자 이내
 *   <li>null 또는 빈 문자열 불가
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record ProductName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    /** Compact Constructor (검증 로직) */
    public ProductName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품명은 null이거나 빈 문자열일 수 없습니다.");
        }

        value = value.trim();

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "상품명은 " + MIN_LENGTH + "자 이상이어야 합니다: " + value.length());
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "상품명은 " + MAX_LENGTH + "자를 초과할 수 없습니다: " + value.length());
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 상품명 (1자 이상 100자 이내)
     * @return ProductName
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static ProductName of(String value) {
        return new ProductName(value);
    }
}
