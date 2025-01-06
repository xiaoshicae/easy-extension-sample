package io.github.xiaoshicae.extension.nonspring.sample;


import io.github.xiaoshicae.extension.core.AbstractExtensionPointDefaultImplementation;

import java.util.List;


/**
 * 扩展点的默认实现
 * 需要实现所有的扩展点，当命中的能力和生效的能力都没有实现某个扩展点是，默认实现会作为兜底逻辑
 */
public class ExtDefaultImpl extends AbstractExtensionPointDefaultImplementation<MyParam> implements Ext1,Ext2,Ext3 {

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

    /**
     * 扩展点的实现情况
     */
    @Override
    public List<Class<?>> implementExtensionPoints() {
        return List.of(Ext1.class, Ext2.class, Ext3.class);
    }
}
