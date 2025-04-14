/*
 * @(#) $(NAME).java    1.0     3/20/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.untils;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 20-March-2025 7:14 PM
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApiResponse {
    private int status;

    private Map<String, Object> errors = null;

    private Object data;

    private String message;

    private long totalElements;

    public ApiResponse(int status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

}
