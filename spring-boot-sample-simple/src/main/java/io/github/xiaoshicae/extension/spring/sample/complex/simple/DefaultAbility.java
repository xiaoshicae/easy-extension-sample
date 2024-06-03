package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.BaseDefaultAbility;
import org.springframework.stereotype.Component;

// 默认能力，必须实现所有@ExtensionPoint注解标注的扩展点
// 业务没有实现某个扩展点时，默认能力作为兜底实现
// 需要@Component注解，以便spring包扫描识别
@Component
public class DefaultAbility extends BaseDefaultAbility<MyParam> implements Ext1, Ext2, Ext3 {
    @Override
    public String doSomething1() {
        return "default doSomething1";
    }

    @Override
    public String doSomething2() {
        return "default doSomething2";
    }

    @Override
    public String doSomething3() {
        return "default doSomething3";
    }
}
