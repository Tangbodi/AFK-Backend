package com.example.demo.Util;

import com.example.demo.Constant.Enum.ReturnCode;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private String error;
    private long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ReturnCode.RC200.getCode());
        response.setMessage(ReturnCode.RC200.getMessage());
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

}
