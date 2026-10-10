package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.PaymentMethodExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.RiskControlExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分期免息能力
 * 命中条件: 订单金额达到 ¥3000 的大额订单
 * 生效后提供 3/6/12 期免息分期，并按分期的口径做信用风控
 */
@Ability(code = InstallmentAbility.CODE)
public class InstallmentAbility implements Matcher<OrderMatchParam>, PaymentMethodExtension, RiskControlExtension {

    public static final String CODE = "ability.installment";

    private static final BigDecimal MIN_INSTALLMENT_AMOUNT = new BigDecimal("3000");
    private static final BigDecimal REVIEW_THRESHOLD = new BigDecimal("10000");

    @Override
    public boolean match(OrderMatchParam param) {
        return param.getOrderAmount().compareTo(MIN_INSTALLMENT_AMOUNT) >= 0;
    }

    @Override
    public List<String> getAvailablePaymentMethods(BigDecimal orderAmount) {
        // 支持支付宝、微信、银行卡 + 3/6/12期分期免息
        return List.of("alipay", "wechat", "bank_card", "installment_3", "installment_6", "installment_12");
    }

    @Override
    public String checkRisk(String userId, BigDecimal orderAmount) {
        // 分期风控: 超过 1 万的分期订单需人工审核，其余通过
        return orderAmount.compareTo(REVIEW_THRESHOLD) > 0 ? "REVIEW" : "PASS";
    }
}
