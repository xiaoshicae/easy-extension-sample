package io.github.xiaoshicae.extension.sample.ecommerce.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionScan;

/**
 * 电商下单流程示例
 * Admin: http://127.0.0.1:8080/easy-extension-admin
 * 应用包(web)下的类会自动扫描；扩展点、能力、业务分布在其他模块的包里，用 @ExtensionScan 补充扫描范围
 */
@SpringBootApplication
@ExtensionScan(basePackages = "io.github.xiaoshicae.extension.sample.ecommerce")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
