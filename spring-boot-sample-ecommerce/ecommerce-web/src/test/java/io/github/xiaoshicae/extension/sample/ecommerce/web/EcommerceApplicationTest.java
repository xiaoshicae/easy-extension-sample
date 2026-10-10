package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.core.ExtensionContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.FreightCalcExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
 * | bizCode=fresh&abilityCodes=free-shipping | 生鲜电商 | 包邮          | 能力排在业务自身(Self)之前，覆盖冷链运费      |
 * | bizCode=digital&abilityCodes=return-7d   | 数码3C  | 七天无理由      | 能力排在业务自身(Self)之前，覆盖15天退货      |
 * | notify-async: bizCode=fresh&abilityCodes=rapid | 生鲜电商 | 急速达    | @Async 线程沿用请求的业务绑定             |
 * | context.callWith(fresh)                   | 生鲜电商  | 无             | 非 HTTP 入口绑定业务                     |
 * | bizCode=unknown&abilityCodes=vip,free-shipping | 无 | 无       | allow-unknown-business=true，全部走默认实现  |
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class EcommerceApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExtensionContext<OrderMatchParam> context;

    private final String checkoutPath = "/api/order/checkout";

    /**
     * 未知业务: 没有业务匹配，allow-unknown-business=true 时所有扩展点走默认实现
     * 能力挂在业务上，没有业务时请求里带的能力也不生效(对比 testRetailWithVipAndFreeShipping)
     */
    @Test
    public void testUnknownBusinessFallsBackToDefaults() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=unknown&abilityCodes=vip,free-shipping"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("促销优惠: -¥0\n")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("运费: +¥8.00")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("支付方式: [alipay, wechat]")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("退货窗口: 不支持")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("通知渠道: [PUSH]")));
    }

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

    /**
     * Case9: 生鲜电商 + 包邮 — 能力与业务实现了同一个扩展点(运费计算)
     * FreshBusiness 的 abilities = {FreeShippingAbility, RapidDeliveryAbility, Self}，包邮排在业务自身之前
     * 预期: 包邮能力覆盖业务自身的冷链运费(¥21)，运费 ¥0
     */
    @Test
    public void testFreshWithFreeShippingOverridesBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=fresh&abilityCodes=free-shipping"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("运费: +¥0\n")));
    }

    /**
     * Case10: 数码3C + 七天无理由 — 能力与业务实现了同一个扩展点(售后策略)
     * DigitalBusiness 的 abilities = {Return7DaysAbility, ..., Self}，七天无理由排在业务自身之前
     * 预期: 七天无理由能力覆盖业务自身的15天退货，退货窗口 7天
     */
    @Test
    public void testDigitalWithReturn7DaysOverridesBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + "?bizCode=digital&abilityCodes=return-7d"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("退货窗口: 7天")));
    }

    /**
     * Case11: 异步线程沿用请求的业务绑定 — easy-extension.async-propagation=true
     * 通知渠道在 @Async 线程池里计算，生鲜 + 急速达仍选中急速达能力的实现
     * 预期: 与同步调用一致，包含短信和微信消息
     */
    @Test
    public void testAsyncNotifyKeepsBusinessBinding() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/notify-async?bizCode=fresh&abilityCodes=rapid"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("SMS")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("WECHAT_MSG")));
    }

    /**
     * Case12: HTTP 之外的入口(消息消费、定时任务、RPC)用 callWith 绑定业务
     * 预期: 以生鲜业务执行，运费为冷链运费 15 + 3 件 x 2 = ¥21
     */
    @Test
    public void testCallWithOutsideHttp() {
        BigDecimal freight = context.callWith(new OrderMatchParam("fresh", List.of()),
                () -> context.first(FreightCalcExtension.class).calcFreight(MockOrders.sample()));
        assertEquals(0, new BigDecimal("21").compareTo(freight));
    }
}
