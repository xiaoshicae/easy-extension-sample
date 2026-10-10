package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.math.BigDecimal;

/**
 * 3. 促销计算扩展点
 * 计算优惠券、满减、折扣等促销优惠
 */
@ExtensionPoint(scenarios = {"create_order"})
public interface PromotionCalcExtension {

    /**
     * @param orderAmount 订单金额
     * @return 优惠金额（正数表示减免）
     */
    BigDecimal calcPromotion(BigDecimal orderAmount);
}
