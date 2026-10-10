package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.math.BigDecimal;
import java.util.List;

/**
 * 7. 支付方式扩展点
 * 根据订单金额推荐或限制支付方式
 */
@ExtensionPoint(scenarios = {"payment"})
public interface PaymentMethodExtension {

    /**
     * @param orderAmount 订单金额
     * @return 可用的支付方式列表 (alipay, wechat, bank_card, installment_3/6/12, cod)
     */
    List<String> getAvailablePaymentMethods(BigDecimal orderAmount);
}
