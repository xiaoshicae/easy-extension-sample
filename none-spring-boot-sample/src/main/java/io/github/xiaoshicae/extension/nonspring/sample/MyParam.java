package io.github.xiaoshicae.extension.nonspring.sample;


/**
 * 生效匹配的参数
 * 该参数由具体业务自己定义，所有的业务和能力均需要基于该参数进行生效判断
 * 用于业务(BusinessA/BusinessB)生效匹配的参数，
 * 在请求开始是将该参数准备好，传给EasyExtension的ISessionManager进行管理，后续扩展点调用是会遍历判断哪个业务和哪些能力生效
 */
public class MyParam {
    private final String name;

    public MyParam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
