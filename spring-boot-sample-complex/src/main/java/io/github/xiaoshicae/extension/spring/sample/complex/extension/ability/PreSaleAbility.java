package io.github.xiaoshicae.extension.spring.sample.complex.extension.ability;

import io.github.xiaoshicae.extension.spring.sample.complex.dto.OrderTagDTO;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.extpoint.BuildOrderTagExt;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.paramdto.ParamDTO;
import org.springframework.stereotype.Component;
import io.github.xiaoshicae.extension.core.ability.AbstractAbility;

import java.util.List;


// 预售能力
@Component
public class PreSaleAbility extends AbstractAbility<ParamDTO> implements BuildOrderTagExt {
    // 能力code
    public static final String abilityCode = "ecom.ability.presale";

    @Override
    public String code() {
        return abilityCode;
    }

    // secondCategory == "presale" 会匹配到该能力
    @Override
    public Boolean match(ParamDTO productInfo) {
        return productInfo.getSecondCategory().equals("presale");
    }

    // 能力的扩展点实现
    @Override
    public List<OrderTagDTO> buildOrderTagExt() {
        return List.of(new OrderTagDTO("is_presale", "true"));
    }
}
