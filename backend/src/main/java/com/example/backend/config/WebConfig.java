package com.example.backend.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /assets/** 到本地 ../assets/ 目录（支持子目录）
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:../assets/")
                .setCachePeriod(0); // 开发阶段禁用缓存
    }
}