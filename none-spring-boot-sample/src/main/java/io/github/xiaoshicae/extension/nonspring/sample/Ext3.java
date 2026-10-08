package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

/**
 * 扩展点3
 * 需要@ExtensionPoint注解
 */
@ExtensionPoint
public interface Ext3 {
    String doSomething3();
}
