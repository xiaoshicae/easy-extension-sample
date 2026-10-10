package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.FreightCalcExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.util.Set;

/**
 * 包邮能力
 * 命中条件: SVIP 会员无门槛包邮，或订单满 ¥500 且收货地不在偏远地区
 */
@Ability(code = FreeShippingAbility.CODE)
public class FreeShippingAbility implements Matcher<OrderMatchParam>, FreightCalcExtension {

    public static final String CODE = "ability.free-shipping";

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("500");
    private static final Set<String> REMOTE_PROVINCES = Set.of("新疆", "西藏", "内蒙古", "青海", "宁夏");

    @Override
    public boolean match(OrderMatchParam param) {
        if ("SVIP".equals(param.getMemberLevel())) {
            return true;
        }
        return param.getOrderAmount().compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                && !REMOTE_PROVINCES.contains(param.getShippingProvince());
    }

    @Override
    public BigDecimal calcFreight(String shippingProvince, int itemCount) {
        // 包邮: 运费为 0
        return BigDecimal.ZERO;
    }
}
