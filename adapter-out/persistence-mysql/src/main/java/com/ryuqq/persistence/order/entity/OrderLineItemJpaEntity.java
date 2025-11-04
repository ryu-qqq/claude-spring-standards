package com.ryuqq.persistence.order.entity;

import com.ryuqq.persistence.common.BaseAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Order Line Item JPA Entity
 *
 * 주문 항목 정보를 저장하는 JPA Entity
 *
 * Long FK 전략:
 * - Order와의 관계를 @ManyToOne이 아닌 String orderId로 표현
 * - Product와의 관계를 @ManyToOne이 아닌 Long productId로 표현
 *
 * @author sangwon-ryu
 * @since 1.0
 */
@Entity
@Table(
    name = "order_line_items",
    indexes = {
        @Index(name = "idx_order_id", columnList = "order_id"),
        @Index(name = "idx_product_id", columnList = "product_id")
    }
)
public class OrderLineItemJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_item_id")
    private Long lineItemId;

    /**
     * 주문 ID (Long FK 전략)
     *
     * ❌ @ManyToOne 사용 금지
     * ✅ String FK 사용 (Order의 PK 타입에 맞춤)
     */
    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    /**
     * 상품 ID (Long FK 전략)
     *
     * ❌ @ManyToOne 사용 금지
     * ✅ Long FK 사용
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    /**
     * JPA 전용 생성자 (Protected)
     *
     * ❌ Public constructor 금지
     * ✅ Protected constructor + Static Factory Method
     */
    protected OrderLineItemJpaEntity() {
        // JPA only
    }

    /**
     * 엔티티 생성 (Static Factory Method)
     *
     * @return OrderLineItemJpaEntity Builder
     */
    public static OrderLineItemJpaEntityBuilder builder() {
        return new OrderLineItemJpaEntityBuilder();
    }

    /**
     * Builder Pattern (Without Lombok)
     *
     * ❌ Lombok @Builder 금지
     * ✅ Plain Java Builder
     */
    public static class OrderLineItemJpaEntityBuilder {
        private String orderId;
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;

        public OrderLineItemJpaEntityBuilder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderLineItemJpaEntityBuilder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public OrderLineItemJpaEntityBuilder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public OrderLineItemJpaEntityBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderLineItemJpaEntityBuilder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public OrderLineItemJpaEntityBuilder subtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public OrderLineItemJpaEntity build() {
            OrderLineItemJpaEntity entity = new OrderLineItemJpaEntity();
            entity.orderId = this.orderId;
            entity.productId = this.productId;
            entity.productName = this.productName;
            entity.quantity = this.quantity;
            entity.unitPrice = this.unitPrice;
            entity.subtotal = this.subtotal;
            return entity;
        }
    }

    // Getters (Lombok 사용 금지)

    public Long getLineItemId() {
        return lineItemId;
    }

    public String getOrderId() {
        return orderId;
    }

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
}
