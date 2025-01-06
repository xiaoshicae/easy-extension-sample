# Easy Extension SpringBoot复杂场景

SpringBoot复杂场景(以电商下单场景为例)，考虑能力叠加，扩展点冲突解决等。

## 一、其它场景demo

* SpringBoot简单场景，请参考[spring-boot-sample-simple](../spring-boot-sample-simple/README.md)
* 简单场景的非Spring-oot项目接入(需要自己注册业务和能力)
  ，请参考[none-spring-boot-sample](../none-spring-boot-sample/README.md)
* 框架设计及详细使用文档请参考: [wiki](https://github.com/xiaoshicae/easy-extension/wiki)

## 二、当前场景demo背景条件简介

* 项目分包简介
  ```text
  ├── business-film     // 电影票业务，业务接入方建议单独拆出来，与平台逻辑进行隔离。业务逻辑建议由业务方维护。业务jar包可以与平台一起打包，或者自定义classloader动态加载业务方jar包
  │
  ├── business-trip     // 酒旅业务
  │ 
  ├── extension-point   // 扩展点定义，最为公共SDK，供业务方import。包含扩展点，默认实现，匹配参数和能力的定义
  │
  └── web               // web demo，模拟平台通用能力系统
  ```

* 扩展点定义
  ```java
  /**
   * 算价扩展点
   * 下单过程中会调用该扩展点进行算价
   */
  @ExtensionPoint
  public interface CalculatePriceExtension {
  
    /**
     * 价格计算
     *
     * @param order 订单
     * @return 该订单的价格
     */
    BigDecimal calculatePrice(OrderDTO order);
  }
  
  /**
   * 延迟关单扩展点
   * 订单未支付时，需要在xxx分钟进行自动关单
   */
  @ExtensionPoint
  public interface DelayCloseOrderExtension {
  
    /**
     * 延迟关单
     *
     * @param orderType 订单类型
     * @return 自动关单时间(0 表示不关单)
     */
    Duration delayCloseOrderDuration(String orderType);
  }
  
  /**
   * 跳过0元校验扩展点
   * 支付过程需要校验订单是否为0元
   */
  @ExtensionPoint
  public interface SkipCheckZeroPriceExtension {
  
    /**
     * 是否跳过0元校验
     *
     * @return 是否跳过参数校验
     */
    Boolean skipCheckZeroPrice();
  }
  ```

* 扩展点的默认实现
  ```java
  /**
   * 扩展点的默认实现，实现了系统所有扩展点
   * 当没有匹配到任何扩展点实现时，作为系统能力的兜底实现
   */
  @ExtensionPointDefaultImplementation
  public class ExtDefaultImpl implements CalculatePriceExtension, SkipCheckZeroPriceExtension, DelayCloseOrderExtension {
  
    /**
     * 算价扩展点默认实现
     * 默认: 原始价格 x 折扣
     *
     * @param order 订单
     * @return 价格
     */
    @Override
    public BigDecimal calculatePrice(OrderDTO order) {
      return order.getOriginalPrice().multiply(order.getDiscount());
    }
  
    /**
     * 延迟关单扩展点默认实现
     * 默认: 10min
     *
     * @param orderType 订单类型
     * @return 延迟关单时间
     */
    @Override
    public Duration delayCloseOrderDuration(String orderType) {
      return Duration.ofMinutes(10);
    }
  
    /**
     * 跳过0元校验扩展点默认实现
     * 默认: false
     *
     * @return 是否跳过
     */
    @Override
    public Boolean skipCheckZeroPrice() {
      return false;
    }
  }
  ```

* 匹配参数定义
  ```java
  
  /**
   * 匹配参数，用于判断扩展点是否生效的参数
   * Business和Ability的match()会接受该参数，并判断是否生效
   */
  @MatcherParam
  public class MatchParam {
    private String Name;
    private String Value;
  
    public MatchParam(){}
  
    public MatchParam(String name, String value) {
      this.Name = name;
      this.Value = value;
    }
  
    public String getName() {
      return Name;
    }
  
    public void setName(String name) {
      Name = name;
    }
  
    public String getValue() {
      return Value;
    }
  
    public void setValue(String value) {
      Value = value;
    }
  }
  ```

* 能力定义
  ```java
  /**
   * 免费体验能力
   * 实现了: 1.跳过0元校验扩展点 2.延迟关单扩展点
   */
  @Ability(code = FreeTrialAbility.CODE)
  public class FreeTrialAbility implements Matcher<MatchParam>, SkipCheckZeroPriceExtension, DelayCloseOrderExtension {
    public static final String CODE = "app.ability.free-trial";
  
    /**
     * 免费体验能力匹配生效条件
     *
     * @param param 参数
     * @return value包含free-trial即表示免费体验能力生效
     */
    @Override
    public Boolean match(MatchParam param) {
      return param.getValue().contains("free-trial");
    }
  
    /**
     * 延迟关单扩展点实现
     * 返回0，即不关单
     *
     * @param orderType 订单类型
     * @return 自定关单时间
     */
    @Override
    public Duration delayCloseOrderDuration(String orderType) {
      return Duration.ZERO;
    }
  
  
    /**
     * 跳过0元校验扩展点实现
     * 返回true
     *
     * @return 是否跳过
     */
    @Override
    public Boolean skipCheckZeroPrice() {
      return true;
    }
  }
  
  
  /**
   * 长关单能力
   * 实现了: 1.延迟关单扩展点
   */
  @Ability(code = LongCloseOrderAbility.CODE)
  public class LongCloseOrderAbility implements Matcher<MatchParam>, DelayCloseOrderExtension {
    public static final String CODE = "app.ability.long-close";
  
  
    /**
     * 长关单能力匹配生效条件
     *
     * @param param 参数
     * @return value包含long-close即表示长关单能力生效
     */
    @Override
    public Boolean match(MatchParam param) {
      return param.getValue().contains("long-close");
    }
  
    /**
     * 延迟关单扩展点实现
     * 返回1h
     *
     * @param orderType 订单类型
     * @return 自定关单时间
     */
    @Override
    public Duration delayCloseOrderDuration(String orderType) {
      return Duration.ofHours(1);
    }
  }
  ```

* 业务定义
  ```java
  /**
   * 电影票业务
   * 挂载了: 1.免费体验能力(priority:10) 2.长关单能力(priority:20)
   * 实现了延迟关单扩展点(priority:30)
   * 挂载的2个能力和业务本身都实现了延迟关单扩展点，冲突是会按优先级匹配
   */
  @Business(code = FilmBusiness.CODE, priority = 30, abilities = {"app.ability.free-trial::10", "app.ability.long-close::20"})
  public class FilmBusiness implements Matcher<MatchParam>, DelayCloseOrderExtension {
    public static final String CODE = "biz.groupon.film";
  
    /**
     * 电影票业务
     *
     * @param param 参数
     * @return name包含film即表示是酒旅业务
     */
    @Override
    public Boolean match(MatchParam param) {
      return param.getName().contains("film");
    }
  
    /**
     * 延迟关单扩展点实现
     * 电影票默认3min未支付自动关单
     *
     * @param orderType 订单类型
     * @return 自动关单时间
     */
    @Override
    public Duration delayCloseOrderDuration(String orderType) {
      return Duration.ofMinutes(3);
    }
  }
  
  
  /**
   * 酒旅业务
   * 没有挂载任何能力
   * 实现了算价扩展点
   */
  @Business(code = TripBusiness.CODE)
  public class TripBusiness implements Matcher<MatchParam>, CalculatePriceExtension {
    public static final String CODE = "biz.trip.base";
  
    /**
     * 酒旅业务匹配生效条件
     *
     * @param param 参数
     * @return name包含xxx-trip即表示是酒旅业务
     */
    @Override
    public Boolean match(MatchParam param) {
      return param.getName().contains("xxx-trip");
    }
  
  
    /**
     * 酒旅业务算价接口自定义实现
     * 折扣固定未65折
     *
     * @param order 订单
     * @return 订单价格
     */
    @Override
    public BigDecimal calculatePrice(OrderDTO order) {
      BigDecimal discount = BigDecimal.valueOf(0.65);
      return order.getOriginalPrice().multiply(discount);
    }
  }
  ```

* Session初始化

  ```java
  /**
   * web请求的Interceptor
   * 在请求处理前，准备好生效匹配的参数<MatchParam>
   * 传给EasyExtension的ISessionManager进行管理，后续扩展点调用是会遍历判断哪个业务和哪些能力生效
   */
  @Component
  public class WebInterceptorConfigurer implements WebMvcConfigurer {
  
    @Resource
    private ISessionManager<MatchParam> sessionManager;
  
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      HandlerInterceptor interceptor = new HandlerInterceptor() {
  
        // 请求开始前需要初始化session
        // 注入匹配参数，用于Business生效匹配
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "unknown";
          String value = request.getParameter("value") != null ? request.getParameter("value").trim() : "unknown";
          sessionManager.initSession(new MatchParam(name, value));
          return true;
        }
  
        // 请求结束后需要清空session
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
          sessionManager.removeSession();
        }
      };
  
      registry.addInterceptor(interceptor).addPathPatterns("/api/**");
    }
  }
  ```

* 扩展点注入

```java

@RestController
@RequestMapping("/api")
public class Controller {

    /**
     * 算价扩展点
     * 动态匹配不同业务的具体实现
     * 未匹配到任何实现时，会走默认实现兜底
     */
    @ExtensionInject
    private CalculatePriceExtension calculatePriceExtension;

    /**
     * 延迟关单扩展点
     */
    @ExtensionInject
    private DelayCloseOrderExtension delayCloseOrderExtension;

    /**
     * 跳过0元校验扩展点
     * 会匹配所有生效的扩展点实现 (包括默认能力)
     */
    @ExtensionInject
    private List<SkipCheckZeroPriceExtension> skipCheckZeroPriceExtensions;


    @RequestMapping("/process")
    public String process() {

        // 调用算价扩展点
        OrderDTO orderDTO = new OrderDTO("1", BigDecimal.valueOf(100), BigDecimal.valueOf(0.9));
        BigDecimal price = calculatePriceExtension.calculatePrice(orderDTO);

        Duration closeOrderDuration = delayCloseOrderExtension.delayCloseOrderDuration("ot");

        List<Boolean> skipCheckList = new ArrayList<>();
        for (SkipCheckZeroPriceExtension ext : skipCheckZeroPriceExtensions) {
            skipCheckList.add(ext.skipCheckZeroPrice());
        }

        return String.format("res: price = %.3f && close order duration = %s && skip ckeck list = %s", price, closeOrderDuration.toString(), Arrays.toString(skipCheckList.toArray()));
    }
}
```

* 业务，能力及扩展点情况实现概览

| 扩展点实现                 | 生效条件                | CalculatePriceExtension      | DelayCloseOrderExtension | SkipCheckZeroPriceExtension |
|-----------------------|---------------------|------------------------------|--------------------------|-----------------------------|
| FreeTrialAbility      | value包含"free-trial" | -                            | Duration.ZERO            | true                        |
| LongCloseOrderAbility | value包含"long-close" | -                            | Duration.ofHours(1)      | -                           |
| FilmBusiness          | name包含"film"        | -                            | Duration.ofMinutes(3)    | -                           |
| TripBusiness          | name包含"xxx-trip"    | Order.price * 0.65           | -                        | -                           |
| 默认实现                  | 默认兜底                | Order.price * Order.discount | Duration.ofMinutes(10)   | false                       |

## 三、扩展点使用情况分析

* Case1: 未命中任何业务

  ```shell
  GET http://127.0.0.1:8080/api/process?name=unknown
  
  由于未命中任何业务,扩展点均走默认实现,因此返回值为:
  res: price = 90.000 && close order duration = PT10M && skip ckeck list = [false]
  ```

* Case2: 请求命中TripBusiness

  ```shell
  GET http://127.0.0.1:8080/api/process?name=xxx-trip
  
  命中了业务Trip,但是业务实现了扩展点CalculatePriceExtension,因此返回值为:
  res: price = 65.000 && close order duration = PT10M && skip ckeck list = [false]
  ```

* Case3: 请求命中FilmBusiness & 所有能力均为生效

  ```shell
  GET http://127.0.0.1:8080/api/process?name=film
  
  命中了业务FilmBusiness,业务实现了扩展点DelayCloseOrderExtension,业务所挂载的能力均为生效,因此返回值为:
  es: price = 90.000 && close order duration = PT3M && skip ckeck list = [false]
  ```

* Case3: 请求命中FilmBusiness & LongCloseOrderAbility能力生效

  ```shell
  GET http://127.0.0.1:8080/api/process?name=film&value=long-close
  
  命中了业务FilmBusiness,业务实现了扩展点DelayCloseOrderExtension,
  业务挂载LongCloseOrderAbility能力生效,能力也实现了扩展点DelayCloseOrderExtension,
  由于挂载的能力优先级更高因此返回值为:
  res: price = 90.000 && close order duration = PT1H && skip ckeck list = [false]
  ```

* Case5: 请求命中FilmBusiness & FreeTrialAbility能力生效 &LongCloseOrderAbility能力生效

  ```shell
  GET http://127.0.0.1:8080/api/process?name=film&value=long-close::free-trial
  
    
  命中了业务FilmBusiness,业务实现了扩展点DelayCloseOrderExtension,
  业务挂载FreeTrialAbility能力生效,能力实现了扩展点DelayCloseOrderExtension和SkipCheckZeroPriceExtension,
  业务挂载LongCloseOrderAbility能力生效,能力实现了扩展点DelayCloseOrderExtension,
  由于挂载的能力优先级更高因此返回值为:
  res: price = 90.000 && close order duration = PT0S && skip ckeck list = [true, false]
  ```

## Case5的扩展点冲突及执行情况分析

| 扩展点实现                 | 优先级 | 生效条件                | CalculatePriceExtension      | DelayCloseOrderExtension | SkipCheckZeroPriceExtension |
|-----------------------|-----|---------------------|------------------------------|--------------------------|-----------------------------|
| FreeTrialAbility      | 10  | value包含"free-trial" | -                            | Duration.ZERO            | true                        |
| LongCloseOrderAbility | 20  | value包含"long-close" | -                            | Duration.ofHours(1)      | -                           |
| FilmBusiness          | 30  | name包含"film"        | -                            | Duration.ofMinutes(3)    | -                           |
| 默认实现                  | -   | 默认兜底                | Order.price * Order.discount | Duration.ofMinutes(10)   | false                       |
| 最终冲突执行情况              | -   | -                   | 业务和能力均为实现，走默认实现              | 多个实现，选择优先级最高的实现          | 多个实现，全部获取                   |

## 管理后台功能简介
### 相关配置
* 引入依赖
  ```xml
  <dependency>
      <groupId>io.github.xiaoshicae</groupId>
      <artifactId>easy-extension-admin-spring-boot-starter</artifactId>
      <version>3.0.1</version>
  </dependency>
  ```
* 项目依赖扩展点jar包同时也需要依赖源码jar包
  ```xml
  <dependencies>
      <dependency>
          <groupId>io.github.xiaoshicae</groupId>
          <artifactId>extension-point</artifactId>
          <version>3.0.1-SNAPSHOT</version>
      </dependency>
  
      <!--  依赖源码，admin会读取源码内容  -->
      <dependency>
          <groupId>io.github.xiaoshicae</groupId>
          <artifactId>extension-point</artifactId>
          <version>3.0.1-SNAPSHOT</version>
          <classifier>sources</classifier> <!-- 通过sources引入源码jar包 -->
      </dependency>
  
      <!-- 其它源码jar包依赖 -->
  </dependencies>
  ```

* 源码jar包打包说明(可以依赖以下maven插件打出源码jar包)
  ```xml
  <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-source-plugin</artifactId>
      <executions>
          <execution>
              <id>attach-sources</id>
              <goals>
                  <goal>jar-no-fork</goal>
              </goals>
          </execution>
      </executions>
  </plugin>
  ```
* 为了避免idea本地调试找不到源码jar包，可以在先install把所有工程打包到本地maven仓库，再手动改下business-film/business-trip/extension-point的pom
  version，这样web项目就会从maven仓库查找依赖，而不是从当前idea工程查找依赖。

### 管理后台使用
* 默认访问的url: http://127.0.0.1:8080/my-extension-admin
* 管理后台提供的能力:
    * 提供扩展点，能力和业务的可视化能力
      ![](/doc/admin-extension.png)
    * 提供了扩展点冲突检测能力 (业务及能力可能实现了相同的扩展点，可能存在冲突。发生冲突时，会选择优先级最高的实现)
      ![](/doc/admin-business-conflict.png)
