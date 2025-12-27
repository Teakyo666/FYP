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
        // Configure cross-domain
        CorsConfiguration config = new CorsConfiguration();
        // Allowed front-end domain names
        config.addAllowedOrigin("http://localhost:5173");
        // Allow cookies to be carried.
        config.setAllowCredentials(true);
        // Allow all request methods
        config.addAllowedMethod("*");
        // Allow all request headers
        config.addAllowedHeader("*");
        // The validity period of the pre-inspection request
        config.setMaxAge(3600L);

        // Configure mapping path
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // All interfaces allow for cross-domain access.
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}