package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionInject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 电商下单流程控制器
 * 演示 10 个扩展点在真实下单流程中的调用方式：每个扩展点只接收自己需要的参数，
 * 而不是把整个订单对象传进去
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final String ORDER_ID = "ORD-20260405-001";
    private static final String MAIN_SKU = "SKU-001";
    private static final String SECOND_SKU = "SKU-002";

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

    @Autowired
    private OrderNotifyService orderNotifyService;

    /**
     * 异步通知: 通知渠道在 @Async 线程池里计算
     * 依赖 easy-extension.async-propagation=true 把请求的业务绑定带到异步线程
     * POST /api/order/notify-async?bizCode=fresh&categories=fresh&urgent=true
     */
    @PostMapping("/notify-async")
    public String notifyAsync(
            @RequestParam(name = "notifyEvent", defaultValue = OrderParams.DEFAULT_NOTIFY_EVENT) String notifyEvent)
            throws Exception {
        List<String> channels = orderNotifyService.notifyChannelsAsync(notifyEvent).get(5, TimeUnit.SECONDS);
        return "异步通知渠道: " + channels;
    }

    /**
     * 完整下单流程
     * 请求参数既用于构造匹配参数(决定命中哪个业务、哪些能力)，也作为扩展点的入参
     * GET /api/order/checkout?bizCode=retail&memberLevel=VIP
     * GET /api/order/checkout?bizCode=fresh&categories=fresh&urgent=true
     * GET /api/order/checkout?bizCode=digital&categories=digital&amount=6999&needInvoice=true&invoiceTarget=company
     */
    @PostMapping("/checkout")
    public String checkout(
            @RequestParam(name = "amount", defaultValue = OrderParams.DEFAULT_AMOUNT) BigDecimal amount,
            @RequestParam(name = "itemCount", defaultValue = OrderParams.DEFAULT_ITEM_COUNT) int itemCount,
            @RequestParam(name = "province", defaultValue = OrderParams.DEFAULT_PROVINCE) String province,
            @RequestParam(name = "categories", defaultValue = OrderParams.DEFAULT_CATEGORIES) String categories,
            @RequestParam(name = "userId", defaultValue = OrderParams.DEFAULT_USER_ID) String userId,
            @RequestParam(name = "needInvoice", defaultValue = OrderParams.DEFAULT_NEED_INVOICE) boolean needInvoice,
            @RequestParam(name = "invoiceTarget", defaultValue = OrderParams.DEFAULT_INVOICE_TARGET) String invoiceTarget,
            @RequestParam(name = "notifyEvent", defaultValue = OrderParams.DEFAULT_NOTIFY_EVENT) String notifyEvent) {

        List<String> categoryList = OrderParams.categories(categories);
        Map<String, Integer> skuQuantities = demoSkuQuantities(itemCount);

        // ================= 下单流程 10 步 =================

        // 1. 订单校验
        String validateResult = orderValidateExtension.validate(amount, itemCount);
        if (validateResult != null) {
            return "订单校验失败: " + validateResult;
        }

        // 2. 库存检查
        Map<String, String> stockResult = stockCheckExtension.checkStock(skuQuantities);

        // 3. 促销计算
        BigDecimal promotion = promotionCalcExtension.calcPromotion(amount);

        // 4. 运费计算
        BigDecimal freight = freightCalcExtension.calcFreight(province, itemCount);

        // 5. 税费计算
        BigDecimal tax = taxCalcExtension.calcTax(amount, categoryList);

        // 6. 风控检查
        String riskResult = riskControlExtension.checkRisk(userId, amount);

        // 7. 支付方式 (单个最优匹配)
        List<String> paymentMethods = paymentMethodExtension.getAvailablePaymentMethods(amount);

        // 8. 发票类型
        String invoiceType = invoiceExtension.determineInvoiceType(needInvoice, invoiceTarget);

        // 9. 售后策略
        Duration returnWindow = afterSalePolicyExtension.getReturnWindow(categoryList);

        // 10. 通知渠道
        List<String> notifyChannels = notifyExtension.getNotifyChannels(notifyEvent);

        // ================= 计算最终价格 =================
        BigDecimal finalAmount = amount.subtract(promotion).add(freight).add(tax);

        // ================= 组装返回 =================
        StringBuilder sb = new StringBuilder();
        sb.append("=== 电商下单流程结果 ===\n");
        sb.append("订单号: ").append(ORDER_ID).append("\n");
        sb.append("原始金额: ¥").append(amount).append("\n");
        sb.append("促销优惠: -¥").append(promotion).append("\n");
        sb.append("运费: +¥").append(freight).append("\n");
        sb.append("税费: +¥").append(tax).append("\n");
        sb.append("最终金额: ¥").append(finalAmount).append("\n");
        sb.append("风控结果: ").append(riskResult).append("\n");
        sb.append("支付方式: ").append(paymentMethods).append("\n");
        sb.append("发票类型: ").append(invoiceType).append("\n");
        sb.append("退货窗口: ").append(formatReturnWindow(returnWindow)).append("\n");
        sb.append("通知渠道: ").append(notifyChannels).append("\n");
        sb.append("库存状态: ").append(stockResult).append("\n");
        sb.append("支付方式数(List注入): ").append(allPaymentMethodExtensions.size()).append(" 个实现");

        return sb.toString();
    }

    /**
     * 演示订单: 把件数拆到两个 SKU 上，SKU-001 占大头(默认 3 件 = 2 + 1)
     */
    private static Map<String, Integer> demoSkuQuantities(int itemCount) {
        Map<String, Integer> skuQuantities = new LinkedHashMap<>();
        skuQuantities.put(MAIN_SKU, itemCount - itemCount / 2);
        if (itemCount / 2 > 0) {
            skuQuantities.put(SECOND_SKU, itemCount / 2);
        }
        return skuQuantities;
    }

    private static String formatReturnWindow(Duration returnWindow) {
        if (returnWindow == null) {
            return "不支持";
        }
        return returnWindow.toDays() > 0 ? returnWindow.toDays() + "天" : returnWindow.toHours() + "小时";
    }
}
