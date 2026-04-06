package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.PaymentMethodExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.RiskControlExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.util.List;

import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 分期免息能力
 * 为大额数码订单提供 3/6/12 期免息分期，并执行信用风控评估
 */
@Ability(code = InstallmentAbility.CODE)
public class InstallmentAbility implements Matcher<OrderMatchParam>, PaymentMethodExtension, RiskControlExtension {

    public static final String CODE = "ability.installment";
    private static final BigDecimal REVIEW_THRESHOLD = new BigDecimal("10000");

    @Override
    public boolean match(OrderMatchParam param) {
        return param.getAbilityCodes() != null && param.getAbilityCodes().contains("installment");
    }

    @Override
    public List<String> getAvailablePaymentMethods(OrderContext ctx) {
        // 支持支付宝、微信、银行卡 + 3/6/12期分期免息
        return List.of("alipay", "wechat", "bank_card", "installment_3", "installment_6", "installment_12");
    }

    @Override
    public String checkRisk(OrderContext ctx) {
        // 分期风控: 大额订单需人工审核，其余通过
        if (ctx.getOriginalAmount() != null && ctx.getOriginalAmount().compareTo(REVIEW_THRESHOLD) > 0) {
            return "REVIEW";
        }
        return "PASS";
    }
}
