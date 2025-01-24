package io.github.xiaoshicae.extension.sample.complex.ability;


import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.complex.extpoint.DelayCloseOrderExtension;
import io.github.xiaoshicae.extension.sample.complex.matchparam.MatchParam;

import java.time.Duration;

/**
 * 长关单能力
 * 实现了: 1.延迟关单扩展点
 */
@Ability(code = LongCloseOrderAbility.CODE)
public class LongCloseOrderAbility implements Matcher<MatchParam>, DelayCloseOrderExtension {
    public static final String CODE = "app.ability.long-close";


    /**
     * 长关单能力匹配生效条件
     *
     * @param param 参数
     * @return value包含long-close即表示长关单能力生效
     */
    @Override
    public Boolean match(MatchParam param) {
        return param.getValue().contains("long-close");
    }

    /**
     * 延迟关单扩展点实现
     * 返回1h
     *
     * @param orderType 订单类型
     * @return 自定关单时间
     */
    @Override
    public Duration delayCloseOrderDuration(String orderType) {
        return Duration.ofHours(1);
    }
}
