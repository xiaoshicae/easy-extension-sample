package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.business.AbstractBusiness;
import io.github.xiaoshicae.extension.core.business.UsedAbility;
import org.springframework.stereotype.Component;

import java.util.List;

// 业务A，实现了扩展点1和扩展点2
// 需要@Component注解，以便spring包扫描识别
@Component
public class BusinessA extends AbstractBusiness<MyParam> implements Ext1, Ext2{
    // 业务A身份唯一标识
    @Override
    public String code() {
        return "x.business.a";
    }

    // 命中业务A的生效条件，通过session传递下来，进行匹配
    @Override
    public Boolean match(MyParam param) {
        return param != null && param.getName().equals("a");
    }

    // 优先级(复杂场景下，业务挂载了能力，可能存在扩展点冲突，需要通过优先级解决冲突)
    // 当前样例先忽略该方法
    @Override
    public Integer priority() {
        return 100;
    }

    // 业务A挂载了哪些能力
    @Override
    public List<UsedAbility> usedAbilities() {
        return List.of();
    }

    // 业务A实现了扩展点1
    @Override
    public String doSomething1() {
        return "businessA doSomething1";
    }

    // 业务A实现了扩展点2
    @Override
    public String doSomething2() {
        return "businessA doSomething2";
    }
}
