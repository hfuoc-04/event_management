package com.demo.demo.config;

import com.demo.demo.service.AuthenticationService;
import jakarta.servlet.Filter; // Nhớ import đúng JwtAuthenticationFilter của em nhé
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    Filter filter;

    @Autowired
    AuthenticationService authenticationService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        req -> req
                                // ==========================================
                                // 0. NHÓM SWAGGER & API DOCS (Mở cửa hoàn toàn)
                                // ==========================================
                                .requestMatchers(
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html"
                                ).permitAll()

                                // ==========================================
                                // 1. NHÓM API AUTH (Mở cửa tự do)
                                // ==========================================
                                .requestMatchers("/api/v1/auth/**").permitAll()

                                // ==========================================
                                // 2. NHÓM API CATEGORY (Quản lý danh mục)
                                // ==========================================
                                .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/categories/*").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/categories").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/categories/*").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/*").hasAuthority("ADMIN")

                                // ==========================================
                                // 3. NHÓM API EVENT (Quản lý sự kiện)
                                // ==========================================
                                .requestMatchers(HttpMethod.GET, "/api/v1/events").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/events/*").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/events").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/events/*").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/events/*").hasAuthority("ADMIN")

                                // ==========================================
                                // 4. NHÓM API ACCOUNT (Quản lý tài khoản)
                                // ==========================================
                                .requestMatchers(HttpMethod.GET, "/api/v1/accounts").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/accounts").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/accounts/*/role").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/accounts/*").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/accounts/*").hasAnyAuthority("ADMIN", "CUSTOMER")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/accounts/*").hasAnyAuthority("ADMIN", "CUSTOMER")
                                .requestMatchers(HttpMethod.POST, "/api/v1/accounts/face").hasAnyAuthority("ADMIN", "CUSTOMER")

                                // ==========================================
                                // 5. NHÓM API REGISTER EVENT (Đăng ký tham gia sự kiện)
                                // ==========================================
                                // Quyền ADMIN (Quản lý tiến độ sự kiện)
                                .requestMatchers(HttpMethod.GET, "/api/v1/register-events/event/*").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/v1/register-events/event/*/checked-in").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/register-events/check-in/*").hasAuthority("ADMIN")

                                // Quyền ADMIN & CUSTOMER (Thao tác cá nhân)
                                .requestMatchers(HttpMethod.GET, "/api/v1/register-events/account/*").hasAnyAuthority("ADMIN", "CUSTOMER")
                                .requestMatchers(HttpMethod.GET, "/api/v1/register-events").hasAnyAuthority("ADMIN", "CUSTOMER") // Check status
                                .requestMatchers(HttpMethod.POST, "/api/v1/register-events").hasAnyAuthority("ADMIN", "CUSTOMER") // Đăng ký
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/register-events").hasAnyAuthority("ADMIN", "CUSTOMER") // Hủy đăng ký

                                // ==========================================
                                // 6. CHẶN CÁC REQUEST CÒN LẠI
                                // ==========================================
                                .anyRequest().authenticated()
                )
                .userDetailsService(authenticationService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}