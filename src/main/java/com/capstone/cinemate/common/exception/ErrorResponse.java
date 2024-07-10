package com.capstone.cinemate.common.exception;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

@Getter
@JsonInclude(Include.NON_NULL)
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String cause;
    private List<FieldError> validation;


    @Getter
    @Setter
    @NoArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.cause = errorCode.getCause();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
    }

    public ErrorResponse(ErrorCode errorCode, List<FieldError> errors) {
        this.cause = errorCode.getCause();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.validation = errors;
    }
}