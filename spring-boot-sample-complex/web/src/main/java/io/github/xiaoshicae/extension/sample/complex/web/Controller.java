package io.github.xiaoshicae.extension.sample.complex.web;

import io.github.xiaoshicae.extension.core.IExtensionInvoker;
import io.github.xiaoshicae.extension.sample.complex.extpoint.CalculatePriceExtension;
import io.github.xiaoshicae.extension.sample.complex.extpoint.DelayCloseOrderExtension;
import io.github.xiaoshicae.extension.sample.complex.dto.OrderDTO;
import io.github.xiaoshicae.extension.sample.complex.extpoint.SkipCheckZeroPriceExtension;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionInject;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 注入扩展点调用invoker
     * 支持单个扩展点调用   E res = invoke(Ext.class, e -> e.xxx())
     * 支持多个个扩展点调用 List<E> resList = invokeAll(Ext.class, e -> e.xxx())
     * 支持单个扩展点调用   E res = scopedInvoke("s", Ext.class, e -> e.xxx())
     * 支持多个个扩展点调用 List<E> resList = scopedInvokeAll("s", Ext.class, e -> e.xxx())
     */
    @Autowired
    private IExtensionInvoker extensionInvoker;

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

        return String.format("res: price = %.3f && close order duration = %s && skip check list = %s", price, closeOrderDuration.toString(), Arrays.toString(skipCheckList.toArray()));
    }

    /**
     * 通过invoker调用扩展点
     * <br>
     * 如果一起请求有多个业务并行，这个时候需要用invoker通过scopedInvoke指定特定的scope进行调用
     */
    @RequestMapping("/process-with-invoker")
    public String processWithInvoker() throws Exception {
        OrderDTO orderDTO = new OrderDTO("2", BigDecimal.valueOf(100), BigDecimal.valueOf(0.9));

        // 通过spring注入方法调用扩展点
        BigDecimal price1 = calculatePriceExtension.calculatePrice(orderDTO);

        // 通过invoke方式调用扩展点
        BigDecimal price2 = extensionInvoker.invoke(CalculatePriceExtension.class, e -> e.calculatePrice(orderDTO));

        // 通过invoke方式调用scope扩展点
        BigDecimal price3 = extensionInvoker.scopedInvoke("xxx", CalculatePriceExtension.class, e -> e.calculatePrice(orderDTO));
        return String.format("res: price1 = %.3f && price2 = %.3f && price3 = %.3f", price1, price2, price3);
    }
}
