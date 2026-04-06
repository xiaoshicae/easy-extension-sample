package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

/**
 * 8. 发票处理扩展点
 * 根据业务类型、用户选择决定发票类型和开票信息
 */
@ExtensionPoint(scenarios = {"create_order", "fulfillment"})
public interface InvoiceExtension {

    /**
     * @param ctx 订单上下文
     * @return 发票类型: NONE(不开票), ELECTRONIC(电子发票), PAPER(纸质发票), SPECIAL(专票)
     */
    String determineInvoiceType(OrderContext ctx);
}
