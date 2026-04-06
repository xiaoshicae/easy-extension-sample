package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionInject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 电商下单流程控制器
 * 演示 10 个扩展点在真实下单流程中的调用方式
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @ExtensionInject
    private OrderValidateExtension orderValidateExtension;

    @ExtensionInject
    private StockCheckExtension stockCheckExtension;

    @ExtensionInject
    private PromotionCalcExtension promotionCalcExtension;

    @ExtensionInject
    private FreightCalcExtension freightCalcExtension;

    @ExtensionInject
    private TaxCalcExtension taxCalcExtension;

    @ExtensionInject
    private RiskControlExtension riskControlExtension;

    @ExtensionInject
    private PaymentMethodExtension paymentMethodExtension;

    @ExtensionInject
    private InvoiceExtension invoiceExtension;

    @ExtensionInject
    private AfterSalePolicyExtension afterSalePolicyExtension;

    @ExtensionInject
    private NotifyExtension notifyExtension;

    // 也演示 List 注入方式: 获取所有匹配的支付方式
    @ExtensionInject
    private List<PaymentMethodExtension> allPaymentMethodExtensions;

    /**
     * 完整下单流程
     * GET /api/order/checkout?bizCode=retail&abilityCodes=vip::free-shipping
     * GET /api/order/checkout?bizCode=fresh&abilityCodes=rapid::return-7d
     * GET /api/order/checkout?bizCode=digital&abilityCodes=installment::return-7d
     */
    @PostMapping("/checkout")
    public String checkout() {
        // 构造模拟订单数据
        List<OrderContext.OrderItem> items = List.of(
                new OrderContext.OrderItem("SKU-001", "商品A", 2, new BigDecimal("99.00"), "general"),
                new OrderContext.OrderItem("SKU-002", "商品B", 1, new BigDecimal("199.00"), "general")
        );
        BigDecimal originalAmount = new BigDecimal("397.00");

        OrderContext ctx = new OrderContext(
                "ORD-20260405-001", "USER-12345", "unknown",
                items, originalAmount,
                "广东省", "深圳市",
                false, null, "alipay"
        );

        // ================= 下单流程 10 步 =================

        // 1. 订单校验
        String validateResult = orderValidateExtension.validate(ctx);
        if (validateResult != null) {
            return "订单校验失败: " + validateResult;
        }

        // 2. 库存检查
        Map<String, String> stockResult = stockCheckExtension.checkStock(ctx);

        // 3. 促销计算
        BigDecimal promotion = promotionCalcExtension.calcPromotion(ctx);

        // 4. 运费计算
        BigDecimal freight = freightCalcExtension.calcFreight(ctx);

        // 5. 税费计算
        BigDecimal tax = taxCalcExtension.calcTax(ctx);

        // 6. 风控检查
        String riskResult = riskControlExtension.checkRisk(ctx);

        // 7. 支付方式 (单个最优匹配)
        List<String> paymentMethods = paymentMethodExtension.getAvailablePaymentMethods(ctx);

        // 8. 发票类型
        String invoiceType = invoiceExtension.determineInvoiceType(ctx);

        // 9. 售后策略
        var returnWindow = afterSalePolicyExtension.getReturnWindow(ctx);

        // 10. 通知渠道
        List<String> notifyChannels = notifyExtension.getNotifyChannels(ctx);

        // ================= 计算最终价格 =================
        BigDecimal finalAmount = originalAmount.subtract(promotion).add(freight).add(tax);

        // ================= 组装返回 =================
        StringBuilder sb = new StringBuilder();
        sb.append("=== 电商下单流程结果 ===\n");
        sb.append("订单号: ").append(ctx.getOrderId()).append("\n");
        sb.append("原始金额: ¥").append(originalAmount).append("\n");
        sb.append("促销优惠: -¥").append(promotion).append("\n");
        sb.append("运费: +¥").append(freight).append("\n");
        sb.append("税费: +¥").append(tax).append("\n");
        sb.append("最终金额: ¥").append(finalAmount).append("\n");
        sb.append("风控结果: ").append(riskResult).append("\n");
        sb.append("支付方式: ").append(paymentMethods).append("\n");
        sb.append("发票类型: ").append(invoiceType).append("\n");
        sb.append("退货窗口: ").append(returnWindow != null ? returnWindow.toDays() + "天" : "不支持").append("\n");
        sb.append("通知渠道: ").append(notifyChannels).append("\n");
        sb.append("库存状态: ").append(stockResult).append("\n");
        sb.append("支付方式数(List注入): ").append(allPaymentMethodExtensions.size()).append(" 个实现");

        return sb.toString();
    }
}
