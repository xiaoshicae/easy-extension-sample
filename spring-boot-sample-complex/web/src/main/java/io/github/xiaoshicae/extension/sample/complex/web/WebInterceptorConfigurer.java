package io.github.xiaoshicae.extension.sample.complex.web;

import io.github.xiaoshicae.extension.core.ISessionManager;
import io.github.xiaoshicae.extension.sample.complex.matchparam.MatchParam;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web请求的Interceptor
 * 在请求处理前，准备好生效匹配的参数<MatchParam>
 * 传给EasyExtension的ISessionManager进行管理，后续扩展点调用是会遍历判断哪个业务和哪些能力生效 (即调用match判断是否生效)
 */
@Component
public class WebInterceptorConfigurer implements WebMvcConfigurer {

    @Resource
    private ISessionManager<MatchParam> sessionManager;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor interceptor = new HandlerInterceptor() {

            // 请求开始前需要初始化session
            // 注入匹配参数，用于Business生效匹配
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "unknown";
                String value = request.getParameter("value") != null ? request.getParameter("value").trim() : "unknown";
                sessionManager.initSession(new MatchParam(name, value));

                String scope = request.getParameter("scope") != null ? request.getParameter("scope").trim() : "";
                String scopedName = request.getParameter("scopedName") != null ? request.getParameter("scopedName").trim() : "unknown";
                String scopedValue = request.getParameter("scopedValue") != null ? request.getParameter("scopedValue").trim() : "unknown";
                sessionManager.initScopedSession(scope, new MatchParam(scopedName, scopedValue));
                return true;
            }

            // 请求结束后需要清空session
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                sessionManager.removeSession();
            }
        };

        registry.addInterceptor(interceptor).addPathPatterns("/api/**");
    }
}
