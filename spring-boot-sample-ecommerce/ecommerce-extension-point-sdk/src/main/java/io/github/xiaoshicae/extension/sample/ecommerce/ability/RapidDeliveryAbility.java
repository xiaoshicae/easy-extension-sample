package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.FreightCalcExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.NotifyExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.util.List;

import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 急速达能力
 * 生鲜业务专属，下单后立即通知配送，增加短信和推送通知，并收取加急配送费
 */
@Ability(code = RapidDeliveryAbility.CODE)
public class RapidDeliveryAbility implements Matcher<OrderMatchParam>, NotifyExtension, FreightCalcExtension {

    public static final String CODE = "ability.rapid-delivery";

    private static final BigDecimal EXPRESS_FEE = new BigDecimal("8.00");

    @Override
    public boolean match(OrderMatchParam param) {
        return param.getAbilityCodes() != null && param.getAbilityCodes().contains("rapid");
    }

    @Override
    public List<String> getNotifyChannels(OrderContext ctx) {
        // 急速达: 短信 + 推送 + 微信消息，确保用户及时收到配送通知
        return List.of("SMS", "PUSH", "WECHAT_MSG");
    }

    @Override
    public BigDecimal calcFreight(OrderContext ctx) {
        // 急速达: 加急配送费 8 元
        return EXPRESS_FEE;
    }
}
