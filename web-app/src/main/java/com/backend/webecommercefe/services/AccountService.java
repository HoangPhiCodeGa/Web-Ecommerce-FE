/*
 * @(#) $(NAME).java    1.0     3/21/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.backend.webecommercefe.services;


import com.backend.webecommercefe.entities.Account;
import com.backend.webecommercefe.untils.ApiResponse;

/*
 * @description
 * @author: Tran Tan Dat
 * @version: 1.0
 * @created: 21-March-2025 10:05 PM
 */
public interface AccountService {
    public ApiResponse login(String username, String password);
    public ApiResponse register(Account account);
}

    