package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.annotation.ExtensionPoint;

/**
 * 扩展点2
 * 需要@ExtensionPoint注解
 */
@ExtensionPoint
public interface Ext2 {
    String doSomething2();
}
