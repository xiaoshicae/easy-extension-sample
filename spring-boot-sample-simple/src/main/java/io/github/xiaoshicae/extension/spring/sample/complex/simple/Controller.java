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

    /**
     * 系统提供的扩展点1
     * 注解@ExtensionInject会注入扩展点1的动态代理
     * 运行时会根据匹配到的业务及使用的能力，选择有最高优先级的生效的扩展点实现
     * 如果业务及使用的能力都没有实现该扩展点，则会走默认实现进行兜底
     */
    @ExtensionInject
    private Ext1 ext1;

    /**
     * 系统提供的扩展点2
     */
    @ExtensionInject
    private Ext2 ext2;

    /**
     * 系统提供的扩展点3
     * 注解@ExtensionInject会注入List<Extension>的动态代理，包含所有生效的实现
     * 运行时会根据匹配到的业务及使用的能力，按照优先级依次包含生效的扩展实现
     * List当然也包含扩展点的默认实现
     */
    @ExtensionInject
    private List<Ext3> ext3List;


    @RequestMapping("/process")
    public String process() {
        String s1 = ext1.doSomething1(); // 执行扩展点1，具体用哪个实现，由匹配到的业务及生效的能力+优先级决定
        String s2 = ext2.doSomething2(); // 执行扩展点2，具体用哪个实现，由匹配到的业务及生效的能力+优先级决定

        List<String> s3List = new ArrayList<>();
        for (Ext3 ext3 : ext3List) {
            s3List.add(ext3.doSomething3()); // 按优先级从高到低，依次执行扩展点3的业务或生效能力的实现
        }
        return String.format("res: ext1 = %s, ext2 = %s, ext3List = %s", s1, s2, Arrays.toString(s3List.toArray()));
    }
}
