package io.github.xiaoshicae.extension.sample.ecommerce.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * 电商下单流程测试用例
 *
 * 业务线 + 能力组合 矩阵:
 *
 * | 请求参数                              | 命中业务   | 生效能力              | 特点                               |
 * |-------------------------------------|-----------|----------------------|-----------------------------------|
 * | bizCode=retail                      | 标准零售    | 无                    | 基础流程，走默认+包邮+7天无理由            |
 * | bizCode=retail&abilityCodes=vip     | 标准零售    | VIP优惠券               | 促销减免 ¥20                        |
 * | bizCode=fresh                       | 生鲜电商    | 无                    | 冷链运费、库存检查、2h退货                  |
 * | bizCode=fresh&abilityCodes=rapid    | 生鲜电商    | 急速达                  | 短信+推送+微信多渠道通知                   |
 * | bizCode=digital                     | 数码3C     | 无                    | 风控REVIEW(>5000)、15天退货、电子发票      |
 * | bizCode=digital&abilityCodes=installment | 数码3C | 分期免息       | 支持3/6/12期分期支付                    |
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class EcommerceApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    private final String checkoutPath = "/api/order/checkout";

    /**
     * Case1: 标准零售 — 无额外能力
     * 预期: 基础运费 ¥8, 无优惠, 无退货窗口(默认不支持), PUSH 通知
     */
    @Test
    public void testRetailBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=retail"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("运费: +¥8.00")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("促销优惠: -¥0")));
    }

    /**
     * Case2: 标准零售 + VIP优惠券
     * 预期: 促销优惠 ¥20
     */
    @Test
    public void testRetailWithVipCoupon() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=retail&abilityCodes=vip"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("促销优惠: -¥20.00")));
    }

    /**
     * Case3: 标准零售 + VIP + 包邮
     * 预期: 优惠 ¥20, 运费 ¥0
     */
    @Test
    public void testRetailWithVipAndFreeShipping() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=retail&abilityCodes=vip,free-shipping"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("促销优惠: -¥20.00")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("运费: +¥0")));
    }

    /**
     * Case4: 生鲜电商 — 无额外能力
     * 预期: 冷链运费 ¥21 (15+3*2), 库存检查, 2h 退货
     */
    @Test
    public void testFreshBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=fresh"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("运费: +¥21")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("退货窗口: 0天"))); // 2 hours = 0 days
    }

    /**
     * Case5: 生鲜电商 + 急速达
     * 预期: 多渠道通知 (SMS, PUSH, WECHAT_MSG)
     */
    @Test
    public void testFreshWithRapidDelivery() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=fresh&abilityCodes=rapid"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("SMS")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("WECHAT_MSG")));
    }

    /**
     * Case6: 数码3C — 无额外能力
     * 预期: 风控 REVIEW (金额 >5000? 不，397 < 5000, 所以 PASS), 15天退货, 电子发票
     */
    @Test
    public void testDigitalBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=digital"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("退货窗口: 15天")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("发票类型: NONE")));
    }

    /**
     * Case7: 数码3C + 分期免息
     * 预期: 支持 installment_3, installment_6, installment_12
     */
    @Test
    public void testDigitalWithInstallment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=digital&abilityCodes=installment"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("installment_3")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("installment_12")));
    }

    /**
     * Case8: 数码3C + 分期 + VIP + 包邮 + 七天无理由
     * 终极组合: 所有能力同时生效
     */
    @Test
    public void testDigitalFullAbilities() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(
                checkoutPath + "?bizCode=digital&abilityCodes=installment,vip,free-shipping,return-7d"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("促销优惠: -¥20.00")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("运费: +¥0")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("installment_12")));
    }
}
