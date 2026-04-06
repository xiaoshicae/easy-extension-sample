package io.github.xiaoshicae.extension.sample.ecommerce.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionScan;

/**
 * 电商下单流程示例
 * Admin: http://127.0.0.1:8080/easy-extension-admin
 */
@SpringBootApplication
@ExtensionScan(scanPackages = "io.github.xiaoshicae.extension.sample.ecommerce")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
