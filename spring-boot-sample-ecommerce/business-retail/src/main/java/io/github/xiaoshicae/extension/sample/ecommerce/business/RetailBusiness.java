package io.github.xiaoshicae.extension.sample.ecommerce.business;

import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.annotation.Self;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.FreeShippingAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.Return7DaysAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.VipCouponAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 标准零售业务
 * 最基础的电商流程，挂载了包邮、七天无理由和VIP优惠能力
 * 能力是否生效由能力自己的 match() 按订单属性判断，这里只决定挂载哪些能力以及它们的优先级
 * abilities 的数组顺序即优先级(靠前者优先)，Self.class 表示业务自身的位置
 */
@Business(code = RetailBusiness.CODE,
        abilities = {FreeShippingAbility.class, Return7DaysAbility.class, VipCouponAbility.class, Self.class})
public class RetailBusiness implements Matcher<OrderMatchParam>, OrderValidateExtension, NotifyExtension {

    public static final String CODE = "biz.retail";

    @Override
    public boolean match(OrderMatchParam param) {
        return "retail".equals(param.getBizCode());
    }

    @Override
    public String validate(BigDecimal orderAmount, int itemCount) {
        // 基础校验：订单金额必须大于0，且至少有一件商品
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return "订单金额不能小于等于0";
        }
        if (itemCount <= 0) {
            return "订单商品不能为空";
        }
        return null; // 校验通过
    }

    @Override
    public List<String> getNotifyChannels(String notifyEvent) {
        // 标准零售: 下单和发货只推送，售后额外发短信
        return "AFTER_SALE".equals(notifyEvent) ? List.of("PUSH", "SMS") : List.of("PUSH");
    }
}
