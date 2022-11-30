package com.tairanchina.csp.avm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.tairanchina.csp")
@Configuration
@MapperScan("com.tairanchina.csp.avm.mapper")
public class AppVersionManagerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AppVersionManagerApplication.class).run(args);
    }
}
