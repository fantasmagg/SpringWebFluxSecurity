package com.spring.boot.webflux.security.app.persistence.repository;

import com.spring.boot.webflux.security.app.persistence.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryDao extends ReactiveMongoRepository<Category,String> {
}
