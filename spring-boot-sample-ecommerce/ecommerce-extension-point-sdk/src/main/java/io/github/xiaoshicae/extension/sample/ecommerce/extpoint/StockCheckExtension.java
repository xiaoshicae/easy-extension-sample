package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

import java.util.Map;

/**
 * 2. 库存检查扩展点
 * 检查各 SKU 的库存状态，返回每个 SKU 的库存状态
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface StockCheckExtension {

    /**
     * @param ctx 订单上下文
     * @return key=skuId, value=stockStatus (AVAILABLE, LOW_STOCK, OUT_OF_STOCK)
     */
    Map<String, String> checkStock(OrderContext ctx);
}
