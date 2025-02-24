package com.group1.MockProject.config;

import com.group1.MockProject.security.JwtAuthenticationEntryPoint;
import com.group1.MockProject.security.JwtAuthenticationFilter;

import com.group1.MockProject.security.JwtAuthenticationEntryPoint;
import com.group1.MockProject.security.JwtAuthenticationFilter;
import com.group1.MockProject.security.JwtAuthenticationEntryPoint;
import com.group1.MockProject.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfiguration(
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    private final String[] WHITE_LIST = {
            "/api/v1/test/**",
            "/api/v1/auth/**",
            "/api/v1/auth/sign-in/instructor",
            "/api/v1/courses/create",
            "/api/v1/courses/**",
            "/login/oauth2/**",
            "/oauth2/**",
            "/api/v1/student/**",
            "/api/v1",
            "/api/v1/",
            "/api/v1/index",
            "/api/v1/homepage",
            "/api/v1/home-page",
            "/api/v1/admin/**",
            "/api/v1/payment/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)  // Tắt CSRF nếu làm việc với API
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/v1/admin").hasRole("ADMIN")
                            .requestMatchers(WHITE_LIST).permitAll()  // Đảm bảo các URL này được phép truy cập mà không cần xác thực
                            .anyRequest().authenticated()  // Các request khác yêu cầu phải đăng nhập
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/v1/auth/oauth2/success", true)
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/api/v1/auth/oauth2/authorize")) // Authorization endpoint
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/login/oauth2/code/google")) // Ensure callback matches Google redirect
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Session will not be created automatically
                ); // Tắt Basic Authentication nếu không cần thiết

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
