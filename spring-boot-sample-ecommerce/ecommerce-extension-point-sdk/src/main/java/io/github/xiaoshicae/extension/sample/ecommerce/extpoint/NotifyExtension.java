package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;
import io.github.xiaoshicae.extension.sample.ecommerce.dto.OrderContext;

import java.util.List;

/**
 * 10. 通知策略扩展点
 * 根据业务类型决定下单成功后的通知方式（短信、邮件、推送等）
 */
@ExtensionPoint(scenarios = {"create_order", "fulfillment", "after_sale"})
public interface NotifyExtension {

    /**
     * @param ctx 订单上下文
     * @return 通知渠道列表: SMS, EMAIL, PUSH, WECHAT_MSG
     */
    List<String> getNotifyChannels(OrderContext ctx);
}
