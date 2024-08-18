package com.spring.boot.webflux.security.app.config.security;

import com.spring.boot.webflux.security.app.exception.ObjectNotFoundException;
import com.spring.boot.webflux.security.app.persistence.documents.Users;
import com.spring.boot.webflux.security.app.service.auth.JwtService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Component
public class AuthManger implements ReactiveAuthenticationManager {

    final JwtService jwtService;
    final ReactiveUserDetailsService users;

    public AuthManger(JwtService jwtService, ReactiveUserDetailsService users) {
        this.jwtService = jwtService;
        this.users = users;
    }

//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) {
//        return Mono.justOrEmpty(authentication)
//                .cast(BearerToken.class)  // Casts the Authentication object to BearerToken
//                .flatMap(auth -> {
//                    String getUserName = jwtService.getUserName(auth.getCredentials());  // Extracts the username from the JWT token
//
//                    return users.findByUsername(getUserName)
//                            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))  // Handles the case where the user is not found
//                            .flatMap(u -> {
//                                if (jwtService.validate(u, auth.getCredentials())) {
//                                    // If the JWT token is valid, create a new UsernamePasswordAuthenticationToken
//                                    return Mono.just(new UsernamePasswordAuthenticationToken(
//                                            u.getUsername(), u.getPassword(), u.getAuthorities()));
//                                } else {
//                                    // If the token is invalid or expired, return an error
//                                    return Mono.error(new IllegalArgumentException("Invalid or expired token"));
//                                }
//                            });
//                });
//    }



    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(
                        authentication
                )
                .cast(BearerToken.class)
                .flatMap(auth -> {
                    String getUserName = jwtService.getUserName(auth.getCredentials());
                    Mono<UserDetails> foundUser= users.findByUsername(getUserName).defaultIfEmpty(new Users());

                    Mono<Authentication> authenticatedUser = foundUser.flatMap(u->{
                        if (u.getUsername()==null){
                            Mono.error(new IllegalArgumentException("user not found"));
                        }
                        if(jwtService.validate(u, auth.getCredentials())){
                            return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword(),u.getAuthorities()));
                        }
                        Mono.error(new IllegalArgumentException("invalid expired token"));
                        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword(),u.getAuthorities()));
                    });
                    return authenticatedUser;
                });

    }

//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) {
//        return Mono.justOrEmpty(
//                        authentication
//                )
//                .cast(BearerToken.class)
//                .flatMap(auth -> {
//                    String getUserName = jwtService.getUserName(auth.getCredentials());
//                    Mono<UserDetails> foundUser= users.findByUsername(getUserName).defaultIfEmpty(new UserDetails() {
//                        @Override
//                        public Collection<? extends GrantedAuthority> getAuthorities() {
//                            return List.of();
//                        }
//
//                        @Override
//                        public String getPassword() {
//                            return "";
//                        }
//
//                        @Override
//                        public String getUsername() {
//                            return "";
//                        }
//
//                        @Override
//                        public boolean isAccountNonExpired() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isAccountNonLocked() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isCredentialsNonExpired() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isEnabled() {
//                            return false;
//                        }
//                    });
//
//                    Mono<Authentication> authenticatedUser = foundUser.flatMap(u->{
//                        if (u.getUsername()==null){
//                            Mono.error(new IllegalArgumentException("user not found"));
//                        }
//                        if(jwtService.validate(u, auth.getCredentials())){
//                            return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword(),u.getAuthorities()));
//                        }
//                        Mono.error(new IllegalArgumentException("invalid expired token"));
//                        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword(),u.getAuthorities()));
//                    });
//                    return authenticatedUser;
//                });
//
//    }
}
