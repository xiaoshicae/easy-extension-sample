# Easy Extension 使用样例

[Easy Extension](https://github.com/xiaoshicae/easy-extension) 是一个轻量级的 Java 业务扩展点框架，通过**扩展点 + 能力 + 业务**的架构模式，优雅地解决多业务场景下的逻辑复用与隔离问题。

本仓库提供了从简单到复杂的完整使用样例，帮助你快速上手框架。

## 核心概念

<img src="/doc/concept.svg" alt="核心概念">

- **扩展点**: 系统定义的接口（如运费计算、订单校验），规定"做什么"
- **能力**: 通用的实现（如包邮、VIP优惠），可被多个业务复用
- **业务**: 接入方（如零售、生鲜），挂载需要的能力，也可以自己实现扩展点
- **默认实现**: 所有扩展点的兜底实现，保证扩展点的默认行为

## 工作原理

<img src="/doc/how-it-works.svg" alt="运行流程">

一个请求进来后，框架自动完成：**匹配业务 → 激活能力 → 按优先级排序 → 调用正确的实现**。业务方只需实现自己关心的扩展点，其余自动降级到通用能力或默认实现。

## 样例一览

本仓库包含 4 个由浅入深的示例项目：

```
easy-extension-sample/
├── spring-boot-sample-simple        # 入门示例：3个扩展点 + 1个能力 + 3个业务
├── spring-boot-sample-complex/      # 进阶示例：能力叠加、扩展点冲突解决
│   ├── extension-point-sdk          #   扩展点SDK（公共定义）
│   ├── business-film                #   电影票业务
│   ├── business-trip                #   酒旅业务
│   └── web                          #   Web应用
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

### 2. 复杂场景 — [spring-boot-sample-complex](/spring-boot-sample-complex/README.md)

以电商下单场景为例，展示能力叠加与冲突解决。

- 3 个扩展点：算价、延迟关单、跳过0元校验
- 2 个能力：免费体验（`FreeTrialAbility`）、长关单（`LongCloseOrderAbility`）
- 2 个业务：电影票（`FilmBusiness`，优先级30）、酒旅（`TripBusiness`）
- 重点演示：**同一扩展点的多个实现之间按优先级冲突解决**

### 3. 电商完整场景 — [spring-boot-sample-ecommerce](/spring-boot-sample-ecommerce/)

覆盖完整电商下单流程的大型示例，适合作为实际项目参考。

<details>
<summary>10 个扩展点</summary>

| 扩展点 | 说明 | 默认值 |
|--------|------|--------|
| OrderValidateExtension | 订单校验 | 金额校验 |
| StockCheckExtension | 库存检查 | 全部充足 |
| PromotionCalcExtension | 促销计算 | ¥0 无优惠 |
| FreightCalcExtension | 运费计算 | ¥8 基础运费 |
| TaxCalcExtension | 税费计算 | ¥0 国内无税 |
| RiskControlExtension | 风控检查 | PASS |
| PaymentMethodExtension | 支付方式 | 支付宝、微信 |
| InvoiceExtension | 发票处理 | 不开票 |
| AfterSalePolicyExtension | 售后策略 | 不支持退货 |
| NotifyExtension | 通知策略 | APP推送 |

</details>

<details>
<summary>5 个能力</summary>

| 能力 | 实现的扩展点 | 效果 |
|------|------------|------|
| FreeShippingAbility | 运费计算 | 运费 = ¥0 |
| Return7DaysAbility | 售后策略 | 7天无理由退货 |
| VipCouponAbility | 促销计算 | 额外减免 ¥20 |
| RapidDeliveryAbility | 运费计算 + 通知策略 | 加急费 ¥8 + 多渠道通知 |
| InstallmentAbility | 支付方式 + 风控检查 | 3/6/12期免息分期 |

</details>

<details>
<summary>3 个业务</summary>

| 业务 | 优先级 | 挂载能力 | 特色 |
|------|--------|---------|------|
| RetailBusiness（标准零售） | 100 | 包邮 + 7天退货 + VIP优惠 | 标准电商流程 |
| FreshBusiness（生鲜电商） | 50 | 包邮 + 急速达 | 冷链运费、2h退货窗口 |
| DigitalBusiness（数码3C） | 75 | 7天退货 + 分期 + VIP + 包邮 | 15天退货、>¥5000人工审核、电子发票 |

</details>

### 4. 非 SpringBoot 场景 — [none-spring-boot-sample](/none-spring-boot-sample/README.md)

纯 Java 接入方式，无需 Spring 容器，手动注册业务和能力。

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
    <version>3.3.0</version>
</dependency>
```

| 扩展点管理 | 冲突检测 |
|:----------:|:--------:|
| ![扩展点管理](/doc/admin-extension.png) | ![冲突检测](/doc/admin-business-conflict.png) |

## 技术栈

| 组件 | 版本 |
|------|------|
| Easy Extension | 3.3.0 |
| Spring Boot | 4.0.5 |
| Java | 21 |

## 文档

框架设计及详细使用文档请参考: [Wiki](https://github.com/xiaoshicae/easy-extension/wiki)

## License

本项目遵循 [Easy Extension](https://github.com/xiaoshicae/easy-extension) 主项目的开源协议。
