package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.time.Duration;
import java.util.List;

/**
 * 9. 售后策略扩展点
 * 按商品品类决定退换货时效
 */
@ExtensionPoint(scenarios = {"after_sale"})
public interface AfterSalePolicyExtension {

    /**
     * @param categories 商品品类
     * @return 无理由退货时长，null 表示不支持
     */
    Duration getReturnWindow(List<String> categories);
}
