package com.capstone.cinemate.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomResponse<T> {

    private final int status;
    private final String message;

    private final T data;
}