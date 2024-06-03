package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.business.AbstractBusiness;
import io.github.xiaoshicae.extension.core.business.UsedAbility;
import org.springframework.stereotype.Component;

import java.util.List;


// 业务B，实现了扩展点1和扩展点3
// 需要@Component注解，以便spring包扫描识别
@Component
public class BusinessB extends AbstractBusiness<MyParam> implements Ext1, Ext3{
    // 业务身B份唯一标识
    @Override
    public String code() {
        return "x.business.b";
    }

    // 命中业务B的生效条件
    @Override
    public Boolean match(MyParam param) {
        return param != null && param.getName().equals("b");
    }

    // 优先级(复杂场景下，业务挂载了能力，可能存在扩展点冲突，需要通过优先级解决冲突)
    // 当前样例先忽略该方法
    @Override
    public Integer priority() {
        return 100;
    }

    // 业务B挂载了哪些能力
    @Override
    public List<UsedAbility> usedAbilities() {
        return List.of();
    }

    // 业务B实现了扩展点1
    @Override
    public String doSomething1() {
        return "businessB doSomething1";
    }

    // 业务B实现了扩展点3
    @Override
    public String doSomething3() {
        return "businessB doSomething3";
    }
}
