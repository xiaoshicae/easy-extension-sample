package io.github.xiaoshicae.extension.sample.ecommerce.web;

import io.github.xiaoshicae.extension.core.ExtensionContext;
import io.github.xiaoshicae.extension.sample.ecommerce.extpoint.FreightCalcExtension;
import io.github.xiaoshicae.extension.sample.ecommerce.matchparam.OrderMatchParam;
import org.hamcrest.Matchers;
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
 * 请求参数既用于构造匹配参数，也作为扩展点入参，默认值见 {@link OrderParams}:
 * bizCode=retail, memberLevel=NORMAL, amount=397.00, itemCount=3, province=广东省, urgent=false, categories=general
 *
 * 能力的命中条件(由能力自己的 match() 按订单属性判断):
 *
 * | 能力        | 命中条件                                     |
 * |------------|--------------------------------------------|
 * | 包邮        | SVIP 会员，或 金额 ≥ ¥500 且收货地非偏远省份      |
 * | VIP优惠券   | memberLevel 是 VIP / SVIP                   |
 * | 急速达      | 勾选加急 且 省份已开通 且 件数 ≤ 10              |
 * | 七天无理由   | 不含 fresh/custom 品类 且 金额 < ¥3000         |
 * | 分期免息    | 金额 ≥ ¥3000                                |
 *
 * 业务线 + 能力组合 矩阵:
 *
 * | 请求参数                                             | 命中业务 | 生效能力        | 验证点                      |
 * |----------------------------------------------------|--------|---------------|----------------------------|
 * | bizCode=retail                                     | 标准零售 | 七天无理由      | 基础运费 ¥8、无优惠、7天退货    |
 * | bizCode=retail&memberLevel=VIP                     | 标准零售 | VIP券+七天无理由 | 促销减免 ¥20                |
 * | bizCode=retail&memberLevel=VIP&amount=699          | 标准零售 | VIP券+包邮      | 满500: 减 ¥50 且包邮         |
 * | bizCode=retail&amount=699&province=新疆             | 标准零售 | 七天无理由      | 偏远地区不包邮，运费 ¥8        |
 * | bizCode=retail&memberLevel=SVIP                    | 标准零售 | VIP券+包邮      | SVIP 无门槛包邮              |
 * | bizCode=retail&categories=custom                   | 标准零售 | 无             | 定制商品不支持无理由退货        |
 * | bizCode=fresh&categories=fresh                     | 生鲜电商 | 无             | 冷链运费 ¥21、2小时退货        |
 * | bizCode=fresh&categories=fresh&province=湖南省      | 生鲜电商 | 无             | 跨省冷链 ¥31                 |
 * | bizCode=fresh&categories=fresh&urgent=true         | 生鲜电商 | 急速达          | 多渠道通知 + 加急费 ¥12        |
 * | bizCode=fresh&categories=fresh&amount=699          | 生鲜电商 | 包邮           | 能力排在 Self 前，覆盖冷链运费   |
 * | bizCode=fresh&categories=fresh&itemCount=60        | 生鲜电商 | 无             | 生鲜限购 50 件                |
 * | bizCode=fresh&categories=fresh&itemCount=50        | 生鲜电商 | 无             | 单 SKU 超 20 件库存紧张        |
 * | bizCode=digital&categories=digital                 | 数码3C  | 七天无理由       | 小额走通用能力的 7 天退货       |
 * | bizCode=digital&categories=digital&amount=6999     | 数码3C  | 分期+包邮        | 分期支付；大额回落业务自身15天延保 |
 * | bizCode=digital&categories=digital&amount=12000    | 数码3C  | 分期+包邮        | 分期能力风控 REVIEW           |
 * | bizCode=digital&categories=digital&userId=NEW-001  | 数码3C  | 七天无理由       | 业务自身风控: 新用户 REVIEW     |
 * | bizCode=digital&needInvoice=true&invoiceTarget=... | 数码3C  | 七天无理由       | 企业专票 / 个人电子发票         |
 * | notify-async: bizCode=fresh&urgent=true            | 生鲜电商 | 急速达          | @Async 线程沿用请求的业务绑定   |
 * | context.callWith(fresh)                            | 生鲜电商 | 无             | 非 HTTP 入口绑定业务           |
 * | bizCode=unknown&memberLevel=SVIP&amount=699        | 无      | 无             | allow-unknown-business，全默认 |
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class EcommerceApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExtensionContext<OrderMatchParam> context;

    private final String checkoutPath = "/api/order/checkout";

    private void checkout(String query, String... expectContains) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post(checkoutPath + query))
                .andExpect(MockMvcResultMatchers.status().isOk());
        for (String expect : expectContains) {
            result.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(expect)));
        }
    }

    /**
     * 未知业务: 没有业务匹配，allow-unknown-business=true 时所有扩展点走默认实现
     * 能力挂在业务上，没有业务时即使订单属性满足能力的命中条件也不生效(对比 testRetailSvipFreeShipping)
     */
    @Test
    public void testUnknownBusinessFallsBackToDefaults() throws Exception {
        checkout("?bizCode=unknown&memberLevel=SVIP&amount=699",
                "促销优惠: -¥0\n", "运费: +¥8.00", "支付方式: [alipay, wechat]",
                "退货窗口: 不支持", "通知渠道: [PUSH]");
    }

    /**
     * Case1: 标准零售 — 金额未满包邮门槛、非会员
     * 预期: 基础运费 ¥8, 无优惠, 命中七天无理由能力
     */
    @Test
    public void testRetailBasic() throws Exception {
        checkout("?bizCode=retail",
                "运费: +¥8.00", "促销优惠: -¥0", "退货窗口: 7天", "通知渠道: [PUSH]");
    }

    /**
     * Case2: 标准零售 + VIP 会员 — 会员等级命中 VIP 优惠券能力
     * 预期: 促销优惠 ¥20 (未满 500)
     */
    @Test
    public void testRetailWithVipCoupon() throws Exception {
        checkout("?bizCode=retail&memberLevel=VIP", "促销优惠: -¥20.00");
    }

    /**
     * Case3: 标准零售 + VIP + 满 ¥500 — 同时命中 VIP 券和包邮能力
     * 预期: 满 500 减 ¥50, 运费 ¥0
     */
    @Test
    public void testRetailVipOverFreeShippingThreshold() throws Exception {
        checkout("?bizCode=retail&memberLevel=VIP&amount=699",
                "促销优惠: -¥50.00", "运费: +¥0\n");
    }

    /**
     * Case4: 标准零售 + 满 ¥500 但收货地偏远 — 包邮能力按省份判断不命中
     * 预期: 运费回落到默认实现的 ¥8
     */
    @Test
    public void testRetailRemoteProvinceNoFreeShipping() throws Exception {
        checkout("?bizCode=retail&amount=699&province=新疆", "运费: +¥8.00");
    }

    /**
     * Case5: 标准零售 + SVIP — SVIP 无门槛包邮
     * 预期: 金额未满 500 也包邮, 并命中 VIP 券
     */
    @Test
    public void testRetailSvipFreeShipping() throws Exception {
        checkout("?bizCode=retail&memberLevel=SVIP", "运费: +¥0\n", "促销优惠: -¥20.00");
    }

    /**
     * Case6: 标准零售 + 定制商品 — 品类不满足七天无理由能力的条件
     * 预期: 能力不命中，零售业务自身不实现售后策略，回落默认实现(不支持退货)
     */
    @Test
    public void testRetailCustomCategoryNoReturn() throws Exception {
        checkout("?bizCode=retail&categories=custom", "退货窗口: 不支持");
    }

    /**
     * Case7: 生鲜电商 — 无能力命中
     * 预期: 冷链运费 ¥21 (15+3*2), 库存充足, 2 小时退货
     */
    @Test
    public void testFreshBasic() throws Exception {
        checkout("?bizCode=fresh&categories=fresh",
                "运费: +¥21", "退货窗口: 2小时", "库存状态: {SKU-001=AVAILABLE, SKU-002=AVAILABLE}");
    }

    /**
     * Case8: 生鲜电商 + 跨省 — 业务自身的运费实现用到了收货省份
     * 预期: 冷链运费 ¥21 + 跨省冷链费 ¥10 = ¥31
     */
    @Test
    public void testFreshCrossProvinceFreight() throws Exception {
        checkout("?bizCode=fresh&categories=fresh&province=湖南省", "运费: +¥31");
    }

    /**
     * Case9: 生鲜电商 + 加急 — 急速达能力按"加急+省份+件数"命中
     * 预期: 多渠道通知 (SMS, PUSH, WECHAT_MSG), 加急配送费 ¥8 覆盖冷链运费
     */
    @Test
    public void testFreshWithRapidDelivery() throws Exception {
        checkout("?bizCode=fresh&categories=fresh&urgent=true",
                "通知渠道: [SMS, PUSH, WECHAT_MSG]", "运费: +¥12.00");
    }

    /**
     * Case10: 生鲜电商 + 满 ¥500 — 能力与业务实现了同一个扩展点(运费计算)
     * FreshBusiness 的 abilities = {FreeShippingAbility, RapidDeliveryAbility, Self}，包邮排在业务自身之前
     * 预期: 包邮能力覆盖业务自身的冷链运费(¥21)，运费 ¥0
     */
    @Test
    public void testFreshWithFreeShippingOverridesBusiness() throws Exception {
        checkout("?bizCode=fresh&categories=fresh&amount=699", "运费: +¥0\n");
    }

    /**
     * Case11: 生鲜电商 + 超量下单 — 业务自身的校验规则
     * 预期: 校验失败，流程中断
     */
    @Test
    public void testFreshTooManyItemsRejected() throws Exception {
        checkout("?bizCode=fresh&categories=fresh&itemCount=60", "订单校验失败: 生鲜商品单次购买不能超过50件");
    }

    /**
     * Case12: 生鲜电商 + 单 SKU 大量下单 — 业务自身的库存检查
     * 预期: 件数拆到两个 SKU 各 25 件，均超过 20 件的库存紧张线
     */
    @Test
    public void testFreshLowStock() throws Exception {
        checkout("?bizCode=fresh&categories=fresh&itemCount=50", "库存状态: {SKU-001=LOW_STOCK, SKU-002=LOW_STOCK}");
    }

    /**
     * Case13: 数码3C — 小额订单
     * 预期: 风控 PASS, 不开票, 命中七天无理由能力(金额 < 3000)
     */
    @Test
    public void testDigitalBasic() throws Exception {
        checkout("?bizCode=digital&categories=digital",
                "风控结果: PASS", "发票类型: NONE", "退货窗口: 7天", "支付方式: [alipay, wechat]");
    }

    /**
     * Case14: 数码3C + 大额订单 — 金额同时决定了分期命中、七天无理由不命中
     * 预期: 支持 3/6/12 期分期；售后回落到业务自身的 15 天延保；满 500 包邮
     */
    @Test
    public void testDigitalLargeOrderUsesInstallment() throws Exception {
        checkout("?bizCode=digital&categories=digital&amount=6999",
                "installment_3", "installment_12", "退货窗口: 15天", "运费: +¥0\n",
                "风控结果: PASS", "支付方式数(List注入): 2 个实现");
    }

    /**
     * Case15: 数码3C + 超大额订单 — 分期能力排在业务自身之前，风控由分期能力接管
     * 预期: 超过 ¥10000 的分期订单需人工审核
     */
    @Test
    public void testDigitalHugeOrderNeedsReview() throws Exception {
        checkout("?bizCode=digital&categories=digital&amount=12000", "风控结果: REVIEW");
    }

    /**
     * Case16: 数码3C + 新用户 — 金额不足分期门槛，风控回落到业务自身
     * 预期: 新注册用户人工审核
     */
    @Test
    public void testDigitalNewUserNeedsReview() throws Exception {
        checkout("?bizCode=digital&categories=digital&userId=NEW-001", "风控结果: REVIEW");
    }

    /**
     * Case17: 数码3C + 开票 — 扩展点按开票诉求(是否开票、抬头类型)返回不同发票
     */
    @Test
    public void testDigitalInvoiceType() throws Exception {
        checkout("?bizCode=digital&categories=digital&needInvoice=true&invoiceTarget=company", "发票类型: SPECIAL");
        checkout("?bizCode=digital&categories=digital&needInvoice=true", "发票类型: ELECTRONIC");
    }

    /**
     * Case18: 数码3C + 超出限购 — 业务自身的校验规则
     */
    @Test
    public void testDigitalPurchaseLimit() throws Exception {
        checkout("?bizCode=digital&categories=digital&itemCount=8", "订单校验失败: 数码商品单次购买不能超过5件");
    }

    /**
     * Case19: 售后事件 — 通知策略扩展点按订单事件返回不同渠道
     * 预期: 零售业务下单只推送，售后额外发短信
     */
    @Test
    public void testRetailAfterSaleNotify() throws Exception {
        checkout("?bizCode=retail", "通知渠道: [PUSH]");
        checkout("?bizCode=retail&notifyEvent=AFTER_SALE", "通知渠道: [PUSH, SMS]");
    }

    /**
     * Case20: 同一个新用户，金额决定风控由谁接管
     * 小额时分期能力不命中，走业务自身风控(新用户 REVIEW)；
     * 大额时分期能力命中且排在 Self 之前，风控由分期能力接管(≤¥10000 放行)
     */
    @Test
    public void testInstallmentRiskOverridesBusinessRisk() throws Exception {
        checkout("?bizCode=digital&categories=digital&userId=NEW-001", "风控结果: REVIEW");
        checkout("?bizCode=digital&categories=digital&userId=NEW-001&amount=6999", "风控结果: PASS");
    }

    /**
     * Case21: 异步线程沿用请求的业务绑定 — easy-extension.async-propagation=true
     * 通知渠道在 @Async 线程池里计算，生鲜 + 加急仍命中急速达能力
     */
    @Test
    public void testAsyncNotifyKeepsBusinessBinding() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/notify-async?bizCode=fresh&categories=fresh&urgent=true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SMS")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("WECHAT_MSG")));
    }

    /**
     * Case22: HTTP 之外的入口(消息消费、定时任务、RPC)用 callWith 绑定业务
     * 预期: 以生鲜业务执行，运费为冷链运费 15 + 3 件 x 2 = ¥21
     */
    @Test
    public void testCallWithOutsideHttp() {
        OrderMatchParam param = new OrderMatchParam("fresh", "NORMAL", new BigDecimal("397.00"), 3,
                "广东省", false, List.of("fresh"));
        BigDecimal freight = context.callWith(param,
                () -> context.first(FreightCalcExtension.class).calcFreight(param.getShippingProvince(), param.getItemCount()));
        assertEquals(0, new BigDecimal("21").compareTo(freight));
    }
}
