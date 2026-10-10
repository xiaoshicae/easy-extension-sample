package io.github.xiaoshicae.extension.nonspring.sample;

import io.github.xiaoshicae.extension.core.exception.ResolutionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 非 Spring 场景测试用例，与 Application#main 的 5 个 Case 一一对应
 *
 * | 请求参数            | 命中业务   | 生效能力  | 特点                                   |
 * |------------------|-----------|---------|--------------------------------------|
 * | unknown          | 无         | 无       | 非严格模式，全部走默认实现                    |
 * | biz-c            | BusinessC | 无       | 业务未实现扩展点，全部走默认实现                 |
 * | biz-b            | BusinessB | 无       | 业务实现 Ext1、Ext3                       |
 * | biz-a            | BusinessA | 无       | 未带 ability-x，能力不生效                   |
 * | biz-a::ability-x | BusinessA | AbilityX | 能力实现 Ext2                            |
 * | biz-a,biz-b      | 两个业务     | -       | 5.0 起多个业务同时匹配，任何模式下都报错            |
 */
public class ApplicationTest {

    private final Application application = new Application();

    @Test
    public void testCase1() {
        assertEquals("res: ext1 = Default doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]",
                application.process("unknown"));
    }

    @Test
    public void testCase2() {
        assertEquals("res: ext1 = Default doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]",
                application.process("biz-c"));
    }

    @Test
    public void testCase3() {
        assertEquals("res: ext1 = BusinessB doSomething1, ext2 = Default doSomething2, ext3List = [BusinessB doSomething3, Default doSomething3]",
                application.process("biz-b"));
    }

    @Test
    public void testCase4() {
        assertEquals("res: ext1 = BusinessA doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]",
                application.process("biz-a"));
    }

    @Test
    public void testCase5() {
        assertEquals("res: ext1 = BusinessA doSomething1, ext2 = AbilityX doSomething2, ext3List = [Default doSomething3]",
                application.process("biz-a::ability-x"));
    }

    /**
     * 5.0 起多个业务同时匹配一律报错(非严格模式也一样)，异常信息里按注册顺序列出匹配到的业务 code
     */
    @Test
    public void testSeveralMatchingBusinessesIsAnError() {
        ResolutionException e = assertThrows(ResolutionException.class, () -> application.process("biz-a,biz-b"));
        assertEquals(ResolutionException.Reason.MULTIPLE_BUSINESSES_MATCHED, e.reason());
        assertEquals("multiple business found, matched business codes: [app.business.a, app.business.b]", e.getMessage());
    }
}
