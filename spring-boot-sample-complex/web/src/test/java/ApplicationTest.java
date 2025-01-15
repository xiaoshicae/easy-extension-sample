import io.github.xiaoshicae.extension.sample.complex.ability.FreeTrialAbility;
import io.github.xiaoshicae.extension.sample.complex.ability.LongCloseOrderAbility;
import io.github.xiaoshicae.extension.sample.complex.business.FilmBusiness;
import io.github.xiaoshicae.extension.sample.complex.business.TripBusiness;
import io.github.xiaoshicae.extension.sample.complex.defaultimpl.ExtDefaultImpl;
import io.github.xiaoshicae.extension.sample.complex.dto.OrderDTO;
import io.github.xiaoshicae.extension.sample.complex.extpoint.CalculatePriceExtension;
import io.github.xiaoshicae.extension.sample.complex.extpoint.DelayCloseOrderExtension;
import io.github.xiaoshicae.extension.sample.complex.extpoint.SkipCheckZeroPriceExtension;
import io.github.xiaoshicae.extension.sample.complex.web.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


/**
 * Admin: http://127.0.0.1:8080/my-extension-admin
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
     * <p> GET http://127.0.0.1:8080/api/process?name=unknown
     *
     * <p> 由于未命中任何业务,扩展点均走默认实现。
     *
     * <p> {@link CalculatePriceExtension}的默认实现{@link ExtDefaultImpl#calculatePrice(OrderDTO)}: originPrice * discount
     * <p> {@link DelayCloseOrderExtension}的默认实现{@link ExtDefaultImpl#delayCloseOrderDuration(String)}: 10 minutes
     * <p> {@link SkipCheckZeroPriceExtension}的默认实现{@link ExtDefaultImpl#skipCheckZeroPrice()}: false
     */
    @Test
    public void testCase1() throws Exception {
        String expected = "res: price = 90.000 && close order duration = PT10M && skip check list = [false]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=unknown")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string(expected));
    }

    /**
     * Case2: 请求命中TripBusiness
     *
     * <p> GET http://127.0.0.1:8080/api/process?name=xxx-trip
     *
     * <p> 命中了业务Trip,业务实现了扩展点CalculatePriceExtension。
     *
     * <p> {@link CalculatePriceExtension}业务实现{@link TripBusiness#calculatePrice(OrderDTO)}: originPrice * 0.65
     * <p> {@link DelayCloseOrderExtension}的默认实现{@link ExtDefaultImpl#delayCloseOrderDuration(String)}: 10 minutes
     * <p> {@link SkipCheckZeroPriceExtension}的默认实现{@link ExtDefaultImpl#skipCheckZeroPrice()}: false
     */
    @Test
    public void testCase2() throws Exception {
        String excepted = "res: price = 65.000 && close order duration = PT10M && skip check list = [false]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=xxx-trip")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string(excepted));
    }


    /**
     * Case3: 请求命中FilmBusiness & 所有能力均未生效
     *
     * <p> GET http://127.0.0.1:8080/api/process?name=film
     *
     * <p> 命中了业务FilmBusiness,业务实现了扩展点DelayCloseOrderExtension,业务所挂载的能力均未生效。
     *
     * <p> {@link CalculatePriceExtension}的默认实现{@link ExtDefaultImpl#calculatePrice(OrderDTO)}: 3 minutes
     * <p> {@link DelayCloseOrderExtension}的业务实现{@link FilmBusiness#delayCloseOrderDuration(String)}}: originPrice * 0.65
     * <p> {@link SkipCheckZeroPriceExtension}的默认实现{@link ExtDefaultImpl#skipCheckZeroPrice()}: false
     */
    @Test
    public void testCase3() throws Exception {
        String excepted = "res: price = 90.000 && close order duration = PT3M && skip check list = [false]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=film")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string(excepted));
    }

    /**
     * Case4: 请求命中FilmBusiness & LongCloseOrderAbility能力生效
     *
     * <p> GET http://127.0.0.1:8080/api/process?name=film&value=long-close
     *
     * <p>
     * 命中了业务FilmBusiness，业务实现了扩展点DelayCloseOrderExtension。
     * 业务挂载LongCloseOrderAbility能力生效，能力也实现了扩展点DelayCloseOrderExtension。
     * 由于挂载的能力优先级更高，因此扩展点会执行能力的实现。
     * </p>
     *
     * <p> {@link CalculatePriceExtension}的默认实现{@link ExtDefaultImpl#calculatePrice(OrderDTO)}: originPrice * discount
     * <p> {@link DelayCloseOrderExtension}的业务实现{@link FilmBusiness#delayCloseOrderDuration(String)}}: 3 minutes
     * <p> {@link DelayCloseOrderExtension}的LongCloseOrderAbility能力实现{@link LongCloseOrderAbility#delayCloseOrderDuration(String)}}: 1 hour
     * <p> {@link SkipCheckZeroPriceExtension}的默认实现{@link ExtDefaultImpl#skipCheckZeroPrice()}: false
     */
    @Test
    public void testCase4() throws Exception {
        String excepted = "res: price = 90.000 && close order duration = PT1H && skip check list = [false]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=film&value=long-close")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string(excepted));
    }

    /**
     * Case5: 请求命中FilmBusiness & FreeTrialAbility能力生效 &LongCloseOrderAbility能力生效
     *
     * <p> GET http://127.0.0.1:8080/api/process?name=film&value=long-close::free-trial
     *
     * <p>
     * 命中了业务FilmBusiness，业务实现了扩展点DelayCloseOrderExtension。
     * 业务挂载FreeTrialAbility能力生效，能力实现了扩展点DelayCloseOrderExtension和SkipCheckZeroPriceExtension。
     * 业务挂载LongCloseOrderAbility能力生效，能力实现了扩展点DelayCloseOrderExtension。
     * 由于挂载的FreeTrialAbility能力优先级更高，因此DelayCloseOrderExtension扩展点会执行FreeTrialAbility能力的实现。
     * 由于挂载的FreeTrialAbility能力优先级更高，因此FreeTrialAbility扩展点会执行FreeTrialAbility能力的实现，再执行默认实现。
     * </p>
     *
     * <p> {@link CalculatePriceExtension}的默认实现{@link ExtDefaultImpl#calculatePrice(OrderDTO)}: originPrice * discount
     * <p> {@link DelayCloseOrderExtension}的业务实现{@link FilmBusiness#delayCloseOrderDuration(String)}}: 3 minutes
     * <p> {@link DelayCloseOrderExtension}的LongCloseOrderAbility能力实现{@link LongCloseOrderAbility#delayCloseOrderDuration(String)}}: 1 hour
     * <p> {@link DelayCloseOrderExtension}的FreeTrialAbility能力实现{@link FreeTrialAbility#delayCloseOrderDuration(String)}}: 0
     * <p> {@link SkipCheckZeroPriceExtension}的FreeTrialAbility能力实现{@link FreeTrialAbility#skipCheckZeroPrice()}: true
     * <p> {@link SkipCheckZeroPriceExtension}的默认实现{@link ExtDefaultImpl#skipCheckZeroPrice()}: false
     */
    @Test
    public void testCase5() throws Exception {
        String excepted = "res: price = 90.000 && close order duration = PT0S && skip check list = [true, false]";

        mockMvc.perform(MockMvcRequestBuilders.get(path + "?name=film&value=long-close::free-trial")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string(excepted));
    }

    /**
     * Case6: scope测试
     *
     * <p> GET http://127.0.0.1:8080/api/process-with-invoker?name=film&scope=xxx&scopedName=xxx-trip
     *
     * <p> 命中了业务FilmBusiness，scope命中xxx-trip
     */
    @Test
    public void testCase6() throws Exception {
        String excepted = "res: price1 = 90.000 && price2 = 90.000 && price3 = 65.000";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/process-with-invoker?name=film&scope=xxx&scopedName=xxx-trip")).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string(excepted));
    }
}
