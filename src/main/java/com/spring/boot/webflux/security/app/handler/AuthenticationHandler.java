package com.spring.boot.webflux.security.app.handler;

import com.spring.boot.webflux.security.app.dto.auth.AuthenticationRequest;
import com.spring.boot.webflux.security.app.dto.auth.AuthenticationResponse;
import com.spring.boot.webflux.security.app.dto.buscar;
import com.spring.boot.webflux.security.app.exception.ObjectNotFoundException;
import com.spring.boot.webflux.security.app.persistence.documents.ReqLogin;
import com.spring.boot.webflux.security.app.persistence.documents.Users;
import com.spring.boot.webflux.security.app.persistence.repository.UserRepository;
import com.spring.boot.webflux.security.app.service.UserService;
import com.spring.boot.webflux.security.app.service.auth.AuthenticationService;
import com.spring.boot.webflux.security.app.service.auth.JwtService;
import com.spring.boot.webflux.security.app.service.imp.ReqRespModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class AuthenticationHandler {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userRepository;

    @Autowired
    private ReactiveUserDetailsService users;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder encoder;

//    public Mono<ServerResponse> authenticate (ServerRequest request){
//        Mono<AuthenticationRequest> authresp = request.bodyToMono(AuthenticationRequest.class);
//
//        return authresp
//                .flatMap(authRq -> authenticationService.login(authRq))
//                .flatMap(authResp-> ServerResponse.ok().bodyValue(authResp))
//                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
//
//    }
//
//    public Mono<ServerResponse> getUserByUsername(ServerRequest request) {
//        Mono<Users> usernameMono = request.bodyToMono(Users.class);
//
//        return usernameMono
//                .flatMap(username -> userRepository.findOneByUsername(username.getUsername())
//                        .flatMap(user -> ServerResponse.ok().bodyValue(user))
//                        .switchIfEmpty(ServerResponse.notFound().build())
//                )
//                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage()));
//    }


//    public Mono<ServerResponse> login(ServerRequest request){
//
//        Mono<ReqLogin> user = request.bodyToMono(ReqLogin.class);
//
//        Mono<UserDetails> foundUser = users.findByUsername(user.getEmail()).defaultIfEmpty(new UserDetails() {
//
//            @Override
//            public Collection<? extends GrantedAuthority> getAuthorities() {
//                return null;
//            }
//
//            @Override
//            public String getPassword() {
//                return null;
//            }
//
//            @Override
//            public String getUsername() {
//                return null;
//            }
//
//            @Override
//            public boolean isAccountNonExpired() {
//                return false;
//            }
//
//            @Override
//            public boolean isAccountNonLocked() {
//                return false;
//            }
//
//            @Override
//            public boolean isCredentialsNonExpired() {
//                return false;
//            }
//
//            @Override
//            public boolean isEnabled() {
//                return false;
//            }
//        });
//
//
//        return foundUser.map(
//                u->{
//                    if(u.getUsername() == null){
//                        return ResponseEntity.status(404).body(new ObjectNotFoundException("a ocurrido un error en el login"));
//
//                    }
//                    if (encoder.matches(user.getPassword(),u.getPassword())){
//                        return ResponseEntity.ok(
//                                new ReqRespModel<>(jwtService.generate(u.getUsername()), "Success")
//                        );
//                    }
//                    return ResponseEntity.badRequest().body(new ReqRespModel<>("","Invalid credentials"));
//                }
//        );
//
//    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(ReqLogin.class)
                .flatMap(user ->
                        users.findByUsername(user.getEmail())
                                .switchIfEmpty(Mono.error(new ObjectNotFoundException("User not found with username " + user.getEmail())))
                                .flatMap(foundUser -> {
                                    // Aquí se asume que `foundUser` es un objeto que implementa `UserDetails`
                                    if (encoder.matches(user.getPassword(), foundUser.getPassword())) {
                                        String token = jwtService.generate(foundUser.getUsername());
                                        ReqRespModel<String> response = new ReqRespModel<>(token, "Success");
                                        return ServerResponse.ok().bodyValue(response);
                                    } else {
                                        return ServerResponse.badRequest().bodyValue(new ReqRespModel<>("","Invalid credentials"));
                                    }
                                })
                )
                .onErrorResume(e -> {
                    if (e instanceof ObjectNotFoundException) {
                        return ServerResponse.status(404).bodyValue(new ReqRespModel<>("","User not found"));
                    } else {
                        return ServerResponse.status(500).bodyValue(new ReqRespModel<>("","An unexpected error occurred"));
                    }
                });
    }
}
