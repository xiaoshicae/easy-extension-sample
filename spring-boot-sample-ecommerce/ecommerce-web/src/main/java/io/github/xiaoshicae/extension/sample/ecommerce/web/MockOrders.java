package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

import java.math.BigDecimal;
import java.util.List;

/**
 * 演示用的固定订单
 * 2 件商品A(¥99) + 1 件商品B(¥199)，原价 ¥397，共 3 件商品
 */
final class MockOrders {

    private MockOrders() {
    }

    static OrderContext sample() {
        List<OrderContext.OrderItem> items = List.of(
                new OrderContext.OrderItem("SKU-001", "商品A", 2, new BigDecimal("99.00"), "general"),
                new OrderContext.OrderItem("SKU-002", "商品B", 1, new BigDecimal("199.00"), "general")
        );
        return new OrderContext(
                "ORD-20260405-001", "USER-12345", "unknown",
                items, new BigDecimal("397.00"),
                "广东省", "深圳市",
                false, null, "alipay"
        );
    }
}
