package com.ryuqq.domain.template2.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * InsufficientStockException - 재고 부족 시 발생
 *
 * <p>HTTP 응답: 400 BAD REQUEST
 *
 * @author development-team
 * @since 1.0.0
 */
public class InsufficientStockException extends DomainException {

    public InsufficientStockException(Long productId, Long currentStock, Long requiredQuantity) {
        super(
                ProductErrorCode.INSUFFICIENT_STOCK,
                String.format(
                        "Insufficient stock for product %d. Current: %d, Required: %d",
                        productId, currentStock, requiredQuantity),
                Map.of(
                        "productId",
                        productId,
                        "currentStock",
                        currentStock,
                        "requiredQuantity",
                        requiredQuantity));
    }
}
