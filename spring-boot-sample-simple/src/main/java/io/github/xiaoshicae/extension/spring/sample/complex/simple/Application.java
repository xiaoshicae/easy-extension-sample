package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionScan;

// 需要@ExtensionScan注解，用来扫描ExtensionPoint注解标识的接口
@SpringBootApplication
@ExtensionScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
