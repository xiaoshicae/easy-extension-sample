package io.github.xiaoshicae.extension.sample.complex.defaultimpl;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPointDefaultImplementation;
import io.github.xiaoshicae.extension.sample.complex.extpoint.CalculatePriceExtension;
import io.github.xiaoshicae.extension.sample.complex.extpoint.DelayCloseOrderExtension;
import io.github.xiaoshicae.extension.sample.complex.dto.OrderDTO;
import io.github.xiaoshicae.extension.sample.complex.extpoint.SkipCheckZeroPriceExtension;

import java.math.BigDecimal;
import java.time.Duration;


/**
 * 扩展点的默认实现，实现了系统所有扩展点
 * 当没有匹配到任何扩展点实现时，作为系统能力的兜底实现
 */
@ExtensionPointDefaultImplementation
public class ExtDefaultImpl implements CalculatePriceExtension, SkipCheckZeroPriceExtension, DelayCloseOrderExtension {

    /**
     * 算价扩展点默认实现
     * 默认: 原始价格 x 折扣
     *
     * @param order 订单
     * @return 价格
     */
    @Override
    public BigDecimal calculatePrice(OrderDTO order) {
        return order.getOriginalPrice().multiply(order.getDiscount());
    }

    /**
     * 延迟关单扩展点默认实现
     * 默认: 10min
     *
     * @param orderType 订单类型
     * @return 延迟关单时间
     */
    @Override
    public Duration delayCloseOrderDuration(String orderType) {
        return Duration.ofMinutes(10);
    }

    /**
     * 跳过0元校验扩展点默认实现
     * 默认: false
     *
     * @return 是否跳过
     */
    @Override
    public Boolean skipCheckZeroPrice() {
        return false;
    }
}
