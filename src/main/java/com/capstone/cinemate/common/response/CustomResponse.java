package com.capstone.cinemate.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CustomResponse<T> {

    private final int status;
    private final String message;

    @Setter
    private T data;
}