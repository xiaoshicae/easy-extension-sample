package io.github.xiaoshicae.extension.nonspring.sample;


import io.github.xiaoshicae.extension.core.business.AbstractBusiness;
import io.github.xiaoshicae.extension.core.business.UsedAbility;
import io.github.xiaoshicae.extension.core.common.Matcher;

import java.util.List;

/**
 * 业务B
 * 实现了扩展点1和扩展点3
 * 业务B没有挂载任何能力
 */
public class BusinessB extends AbstractBusiness<MyParam>  implements Matcher<MyParam>, Ext1, Ext3 {

    /**
     * 业务code
     */
    @Override
    public String code() {
        return "app.business.b";
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
        return param.getName().contains("biz-b");
    }

    /**
     * BusinessB实现了扩展点1和3
     */
    @Override
    public List<Class<?>> implementExtensionPoints() {
        return List.of(Ext1.class, Ext3.class);
    }

    /**
     * 扩展点1的BusinessB自定义实现
     */
    @Override
    public String doSomething1() {
        return "BusinessB doSomething1";
    }

    /**
     * 扩展点3的BusinessB自定义实现
     */
    @Override
    public String doSomething3() {
        return "BusinessB doSomething3";
    }

    /**
     * BusinessB的优先级
     */
    @Override
    public Integer priority() {
        return 0;
    }

    /**
     * BusinessB未使用任何能力
     */
    @Override
    public List<UsedAbility> usedAbilities() {
        return List.of();
    }
}
