package io.github.xiaoshicae.extension.spring.sample.complex.simple;


import io.github.xiaoshicae.extension.core.annotation.ExtensionPointDefaultImplementation;


/**
 * 扩展点的默认实现
 * 需要实现所有的扩展点，当命中的能力和生效的能力都没有实现某个扩展点是，默认实现会作为兜底逻辑
 * 需要@ExtensionPointDefaultImplementation注解
 */
@ExtensionPointDefaultImplementation
public class ExtDefaultImpl implements Ext1, Ext2, Ext3 {

    /**
     * 扩展点1的默认实现
     */
    @Override
    public String doSomething1() {
        return "Default doSomething1";
    }

    /**
     * 扩展点2的默认实现
     */
    @Override
    public String doSomething2() {
        return "Default doSomething2";
    }

    /**
     * 扩展点3的默认实现
     */
    @Override
    public String doSomething3() {
        return "Default doSomething3";
    }
}
