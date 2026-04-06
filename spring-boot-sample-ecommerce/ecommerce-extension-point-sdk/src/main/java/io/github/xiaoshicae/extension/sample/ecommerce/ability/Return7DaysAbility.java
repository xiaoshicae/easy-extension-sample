package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.AfterSalePolicyExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.time.Duration;

import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 七天无理由退货能力
 * 为用户提供7天无理由退换货保障
 */
@Ability(code = Return7DaysAbility.CODE)
public class Return7DaysAbility implements Matcher<OrderMatchParam>, AfterSalePolicyExtension {

    public static final String CODE = "ability.return-7d";

    @Override
    public boolean match(OrderMatchParam param) {
        return param.getAbilityCodes() != null && param.getAbilityCodes().contains("return-7d");
    }

    @Override
    public Duration getReturnWindow(OrderContext ctx) {
        // 7天无理由退货
        return Duration.ofDays(7);
    }
}
