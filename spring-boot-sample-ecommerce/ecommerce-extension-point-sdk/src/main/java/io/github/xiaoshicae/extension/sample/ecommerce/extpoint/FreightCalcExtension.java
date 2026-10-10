package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.math.BigDecimal;

/**
 * 4. 运费计算扩展点
 * 根据收货省份和商品件数计算运费
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface FreightCalcExtension {

    /**
     * @param shippingProvince 收货省份
     * @param itemCount        商品件数
     * @return 运费金额
     */
    BigDecimal calcFreight(String shippingProvince, int itemCount);
}
