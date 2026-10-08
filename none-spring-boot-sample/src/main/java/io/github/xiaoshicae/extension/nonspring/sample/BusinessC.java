package io.github.xiaoshicae.extension.nonspring.sample;


import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 业务C
 * 业务C没有挂载任何能力，也没有实现任何扩展点，因此所有扩展点均用默认实现
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
    public boolean match(MyParam param) {
        return param.getName().contains("biz-c");
    }
}
