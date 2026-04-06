package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.FreightCalcExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;

import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 包邮能力
 * 满足条件时免除运费
 */
@Ability(code = FreeShippingAbility.CODE)
public class FreeShippingAbility implements Matcher<OrderMatchParam>, FreightCalcExtension {

    public static final String CODE = "ability.free-shipping";

    @Override
    public boolean match(OrderMatchParam param) {
        return param.getAbilityCodes() != null && param.getAbilityCodes().contains("free-shipping");
    }

    @Override
    public BigDecimal calcFreight(OrderContext ctx) {
        // 包邮: 运费为 0
        return BigDecimal.ZERO;
    }
}
