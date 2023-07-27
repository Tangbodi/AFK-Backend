package com.example.demo.Util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private String error;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message, String error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setError(error);
        return response;
    }
}
