# Easy Extension SpringBoot简单场景

SpringBoot简单场景

## 一、其它场景demo

* SpringBoot复杂场景(能力叠加，冲突等)，请参考[spring-boot-sample-complex](../spring-boot-sample-complex/README.md)
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
   * 需要实现所有的扩展点，当命中的能力和生效的能力都没有实现某个扩展点是，默认实现会作为兜底逻辑
   * 需要@ExtensionPointDefaultImplementation注解
   */
  @ExtensionPointDefaultImplementation
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
   * 需要@MatcherParam注解，以便包扫描能识别到
   * 该参数由具体业务自己定义，所有的业务和能力均需要基于该参数进行生效判断
   * 用于业务(BusinessA/BusinessB)生效匹配的参数，
   * 在请求开始是将该参数准备好，传给EasyExtension的ISessionManager进行管理，后续扩展点调用是会遍历判断哪个业务和哪些能力生效
   */
  @MatcherParam
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
      public Boolean match(MyParam param) {
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
   * 需要@Business注解，以便包扫描能识别到；code表示能力的唯一id；abilities表示业务挂载的能力
   * 业务挂载了能力，即继承了能力的扩展点实现
   */
  @Business(code = "xxx.biz.a", abilities = "app.ability.x")
  public class BusinessA implements Matcher<MyParam>, Ext1 {
  
      /**
       * 业务命中判断
       * 业务必须实现Matcher<XXX>
       *
       * @param param for match predict
       * @return 业务是否命中
       */
      @Override
      public Boolean match(MyParam param) {
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
      public Boolean match(MyParam param) {
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
      public Boolean match(MyParam param) {
          return param.getName().contains("biz-c");
      }
  }
  ```

* Session初始化

  ```java
  /**
   * web请求的Interceptor
   * 在请求处理前，准备好生效匹配的参数<MyParam>
   * 传给EasyExtension的ISessionManager进行管理，后续扩展点调用是会遍历判断哪个业务和哪些能力生效
   */
  @Component
  public class WebInterceptorConfigurer implements WebMvcConfigurer {
  
      @Resource
      private ISessionManager<MyParam> sessionManager;
  
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
          HandlerInterceptor interceptor = new HandlerInterceptor() {
              // 请求开始前需要准备生效匹配的参数<MyParam>
              // 初始化session
              @Override
              public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                  String name = request.getParameter("name") != null ? request.getParameter("name").trim() : "unknown";
                  sessionManager.initSession(new MyParam(name));
                  return true;
              }
  
              // 请求结束后需要清空session
              @Override
              public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                  sessionManager.removeSession();
              }
          };
  
          registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/favicon.ico");
      }
  }
  ```

* 业务，能力及扩展点情况实现概览

| 扩展点实现     | 生效条件              | Ext1                     | Ext2                   | Ext3                     |
|-----------|-------------------|--------------------------|------------------------|--------------------------|
| AbilityX  | name包含"ability-x" | "AbilityX doSomething1"  | -                      | -                        |
| BusinessA | name包含"biz-a"     | "BusinessA doSomething1" | -                      | -                        |
| BusinessB | name包含"biz-b"     | "BusinessB doSomething1" | -                      | "BusinessB doSomething3" |
| BusinessC | name包含"biz-c"     | -                        | -                      | -                        |
| 默认实现      | 默认兜底              | "Default doSomething1"   | "Default doSomething2" | "Default doSomething3"   |

## 三、扩展点使用情况分析

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
  res: res: ext1 = BusinessA doSomething1, ext2 = AbilityX doSomething2, ext3List = [Default doSomething3]
  ```
  
