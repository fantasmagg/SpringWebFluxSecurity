package com.spring.boot.webflux.security.app.service;

import com.spring.boot.webflux.security.app.dto.SaveUser;
import com.spring.boot.webflux.security.app.persistence.documents.Users;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<Users> registerOneCustomer(SaveUser saveUser);
    Mono<Users>findOneByUsername (String username);
}
