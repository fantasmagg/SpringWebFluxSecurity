package com.spring.boot.webflux.security.app.persistence.repository;

import com.spring.boot.webflux.security.app.persistence.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductDao extends ReactiveMongoRepository<Product,String> {
}
