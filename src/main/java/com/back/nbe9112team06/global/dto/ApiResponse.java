package com.back.nbe9112team06.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 임시 dto 입니다.
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, message, data);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(statusCode, message, null);
    }
}
