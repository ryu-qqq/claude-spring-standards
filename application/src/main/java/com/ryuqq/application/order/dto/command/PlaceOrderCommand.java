package com.ryuqq.application.order.dto.command;

import java.math.BigDecimal;
import java.util.List;

/**
 * Place Order Command DTO
 *
 * 주문 생성 명령 데이터
 *
 * @author sangwon-ryu
 * @since 1.0
 */
public class PlaceOrderCommand {

    private final Long customerId;
    private final String customerName;
    private final String customerEmail;
    private final String shippingAddress;
    private final String shippingZipCode;
    private final String shippingCity;
    private final List<OrderLineItemCommand> lineItems;
    private final BigDecimal totalAmount;
    private final String paymentMethod;

    public PlaceOrderCommand(
        Long customerId,
        String customerName,
        String customerEmail,
        String shippingAddress,
        String shippingZipCode,
        String shippingCity,
        List<OrderLineItemCommand> lineItems,
        BigDecimal totalAmount,
        String paymentMethod
    ) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.shippingZipCode = shippingZipCode;
        this.shippingCity = shippingCity;
        this.lineItems = lineItems;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

    // Getters (Lombok 사용 금지)

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getShippingZipCode() {
        return shippingZipCode;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public List<OrderLineItemCommand> getLineItems() {
        return lineItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Order Line Item Command (Nested DTO)
     */
    public static class OrderLineItemCommand {
        private final Long productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal unitPrice;

        public OrderLineItemCommand(
            Long productId,
            String productName,
            int quantity,
            BigDecimal unitPrice
        ) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
    }
}
