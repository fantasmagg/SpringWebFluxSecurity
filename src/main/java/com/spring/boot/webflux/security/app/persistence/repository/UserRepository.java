package com.spring.boot.webflux.security.app.persistence.repository;

import com.spring.boot.webflux.security.app.persistence.documents.Users;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<Users,String> {
    Mono<Users> findByUsername(String username);
}
