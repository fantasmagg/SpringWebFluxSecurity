package com.spring.boot.webflux.security.app.service.imp;


import com.spring.boot.webflux.security.app.service.IReqRespModel;

public class ReqRespModel<T> implements IReqRespModel<T> {

    private T data;

    private String message;

    public ReqRespModel(T data, String message) {
        this.data = data;
        this.message = message;
    }

    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
