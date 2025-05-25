package com.flix.flix.security;

import org.springframework.beans.factory.annotation.Value; // <--- Import ini
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <--- Import ini untuk HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // <--- Import ini
import org.springframework.web.cors.CorsConfigurationSource; // <--- Import ini
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // <--- Import ini

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flix.flix.constant.ApiBash;
import com.flix.flix.service.impl.RedisTokenBlackListService;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

import java.util.Arrays; // <--- Import ini

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenBlackListService redisTokenBlackListService;
    private final ObjectMapper objectMapper;

    // Inject frontend.url dari application.properties/env
    @Value("${frontend.url}")
    private String frontendUrl; // <--- Tambahkan ini

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(crsf -> crsf.disable()) // Matikan CSRF jika ini API stateless
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <--- Tambahkan ini untuk mengaktifkan CORS
                .authorizeHttpRequests(authz -> authz
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        // Izinkan permintaan OPTIONS untuk semua path tanpa autentikasi (penting untuk preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // <--- BARIS KRITIS UNTUK CORS PREFLIGHT
                        .requestMatchers(ApiBash.USER + ApiBash.AUTH + "/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, objectMapper, redisTokenBlackListService), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    // Bean untuk konfigurasi CORS yang akan digunakan oleh Spring Security
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Menggunakan frontendUrl yang diinject dari properties
        configuration.setAllowedOrigins(Arrays.asList(frontendUrl)); // <--- Mengambil origin dari properties
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Mengizinkan semua header (termasuk Authorization)
        configuration.setAllowCredentials(true); // Penting jika Anda mengirim header Authorization atau cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Terapkan untuk semua path
        return source;
    }
}