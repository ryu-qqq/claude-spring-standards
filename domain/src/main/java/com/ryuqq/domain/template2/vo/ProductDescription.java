package com.ryuqq.domain.template2.vo;

/**
 * Product Description Value Object
 *
 * <p><strong>도메인 규칙</strong>:
 *
 * <ul>
 *   <li>상품 설명: 최대 1000자 이내
 *   <li>null 허용 (선택적 필드)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record ProductDescription(String value) {

    private static final int MAX_LENGTH = 1000;

    /** Compact Constructor (검증 로직) */
    public ProductDescription {
        if (value != null) {
            value = value.trim();
            if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        "상품 설명은 " + MAX_LENGTH + "자를 초과할 수 없습니다: " + value.length());
            }
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 상품 설명 (최대 1000자, null 허용)
     * @return ProductDescription
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static ProductDescription of(String value) {
        return new ProductDescription(value);
    }

    /**
     * null 값 생성
     *
     * @return null 값을 가진 ProductDescription
     */
    public static ProductDescription empty() {
        return new ProductDescription(null);
    }
}
