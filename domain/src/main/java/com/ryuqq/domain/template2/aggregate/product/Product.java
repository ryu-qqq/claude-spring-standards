package com.ryuqq.domain.template2.aggregate.product;

import com.ryuqq.domain.common.event.DomainEvent;
import com.ryuqq.domain.template2.event.ProductCreatedEvent;
import com.ryuqq.domain.template2.event.ProductStatusChangedEvent;
import com.ryuqq.domain.template2.event.ProductStockDepletedEvent;
import com.ryuqq.domain.template2.exception.InsufficientStockException;
import com.ryuqq.domain.template2.exception.ProductInvalidStateException;
import com.ryuqq.domain.template2.vo.Money;
import com.ryuqq.domain.template2.vo.ProductDescription;
import com.ryuqq.domain.template2.vo.ProductId;
import com.ryuqq.domain.template2.vo.ProductName;
import com.ryuqq.domain.template2.vo.ProductStatus;
import com.ryuqq.domain.template2.vo.StockQuantity;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Product Aggregate Root
 *
 * <p><strong>비즈니스 규칙</strong>:
 *
 * <ul>
 *   <li>상품 생성 시 기본 상태는 DRAFT
 *   <li>재고 차감은 판매 중(ON_SALE) 상태에서만 가능
 *   <li>재고가 0이 되면 자동으로 OUT_OF_STOCK 상태로 변경
 *   <li>재고 복구 시 OUT_OF_STOCK 상태면 자동으로 ON_SALE 상태로 변경
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class Product {

    // ==================== 필드 ====================

    private final ProductId id;
    private final ProductName name;
    private final ProductDescription description;
    private Money price;
    private StockQuantity stockQuantity;
    private ProductStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private final Clock clock;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // ==================== 생성자 (private) ====================

    private Product(
            ProductId id,
            ProductName name,
            ProductDescription description,
            Money price,
            StockQuantity stockQuantity,
            ProductStatus status,
            Instant createdAt,
            Instant updatedAt,
            Clock clock) {
        // 내부 불변조건 검증 (개발자 오류)
        if (name == null) {
            throw new IllegalArgumentException("ProductName must not be null - this is a bug");
        }
        if (description == null) {
            throw new IllegalArgumentException(
                    "ProductDescription must not be null - this is a bug");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price must not be null - this is a bug");
        }
        if (stockQuantity == null) {
            throw new IllegalArgumentException("StockQuantity must not be null - this is a bug");
        }
        if (status == null) {
            throw new IllegalArgumentException("ProductStatus must not be null - this is a bug");
        }
        if (clock == null) {
            throw new IllegalArgumentException("Clock must not be null - this is a bug");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.clock = clock;
    }

    // ==================== 정적 팩토리 메서드 ====================

    /**
     * 신규 상품 생성
     *
     * @param name 상품명
     * @param description 상품 설명
     * @param price 상품 가격
     * @param stockQuantity 초기 재고 수량
     * @param clock Clock (시간 생성용)
     * @return 생성된 상품
     */
    public static Product forNew(
            ProductName name,
            ProductDescription description,
            Money price,
            StockQuantity stockQuantity,
            Clock clock) {
        Instant now = clock.instant();
        Product product =
                new Product(
                        ProductId.forNew(),
                        name,
                        description,
                        price,
                        stockQuantity,
                        ProductStatus.DRAFT,
                        now,
                        now,
                        clock);
        product.registerEvent(ProductCreatedEvent.from(product, now));
        return product;
    }

    /**
     * 기존 상품 조회/참조용 생성
     *
     * @param id 상품 ID
     * @param name 상품명
     * @param description 상품 설명
     * @param price 상품 가격
     * @param stockQuantity 재고 수량
     * @param status 상품 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @param clock Clock
     * @return 상품
     */
    public static Product of(
            ProductId id,
            ProductName name,
            ProductDescription description,
            Money price,
            StockQuantity stockQuantity,
            ProductStatus status,
            Instant createdAt,
            Instant updatedAt,
            Clock clock) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }
        return new Product(
                id, name, description, price, stockQuantity, status, createdAt, updatedAt, clock);
    }

    /**
     * 영속성 복원용 생성 (Event 없음)
     *
     * @param id 상품 ID
     * @param name 상품명
     * @param description 상품 설명
     * @param price 상품 가격
     * @param stockQuantity 재고 수량
     * @param status 상품 상태
     * @param createdAt 생성 시각
     * @param updatedAt 수정 시각
     * @param clock Clock
     * @return 상품
     */
    public static Product reconstitute(
            ProductId id,
            ProductName name,
            ProductDescription description,
            Money price,
            StockQuantity stockQuantity,
            ProductStatus status,
            Instant createdAt,
            Instant updatedAt,
            Clock clock) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }
        return new Product(
                id, name, description, price, stockQuantity, status, createdAt, updatedAt, clock);
    }

    // ==================== 비즈니스 메서드 ====================

    /**
     * 상품 판매 시작 (DRAFT -> ON_SALE)
     *
     * @throws ProductInvalidStateException DRAFT 상태가 아닌 경우
     */
    public void startSale() {
        if (this.status != ProductStatus.DRAFT) {
            throw ProductInvalidStateException.cannotChangeStatus(
                    this.id.value(), this.status, ProductStatus.ON_SALE);
        }

        ProductStatus previousStatus = this.status;
        this.status = ProductStatus.ON_SALE;
        this.updatedAt = clock.instant();

        Instant now = clock.instant();
        registerEvent(ProductStatusChangedEvent.from(this, previousStatus, now));
    }

    /**
     * 상품 판매 중지 (ON_SALE -> SUSPENDED)
     *
     * @throws ProductInvalidStateException ON_SALE 상태가 아닌 경우
     */
    public void suspend() {
        if (this.status != ProductStatus.ON_SALE) {
            throw ProductInvalidStateException.cannotChangeStatus(
                    this.id.value(), this.status, ProductStatus.SUSPENDED);
        }

        ProductStatus previousStatus = this.status;
        this.status = ProductStatus.SUSPENDED;
        this.updatedAt = clock.instant();

        Instant now = clock.instant();
        registerEvent(ProductStatusChangedEvent.from(this, previousStatus, now));
    }

    /** 상품 판매 종료 (임의 상태 -> DISCONTINUED) */
    public void discontinue() {
        if (this.status == ProductStatus.DISCONTINUED) {
            return; // 이미 종료된 상품
        }

        ProductStatus previousStatus = this.status;
        this.status = ProductStatus.DISCONTINUED;
        this.updatedAt = clock.instant();

        Instant now = clock.instant();
        registerEvent(ProductStatusChangedEvent.from(this, previousStatus, now));
    }

    /**
     * 재고 차감
     *
     * @param quantity 차감할 수량
     * @throws ProductInvalidStateException 판매 중 상태가 아닌 경우
     * @throws InsufficientStockException 재고가 부족한 경우
     */
    public void deductStock(StockQuantity quantity) {
        if (!canDeductStock()) {
            throw ProductInvalidStateException.cannotDeductStock(this.id.value(), this.status);
        }

        if (this.stockQuantity.isInsufficient(quantity)) {
            throw new InsufficientStockException(
                    this.id.value(), this.stockQuantity.value(), quantity.value());
        }

        this.stockQuantity = this.stockQuantity.subtract(quantity);
        this.updatedAt = clock.instant();

        // 재고가 0이 되면 자동으로 OUT_OF_STOCK 상태로 변경
        if (!this.stockQuantity.isAvailable()) {
            ProductStatus previousStatus = this.status;
            this.status = ProductStatus.OUT_OF_STOCK;

            Instant now = clock.instant();
            registerEvent(ProductStockDepletedEvent.from(this, now));
            registerEvent(ProductStatusChangedEvent.from(this, previousStatus, now));
        }
    }

    /**
     * 재고 복구
     *
     * @param quantity 복구할 수량
     * @throws ProductInvalidStateException 재고 복구 불가능한 상태인 경우
     */
    public void restoreStock(StockQuantity quantity) {
        if (!canRestoreStock()) {
            throw ProductInvalidStateException.cannotRestoreStock(this.id.value(), this.status);
        }

        this.stockQuantity = this.stockQuantity.add(quantity);
        this.updatedAt = clock.instant();

        // OUT_OF_STOCK 상태에서 재고가 복구되면 자동으로 ON_SALE 상태로 변경
        if (this.status == ProductStatus.OUT_OF_STOCK && this.stockQuantity.isAvailable()) {
            ProductStatus previousStatus = this.status;
            this.status = ProductStatus.ON_SALE;

            Instant now = clock.instant();
            registerEvent(ProductStatusChangedEvent.from(this, previousStatus, now));
        }
    }

    /**
     * 가격 변경
     *
     * @param newPrice 새로운 가격
     * @throws ProductInvalidStateException 판매 종료된 상품인 경우
     */
    public void changePrice(Money newPrice) {
        if (this.status == ProductStatus.DISCONTINUED) {
            throw ProductInvalidStateException.cannotChangeStatus(
                    this.id.value(), this.status, this.status // 상태 변경 불가
                    );
        }

        this.price = newPrice;
        this.updatedAt = clock.instant();
    }

    // ==================== 판단 메서드 (도메인 객체가 스스로 판단) ====================

    private boolean canDeductStock() {
        return this.status == ProductStatus.ON_SALE;
    }

    private boolean canRestoreStock() {
        return this.status == ProductStatus.ON_SALE || this.status == ProductStatus.OUT_OF_STOCK;
    }

    /**
     * 판매 가능한 상품인지 확인
     *
     * @return 판매 가능하면 true
     */
    public boolean isSellable() {
        return this.status.isSellable() && this.stockQuantity.isAvailable();
    }

    /**
     * 재고가 있는지 확인
     *
     * @return 재고가 있으면 true
     */
    public boolean hasStock() {
        return this.stockQuantity.isAvailable();
    }

    /**
     * 재고 부족 여부 확인
     *
     * @param required 필요한 수량
     * @return 재고가 부족하면 true
     */
    public boolean isStockInsufficient(StockQuantity required) {
        return this.stockQuantity.isInsufficient(required);
    }

    // ==================== Event 관리 ====================

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }

    // ==================== Getter ====================

    public ProductId id() {
        return id;
    }

    public ProductName name() {
        return name;
    }

    public ProductDescription description() {
        return description;
    }

    public Money price() {
        return price;
    }

    public StockQuantity stockQuantity() {
        return stockQuantity;
    }

    public ProductStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
