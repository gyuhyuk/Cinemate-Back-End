package com.capstone.cinemate.common.exception;

import com.capstone.cinemate.common.response.CustomResponse;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public CustomResponse<String> handleException(Exception e) {
        return new CustomResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage()
        );
    }

    @ExceptionHandler(CustomException.class)
    public CustomResponse<String> handleBaseException(CustomException e) {
        return new CustomResponse<>(
                e.getErrorCode().getStatus(),
                e.getErrorCode().getCode(),
                e.getErrorCode().getCause()
        );
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class
    })
    public CustomResponse<String> handleIllegalArgumentException(Exception e) {
        return new CustomResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage()
        );
    }

    // 요청 dto가 유효성 검사에서 틀렸을 때 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected CustomResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new CustomResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getBindingResult().getFieldErrors().get(0).getDefaultMessage()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public CustomResponse<String> handleNotFoundException(NotFoundException e) {
        return new CustomResponse<>(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
    }
}