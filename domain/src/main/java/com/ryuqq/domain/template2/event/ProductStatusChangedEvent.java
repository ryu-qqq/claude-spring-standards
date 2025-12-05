package com.ryuqq.domain.template2.event;

import com.ryuqq.domain.common.event.DomainEvent;
import com.ryuqq.domain.template2.aggregate.product.Product;
import com.ryuqq.domain.template2.vo.ProductId;
import com.ryuqq.domain.template2.vo.ProductStatus;
import java.time.Instant;

/**
 * 상품 상태 변경 이벤트
 *
 * <p>상품 상태가 변경되었을 때 발행됩니다.
 *
 * @param productId 상품 ID (VO)
 * @param previousStatus 이전 상태
 * @param currentStatus 현재 상태
 * @param occurredAt 이벤트 발생 시각
 */
public record ProductStatusChangedEvent(
        ProductId productId,
        ProductStatus previousStatus,
        ProductStatus currentStatus,
        Instant occurredAt)
        implements DomainEvent {

    /**
     * Aggregate로부터 Event 생성
     *
     * @param product 상품 Aggregate
     * @param previousStatus 이전 상태
     * @param occurredAt 이벤트 발생 시각
     * @return 상품 상태 변경 이벤트
     */
    public static ProductStatusChangedEvent from(
            Product product, ProductStatus previousStatus, Instant occurredAt) {
        return new ProductStatusChangedEvent(
                product.id(), previousStatus, product.status(), occurredAt);
    }
}
