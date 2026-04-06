package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

import java.math.BigDecimal;

/**
 * 5. 税费计算扩展点
 * 计算跨境商品的关税、增值税等
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface TaxCalcExtension {

    /**
     * @param ctx 订单上下文
     * @return 税费金额
     */
    BigDecimal calcTax(OrderContext ctx);
}
