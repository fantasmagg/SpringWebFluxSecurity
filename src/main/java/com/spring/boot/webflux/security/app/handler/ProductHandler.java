package com.spring.boot.webflux.security.app.handler;

import com.spring.boot.webflux.security.app.dto.SaveProduct;
import com.spring.boot.webflux.security.app.persistence.documents.Product;
import com.spring.boot.webflux.security.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

//ahora esta clase es como nuestro controlador
@Component
public class ProductHandler {

    @Autowired
    private ProductService productService;

    @Autowired
    private Validator validator;

    //para nosotros aqui hacer una paginable, es simple, solo tenemos que extraer lo valores
    //del queryParam es decir de la url
//    public Mono<ServerResponse> findAll(ServerRequest request){
//        Pageable pageable = PageRequest.of(
//          Integer.parseInt(request.queryParam("page").orElse("0")),
//          Integer.parseInt(request.queryParam("size").orElse("10"))
//        );
//        return ServerResponse
//                .ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(productService.findAll(pageable),Product.class);
//
//    }

    public Mono<ServerResponse> findAll(ServerRequest request){
        Pageable pageable = PageRequest.of(
                Integer.parseInt(request.queryParam("p").orElse("0")),
                Integer.parseInt(request.queryParam("limit").orElse("10"))
        );

        Flux<Product> productFlux =productService.findAll(pageable);

        return productFlux
                .collectList()
                .flatMap(produts ->{
                    if(!produts.isEmpty()){
                        return ServerResponse.ok().bodyValue(produts);
                    }
                    else{
                        return ServerResponse.notFound().build();
                    }
                });

    }
    public Mono<ServerResponse> findOneById(ServerRequest request){

        String id = request.pathVariable("id");
        return productService.findOneById(id)
                .flatMap(x ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(x)))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> createOne(ServerRequest request){
        Mono<SaveProduct> producto = request.bodyToMono(SaveProduct.class);

        return producto.flatMap(x->{
            //BeanPropertyBindingResult=Encapsular Errores de Validación
            //es decir cual quier error que ocurra relacionado a ese clase en esta peticion
            // eso la encapsulara y nos la detallara
            Errors errors = new BeanPropertyBindingResult(x, SaveProduct.class.getName());
            validator.validate(x,errors);
            if(errors.hasErrors()){
                return Flux.fromIterable(errors.getFieldErrors())
                        .map(fieldErrors ->{
                            return "el campo "+fieldErrors.getField()+" "+fieldErrors.getDefaultMessage();
                        })
                        .collectList()
                        .flatMap(list -> ServerResponse.badRequest().body(BodyInserters.fromValue(list)));
            }else{
                return productService.save(x)
                        .flatMap(pdb->ServerResponse.created(URI.create("/api/producto/".concat(pdb.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(pdb)));
            }
        });
    }

    public Mono<ServerResponse> updateOneById(ServerRequest request){
        String id = request.pathVariable("id");
        Mono<SaveProduct> saveProduct = request.bodyToMono(SaveProduct.class);

        return productService.updateOneById(id,saveProduct)
                        .flatMap(saveProducto ->ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(saveProducto)))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> eliminar (ServerRequest request){
        String id = request.pathVariable("id");
        return productService.disableOneById(id)
                .flatMap(x->ServerResponse.noContent().build()
                        .switchIfEmpty(ServerResponse.notFound().build()));

    }

}
