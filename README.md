# Easy-Extension-使用样例
* Spring-Boot简单场景(只有多业务匹配，不考虑能力挂载)，请参考[spring-boot-sample-simple](/spring-boot-sample-simple)
* Spring-Boot复杂场景(同时考虑业务和能力挂载)，请参考[spring-boot-sample-complex](/spring-boot-sample-complex)
* 非Spring项目接入(需要自己注册业务和能力)，请参考[none-spring-boot-sample](/none-spring-boot-sample)


# Spring-Boot项目接入效果
```java
@RestController
@RequestMapping("/api")
public class Controller {
  // 动态注入扩展点1，不同业务和能力会有不同实现
  // 会根据匹配到的业务和挂载的扩展点，注入优先级最高的实现
  // 未匹配到任何业务和能力的实现，会走默认能力进行兜底
  @ExtensionInject
  private Ext1 ext1;

  // 动态注入扩展点2，不同业务会和能力有不同实现
  // 会根据匹配到的业务和挂载的扩展点，注入优先级最高的实现
  // 未匹配到任何业务和能力的实现，会走默认能力进行兜底
  @ExtensionInject
  private Ext2 ext2;

  // 动态注入扩展点3，不同业务会和能力有不同实现
  // 会根据匹配到的业务和挂载的扩展点，注入所有匹配到的实现
  // 包括默认能力
  @ExtensionInject
  private List<Ext3> ext3List;

  @RequestMapping("/process")
  public String process() {
    String s1 = ext1.doSomething1();
    String s2 = ext2.doSomething2();
    List<String> s3List = new ArrayList<>();
    for (Ext3 ext3 : ext3List) {
      s3List.add(ext3.doSomething3());
    }
    return String.format("res: ext1 = %s, ext2 = %s, ext3List = %s", s1, s2, Arrays.toString(s3List.toArray()));
  }
}
```

# 文档
完整文档请参考: [wiki](https://github.com/xiaoshicae/easy-extension/wiki)
