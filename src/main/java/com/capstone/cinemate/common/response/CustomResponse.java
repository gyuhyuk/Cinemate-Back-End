package com.capstone.cinemate.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomResponse<T> {

    private final int status;
    private final String message;

    private final T data;

//    public CustomResponse(int status, String message, T data) {
//        this.status = status;
//        this.message = message;
//        this.data = data;
//    }
}