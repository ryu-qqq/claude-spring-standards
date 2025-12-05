package com.ryuqq.domain.template2.event;

import com.ryuqq.domain.common.event.DomainEvent;
import com.ryuqq.domain.template2.aggregate.product.Product;
import com.ryuqq.domain.template2.vo.ProductId;
import com.ryuqq.domain.template2.vo.StockQuantity;
import java.time.Instant;

/**
 * 상품 재고 소진 이벤트
 *
 * <p>상품 재고가 0이 되었을 때 발행됩니다.
 *
 * @param productId 상품 ID (VO)
 * @param stockQuantity 현재 재고 수량 (VO, 0)
 * @param occurredAt 이벤트 발생 시각
 */
public record ProductStockDepletedEvent(
        ProductId productId, StockQuantity stockQuantity, Instant occurredAt)
        implements DomainEvent {

    /**
     * Aggregate로부터 Event 생성
     *
     * @param product 상품 Aggregate
     * @param occurredAt 이벤트 발생 시각
     * @return 상품 재고 소진 이벤트
     */
    public static ProductStockDepletedEvent from(Product product, Instant occurredAt) {
        return new ProductStockDepletedEvent(product.id(), product.stockQuantity(), occurredAt);
    }
}
