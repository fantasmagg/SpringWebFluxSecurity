package com.spring.boot.webflux.security.app.service;

public interface IReqRespModel<T> {
    T getData();
    String getMessage();
}
