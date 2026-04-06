package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

/**
 * 1. 订单校验扩展点
 * 下单前验证订单数据完整性、商品可用性、用户状态等
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface OrderValidateExtension {

    /**
     * @param ctx 订单上下文
     * @return 校验结果，null 表示通过，非 null 表示错误信息
     */
    String validate(OrderContext ctx);
}
