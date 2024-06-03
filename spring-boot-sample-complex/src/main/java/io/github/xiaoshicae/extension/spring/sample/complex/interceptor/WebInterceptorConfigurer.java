package io.github.xiaoshicae.extension.spring.sample.complex.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebInterceptorConfigurer implements WebMvcConfigurer {

    private BusinessInterceptor interceptor;

    @Autowired
    public void setInterceptor(BusinessInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/favicon.ico");
    }
}
