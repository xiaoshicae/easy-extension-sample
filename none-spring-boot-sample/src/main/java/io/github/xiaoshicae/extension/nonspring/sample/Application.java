package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.ExtensionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Application {

    public static void main(String[] args) {
        Application m = new Application();

        // Case1: 未命中任何业务
        String res = m.process("unknown");
        System.out.println("Case1, param=unknown, " + res);

        // Case2: 请求命中BusinessC
        res = m.process("biz-c");
        System.out.println("Case2, param=biz-c, " + res);

        // Case3: 请求命中BusinessB
        res = m.process("biz-b");
        System.out.println("Case3, param=biz-b, " + res);

        // Case4: 请求命中BusinessA & AbilityX未生效
        res = m.process("biz-a");
        System.out.println("Case4, param=biz-a, " + res);

        // Case5: 请求命中BusinessA & AbilityX生效
        res = m.process("biz-a::ability-x");
        System.out.println("Case5, param=biz-a::ability-x, " + res);
    }

    private final ExtensionContext<MyParam> extContext = buildExtensionContext();

    public String process(String param) {
        // 以 param 对应的业务执行 doProcess：绑定、执行、解绑一步完成(4.1)
        // 4.0 写法：try (Binding binding = extContext.bind(new MyParam(param))) { return doProcess(); }
        return extContext.callWith(new MyParam(param), this::doProcess);
    }

    private String doProcess() {
        // 执行扩展点1，具体用哪个实现，由匹配到的业务及生效的能力+优先级决定
        Ext1 ext1 = extContext.first(Ext1.class);
        String s1 = ext1.doSomething1();

        // 执行扩展点2，具体用哪个实现，由匹配到的业务及生效的能力+优先级决定
        Ext2 ext2 = extContext.first(Ext2.class);
        String s2 = ext2.doSomething2();

        // 按优先级从高到低，依次执行扩展点3的业务或生效能力的实现(最后是默认实现)
        List<String> s3List = new ArrayList<>();
        for (Ext3 ext3 : extContext.all(Ext3.class)) {
            s3List.add(ext3.doSomething3());
        }
        return String.format("res: ext1 = %s, ext2 = %s, ext3List = %s", s1, s2, Arrays.toString(s3List.toArray()));
    }

    private static ExtensionContext<MyParam> buildExtensionContext() {
        // build() 时一次性校验全部装配(注册顺序不影响结果)，构建后不可变
        return ExtensionContext.<MyParam>builder()
                // 扩展点
                .extensionPoint(Ext1.class, Ext2.class, Ext3.class)
                // 扩展点默认实现
                .defaultImplementation(new ExtDefaultImpl())
                // 能力
                .ability(new AbilityX())
                // 业务
                .business(new BusinessA())
                .business(new BusinessB())
                .business(new BusinessC())
                // 非严格模式：未命中任何业务时，扩展点走默认实现(Case1)；严格模式(缺省)下会抛出ResolutionException
                .strict(false)
                .build();
    }
}
