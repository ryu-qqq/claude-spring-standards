package com.ryuqq.domain.template2.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ProductValidationException - 상품 검증 실패 시 발생
 *
 * <p>HTTP 응답: 400 BAD REQUEST
 *
 * @author development-team
 * @since 1.0.0
 */
public class ProductValidationException extends DomainException {

    private ProductValidationException(String field, String reason, ProductErrorCode errorCode) {
        super(
                errorCode,
                String.format("Invalid %s: %s", field, reason),
                Map.of("field", field, "reason", reason));
    }

    public static ProductValidationException invalidPrice(Long invalidPrice) {
        return new ProductValidationException(
                "price",
                String.format("Price must not be negative: %d", invalidPrice),
                ProductErrorCode.INVALID_PRODUCT_PRICE);
    }

    public static ProductValidationException invalidStockQuantity(Long invalidQuantity) {
        return new ProductValidationException(
                "stockQuantity",
                String.format("Stock quantity must not be negative: %d", invalidQuantity),
                ProductErrorCode.INVALID_STOCK_QUANTITY);
    }
}
