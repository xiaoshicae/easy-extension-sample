package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.xiaoshicae.extension.spring.boot.autoconfigure.annotation.ExtensionScan;

/**
 * 注解@ExtensionScan可以指定扩展点，能力，及业务的包扫码路径，默认是当前工程包路径
 */
@SpringBootApplication
@ExtensionScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
