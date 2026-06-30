package com.grayunited.payments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Enable CORS for React Frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Disable CSRF completely for Stateless REST APIs
                .csrf(csrf -> csrf.disable())

                // 3. CRITICAL: Enforce Stateless Session Management so no cookies are created
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Endpoint Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Allow Webhooks (Stripe/Razorpay must reach this unauthenticated)
                        .requestMatchers("/api/payments/webhook").permitAll()

                        // Allow Frontend to initiate and verify payments
                        .requestMatchers("/api/payments/internal/create-order").permitAll()
                        .requestMatchers("/api/payments/internal/verify-signature").permitAll()

                        // Allow public subscriber operations (Newsletter)
                        .requestMatchers("/api/subscribers/**").permitAll()

                        // ✅ NEW: Allow public access to the Secure Intake Form
                        .requestMatchers("/api/v1/intake/**").permitAll()

                        // Tighten down all other production endpoints
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins (Update with your Vercel/Netlify domain for production)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS to all routes
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}