package io.github.xiaoshicae.extension.sample.complex.ability;


import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.complex.extpoint.DelayCloseOrderExtension;
import io.github.xiaoshicae.extension.sample.complex.extpoint.SkipCheckZeroPriceExtension;
import io.github.xiaoshicae.extension.sample.complex.matchparam.MatchParam;

import java.time.Duration;

/**
 * 免费体验能力
 * 实现了: 1.跳过0元校验扩展点 2.延迟关单扩展点
 */
@Ability(code = FreeTrialAbility.CODE)
public class FreeTrialAbility implements Matcher<MatchParam>, DelayCloseOrderExtension, SkipCheckZeroPriceExtension  {
    public static final String CODE = "app.ability.free-trial";

    /**
     * 免费体验能力匹配生效条件
     *
     * @param param 参数
     * @return value包含free-trial即表示免费体验能力生效
     */
    @Override
    public Boolean match(MatchParam param) {
        return param.getValue().contains("free-trial");
    }

    /**
     * 延迟关单扩展点实现
     * 返回0，即不关单
     *
     * @param orderType 订单类型
     * @return 自定关单时间
     */
    @Override
    public Duration delayCloseOrderDuration(String orderType) {
        return Duration.ZERO;
    }


    /**
     * 跳过0元校验扩展点实现
     * 返回true
     *
     * @return 是否跳过
     */
    @Override
    public Boolean skipCheckZeroPrice() {
        return true;
    }
}
