package com.example.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@MapperScan("com.example.backend.Mappers")
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> startupSuccessListener() {
        return event -> {
            System.out.println("=====================================");
            System.out.println("🎉 项目启动成功！");
            System.out.println("📌 访问地址：http://localhost:8080");
            System.out.println("=====================================");
        };
    }
}