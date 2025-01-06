package io.github.xiaoshicae.extension.sample.complex.dto;

import java.math.BigDecimal;

public class OrderDTO {
    private final String orderId;
    private final BigDecimal originalPrice;
    private final BigDecimal discount;

    public OrderDTO(String orderId, BigDecimal originalPrice, BigDecimal discount) {
        this.orderId = orderId;
        this.originalPrice = originalPrice;
        this.discount = discount;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }
}
