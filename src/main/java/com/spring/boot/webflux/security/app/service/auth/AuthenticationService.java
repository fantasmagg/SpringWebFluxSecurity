package com.spring.boot.webflux.security.app.service.auth;

import com.spring.boot.webflux.security.app.dto.SaveUser;
import com.spring.boot.webflux.security.app.persistence.documents.RegisteredUser;
import com.spring.boot.webflux.security.app.persistence.documents.Users;
import com.spring.boot.webflux.security.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
                    String jwt = jwtService.generateToken(uc,generateExtraClaims(uc));
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


}
