package com.ryuqq.persistence.order.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order JPA Entity (Hook OFF 스타일)
 *
 * ❌ @ManyToOne, @OneToMany 사용 (Long FK 전략 위반)
 * ❌ Lombok 사용
 * ❌ Public Constructor
 * ❌ Public Setter
 */
@Data  // ❌ Lombok
@Builder  // ❌ Lombok
@NoArgsConstructor  // ❌ Public no-args constructor
@AllArgsConstructor  // ❌ Public all-args constructor
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    /**
     * ❌ @ManyToOne 사용 (Long FK 전략 위반)
     * ❌ N+1 문제 발생 가능
     * ❌ 지연 로딩 문제
     */
    @ManyToOne(fetch = FetchType.LAZY)  // ❌ JPA 관계 어노테이션
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;  // ❌ Entity 직접 참조

    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    /**
     * ❌ @OneToMany 사용 (양방향 관계)
     * ❌ N+1 문제 발생 가능
     * ❌ 영속성 전이 복잡성
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)  // ❌ JPA 관계
    @Builder.Default
    private List<OrderLineItemEntity> lineItems = new ArrayList<>();

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // ❌ Public setter (Lombok @Data가 생성)
    // order.setStatus("CANCELLED") 가능

    /**
     * 비즈니스 메서드 추가 시도
     * 하지만 Lombok @Data의 setter와 충돌 가능
     */
    public void placeOrder() {
        this.status = "PLACED";
        this.orderedAt = LocalDateTime.now();
    }

    public void cancelOrder(String reason) {
        // ❌ Setter가 public이므로 캡슐화 깨짐
        // 외부에서 order.setStatus() 직접 호출 가능
        this.status = "CANCELLED";
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    // ❌ Getter 체이닝 가능
    // order.getCustomer().getAddress().getZipCode()
}

/**
 * Customer JPA Entity (Hook OFF 스타일)
 *
 * ❌ Lombok 사용
 * ❌ 양방향 관계
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /**
     * ❌ @ManyToOne 사용
     * ❌ Getter 체이닝: customer.getAddress().getZipCode()
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    /**
     * ❌ 양방향 관계 설정
     * ❌ 순환 참조 위험
     */
    @OneToMany(mappedBy = "customer")
    @Builder.Default
    private List<OrderEntity> orders = new ArrayList<>();
}

/**
 * Order Line Item JPA Entity (Hook OFF 스타일)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_line_items")
class OrderLineItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_item_id")
    private Long lineItemId;

    /**
     * ❌ @ManyToOne 사용 (양방향 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    /**
     * ❌ @ManyToOne 사용
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;
}

@Data
@Entity
@Table(name = "products")
class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;
    private BigDecimal price;
}

@Data
@Entity
@Table(name = "addresses")
class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String street;
    private String city;
    private String zipCode;
}
