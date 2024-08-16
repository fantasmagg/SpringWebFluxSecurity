package com.spring.boot.webflux.security.app.service;

import com.spring.boot.webflux.security.app.dto.SaveCategory;
import com.spring.boot.webflux.security.app.persistence.documents.Category;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    Flux<Category> findAll(Pageable pageable);

    Mono<Category> findOneById(String id);

    Mono<Category> save(SaveCategory x);

    Mono<Category> updateOneById(String id, Mono<SaveCategory> saveProduct);

    Mono<Void> disableOneById(String id);
}
