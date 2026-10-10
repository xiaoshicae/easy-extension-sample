package io.github.xiaoshicae.extension.sample.ecommerce.ability;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.AfterSalePolicyExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * 七天无理由退货能力
 * 命中条件: 不含生鲜/定制商品，且金额低于 ¥3000（大额订单由业务自己定更长的保障期）
 */
@Ability(code = Return7DaysAbility.CODE)
public class Return7DaysAbility implements Matcher<OrderMatchParam>, AfterSalePolicyExtension {

    public static final String CODE = "ability.return-7d";

    private static final Set<String> NO_RETURN_CATEGORIES = Set.of("fresh", "custom");
    private static final BigDecimal BIG_ORDER_THRESHOLD = new BigDecimal("3000");

    @Override
    public boolean match(OrderMatchParam param) {
        if (param.getOrderAmount().compareTo(BIG_ORDER_THRESHOLD) >= 0) {
            return false;
        }
        return param.getCategories().stream().noneMatch(NO_RETURN_CATEGORIES::contains);
    }

    @Override
    public Duration getReturnWindow(List<String> categories) {
        // 7天无理由退货
        return Duration.ofDays(7);
    }
}
