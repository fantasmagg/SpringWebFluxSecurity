package com.spring.boot.webflux.security.app.service.auth;

import com.spring.boot.webflux.security.app.dto.SaveUser;
import com.spring.boot.webflux.security.app.dto.auth.AuthenticationRequest;
import com.spring.boot.webflux.security.app.dto.auth.AuthenticationResponse;
import com.spring.boot.webflux.security.app.exception.ObjectNotFoundException;
import com.spring.boot.webflux.security.app.persistence.documents.RegisteredUser;
import com.spring.boot.webflux.security.app.persistence.documents.ReqLogin;
import com.spring.boot.webflux.security.app.persistence.documents.Users;
import com.spring.boot.webflux.security.app.persistence.repository.UserRepository;
import com.spring.boot.webflux.security.app.service.UserService;
import com.spring.boot.webflux.security.app.service.imp.ReqRespModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
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
    @Autowired
    private ReactiveUserDetailsService users;

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
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
                    String jwt = jwtService.generate(uc.getUsername(),generateExtraClaims(uc));
                    user.setJwt(jwt);
                    return user;
                });
    }
    public Mono<ServerResponse> reactUserDetail (ReqLogin username){
        return userRepository.findByUsername(username.getEmail())
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("User not found with username " + username.getEmail())))
                .flatMap(foundUser -> {
                    // Aquí se asume que `foundUser` es un objeto que implementa `UserDetails`
                    if (encoder.matches(username.getPassword(), foundUser.getPassword())) {
                        String token = jwtService.generate(foundUser.getUsername(),generateExtraClaims(foundUser));
                        ReqRespModel<String> response = new ReqRespModel<>(token, "Success");
                        return ServerResponse.ok().bodyValue(response);
                    } else {
                        return ServerResponse.badRequest().bodyValue(new ReqRespModel<>("","Invalid credentials"));
                    }
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
                    String jwt = jwtService.generate(user.getUsername(),generateExtraClaims(user));
                    AuthenticationResponse authRep = new AuthenticationResponse();
                    authRep.setJwt(jwt);
                    return authRep;
                });

    }


}
