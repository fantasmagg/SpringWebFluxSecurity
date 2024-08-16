package com.spring.boot.webflux.security.app.service.imp;

import com.spring.boot.webflux.security.app.dto.SaveProduct;
import com.spring.boot.webflux.security.app.exception.ObjectNotFoundException;
import com.spring.boot.webflux.security.app.persistence.documents.Category;
import com.spring.boot.webflux.security.app.persistence.documents.Product;
import com.spring.boot.webflux.security.app.persistence.repository.CategoryDao;
import com.spring.boot.webflux.security.app.persistence.repository.ProductDao;
import com.spring.boot.webflux.security.app.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImp implements ProductService {

    @Autowired
    private ProductDao dao;

    @Autowired
    private CategoryDao categoryDao;

    private Logger log = LoggerFactory.getLogger(ProductServiceImp.class);

    @Override
    public Flux<Product> findAll(Pageable pageable) {
        return dao.findAll()
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .take(pageable.getPageSize())
                .map(prod -> {
                    prod.setName(prod.getName().toUpperCase());
                    return prod;
                });
    }

    @Override
    public Mono<Product> findOneById(String id) {
        return dao.findById(id).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Product> save(SaveProduct x) {
        return categoryDao.findById(x.getCategoryId())
                .flatMap(category ->{
                    Product product = new Product();
                    product.setName(x.getName());
                    product.setPrice(x.getPrice());
                    product.setStatus(Product.ProductStatus.ENABLED);
                    product.setCategory(category);
                    return dao.save(product);
                }).switchIfEmpty(Mono.error(new ObjectNotFoundException("category not found")));

    }

    @Override
    public Mono<Product> updateOneById(String id, Mono<SaveProduct> saveProduct) {

        return dao.findById(id)
                .flatMap(existeProduct ->
                   saveProduct.flatMap(saveProductMono ->
                           categoryDao.findById(saveProductMono.getCategoryId())
                                   .flatMap(categorys ->{
                                       existeProduct.setName(saveProductMono.getName());
                                       existeProduct.setPrice(saveProductMono.getPrice());
                                       existeProduct.setStatus(Product.ProductStatus.ENABLED);
                                       existeProduct.setCategory(categorys);
                                       return dao.save(existeProduct);
                                   }))
                           .switchIfEmpty(Mono.error(new ObjectNotFoundException("categoria no encontrada")))
                ).switchIfEmpty(Mono.error(new ObjectNotFoundException("product not found")));
    }

    @Override
    public Mono<Void> disableOneById(String id) {

        return dao.findById(id).flatMap(x-> {
            x.setStatus(Product.ProductStatus.DISABLED);
            return dao.save(x);

        }).switchIfEmpty(Mono.error(new ObjectNotFoundException("product not found")))
                .then(Mono.empty());



    }


}
