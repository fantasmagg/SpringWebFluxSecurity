package com.spring.boot.webflux.security.app;

import com.spring.boot.webflux.security.app.handler.CategoriaHandler;
import com.spring.boot.webflux.security.app.handler.CustomerHandler;
import com.spring.boot.webflux.security.app.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {
    @Bean
    public RouterFunction<ServerResponse> routers(CategoriaHandler handlerC, ProductHandler handlerP, CustomerHandler handlerU){
        //category
        return route(GET("/findcategoria").and(accept(MediaType.APPLICATION_JSON)),handlerC::findAllCategory)
                .andRoute(POST("/api/v1/crearcategory").and(contentType(MediaType.APPLICATION_JSON)),handlerC::createOne)
                .andRoute(PUT("/api/v1/eliminar/{id}/disabled"),handlerC::eliminar)
                .andRoute(GET("/api/v1/buscar/{id}"),handlerC::findOneById)
                .andRoute(PUT("/api/v1/update/{id}").and(contentType(MediaType.APPLICATION_JSON)),handlerC::updateOneById)
                //PRODUCT
                .andRoute(GET("/api/v1/products"),handlerP::findAll)
                .andRoute(POST("/api/v1/creatproduct"), handlerP::createOne)
                .andRoute(PUT("/api/v1/eliminarproduct/{id}/disabled"),handlerP::eliminar)
                .andRoute(GET("/api/v1/buscarproduct/{id}"),handlerP::findOneById)
                .andRoute(PUT("/api/v1/updateproduct/{id}").and(contentType(MediaType.APPLICATION_JSON)),handlerP::updateOneById)
                .andRoute(POST("/api/v1/customers"),handlerU::registerOne);
    }


}
