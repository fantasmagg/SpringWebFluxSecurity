package com.spring.boot.webflux.security.app.persistence.repository;

import com.spring.boot.webflux.security.app.persistence.documents.Users;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<Users,String> {
    Mono<Users> findByUsername(String username);
//    Mono<UserDetails> findByUsername(String username);

}
