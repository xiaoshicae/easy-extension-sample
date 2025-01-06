package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.common.Matcher;

/**
 * 业务A
 * 实现了扩展点1
 * 需要@Business注解，以便包扫描能识别到；code表示能力的唯一id；abilities表示业务挂载的能力
 * 业务挂载了能力，即继承了能力的扩展点实现
 */
@Business(code = "xxx.biz.a", abilities = "app.ability.x")
public class BusinessA implements Matcher<MyParam>, Ext1 {

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
     * 扩展点1的BusinessA自定义实现
     */
    @Override
    public String doSomething1() {
        return "BusinessA doSomething1";
    }
}
