package io.github.xiaoshicae.extension.spring.sample.complex.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 扩展点、能力、业务、默认实现都在本应用所在的包下，框架会自动扫描，无需额外配置
 * (不在应用包下时，用@ExtensionScan(basePackages = "...")补充扫描范围)
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
