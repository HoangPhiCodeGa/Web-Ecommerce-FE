package com.backend.webecommercefe.dto;

import lombok.Data;

@Data
public class ApiResponseDTO<T> {
    private int code;
    private String message;
    private T data;
}
