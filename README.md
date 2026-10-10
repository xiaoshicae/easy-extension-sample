# Easy Extension 使用样例

[Easy Extension](https://github.com/xiaoshicae/easy-extension) 是一个轻量级的 Java 业务扩展点框架，通过**扩展点 + 能力 +
业务**的架构模式，优雅地解决多业务场景下的逻辑复用与隔离问题。

本仓库提供了从简单到复杂的完整使用样例，帮助你快速上手框架。

## 核心概念

<img src="/doc/concept.svg" alt="核心概念">

- **扩展点**: 系统定义的接口（如运费计算、订单校验），规定"做什么"
- **能力**: 通用的实现（如包邮、VIP优惠），可被多个业务复用
- **业务**: 接入方（如零售、生鲜），挂载需要的能力，也可以自己实现扩展点
- **默认实现**: 扩展点的兜底实现，保证扩展点的默认行为（每个扩展点至多一个，一个类可兜底多个扩展点）

## 工作原理

<img src="/doc/how-it-works.svg" alt="运行流程">

一个请求进来后，框架自动完成：**匹配业务 → 激活能力 → 按 `abilities` 顺序排序 → 调用正确的实现**。业务方只需实现自己关心的扩展点，其余自动降级到通用能力或默认实现。

## 样例一览

本仓库包含 3 个由浅入深的示例项目：

```
easy-extension-sample/
├── spring-boot-sample-simple        # 入门示例：3个扩展点 + 1个能力 + 3个业务
├── spring-boot-sample-ecommerce/    # 完整电商示例：10个扩展点 + 5个能力 + 3个业务
│   ├── ecommerce-extension-point-sdk#   电商扩展点SDK
│   ├── business-retail              #   标准零售业务
│   ├── business-fresh               #   生鲜电商业务
│   ├── business-digital             #   数码3C业务
│   └── ecommerce-web                #   Web应用 + 完整测试用例
└── none-spring-boot-sample          # 非SpringBoot接入示例（手动注册）
```

### 1. 简单场景 — [spring-boot-sample-simple](/spring-boot-sample-simple/README.md)

最小化示例，快速理解扩展点机制。

- 3 个扩展点 `Ext1` / `Ext2` / `Ext3`
- 1 个能力 `AbilityX`，3 个业务 `BusinessA` / `BusinessB` / `BusinessC`
- 展示：默认兜底、能力挂载继承、`List<Extension>` 获取所有生效实现

### 2. 电商完整场景 — [spring-boot-sample-ecommerce](/spring-boot-sample-ecommerce/)

覆盖完整电商下单流程的大型示例，适合作为实际项目参考。

还演示了 4.1 的绑定用法：

- **异步线程沿用业务绑定**：`OrderNotifyService` 的 `@Async` 方法在线程池里调用扩展点，靠 `easy-extension.async-propagation: true` 拿到请求的业务(`POST /api/order/notify-async?bizCode=fresh&categories=fresh&urgent=true`)
- **HTTP 之外的入口**：消息消费、定时任务、RPC 用 `context.callWith(param, () -> ...)` 绑定业务，见 `EcommerceApplicationTest#testCallWithOutsideHttp`

<details>
<summary>10 个扩展点</summary>

每个扩展点只声明自己需要的入参，而不是统一接收一个大而全的订单上下文：

| 扩展点                      | 说明   | 入参                            | 默认值     |
|--------------------------|------|-------------------------------|---------|
| OrderValidateExtension   | 订单校验 | 金额、件数                         | 金额/件数校验 |
| StockCheckExtension      | 库存检查 | SKU 及件数                       | 全部充足    |
| PromotionCalcExtension   | 促销计算 | 金额                            | ¥0 无优惠  |
| FreightCalcExtension     | 运费计算 | 收货省份、件数                       | ¥8 基础运费 |
| TaxCalcExtension         | 税费计算 | 金额、商品品类                       | ¥0 国内无税 |
| RiskControlExtension     | 风控检查 | 用户、金额                         | PASS    |
| PaymentMethodExtension   | 支付方式 | 金额                            | 支付宝、微信  |
| InvoiceExtension         | 发票处理 | 是否开票、抬头类型                     | 不开票     |
| AfterSalePolicyExtension | 售后策略 | 商品品类                          | 不支持退货   |
| NotifyExtension          | 通知策略 | 订单事件                          | APP推送   |

</details>

<details>
<summary>5 个能力</summary>

能力是否生效由能力自己的 `match(OrderMatchParam)` 按订单属性判断（会员等级、金额、收货省份、件数、是否加急、商品品类），
请求不需要指明要启用哪些能力：

| 能力                   | 命中条件                              | 实现的扩展点      | 效果                |
|----------------------|-----------------------------------|-------------|-------------------|
| FreeShippingAbility  | SVIP 会员，或 金额 ≥ ¥500 且收货地非偏远省份     | 运费计算        | 运费 = ¥0           |
| Return7DaysAbility   | 不含生鲜/定制品类 且 金额 < ¥3000            | 售后策略        | 7天无理由退货           |
| VipCouponAbility     | 会员等级为 VIP / SVIP                  | 促销计算        | 满 500 减 ¥50，否则 ¥20 |
| RapidDeliveryAbility | 勾选加急 且 省份已开通急速达 且 件数 ≤ 10         | 运费计算 + 通知策略 | 加急费 ¥12 + 多渠道通知   |
| InstallmentAbility   | 金额 ≥ ¥3000                        | 支付方式 + 风控检查 | 3/6/12期免息分期       |

</details>

<details>
<summary>3 个业务</summary>

业务用 `bizCode` 认领请求，并决定挂载哪些能力以及它们的优先级：

| 业务                    | 挂载能力（`abilities` 顺序即优先级，`Self` 为业务自身） | 特色                     |
|-----------------------|-----------------------------------------|------------------------|
| RetailBusiness（标准零售）  | 包邮 → 7天退货 → VIP优惠 → Self                | 标准电商流程                 |
| FreshBusiness（生鲜电商）   | 包邮 → 急速达 → Self                         | 冷链运费(跨省加价)、2h退货窗口      |
| DigitalBusiness（数码3C） | 7天退货 → 分期 → VIP → 包邮 → Self             | 15天延保、新用户人工审核、专票/电子发票  |

</details>

<details>
<summary>请求参数</summary>

同一份请求参数有两个用途：`MatcherParamConfig` 用它构造 `OrderMatchParam`（决定命中哪个业务、哪些能力），
`OrderController` 用它作为扩展点的入参。

| 参数            | 默认值        | 说明                                  |
|---------------|------------|-------------------------------------|
| bizCode       | retail     | 业务标识: retail / fresh / digital       |
| memberLevel   | NORMAL     | 会员等级: NORMAL / VIP / SVIP            |
| amount        | 397.00     | 订单金额                                |
| itemCount     | 3          | 商品件数（演示订单按 2 + 1 拆到两个 SKU 上）         |
| province      | 广东省        | 收货省份                                |
| urgent        | false      | 是否勾选加急配送                            |
| categories    | general    | 商品品类，逗号分隔: general / fresh / digital / custom |
| userId        | USER-12345 | 下单用户，`NEW-` 前缀表示新注册用户                |
| needInvoice   | false      | 是否开票                                |
| invoiceTarget | personal   | 抬头类型: personal / company             |
| notifyEvent   | ORDER_CREATED | 订单事件: ORDER_CREATED / SHIPPED / AFTER_SALE |

中文参数值（如 `province=湖南省`）需要 URL 编码，否则 Tomcat 会直接返回 400。

```bash
# 标准零售 + VIP 会员: 命中 VIP 券，减 ¥20
curl -X POST "http://127.0.0.1:8080/api/order/checkout?bizCode=retail&memberLevel=VIP"

# 生鲜 + 加急: 命中急速达，多渠道通知 + 加急费
curl -X POST "http://127.0.0.1:8080/api/order/checkout?bizCode=fresh&categories=fresh&urgent=true"

# 数码大额订单: 命中分期和包邮，售后回落到业务自身的 15 天延保
curl -X POST "http://127.0.0.1:8080/api/order/checkout?bizCode=digital&categories=digital&amount=6999"
```

</details>

### 3. 非 SpringBoot 场景 — [none-spring-boot-sample](/none-spring-boot-sample/README.md)

纯 Java 接入方式，无需 Spring 容器，用 `ExtensionContext.builder()` 注册扩展点、默认实现、能力和业务。

## 快速开始

### 环境要求

- Java 21+
- Maven 3.6+
- Spring Boot 4.0+ (非 SpringBoot 示例除外)

### 运行示例

```bash
# 克隆项目
git clone https://github.com/xiaoshicae/easy-extension-sample.git
cd easy-extension-sample

# 编译安装（admin功能需要源码jar包）
mvn clean install

# 运行简单场景
cd spring-boot-sample-simple
mvn spring-boot:run

# 访问示例接口
curl "http://127.0.0.1:8080/api/process?name=biz-a::ability-x"
```

## Admin 管理后台

框架内置可视化管理后台，支持扩展点、业务、能力的查看及冲突检测。

- 本地访问地址: `http://127.0.0.1:8080/easy-extension-admin`

引入依赖即可启用：

```xml

<dependency>
    <groupId>io.github.xiaoshicae</groupId>
    <artifactId>easy-extension-admin-spring-boot-starter</artifactId>
    <version>5.0.0</version>
</dependency>
```

|               扩展点管理                |                   冲突检测                    |
|:----------------------------------:|:-----------------------------------------:|
| ![扩展点管理](/doc/admin-extension.png) | ![冲突检测](/doc/admin-business-conflict.png) |

## 技术栈

| 组件             | 版本    |
|----------------|-------|
| Easy Extension | 5.0.0 |
| Spring Boot    | 4.0.5 |
| Java           | 21    |

## 文档

框架设计及详细使用文档请参考: [Wiki](https://github.com/xiaoshicae/easy-extension/wiki)

从 4.x 升级请参考: [5.0 迁移指南](https://github.com/xiaoshicae/easy-extension/blob/main/doc/migration-5.0.md)

从 3.x 升级请参考: [4.0 迁移指南](https://github.com/xiaoshicae/easy-extension/blob/main/doc/migration-4.0.md)

## License

本项目遵循 [Easy Extension](https://github.com/xiaoshicae/easy-extension) 主项目的开源协议。
