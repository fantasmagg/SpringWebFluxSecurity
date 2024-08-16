package com.spring.boot.webflux.security.app.handler;

import com.spring.boot.webflux.security.app.dto.SaveUser;
import com.spring.boot.webflux.security.app.service.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class CustomerHandler {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private Validator validator;

    public Mono<ServerResponse> registerOne(ServerRequest request){
            Mono<SaveUser> customer = request.bodyToMono(SaveUser.class);

            return customer.flatMap(x ->{

                Errors errors = new BeanPropertyBindingResult(x, SaveUser.class.getName());
                validator.validate(x,errors);

                if(errors.hasErrors()){
                    return Flux.fromIterable(errors.getFieldErrors())
                            .map(fieldErrors -> {
                                return "el campo "+fieldErrors.getField()+" "+fieldErrors.getDefaultMessage();

                            })
                            .collectList()
                            .flatMap(list -> ServerResponse.badRequest().body(BodyInserters.fromValue(list)));

                }
                else {
                    return authenticationService.registerOneCustomer(x)
                            .flatMap(udb -> ServerResponse.created(URI.create("/api/users/".concat(udb.getId())))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(BodyInserters.fromValue(udb)));
                }

            });
    }

}
