package io.github.xiaoshicae.extension.sample.ecommerce.business;

import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 标准零售业务
 * 最基础的电商流程，挂载了包邮和七天无理由能力
 */
@Business(code = RetailBusiness.CODE, priority = 100,
        abilities = {"ability.free-shipping::10", "ability.return-7d::20", "ability.vip-coupon::30"})
public class RetailBusiness implements Matcher<OrderMatchParam>, OrderValidateExtension, NotifyExtension {

    public static final String CODE = "biz.retail";

    @Override
    public boolean match(OrderMatchParam param) {
        return "retail".equals(param.getBizCode());
    }

    @Override
    public String validate(OrderContext ctx) {
        // 基础校验：订单金额必须大于0
        if (ctx.getOriginalAmount() == null || ctx.getOriginalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "订单金额不能小于等于0";
        }
        if (ctx.getItems() == null || ctx.getItems().isEmpty()) {
            return "订单商品不能为空";
        }
        return null; // 校验通过
    }

    @Override
    public List<String> getNotifyChannels(OrderContext ctx) {
        // 标准零售: 仅推送通知
        return List.of("PUSH");
    }
}
