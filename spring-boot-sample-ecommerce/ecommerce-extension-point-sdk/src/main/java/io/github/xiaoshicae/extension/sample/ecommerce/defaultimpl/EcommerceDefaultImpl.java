package io.github.xiaoshicae.extension.sample.ecommerce.defaultimpl;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPointDefaultImplementation;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电商下单流程默认实现
 * 实现了所有10个扩展点，作为系统兜底能力
 */
@ExtensionPointDefaultImplementation
public class EcommerceDefaultImpl implements
        OrderValidateExtension,
        StockCheckExtension,
        PromotionCalcExtension,
        FreightCalcExtension,
        TaxCalcExtension,
        RiskControlExtension,
        PaymentMethodExtension,
        InvoiceExtension,
        AfterSalePolicyExtension,
        NotifyExtension {

    // 1. 订单校验
    @Override
    public String validate(OrderContext ctx) {
        if (ctx.getOriginalAmount() != null && ctx.getOriginalAmount().compareTo(BigDecimal.ZERO) < 0) {
            return "订单金额不能为负";
        }
        return null;
    }

    // 2. 库存检查
    @Override
    public Map<String, String> checkStock(OrderContext ctx) {
        // 默认: 所有商品库存充足
        Map<String, String> result = new HashMap<>();
        for (OrderContext.OrderItem item : ctx.getItems()) {
            result.put(item.skuId(), "AVAILABLE");
        }
        return result;
    }

    // 3. 促销计算
    @Override
    public BigDecimal calcPromotion(OrderContext ctx) {
        // 默认: 无优惠
        return BigDecimal.ZERO;
    }

    // 4. 运费计算
    @Override
    public BigDecimal calcFreight(OrderContext ctx) {
        // 默认: 基础运费 8 元
        return new BigDecimal("8.00");
    }

    // 5. 税费计算
    @Override
    public BigDecimal calcTax(OrderContext ctx) {
        // 默认: 国内订单无额外税费
        return BigDecimal.ZERO;
    }

    // 6. 风控检查
    @Override
    public String checkRisk(OrderContext ctx) {
        // 默认: 全部通过
        return "PASS";
    }

    // 7. 支付方式
    @Override
    public List<String> getAvailablePaymentMethods(OrderContext ctx) {
        // 默认: 支付宝 + 微信
        return List.of("alipay", "wechat");
    }

    // 8. 发票处理
    @Override
    public String determineInvoiceType(OrderContext ctx) {
        // 默认: 不开票
        return "NONE";
    }

    // 9. 售后策略
    @Override
    public Duration getReturnWindow(OrderContext ctx) {
        // 默认: 不支持无理由退货
        return null;
    }

    // 10. 通知策略
    @Override
    public List<String> getNotifyChannels(OrderContext ctx) {
        // 默认: 仅APP推送
        return List.of("PUSH");
    }
}
