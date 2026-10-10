package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.math.BigDecimal;

/**
 * 1. 订单校验扩展点
 * 下单前验证订单金额、件数等是否满足业务规则
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface OrderValidateExtension {

    /**
     * @param orderAmount 订单金额
     * @param itemCount   商品件数
     * @return 校验结果，null 表示通过，非 null 表示错误信息
     */
    String validate(BigDecimal orderAmount, int itemCount);
}
