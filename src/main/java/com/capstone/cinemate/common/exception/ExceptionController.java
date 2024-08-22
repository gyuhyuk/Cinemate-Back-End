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
    public ResponseEntity<CustomResponse<String>> handleBaseException(CustomException e) {
        CustomResponse customResponse= new CustomResponse<>(
                e.getErrorCode().getStatus(),
                e.getErrorCode().getCode(),
                e.getErrorCode().getCause()
        );
        return ResponseEntity.status(resolveHttpStatus(e.getErrorCode())).body(customResponse);
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


    // MethodArgumentNotValidException 예외를 처리하는 핸들러(요청 바디가 잘못된 경우, 요청 dto에서 잘못된 경우 ex - 이메일 형식)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<String>> springValidationExceptionHandler(
            final MethodArgumentNotValidException e) {

        int status = HttpStatus.BAD_REQUEST.value();
        String message = HttpStatus.BAD_REQUEST.getReasonPhrase();
        String data = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        CustomResponse<String> customResponse = new CustomResponse<>(status, message, data);
        return ResponseEntity.badRequest().body(customResponse);
    }

    // 요청 dto가 유효성 검사에서 틀렸을 때 발생
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    protected CustomResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        return new CustomResponse<>(
//                HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.getReasonPhrase(),
//                e.getBindingResult().getFieldErrors().get(0).getDefaultMessage()
//        );
//    }

    @ExceptionHandler(NotFoundException.class)
    public CustomResponse<String> handleNotFoundException(NotFoundException e) {
        return new CustomResponse<>(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage()
        );
    }

    private HttpStatus resolveHttpStatus(ErrorCode errorCode){
        return HttpStatus.resolve(errorCode.getStatus());
    }
}