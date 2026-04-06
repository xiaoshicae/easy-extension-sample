package io.github.xiaoshicae.extension.sample.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单上下文，贯穿整个下单流程
 */
public class OrderContext {
    private final String orderId;
    private final String userId;
    private final String orderType;       // retail, fresh, digital
    private final List<OrderItem> items;
    private final BigDecimal originalAmount;
    private final String shippingProvince;
    private final String shippingCity;
    private final Boolean needInvoice;
    private final String invoiceType;      // personal, company
    private final String paymentMethod;    // alipay, wechat, bank_card, installment

    public OrderContext(String orderId, String userId, String orderType,
                        List<OrderItem> items, BigDecimal originalAmount,
                        String shippingProvince, String shippingCity,
                        Boolean needInvoice, String invoiceType, String paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderType = orderType;
        this.items = items;
        this.originalAmount = originalAmount;
        this.shippingProvince = shippingProvince;
        this.shippingCity = shippingCity;
        this.needInvoice = needInvoice;
        this.invoiceType = invoiceType;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getOrderType() { return orderType; }
    public List<OrderItem> getItems() { return items; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public String getShippingProvince() { return shippingProvince; }
    public String getShippingCity() { return shippingCity; }
    public Boolean getNeedInvoice() { return needInvoice; }
    public String getInvoiceType() { return invoiceType; }
    public String getPaymentMethod() { return paymentMethod; }

    /**
     * 订单项
     */
    public record OrderItem(String skuId, String name, int quantity, BigDecimal unitPrice, String category) {
    }
}
