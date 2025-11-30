package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1. 配置跨域
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端的域名（你的前端地址是 http://localhost:5173）
        config.addAllowedOrigin("http://localhost:5173");
        // 允许携带Cookie（如果需要）
        config.setAllowCredentials(true);
        // 允许所有请求方法（必须包含 OPTIONS 预检方法）
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 预检请求的有效期（秒），避免频繁发送预检请求
        config.setMaxAge(3600L);

        // 2. 配置映射路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 所有接口都允许跨域

        // 3. 返回过滤器
        return new CorsFilter(source);
    }
}