package io.github.xiaoshicae.extension.sample.complex.business;


import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.complex.extpoint.CalculatePriceExtension;
import io.github.xiaoshicae.extension.sample.complex.matchparam.MatchParam;
import io.github.xiaoshicae.extension.sample.complex.dto.OrderDTO;

import java.math.BigDecimal;


/**
 * 酒旅业务
 * 没有挂载任何能力
 * 实现了算价扩展点
 */
@Business(code = TripBusiness.CODE)
public class TripBusiness implements Matcher<MatchParam>, CalculatePriceExtension {
    public static final String CODE = "biz.trip.base";

    /**
     * 酒旅业务匹配生效条件
     *
     * @param param 参数
     * @return name包含xxx-trip即表示是酒旅业务
     */
    @Override
    public Boolean match(MatchParam param) {
        return param.getName().contains("xxx-trip");
    }


    /**
     * 酒旅业务算价接口自定义实现
     * 折扣固定未65折
     *
     * @param order 订单
     * @return 订单价格
     */
    @Override
    public BigDecimal calculatePrice(OrderDTO order) {
        BigDecimal discount = BigDecimal.valueOf(0.65);
        return order.getOriginalPrice().multiply(discount);
    }
}
