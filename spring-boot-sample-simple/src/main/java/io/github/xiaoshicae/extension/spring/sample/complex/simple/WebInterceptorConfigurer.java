package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.IExtensionSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;


/**
 * web请求的Interceptor
 * 在请求处理前，准备好生效匹配的参数<MyParam>
 * 传给EasyExtension的ISessionManager进行管理，后续扩展点调用是会遍历判断哪个业务和哪些能力生效 (即调用match判断是否生效)
 */
@Component
public class WebInterceptorConfigurer implements WebMvcConfigurer {

    @Resource
    private IExtensionSession<MyParam> session;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor interceptor = new HandlerInterceptor() {
            // 请求开始前需要准备生效匹配的参数<MyParam>
            // 初始化session
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "unknown";
                session.initSession(new MyParam(name));
                return true;
            }

            // 请求结束后需要清空session
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                session.removeSession();
            }
        };

        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/favicon.ico");
    }
}
