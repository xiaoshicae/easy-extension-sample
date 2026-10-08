package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.spring.boot.autoconfigure.web.MatcherParamResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 提供MatcherParamResolver Bean即可：框架在每个web请求开始前，用它从HTTP请求构造匹配参数<MyParam>并绑定到当前线程，
 * 请求结束后自动解绑，无需手写Interceptor
 */
@Configuration
public class MatcherParamConfig {

    @Bean
    public MatcherParamResolver<MyParam> matcherParamResolver() {
        return request -> {
            String name = request.getParameter("name");
            return new MyParam(name != null ? name.trim() : "unknown");
        };
    }
}
