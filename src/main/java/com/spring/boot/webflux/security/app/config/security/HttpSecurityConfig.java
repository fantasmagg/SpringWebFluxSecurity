package com.spring.boot.webflux.security.app.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
public class HttpSecurityConfig {


    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authManager) throws Exception {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authManager);

        SecurityWebFilterChain filterChain = http
                .csrf(csrfSpec -> csrfSpec.disable())
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(auth -> {
                    auth.pathMatchers(HttpMethod.POST, "/api/v1/customers").permitAll();
                    auth.pathMatchers(HttpMethod.POST, "/auth/authenticate").permitAll();
                    auth.pathMatchers(HttpMethod.GET, "/auth/validate-token").permitAll();
                    auth.anyExchange().authenticated();
                })
                .build();
        return filterChain;
    }
}
