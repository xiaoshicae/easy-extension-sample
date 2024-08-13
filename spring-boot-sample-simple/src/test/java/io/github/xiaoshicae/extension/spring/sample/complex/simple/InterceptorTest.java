package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.ISessionManager;
import io.github.xiaoshicae.extension.core.exception.ExtensionException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterceptorTest {
    @Mock
    private ISessionManager<MyParam> mockSessionManager;

    @Mock
    private HttpServletRequest mockRequest;

    @InjectMocks
    private Interceptor interceptor;

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
    void preHandle() throws Exception {
        when(mockRequest.getParameter(anyString())).thenReturn("123");
        doNothing().when(mockSessionManager).initSession(any());
        boolean b = interceptor.preHandle(mockRequest, null, null);
        assertTrue(b);

        doThrow(ExtensionException.class).when(mockSessionManager).initSession(any());
        assertThrows(ExtensionException.class, ()-> interceptor.preHandle(mockRequest, null, null));
    }

    @Test
    void afterCompletion() throws Exception {
        interceptor.afterCompletion(null, null, null, null);
    }
}
