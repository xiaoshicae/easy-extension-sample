package io.github.xiaoshicae.extension.sample.complex.business;


import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;
import io.github.xiaoshicae.extension.sample.complex.extpoint.DelayCloseOrderExtension;
import io.github.xiaoshicae.extension.sample.complex.matchparam.MatchParam;

import java.time.Duration;


/**
 * 电影票业务
 * 挂载了: 1.免费体验能力(priority:10) 2.长关单能力(priority:20)
 * 实现了延迟关单扩展点(priority:30)
 * 挂载的2个能力和业务本身都实现了延迟关单扩展点，冲突是会按优先级匹配
 */
@Business(code = FilmBusiness.CODE, priority = 30, abilities = {"app.ability.free-trial::10", "app.ability.long-close::20"})
public class FilmBusiness implements Matcher<MatchParam>, DelayCloseOrderExtension {
    public static final String CODE = "biz.groupon.film";

    /**
     * 电影票业务
     *
     * @param param 参数
     * @return name包含film即表示是酒旅业务
     */
    @Override
    public Boolean match(MatchParam param) {
        return param.getName().contains("film");
    }

    /**
     * 延迟关单扩展点实现
     * 电影票默认3min未支付自动关单
     *
     * @param orderType 订单类型
     * @return 自动关单时间
     */
    @Override
    public Duration delayCloseOrderDuration(String orderType) {
        return Duration.ofMinutes(3);
    }
}
