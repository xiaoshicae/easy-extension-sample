package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.util.Map;

/**
 * 2. 库存检查扩展点
 * 检查各 SKU 的库存状态
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface StockCheckExtension {

    /**
     * @param skuQuantities 本次购买的 SKU 及件数: key=skuId, value=件数
     * @return key=skuId, value=stockStatus (AVAILABLE, LOW_STOCK, OUT_OF_STOCK)
     */
    Map<String, String> checkStock(Map<String, Integer> skuQuantities);
}
