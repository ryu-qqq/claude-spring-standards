package com.ryuqq.domain.order;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Order Line Item Entity
 *
 * 주문 항목을 나타내는 Entity (Order Aggregate 내부)
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public class OrderLineItem {

    private final Long productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal subtotal;

    /**
     * OrderLineItem 생성자 (외부 직접 호출 금지)
     */
    protected OrderLineItem(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
    ) {
        validateLineItem(productId, productName, quantity, unitPrice);

        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * 새로운 주문 항목 생성 (Static Factory)
     *
     * @param productId 상품 ID (Long FK 전략)
     * @param productName 상품명
     * @param quantity 수량
     * @param unitPrice 단가
     * @return 생성된 OrderLineItem
     */
    public static OrderLineItem of(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
    ) {
        return new OrderLineItem(productId, productName, quantity, unitPrice);
    }

    /**
     * 주문 항목 검증 (Private Helper)
     */
    private static void validateLineItem(
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice
    ) {
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("단가는 0보다 커야 합니다.");
        }
    }

    // Getters (Lombok 사용 금지)

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineItem that = (OrderLineItem) o;
        return quantity == that.quantity &&
               Objects.equals(productId, that.productId) &&
               Objects.equals(productName, that.productName) &&
               Objects.equals(unitPrice, that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, quantity, unitPrice);
    }
}
