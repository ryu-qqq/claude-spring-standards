package com.ryuqq.domain.template2.exception;

import com.ryuqq.domain.common.exception.DomainException;
import java.util.Map;

/**
 * ProductNotFoundException - Product를 찾을 수 없을 때 발생
 *
 * <p>HTTP 응답: 404 NOT FOUND
 *
 * @author development-team
 * @since 1.0.0
 */
public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(Long productId) {
        super(
                ProductErrorCode.PRODUCT_NOT_FOUND,
                String.format("Product not found: %d", productId),
                Map.of("productId", productId));
    }

    public ProductNotFoundException(String productName) {
        super(
                ProductErrorCode.PRODUCT_NOT_FOUND,
                String.format("Product not found with name: %s", productName),
                Map.of("productName", productName));
    }
}
