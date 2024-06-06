package io.github.xiaoshicae.extension.spring.sample.complex.simple;

// 用于业务或者能力匹配的参数，通过session传递给业务和能力进行匹配
public class MyParam {
    private final String name;

    public MyParam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
