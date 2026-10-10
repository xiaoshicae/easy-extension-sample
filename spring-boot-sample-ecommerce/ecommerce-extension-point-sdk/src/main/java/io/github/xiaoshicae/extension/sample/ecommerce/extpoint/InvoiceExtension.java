package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

/**
 * 8. 发票处理扩展点
 * 根据用户的开票诉求决定实际开具的发票类型
 */
@ExtensionPoint(scenarios = {"create_order", "fulfillment"})
public interface InvoiceExtension {

    /**
     * @param needInvoice   用户是否需要发票
     * @param invoiceTarget 抬头类型: personal(个人), company(企业)
     * @return 发票类型: NONE(不开票), ELECTRONIC(电子发票), PAPER(纸质发票), SPECIAL(专票)
     */
    String determineInvoiceType(boolean needInvoice, String invoiceTarget);
}
