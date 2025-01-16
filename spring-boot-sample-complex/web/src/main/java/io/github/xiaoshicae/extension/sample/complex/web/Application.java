package io.github.xiaoshicae.extension.sample.complex.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionScan;


/**
 * Admin: http://127.0.0.1:8080/my-extension-admin
 */
@SpringBootApplication
@ExtensionScan(scanPackages = "io.github.xiaoshicae.extension.sample.complex")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
