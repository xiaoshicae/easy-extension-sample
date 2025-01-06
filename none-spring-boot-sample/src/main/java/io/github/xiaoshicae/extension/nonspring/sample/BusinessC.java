package io.github.xiaoshicae.extension.nonspring.sample;


import io.github.xiaoshicae.extension.core.business.AbstractBusiness;
import io.github.xiaoshicae.extension.core.business.UsedAbility;
import io.github.xiaoshicae.extension.core.common.Matcher;

import java.util.List;

/**
 * 业务C
 * 业务C没有挂载任何能力，也没有实现任何扩展点，因此所哟扩展点均用系统默认实现
 */
public class BusinessC extends AbstractBusiness<MyParam> implements Matcher<MyParam> {

    /**
     * 业务code
     */
    @Override
    public String code() {
        return "app.business.c";
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
        return param.getName().contains("biz-c");
    }

    /**
     * BusinessC未实现任何扩展点
     */
    @Override
    public List<Class<?>> implementExtensionPoints() {
        return List.of();
    }

    /**
     * BusinessC的优先级
     */
    @Override
    public Integer priority() {
        return 0;
    }

    /**
     * BusinessC未使用任何能力
     */
    @Override
    public List<UsedAbility> usedAbilities() {
        return List.of();
    }
}
