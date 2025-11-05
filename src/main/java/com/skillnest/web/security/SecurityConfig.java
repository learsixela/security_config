package com.skillnest.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/").permitAll()
            	.requestMatchers("/home").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().permitAll()//.authenticated()//.permitAll()
            )
            .formLogin(login -> login.loginPage("/login").permitAll())
            .logout(logout -> logout
            		.logoutUrl("/logout")
            		.logoutSuccessUrl("/")
            		
            		);
        return http.build();
    }
}