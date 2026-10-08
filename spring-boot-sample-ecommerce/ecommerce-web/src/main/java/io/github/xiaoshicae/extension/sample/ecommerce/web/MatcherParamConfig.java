package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.web.MatcherParamResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 匹配参数解析
 * 提供 MatcherParamResolver Bean 即可：框架在每个 web 请求开始前用它构造匹配参数并绑定到当前线程，
 * 用于 Business 和 Ability 的 match() 判断，请求结束后自动解绑，无需手写 Interceptor
 * 不需要绑定业务身份的路径(如 admin 后台)在 easy-extension.session-exclude-path-patterns 中配置
 */
@Configuration
public class MatcherParamConfig {

    @Bean
    public MatcherParamResolver<OrderMatchParam> orderMatchParamResolver() {
        return request -> {
            String bizCode = getParam(request, "bizCode", "retail");
            String abilityCodesStr = getParam(request, "abilityCodes", "");
            List<String> abilityCodes = abilityCodesStr.isEmpty()
                    ? List.of()
                    : List.of(abilityCodesStr.split(","));
            return new OrderMatchParam(bizCode, abilityCodes);
        };
    }

    private static String getParam(HttpServletRequest req, String name, String defaultVal) {
        String val = req.getParameter(name);
        return val != null ? val.trim() : defaultVal;
    }
}
