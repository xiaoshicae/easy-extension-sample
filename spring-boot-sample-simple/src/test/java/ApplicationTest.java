import io.github.xiaoshicae.extension.spring.sample.complex.simple.AbilityX;
import io.github.xiaoshicae.extension.spring.sample.complex.simple.Application;
import io.github.xiaoshicae.extension.spring.sample.complex.simple.BusinessA;
import io.github.xiaoshicae.extension.spring.sample.complex.simple.BusinessB;
import io.github.xiaoshicae.extension.spring.sample.complex.simple.BusinessC;
import io.github.xiaoshicae.extension.spring.sample.complex.simple.Ext1;
import io.github.xiaoshicae.extension.spring.sample.complex.simple.Ext2;
import io.github.xiaoshicae.extension.spring.sample.complex.simple.Ext3;
import io.github.xiaoshicae.extension.spring.sample.complex.simple.ExtDefaultImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 业务，能力及扩展点情况实现概览
 * <p> ✅表示实现了该扩展点，❌表示没有实现该扩展点
 * <pre>
 * |扩展点实现       | 生效条件          |Ext1|Ext2|Ext3|
 * |---------------|------------------|----|----|----|
 * |{@link AbilityX}      |name包含"ability-x"| ✅ | ❌ | ❌ |
 * |{@link BusinessA}     |name包含"biz-a"    | ✅ | ❌ | ❌ |
 * |{@link BusinessB}     |name包含"biz-b"    | ✅ | ❌ | ✅ |
 * |{@link BusinessC}     |name包含"biz-c"    | ❌ | ❌ | ❌ |
 * |{@link ExtDefaultImpl}|作为兜底，始终true   | ✅ | ✅ | ✅ |
 * </pre>
 */

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    private final String path = "/api/process";

    /**
     * Case1: 未命中任何业务
     *
     * <p>URL: http://localhost:8080/api/process?name=unknown
     *
     * <p> 由于未命中任何业务,扩展点均走默认实现
     * <p> {@link Ext1}的默认实现 {@link ExtDefaultImpl#doSomething1()}: "Default doSomething1"
     * <p> {@link Ext2}的默认实现 {@link ExtDefaultImpl#doSomething2()}: "Default doSomething2"
     * <p> {@link Ext3}的默认实现 {@link ExtDefaultImpl#doSomething3()}: "Default doSomething3"
     */
    @Test
    public void testCase1() throws Exception {
        String expected = "res: ext1 = Default doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=unknown")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Case2: 请求命中BusinessC
     *
     * <p>URL: http://localhost:8080/api/process?name=biz-c
     *
     * <p> 命中了业务C,但是业务C未实现任何扩展点也没挂载任何能力,扩展点均走默认实现
     * <p> {@link Ext1}的默认实现 {@link ExtDefaultImpl#doSomething1()}: "Default doSomething1"
     * <p> {@link Ext2}的默认实现 {@link ExtDefaultImpl#doSomething2()}: "Default doSomething2"
     * <p> {@link Ext3}的默认实现 {@link ExtDefaultImpl#doSomething3()}: "Default doSomething3"
     */
    @Test
    public void testCase2() throws Exception {
        String expected = "res: ext1 = Default doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=biz-c")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(expected));
    }


    /**
     * Case3: 请求命中BusinessB
     *
     * <p>URL: http://localhost:8080/api/process?name=biz-b
     *
     * <p> 命中了业务B,业务B实现了扩展点1和3
     * <p> {@link Ext1}的BusinessB实现 {@link BusinessB#doSomething1()}: "Default doSomething1"
     * <p> {@link Ext2}的默认实现 {@link ExtDefaultImpl#doSomething2()}: "Default doSomething2"
     * <p> {@link Ext3}的业务B实现 {@link BusinessB#doSomething3()}:     "BusinessB doSomething3"
     * <p> {@link Ext3}的默认实现 {@link ExtDefaultImpl#doSomething3()}: "Default doSomething3"
     */
    @Test
    public void testCase3() throws Exception {
        String expected = "res: ext1 = BusinessB doSomething1, ext2 = Default doSomething2, ext3List = [BusinessB doSomething3, Default doSomething3]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=biz-b")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Case4: 请求命中{@link BusinessA} & {@link AbilityX}未生效
     *
     * <p>URL: http://localhost:8080/api/process?name=biz-a
     *
     * <p> 命中了业务A,业务A实现了扩展点1
     * <p> {@link Ext1}的业务A实现 {@link BusinessA#doSomething1()}:     "BusinessA doSomething1"
     * <p> {@link Ext2}的默认实现 {@link ExtDefaultImpl#doSomething2()}: "Default doSomething2"
     * <p> {@link Ext3}的默认实现 {@link ExtDefaultImpl#doSomething3()}: "Default doSomething3"
     */
    @Test
    public void testCase4() throws Exception {
        String expected = "res: ext1 = BusinessA doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=biz-a")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Case5: 请求命中{@link BusinessA} & {@link AbilityX}生效
     *
     * <p>URL: http://localhost:8080/api/process?name=biz-a::ability-x
     *
     * <p> 命中了业务A,且能力X生效,业务A实现了扩展点1,能力X实现扩展点2
     * <p> {@link Ext1}的业务A实现 {@link BusinessA#doSomething1()}:     "BusinessA doSomething1"
     * <p> {@link Ext2}的能力X实现 {@link AbilityX#doSomething2()}:      "AbilityX doSomething2"
     * <p> {@link Ext3}的默认实现 {@link ExtDefaultImpl#doSomething3()}: "Default doSomething3"
     */
    @Test
    public void testCase5() throws Exception {
        String expected = "res: ext1 = BusinessA doSomething1, ext2 = AbilityX doSomething2, ext3List = [Default doSomething3]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=biz-a::ability-x")).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andExpect(MockMvcResultMatchers.content().string(expected));
    }
}
