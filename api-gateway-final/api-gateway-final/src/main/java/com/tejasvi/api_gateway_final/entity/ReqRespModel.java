package com.tejasvi.api_gateway_final.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReqRespModel<T> implements IReqRespModel<T>{
    private T data;
    private String message;
    @Override
    public T getData() {
        return this.data;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
