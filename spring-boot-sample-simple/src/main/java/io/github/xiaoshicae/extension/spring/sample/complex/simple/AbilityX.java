package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.annotation.Ability;
import io.github.xiaoshicae.extension.core.common.Matcher;


/**
 * 能力X 实现了扩展点2
 * <br>需要@Ability注解，以便包扫描能识别到；code表示能力的唯一id
 */
@Ability(code = "app.ability.x")
public class AbilityX implements Matcher<MyParam>, Ext2 {

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
     * 扩展点2的AbilityX自定义实现
     */
    @Override
    public String doSomething2() {
        return "AbilityX doSomething2";
    }
}
