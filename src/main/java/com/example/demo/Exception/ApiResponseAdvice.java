package com.example.demo.Exception;

import com.example.demo.Constant.Enum.ReturnCode;
import com.example.demo.Util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//RestControllerAdvice is advised annotation of RestController, it used for Global Exception Handler, Global Data Binding, Global Data Preprocessing
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //if the return type of Controller is String, then we need to convert String to JSON
        if (object instanceof String) {
            return objectMapper.writeValueAsString(ApiResponse.success(object));
        }
        if (object instanceof ApiResponse) {
            return object;
        }
        return ApiResponse.error(ReturnCode.RC500.getCode(), "Internal Server Error");
    }
}
