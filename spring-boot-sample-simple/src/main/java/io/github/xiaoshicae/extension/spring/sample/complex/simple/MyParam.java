package io.github.xiaoshicae.extension.spring.sample.complex.simple;


import io.github.xiaoshicae.extension.core.annotation.MatcherParam;


/**
 * 生效匹配的参数
 * 需要@MatcherParam注解，以便包扫描能识别到
 * 该参数由具体业务自己定义，所有的业务和能力均需要基于该参数进行生效判断
 * 用于业务(BusinessA/BusinessB)生效匹配的参数，
 * 在请求开始是将该参数准备好，传给EasyExtension的ISessionManager进行管理，后续扩展点调用是会遍历判断哪个业务和哪些能力生效
 */
@MatcherParam
public class MyParam {
    private final String name;

    public MyParam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
