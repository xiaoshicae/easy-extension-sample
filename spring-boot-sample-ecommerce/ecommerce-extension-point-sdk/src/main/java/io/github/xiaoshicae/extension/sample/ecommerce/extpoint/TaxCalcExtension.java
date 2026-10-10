package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.math.BigDecimal;
import java.util.List;

/**
 * 5. 税费计算扩展点
 * 按金额和商品品类计算关税、消费税等
 * 本示例只有默认实现，演示"扩展点可以只保留兜底行为"
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface TaxCalcExtension {

    /**
     * @param orderAmount 订单金额
     * @param categories  商品品类
     * @return 税费金额
     */
    BigDecimal calcTax(BigDecimal orderAmount, List<String> categories);
}
