package io.github.xiaoshicae.extension.sample.ecommerce.business;

import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.annotation.Self;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.FreeShippingAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.RapidDeliveryAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 生鲜电商业务
 * 特色: 冷链运费计算、库存实时检查、急速达通知、不支持无理由退货
 * 挂载了包邮和急速达能力
 * abilities 的数组顺序即优先级(靠前者优先)，Self.class 表示业务自身的位置
 */
@Business(code = FreshBusiness.CODE,
        abilities = {FreeShippingAbility.class, RapidDeliveryAbility.class, Self.class})
public class FreshBusiness implements Matcher<OrderMatchParam>, OrderValidateExtension, FreightCalcExtension,
        StockCheckExtension, AfterSalePolicyExtension {

    public static final String CODE = "biz.fresh";

    private static final int MAX_ITEM_COUNT = 50;
    private static final int LOW_STOCK_QUANTITY = 20;
    private static final String LOCAL_PROVINCE = "广东省";

    @Override
    public boolean match(OrderMatchParam param) {
        return "fresh".equals(param.getBizCode());
    }

    @Override
    public String validate(BigDecimal orderAmount, int itemCount) {
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return "订单金额不能小于等于0";
        }
        // 生鲜商品不能超过50件（保鲜限制）
        if (itemCount > MAX_ITEM_COUNT) {
            return "生鲜商品单次购买不能超过" + MAX_ITEM_COUNT + "件";
        }
        return null;
    }

    @Override
    public BigDecimal calcFreight(String shippingProvince, int itemCount) {
        // 生鲜冷链: 基础运费 15 元 + 每件商品 2 元，仓库省外再加 10 元跨省冷链费
        BigDecimal coldChain = new BigDecimal("15").add(BigDecimal.valueOf(itemCount * 2L));
        return LOCAL_PROVINCE.equals(shippingProvince) ? coldChain : coldChain.add(new BigDecimal("10"));
    }

    @Override
    public Map<String, String> checkStock(Map<String, Integer> skuQuantities) {
        // 生鲜库存实时检查: 单 SKU 买得多时库存吃紧
        Map<String, String> result = new LinkedHashMap<>();
        skuQuantities.forEach((skuId, quantity) ->
                result.put(skuId, quantity > LOW_STOCK_QUANTITY ? "LOW_STOCK" : "AVAILABLE"));
        return result;
    }

    @Override
    public Duration getReturnWindow(List<String> categories) {
        // 生鲜商品不支持7天无理由退货（食品安全法规定），仅支持收货后2小时内因质量问题退货
        return categories.contains("fresh") ? Duration.ofHours(2) : Duration.ofDays(7);
    }
}
