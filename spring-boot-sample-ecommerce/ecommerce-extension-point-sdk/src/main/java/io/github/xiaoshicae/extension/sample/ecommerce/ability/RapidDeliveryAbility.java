package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.FreightCalcExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.NotifyExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 急速达能力
 * 命中条件: 用户勾选了加急、收货省份已开通急速达、且件数不超过 10 件
 * 生效后收取加急配送费，并增加短信和微信通知
 */
@Ability(code = RapidDeliveryAbility.CODE)
public class RapidDeliveryAbility implements Matcher<OrderMatchParam>, NotifyExtension, FreightCalcExtension {

    public static final String CODE = "ability.rapid-delivery";

    private static final BigDecimal EXPRESS_FEE = new BigDecimal("12.00");
    private static final Set<String> COVERED_PROVINCES = Set.of("广东省", "上海市", "北京市", "浙江省", "江苏省");
    private static final int MAX_ITEM_COUNT = 10;

    @Override
    public boolean match(OrderMatchParam param) {
        return param.isUrgentDelivery()
                && COVERED_PROVINCES.contains(param.getShippingProvince())
                && param.getItemCount() <= MAX_ITEM_COUNT;
    }

    @Override
    public List<String> getNotifyChannels(String notifyEvent) {
        // 急速达: 短信 + 推送 + 微信消息，确保用户及时收到配送通知
        return List.of("SMS", "PUSH", "WECHAT_MSG");
    }

    @Override
    public BigDecimal calcFreight(String shippingProvince, int itemCount) {
        // 急速达: 加急配送费 12 元
        return EXPRESS_FEE;
    }
}
