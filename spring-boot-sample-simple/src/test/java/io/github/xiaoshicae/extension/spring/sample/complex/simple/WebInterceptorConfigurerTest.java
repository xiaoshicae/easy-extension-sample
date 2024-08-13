package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class WebInterceptorConfigurerTest {
    @Mock
    private Interceptor interceptor;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @InjectMocks
    private WebInterceptorConfigurer configurer;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void addInterceptors() {
        when(interceptorRegistry.addInterceptor(interceptor)).thenReturn(interceptorRegistration);
        when(interceptorRegistration.addPathPatterns(anyString())).thenReturn(interceptorRegistration);
        when(interceptorRegistration.excludePathPatterns(anyString())).thenReturn(interceptorRegistration);

        configurer.addInterceptors(interceptorRegistry);
    }
}