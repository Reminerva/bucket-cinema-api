package com.flix.flix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**") // Terapkan CORS untuk semua endpoint
                .allowedOrigins(frontendUrl) // Izinkan origin dari aplikasi React Anda
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Izinkan metode HTTP yang relevan
                .allowedHeaders("*") // Izinkan semua header
                .allowCredentials(true); // Izinkan kredensial (seperti cookies atau authorization headers)
    }
}