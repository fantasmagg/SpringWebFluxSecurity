package com.spring.boot.webflux.security.app.config.security;

import com.spring.boot.webflux.security.app.exception.ObjectNotFoundException;
import com.spring.boot.webflux.security.app.persistence.documents.Users;
import com.spring.boot.webflux.security.app.persistence.repository.UserRepository;
import com.spring.boot.webflux.security.app.service.UserService;
import com.spring.boot.webflux.security.app.service.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityBeansInjector {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }

    //FAKE users
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder encoder){
//        //spring is going to take care of supplying us the argument
//        UserDetails user = User.builder()
//                .username("gota")
//                .password(encoder.encode("gota"))
//                .roles("USER")
//                .build();
//
//        return new MapReactiveUserDetailsService(user);
//    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return (username -> userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("User not found with username " + username)))
                .map(users -> {
                    // Asumiendo que 'Users' implementa 'UserDetails'
                    return new User(users.getUsername(), users.getPassword(), users.getAuthorities());
                }));
    }



    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthConverter jwtAuthConvert, AuthManger jwtAuthManager){
        AuthenticationWebFilter jwtFilter= new AuthenticationWebFilter(jwtAuthManager);
        jwtFilter.setServerAuthenticationConverter(jwtAuthConvert);
        return http.
                authorizeExchange(auth -> {
                    auth.pathMatchers("/auth/authenticate").permitAll();
                    auth.anyExchange().authenticated();
                })
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .httpBasic(ht -> ht.disable())
                .formLogin(fo -> fo.disable())
                .csrf(cs ->cs.disable())
                .build();
    }


}
