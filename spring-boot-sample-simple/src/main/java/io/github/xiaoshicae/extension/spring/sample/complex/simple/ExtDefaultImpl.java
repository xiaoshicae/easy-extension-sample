package io.github.xiaoshicae.extension.spring.sample.complex.simple;


import io.github.xiaoshicae.extension.core.annotation.DefaultImplementation;


/**
 * 扩展点的默认实现
 * 当命中的业务和生效的能力都没有实现某个扩展点时，默认实现会作为兜底逻辑
 * 需要@DefaultImplementation注解；每个扩展点至多一个默认实现，一个类可以同时兜底多个扩展点
 */
@DefaultImplementation
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
