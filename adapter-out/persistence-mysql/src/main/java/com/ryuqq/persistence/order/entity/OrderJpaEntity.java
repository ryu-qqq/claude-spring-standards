package com.ryuqq.persistence.order.entity;

import com.ryuqq.persistence.common.BaseAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order JPA Entity
 *
 * 주문 정보를 저장하는 JPA Entity
 *
 * Long FK 전략:
 * - Customer와의 관계를 @ManyToOne이 아닌 Long customerId로 표현
 * - JPA 관계 어노테이션 (@ManyToOne, @OneToMany, @OneToOne, @ManyToMany) 금지
 *
 * @author sangwon-ryu
 * @since 1.0
 */
@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_ordered_at", columnList = "ordered_at")
    }
)
public class OrderJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    /**
     * 고객 ID (Long FK 전략)
     *
     * ❌ @ManyToOne 사용 금지
     * ✅ Long FK 사용
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 255)
    private String customerEmail;

    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    @Column(name = "shipping_zip_code", length = 20)
    private String shippingZipCode;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatusEnum status;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    /**
     * 주문 항목 목록
     *
     * ❌ @OneToMany 사용 금지
     * ✅ 별도 조회 또는 컬렉션 없이 관리
     *
     * 참고: 필요 시 별도 Repository에서 orderId로 조회
     */
    @Transient
    private List<OrderLineItemJpaEntity> lineItems = new ArrayList<>();

    /**
     * JPA 전용 생성자 (Protected)
     *
     * ❌ Public constructor 금지
     * ✅ Protected constructor + Static Factory Method
     */
    protected OrderJpaEntity() {
        // JPA only
    }

    /**
     * 엔티티 생성 (Static Factory Method)
     *
     * @return OrderJpaEntity Builder
     */
    public static OrderJpaEntityBuilder builder() {
        return new OrderJpaEntityBuilder();
    }

    /**
     * Builder Pattern (Without Lombok)
     *
     * ❌ Lombok @Builder 금지
     * ✅ Plain Java Builder
     */
    public static class OrderJpaEntityBuilder {
        private String orderId;
        private Long customerId;
        private String customerName;
        private String customerEmail;
        private String shippingAddress;
        private String shippingZipCode;
        private String shippingCity;
        private OrderStatusEnum status;
        private BigDecimal totalAmount;
        private LocalDateTime orderedAt;

        public OrderJpaEntityBuilder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderJpaEntityBuilder customerId(Long customerId) {
            this.customerId = customerId;
            return this;
        }

        public OrderJpaEntityBuilder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public OrderJpaEntityBuilder customerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        public OrderJpaEntityBuilder shippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public OrderJpaEntityBuilder shippingZipCode(String shippingZipCode) {
            this.shippingZipCode = shippingZipCode;
            return this;
        }

        public OrderJpaEntityBuilder shippingCity(String shippingCity) {
            this.shippingCity = shippingCity;
            return this;
        }

        public OrderJpaEntityBuilder status(OrderStatusEnum status) {
            this.status = status;
            return this;
        }

        public OrderJpaEntityBuilder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public OrderJpaEntityBuilder orderedAt(LocalDateTime orderedAt) {
            this.orderedAt = orderedAt;
            return this;
        }

        public OrderJpaEntity build() {
            OrderJpaEntity entity = new OrderJpaEntity();
            entity.orderId = this.orderId;
            entity.customerId = this.customerId;
            entity.customerName = this.customerName;
            entity.customerEmail = this.customerEmail;
            entity.shippingAddress = this.shippingAddress;
            entity.shippingZipCode = this.shippingZipCode;
            entity.shippingCity = this.shippingCity;
            entity.status = this.status;
            entity.totalAmount = this.totalAmount;
            entity.orderedAt = this.orderedAt;
            return entity;
        }
    }

    // Getters (Lombok 사용 금지)

    public String getOrderId() {
        return orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getShippingZipCode() {
        return shippingZipCode;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public List<OrderLineItemJpaEntity> getLineItems() {
        return lineItems;
    }

    /**
     * 상태 변경 메서드 (Setter 대체)
     *
     * ❌ public void setStatus(OrderStatusEnum status) 금지
     * ✅ 비즈니스 의미가 명확한 메서드
     */
    public void changeStatus(OrderStatusEnum newStatus) {
        this.status = newStatus;
    }

    /**
     * 주문 취소 (비즈니스 메서드)
     */
    public void cancel(String reason) {
        this.status = OrderStatusEnum.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }
}
