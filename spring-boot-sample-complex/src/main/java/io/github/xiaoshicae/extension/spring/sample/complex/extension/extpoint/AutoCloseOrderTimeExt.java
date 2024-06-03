package io.github.xiaoshicae.extension.spring.sample.complex.extension.extpoint;


import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

import java.time.Duration;

// 超时支付，自动关单时间扩展点
@ExtensionPoint
public interface AutoCloseOrderTimeExt {
    Duration autoCloseOrderTime();
}
