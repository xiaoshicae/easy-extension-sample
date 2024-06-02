package io.github.xiaoshicae.extension.nonspring.sample.extension.business;

import io.github.xiaoshicae.extension.nonspring.sample.extension.extpoint.AutoCloseOrderTimeExt;
import io.github.xiaoshicae.extension.nonspring.sample.extension.paramdto.ParamDTO;
import io.github.xiaoshicae.extension.core.business.AbstractBusiness;
import io.github.xiaoshicae.extension.core.business.UsedAbility;
import io.github.xiaoshicae.extension.nonspring.sample.extension.ability.PreSaleAbility;

import java.time.Duration;
import java.util.List;

public class BusinessFilm extends AbstractBusiness<ParamDTO> implements AutoCloseOrderTimeExt {
    // 业务身份code
    private static final String businessCode = "ecom.groupon.film";

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

    // category == "film" 会匹配到该业务身份
    @Override
    public Boolean match(ParamDTO productInfo) {
        return productInfo.getCategory().equals("film");
    }

    // 挂载了预售能力
    @Override
    public List<UsedAbility> usedAbilities() {
        return List.of(new UsedAbility(PreSaleAbility.abilityCode, 200));
    }

    // 业务自定义扩展点实现
    @Override
    public Duration autoCloseOrderTime() {
        return Duration.ofMinutes(10);
    }
}
