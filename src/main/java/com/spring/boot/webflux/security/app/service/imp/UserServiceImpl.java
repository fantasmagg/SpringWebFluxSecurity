package com.spring.boot.webflux.security.app.service.imp;

import com.spring.boot.webflux.security.app.dto.SaveUser;
import com.spring.boot.webflux.security.app.exception.InvalidPasswordException;
import com.spring.boot.webflux.security.app.exception.ObjectNotFoundException;
import com.spring.boot.webflux.security.app.persistence.documents.Users;
import com.spring.boot.webflux.security.app.persistence.repository.UserRepository;
import com.spring.boot.webflux.security.app.persistence.util.Role;
import com.spring.boot.webflux.security.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    //aqui simplemente estamos registrando al usuario
    @Override
    public Mono<Users> registerOneCustomer(SaveUser saveUser) {
        return Mono.just(saveUser)
                .map(x -> {
                    validatePassword(x);
                    Users user = new Users();
                    user.setPassword(passwordEncoder.encode(x.getPassword()));

                    user.setName(x.getName());
                    user.setUsername(x.getUsername());
                    user.setRole(Role.ROLE_CUSTOMER);
                    return user;
                })
                .flatMap(uc -> userRepository.save(uc))
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("a ocurrido un error")));
    }

//    @Override
//    public Mono<Users> findOneByUsername(String username) {
//        Mono<Users> ss = userRepository.findByUsername(username)
//                .flatMap(user -> {
//                    System.out.println("User found: " + user.getName());
//                    return Mono.just(user);
//                })
//                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
//
//        // Aquí suscribimos al Mono para imprimir el resultado o el error
//        ss.subscribe(
//                user -> System.out.println("Subscription result: " + user.getName()),
//                error -> System.out.println("Subscription error: " + error.getMessage())
//        );
//
//        return ss;
//    }

    @Override
    public Mono<Users> findOneByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    System.out.println("User found: " + user.getName());
                    return Mono.just(user);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }


//    @Bean
//    public ReactiveUserDetailsService reactiveUserDetailsService() {
//        return username -> userRepository.findByUsername(username)
//                .map(user -> User.withUsername(user.getUsername())
//                        .password(user.getPassword()) // Asegúrate de que la contraseña esté codificada
//                        .roles(user.getRole().name())
//                        .build());
//    }

//    @Bean
//    public ReactiveUserDetailsService reactiveUserDetailsService(String username) {
//
//        return (ReactiveUserDetailsService) userRepository.findByUsername(username)
//                .map(user -> User.withUsername(user.getUsername())
//                        .password(user.getPassword())
//                        .roles(user.getRole().name())
//                        .build());
//
//    }

    //aqui lo que estamos hhaciendo es validar el password
    private void validatePassword(SaveUser dto){
        if(!StringUtils.hasText(dto.getPassword())||!StringUtils.hasText(dto.getPassword())){
            throw new InvalidPasswordException("Password is incorred");
        }
        if(!dto.getPassword().equals(dto.getRepeatedPassword())){
            throw new InvalidPasswordException("Password don't match");
        }

    }

}
