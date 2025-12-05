package com.ryuqq.domain.template2.vo;

/**
 * Stock Quantity Value Object
 *
 * <p><strong>도메인 규칙</strong>: 재고 수량은 0 이상이어야 한다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record StockQuantity(Long value) {

    public static final StockQuantity ZERO = StockQuantity.of(0L);

    /** Compact Constructor (검증 로직) */
    public StockQuantity {
        if (value == null) {
            throw new IllegalArgumentException("재고 수량은 null일 수 없습니다.");
        }
        if (value < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다: " + value);
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 재고 수량 (null 불가, 0 이상)
     * @return StockQuantity
     * @throws IllegalArgumentException value가 null이거나 음수인 경우
     */
    public static StockQuantity of(Long value) {
        return new StockQuantity(value);
    }

    /**
     * 재고 수량 더하기
     *
     * @param other 더할 수량
     * @return 합계
     */
    public StockQuantity add(StockQuantity other) {
        return new StockQuantity(this.value + other.value);
    }

    /**
     * 재고 수량 빼기
     *
     * @param other 뺄 수량
     * @return 차액
     * @throws IllegalArgumentException 결과가 음수인 경우
     */
    public StockQuantity subtract(StockQuantity other) {
        return new StockQuantity(this.value - other.value);
    }

    /**
     * 재고가 있는지 확인
     *
     * @return 재고가 0보다 크면 true
     */
    public boolean isAvailable() {
        return value > 0;
    }

    /**
     * 재고 부족 여부 확인
     *
     * @param required 필요한 수량
     * @return 재고가 부족하면 true
     */
    public boolean isInsufficient(StockQuantity required) {
        return this.value < required.value;
    }
}
