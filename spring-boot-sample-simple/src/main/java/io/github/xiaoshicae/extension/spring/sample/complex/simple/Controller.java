package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionInject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class Controller {
    // 动态注入扩展点1，不同业务会有不同实现
    @ExtensionInject
    private Ext1 ext1;

    // 动态注入扩展点2，不同业务会有不同实现
    @ExtensionInject
    private Ext2 ext2;

    // 动态注入扩展点3，不同业务会有不同实现
    @ExtensionInject
    private Ext3 ext3;

    @RequestMapping("/process")
    public String process() {
        String s1 = ext1.doSomething1();
        String s2 = ext2.doSomething2();
        String s3 = ext3.doSomething3();
        return String.format("ext1 = %s, ext2 = %s, ext3 = %s", s1, s2, s3);
    }
}
