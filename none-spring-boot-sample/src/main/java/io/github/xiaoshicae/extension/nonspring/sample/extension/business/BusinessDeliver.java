package io.github.xiaoshicae.extension.nonspring.sample.extension.business;

import io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint.AutoCloseOrderTimeExt;
import io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint.BuildOrderTagExt;
import io.github.xiaoshicae.extension.nonspring.sample.extension.paramdto.ParamDTO;
import io.github.xiaoshicae.extension.core.business.AbstractBusiness;
import io.github.xiaoshicae.extension.core.business.UsedAbility;
import io.github.xiaoshicae.extension.nonspring.sample.extension.ability.FreeTrialAbility;
import io.github.xiaoshicae.extension.nonspring.sample.extension.ability.PreSaleAbility;
import io.github.xiaoshicae.extension.nonspring.sample.dto.OrderTagDTO;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

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
