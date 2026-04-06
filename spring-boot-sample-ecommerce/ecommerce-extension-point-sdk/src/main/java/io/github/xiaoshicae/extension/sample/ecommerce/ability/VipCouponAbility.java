package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.PromotionCalcExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;

import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * VIP优惠券能力
 * 为VIP用户提供额外优惠折扣
 */
@Ability(code = VipCouponAbility.CODE)
public class VipCouponAbility implements Matcher<OrderMatchParam>, PromotionCalcExtension {

    public static final String CODE = "ability.vip-coupon";

    @Override
    public boolean match(OrderMatchParam param) {
        return param.getAbilityCodes() != null && param.getAbilityCodes().contains("vip");
    }

    @Override
    public BigDecimal calcPromotion(OrderContext ctx) {
        // VIP 用户额外减免 20 元
        return new BigDecimal("20.00");
    }
}
