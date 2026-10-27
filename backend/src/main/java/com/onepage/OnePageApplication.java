package com.onepage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动入口
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.onepage.mapper")
public class OnePageApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnePageApplication.class, args);
    }
}
