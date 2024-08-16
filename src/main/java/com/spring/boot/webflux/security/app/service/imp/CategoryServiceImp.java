package com.spring.boot.webflux.security.app.service.imp;

import com.spring.boot.webflux.security.app.dto.SaveCategory;
import com.spring.boot.webflux.security.app.exception.ObjectNotFoundException;
import com.spring.boot.webflux.security.app.persistence.documents.Category;
import com.spring.boot.webflux.security.app.persistence.repository.CategoryDao;
import com.spring.boot.webflux.security.app.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CategoryServiceImp implements CategoryService {

    @Autowired
    private CategoryDao dao;

    @Override
    public Flux<Category> findAll(Pageable pageable) {
        return dao.findAll()
                .skip(pageable.getPageNumber() * pageable.getPageSize())
                .take(pageable.getPageSize());
    }

    @Override
    public Mono<Category> findOneById(String id) {
        return dao.findById(id).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Category> save(SaveCategory x) {
        return Mono.just(x)
                .map(sc ->{
                    Category category = new Category();
                    category.setName(x.getName());
                    category.setStatus(Category.CategoryStatus.ENABLED);
                    return category;
                }).
                flatMap(ca -> dao.save(ca))
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("error al intentar crear una category")));

    }

    @Override
    public Mono<Category> updateOneById(String id, Mono<SaveCategory> saveCategory) {
        return dao.findById(id)
                .flatMap(x -> saveCategory
                        .flatMap(xs->{
                            x.setName(xs.getName());
                            return dao.save(x);
                        }))
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("category not found")));
    }

    @Override
    public Mono<Void> disableOneById(String id) {
        return dao.findById(id)
                .flatMap(x->{
                    x.setStatus(Category.CategoryStatus.DISABLED);
                    return dao.save(x);
                })
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Category not found")))
                .then(Mono.empty());
    }
}
