package com.example.demo.Exception;

import com.example.demo.Util.ApiResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<String>> HandleValidationException(Exception e) {
        ApiResponse<String> response = null;
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            response = ApiResponse.error(HttpStatus.BAD_REQUEST.value()
                    , exception.getBindingResult().getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage).collect(Collectors.joining(";")));
        } else if (e instanceof ConstraintViolationException) {
            ConstraintViolationException exception = (ConstraintViolationException) e;
            response = ApiResponse.error(HttpStatus.BAD_REQUEST.value()
                    , exception.getConstraintViolations().stream()
                            .map(constraintViolation -> constraintViolation.getMessage()).collect(Collectors.joining(";")));
        } else if (e instanceof BindException) {
            BindException exception = (BindException) e;
            response = ApiResponse.error(HttpStatus.BAD_REQUEST.value()
                    , exception.getBindingResult().getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage).collect(Collectors.joining(";")));
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}