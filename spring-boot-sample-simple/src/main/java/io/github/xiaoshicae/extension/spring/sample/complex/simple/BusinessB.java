package io.github.xiaoshicae.extension.spring.sample.complex.simple;


import io.github.xiaoshicae.extension.core.annotation.Business;
import io.github.xiaoshicae.extension.core.interfaces.Matcher;

/**
 * 业务B
 * 实现了扩展点1和扩展点3
 * 需要@Business注解，以便包扫描能识别到；code表示能力的唯一id
 * 业务B没有挂载任何能力
 */
@Business(code = "app.business.b")
public class BusinessB implements Matcher<MyParam>, Ext1, Ext3 {

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
}
