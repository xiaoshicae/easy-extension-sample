package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

import java.util.List;

/**
 * 7. 支付方式扩展点
 * 根据业务类型、用户偏好、订单金额等推荐或限制支付方式
 */
@ExtensionPoint(scenarios = {"payment"})
public interface PaymentMethodExtension {

    /**
     * @param ctx 订单上下文
     * @return 可用的支付方式列表 (alipay, wechat, bank_card, installment, cod)
     */
    List<String> getAvailablePaymentMethods(OrderContext ctx);
}
