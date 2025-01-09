package io.github.xiaoshicae.extension.sample.complex.extpoint;


import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

/**
 * 跳过0元校验扩展点
 * 支付过程需要校验订单是否为0元
 */
@ExtensionPoint
public interface SkipCheckZeroPriceExtension {

    /**
     * 是否跳过0元校验
     *
     * @return 是否跳过参数校验
     */
    Boolean skipCheckZeroPrice();
}
