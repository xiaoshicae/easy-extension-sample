package io.github.xiaoshicae.extension.sample.ecommerce.business;

import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 生鲜电商业务
 * 特色: 冷链运费计算、库存实时检查、急速达通知、不支持无理由退货
 * 挂载了包邮和急速达能力
 */
@Business(code = FreshBusiness.CODE, priority = 50,
        abilities = {"ability.free-shipping::10", "ability.rapid-delivery::20"})
public class FreshBusiness implements Matcher<OrderMatchParam>, OrderValidateExtension, FreightCalcExtension,
        StockCheckExtension, AfterSalePolicyExtension {

    public static final String CODE = "biz.fresh";

    @Override
    public boolean match(OrderMatchParam param) {
        return "fresh".equals(param.getBizCode());
    }

    @Override
    public String validate(OrderContext ctx) {
        // 生鲜校验: 金额限制（生鲜单价不能过高）
        if (ctx.getOriginalAmount() == null || ctx.getOriginalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return "订单金额不能小于等于0";
        }
        // 生鲜商品不能超过50件（保鲜限制）
        int totalQty = ctx.getItems().stream().mapToInt(item -> item.quantity()).sum();
        if (totalQty > 50) {
            return "生鲜商品单次购买不能超过50件";
        }
        return null;
    }

    @Override
    public BigDecimal calcFreight(OrderContext ctx) {
        // 生鲜冷链: 基础运费 15 元 + 每增加 1 件商品 +2 元
        int totalQty = ctx.getItems().stream().mapToInt(item -> item.quantity()).sum();
        return new BigDecimal("15").add(new BigDecimal(totalQty * 2));
    }

    @Override
    public Map<String, String> checkStock(OrderContext ctx) {
        // 生鲜库存实时检查: 假设所有 SKU 都有库存
        Map<String, String> result = new HashMap<>();
        for (OrderContext.OrderItem item : ctx.getItems()) {
            if (item.quantity() > 20) {
                result.put(item.skuId(), "LOW_STOCK");
            } else {
                result.put(item.skuId(), "AVAILABLE");
            }
        }
        return result;
    }

    @Override
    public Duration getReturnWindow(OrderContext ctx) {
        // 生鲜商品不支持7天无理由退货（食品安全法规定）
        return Duration.ofHours(2); // 仅支持收货后2小时内因质量问题退货
    }
}
