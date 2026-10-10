package io.github.xiaoshicae.extension.sample.ecommerce.web;

import java.util.Arrays;
import java.util.List;

/**
 * 下单请求参数的默认值与解析
 * 匹配参数(MatcherParamConfig)和下单流程(OrderController)读的是同一份请求参数，
 * 默认值集中在这里，保证两边看到的是同一张订单
 */
final class OrderParams {

    static final String DEFAULT_BIZ_CODE = "retail";
    static final String DEFAULT_MEMBER_LEVEL = "NORMAL";
    static final String DEFAULT_AMOUNT = "397.00";
    static final String DEFAULT_ITEM_COUNT = "3";
    static final String DEFAULT_PROVINCE = "广东省";
    static final String DEFAULT_URGENT = "false";
    static final String DEFAULT_CATEGORIES = "general";
    static final String DEFAULT_USER_ID = "USER-12345";
    static final String DEFAULT_NEED_INVOICE = "false";
    static final String DEFAULT_INVOICE_TARGET = "personal";
    static final String DEFAULT_NOTIFY_EVENT = "ORDER_CREATED";

    private OrderParams() {
    }

    /**
     * 商品品类，多个用英文逗号分隔，如 categories=digital,custom
     */
    static List<String> categories(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
