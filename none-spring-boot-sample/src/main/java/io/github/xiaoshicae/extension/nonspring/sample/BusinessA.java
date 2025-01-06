package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.business.AbstractBusiness;
import io.github.xiaoshicae.extension.core.business.UsedAbility;
import io.github.xiaoshicae.extension.core.common.Matcher;

import java.util.List;

/**
 * 业务A
 * 实现了扩展点1
 * 业务挂载了能力，即继承了能力的扩展点实现
 */
public class BusinessA extends AbstractBusiness<MyParam>  implements Matcher<MyParam>, Ext1 {

    /**
     * 业务code
     */
    @Override
    public String code() {
        return "";
    }

    /**
     * 业务命中判断
     * 业务必须实现Matcher<XXX>
     *
     * @param param for match predict
     * @return 业务是否命中
     */
    @Override
    public Boolean match(MyParam param) {
        return param.getName().contains("biz-a");
    }

    /**
     * BusinessA实现了扩展点1
     */
    @Override
    public List<Class<?>> implementExtensionPoints() {
        return List.of(Ext1.class);
    }

    /**
     * 扩展点1的BusinessA自定义实现
     */
    @Override
    public String doSomething1() {
        return "BusinessA doSomething1";
    }


    /**
     * BusinessA的优先级
     */
    @Override
    public Integer priority() {
        return 0;
    }


    /**
     * BusinessA使用了AbilityX
     */
    @Override
    public List<UsedAbility> usedAbilities() {
        return List.of(new UsedAbility("app.ability.x", 1));
    }
}
