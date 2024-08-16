package com.spring.boot.webflux.security.app.service;

import com.spring.boot.webflux.security.app.dto.SaveProduct;
import com.spring.boot.webflux.security.app.persistence.documents.Category;
import com.spring.boot.webflux.security.app.persistence.documents.Product;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<Product> findAll(Pageable pageable);

    Mono<Product> findOneById(String id);

    Mono<Product> save(SaveProduct x);

    Mono<Product> updateOneById(String id, Mono<SaveProduct> saveProduct);

    Mono<Void> disableOneById(String id);
}
