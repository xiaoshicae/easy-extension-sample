package io.github.xiaoshicae.extension.sample.ecommerce.business;

import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 数码3C业务
 * 特色: 分期支付、延保售后、专票支持、严格风控
 * 挂载了七天无理由和分期免息能力
 */
@Business(code = DigitalBusiness.CODE, priority = 75,
        abilities = {"ability.return-7d::10", "ability.installment::20", "ability.vip-coupon::30", "ability.free-shipping::40"})
public class DigitalBusiness implements Matcher<OrderMatchParam>, OrderValidateExtension, AfterSalePolicyExtension,
        RiskControlExtension, InvoiceExtension {

    public static final String CODE = "biz.digital";

    @Override
    public boolean match(OrderMatchParam param) {
        return "digital".equals(param.getBizCode());
    }

    @Override
    public String validate(OrderContext ctx) {
        if (ctx.getOriginalAmount() == null || ctx.getOriginalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "订单金额不能小于等于0";
        }
        // 数码商品单价超过5000需要额外风控验证
        if (ctx.getOriginalAmount().compareTo(new BigDecimal("5000")) > 0) {
            // 这里仅做提示，实际由风控扩展点处理
        }
        return null;
    }

    @Override
    public Duration getReturnWindow(OrderContext ctx) {
        // 数码3C: 15天无理由退货（高于标准7天）
        return Duration.ofDays(15);
    }

    @Override
    public String checkRisk(OrderContext ctx) {
        // 数码商品风控: 超过 5000 元的订单需要人工审核
        if (ctx.getOriginalAmount() != null && ctx.getOriginalAmount().compareTo(new BigDecimal("5000")) > 0) {
            return "REVIEW";
        }
        return "PASS";
    }

    @Override
    public String determineInvoiceType(OrderContext ctx) {
        // 数码业务默认开具电子发票
        if (ctx.getNeedInvoice() != null && ctx.getNeedInvoice()) {
            return "ELECTRONIC";
        }
        return "NONE";
    }
}
