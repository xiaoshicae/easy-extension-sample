package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

import java.math.BigDecimal;

/**
 * 4. 运费计算扩展点
 * 根据收货地址、商品类型（如冷链）、业务类型计算运费
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface FreightCalcExtension {

    /**
     * @param ctx 订单上下文
     * @return 运费金额
     */
    BigDecimal calcFreight(OrderContext ctx);
}
