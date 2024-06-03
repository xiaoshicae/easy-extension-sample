package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


// 注入Interceptor
@Configuration
public class WebInterceptorConfigurer implements WebMvcConfigurer {
    @Resource
    private Interceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/favicon.ico");
    }
}
