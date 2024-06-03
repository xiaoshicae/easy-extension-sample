package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.ISessionManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class Interceptor implements HandlerInterceptor {

    // 框架启动会自动注入ISessionManager，可以直接通过spring自动注入获取
    @Resource
    private ISessionManager<MyParam> sessionManager;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 根据入参构造用于business和ability匹配的参数
        String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "unknown";

        // 初始化session
        sessionManager.initSession(new MyParam(name));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //  清空session
        sessionManager.removeSession();
    }
}
