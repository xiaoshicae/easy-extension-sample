package io.github.xiaoshicae.extension.spring.sample.complex.extension.business;


import io.github.xiaoshicae.extension.spring.sample.complex.dto.OrderTagDTO;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.ability.FreeTrialAbility;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.ability.PreSaleAbility;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.extpoint.AutoCloseOrderTimeExt;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.extpoint.BuildOrderTagExt;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.paramdto.ParamDTO;
import org.springframework.stereotype.Component;
import io.github.xiaoshicae.extension.core.business.AbstractBusiness;
import io.github.xiaoshicae.extension.core.business.UsedAbility;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;


@Component
public class BusinessDeliver extends AbstractBusiness<ParamDTO> implements AutoCloseOrderTimeExt, BuildOrderTagExt {
    // 业务身份code
    private static final String businessCode = "ecom.deliver.deliver";

    // 业务实现的扩展点优先级，数值越小优先级越高
    private static final Integer businessPriority = 100;

    @Override
    public String code() {
        return businessCode;
    }

    @Override
    public Integer priority() {
        return businessPriority;
    }

    // category == "deliver" 会匹配到该业务身份
    @Override
    public Boolean match(ParamDTO productInfo) {
        return productInfo.getCategory().equals("deliver");
    }

    // 挂载了预售能力，且优先级为10(大于业务本身的优先级)
    @Override
    public List<UsedAbility> usedAbilities() {
        return List.of(new UsedAbility(PreSaleAbility.abilityCode, 10), new UsedAbility(FreeTrialAbility.abilityCode, 20));
    }

    // 业务自定义扩展点实现
    public Duration autoCloseOrderTime() {
        return Duration.ofMinutes(20);
    }

    // 业务自定义扩展点实现
    @Override
    public List<OrderTagDTO> buildOrderTagExt() {
        return Arrays.asList(new OrderTagDTO("order_type", "deliver"));
    }
}
