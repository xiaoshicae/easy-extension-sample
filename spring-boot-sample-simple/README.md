# Easy Extension SpringBoot简单场景

SpringBoot简单场景

## 一、其它场景demo

* SpringBoot电商完整场景(能力叠加，优先级等)，请参考[spring-boot-sample-ecommerce](../spring-boot-sample-ecommerce/)
* 简单场景的非Spring-oot项目接入(需要自己注册业务和能力)
  ，请参考[none-spring-boot-sample](../none-spring-boot-sample/README.md)
* 框架设计及详细使用文档请参考: [wiki](https://github.com/xiaoshicae/easy-extension/wiki)

## 二、当前场景demo背景条件简介

* 扩展点定义
    ```java
    /**
     * 扩展点1
     * 需要@ExtensionPoint注解，以便包扫描能识别到
     */
    @ExtensionPoint
    public interface Ext1 {
        String doSomething1();
    }
    
    /**
     * 扩展点2
     */
    @ExtensionPoint
    public interface Ext2 {
        String doSomething2();
    }
    
    /**
     * 扩展点3
     */
    @ExtensionPoint
    public interface Ext3 {
        String doSomething3();
    }
    ```

* 扩展点的默认实现

  ```java
  /**
   * 扩展点的默认实现
   * 当命中的业务和生效的能力都没有实现某个扩展点时，默认实现会作为兜底逻辑
   * 需要@DefaultImplementation注解；每个扩展点至多一个默认实现，一个类可以同时兜底多个扩展点
   */
  @DefaultImplementation
  public class ExtDefaultImpl implements Ext1, Ext2, Ext3 {
  
      /**
       * 扩展点1的默认实现
       */
      @Override
      public String doSomething1() {
          return "Default doSomething1";
      }
  
      /**
       * 扩展点2的默认实现
       */
      @Override
      public String doSomething2() {
          return "Default doSomething2";
      }
  
      /**
       * 扩展点3的默认实现
       */
      @Override
      public String doSomething3() {
          return "Default doSomething3";
      }
  }
  ```

* 匹配参数定义

  ```java
  /**
   * 生效匹配的参数
   * 该参数由具体业务自己定义，所有的业务和能力均需要基于该参数进行生效判断
   * 用于业务(BusinessA/BusinessB)生效匹配的参数，参数类型由Matcher<MyParam>的泛型推导，无需额外注解
   * 每次请求由MatcherParamResolver从HTTP请求构造该参数，框架据此判断哪个业务和哪些能力生效
   */
  public class MyParam {
      private final String name;
  
      public MyParam(String name) {
          this.name = name;
      }
  
      public String getName() {
          return name;
      }
  }
  ```

* 能力定义

  ```java
  /**
   * 能力X
   * 实现了扩展点2
   * 需要@Ability注解，以便包扫描能识别到；code表示能力的唯一id
   */
  @Ability(code = "app.ability.x")
  public class AbilityX implements Matcher<MyParam>, Ext2 {
  
      /**
       * 能力生效判断
       * 能力必须实现Matcher<XXX>
       *
       * @param param for match predict
       * @return 能力是否生效
       */
      @Override
      public boolean match(MyParam param) {
          return param.getName().contains("ability-x");
      }
  
      /**
       * 扩展点2的AbilityX自定义实现
       */
      @Override
      public String doSomething2() {
          return "AbilityX doSomething2";
      }
  }
  ```

* 业务定义

  ```java
  /**
   * 业务A
   * 实现了扩展点1
   * 需要@Business注解，以便包扫描能识别到；code表示业务的唯一id；abilities表示业务挂载的能力(能力类)
   * 业务挂载了能力，即继承了能力的扩展点实现
   * abilities的数组顺序即优先级；未列出Self.class时，业务自身优先
   */
  @Business(code = "xxx.biz.a", abilities = AbilityX.class)
  public class BusinessA implements Matcher<MyParam>, Ext1 {
  
      /**
       * 业务命中判断
       * 业务必须实现Matcher<XXX>
       *
       * @param param for match predict
       * @return 业务是否命中
       */
      @Override
      public boolean match(MyParam param) {
          return param.getName().contains("biz-a");
      }
  
      /**
       * 扩展点1的BusinessA自定义实现
       */
      @Override
      public String doSomething1() {
          return "BusinessA doSomething1";
      }
  }
  
  /**
   * 业务B
   * 实现了扩展点1和扩展点3
   * 需要@Business注解，以便包扫描能识别到；code表示能力的唯一id
   * 业务B没有挂载任何能力
   */
  @Business(code = "app.business.b")
  public class BusinessB implements Matcher<MyParam>, Ext1, Ext3 {
  
      /**
       * 业务命中判断
       * 业务必须实现Matcher<XXX>
       *
       * @param param for match predict
       * @return 业务是否命中
       */
      @Override
      public boolean match(MyParam param) {
          return param.getName().contains("biz-b");
      }
  
      /**
       * 扩展点1的BusinessB自定义实现
       */
      @Override
      public String doSomething1() {
          return "BusinessB doSomething1";
      }
  
      /**
       * 扩展点3的BusinessB自定义实现
       */
      @Override
      public String doSomething3() {
          return "BusinessB doSomething3";
      }
  }
  
  
  /**
   * 业务C
   * 需要@Business注解，以便包扫描能识别到
   * 业务C没有挂载任何能力，也没有实现任何扩展点，因此所哟扩展点均用系统默认实现
   */
  @Business(code = "app.business.c")
  public class BusinessC implements Matcher<MyParam> {
  
      /**
       * 业务命中判断
       * 业务必须实现Matcher<XXX>
       *
       * @param param for match predict
       * @return 业务是否命中
       */
      @Override
      public boolean match(MyParam param) {
          return param.getName().contains("biz-c");
      }
  }
  ```

* 匹配参数解析

  ```java
  /**
   * 提供MatcherParamResolver Bean即可：框架在每个web请求开始前，用它从HTTP请求构造匹配参数<MyParam>并绑定到当前线程，
   * 请求结束后自动解绑，无需手写Interceptor
   */
  @Configuration
  public class MatcherParamConfig {

      @Bean
      public MatcherParamResolver<MyParam> matcherParamResolver() {
          return request -> {
              String name = request.getParameter("name");
              return new MyParam(name != null ? name.trim() : "unknown");
          };
      }
  }
  ```

* 扩展点注入
  
  ```java
  @RestController
  @RequestMapping("/api")
  public class Controller {
  
    /**
     * 系统提供的扩展点1
     * 注解@ExtensionInject会注入扩展点1的动态代理
     * 运行时会根据匹配到的业务及使用的能力，选择有最高优先级的生效的扩展点实现
     * 如果业务及使用的能力都没有实现该扩展点，则会走默认实现进行兜底
     */
    @ExtensionInject
    private Ext1 ext1;
  
    /**
     * 系统提供的扩展点2
     */
    @ExtensionInject
    private Ext2 ext2;
  
    /**
     * 系统提供的扩展点3
     * 注解@ExtensionInject会注入List<Extension>的动态代理，包含所有生效的实现
     * 运行时会根据匹配到的业务及使用的能力，按照优先级依次包含生效的扩展实现
     * List当然也包含扩展点的默认实现
     */
    @ExtensionInject
    private List<Ext3> ext3List;
  
  
    @RequestMapping("/process")
    public String process() {
      String s1 = ext1.doSomething1(); // 执行扩展点1，具体用哪个实现，由匹配到的业务及生效的能力+优先级决定
      String s2 = ext2.doSomething2(); // 执行扩展点2，具体用哪个实现，由匹配到的业务及生效的能力+优先级决定
  
      List<String> s3List = new ArrayList<>();
      for (Ext3 ext3 : ext3List) {
        s3List.add(ext3.doSomething3()); // 按优先级从高到低，依次执行扩展点3的业务或生效能力的实现
      }
      return String.format("res: ext1 = %s, ext2 = %s, ext3List = %s", s1, s2, Arrays.toString(s3List.toArray()));
    }
  }
  ```

* 业务，能力及扩展点情况实现概览

| 扩展点实现     | 生效条件              | Ext1                     | Ext2                   | Ext3                     |
|-----------|-------------------|--------------------------|------------------------|--------------------------|
| AbilityX  | name包含"ability-x" | -                        | "AbilityX doSomething2" | -                        |
| BusinessA | name包含"biz-a"     | "BusinessA doSomething1" | -                      | -                        |
| BusinessB | name包含"biz-b"     | "BusinessB doSomething1" | -                      | "BusinessB doSomething3" |
| BusinessC | name包含"biz-c"     | -                        | -                      | -                        |
| 默认实现      | 默认兜底              | "Default doSomething1"   | "Default doSomething2" | "Default doSomething3"   |

## 三、扩展点使用情况分析
以下case可以参考[ApplicationTest](./src/test/java/ApplicationTest.java)

* Case1: 未命中任何业务

  ```shell
  GET http://127.0.0.1:8080/api/process?name=unknown
  
  由于未命中任何业务,扩展点均走默认实现,因此返回值为:
  res: ext1 = Default doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]
  ```

* Case2: 请求命中BusinessC

  ```shell
  GET http://127.0.0.1:8080/api/process?name=biz-c
  
  命中了业务C,但是业务C未实现任何扩展点也没挂载任何能力,扩展点均走默认实现,因此返回值为:
  res: ext1 = Default doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]
  ```

* Case3: 请求命中BusinessB

  ```shell
  GET http://127.0.0.1:8080/api/process?name=biz-b
  
  命中了业务B,业务B实现了扩展点1和3,因此返回值为:
  res: ext1 = BusinessB doSomething1, ext2 = Default doSomething2, ext3List = [BusinessB doSomething3, Default doSomething3]
  ```

* Case4: 请求命中BusinessA & AbilityX未生效

  ```shell
  GET http://127.0.0.1:8080/api/process?name=biz-a
  
  命中了业务A,业务A实现了扩展点1,因此返回值为:
  res: ext1 = BusinessA doSomething1, ext2 = Default doSomething2, ext3List = [Default doSomething3]
  ```

* Case5: 请求命中BusinessA & AbilityX生效

  ```shell
  GET http://127.0.0.1:8080/api/process?name=biz-a::ability-x
  
  命中了业务A,且能力X生效,业务A实现了扩展点1,能力X实现扩展点2,因此返回值为:
  res: ext1 = BusinessA doSomething1, ext2 = AbilityX doSomething2, ext3List = [Default doSomething3]
  ```
  
