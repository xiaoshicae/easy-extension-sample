package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.PromotionCalcExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.util.Set;

/**
 * VIP优惠券能力
 * 命中条件: 会员等级为 VIP 或 SVIP
 */
@Ability(code = VipCouponAbility.CODE)
public class VipCouponAbility implements Matcher<OrderMatchParam>, PromotionCalcExtension {

    public static final String CODE = "ability.vip-coupon";

    private static final Set<String> VIP_LEVELS = Set.of("VIP", "SVIP");
    private static final BigDecimal BIG_ORDER_THRESHOLD = new BigDecimal("500");

    @Override
    public boolean match(OrderMatchParam param) {
        return VIP_LEVELS.contains(param.getMemberLevel());
    }

    @Override
    public BigDecimal calcPromotion(BigDecimal orderAmount) {
        // VIP 券: 满 500 减 50，否则减 20
        return orderAmount.compareTo(BIG_ORDER_THRESHOLD) >= 0 ? new BigDecimal("50.00") : new BigDecimal("20.00");
    }
}
