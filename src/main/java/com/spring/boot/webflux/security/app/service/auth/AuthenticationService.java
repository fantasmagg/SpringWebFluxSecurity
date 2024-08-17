package com.spring.boot.webflux.security.app.service.auth;

import com.spring.boot.webflux.security.app.dto.SaveUser;
import com.spring.boot.webflux.security.app.dto.auth.AuthenticationRequest;
import com.spring.boot.webflux.security.app.dto.auth.AuthenticationResponse;
import com.spring.boot.webflux.security.app.persistence.documents.RegisteredUser;
import com.spring.boot.webflux.security.app.persistence.documents.Users;
import com.spring.boot.webflux.security.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;


    //aqui estamos registrando a un usuario
    //registerOneCustomer ese metodo es el que registra
    // lo demas es para devolver la respuesta
    public Mono<RegisteredUser>registerOneCustomer (SaveUser x){
        return userService.registerOneCustomer(x)
                .map(uc-> {
                    RegisteredUser user = new RegisteredUser();
                    user.setId(uc.getId());
                    user.setName(uc.getName());
                    user.setUsername(uc.getUsername());
                    user.setRole(uc.getRole().name());
                    String jwt = jwtService.generate(uc.getUsername());
                    user.setJwt(jwt);
                    return user;
                });
    }

    //
    private Map<String, Object> generateExtraClaims(Users user) {
        Map<String,Object> extraClaims = new HashMap<>();
        extraClaims.put("name",user.getName());
        extraClaims.put("role",user.getRole().name());
        extraClaims.put("authorities",user.getAuthorities());

        return extraClaims;
    }


    public Mono<AuthenticationResponse> login(AuthenticationRequest authenticationRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword()
        );

        return reactiveAuthenticationManager.authenticate(authentication)
                .flatMap(auth -> {
                    System.out.println("Authenticating user: " + authenticationRequest.getUsername());
                    return userService.findOneByUsername(authenticationRequest.getUsername())
                            .doOnNext(user -> System.out.println("User found: " + user.getName()))
                            .switchIfEmpty(Mono.error(new RuntimeException("User not found in database")));
                })
                .map(user -> {
                    String jwt = jwtService.generate(user.getUsername());
                    AuthenticationResponse authRep = new AuthenticationResponse();
                    authRep.setJwt(jwt);
                    return authRep;
                });

    }


}
