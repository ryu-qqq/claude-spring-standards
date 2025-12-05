package com.ryuqq.domain.template2.exception;

import com.ryuqq.domain.common.exception.DomainException;
import com.ryuqq.domain.template2.vo.ProductStatus;
import java.util.Map;

/**
 * ProductInvalidStateException - 상품 상태 전환 불가 시 발생
 *
 * <p>HTTP 응답: 400 BAD REQUEST
 *
 * @author development-team
 * @since 1.0.0
 */
public class ProductInvalidStateException extends DomainException {

    private ProductInvalidStateException(String message, Map<String, Object> args) {
        super(ProductErrorCode.CANNOT_CHANGE_STATUS, message, args);
    }

    public static ProductInvalidStateException cannotChangeStatus(
            Long productId, ProductStatus currentStatus, ProductStatus targetStatus) {
        return new ProductInvalidStateException(
                String.format(
                        "Cannot change product %d status from %s to %s",
                        productId, currentStatus, targetStatus),
                Map.of(
                        "productId",
                        productId,
                        "currentStatus",
                        currentStatus.name(),
                        "targetStatus",
                        targetStatus.name()));
    }

    public static ProductInvalidStateException cannotDeductStock(
            Long productId, ProductStatus currentStatus) {
        return new ProductInvalidStateException(
                String.format(
                        "Cannot deduct stock for product %d. Current status: %s",
                        productId, currentStatus),
                Map.of(
                        "productId",
                        productId,
                        "currentStatus",
                        currentStatus.name(),
                        "action",
                        "deductStock"));
    }

    public static ProductInvalidStateException cannotRestoreStock(
            Long productId, ProductStatus currentStatus) {
        return new ProductInvalidStateException(
                String.format(
                        "Cannot restore stock for product %d. Current status: %s",
                        productId, currentStatus),
                Map.of(
                        "productId",
                        productId,
                        "currentStatus",
                        currentStatus.name(),
                        "action",
                        "restoreStock"));
    }
}
