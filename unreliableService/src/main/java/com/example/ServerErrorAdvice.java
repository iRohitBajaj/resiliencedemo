package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServerErrorAdvice {
    @ExceptionHandler(ServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerErrorException(ServerErrorException e) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus((HttpStatus.INTERNAL_SERVER_ERROR.value()));
        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        error.setErrorMsg(e.getMessage());
        return error;
    }
}
