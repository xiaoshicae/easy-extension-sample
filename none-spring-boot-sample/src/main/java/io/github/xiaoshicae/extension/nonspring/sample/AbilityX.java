package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.ability.AbstractAbility;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;

import java.util.List;


/**
 * 能力X
 * 实现了扩展点2
 */
public class AbilityX extends AbstractAbility<MyParam> implements Ext2 {

    /**
     * code表示能力唯一id
     */
    @Override
    public String code() {
        return "app.ability.x";
    }

    /**
     * 能力生效判断
     * 能力必须实现Matcher<XXX>
     *
     * @param param for match predict
     * @return 能力是否生效
     */
    @Override
    public Boolean match(MyParam param) {
        return param.getName().contains("ability-x");
    }

    /**
     * AbilityX实现了扩展点2
     */
    @Override
    public List<Class<?>> implementExtensionPoints() {
        return List.of(Ext2.class);
    }

    /**
     * 扩展点2的AbilityX自定义实现
     */
    @Override
    public String doSomething2() {
        return "AbilityX doSomething2";
    }
}
