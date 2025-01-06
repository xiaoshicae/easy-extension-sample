package io.github.xiaoshicae.extension.sample.complex.web;

import io.github.xiaoshicae.extension.sample.complex.extpoint.CalculatePriceExtension;
import io.github.xiaoshicae.extension.sample.complex.extpoint.DelayCloseOrderExtension;
import io.github.xiaoshicae.extension.sample.complex.dto.OrderDTO;
import io.github.xiaoshicae.extension.sample.complex.extpoint.SkipCheckZeroPriceExtension;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionInject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api")
public class Controller {

    /**
     * 算价扩展点
     * 动态匹配不同业务的具体实现
     * 未匹配到任何实现时，会走默认实现兜底
     */
    @ExtensionInject
    private CalculatePriceExtension calculatePriceExtension;

    /**
     * 延迟关单扩展点
     */
    @ExtensionInject
    private DelayCloseOrderExtension delayCloseOrderExtension;

    /**
     * 跳过0元校验扩展点
     * 会匹配所有生效的扩展点实现 (包括默认能力)
     */
    @ExtensionInject
    private List<SkipCheckZeroPriceExtension> skipCheckZeroPriceExtensions;


    @RequestMapping("/process")
    public String process() {

        // 调用算价扩展点
        OrderDTO orderDTO = new OrderDTO("1", BigDecimal.valueOf(100), BigDecimal.valueOf(0.9));
        BigDecimal price = calculatePriceExtension.calculatePrice(orderDTO);

        Duration closeOrderDuration = delayCloseOrderExtension.delayCloseOrderDuration("ot");

        List<Boolean> skipCheckList = new ArrayList<>();
        for (SkipCheckZeroPriceExtension ext : skipCheckZeroPriceExtensions) {
            skipCheckList.add(ext.skipCheckZeroPrice());
        }

        return String.format("res: price = %.3f && close order duration = %s && skip ckeck list = %s", price, closeOrderDuration.toString(), Arrays.toString(skipCheckList.toArray()));
    }
}
