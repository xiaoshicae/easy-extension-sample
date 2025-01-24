package io.github.xiaoshicae.extension.spring.sample.complex.simple;


import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 业务C
 * 需要@Business注解，以便包扫描能识别到
 * 业务C没有挂载任何能力，也没有实现任何扩展点，因此所哟扩展点均用系统默认实现
 */
@Business(code = "app.business.c")
public class BusinessC implements Matcher<MyParam> {

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
}
