package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionInject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api")
public class Controller {
    // 动态注入扩展点1，不同业务和能力会有不同实现
    // 会根据匹配到的业务和挂载的扩展点，注入优先级最高的实现
    // 未匹配到任何业务和能力的实现，会走默认能力进行兜底
    @ExtensionInject
    private Ext1 ext1;

    // 动态注入扩展点2，不同业务会和能力有不同实现
    // 会根据匹配到的业务和挂载的扩展点，注入优先级最高的实现
    // 未匹配到任何业务和能力的实现，会走默认能力进行兜底
    @ExtensionInject
    private Ext2 ext2;

    // 动态注入扩展点3，不同业务会和能力有不同实现
    // 会根据匹配到的业务和挂载的扩展点，注入所有匹配到的实现
    // 包括默认能力
    @ExtensionInject
    private List<Ext3> ext3List;

    @RequestMapping("/process")
    public String process() {
        String s1 = ext1.doSomething1();
        String s2 = ext2.doSomething2();
        List<String> s3List = new ArrayList<>();
        for (Ext3 ext3 : ext3List) {
            s3List.add(ext3.doSomething3());
        }
        return String.format("res: ext1 = %s, ext2 = %s, ext3List = %s", s1, s2, Arrays.toString(s3List.toArray()));
    }
}
