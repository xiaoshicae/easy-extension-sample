package io.github.xiaoshicae.extension.sample.complex.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.complex.dto.OrderDTO;

import java.math.BigDecimal;

/**
 * 算价扩展点
 * 下单过程中会调用该扩展点进行算价
 */
@ExtensionPoint
public interface CalculatePriceExtension {

    /**
     * 价格计算
     *
     * @param order 订单
     * @return 该订单的价格
     */
    BigDecimal calculatePrice(OrderDTO order);
}
