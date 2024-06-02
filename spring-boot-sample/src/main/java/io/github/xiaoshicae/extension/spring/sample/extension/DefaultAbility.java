package io.github.xiaoshicae.extension.spring.sample.extension;

import io.github.xiaoshicae.extension.spring.sample.dto.OrderTagDTO;
import io.github.xiaoshicae.extension.spring.sample.extension.extpoint.AutoCloseOrderTimeExt;
import io.github.xiaoshicae.extension.spring.sample.extension.extpoint.BuildOrderTagExt;
import io.github.xiaoshicae.extension.spring.sample.extension.extpoint.SkipSendMsgExt;
import io.github.xiaoshicae.extension.spring.sample.extension.paramdto.ParamDTO;
import org.springframework.stereotype.Component;
import io.github.xiaoshicae.extension.core.BaseDefaultAbility;

import java.time.Duration;
import java.util.List;

// 默认能力，在所有扩展点都没命中的情况下，会走系统提供的默认扩展点实现
@Component
public class DefaultAbility extends BaseDefaultAbility<ParamDTO> implements AutoCloseOrderTimeExt, BuildOrderTagExt, SkipSendMsgExt {

    @Override
    public Duration autoCloseOrderTime() {
        return Duration.ofMinutes(5);
    }

    @Override
    public List<OrderTagDTO> buildOrderTagExt() {
        return List.of();
    }

    @Override
    public boolean skipSendMsg() {
        return false;
    }
}
