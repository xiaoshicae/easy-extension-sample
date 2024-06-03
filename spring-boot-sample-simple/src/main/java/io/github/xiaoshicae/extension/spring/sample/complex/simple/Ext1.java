package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

// 扩展点1，需要@ExtensionPoint注解，以便spring包扫描识别
@ExtensionPoint
public interface Ext1 {
    String doSomething1();
}
