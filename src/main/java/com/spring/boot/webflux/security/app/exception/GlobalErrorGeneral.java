package com.spring.boot.webflux.security.app.exception;

import com.spring.boot.webflux.security.app.dto.ApiError;
import com.spring.boot.webflux.security.app.handler.CategoriaHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GlobalErrorGeneral extends DefaultErrorAttributes {

    private Logger log = LoggerFactory.getLogger(GlobalErrorGeneral.class);

    @Override
    public Map<String,Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options){
        Map<String, Object> errorMap = new HashMap<>();
        Throwable error = getError(request);
        errorMap.put("status", 500); // O utiliza el status que corresponda
        errorMap.put("timestamp", new Date());
        errorMap.put("message", error.getMessage());
        errorMap.put("endpoint", request.path());
        errorMap.put("trace", error.getStackTrace()); // Opcional, para más detalles
        return errorMap;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ServerResponse> handleValidationException(WebExchangeBindException ex) {

        List<String> errors = ex.getFieldErrors().stream()
                .map(fieldError -> {
                    switch (fieldError.getField()) {
                        case "price":
                            return "El precio debe ser mayor que 0.01.";
                        case "name":
                            return "El nombre no puede estar en blanco.";
                        // Agrega más casos según los campos que quieras personalizar
                        default:
                            return "Error en el campo " + fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                })
                .collect(Collectors.toList());

        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errors));
    }


}