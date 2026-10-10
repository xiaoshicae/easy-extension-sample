package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.util.List;

/**
 * 10. 通知策略扩展点
 * 按订单事件决定通知方式（短信、邮件、推送等）
 */
@ExtensionPoint(scenarios = {"create_order", "fulfillment", "after_sale"})
public interface NotifyExtension {

    /**
     * @param notifyEvent 订单事件: ORDER_CREATED(下单成功), SHIPPED(已发货), AFTER_SALE(售后)
     * @return 通知渠道列表: SMS, EMAIL, PUSH, WECHAT_MSG
     */
    List<String> getNotifyChannels(String notifyEvent);
}
