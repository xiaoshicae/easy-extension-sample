package io.github.xiaoshicae.extension.spring.sample.complex.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import io.github.xiaoshicae.extension.spring.sample.complex.extension.paramdto.ParamDTO;
import io.github.xiaoshicae.extension.core.ISessionManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class BusinessInterceptor implements HandlerInterceptor {

    @Resource
    private ISessionManager<ParamDTO> sessionManager;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 根据入参构造用于business和ability匹配的参数
        String category = StringUtils.defaultString(request.getParameter("category"));
        String secondCategory = StringUtils.defaultString(request.getParameter("secondCategory"));
        String extra = StringUtils.defaultString(request.getParameter("extra"));

        // 初始化session
        sessionManager.initSession(new ParamDTO(category, secondCategory, extra));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //  清空session
        sessionManager.removeSession();
    }
}
