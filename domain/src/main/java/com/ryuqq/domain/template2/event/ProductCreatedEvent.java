package com.ryuqq.domain.template2.event;

import com.ryuqq.domain.common.event.DomainEvent;
import com.ryuqq.domain.template2.aggregate.product.Product;
import com.ryuqq.domain.template2.vo.Money;
import com.ryuqq.domain.template2.vo.ProductId;
import com.ryuqq.domain.template2.vo.ProductName;
import com.ryuqq.domain.template2.vo.ProductStatus;
import com.ryuqq.domain.template2.vo.StockQuantity;
import java.time.Instant;

/**
 * 상품 생성 이벤트
 *
 * <p>상품이 성공적으로 생성되었을 때 발행됩니다.
 *
 * @param productId 상품 ID (VO)
 * @param productName 상품명 (VO)
 * @param price 상품 가격 (VO)
 * @param stockQuantity 재고 수량 (VO)
 * @param status 상품 상태 (VO/Enum)
 * @param occurredAt 이벤트 발생 시각
 */
public record ProductCreatedEvent(
        ProductId productId,
        ProductName productName,
        Money price,
        StockQuantity stockQuantity,
        ProductStatus status,
        Instant occurredAt)
        implements DomainEvent {

    /**
     * Aggregate로부터 Event 생성
     *
     * <p>이 메서드만 사용하여 Event를 생성해야 합니다.
     *
     * <p>Aggregate가 Clock에서 얻은 시간을 전달받아 사용합니다.
     *
     * @param product 상품 Aggregate
     * @param occurredAt 이벤트 발생 시각 (Aggregate의 clock.instant()에서 전달)
     * @return 상품 생성 이벤트
     */
    public static ProductCreatedEvent from(Product product, Instant occurredAt) {
        return new ProductCreatedEvent(
                product.id(),
                product.name(),
                product.price(),
                product.stockQuantity(),
                product.status(),
                occurredAt);
    }
}
