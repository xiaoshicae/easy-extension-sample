package io.github.xiaoshicae.extension.nonspring.sample.extension.ability;

import io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint.AutoCloseOrderTimeExt;
import io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint.BuildOrderTagExt;
import io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint.SkipSendMsgExt;
import io.github.xiaoshicae.extension.nonspring.sample.extension.paramdto.ParamDTO;
import io.github.xiaoshicae.extension.core.ability.AbstractAbility;
import io.github.xiaoshicae.extension.nonspring.sample.dto.OrderTagDTO;

import java.time.Duration;
import java.util.List;

public class FreeTrialAbility extends AbstractAbility<ParamDTO> implements AutoCloseOrderTimeExt, BuildOrderTagExt, SkipSendMsgExt {
    // 能力code
    public static final String abilityCode = "ecom.ability.free";

    @Override
    public String code() {
        return abilityCode;
    }

    // secondCategory == "free" 会匹配到该能力
    @Override
    public Boolean match(ParamDTO productInfo) {
        return productInfo.getSecondCategory().equals("free") || productInfo.getExtra().equals("lemon");
    }

    // 能力的扩展点实现
    @Override
    public List<OrderTagDTO> buildOrderTagExt() {
        return List.of(new OrderTagDTO("is_free_trail", "true"));
    }

    // 能力的扩展点实现
    @Override
    public boolean skipSendMsg() {
        return true;
    }

    // 能力的扩展点实现
    @Override
    public Duration autoCloseOrderTime() {
        return Duration.ofDays(30);
    }
}
