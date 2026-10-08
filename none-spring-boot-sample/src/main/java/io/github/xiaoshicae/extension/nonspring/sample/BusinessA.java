package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.annotation.Self;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 业务A 实现了扩展点1，业务挂载了能力X，即继承了能力X的扩展点实现。
 * code表示业务的唯一id (即业务身份)
 * abilities的数组顺序即优先级：Self.class表示业务自身的位置，这里业务自身优先于能力X
 */
@Business(code = "app.business.a", abilities = {Self.class, AbilityX.class})
public class BusinessA implements Matcher<MyParam>, Ext1 {

    /**
     * 业务命中判断
     * 业务必须实现Matcher<XXX>
     *
     * @param param for match predict
     * @return 业务是否命中
     */
    @Override
    public boolean match(MyParam param) {
        return param.getName().contains("biz-a");
    }

    /**
     * 扩展点1的BusinessA自定义实现
     */
    @Override
    public String doSomething1() {
        return "BusinessA doSomething1";
    }
}
