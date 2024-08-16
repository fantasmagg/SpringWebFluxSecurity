package com.spring.boot.webflux.security.app.config.security;

import com.spring.boot.webflux.security.app.exception.ObjectNotFoundException;
import com.spring.boot.webflux.security.app.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityBeansInjector {


    @Autowired
    private UserRepository userRepository;

    // AuthenticationManager eso es una interfas que nos permite authenticar a los usuarios
    // y este metodo es basicamente una implementacion de esa intefas que nos permetira poder authenticar a un usuario
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager reactiveAuthManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        reactiveAuthManager.setPasswordEncoder(passwordEncoder);
        return reactiveAuthManager;
    }

    //con este metodo podemos crear nuestra extrategia de authenticasion
//    @Bean
//    public AuthenticationProvider authenticationProvider(){
//
//        DaoAuthenticationProvider authenticationStrategy = new DaoAuthenticationProvider();
//        authenticationStrategy.setPasswordEncoder(passwordEncoder());
//        authenticationStrategy.setUserDetailsService(userDetailsService());
//
//        return authenticationStrategy;
//
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return (username -> {
            return (UserDetails) userRepository.findByUsername(username)
                    .switchIfEmpty(Mono.error( new ObjectNotFoundException("user not found with username "+username)));
        });
    }


}
