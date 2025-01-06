package io.github.xiaoshicae.extension.sample.complex.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.time.Duration;

/**
 * 延迟关单扩展点
 * 订单未支付时，需要在xxx分钟进行自动关单
 */
@ExtensionPoint
public interface DelayCloseOrderExtension {

    /**
     * 延迟关单
     *
     * @param orderType 订单类型
     * @return 自动关单时间(0 表示不关单)
     */
    Duration delayCloseOrderDuration(String orderType);
}
