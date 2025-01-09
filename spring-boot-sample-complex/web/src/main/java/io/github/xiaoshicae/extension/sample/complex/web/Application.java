package io.github.xiaoshicae.extension.sample.complex.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionScan;

/**
 * <p> 1. 先 maven install easy-extension-sample,
 * extension-point-sdk/business-film/business-trip 会打包并发布到本地仓库，同时源码jar包也会被发布到本地仓库
 * 这样web项目的依赖才不会报错
 *
 * <p> 2. 通过 http://localhost:8080/my-extension-admin 访问管理后台
 * <p> 3. 若果扩展点源码信息没有显示在管理后台，可以参考 /doc/resources-code-classpath.png
 * 将extension-point-sdk/business-film/business-trip手动添加到classpath
 */
@SpringBootApplication
@ExtensionScan(scanPackages = "io.github.xiaoshicae.extension.sample.complex")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
