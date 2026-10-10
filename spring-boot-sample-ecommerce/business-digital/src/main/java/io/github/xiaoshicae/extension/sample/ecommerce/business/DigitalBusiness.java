package io.github.xiaoshicae.extension.sample.ecommerce.business;

import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.annotation.Self;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.FreeShippingAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.InstallmentAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.Return7DaysAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.ability.VipCouponAbility;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.*;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

/**
 * 数码3C业务
 * 特色: 自营15天延保、专票支持、新用户风控
 * 挂载了七天无理由、分期免息、VIP优惠和包邮能力
 * abilities 的数组顺序即优先级(靠前者优先)，Self.class 表示业务自身的位置
 */
@Business(code = DigitalBusiness.CODE,
        abilities = {Return7DaysAbility.class, InstallmentAbility.class, VipCouponAbility.class, FreeShippingAbility.class, Self.class})
public class DigitalBusiness implements Matcher<OrderMatchParam>, OrderValidateExtension, AfterSalePolicyExtension,
        RiskControlExtension, InvoiceExtension {

    public static final String CODE = "biz.digital";

    private static final int MAX_ITEM_COUNT = 5;
    private static final String NEW_USER_PREFIX = "NEW-";

    @Override
    public boolean match(OrderMatchParam param) {
        return "digital".equals(param.getBizCode());
    }

    @Override
    public String validate(BigDecimal orderAmount, int itemCount) {
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return "订单金额不能小于等于0";
        }
        // 数码商品限购，防止黄牛
        if (itemCount > MAX_ITEM_COUNT) {
            return "数码商品单次购买不能超过" + MAX_ITEM_COUNT + "件";
        }
        return null;
    }

    @Override
    public Duration getReturnWindow(List<String> categories) {
        // 数码3C 自营延保 15 天，小额标品会先命中七天无理由能力，这里兜住大额和定制机
        return Duration.ofDays(15);
    }

    @Override
    public String checkRisk(String userId, BigDecimal orderAmount) {
        // 数码风控: 新注册用户需人工审核，大额订单的信用审核由分期免息能力负责
        return userId != null && userId.startsWith(NEW_USER_PREFIX) ? "REVIEW" : "PASS";
    }

    @Override
    public String determineInvoiceType(boolean needInvoice, String invoiceTarget) {
        // 数码业务: 企业抬头开专票，个人抬头开电子发票
        if (!needInvoice) {
            return "NONE";
        }
        return "company".equals(invoiceTarget) ? "SPECIAL" : "ELECTRONIC";
    }
}
