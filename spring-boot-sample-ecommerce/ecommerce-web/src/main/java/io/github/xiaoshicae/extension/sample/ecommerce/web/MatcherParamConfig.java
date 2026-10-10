package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.web.MatcherParamResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * 匹配参数解析
 * 提供 MatcherParamResolver Bean 即可：框架在每个 web 请求开始前用它构造匹配参数并绑定到当前线程，
 * 用于 Business 和 Ability 的 match() 判断，请求结束后自动解绑，无需手写 Interceptor
 * 这里把订单特征(会员等级、金额、收货省份、是否加急、商品品类)都放进匹配参数，
 * 业务按 bizCode 认领请求，能力则各自按这些属性判断要不要生效
 * 不需要绑定业务身份的路径(如 admin 后台)在 easy-extension.session-exclude-path-patterns 中配置
 */
@Configuration
public class MatcherParamConfig {

    @Bean
    public MatcherParamResolver<OrderMatchParam> orderMatchParamResolver() {
        return request -> new OrderMatchParam(
                param(request, "bizCode", OrderParams.DEFAULT_BIZ_CODE),
                param(request, "memberLevel", OrderParams.DEFAULT_MEMBER_LEVEL),
                new BigDecimal(param(request, "amount", OrderParams.DEFAULT_AMOUNT)),
                Integer.parseInt(param(request, "itemCount", OrderParams.DEFAULT_ITEM_COUNT)),
                param(request, "province", OrderParams.DEFAULT_PROVINCE),
                Boolean.parseBoolean(param(request, "urgent", OrderParams.DEFAULT_URGENT)),
                OrderParams.categories(param(request, "categories", OrderParams.DEFAULT_CATEGORIES)));
    }

    private static String param(HttpServletRequest req, String name, String defaultVal) {
        String val = req.getParameter(name);
        return val != null && !val.isBlank() ? val.trim() : defaultVal;
    }
}
