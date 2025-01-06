package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.DefaultExtensionContext;
import io.github.xiaoshicae.extension.core.ExtensionContextRegisterHelper;
import io.github.xiaoshicae.extension.core.IExtensionContext;
import io.github.xiaoshicae.extension.core.exception.ExtensionException;
import io.github.xiaoshicae.extension.core.exception.QueryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Application {

    public static void main(String[] args) throws Exception {
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
        System.out.println("Case3, param=biz-a, " + res);

        // Case5: 请求命中BusinessA & AbilityX生效
        res = m.process("biz-a::ability-x");
        System.out.println("Case3, param=biz-a::ability-x, " + res);
    }

    private final IExtensionContext<MyParam> extContext = new DefaultExtensionContext<>();

    public Application() throws Exception {
        this.registerBusinessAndAbility(extContext);
    }

    public String process(String param) throws ExtensionException {
        try {
            beforeProcess(param);
            return doProcess();
        } finally {
            afterProcess();
        }
    }

    private String doProcess() throws QueryException {
        // 执行扩展点1，具体用哪个实现，由匹配到的业务及生效的能力+优先级决定
        Ext1 ext1 = extContext.getFirstMatchedExtension(Ext1.class);
        String s1 = ext1.doSomething1();

        // 执行扩展点2，具体用哪个实现，由匹配到的业务及生效的能力+优先级决定
        Ext2 ext2 = extContext.getFirstMatchedExtension(Ext2.class);
        String s2 = ext2.doSomething2();

        // 按优先级从高到低，依次执行扩展点3的业务或生效能力的实现
        List<String> s3List = new ArrayList<>();
        List<Ext3> ext3List = extContext.getAllMatchedExtension(Ext3.class);
        for (Ext3 ext3 : ext3List) {
            s3List.add(ext3.doSomething3());
        }
        return String.format("res: ext1 = %s, ext2 = %s, ext3List = %s", s1, s2, Arrays.toString(s3List.toArray()));
    }


    private void beforeProcess(String param) throws ExtensionException {
        extContext.initSession(new MyParam(param));
    }

    private void afterProcess() throws ExtensionException {
        extContext.removeSession();
    }

    private void registerBusinessAndAbility(IExtensionContext<MyParam> register) throws ExtensionException {
        // 册helper工具(便于注册不受顺序影响)
        ExtensionContextRegisterHelper<MyParam> helper = new ExtensionContextRegisterHelper<>();

        // 收集扩展点
        helper.addExtensionPointClasses(Ext1.class, Ext2.class, Ext3.class);

        // 收集匹配参数class
        helper.setMatcherParamClass(MyParam.class);

        // 收集扩展点默认实现
        helper.setExtensionPointDefaultImplementation(new ExtDefaultImpl());

        // 收集能力
        helper.addAbilities(new AbilityX());

        // 收集业务
        helper.addBusinesses(new BusinessA(), new BusinessB(), new BusinessC());

        // 执行注册
        helper.doRegister(register);
    }
}
