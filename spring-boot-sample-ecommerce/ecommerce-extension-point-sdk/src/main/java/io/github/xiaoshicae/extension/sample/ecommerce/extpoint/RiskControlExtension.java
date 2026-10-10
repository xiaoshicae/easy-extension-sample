package io.github.xiaoshicae.extension.sample.ecommerce.extpoint;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.math.BigDecimal;

/**
 * 6. 风控检查扩展点
 * 检测刷单、套现、欺诈等风险，返回处置建议
 */
@ExtensionPoint(scenarios = {"create_order", "payment"})
public interface RiskControlExtension {

    /**
     * @param userId      下单用户
     * @param orderAmount 订单金额
     * @return 风险等级: PASS(通过), REVIEW(人工审核), REJECT(拒绝)
     */
    String checkRisk(String userId, BigDecimal orderAmount);
}
