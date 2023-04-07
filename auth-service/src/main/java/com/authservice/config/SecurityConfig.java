package com.authservice.config;

import com.authservice.application.provider.JwtProvider;
import com.authservice.common.security.jwt.JwtAccessDeniedHandler;
import com.authservice.common.security.jwt.JwtAuthenticationEntryPoint;
import com.authservice.common.security.jwt.JwtFilter;
import com.authservice.common.security.session.UserDetailsCustomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsCustomService userDetailsService;
    private final JwtProvider jwtProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        // endpoint 별 권한 설정
        security
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests()
                .anyRequest().permitAll();

        // 정책 설정
        security
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // JWT

        // security 세션, jwt 관련 설정
        security
                .formLogin().disable()
                .logout().disable()
                .userDetailsService(userDetailsService)
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint);

        // TODO: 2023/03/31 OAuth2 설정

        return security.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}