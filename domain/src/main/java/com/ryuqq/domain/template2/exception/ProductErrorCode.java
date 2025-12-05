package com.ryuqq.domain.template2.exception;

import com.ryuqq.domain.common.exception.ErrorCode;

/**
 * ProductErrorCode - Product Bounded Context 에러 코드
 *
 * <p>Product 도메인에서 발생하는 모든 비즈니스 예외의 에러 코드를 정의합니다.
 *
 * <p><strong>에러 코드 규칙:</strong>
 *
 * <ul>
 *   <li>형식: PRODUCT-{3자리 숫자}
 *   <li>HTTP 상태 코드는 int 사용 (Spring HttpStatus 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum ProductErrorCode implements ErrorCode {

    // === 404 Not Found ===
    PRODUCT_NOT_FOUND("PRODUCT-001", 404, "Product not found"),

    // === 400 Bad Request (Validation) ===
    INVALID_PRODUCT_NAME("PRODUCT-010", 400, "Invalid product name"),
    INVALID_PRODUCT_DESCRIPTION("PRODUCT-011", 400, "Invalid product description"),
    INVALID_PRODUCT_PRICE("PRODUCT-012", 400, "Invalid product price"),
    INVALID_STOCK_QUANTITY("PRODUCT-013", 400, "Invalid stock quantity"),

    // === 400 Bad Request (Business Rule) ===
    INSUFFICIENT_STOCK("PRODUCT-030", 400, "Insufficient stock"),
    CANNOT_DEDUCT_STOCK("PRODUCT-031", 400, "Cannot deduct stock"),
    CANNOT_CHANGE_STATUS("PRODUCT-032", 400, "Cannot change product status"),
    CANNOT_UPDATE_PRODUCT("PRODUCT-033", 400, "Cannot update product"),

    // === 409 Conflict ===
    PRODUCT_NAME_DUPLICATED("PRODUCT-020", 409, "Product name already exists"),

    // === 500 Internal Server Error ===
    PRODUCT_CREATION_FAILED("PRODUCT-050", 500, "Failed to create product");

    private final String code;
    private final int httpStatus;
    private final String message;

    ProductErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
