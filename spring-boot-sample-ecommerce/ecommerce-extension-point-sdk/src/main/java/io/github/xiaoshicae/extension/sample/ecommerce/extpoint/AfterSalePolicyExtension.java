package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

import java.time.Duration;

/**
 * 9. 售后策略扩展点
 * 根据业务类型决定退换货规则（时效、运费承担方等）
 */
@ExtensionPoint(scenarios = {"after_sale"})
public interface AfterSalePolicyExtension {

    /**
     * @param ctx 订单上下文
     * @return 无理由退货时长，null 表示不支持
     */
    Duration getReturnWindow(OrderContext ctx);
}
