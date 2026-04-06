package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.core.IExtensionSession;
import io.github.xiaoshicae.extension.core.exception.SessionException;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 拦截器
 * 在请求处理前初始化匹配参数，用于 Business 和 Ability 的 match() 判断
 */
@Component
public class OrderInterceptorConfigurer implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(OrderInterceptorConfigurer.class);
    private final IExtensionSession<OrderMatchParam> session;

    public OrderInterceptorConfigurer(IExtensionSession<OrderMatchParam> session) {
        this.session = session;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor interceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String bizCode = getParam(request, "bizCode", "retail");
                String abilityCodesStr = getParam(request, "abilityCodes", "");
                List<String> abilityCodes = abilityCodesStr.isEmpty()
                        ? List.of()
                        : List.of(abilityCodesStr.split(","));
                try {
                    session.initSession(new OrderMatchParam(bizCode, abilityCodes));
                } catch (SessionException e) {
                    log.error("Failed to init session for bizCode={}", bizCode, e);
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                session.removeSession();
            }

            private String getParam(HttpServletRequest req, String name, String defaultVal) {
                String val = req.getParameter(name);
                return val != null ? val.trim() : defaultVal;
            }
        };
        registry.addInterceptor(interceptor).addPathPatterns("/api/order/**");
    }
}
